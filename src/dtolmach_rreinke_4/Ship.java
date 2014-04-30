package dtolmach_rreinke_4;

import java.util.ArrayList;
import java.util.List;

public class Ship {
	
	ArrayList<Integer> cells;
	
	public Ship()
	{
		cells = new ArrayList<Integer>();
	}
	
	public void addCell(int c)
	{
		cells.add(c);
	}
	
	public boolean containsCell(int c)
	{
		return cells.contains(c);
	}
	
}

