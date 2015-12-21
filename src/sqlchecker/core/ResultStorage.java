package sqlchecker.core;

import java.util.ArrayList;

import sqlchecker.io.IOUtil;


/**
 * Stores results as raw text and as csv
 * 
 * @author Max Hofmann
 *
 */
public class ResultStorage {

	/**
	 * Raw result text (html with annotations)
	 */
	private String raw = "";
	
	/**
	 * Stores the counts in the following order:
	 * Right, wrong, ignored, exceptions
	 */
	private int[] counts = new int[4];
	
	/**
	 * CSV line corresponding to the results of this submission
	 */
	private String csv = "";

	/**
	 * Log entry corresponding to the raw results
	 */
	private String logEntry = "";
	
	
	/**
	 * Path of the submission file corresponding to the results
	 * stored in this class
	 */
	private String filePath = "";
	
	
	/**
	 * Name(s) of the students who submitted the file
	 */
	private ArrayList<String> name = null;
	
	/**
	 * Student IDs of the students who submitted the file
	 */
	private ArrayList<String> matrikelnummer = null;
	
	private String csv_name = "";
	private String csv_matrikelnummer = "";
	
	/**
	 * Amount of static queries executed
	 */
	private int staticAmount = -1;
	
	/**
	 * Counts executed from the static queries (They were executed
	 * via dbfit)
	 */
	private int[] staticCounts = new int[4];
	
	/**
	 * Creates a ResultStorage object and sets the counts to -1. 
	 * It is highly recommended to call the setCounts() function 
	 * after using this constructor
	 * @param fname File name of the submission
	 * @param resultRaw The raw annotated html results for
	 * this submission
	 */
	public ResultStorage(String fname, String resultRaw) {
		this(fname, null, null, resultRaw, -1, -1, -1, -1);
	}
	
	
	/**
	 * Creates a ResultStorage object
	 * @param fpath Path of the submission
	 * @param resultRaw The raw annotated html results for
	 * this submission
	 * @param right Amount of correct values (can be taken from 
	 * the Fixture.counts field)
	 * @param wrong Amount of wrong values (can be taken from 
	 * the Fixture.counts field)
	 * @param ignored Amount of ignored values (can be taken from 
	 * the Fixture.counts field)
	 * @param exceptions Amount of error values (can be taken from 
	 * the Fixture.counts field)
	 */
	public ResultStorage(String fpath, ArrayList<String> sName, ArrayList<String> sMatrikelnummer, String resultRaw, int right, int wrong, int ignored, int exceptions) {
		
		this.raw = resultRaw;
		this.filePath = fpath;
		
		this.setCounts(right, wrong, ignored, exceptions);
		this.name = sName;
		this.matrikelnummer = sMatrikelnummer;
		
		updateOutput(true, true);
	}

	
	/*
	 * Updates the logEntry and csv line
	 */
	
	/**
	 * Updates the logEntry and/or the output CSV line b using
	 * the current values of the class variables
	 * @param updateCSV True iff the CSV line should be updated
	 * @param updateLogEntry True iff the log entry corresponding
	 * to this submission should be updated
	 */
	private void updateOutput(boolean updateCSV, boolean updateLogEntry) {
		// create csv line for this record
		if (updateCSV) {
			this.csv = generateCSVLine();
		}
		
		// create log entry (has to be done AFTER generateCSVHeader())
		if (updateLogEntry) {
			this.logEntry = generateLogEntry();
		}
	}
	
	
	/**
	 * @return CSV line corresponding to the results of this submission
	 */
	public String getCSVLine() {
		return this.csv;
	}
	
	/**
	 * @return Log entry corresponding to the raw results
	 */
	public String getLogEntry() {
		return this.logEntry;
	}
	
	/**
	 * @return The annotated raw text as html
	 */
	public String getRawText() {
		return this.raw;
	}
	
	
	public int[] getCounts() {
		return this.counts.clone();
	}
	
	/**
	 * Checks if the results indicate, that this submission is 
	 * completely correct
	 * @return True iff (WRONG + IGNORED + ERRORS == 0)
	 */
	public boolean isPassed() {
		// passed iff there are no wrong/ignored/exception entries!
		int problems = counts[1] + counts[2] + counts[3];
		return (problems == 0);
	}
	
	
	/**
	 * Updates the counts field stored in this object
	 * @param right Amount of correct values (can be taken from 
	 * the Fixture.counts field)
	 * @param wrong Amount of wrong values (can be taken from 
	 * the Fixture.counts field)
	 * @param ignored Amount of ignored values (can be taken from 
	 * the Fixture.counts field)
	 * @param exceptions Amount of error values (can be taken from 
	 * the Fixture.counts field)
	 */
	public void setCounts(int right, int wrong, int ignored, int exceptions) {
		counts[0] = right;
		counts[1] = wrong;
		counts[2] = ignored;
		counts[3] = exceptions;
	}

	/**
	 * Set the names of the student(s)
	 * @param name ArrayList<String>
	 */
	public void setName(ArrayList<String> name) {
		this.name = name;
	}

	/**
	 * Set the student ID(s)
	 * @param matrikelnummer ArrayList<String>
	 */
	public void setMatrikelnummer(ArrayList<String> matrikelnummer) {
		this.matrikelnummer = matrikelnummer;
	}

	/**
	 * This function is part of the initialization routine of this
	 * class and generates a CSV line corresponding to the
	 * results stored in this class
	 * @return CSV line corresponding to the results
	 */
	private String generateCSVLine() {
		csv_name = "";
		csv_matrikelnummer = "";
		
		if( name != null){
			for(int i = 0; i < name.size();i++){
				csv_name = csv_name + name.get(i).toString();
				if(i < (name.size() - 1)){
					csv_name = csv_name + IOUtil.VALUE_DELIMITER + " ";
				}
			}
		}
		if( matrikelnummer != null){
			for(int i = 0; i < matrikelnummer.size();i++){
				csv_matrikelnummer = csv_matrikelnummer + matrikelnummer.get(i);
				if(i < (matrikelnummer.size() - 1)){
					csv_matrikelnummer = csv_matrikelnummer + IOUtil.VALUE_DELIMITER + " ";
				}
			}
		}

		String staticCSV = this.staticAmount + IOUtil.CSV_DELIMITER;
		
		staticCSV += staticCounts[0] + IOUtil.CSV_DELIMITER
				+ staticCounts[1] + IOUtil.CSV_DELIMITER
				+ staticCounts[2] + IOUtil.CSV_DELIMITER
				+ staticCounts[3] + IOUtil.CSV_DELIMITER;

		String csvLine = csv_name + IOUtil.CSV_DELIMITER
				+ csv_matrikelnummer + IOUtil.CSV_DELIMITER
				+ filePath + IOUtil.CSV_DELIMITER
				+ staticCSV;
		
		
		
		String[] statements = raw.split("</table>"); // instead of <table>
		
		int start = 0;
		// skip connection and driver definition
		if (statements.length > 2) start = 2;

		String statusLine = IOUtil.CSV_DELIMITER;
		boolean expectsError = false;
		for (int i = start; i < statements.length; i++) {
			String tmp = statements[i];
			
			expectsError = tmp.contains("<!--error-->");

			// parse status
			if (tmp.contains("class=\"pass\"")) {
				statusLine += "p";
				// one less passed, one more error
				if (expectsError) {
					this.counts[0] = Math.max(this.counts[0] - 1, 0);
					this.counts[3]++;
				}
			}
			if (tmp.contains("class=\"ignore\"")) {
				statusLine += "i";
			}
			if (tmp.contains("class=\"fail\"")) {
				statusLine += "f";
			}
			if (tmp.contains("class=\"error\"")) {
				statusLine += "e";
				// one more passed, one less error
				if (expectsError) {
					this.counts[0]++;
					this.counts[3] = Math.max(this.counts[3] - 1, 0);
				}
			}
			
			
			// errorExpected, but query has no status annotation
			if (!((tmp.contains("class=\"pass\""))
					|| (tmp.contains("class=\"ignore\""))
					|| (tmp.contains("class=\"fail\""))
					|| (tmp.contains("class=\"error\""))) ) {
				// add an error count
				// because there are no annotations here, the is
				// no count-value to decrease
				if (expectsError) {
					this.counts[3]++;
				}
			}
			
			statusLine += IOUtil.CSV_DELIMITER;
		}
		
		// counts
		String countLine = counts[0] + IOUtil.CSV_DELIMITER
				+ counts[1] + IOUtil.CSV_DELIMITER
				+ counts[2] + IOUtil.CSV_DELIMITER
				+ counts[3];
		csvLine += countLine;
		
		// status labels
		csvLine += statusLine;
		
		
		
		return csvLine;
	}
	
	
	
	/**
	 * This function is part of the initialization routine of this
	 * class and generates a log entry corresponding to the raw
	 * results. This log entry shows all wrong statements and errors
	 * @return The log entry corresponding to the results
	 */
	private String generateLogEntry() {
		String logRaw = "";
		boolean errorExpected = false;
		
		logRaw += "\n\n-------------------- Detailed results for " 
				+ filePath + " --------------------\n";
		
		
		/*
		 * as a test, show the complete dbfit html (annotated) result
		 */
		logRaw += this.raw + "\n\n";
		
		if (!errorExpected) return logRaw;
		
		// - end of test code -
		
		// todo: submission number!
		if (isPassed()) {
			logRaw += "No problems, everything is correct!";
		} else {
			// there were some problems, split by statement
			String[] statements = raw.split("</table>");
			
			int start = 0;
			// skip first empty element, connection and driver definition
			if (statements.length > 2) start = 2;

			for (int i = start; i < statements.length; i++) {
				
				String tmp = statements[i];
				errorExpected = tmp.contains("<!--error-->");
				
				if (tmp.contains("class=\"ignore\"")
						|| tmp.contains("class=\"fail\"")
						|| tmp.contains("class=\"error\"")) {
					// something did not go as expected here!
					logRaw += "\n * Statement " + (i+1) + " [ERROR_EXPECTED=" + errorExpected + "] * \n" + tmp + "\n * * * * * * * * \n\n"; 
				}
			}
		}
		return logRaw;
	}


	/**
	 * For storing information about executing static queries of a student
	 * submission
	 * @param staticRs The result set which was created from executing
	 * the static queries of a student submission
	 * @param qamount Amount of static queries executed
	 */
	public void setStaticResults(ResultStorage staticRs, int qamount) {
		// store counts & amount
		this.staticCounts = staticRs.counts.clone();
		this.staticAmount = qamount;
		
		// append logEntry
		this.logEntry += "\n" + staticRs.generateLogEntry();
		
		// update CSV line corresponding to this submission
		updateOutput(true, false);
		
	}
	
	
	public static void main(String[] args) {
		String test = "<!--error-->\n<table>QUERY1</table>\n<!--error-->\n<table>QUERY222</table>\n<table>QUERY_333</table>";
		String splitter = "</table>";
		
		System.out.println(test + "\n- - - - - - - - - -");
		
		String[] tokens = test.split(splitter);
		
		for (int i = 0; i < tokens.length; i++) {
			System.out.println("< " + (i+1) + " >");
			System.out.println(tokens[i]);
		}
	}
	
	
	
}
