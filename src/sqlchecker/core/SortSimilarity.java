package sqlchecker.core;

import java.util.Comparator;

public class SortSimilarity implements Comparator<PlagiatTest>{
	@Override
	public int compare(PlagiatTest a1, PlagiatTest a2) {
		float sim1 = a1.getSimilarity();
		float sim2 = a2.getSimilarity();
		if (sim1 < sim2) return -1;
		if (sim1 > sim2) return 1;
		return 0;
	}	
}
