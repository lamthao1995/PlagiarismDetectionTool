package nlp1;

import java.util.Comparator;

public class SeedingPairComparator implements Comparator<SeedingPair> {

	@Override
	public int compare(SeedingPair o1, SeedingPair o2) {
		// TODO Auto-generated method stub
		if (o1.susp > o2.susp) return 1;
		else if (o1.susp == o2.susp) return 0;
		return -1;
	}

}
