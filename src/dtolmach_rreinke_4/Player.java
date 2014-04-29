package dtolmach_rreinke_4;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {
	
	public enum State {
	    MISS, HIT, SUNK, SHIP, EMPTY 
	}
	
	private final static int N = 10; 
	private State myBoard[][] = new State[N][N];
	private State opponent[][] = new State[N][N];
	private ArrayList<Ship> myShips = new ArrayList<Ship>();
	private int mySunkShips;
	
	public Player()
	{
		for(int i=0; i<N; i++) {
			Arrays.fill(myBoard, State.EMPTY);
			Arrays.fill(opponent, State.EMPTY);
		}
		mySunkShips = 0;
			
	}
	
	public void setBoard(boolean board[][])
	{
		for(int i=0; i<N; i++) {
			for(int j=0; j<N; j++) {
				if(board[i][j] == true)
					myBoard[i][j] = State.SHIP;
			}
		}
	}
	
	public State getMyBoardState(int l, int n)
	{
		return myBoard[l][n];
	}
	
	public State getOpponentState(int l, int n)
	{
		return opponent[l][n];
	}
	
	public void validateOpponentMove(int l, int n)
	{
		switch(myBoard[l][n]){
			case EMPTY : myBoard[l][n] = State.MISS;
			case SHIP  : checkHit(l, n);
			default : break;
		}
	}
	
	public void checkHit(int l, int n)
	{
		myBoard[l][n] = State.HIT;
		for (Ship s : myShips) {
			if (s.containsCell(l,n)) {
				for (Cell c: s.cells) {
					if(myBoard[c.l][c.n] != State.HIT)
						return;
				}
				sinkShip(s);
				return;
			}
		}
	}
	
	public void sinkShip(Ship s)
	{
		for (Cell c: s.cells) 
			myBoard[c.l][c.n] = State.SUNK;
		mySunkShips++;
	}
	
	

}
