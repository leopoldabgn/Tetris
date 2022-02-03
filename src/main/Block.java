package main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Block 
{

	private String[][] blockTab = new String[][]{ // 7 pieces differentes !
			
			new String[] {  "XX.",
						    "XX.",
						    "..."},
			
			new String[] {  ".X..",
							".X..",
							".X..",
							".X.."},
			
			new String[] {  "...",
							"XXX",
							".X."},
			
			new String[] {  ".X.",
				    		".X.",
							".XX"},
			
			new String[] {  ".X.",
							".X.",
							"XX."},
	
			new String[] {  "XX.",
							".XX",
							"..."},
	
			new String[] {  ".XX",
							"XX.",
							"..."},
			
			};
			
			private int index;
			private List<Square> sqr = new ArrayList<>();
			private Color color;
			private final int BLOCK_SIZE = GamePanel.BLOCK_SIZE;
			private int x, y;
		
	public Block(Block block)
	{
		this.index = block.getIndex();
		this.color = block.getColor();
		this.x = block.getX();
		this.y = block.getY();
		createSquareList(this.getX(), this.getY());
	}
			
	public Block(Block block, int x, int y)
	{
		this.index = block.getIndex();
		this.color = block.getColor();
		this.x = x;
		this.y = y;
		createSquareList(x, y);
	}
	
	public Block(int index, Color color)
	{
		this.index = index;
		this.color = color;
		this.x = BLOCK_SIZE*4;
		this.y = 0;
		createSquareList(BLOCK_SIZE*4,-BLOCK_SIZE*2);
	}
	
	public void draw(Graphics g)
	{
		if(sqr != null)
			for(Square s : sqr)
				s.draw(g);
	}

	public char[][] toCharTab(String[] tab)
	{
		char[][] temp = new char[tab.length][tab[0].length()];
        for(int i=0;i<tab.length;i++)
        	temp[i] = tab[i].toCharArray();
        
        return temp;
	}
	
	public void rotate() // RECUPERER FONCTION RUBIKS CUBE QUI PERMET DE PERMUTER LE HAUT DU CUBE.
	{
		if(index != 1)
		{
			String tab[] = blockTab[index];
	        char[][] temp = toCharTab(tab);
	        char wait;
	        
	        wait = temp[0][2];
	
	        temp[0][2] = temp[0][0];
	        temp[0][0] = wait;
	
	        wait = temp[2][2];
	        temp[2][2] = temp[0][0];
	        temp[0][0] = temp[2][0];
	        temp[2][0] = wait;
			
	        wait = temp[1][2];
	
	        temp[1][2] = temp[0][1];
	        temp[0][1] = wait;
	
	        wait = temp[2][1];
	        temp[2][1] = temp[0][1];
	        temp[0][1] = temp[1][0];
	        temp[1][0] = wait;
	        
	        for(int j=0;j<tab.length;j++)
	        	tab[j] = ""+temp[j][0]+temp[j][1]+temp[j][2];
		}
		else
		{
			String[] tab = new String[] {  ".X..",
									       ".X..",
									 	   ".X..",
										   ".X.."};
			
			if(Arrays.equals(blockTab[index], tab))
			{
				blockTab[index] = new String[] {  "....",
											      "XXXX",
												  "....",
												  "...."};
			}
			else
				blockTab[index] = tab;
		}
        
        /*for(String b : tab)
        	System.out.println(b);
        
        System.out.println("");*/
	}
	
	public void createSquareList(int x, int y)
	{
		String[] pattern = blockTab[index];
		sqr.removeAll(sqr);
		for(int j=0;j<pattern.length;j++)
		{
			for(int i=0;i<pattern[0].length();i++)
			{
				if(pattern[j].charAt(i) == 'X')
				{
					sqr.add(new Square(x+i*BLOCK_SIZE, y+j*BLOCK_SIZE, BLOCK_SIZE, color));
				}
			}
		}
				
	}
	
	public static List<Square> createSquareList(int x, int y, int size, String[] pattern, Color c)
	{
		List<Square> square = new ArrayList<>();
		for(int j=0;j<pattern.length;j++)
		{
			for(int i=0;i<pattern[0].length();i++)
			{
				if(pattern[j].charAt(i) == 'X')
				{
					square.add(new Square(x+i*size, y+j*size, size, c));
				}
			}
		}
		
		return square;		
	}
	
	public void refreshSquareList()
	{
		createSquareList(x, y);
	}
	
	public void move(int mouvX, int mouvY)
	{
		for(Square s : sqr)
		{
			s.addX(mouvX);
			s.addY(mouvY);
		}
		x += mouvX;
		y += mouvY;
	}
	
	public boolean checkCoordinate(int x1, int y1, int x2, int y2)
	{
		for(Square s : sqr)
			if(!((s.getX() >= x1 && s.getX() <= x2) && (s.getY() >= y1 && s.getY() <= y2)))
			{
				System.out.println(x1+"--"+x2+"  "+y1+"--"+y2);
				System.out.println(s.getX()+" "+s.getY());
				return false;
			}
		return true;
	}
	
	public boolean canRotate()
	{
		if(index == 0)
			return false;
	return true;
	}
	
	public List<Square> getSquareList()
	{
		return sqr;
	}

	public int getIndex() 
	{
		return index;
	}

	public void setIndex(int index) 
	{
		this.index = index;
	}
	
	public Color getColor()
	{
		return color;
	}

	public int getX() 
	{
		return x;
	}

	public int getY() 
	{
		return y;
	}
	
}
