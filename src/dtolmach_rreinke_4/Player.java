package dtolmach_rreinke_4;

import java.util.ArrayList;
import java.util.Arrays;

import dtolmach_rreinke_4.Battleship.State;

public class Player {
	
	private final static int N = 64; 
	private State myBoard[] = new State[N];
	private State opponent[] = new State[N];
	private ArrayList<Ship> myShips = new ArrayList<Ship>();
	private int mySunkShips;
	
	public Player()
	{
		Arrays.fill(myBoard, State.EMPTY);
		Arrays.fill(opponent, State.EMPTY);
		
		mySunkShips = 0;
		
//		ArrayList<Integer> cells = new ArrayList<Integer>();
//		cells.add(4);
//		myShips.add(new Ship(cells));
//		setBoard();
			
	}
	
	public void addShip(Ship s)
	{
		myShips.add(s);
		for(int c: s.cells) {
			myBoard[c] = State.SHIP;
		}
	}
	
	public void setBoard()
	{
		for(Ship s : myShips) {
			for(int c : s.cells) {
				myBoard[c] = State.SHIP;
			}
		}
	}
	
	public State getMyBoardState(int c)
	{
		return myBoard[c];
	}
	
	public State getOpponentState(int c)
	{
		return opponent[c];
	}
	
	public void setOpponentState(State s, int c)
	{
		opponent[c] = s;
	}
	
	public void validateOpponentMove(int c)
	{
		State s = myBoard[c];
		if (s == State.EMPTY){
			myBoard[c] = State.MISS;
		} else if (s == State.SHIP || s == State.HIT){
			checkHit(c);
		}
	}
	
	public void checkHit(int cell)
	{
		myBoard[cell] = State.HIT;
		for (Ship s : myShips) {
			if (s.containsCell(cell)) {
				for (int c: s.cells) {
					if(myBoard[c] != State.HIT)
						return;
				}
				sinkShip(s);
				return;
			}
		}
	}
	
	public void sinkShip(Ship s)
	{
		for (int c: s.cells) 
			myBoard[c] = State.SUNK;
		mySunkShips++;
	}
	
	public int getMySunkShips()
	{
		return mySunkShips;
	}
	
	public State[] getMyBoard()
	{
		return myBoard;
	}
	
	public State[] getOppBoard()
	{
		return opponent;
	}
	
	public static void main( String args[] )
	{
		Player p = new Player();
		State s = p.getMyBoardState(3);
		State q = p.getMyBoardState(4);
		p.validateOpponentMove(3);
		p.validateOpponentMove(4);
		
	}

}
