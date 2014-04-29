package dtolmach_rreinke_4;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class InteractionPanel extends JPanel {
	Boolean myTurn;
	
	public InteractionPanel(String name, Boolean myTurn) {
		this.myTurn = myTurn;
		Font myFont1 = new Font("Comic Sans MS", Font.BOLD, 20);
		Font myFont2 = new Font("Copperplate Gothic Bold", Font.BOLD, 30);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel();
		JLabel welcome = new JLabel ("Welcome to Battleship " + name + "!");
		welcome.setFont(myFont2);
		topPanel.add(welcome);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new FlowLayout());
		middlePanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		if(myTurn == true) {
			
			JLabel info = new JLabel("Choose a location to attack: ");
//			info.setFont(myFont1);
//			middlePanel.add(info);
//			JTextField move = new JTextField();
//			move.setPreferredSize(new Dimension(30, 30));
//			move.setMaximumSize(new Dimension(30, 30));
//			middlePanel.add(move);
//			JButton send = new JButton("Fire Away!");
//			middlePanel.add(send);
//			mainPanel.add(middlePanel, BorderLayout.CENTER);
			
		} else {
			
			JLabel info = new JLabel("Waiting on opponent...");
			info.setFont(myFont1);
			middlePanel.add(info);
			mainPanel.add(mainPanel, BorderLayout.CENTER);
			
		}
		
		JPanel bottomPanel = new JPanel();
		JButton surrender = new JButton("I Surrender Captain!");
		bottomPanel.add(surrender);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		add(mainPanel);
		
	}
	
}
