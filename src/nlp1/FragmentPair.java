package nlp1;

public class FragmentPair {
	public int srcBegin;
	public int srcEnd;
	public int suspBegin;
	public int suspEnd;
	public double sim;
	public FragmentPair(int a, int b, int c, int d, double e){
		this.srcBegin = a;
		this.srcEnd = b;
		this.suspBegin = c;
		this.suspEnd = d;
		this.sim = e;
	}
}
