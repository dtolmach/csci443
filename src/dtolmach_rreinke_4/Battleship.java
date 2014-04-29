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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class Battleship 
{
  // Somewhat arbitrary, but see the section titled Understanding Ports:
  // http://docs.oracle.com/javase/tutorial/networking/overview/networking.html
  private static final int PORT = 51042;
  
  private GameServer server;
  private GameClient client;
  private MessageSender sender;
  private Thread listener;

  private JFrame frame;
  private JTextArea messageArea;
  private static String myUsername;

  private String theirUsername;

  public Battleship() throws IOException
  {
    // Add a random 3-digit number for debugging when both server and client are on the same machine...
    this.myUsername = System.getenv( "USERNAME" ) + ( (int)( Math.random() * 900 + 100 ) );

    GridLayout layout = new GridLayout(2, 0);
	layout.setHgap(10);
	this.frame = new JFrame("Welcome to Battleship!");
	this.frame.setSize(800, 600);
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
	
	MyPanel myPanel = new MyPanel(myUsername, this);
	frame.add(myPanel);    
    
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
          server = new GameServer( messageArea );
          sender = server;
          listener = new Thread( server );
          listener.start();
          break;
        case "Join Game..." :
          //JOptionPane.showMessageDialog( frame, "Start a chat server/client here...", ae.getActionCommand(), JOptionPane.INFORMATION_MESSAGE );
          client = new GameClient( messageArea );
          sender = client;
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
  
  public static void setCellClicked(String message, Battleship b){
	  System.out.println("Name of clicked cell is: " + message);
	  b.setMsgArea(message);
	  
	  // this.sender is set in the actionPerfermed method when a server or client is created.
	  // Since this.sender is an instance of either SimpleChatServer or SimpleChatClient,
	  // and both of those classes implement MessageSender, we're sure this.sender can do this.
	  b.sender.sendMessage(message);
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
  private interface MessageSender
  {
    public void sendMessage( String message );
  }

  /*
   * Adapted from http://docs.oracle.com/javase/tutorial/networking/sockets/index.html
   */
  private class GameServer implements Runnable, MessageSender
  {
	private JTextArea messages;
    private PrintWriter output;
    private boolean myMove;
//    Player p;

    public GameServer( JTextArea messages )
    {
    	this.messages = messages;
    	myMove = true;
//    	p = new Player();
    }

    public void sendMessage( String message )
    {
      this.output.println( message );
    }

    @Override
    public void run()
    {
      try
      {
    	InetAddress.getLocalHost().getHostAddress();
        messages.append( "Connect to " + InetAddress.getLocalHost().getHostAddress() + " ...\n" );
      }
      catch( UnknownHostException e )
      {
        e.printStackTrace();
        System.exit( 1 );
      }

      try( ServerSocket serverSocket = new ServerSocket( PORT );
           Socket clientSocket = serverSocket.accept();  // Blocks until a connection is made.
    	   
//    	   if (myMove == true) {
//    		   //make a move
//    	   } else {
//    		   //accept the move
//    	   }
    	   
    	   
           BufferedReader in = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
           PrintWriter out = new PrintWriter( clientSocket.getOutputStream(), true ); )
      {
        // Save a reference to the output connection for use by sendMessage method.
        this.output = out;

        // Exchange user names.
        theirUsername = in.readLine();
        out.println( myUsername );
        messages.append( "Connected to " + theirUsername + ".\n" );

        // Enable the message JTextField so it looks like things are ready to go.
//        messageField.setEnabled( true );

        // Sending messages will happen on the GUI thread by calling the sendMessage method.
        // Thus, this thread only needs to listen for messages and display them.
        String message;
        do
        {
          message = in.readLine();  // Blocks until a message is received.
          messages.append( theirUsername + ": " + message + "\n" );
        }
        while( !message.equalsIgnoreCase( "GoodBye" ) );
        
        // Send the "GoodBye" message back to stop the other thread.
        out.println( "GoodBye" );

        // Disable the message JTextField so it looks like things are done.
//        messageField.setEnabled( false );
      }
      catch( IOException e )
      {
        e.printStackTrace();
        System.exit( 1 );
      }
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
//    private Player p;

    public GameClient( JTextArea messages )
    {
    	this.messages = messages;
    	myMove = false;
//    	p = new Player();
    }
    
    public void sendMessage( String message )
    {
      this.output.println( message );
    }

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
        out.println( myUsername );
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
          messages.append( theirUsername + ": " + message + "\n" );
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
