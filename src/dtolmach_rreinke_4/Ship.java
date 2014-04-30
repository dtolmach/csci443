package dtolmach_rreinke_4;

import java.util.ArrayList;
import java.util.List;

public class Ship {
	
	ArrayList<Integer> cells;
	
	public Ship(ArrayList<Integer> cells)
	{
		this.cells = cells;
	}
	
	public boolean containsCell(int c)
	{
		return cells.contains(c);
	}
	
}

