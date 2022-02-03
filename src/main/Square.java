package main;

import java.awt.Color;
import java.awt.Graphics;

public class Square 
{

	private int size, x, y;
	private Color color;	
	private boolean blinkBool = false;
	
	public Square(int x, int y, int size, Color color)
	{
		this.size = size;
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public void draw(Graphics g)
	{
		int s = (int)(size/6);
		int s2 = (int)(s/2);
		if(isBlinking())
		{
			g.setColor(Color.WHITE);
			g.fillRect(x, y, size, size);
			g.setColor(getDarkColor(Color.WHITE, 30));
			g.drawRect(x, y, size, size);
		}
		else
		{
			Color darkColor = getDarkColor(color, 30);
			g.setColor(color);
			g.fillRect(x, y, size, size);
			g.setColor(Color.WHITE);
			g.fillRect(x, y, s, s);
			g.fillRect(x+s, y+2*s, s, s);
			g.fillRect(x+s, y+s, s, s);
			g.fillRect(x+2*s, y+s, s, s);
			g.setColor(darkColor);
			//g.drawRect(x, y, size, size);
			g.fillRect(x+s2, y, size-s2, s2);
			g.fillRect(x, y+size-s2, size, s2);
			g.fillRect(x, y+s2, s2, size-s2);
			g.fillRect(x+size-s2, y, s2, size-s2);
		}
	}

	public Color getDarkColor(Color c, int var)
	{
		int[] cTab = new int[] {c.getRed()-var, c.getGreen()-var, c.getBlue()-var};

		for(int i=0;i<3;i++)
			if(cTab[i] < 0)
				cTab[i] = 0;
		
		return new Color(cTab[0], cTab[1], cTab[2]);
	}
	
	public Color getColor() 
	{
		return color;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void addX(int nb)
	{
		this.x += nb;
	}
	
	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
		
	public void addY(int nb)
	{
		this.y += nb;
	}
	
	public boolean isBlinking()
	{
		return blinkBool;
	}
	
	public void setBlinkState(boolean bool)
	{
		this.blinkBool = bool;
	}
	
	public void reverseBlinkState()
	{
		if(blinkBool)
			blinkBool = false;
		else
			blinkBool = true;
	}
}