package dtolmach_rreinke_4;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {
	
	public enum State {
	    MISS, HIT, SUNK, SHIP, EMPTY 
	}
	
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
		
		ArrayList<Integer> cells = new ArrayList<Integer>();
		cells.add(new Integer(4));
		myShips.add(new Ship(cells));
		setBoard();
			
	}
	
	public void addShip(Ship s)
	{
		myShips.add(s);
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
	
	public void validateOpponentMove(int c)
	{
		switch(myBoard[c]){
			case EMPTY : myBoard[c] = State.MISS;
			case SHIP  : checkHit(c);
			default : break;
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
	

}
