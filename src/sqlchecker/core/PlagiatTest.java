package sqlchecker.core;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.Levenshtein;
import org.simmetrics.simplifiers.Simplifiers;

import sqlchecker.io.IOUtil;
import sqlchecker.io.OutputWriter;

import static org.simmetrics.StringMetricBuilder.with;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PlagiatTest {
	
	private int matrikelnummer = -1;
	private String comments = "";
	private int compareMatrikelnummer = -1;
	private float similarity = -1;
	
	PlagiatTest (int matnummer, String com) {
		this.matrikelnummer = matnummer;
		this.comments = com;
	}
	
	PlagiatTest (int matnummer, int compareNum, float sim){
		this.matrikelnummer = matnummer;
		this.compareMatrikelnummer = compareNum;
		this.similarity = sim;
	}
	
	public int getMatrikelnummer() {
		return this.matrikelnummer;
	}

	public int getCompareMatrikelnummer() {
		return this.compareMatrikelnummer;
	}

	public float getSimilarity() {
		return this.similarity;
	}

	public static float similarityStrings(String stringA, String stringB) {

		StringMetric metric = with(new Levenshtein())
				.simplify(Simplifiers.removeDiacritics())
				.simplify(Simplifiers.removeNonWord())
				.simplify(Simplifiers.toLowerCase()).build();

		return metric.compare(stringA, stringB);
	}
	
	public static ArrayList<String> generatePlagiatList(ArrayList<PlagiatTest> com){
		
		ArrayList<PlagiatTest> resultList = new ArrayList<PlagiatTest>();
		for(int i = 0; i < com.size(); i++){
			for(int j = i + 1; j < com.size(); j++){
				String comment = com.get(i).comments;
				String compareComment = com.get(j).comments;
				float sim = similarityStrings(comment, compareComment);
				PlagiatTest result = new PlagiatTest(com.get(i).matrikelnummer, com.get(j).matrikelnummer, sim);
				resultList.add(result);
			}
		}
		Collections.sort(resultList, Collections.reverseOrder(new SortSimilarity()));
		ArrayList<String> resultListString = new ArrayList<String>();
		resultListString.add("Student 1" +IOUtil.CSV_DELIMITER+ "Student 2" +IOUtil.CSV_DELIMITER+ "Aehnlichkeit" +IOUtil.CSV_DELIMITER);
		for(PlagiatTest l: resultList){
			resultListString.add(l.matrikelnummer + IOUtil.CSV_DELIMITER + l.compareMatrikelnummer + IOUtil.CSV_DELIMITER + l.similarity + IOUtil.CSV_DELIMITER);
		}
		return resultListString;
	}
	
	public static void main(String[] args) {
		
		PlagiatTest erste = new PlagiatTest(123456, "Dies ist ein Tést-String");
		PlagiatTest zweite = new PlagiatTest(123459, "dies ist ein                          test-string");
		PlagiatTest dritte = new PlagiatTest(123458, "Hier steht ein ganz anderer Kommentar");
		PlagiatTest vierte = new PlagiatTest(123460, "Hier steht ein gan anderer Kommentar");
		
		ArrayList<PlagiatTest> list = new ArrayList<PlagiatTest>();
		list.add(erste);
		list.add(zweite);
		list.add(dritte);
		list.add(vierte);
	
		ArrayList<String> resLis = generatePlagiatList(list);
		for(String l: resLis){
			System.out.println(l);
		}
		String fname = "PlagiatReport.csv";
		fname = OutputWriter.makeUnique(fname);
		System.out.println(fname);
		try {
			OutputWriter plagiatWriter = new OutputWriter(fname, resLis);
			plagiatWriter.writeLines();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
