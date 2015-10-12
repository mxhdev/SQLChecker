package sqlchecker.test;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.Levenshtein;
import org.simmetrics.simplifiers.Simplifiers;
import static org.simmetrics.StringMetricBuilder.with;

public final class plagiatTest {
	
	public static float similarityStrings(String stringA, String stringB) {

		StringMetric metric = with(new Levenshtein())
				.simplify(Simplifiers.removeDiacritics())
				.simplify(Simplifiers.removeNonWord())
				.simplify(Simplifiers.toLowerCase()).build();

		return metric.compare(stringA, stringB);
	}
	
	
	
	public static void main(String[] args) {
		
		String a = "Dies ist ein Tést-String";
		String b = "dies ist ein                          test-string";
		
		
		Float resultStrings = similarityStrings(a, b);
		System.out.println("Function similarityStrings:");
		System.out.println("Similarity of Strings: " +resultStrings);
	}

}
