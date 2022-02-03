package main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

class BoxPan extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final int BLOCK_SIZE = GamePanel.BLOCK_SIZE;
	private Block theBlock;
	private JLabel lbl;
	
	public BoxPan(int w, int h, String str, JLabel valLbl)
	{
		super();
		this.setPreferredSize(new Dimension(w, h));

		Font font = new Font("Arial", Font.PLAIN, 38);
		
		lbl = new JLabel(str);
		lbl.setFont(font);
		valLbl.setFont(font);
		
		JPanel pan = new JPanel(),
				pan2 = new JPanel(),
				pan3 = new JPanel();
		pan.add(pan2);
		pan2.add(lbl);
		pan.add(pan3);
		pan3.add(valLbl);
		pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
		pan2.setLayout(new BoxLayout(pan2, BoxLayout.LINE_AXIS));
		pan3.setLayout(new BoxLayout(pan3, BoxLayout.LINE_AXIS));

		this.add(pan);
	}
	
	public BoxPan(int w, int h, Block theBlock)
	{
		super();
		this.setPreferredSize(new Dimension(w, h));
		setBlock(theBlock);
	}
	
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
		if(theBlock != null)
			theBlock.draw(g);
	}
	
	public void setBlock(Block b)
	{
		Dimension dim;
		int index = b.getIndex();
		
		if(index == 0)
			dim = new Dimension(BLOCK_SIZE, BLOCK_SIZE);
		else if(index >= 1 && index <= 2)
			dim = new Dimension(BLOCK_SIZE/2, 0);
		else if(index == 3)
			dim = new Dimension(0, BLOCK_SIZE/2);
		else if(index == 4)
			dim = new Dimension(BLOCK_SIZE, BLOCK_SIZE/2);
		else if(index >= 5 && index <= 6)
			dim = new Dimension(BLOCK_SIZE/2, BLOCK_SIZE);
		else
			dim = new Dimension(0, 0);
		
		this.theBlock = new Block(b, (int)dim.getWidth(), (int)dim.getHeight());
	}
	
}
