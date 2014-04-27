package dtolmach_rreinke_4;

public class Cell{
	
	int l;
	int n;
	
	public Cell(int l, int n)
	{
		this.l = l;
		this.n = n;
	}

	public boolean equal(int l, int n){
		return (this.l == l && this.n == n);
	}
}