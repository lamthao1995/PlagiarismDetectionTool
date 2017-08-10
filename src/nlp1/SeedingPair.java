package nlp1;

import java.util.Comparator;

public class SeedingPair implements Comparable<SeedingPair>{
	public int src;
	public int susp;
	public int category;
	public SeedingPair(int U, int V, int C){
		this.src = U;
		this.susp = V;
		this.category = C;
	}
	@Override
	public int compareTo(SeedingPair arg0) {
		// TODO Auto-generated method stub
		if (this.susp > arg0.susp) return 1;
		else if (this.susp == arg0.susp) return 0;
		return -1;
	}
}
