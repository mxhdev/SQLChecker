package sqlchecker.core;

import static org.simmetrics.StringMetricBuilder.with;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.metrics.Levenshtein;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

public class CalculateSimilarity {
	
	/**
	 * Calculate the similarity of two strings. At the moment Levenshtein 
	 * distance is used for that. The returned float value is limited to
	 * 0 all characters different and 1 all are the same. -1 is returned if
	 * stringA is empty. 
	 * @param stringA
	 * @param stringB
	 * @return The returned float value is limited to 0 all characters
	 * different and 1 all are the same. -1 is returned if stringA is empty.
	 */
	public static float similarityStringsLevenshstein(String stringA, String stringB) {

		if(stringA.equals("")){
			return -1;
		}else{
			StringMetric metric = with(new Levenshtein())
					.simplify(Simplifiers.removeNonWord())
					.simplify(Simplifiers.toLowerCase()).build();

			return metric.compare(stringA, stringB);			
		}
	}
	
	public static float similarityStringsCosine(String stringA, String stringB) {

		if(stringA.equals("")){
			return -1;
		}else{
			StringMetric metric = 
					with(new CosineSimilarity<String>())
					.simplify(Simplifiers.toLowerCase())
					.tokenize(Tokenizers.whitespace())
					.build();

			return metric.compare(stringA, stringB);	
		}
	}
	
	public static void main(String[] args) {
		
		String com1 = "SELECT bezeichnung, preis FROM produkte; \n"
						+"-- Dies ist ein Kommentar für s1 1a \n"
						+"Select dasd from asdasd; \n"
						+ "/* Kommentar zeile 1 !,; #*# \n"
						+"und hier Zeile 2 */";
		String com2 = "SELECT bezeichnung, preis FROM produkte; \n"
						+"-- Dies ist ein Kommentar für s1 1a \n"
						+"Select dasd from asdasd; \n"
						+"# Hier steht noch ein Kommentar\n"
						+"Select dasda from dasd where;"
						+ "/* Kommentar zeile 1 !,; #*# \n"
						+"und hier Zeile 2 */";
		
		System.out.println(com1);
		System.out.println(".............................");
		System.out.println(com2 +" \n");
		String scoreLevenshtein = String.valueOf(similarityStringsLevenshstein(com1, com2));
		String scoreCousine = String.valueOf(similarityStringsCosine(com1, com2));
		System.out.println("Similarity Score Levenshtein: " + scoreLevenshtein);
		System.out.println("Similarity Score Cousine: " + scoreCousine);
	}

}
