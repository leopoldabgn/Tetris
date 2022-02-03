package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JFrame // implements Runnable
{
	private static final long serialVersionUID = 1L;
	
	public static Color backgroundColor = new Color(47, 47, 47);

	private JPanel container = new JPanel();
	private Menu menu = new Menu(this);
	private GamePanel gamePanel;
	
	public Window(int w, int h) 
	{
		super();
		this.setTitle("Tetris");
		this.setSize(new Dimension(w, h));
		this.setMinimumSize(new Dimension(w, h));
		//this.setMaximumSize(new Dimension(w, h));
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setDefaultLookAndFeelDecorated(true);
		//this.setExtendedState(Frame.MAXIMIZED_BOTH);
		container.setBackground(backgroundColor);
		container.add(menu, BorderLayout.CENTER);
		this.getContentPane().add(container, BorderLayout.CENTER);
		
		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
					try {
						if(menu.getMusic() != null)
							menu.getMusic().stop();
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
						e1.printStackTrace();
					}
				System.exit(0);
				}
		});
		//this.run();
	}
	
	public void startGame()
	{
		gamePanel = new GamePanel(this, menu.getMod());
		container.removeAll();
		container.add(gamePanel, BorderLayout.CENTER);
		this.repaint();
		gamePanel.revalidate();
		gamePanel.requestFocus();
	}
	
	public void setMenuView()
	{
		if(menu.getMusic() != null)
		try {
			menu.getMusic().stop();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
		container.removeAll();
		container.add(menu, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	/*@Override
	public void run() 
	{
		boolean bool = true;
		double savedTime = System.currentTimeMillis();
		while(true)
		{
			if(System.currentTimeMillis() - savedTime > 2)
			{
				savedTime = System.currentTimeMillis();

					gamePanel.requestFocus();
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					gamePanel2.requestFocus();

			}
		}
	}*/
	
}
