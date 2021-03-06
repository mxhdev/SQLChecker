package sqlchecker.core;

import sqlchecker.io.IOUtil;
import sqlchecker.io.impl.SubmissionReader;

import java.util.ArrayList;
import java.util.Collections;

public final class PlagiatTest {
	
	private ArrayList<String> matrikelnummer;
	private ArrayList<String> name;
	private ArrayList<String> compareMatrikelnummer;
	private ArrayList<String> compareName;
	private float simMax = -2;
	private ArrayList<String[]> comments = new ArrayList<String[]>();
	private ArrayList<String[]> similarities = new ArrayList<String[]>();
	private String filePath = "";
	private String compareFilePath = "";
	
	
	/**
	 * @param matnummer
	 * @param nam
	 * @param exer
	 * @param fiPa
	 */
	PlagiatTest (ArrayList<String> matnummer, ArrayList<String> nam, ArrayList<String[]> exer, String fiPa) {
		this.matrikelnummer = matnummer;
		this.name = nam;
		this.comments = exer;
		this.filePath = fiPa;
		
	}
	
	/**
	 * @param matnummer
	 * @param compareNum
	 * @param nam
	 * @param compName
	 * @param exSim
	 * @param sim
	 * @param fiPa
	 * @param coFiPa
	 */
	PlagiatTest (ArrayList<String> matnummer, ArrayList<String> compareNum, ArrayList<String> nam, ArrayList<String> compName, ArrayList<String[]> exSim, float sim, String fiPa, String coFiPa){
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
	
	/**
	 * Gets the list of PlagiatTest objects for all submissions and calculate each
	 * similarity for the exercises of the submission i and i+1.  
	 * @param com List of PlagiatTest objects
	 * @param SolutionExercises List of exercises (defined in the raw.sql file)
	 * @param staticEnabled Boolean if the static tags are enabled in student submissions
	 * @return ArrayList<String> ordered by maximum similarity score. The list contain
	 * the filePath of each submission, the maximum similarity and the similarity of 
	 * each exercise.
	 */
	public static ArrayList<String> generatePlagiatList(ArrayList<PlagiatTest> com, ArrayList<String> SolutionExercises, boolean staticEnabled){
		
		ArrayList<PlagiatTest> resultList = new ArrayList<PlagiatTest>();
		String [] compareComment = new String[3];
		String empty = "";
		String authors = "authors";
		// All submissions
		for(int i = 0; i < com.size(); i++){
			//Compare with submission i+1
			for(int j = i + 1; j < com.size(); j++){
				//Compare each exercises
				ArrayList<Float> simExer = new ArrayList<Float>();
				ArrayList<String[]> orderExercise = new ArrayList<String[]>();
				for(int k = 0; k < com.get(i).comments.size(); k++){
					// get comment from submission i and exercise k
					String[] comment = com.get(i).comments.get(k);
					// search for exercise k in submission i+1
					int l = 0;
					float sim = -1;
					for(String[] commentCompare: com.get(j).comments){
						if(comment[0].equals(commentCompare[0]) && !(comment[0].equals(empty)) && !(comment[0].equals(authors))){
							compareComment = com.get(j).comments.get(l);
							//compare similarity of comments
							sim = CalculateSimilarity.similarityStringsCosine(comment[2], compareComment[2]);
							simExer.add(sim);
						}else{
							l++;
						}
					}
					if(!(comment[0].equals(empty)) && !(comment[0].equals(authors))){
						// float to string
						String simInString = String.format("%s",sim);
						String[] a = new String[]{comment[0],simInString};
						orderExercise.add(a);
					}
				}
				Float simMax = 0f;
				if (!simExer.isEmpty())
					simMax = Collections.max(simExer);
				
				PlagiatTest result = new PlagiatTest(com.get(i).getMatrikelnummer(), com.get(j).getMatrikelnummer(), com.get(i).getName(), com.get(j).getName(), orderExercise, simMax, com.get(i).getFilePath(), com.get(j).getFilePath());
				// add to list
				resultList.add(result);
			}
		}
		// Sort list descending on simMax
		Collections.sort(resultList, Collections.reverseOrder(new SortSimilarity()));
		
		ArrayList<String> resultListStringArray = new ArrayList<String>();
		
		// Generate report header
		String plagiatHeader = "Submission 1" +IOUtil.CSV_DELIMITER+ "Submission 2" +IOUtil.CSV_DELIMITER+ "MaxSimilarity" +IOUtil.CSV_DELIMITER;
		if(staticEnabled){
			plagiatHeader = plagiatHeader + "static" + IOUtil.CSV_DELIMITER;
		}
		// Add exercise(s) to header
		for(int i = 0; i < SolutionExercises.size(); i++){
			plagiatHeader = plagiatHeader + SolutionExercises.get(i).toString() + IOUtil.CSV_DELIMITER;
		}
		
		resultListStringArray.add(plagiatHeader);
		
		// add body of the report
		String body = "";
		String noComment = "-1.0";
		for(PlagiatTest l: resultList){
			String valueMax = "";
			if(l.simMax == -1){
				valueMax = "No Comment found for "+ l.filePath;
			}else{
				valueMax = String.valueOf(l.simMax);
			}
			body = l.filePath + IOUtil.CSV_DELIMITER + l.compareFilePath + IOUtil.CSV_DELIMITER + valueMax + IOUtil.CSV_DELIMITER;
			//search for similarity of static comments, if static is enabled
			String value = "";
			String stati = "static";
			// only if static tags in submissions are enabled
			if(staticEnabled){
				for(int i = 0; i < l.similarities.size(); i++){
					String[] exerciseArray = l.similarities.get(i);
					value = "";
					if(exerciseArray[0].equals(stati)){
						if(exerciseArray[1].equals(noComment)){
							value = "No Comment found for "+ l.filePath;
						}else{
							value = exerciseArray[1];
						}			
					}
				}
				body = body + value + IOUtil.CSV_DELIMITER;
			}
			// Search for all other tags and print them in the same order like the header.
			int quantityExercises = SolutionExercises.size();
			if(staticEnabled){
				quantityExercises = quantityExercises + 1;
			}
			if(quantityExercises < l.similarities.size()){
				System.out.println("Warning there are more tags in the submisson than in the solution: " +l.filePath);
			}else{
				for(int i = 0; i < SolutionExercises.size(); i++){
					value = "";
					for(int j = 0; j < l.similarities.size(); j++){
						String[] exerciseArray = l.similarities.get(j);
						if(SolutionExercises.get(i).toString().equals(exerciseArray[0])){
							if(exerciseArray[1].equals(noComment)){
								value = "No Comment found for "+ l.filePath;
							}else{
								value = exerciseArray[1];
							}	
						}
					}
					// add values to body
					body = body + value + IOUtil.CSV_DELIMITER;
				}
			}
			// add body of a submission to list
			resultListStringArray.add(body);
		}
		return resultListStringArray;
	}
	
	/**
	 * @param com
	 * @param SolutionExercises
	 * @param staticEnabled
	 * @return ArrayList<String> 
	 * Generates a report containing the comments of the students regarding the exercises. 
	 */
	public static ArrayList<String> generateCommentReport (ArrayList<PlagiatTest> com, ArrayList<String> SolutionExercises, boolean staticEnabled) {
		
		ArrayList<String> commentReport = new ArrayList<String>();
		String header = "";
		String body = "";
		
		// Header of the report
		header = "Name" + IOUtil.CSV_DELIMITER + "Matrikelnummer" + IOUtil.CSV_DELIMITER + "Submission" + IOUtil.CSV_DELIMITER;
		
		// if true add column static
		if(staticEnabled){
			header = header + "static" + IOUtil.CSV_DELIMITER;
		}
		
		//add every given exercise to the header
		for(int i = 0; i < SolutionExercises.size(); i++){
			header = header + SolutionExercises.get(i).toString() + IOUtil.CSV_DELIMITER;
		}
		// add header to ArrayList<String>
		commentReport.add(header);
		
		//for every submission in list
		for(PlagiatTest p: com){
			String studentIDs = "";
			String names = "";
			
			// Get Name(s)
			for(int n = 0; n < p.getName().size(); n++){
				if(n > 0){
					names = names + ", ";
				}
				names = names + p.getName().get(n).toString();
			}
			
			// Get student id(s)
			for(int n = 0; n < p.getMatrikelnummer().size(); n++){
				if(n > 0){
					studentIDs = studentIDs + ", ";
				}
				studentIDs = studentIDs + p.getMatrikelnummer().get(n).toString();
			}
			
			// Add submission metadata to body
			body = "";
			body = body + names + IOUtil.CSV_DELIMITER + studentIDs + IOUtil.CSV_DELIMITER + p.getFilePath() + IOUtil.CSV_DELIMITER;
			
			//For every exercise in solution fille
			for(int i = 0; i < SolutionExercises.size(); i++){
				String exerciseNameList = SolutionExercises.get(i).toString();
				
				//search for comments in submission regarding to current exercise
				for(int j = 0; j < p.comments.size(); j++){
					String[] exerciseComment = p.comments.get(j);
					if(exerciseNameList.equals(exerciseComment[0])){
						// get comment
						String comment = exerciseComment[2];
						// replace all ; and linebrakes 
						comment = comment.replaceAll("(;|\n)", " ");
						// add comment to body of the report
						body = body + comment + IOUtil.CSV_DELIMITER;
					}
				}
			}
			
			commentReport.add(body);
		}
		return commentReport;
	}
	
	/**
	 * Gets the List of submissions and exercises transform each submission to
	 * a PlagiatTest object by getting the name, the studentID, and the solution
	 * array of the exercises.
	 * Call Function generatePlagiatList with the list of PlagiatTest objects and the 
	 * exercise list.
	 * @param subs ArrayList of SubmissionReader
	 * @param exer ArrayList<String> of exercises
	 * @param staticEnabled
	 * @return ArrayList<String> 
	 */
	public static ArrayList<ArrayList<String>> extractComments (ArrayList<SubmissionReader> subs, ArrayList<String> exer, boolean staticEnabled){
		
		ArrayList<String[]> exercises = new ArrayList<String[]>();
		ArrayList<String[]> staticSQLs = new ArrayList<String[]>();
		ArrayList<PlagiatTest> subsExtracted = new ArrayList<PlagiatTest>();
		for(int i = 0; i < subs.size(); i++){
			// get submission from list
			exercises = subs.get(i).getMapping();			
			
			// get information from submission
			ArrayList<String> name = subs.get(i).getName();
			ArrayList<String> matrikelnummer = subs.get(i).getMatrikelnummer();
			String filePath = subs.get(i).getFilePath();
			
			// if true get all comments from static tags
			if(staticEnabled){
				staticSQLs = subs.get(i).getStaticMapping();
				String staticComments = "";
				for(int j = 0; j < staticSQLs.size(); j++){
					staticComments = staticComments + staticSQLs.get(j)[2];
				}
				String[] staticTag = new String[]{"static","",staticComments};
				exercises.add(exercises.size(),staticTag);
			}
			
			// Generate object with given information
			PlagiatTest sub = new PlagiatTest(matrikelnummer, name, exercises, filePath);
			
			// Add submission to list
			subsExtracted.add(sub);
		}
		ArrayList<ArrayList<String>> reports = new ArrayList<ArrayList<String>>();
		
		// generate reports from list
		reports.add(generateCommentReport(subsExtracted, exer, staticEnabled));
		reports.add(generatePlagiatList(subsExtracted, exer, staticEnabled));
		
		return reports;
	}
	
	/**
	 * Only used for testing purpose
	 * @param args
	 */
	public static void main(String[] args) {
		
		String eins = "Das ist ein Teststring.";
		String zwei = "Das ist ein";
		
		System.out.println("String: " + eins);
		System.out.println("String: " + zwei + "\n");
		
		System.out.println("Aehnlichkeit Cosin:" +CalculateSimilarity.similarityStringsCosine(eins, zwei) );
		System.out.println("Aehnlichkeit Levenshtein:" +CalculateSimilarity.similarityStringsLevenshstein(eins, zwei) );
		
		System.out.println("Aehnlichkeit Cosin:" +CalculateSimilarity.similarityStringsCosine(zwei, eins) );
		System.out.println("Aehnlichkeit Levenshtein:" +CalculateSimilarity.similarityStringsLevenshstein(zwei, eins) );
	}

}
