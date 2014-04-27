package dtolmach_rreinke_4;

import java.util.ArrayList;
import java.util.List;

public class Ship {
	
	ArrayList<Cell> cells;
	
	public Ship(ArrayList<Cell> cells)
	{
		cells = this.cells;
	}
	
	public boolean containsCell(int l, int n)
	{
		for(Cell c : cells)
			if(c.equal(l,n))
				return true;
		return false;
	}
	
}

