package dtolmach_rreinke_4;

/**
 * A simple GUI to be used to implement asynchronous chat using socket based
 * connections between two machines where one machine acts as the server and
 * the other machine acts as the client.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.OverlayLayout;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import dtolmach_rreinke_4.Battleship.MessageSender;


public class Battleship 
{
	public enum State {
	    MISS, HIT, SUNK, SHIP, EMPTY 
	}
  
  // Somewhat arbitrary, but see the section titled Understanding Ports:
  // http://docs.oracle.com/javase/tutorial/networking/overview/networking.html
  private static final int PORT = 51042;
  private static final int maxShips = 1;
  
  private GameServer server;
  private GameClient client;
  private MessageSender sender;
  private Thread listener;

  private JFrame frame;
  private JTextArea messageArea;
  private static String myUsername;
  
  private JPanel myPanelHolder;

  private String theirUsername;  

  public Battleship() throws IOException
  {
    // Add a random 3-digit number for debugging when both server and client are on the same machine...
    this.myUsername = System.getenv( "USERNAME" ) + ( (int)( Math.random() * 900 + 100 ) );

    GridLayout layout = new GridLayout(2, 0);
	layout.setHgap(10);
	this.frame = new JFrame("Welcome to Battleship!");
	this.frame.setSize(800, 800);
	this.frame.setVisible(true);
	this.frame.setLayout(layout);
	
	// A multi-line, non-editable text area for the chat messages.
    this.messageArea = new JTextArea( 20, 40 );
    this.messageArea.setEditable( false );
    this.messageArea.setLineWrap( true );
    this.messageArea.setWrapStyleWord( true );
	
	InteractionPanel interact = new InteractionPanel(myUsername, true);
	interact.add( new JScrollPane( this.messageArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER ) );
	frame.add(interact);
	
	this.myPanelHolder = new JPanel();
	this.myPanelHolder.setLayout(new OverlayLayout(myPanelHolder));
	
//	MyPanel myPanel = new MyPanel(myUsername, this);
	
	frame.add(myPanelHolder);    
    
    JMenuBar menuBar = new JMenuBar();
    menuBar.add( createFileMenu() );
    menuBar.add( createHelpMenu() );
    this.frame.setJMenuBar( menuBar );

    this.frame.pack();
    this.frame.setVisible( true ); 
    
  }

  private JMenu createFileMenu()
  {
    FileMenuListener listener = new FileMenuListener();
    
    JMenu fileMenu = new JMenu( "File" );
    fileMenu.setMnemonic( KeyEvent.VK_F );

    JMenuItem menuItem = new JMenuItem( "New Game...", KeyEvent.VK_N );
    menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_N, ActionEvent.ALT_MASK ) );
    menuItem.addActionListener( listener );
    fileMenu.add( menuItem );
   
    menuItem = new JMenuItem( "Join Game...", KeyEvent.VK_J );
    menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_J, ActionEvent.ALT_MASK ) );
    menuItem.addActionListener( listener );
    fileMenu.add( menuItem );
   
    fileMenu.addSeparator();

    fileMenu.addSeparator();
    menuItem = new JMenuItem( "Exit", KeyEvent.VK_X );
    menuItem.addActionListener( listener );
    fileMenu.add( menuItem );
    
    return fileMenu;
  }
  
  private void setMsgArea(String msg)
  {
	  messageArea.append( myUsername + ": " + msg + "\n" );
  }
  
  private class FileMenuListener implements ActionListener
  {
    @Override
    public void actionPerformed( ActionEvent ae )
    {
      // Switch statements with Strings is new in JDK 7.
      switch( ae.getActionCommand() )
      {
        case "New Game..." :
          //JOptionPane.showMessageDialog( frame, "Start a chat server/client here...", ae.getActionCommand(), JOptionPane.INFORMATION_MESSAGE );
          server = new GameServer( messageArea, myPanelHolder );          
          sender = server;
          myPanelHolder.add(new CreateBoard(myPanelHolder, myUsername, sender, messageArea));
          myPanelHolder.revalidate();
          listener = new Thread( server );
          listener.start();
          break;
        case "Join Game..." :
          //JOptionPane.showMessageDialog( frame, "Start a chat server/client here...", ae.getActionCommand(), JOptionPane.INFORMATION_MESSAGE );
          client = new GameClient( messageArea, myPanelHolder );          
          sender = client;
          myPanelHolder.add(new CreateBoard(myPanelHolder, myUsername, sender, messageArea));
          myPanelHolder.revalidate();
          listener = new Thread( client );
          listener.start();
          break;
        case "Exit" :
          // Send the "GoodBye" message to stop the other thread.
          if( sender != null )  sender.sendMessage( "GoodBye" );
          // Wait for the listener thread (which may be either a server or client) to stop,
          // which happens when the "GoodBye" message is sent and returned.
          frame.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
          while( listener.isAlive() )
          {
            try { Thread.sleep( 200 ); } catch( InterruptedException e ) { /* Ignore interrupt. */ }
          }
          System.exit( 0 );
          break;
      }
    }
  }

  private JMenu createHelpMenu()
  {
    HelpMenuListener listener = new HelpMenuListener();
    
    JMenu helpMenu = new JMenu( "Help" );
    helpMenu.setMnemonic( KeyEvent.VK_H );

    JMenuItem menuItem = new JMenuItem( "About...", KeyEvent.VK_A );
    helpMenu.add( menuItem );
    menuItem.addActionListener( listener );
    
    return helpMenu;
  }

  private class HelpMenuListener implements ActionListener
  {
    @Override
    public void actionPerformed( ActionEvent ae )
    {
      String message = null;
      // Switch statements with Strings is new in JDK 7.
      switch( ae.getActionCommand() )
      {
        case "About..." :
          message = "Simple Chat\n\nCS 443, Spring 2014\n\nAuthor: Dr. Randy Bower\n\n";
          break;
      }
      JOptionPane.showMessageDialog( frame, message, ae.getActionCommand(), JOptionPane.INFORMATION_MESSAGE );
    }
  }
  
  /**
   * Invocates the sendMessage sender for either the client or server
   * @param message 
   * @param sender2 -> the battleship object to identify current sender and message area
 * @throws IOException 
   */
  public static void setCellClicked(String message, MessageSender sender2, JTextArea messageA) throws IOException{
	  messageA.append(myUsername + " clicked on " + message + "\n");
	  sender2.sendMessage("NUM");
	  sender2.sendMessage(message);	  
	  //repaint board and disable it for opponents move
	  sender2.repaint(false);
  }
  
  public static void callSenderSetBoard(MessageSender sender2, ArrayList<Integer> cellsClicked) {
	  sender2.addShipToBoard(cellsClicked);		
	}

  public static void main( String args[] )
  {
    try
    {
      javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName() );
    }
    catch( Exception e )
    {
      // Ignore exceptions and continue; if this fails for some reason, the GUI
      // will still open with default Java, six-year-old-with-a-crayon look.
      System.err.println( "Problem setting UI." );
    }    

    /*
     * Create the GUI. For thread safety, this method should be invoked from the event-dispatching thread.
     * See http://docs.oracle.com/javase/7/docs/api/javax/swing/package-summary.html#threading
     */
    try
    {
      SwingUtilities.invokeAndWait( new Runnable()
      {
        public void run()
        {
          try {
			new Battleship();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
      } );
    }
    catch( Exception e )
    {
      System.err.println( "ERROR: createGUI() did not complete successfully." );
      e.printStackTrace();
    }
  }

  /*
   * Creating an interface with both SimpleChatServer and SimpleChatClient implementing
   * the interface so both can send messages.
   */
  public interface MessageSender
  {
    public void sendMessage( String message );	
    public void setLastClicked(int parseInt);
	public void addShipToBoard(ArrayList<Integer> cellsClicked);
    public void showBoards() throws IOException;
    public void repaint(boolean enabled);
    
  }

  /*
   * Adapted from http://docs.oracle.com/javase/tutorial/networking/sockets/index.html
   */
  private class GameServer implements Runnable, MessageSender
  {
	private JTextArea messages;
    private PrintWriter output;
    Player p;
    private int lastClick;
    private MyPanel myPanel;

    public GameServer( JTextArea messages, JPanel myPanelHolder )
    {
    	this.messages = messages;
    	p = new Player();
    }

    public void sendMessage( String message )
    {
      this.output.println( message );
    }

	@Override
	public void addShipToBoard(ArrayList<Integer> cellsClicked) {
		Ship s = new Ship();
		for(int c: cellsClicked)
			s.addCell(c);
		p.addShip(s);		
		//
	}

	@Override
	public void showBoards() throws IOException {
		myPanelHolder.removeAll();
		myPanel = new MyPanel(myUsername, sender, messageArea, p.getMyBoard(), p.getOppBoard(), true);
	    myPanelHolder.add(myPanel);
		
	}

	@Override
	public void setLastClicked(int parseInt) {
		lastClick = parseInt;		
	}

	@Override
	public void run() {
		try
	      {
	        messages.append( "Connect to " + InetAddress.getLocalHost().getHostAddress() + " ...\n" );
	      }
	      catch( UnknownHostException e )
	      {
	        e.printStackTrace();
	        System.exit( 1 );
	      }

	      try( ServerSocket serverSocket = new ServerSocket( PORT );
	           Socket clientSocket = serverSocket.accept();  // Blocks until a connection is made.
	           BufferedReader in = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
	           PrintWriter out = new PrintWriter( clientSocket.getOutputStream(), true ); )
	      {
	        // Save a reference to the output connection for use by sendMessage method.
	        this.output = out;

	        // Exchange user names.
	        theirUsername = in.readLine();
	        out.println( myUsername );
	        messages.append( "Connected to " + theirUsername + ".\n" );

	        

	        // Sending messages will happen on the GUI thread by calling the sendMessage method.
	        // Thus, this thread only needs to listen for messages and display them.
	        String message;
	        do
	        {
	          message = in.readLine();  // Blocks until a message is received.
	          if (message.equals("MSG")) {
	        	  message = in.readLine();
	        	  messages.append(message + "\n");
	          }
	          else if (message.equals("NUM")) {
	        	  message = in.readLine();
	        	  messages.append(theirUsername + "clicked on " + message + "\n");
	        	  int num;
	        	  try {
	        		  num = Integer.parseInt(message);
	        	  } catch (NumberFormatException nfe) {
	        		  num = -1;
	        	  }
	        	  if (num >= 0) {
	        		  p.validateOpponentMove(num);
	        		  State s = p.getMyBoardState(num);
	        		  messages.append(" -- " + stateToString(s));
	        		  // send state to opponent
	        		  out.println("STATE");
	        		  out.println(num);
	        		  out.println(stateToString(s));
	        		  repaint(true);
	        		  
	        		  // check if game is over
	        		  if (p.getMySunkShips() == maxShips){
	        			  messages.append("GAME OVER \n");
	        			  out.println("GAME OVER");
	        			  repaint(false);
	        		  }
	        	  }
	          }
	          else if (message.equals("STATE")) {
	        	  message = in.readLine();
	        	  int num;
	        	  try {
	        		  num = Integer.parseInt(message);
	        	  } catch (NumberFormatException nfe) {
	        		  num = -1;
	        	  }
	        	  message = in.readLine();
	        	  messages.append(" -- " + message);
	        	  if (num >=0) {
		        	  if (message.equals("hit")) p.setOpponentState(State.HIT, num);
		        	  if (message.equals("sunk")) p.setOpponentState(State.SUNK, num);
		        	  if (message.equals("miss")) p.setOpponentState(State.MISS, num);
	        	  }
	          }
//	          repaint(true);      
	        	  
	        }
	        while( !message.equalsIgnoreCase( "GoodBye" ) );
	        
	        // Send the "GoodBye" message back to stop the other thread.
	        out.println( "GoodBye" );

	        
	      }
	      catch( IOException e )
	      {
	        e.printStackTrace();
	        System.exit( 1 );
	      }
	    }

	@Override
	public void repaint(boolean enabled) {
		myPanelHolder.removeAll();
		try {
			myPanel = new MyPanel(myUsername, sender, messageArea, p.getMyBoard(), p.getOppBoard(), enabled);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myPanelHolder.add(myPanel);
		myPanelHolder.revalidate();
		
	}
		
	}
  

  /*
   * Adapted from http://docs.oracle.com/javase/tutorial/networking/sockets/index.html
   */
  private class GameClient implements Runnable, MessageSender
  {
	private JTextArea messages;
    private PrintWriter output;
    private boolean myMove;
    private Player p;
    private int lastClick;
    private MyPanel myPanel;

    public GameClient( JTextArea messages, JPanel myPanelHolder )
    {
    	this.messages = messages;
    	myMove = false;
    	p = new Player();
    }
    
    public void sendMessage( String message )
    {
      this.output.println( message );
    }
    
    /**
     @Override
    public void run()
    {
      String ip = JOptionPane.showInputDialog( frame, "Enter IP address for server:", "HelloClient", JOptionPane.QUESTION_MESSAGE );

      // For debugging when both server and client are on the same machine...
      if( ip == null || ip.isEmpty() )
      {
        try
        {
          ip = InetAddress.getLocalHost().getHostAddress();
        }
        catch( UnknownHostException e )
        {
          e.printStackTrace();
          System.exit( 1 );
        }
      }
      
      messages.append( "Connecting to " + ip + " ...\n" );

      try( Socket clientSocket = new Socket( ip, PORT );
           BufferedReader in = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
           PrintWriter out = new PrintWriter( clientSocket.getOutputStream(), true ); )
      {
        // Save a reference to the output connection for use by sendMessage method.
        this.output = out;

        // Exchange user names.
        out.println("USER");
        out.println( myUsername );
        String line = in.readLine();
        if (line.equals("USER"))
        	theirUsername = in.readLine();
        
        messages.append( "Connected to " + theirUsername + ".\n" );

        // Enable the message JTextField so it looks like things are ready to go.
//        messageField.setEnabled( true );

        // Sending messages will happen on the GUI thread by calling the sendMessage method.
        // Thus, this thread only needs to listen for messages and display them.
        String message;
        do
        {
        	message = in.readLine();  // Blocks until a message is received.
            
            if (message.equals("NUM")) {
          	  message = in.readLine();
          	  int num;
                try{
              	  num = Integer.parseInt(message);
                } catch(NumberFormatException nfe)   {
              	  num = -1;
                }
                
                if (num >= 0) {
      	          p.validateOpponentMove(num);
      	          State s  = p.getMyBoardState(num);
      	          out.println("STATE");
      	          out.println(stateToString(s));
      	          
      	          
      	          //check if the game is over
      	          if (p.getMySunkShips() == maxShips){
      	        	  //game over
      	        	  out.println("GAMEOVER");
      	          }
      	          //repaint board and enable for move
//      	         repaint();
      	      }
            }
            else if (message.equals("STATE")) {
          	  message = in.readLine();
          	  if (message.equals("hit"))
          		  p.setOpponentState(State.HIT, lastClick);
          	  if (message.equals("sunk"))
          		  p.setOpponentState(State.SUNK, lastClick);
          	  if (message.equals("miss"))
          		  p.setOpponentState(State.MISS, lastClick);
            }
            else if (message.equals("MSG")) {
          	  messages.append(in.readLine());
            }
        }
        while( !message.equalsIgnoreCase( "GoodBye" ) );

        // Send the "GoodBye" message back to stop the other thread.
        out.println( "GoodBye" );

        // Disable the message JTextField so it looks like things are done.
//        messageField.setEnabled( false );
      }
      catch( UnknownHostException e )
      {
        System.err.println( "Couldn't connect to: " + ip );
        e.printStackTrace();
        System.exit( 1 );
      }
      catch( IOException e )
      {
        System.err.println( "Couldn't get I/O for the connection to: " + ip );
        e.printStackTrace();
        System.exit( 1 );
      }
    }
    **/

	@Override
	public void addShipToBoard(ArrayList<Integer> cellsClicked) {
		Ship s = new Ship();
		for(int c: cellsClicked)
			s.addCell(c);
		p.addShip(s);
		
	}

	@Override
	public void showBoards() throws IOException {
		myPanelHolder.removeAll();
		myPanel = new MyPanel(myUsername, sender, messageArea, p.getMyBoard(), p.getOppBoard(), false);
	    myPanelHolder.add(myPanel);
		
	}

	@Override
	public void setLastClicked(int parseInt) {
		lastClick = parseInt;
		
	}

	@Override
	public void run() {
		String ip = JOptionPane.showInputDialog( frame, "Enter IP address for server:", "HelloClient", JOptionPane.QUESTION_MESSAGE );

	      // For debugging when both server and client are on the same machine...
	      if( ip == null || ip.isEmpty() )
	      {
	        try
	        {
	          ip = InetAddress.getLocalHost().getHostAddress();
	        }
	        catch( UnknownHostException e )
	        {
	          e.printStackTrace();
	          System.exit( 1 );
	        }
	      }
	      messages.append( "Connecting to " + ip + " ...\n" );

	      try ( Socket clientSocket = new Socket( ip, PORT );
	           BufferedReader in = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
	           PrintWriter out = new PrintWriter( clientSocket.getOutputStream(), true ); )
	      {
	        // Save a reference to the output connection for use by sendMessage method.
	        this.output = out;

	        // Exchange user names.
	        out.println( myUsername );
	        theirUsername = in.readLine();
	        messages.append( "Connected to " + theirUsername + ".\n" );

	        

	        // Sending messages will happen on the GUI thread by calling the sendMessage method.
	        // Thus, this thread only needs to listen for messages and display them.
	        String message;
	        do
	        {
	        	message = in.readLine();  // Blocks until a message is received.
		          if (message.equals("MSG")) {
		        	  message = in.readLine();
		        	  messages.append(message + "\n");
		          }
		          else if (message.equals("NUM")) {
		        	  message = in.readLine();
		        	  messages.append(theirUsername + "clicked on " + message + "\n");
		        	  int num;
		        	  try {
		        		  num = Integer.parseInt(message);
		        	  } catch (NumberFormatException nfe) {
		        		  num = -1;
		        	  }
		        	  if (num >= 0) {
		        		  p.validateOpponentMove(num);
		        		  State s = p.getMyBoardState(num);
		        		  messages.append(" -- " + stateToString(s));
		        		  // send state to opponent
		        		  out.println("STATE");
		        		  out.println(num);
		        		  out.println(stateToString(s));
		        		  
		        		  //enable board
		        		  repaint(true);
		        		  
		        		  // check if game is over
		        		  if (p.getMySunkShips() == maxShips){
		        			  messages.append("GAME OVER \n");
		        			  out.println("GAME OVER");
		        			  //disable board if game is over
		        			  repaint(false);
		        		  }
		        	  }
		          }
		          else if (message.equals("STATE")) {
		        	  message = in.readLine();
		        	  int num;
		        	  try {
		        		  num = Integer.parseInt(message);
		        	  } catch (NumberFormatException nfe) {
		        		  num = -1;
		        	  }
		        	  message = in.readLine();
		        	  messages.append(" -- " + message);
		        	  if (num >=0) {
			        	  if (message.equals("hit")) p.setOpponentState(State.HIT, num);
			        	  if (message.equals("sunk")) p.setOpponentState(State.SUNK, num);
			        	  if (message.equals("miss")) p.setOpponentState(State.MISS, num);
		        	  }
		          }
//		          repaint(true);
		          
	        }
	        while( !message.equalsIgnoreCase( "GoodBye" ) );

	        // Send the "GoodBye" message back to stop the other thread.
	        out.println( "GoodBye" );

	        
	      }
	      catch ( UnknownHostException e )
	      {
	        System.err.println( "Couldn't connect to: " + ip );
	        e.printStackTrace();
	        System.exit( 1 );
	      }
	      catch( IOException e )
	      {
	        System.err.println( "Couldn't get I/O for the connection to: " + ip );
	        e.printStackTrace();
	        System.exit( 1 );
	      }
	    }

	@Override
	public void repaint(boolean enabled) {
		myPanelHolder.removeAll();
		try {
			myPanel = new MyPanel(myUsername, sender, messageArea, p.getMyBoard(), p.getOppBoard(),enabled);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myPanelHolder.add(myPanel);
		myPanelHolder.revalidate();
		
	}
		
	}
  
  
  public String stateToString(State s)
  {
	  if (s == State.MISS) return "miss";
	  if (s == State.HIT)  return "hit";
	  if (s == State.SUNK) return "sunk";
	  return "";
		    
  }
  
  private class ChatWindowListener implements WindowListener
  {
    @Override
    public void windowClosing( WindowEvent arg0 )
    {
      // Send the "GoodBye" message to stop the other thread.
      if( sender != null )  sender.sendMessage( "GoodBye" );
      // Wait for the listener thread (which may be either a server or client) to stop,
      // which happens when the "GoodBye" message is sent and returned.
      frame.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
      while( listener.isAlive() )
      {
        try { Thread.sleep( 200 ); } catch( InterruptedException e ) { /* Ignore interrupt. */ }
      }
      System.exit( 0 );
    }

    // These methods are not used.
    @Override  public void windowActivated( WindowEvent arg0 )  {  }
    @Override  public void windowClosed( WindowEvent arg0 )  {  }
    @Override  public void windowDeactivated( WindowEvent arg0 )  {  }
    @Override  public void windowDeiconified( WindowEvent arg0 )  {  }
    @Override  public void windowIconified( WindowEvent arg0 )  {  }
    @Override  public void windowOpened( WindowEvent arg0 )  {  }
  }

}
