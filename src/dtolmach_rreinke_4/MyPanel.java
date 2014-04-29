package dtolmach_rreinke_4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MyPanel extends JPanel {
	
	private enum State1 {NEITHER, HIT, MISS};
	
	public MyPanel(String name, final Battleship b) throws IOException {
		
		Font myFont1 = new Font("Comic Sans MS", Font.BOLD, 20);
		GridLayout layout1 = new GridLayout(0, 2);
		setLayout(layout1);
		//layout1.setHgap(10);
		setSize(700, 200);
		
		GridLayout layout = new GridLayout(8, 8);
		
		JPanel myPanel = new JPanel();
		JPanel myPanelHolder = new JPanel();
		myPanel.setLayout(layout);
		myPanel.setBorder(BorderFactory.createEtchedBorder());
		myPanelHolder.setLayout(new BorderLayout());
		
		JPanel opponentPanel = new JPanel();
		JPanel opponentPanelHolder = new JPanel();
		opponentPanel.setLayout(layout);
		opponentPanel.setBorder(BorderFactory.createEtchedBorder());
		opponentPanelHolder.setLayout(new BorderLayout());
		
		for(int i = 0; i < 64; i++) {			
			JLabel test = new JLabel();
			//test.setIcon(createImageIcon("miss.png", "A missile was fired, but missed"));
			//test.setIcon(createImageIcon("hit.png", "A missile was fired, and hit"));
			test.setIcon(createImageIcon("Ocean_Square.jpg",i + ""));
			myPanel.add(test);
			
		}
		
		for(int j = 0; j < 64; j++){
			//Add a clicklistener to each label and have the picture change depending on
			//whether or not something was hit
			final JLabel test = new JLabel();
			test.setIcon(createImageIcon("Ocean_Square.jpg", j + ""));
			test.setName("" + j);
			test.addMouseListener(new MouseAdapter() 
			{
				public void mouseClicked(MouseEvent e)  
			    {
					Battleship.setCellClicked(test.getName(), b);
					
			    }			      
			});
			opponentPanel.add(test);
				
		}
		
		JLabel myBoard = new JLabel(name + "'s Board");
		myBoard.setFont(myFont1);
		myBoard.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel opponentBoard = new JLabel("Opponent's Board");
		opponentBoard.setFont(myFont1);
		opponentBoard.setHorizontalAlignment(SwingConstants.CENTER);
		
		myPanelHolder.add(myBoard, BorderLayout.NORTH);
		myPanelHolder.add(myPanel, BorderLayout.CENTER);
		add(myPanelHolder);
		
		opponentPanelHolder.add(opponentBoard, BorderLayout.NORTH);
		opponentPanelHolder.add(opponentPanel, BorderLayout.CENTER);
		add(opponentPanelHolder);
		
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path,
	                                           String description) {
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}

}
