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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlagiatTest {
	
	private ArrayList<String> matrikelnummer;
	private ArrayList<String> name;
	private ArrayList<String> compareMatrikelnummer;
	private ArrayList<String> compareName;
	private float simMax = -2;
	private ArrayList<String[]> comments = new ArrayList<String[]>();
	private ArrayList<Float> similarities = new ArrayList<Float>();
	private String filePath = "";
	private String compareFilePath = "";
	
	PlagiatTest (ArrayList<String> matnummer, ArrayList<String> nam, ArrayList<String[]> exer, String fiPa) {
		this.matrikelnummer = matnummer;
		this.name = nam;
		this.comments = exer;
		this.filePath = fiPa;
	}
	
	PlagiatTest (ArrayList<String> matnummer, ArrayList<String> compareNum, ArrayList<String> nam, ArrayList<String> compName, ArrayList<Float> exSim, float sim, String fiPa, String coFiPa){
		this.matrikelnummer = matnummer;
		this.name = nam;
		this.compareMatrikelnummer = compareNum;
		this.compareName = compName;
		this.similarities = exSim;
		this.simMax = sim;
		this.filePath = fiPa;
		this.compareFilePath = coFiPa;
	}
	
	public ArrayList<String> getMatrikelnummer() {
		return this.matrikelnummer;
	}

	public ArrayList<String> getCompareMatrikelnummer() {
		return this.compareMatrikelnummer;
	}

	public float getSimilarity() {
		return this.simMax;
	}
	
	public ArrayList<String> getName() {
		return this.name;
	}

	public ArrayList<String> getCompareName() {
		return this.compareName;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public String getCompareFilePath() {
		return compareFilePath;
	}

	public static float similarityStrings(String stringA, String stringB) {

		if(stringA.equals("")){
			return -1;
		}else{
			StringMetric metric = with(new Levenshtein())
					.simplify(Simplifiers.removeDiacritics())
					.simplify(Simplifiers.removeNonWord())
					.simplify(Simplifiers.toLowerCase()).build();

			return metric.compare(stringA, stringB);			
		}
	}
	
	public static ArrayList<String> generatePlagiatList(ArrayList<PlagiatTest> com, ArrayList<String> SolutionExercises){
		
		ArrayList<PlagiatTest> resultList = new ArrayList<PlagiatTest>();
		String [] compareComment = new String[2]; 
		// All submissions
		for(int i = 0; i < com.size(); i++){
			//Compare with submission i+1
			for(int j = i + 1; j < com.size(); j++){
				//Compare each exercises
				ArrayList<Float> simExer = new ArrayList<Float>();
				for(int k = 0; k < SolutionExercises.size(); k++){
					// get comment from submission i and exercise k
					String[] comment = com.get(i).comments.get(k);
					// search for exercise k in submission i+1
					int l = 0;
					for(String[] commentCompare: com.get(j).comments){
						if(comment[0].equals(commentCompare[0])){
							compareComment = com.get(j).comments.get(l);
						}else{
							l++;
						}
					}
					//compare similarity of comments
					float sim = similarityStrings(comment[1], compareComment[1]);
					simExer.add(sim);
				}
				Float simMax = Collections.max(simExer);
				PlagiatTest result = new PlagiatTest(com.get(i).getMatrikelnummer(), com.get(j).getMatrikelnummer(), com.get(i).getName(), com.get(j).getName(), simExer, simMax, com.get(i).getFilePath(), com.get(j).getFilePath());
				resultList.add(result);
			}
		}
		Collections.sort(resultList, Collections.reverseOrder(new SortSimilarity()));
		ArrayList<String> resultListStringArray = new ArrayList<String>();
		String plagiatHeader = "Submission 1" +IOUtil.CSV_DELIMITER+ "Submission 2" +IOUtil.CSV_DELIMITER+ "MaxSimilarity" +IOUtil.CSV_DELIMITER;
		for(int i = 0; i < SolutionExercises.size(); i++){
			plagiatHeader = plagiatHeader + SolutionExercises.get(i).toString() + IOUtil.CSV_DELIMITER;
		}
		resultListStringArray.add(plagiatHeader);
		String body = "";
		for(PlagiatTest l: resultList){
			body = l.filePath + IOUtil.CSV_DELIMITER + l.compareFilePath + IOUtil.CSV_DELIMITER + l.simMax + IOUtil.CSV_DELIMITER;
			for(int i = 0; i < l.similarities.size(); i++){
				String value = "";
				if(l.similarities.get(i) == -1){
					value = "no Comment found for "+ l.filePath;
				}else{
					value = l.similarities.get(i).toString();
				}				
				body = body + value + IOUtil.CSV_DELIMITER;
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
				//extract only the comments and filter the SQL-statement
				content[1] = returnComment(content[1]);
			}			
			ArrayList<String> name = subs.get(i).getName();
			ArrayList<String> matrikelnummer = subs.get(i).getMatrikelnummer();
			String filePath = subs.get(i).getFilePath();
			PlagiatTest sub = new PlagiatTest(matrikelnummer, name, exercise, filePath);
			subsExtracted.add(sub);
		}
		return generatePlagiatList(subsExtracted, exer);
	}
	
	public static String returnComment (String com){
		
		String original = com;
		String cleaned = "";
		//Find comments in String
		Pattern p = Pattern.compile("(?m)(?:#|--).*|(/\\*[\\w\\W]*?(?=\\*/)\\*/)");
		Matcher m = p.matcher(original);
		
		while (m.find()) {
			cleaned = cleaned + m.group();
		}
		
		//Delete comment signs: '#', '--', '/*' and '*/'
		cleaned = cleaned.replaceAll("(#|--|/\\*|\\*/)", "");
		return cleaned;
	}
	
	public static void main(String[] args) {
		
		String mitKom = "SELECT bezeichnung, preis FROM produkte; \n"
						+"-- Dies ist ein Kommentar für s1 1a \n"
						+"Select dasd from asdasd; \n"
						+"# Hier steht noch ein Kommentar\n"
						+"Select dasda from dasd where;"
						+ "/* Kommentar zeile 1 !,; #*# \n"
						+"und hier Zeile 2 */";
		
		System.out.println(mitKom);
		System.out.println("Magic:.................");
		System.out.println(returnComment(mitKom));
	}

}
