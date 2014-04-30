package dtolmach_rreinke_4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import dtolmach_rreinke_4.Battleship.MessageSender;

public class CreateBoard extends JPanel {
	
	public static ArrayList<Integer> cellsClicked = new ArrayList<Integer>();
	
	public CreateBoard(final JPanel panelHolder, final String name, final MessageSender sender, final JTextArea messageArea) {
		
		Font myFont1 = new Font("Comic Sans MS", Font.BOLD, 18);
		Font myFont2 = new Font("Comic Sans MS", Font.BOLD, 30);
		JPanel setMyBoard = new JPanel();
		GridLayout layout = new GridLayout(8, 8);
		setMyBoard.setLayout(layout);
		setMyBoard.setBorder(BorderFactory.createEtchedBorder());
		setMyBoard.setPreferredSize(new Dimension(300, 300));
		setMyBoard.setMinimumSize(new Dimension(300, 300));
		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		JPanel bottom = new JPanel();
		
		// These are the instructions for setting up your own board
		JLabel welcome = new JLabel("Before the game begins, place these ships:");
		welcome.setFont(myFont2);
		welcome.setHorizontalAlignment(SwingConstants.CENTER);
		//JLabel instruction = new JLabel("Please place the following ships:");
		//instruction.setHorizontalAlignment(SwingConstants.CENTER);
		//instruction.setFont(myFont1);
		
		// Description of small ship placement
		JLabel smallShip1 = new JLabel("Three small ships (1 square each) -");
		smallShip1.setHorizontalAlignment(SwingConstants.CENTER);
		smallShip1.setFont(myFont1);
		JLabel smallShip = new JLabel();
		smallShip.setIcon(createImageIcon("small_ship.png", "This is the smallest ship"));
		smallShip.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Description of medium ship placement
		JLabel mediumShip1 = new JLabel("Two medium ship (3 adjacent squares each) -");
		mediumShip1.setHorizontalAlignment(SwingConstants.CENTER);
		mediumShip1.setFont(myFont1);
		JLabel mediumShip = new JLabel();
		mediumShip.setIcon(createImageIcon("Medium-Ship.png", "This is the medium ship"));
		mediumShip.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Description of large ship placement
		JLabel largeShip1 = new JLabel("One large ship (5 squares) -");
		largeShip1.setHorizontalAlignment(SwingConstants.CENTER);
		largeShip1.setFont(myFont1);
		JLabel largeShip = new JLabel();
		largeShip.setIcon(createImageIcon("Large_Ship.png", "This is the largest ship"));
		largeShip.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Add all of the ship placement directions to the panel
		top.add(welcome, BorderLayout.CENTER);
		JPanel introduction = new JPanel();
		introduction.setLayout(new GridLayout(10, 0));
		//introduction.add(instruction);
		introduction.add(smallShip1);
		introduction.add(smallShip);
		introduction.add(mediumShip1);
		introduction.add(mediumShip);
		introduction.add(largeShip1);
		introduction.add(largeShip);
		JButton addShip = new JButton("Add ship");
		addShip.setFont(myFont1);
		addShip.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {	            
	        	Battleship.callSenderSetBoard(sender, cellsClicked);
	        	cellsClicked.clear();
//	        	createNewShip();
	        	System.out.println("created new ship");
	        	
	        }
		});
		introduction.add(addShip);
		JButton startGame = new JButton("Start Game");
		startGame.setFont(myFont1);
		startGame.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	try {
	        		setVisible(false);
					sender.repaint();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	System.out.println("Starting game...");
	        	
	        }
		});
		introduction.add(startGame);
		JLabel help = new JLabel("For further instruction, click the help button!");
		help.setFont(myFont1);
		help.setForeground(Color.RED);
		introduction.add(help);
		bottom.add(introduction);
		add(top);
		
		JPanel setMyBoardHolder = new JPanel();
		setMyBoardHolder.setLayout(new BorderLayout());
		
		for(int i = 0; i < 64; i++) {
			
			final JLabel test = new JLabel();
			//Add a clicklistener to each label and have the picture change depending on
			//whether or not something was hit
			test.setIcon(createImageIcon("Ocean_Square.jpg", ""));
			test.setName("" + i);
			test.addMouseListener(new MouseAdapter() 
			{
				public void mouseClicked(MouseEvent e)  
			    {
					
					System.out.println(test.getName());
					test.setIcon(createImageIcon("spot_selected.png","This spot has a ship now"));
					cellsClicked.add(Integer.parseInt(test.getName()));
					
			    }			      
			});
			setMyBoard.add(test);
			
		}
		
//		setMyBoardHolder.add(setMyBoard, BorderLayout.CENTER);
//		add(setMyBoardHolder);
		bottom.add(setMyBoard);
		add(bottom);
		
	}
	
	private JMenu createHelpMenu() {
		// TODO Auto-generated method stub
		return null;
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
