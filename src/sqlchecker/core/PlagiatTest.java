package sqlchecker.core;

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.Levenshtein;
import org.simmetrics.simplifiers.Simplifiers;

import sqlchecker.io.IOUtil;
import sqlchecker.io.OutputWriter;
import sqlchecker.io.impl.SubmissionReader;

import static org.simmetrics.StringMetricBuilder.with;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PlagiatTest {
	
	private String matrikelnummer = "";
	private String name = "";
	private String compareMatrikelnummer = "";
	private String compareName = "";
	private float simMax = -1;
	private ArrayList<String[]> comments = new ArrayList<String[]>();
	private ArrayList<Float> similarities = new ArrayList<Float>();
	
	PlagiatTest (String matnummer, String nam, ArrayList<String[]> exer) {
		this.matrikelnummer = matnummer;
		this.comments = exer;
	}
	
	PlagiatTest (String matnummer, String compareNum, String nam, String compName, ArrayList<Float> exSim, float sim){
		this.matrikelnummer = matnummer;
		this.name = nam;
		this.compareMatrikelnummer = compareNum;
		this.compareName = compName;
		this.similarities = exSim;
		this.simMax = sim;
	}
	
	public String getMatrikelnummer() {
		return this.matrikelnummer;
	}

	public String getCompareMatrikelnummer() {
		return this.compareMatrikelnummer;
	}

	public float getSimilarity() {
		return this.simMax;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompareName() {
		return compareName;
	}

	public void setCompareName(String compareName) {
		this.compareName = compareName;
	}

	public static float similarityStrings(String stringA, String stringB) {

		StringMetric metric = with(new Levenshtein())
				.simplify(Simplifiers.removeDiacritics())
				.simplify(Simplifiers.removeNonWord())
				.simplify(Simplifiers.toLowerCase()).build();

		return metric.compare(stringA, stringB);
	}
	
	public static ArrayList<String> generatePlagiatList(ArrayList<PlagiatTest> com, ArrayList<String> exercises){
		
		ArrayList<PlagiatTest> resultList = new ArrayList<PlagiatTest>();
		ArrayList<Float> simExer = new ArrayList<Float>();
		// All submissions
		for(int i = 0; i < com.size(); i++){
			//Compare with submission i+1
			for(int j = i + 1; j < com.size(); j++){
				//Compare each exercises
				for(int k = 0; k < exercises.size(); k++){
					// get comment from submission i and exercise k
					String[] comment = com.get(i).comments.get(k);
					// get comment from submission i+1 and exercise k
					String[] compareComment = com.get(j).comments.get(k);
					//compare similarity of comments
					float sim = similarityStrings(comment[1], compareComment[1]);
					simExer.add(sim);
				}
				Float simMax = Collections.max(simExer);
				PlagiatTest result = new PlagiatTest(com.get(i).getMatrikelnummer(), com.get(j).getMatrikelnummer(), com.get(i).getName(), com.get(j).getName(), simExer, simMax);
				resultList.add(result);
			}
		}
		Collections.sort(resultList, Collections.reverseOrder(new SortSimilarity()));
		ArrayList<String> resultListStringArray = new ArrayList<String>();
		String plagiatHeader = "Student 1" +IOUtil.CSV_DELIMITER+ "Student 2" +IOUtil.CSV_DELIMITER+ "MaxSimilarity" +IOUtil.CSV_DELIMITER;
		for(int i = 0; i < exercises.size(); i++){
			plagiatHeader = plagiatHeader + exercises.get(i).toString() + IOUtil.CSV_DELIMITER;
		}
		resultListStringArray.add(plagiatHeader);
		String body = "";
		for(PlagiatTest l: resultList){
			body = l.matrikelnummer + IOUtil.CSV_DELIMITER + l.compareMatrikelnummer + IOUtil.CSV_DELIMITER + l.simMax + IOUtil.CSV_DELIMITER;
			for(int i = 0; i < simExer.size(); i++){
				body = body + simExer.get(i).toString() + IOUtil.CSV_DELIMITER;
			}
			resultListStringArray.add(body);
		}
		return resultListStringArray;
	}
	
	public static ArrayList<String> extractComments (ArrayList<SubmissionReader> subs, ArrayList<String> exer){
		
		ArrayList<String[]> exercise = new ArrayList<String[]>(); 
		ArrayList<PlagiatTest> subsExtracted = new ArrayList<PlagiatTest>();
		for(int i = 0; i < subs.size(); i++){
			exercise = subs.get(i).getMapping();
			for(int j = 0; j < exercise.size(); j++){
				String[] content = exercise.get(j);
				content[1] = returnComment(content[1]);
			}			
			PlagiatTest sub = new PlagiatTest(subs.get(i).getMatrikelnummer(), subs.get(i).getName(), exercise);
			subsExtracted.add(sub);
		}
		return generatePlagiatList(subsExtracted, exer);
	}
	
	public static String returnComment (String com){
		
		//to implement: remove all non comments
		return com;
	}
	
	public static void main(String[] args) {
		
		for(int x = 0; x < IOUtil.tags.length; x++){
			System.out.println(IOUtil.tags);
		}
		/*
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
		}*/
	}

}
