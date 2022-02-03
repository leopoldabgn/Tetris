package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Menu extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private Window win;
	private Title title = new Title(20);
	private MenuBox menuBox = new MenuBox();
	private AudioPlayer mainMusic;
	private int mod = 0;
	
	public Menu(Window win)//int w, int h)
	{
		super();
		this.win = win;
		this.setPreferredSize(new Dimension(500,500));
		
		this.add(title);
		this.add(menuBox);
	}
	
	class Title extends JPanel
	{
		private static final long serialVersionUID = 1L;

		private String[][] letters = new String[][] {
				new String[] {
								"XXX",
								".X.",
								".X.",
								".X.",
								".X."},
				new String[] {
								"XXX",
								"X..",
								"XX.",
								"X..",
								"XXX"},
				new String[] {
								"XXX",
								"X.X",
								"XXX",
								"XX.",
								"X.X"},
				new String[] {
								".X.",
								".X.",
								".X.",
								".X.",
								".X."},
				new String[] {
								"XXX",
								"X..",
								"XXX",
								"..X",
								"XXX"}
				};
		
		private List<List<Square>> letList = new ArrayList<>();
			
		public Title(int fontSize)
		{
			super();
			int x = 0, y = 0, size = fontSize;
			this.setPreferredSize(new Dimension(((size*4)*(letters.length+1))-size, size*5));
			for(int i=0;i<letters.length;i++)
			{
				letList.add(Block.createSquareList(x, y, size, letters[i], GamePanel.getRandColor()));
				x += size*4;
				if(i == 1)
				{
					letList.add(Block.createSquareList(x, y, size, letters[0], GamePanel.getRandColor()));
					x += size*4;
				}
				
			}
			
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			for(int j=0;j<letList.size();j++)
			{
				for(Square sqr : letList.get(j))
				{
					sqr.draw(g);
				}
			}
		}
		
	}
	
	class MenuBox extends JPanel
	{
		private static final long serialVersionUID = 1L;

		private JPanel pan1, pan2, pan3;
		
		private StringButton[] arrows = new StringButton[] {
				new StringButton("<", false), new StringButton(">", true), 
				new StringButton("<", false), new StringButton(">", true)};
		
		private JLabel mod, music;
		
		private JButton validate = new JButton("Play");
		
		private ActionListener modListener, musicListener;
		
		private String[] musics = getAllMusics();
		
		private int modIndex = 0,
					musicIndex = 0;
		
		public MenuBox()
		{
			super();
			//Color boxColor = Color.GRAY;
			pan1 = new JPanel();
			pan2 = new JPanel();
			pan3 = new JPanel();
			
			mod = new JLabel("Normal Mod");
			if(musics != null)
				music = new JLabel(getNoExtName(new File(musics[0])));
			else
				music = new JLabel("No Music");
			
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			//pan1.setBackground(boxColor);
			pan1.setLayout(new BoxLayout(pan1, BoxLayout.LINE_AXIS));
			//pan2.setBackground(boxColor);
			pan2.setLayout(new BoxLayout(pan2, BoxLayout.LINE_AXIS));
			pan3.setLayout(new BoxLayout(pan3, BoxLayout.LINE_AXIS));

			pan1.add(arrows[0]);
			pan1.add(mod);
			pan1.add(arrows[1]);
			
			pan2.add(arrows[2]);
			pan2.add(music);
			pan2.add(arrows[3]);
			
			pan3.add(validate);
			
			modListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					StringButton sB = (StringButton)e.getSource();
					boolean direction = sB.getDirection();
					if(direction && modIndex == 0)
					{
						modIndex++;
						mod.setText("Break mod");
					}
					else if(!direction && modIndex == 1)
					{
						modIndex--;
						mod.setText("Normal mod");
					}
				}
			};
			
			musicListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(musics == null)
						return;
					StringButton sB = (StringButton)e.getSource();
					boolean direction = sB.getDirection();
					if(direction && (musicIndex < musics.length-1))
					{
						musicIndex++;
					}
					else if(!direction && (musicIndex > -1))
					{
						musicIndex--;
					}
					if(musicIndex > -1)
						music.setText(getNoExtName(new File(musics[musicIndex])));
					else
						music.setText("No Music");
				}
			};
		
			validate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(musics != null && musicIndex > -1)
						mainMusic = new AudioPlayer(musics[musicIndex], true);
					setMod(modIndex);
					win.startGame();
				}
			});
			
			for(int i=0;i<4;i++)
				if(i<2)
					arrows[i].addActionListener(modListener);
				else
					arrows[i].addActionListener(musicListener);
			
			this.add(pan1);
			this.add(pan2);
			this.add(pan3);
		}
	}
	
	 
	public class StringButton extends JButton 
	{
	    private static final long serialVersionUID = 1L;
	 
		private boolean direction;
	    
	    public StringButton(String txt, boolean direction)
	    {
	        super(txt);
	        this.direction = direction;
	        setForeground(Color.RED);
	        this.setFont(new Font(txt, Font.BOLD, 34));
	        setOpaque(false);
	        setContentAreaFilled(false); // On met à false pour empêcher le composant de peindre l'intérieur du JButton.
	        setBorderPainted(false); // De même, on ne veut pas afficher les bordures.
	        setFocusPainted(false); // On n'affiche pas l'effet de focus.
	         
	        setHorizontalAlignment(SwingConstants.CENTER);
	        setHorizontalTextPosition(SwingConstants.CENTER); 
	    }
	    
	    public boolean getDirection()
	    {
	    	return this.direction;
	    }
	    
	}
	
	public String getNoExtName(File f)
	{
		if(f == null)
			return null;
		return f.getName().substring(0, f.getName().lastIndexOf("."));
	}
	
	public boolean checkExtension(File f, String extension)
	{
		if(f.isFile())
		{
			String ext;
			ext = f.getName().substring(f.getName().lastIndexOf(".")+1).toUpperCase();
			if(ext.equals(extension.toUpperCase()))
					return true;
		}
		return false;
	}
	
	public String[] filesToStrings(File[] files)
	{
		List<String> strList = new ArrayList<>();
		String[] strTab;
		if(files == null)
			return null;
		
		for(File f : files)
			strList.add(f.getAbsolutePath());
		
		strTab = new String[strList.size()];
		for(int i=0;i<strList.size();i++)
			strTab[i] = strList.get(i);
		
		return strTab;
	}
	
	private String[] getAllMusics()
	{
		File musicFolder = new File("musics");
		FileFilter wavFilter = new FileFilter() {
			public boolean accept(File f) {
				return checkExtension(f, "WAV");
			}
		};

	      File[] list = musicFolder.listFiles(wavFilter);
	      if(list == null)
	      {
	        return null;
	      }
	      else
	    	  return filesToStrings(list);
	      
	}
	
	public AudioPlayer getMusic()
	{
		return mainMusic;
	}
	
	public void setMod(int mod)
	{
		this.mod = mod;
	}
	
	public int getMod()
	{
		return mod;
	}
	
}
