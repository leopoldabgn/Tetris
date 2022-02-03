package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, KeyListener
{
	private static final long serialVersionUID = 1L;

	private Window window;

	private Timer timer = new Timer(1, this);
	private AudioPlayer fallingSound = new AudioPlayer("sounds/fall.wav", false),
						moveSound = new AudioPlayer("sounds/move.wav", false),
						lineSound = new AudioPlayer("sounds/line1.wav", false),
						lineClearSound = new AudioPlayer("sounds/line2.wav", false);
	private final static int WIDTH_BLOCK_MAX = 10, HEIGHT_BLOCK_MAX = 20;
	public final static int BLOCK_SIZE = 30;
	public final static int WIDTH = WIDTH_BLOCK_MAX*BLOCK_SIZE, HEIGHT = HEIGHT_BLOCK_MAX*BLOCK_SIZE;
	private Square[][] mainTab = new Square[HEIGHT_BLOCK_MAX][WIDTH_BLOCK_MAX];
	private Block block, nextBlock;
	private long savedTime, savedTimeRotation;
	private int speed = 650;
	private int score = 0, level = 0, lines = 0;
	private JLabel scoreLbl = new JLabel(""+score),
				   levelLbl = new JLabel(""+level),
				   linesLbl = new JLabel(""+lines);
	private GameContainer gameContainer;
	private InfoContainer infoContainer;
	private boolean lockedBlock = false, gameOver;
	// Global variables for BlinkAnimation :
	
	private boolean blinkAnim = false;
	
	// Global Variable for FallingAnimation :
	
	private List<int[]> listLines;
	private int indexLines = 0, countIndex = 0;
	private boolean fallAnim = false;
	
	private boolean pause = false;
	
	public GamePanel(Window window, int mod) // 10x20 --> 300x600 --> chaque cube = 30x30.
	{
		this.window = window;
		if(mod == 1)
			genLevel(0); /////
		block = getRandomBlock();
		nextBlock = getRandomBlock(block.getIndex(), block.getColor());
		
		gameContainer = new GameContainer(WIDTH, HEIGHT);
		infoContainer = new InfoContainer(WIDTH/2, HEIGHT);
		
		//this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.add(gameContainer);
		this.add(infoContainer);
		this.addKeyListener(this);
	    this.setFocusable(true);
	    this.requestFocus();
	    timer.start();
	    this.savedTime = System.currentTimeMillis();
	    this.savedTimeRotation = savedTime;
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Image img = new ImageIcon("resources/brique.jpg").getImage();
		g.drawImage(img, 0, 0, null);
		gameContainer.repaint();
		infoContainer.repaint();
		if(pause || gameOver)
			drawBlackScreen(g);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// Au cas ou :
		if(gameOver)
			return;
		
		if(!blinkAnim && !fallAnim)
		{
			if(lockedBlock)
			{
				putInMainTab(block);
				block = nextBlock;
				nextBlock = getRandomBlock(block.getIndex(), block.getColor());
				infoContainer.setNextBlock(nextBlock);
				int[] tab = searchLines();
				//delLines(tab); //// On supprime les lignes !! Alors qu'on en a besoin apres !!!
				repaint();
				//listLines = fallLines(); /// On recuperer uniquement si elles ont ete supprime !
				if(tab != null)
				{
					refreshInfoPan(tab);
					startBlinkAnim();
				}
				lockedBlock = false;
			}
			
			if(((System.currentTimeMillis() - savedTime) > speed) && !lockedBlock)
			{
				savedTime = System.currentTimeMillis();
				if(canMove(block, 0, BLOCK_SIZE))
				{
					countIndex = 0;
					block.move(0, BLOCK_SIZE);
					repaint();
				}
				else if(isOnTheGround(block) || isBlocked(block))
				{
					if(block.isOnTop()) {
						timer.stop();
						gameOver = true;
						gameContainer.gameOverPan.setVisible(true);
						repaint();
					}
					lockedBlock = true;
					fallingSound.restart();
					fallingSound.play();
				}
			}
		}
		else
		{
			
			if(blinkAnim)
				blinkingAnim(250);
			else if(fallAnim)
				fallingAnim();
		}
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(gameOver)
			return;
		if(!pause || e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			if(!fallAnim && !blinkAnim)
			{
				switch(e.getKeyCode())
				{
				case KeyEvent.VK_LEFT:
					if(canMove(block, -BLOCK_SIZE, 0))
						block.move(-BLOCK_SIZE, 0);
					break;
				case KeyEvent.VK_RIGHT:
					if(canMove(block, BLOCK_SIZE, 0))
						block.move(BLOCK_SIZE, 0);
					break;
				case KeyEvent.VK_DOWN:
					if(canMove(block, 0, BLOCK_SIZE))
						block.move(0, BLOCK_SIZE);
					break;
				case KeyEvent.VK_UP:
					if((System.currentTimeMillis() - savedTimeRotation) > 100 && block.canRotate())
					{
						savedTimeRotation = System.currentTimeMillis();
						if(canMove(block, 0, BLOCK_SIZE))
						{
							block.rotate();
							block.refreshSquareList();
							if(isBlocked(block))//|| isBlockOnMainTab(block))
							{
								for(int i=0;i<3;i++)
									block.rotate();
								block.refreshSquareList();
							}
							else
							{
								moveSound.restart();
								moveSound.play();
							}
						}
					}
					break;
				case KeyEvent.VK_ESCAPE:
					if(pause)
						resume();
					else
						setPause();
					break;
				default:
					break;
				}
				repaint();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	class GameContainer extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		private JPanel gameOverPan;

		public GameContainer(int w, int h)
		{
			super();
			this.setPreferredSize(new Dimension(w, h));
			this.setBackground(Color.BLACK);
			this.setLayout(null);
			JButton restart, menu;
			restart = new JButton("Restart");
			restart.addActionListener(e -> {
				restartGame();
			});
			menu = new JButton("Menu");
			menu.addActionListener(e -> {
				backToMenu();
			});
			gameOverPan = new JPanel();
			gameOverPan.setOpaque(false);
			gameOverPan.setLayout(new BorderLayout());
			gameOverPan.setSize(220, 40);
			gameOverPan.setLocation(39, 3*(int)getPreferredSize().getHeight()/4);
			gameOverPan.add(restart, BorderLayout.WEST);
			gameOverPan.add(menu, BorderLayout.EAST);

			// INVISIBLE au debut.
			gameOverPan.setVisible(false);

			this.add(gameOverPan, BorderLayout.SOUTH);
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			/*g.setColor(new Color(255,155,255,50));
			for(int j=0;j<HEIGHT_BLOCK_MAX;j++)
				for(int i=0;i<WIDTH_BLOCK_MAX;i++)
					g.drawRect(i*BLOCK_SIZE, j*BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);*/
			block.draw(g);
			dispMainTab(g);

			if(pause || gameOver)
			{
				drawBlackScreen(g);
				g.setFont(new Font("Sans-serif", Font.BOLD, 36));
				g.setColor(Color.WHITE);
			}
			if(pause)
				g.drawString("PAUSE", 85, getHeight()/2 - 20);
			else if(gameOver)
				g.drawString("GAME OVER", 28, getHeight()/2 - 20);
		}
		
	}
	
	class InfoContainer extends JPanel
	{
		private static final long serialVersionUID = 1L;
		
		private BoxPan scorePan, lvlPan, linesPan, nextBlockPan;
		
		public InfoContainer(int w, int h)
		{
			this.setPreferredSize(new Dimension(w, h));
			
			scorePan = new BoxPan(w-10, 100, "SCORE", scoreLbl);
			lvlPan = new BoxPan(w-10, 100, "LEVEL", levelLbl);
			linesPan = new BoxPan(w-10, 100, "LINES", linesLbl);
			nextBlockPan = new BoxPan(BLOCK_SIZE*4, BLOCK_SIZE*4, nextBlock);
			this.add(scorePan);
			this.add(lvlPan);
			this.add(linesPan);
			this.add(nextBlockPan);
		}
		
		public void setNextBlock(Block b)
		{
			nextBlockPan.setBlock(b);
		}
		
	}

	public static Color getRandColor()
	{
		int randNb = (int)(Math.random()*7);
		if(randNb == 0)
			return new Color(183, 46, 29); // RED
		else if(randNb == 1)
			return new Color(65, 64, 252); // BLUE
		else if(randNb == 2)
			return Color.MAGENTA;
		else if(randNb == 3)
			return new Color(244, 100, 10);
		else if(randNb == 4)
			return Color.CYAN;
		else
			return Color.ORANGE;
	}

	public boolean canMove(Block b, int addX, int addY)
	{
		List<Square> sqr = b.getSquareList();
		
		for(Square s : sqr)
			if(isObstacle(s.getX()+addX, s.getY()+addY))
				return false;
		
		return true;
	}
	
	public boolean isObstacle(int x, int y)
	{
		if(!isInBorders(x, y))
			return true;
		
		return checkMainTab(x, y);
	}
	
	public boolean isInBorders(int x, int y)
	{
		if((x>= 0 && x < WIDTH) && (y < HEIGHT)) // (y >= 0 && y < HEIGHT))
			return true;
		return false;
	}
	
	public boolean isOnTheGround(Block b)
	{
		List<Square> sqr = b.getSquareList();
		
		for(Square s : sqr)
			if(isInBorders(s.getX(), s.getY()) && !isInBorders(s.getX(), s.getY()+BLOCK_SIZE))
				return true;
		return false;
	}
	
	public Block getRandomBlock()
	{
		int randNb = (int)(Math.random()*7);
		return new Block(randNb, getRandColor());
	}
	
	public Block getRandomBlock(int forbiddenBlock, Color forbiddenColor)
	{
		int randNb;
		Color c;
		do
		{
			randNb = (int)(Math.random()*7);
		}while(randNb == forbiddenBlock);
		
		do
		{
			c = getRandColor();
		}while(c == forbiddenColor);
		
		return new Block(randNb, c);
	}
	
	public int[] getPosition(int l, int c)
	{
		return new int[] {l*BLOCK_SIZE, c*BLOCK_SIZE};
	}
	
	public int[] getGridPosition(Square sqr)
	{
		return new int[] {sqr.getX()/BLOCK_SIZE, sqr.getY()/BLOCK_SIZE};
	}
	
	public int[] getRealPosition(int j, int i)
	{
		return new int[] {j*BLOCK_SIZE, i*BLOCK_SIZE};
	}
	
	public void putInMainTab(Block b)
	{
		List<Square> sqr = b.getSquareList();
		int[] pos = null;
	
		for(Square s : sqr)
		{
			pos = getGridPosition(s);
			try
			{
				mainTab[pos[1]][pos[0]] = s;
			}
			catch(Exception e)
			{
			}
		}
		
	}
	
	public void dispMainTab(Graphics g)
	{
		for(int j=0;j<HEIGHT_BLOCK_MAX;j++)
			for(int i=0;i<WIDTH_BLOCK_MAX;i++)
				if(mainTab[j][i] != null)
					mainTab[j][i].draw(g);
	}
	
	public boolean checkMainTab(int x, int y)
	{
		if(mainTab == null)
			return false;
		
		for(Square[] sqr : mainTab)
		{
			for(Square s : sqr)
			{
				if(s != null)
					if((x >= s.getX() && x < s.getX()+BLOCK_SIZE) && (y >= s.getY() && y <= s.getY()))
					{
						return true;
					}
			}
		}
		
		return false;
	}
	
	public boolean isBlocked(Block b)
	{
		if(!canMove(block, 0, BLOCK_SIZE))
			return true;
		
		return false;
	}
	
	public int[] searchLines()
	{
		if(mainTab == null)
			return null;
		
		int index;
		List<Integer> l = new ArrayList<Integer>();
		
		for(int j=0;j<HEIGHT_BLOCK_MAX;j++)
		{
			index = 0;
			for(int i=0;i<WIDTH_BLOCK_MAX;i++)
			{
				if(mainTab[j][i] != null)
					index++;
			}
			if(index == WIDTH_BLOCK_MAX)
				l.add(j);
		}
		
		if(l.size() > 0)
		{
			int[] tab = new int[l.size()];
			
			for(int i=0;i<l.size();i++)
				tab[i] = l.get(i);
			
			return tab;
		}
		return null;
	}
	
	public void delLines(int[] lines)
	{
		if(lines == null || mainTab == null)
			return;
		
		for(Integer j : lines)
		{
			for(int i=0;i<WIDTH_BLOCK_MAX;i++)
				if(mainTab[j][i] != null)
					mainTab[j][i] = null;
		}
	}
	
	public List<int[]> fallLines()
	{
		if(mainTab == null)
			return null;
		List<int[]> list = new ArrayList<>();
		int count = 0, count2;
		//toString(mainTab);
		for(int j=HEIGHT_BLOCK_MAX-1;j>=1;j--)
		{
			count2 = 0;
			if(isEmptyLine(j))
			{
				for(int i=0;i<WIDTH_BLOCK_MAX;i++)
				{
					if(mainTab[j-1][i] != null)
					{
						mainTab[j+count][i] = mainTab[j-1][i];
						//mainTab[j+count][i].setY((j+count)*BLOCK_SIZE);
						mainTab[j-1][i] = null;
					}
					else
						count2++;
				}
				if(count2 == WIDTH_BLOCK_MAX)
					count++;
				else
				{
					list.add(new int[] {j, count});
					//fallAnimAndSetPos(j, count);
				}
			}
		}
		//toString(mainTab);
		return list;
	}
	
	public void toString(Square[][] tab)
	{
		if(tab == null)
			return;
		
		for(int j=0;j<HEIGHT_BLOCK_MAX;j++)
		{
			for(int i=0;i<WIDTH_BLOCK_MAX;i++)
			{
				if(tab[j][i] != null)
					System.out.println("tab["+j+"]["+i+"] = "+tab[j][i]);
				else
					System.out.println("tab["+j+"]["+i+"] = null");
			}
		}
		
	}
	
	public boolean contains(int[] tab, int integer)
	{
		if(tab == null)
			return false;
		for(Integer i : tab)
			if(i == integer)
				return true;
		return false;
	}
	
	public boolean isEmptyLine(int index)
	{
		if(mainTab == null || (index < 0 || index >= HEIGHT_BLOCK_MAX))
			return true;
		
		for(int i=0;i<WIDTH_BLOCK_MAX;i++)
			if(mainTab[index][i] != null)
			{
				return false;
			}
		return true;
	}

	public void refreshInfoPan(int[] tab)
	{
		score += 25*tab.length;
		lines += tab.length;
		level = (int)(lines/10);
		speed = 1000/(level+1);
		scoreLbl.setText(""+score);
		levelLbl.setText(""+level);
		linesLbl.setText(""+lines);
	}

	public void fallAnimAndSetPos(int index, int count)
	{
		for(int j=0;j<BLOCK_SIZE+BLOCK_SIZE*count;j++)
		{
			for(int i=0;i<WIDTH_BLOCK_MAX;i++)
				if(mainTab[index+count][i] != null)
				{
					mainTab[index+count][i].addY(1);
				}
			/*try {
				Thread.sleep(20);
				repaint();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		
	}

	public void startFallingAnim()
	{
		delLines(searchLines());
		listLines = fallLines();
		fallAnim = true;
		indexLines = 0;
		countIndex = 0;
		fallingSound.restart();
		fallingSound.play();
	}
	
	public void fallingAnim()
	{
		int[] t = listLines.get(indexLines);
		if(countIndex < BLOCK_SIZE+BLOCK_SIZE*t[1])
		{
			for(int i=0;i<WIDTH_BLOCK_MAX;i++)
				if(mainTab[t[0]+t[1]][i] != null)
				{
					mainTab[t[0]+t[1]][i].addY(2);
				}
				countIndex+=2;
				repaint();
		}
		else if(indexLines < listLines.size()-1)
		{
			countIndex = 0;
			indexLines++;
		}
		else
			fallAnim = false;
	}
	
	public void startBlinkAnim()
	{
		blinkAnim = true;
		countIndex = 0;
		if(searchLines().length >= 4)
		{
			lineClearSound.restart();
			lineClearSound.play();
		}
		else
		{
			lineSound.restart();
			lineSound.play();
		}
	}
	
	public void blinkingAnim(int animSpeed)
	{
		int[] t = searchLines();
		
		if((System.currentTimeMillis() - savedTime) > animSpeed)
		{
			savedTime = System.currentTimeMillis();
			for(int j=0;j<t.length;j++)
			{
				for(int i=0;i<WIDTH_BLOCK_MAX;i++)
					if(mainTab[t[j]][i] != null)
						mainTab[t[j]][i].reverseBlinkState();
			}
			countIndex++;
			if(countIndex >= 5)
			{
				blinkAnim = false;
				startFallingAnim();
			}
			else
			{
				repaint();
			}
		}
		
	}
	
	public boolean isBlockOnMainTab(Block b)
	{
		List<Square> sqr = b.getSquareList();
		for(Square s : sqr)
			if(checkMainTab(s.getX(), s.getY()))
				return true;
		return false;
	}

	public void genLevel(int difficulty)
	{
		int l = 10;
		boolean randNb;
		int[] pos;
		
		for(int j=l;j<HEIGHT_BLOCK_MAX;j++)
		{
			for(int i=0;i<WIDTH_BLOCK_MAX;i++)
			{
				randNb = (int)(Math.random()*10) > 6 ? false : true;
				if(randNb)
				{
					pos = getPosition(j, i);
					mainTab[j][i] = new Square(pos[1], pos[0], BLOCK_SIZE, getRandColor());
				}
					
			}
		}
		
	}
	
	public void drawBlackScreen(Graphics g)
	{
		g.setColor(new Color(0, 0, 0, 210));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	}

	public void setPause()
	{
		pause = true;
		timer.stop();
		repaint();
	}
	
	public void resume()
	{
		pause = false;
		timer.start();
	}
	
	public void restartGame()
	{
		block = getRandomBlock();
		nextBlock = getRandomBlock(block.getIndex(), block.getColor());
		
		score = 0;
		level = 0;
		lines = 0;
		lockedBlock = false;
		blinkAnim = false;
		indexLines = 0;
		countIndex = 0;
		fallAnim = false;
		pause = false;
		mainTab = new Square[HEIGHT_BLOCK_MAX][WIDTH_BLOCK_MAX];
		scoreLbl = new JLabel(""+score);
		levelLbl = new JLabel(""+level);
		linesLbl = new JLabel(""+lines);

		gameContainer = new GameContainer(WIDTH, HEIGHT);
		infoContainer = new InfoContainer(WIDTH/2, HEIGHT);

		this.removeAll();
		this.add(gameContainer);
		this.add(infoContainer);
		timer.start();
	    this.savedTime = System.currentTimeMillis();
	    this.savedTimeRotation = savedTime;
		gameOver = false;
		revalidate();
		repaint();
	}

	public void backToMenu()
	{
		window.setMenuView();
	}

}


