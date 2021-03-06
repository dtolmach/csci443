package dtolmach_rreinke_4;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import dtolmach_rreinke_4.Battleship.MessageSender;
import dtolmach_rreinke_4.Battleship.State;

public class MyPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	State myb[] = new State[64];
	State opb[] = new State[64];
	
	public MyPanel(String name, final MessageSender sender, final JTextArea messageArea, State myb[], State opb[], boolean enabled) throws IOException {
		
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
			
			if (myb[i] == State.EMPTY)
				test.setIcon(createImageIcon("Ocean_Square.jpg",i + ""));
			if (myb[i] == State.HIT)
				test.setIcon(createImageIcon("hit!.png",i + ""));
			if (myb[i] == State.SHIP)
				test.setIcon(createImageIcon("Large_Ship.png",i + ""));
			if (myb[i] == State.MISS)
				test.setIcon(createImageIcon("miss.png",i + ""));
			if (myb[i] == State.SUNK)
				test.setIcon(createImageIcon("sunken.png",i + ""));
			
			//test.setIcon(createImageIcon("miss.png", "A missile was fired, but missed"));
			//test.setIcon(createImageIcon("hit.png", "A missile was fired, and hit"));
			
			
			myPanel.add(test);
			
		}
		
		for(int j = 0; j < 64; j++){
			//Add a clicklistener to each label and have the picture change depending on
			//whether or not something was hit
			final JLabel test = new JLabel();
			if (opb == null)
				test.setIcon(createImageIcon("Ocean_Square.jpg", ""));
			else {
				if (opb[j] == State.EMPTY)
					if (enabled == false)
						test.setIcon(createImageIcon("grey.jpg", ""));
					else
						test.setIcon(createImageIcon("Ocean_Square.jpg",""));
				if (opb[j] == State.HIT)
					test.setIcon(createImageIcon("hit!.png",""));
				if (opb[j] == State.SHIP)
					test.setIcon(createImageIcon("Large_Ship.png", ""));
				if (opb[j] == State.MISS)
					test.setIcon(createImageIcon("miss.png",""));
				if (opb[j] == State.SUNK)
					test.setIcon(createImageIcon("sunken.png",""));
			}
			test.setName("" + j);
			if (enabled == true) {
				test.addMouseListener(new MouseAdapter() 
				{
					public void mouseClicked(MouseEvent e)  
				    {
						try {
							Battleship.setCellClicked(test.getName(), sender, messageArea);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
				    }			      
				});
			}
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
