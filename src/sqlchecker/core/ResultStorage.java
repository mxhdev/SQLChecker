package sqlchecker.core;

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
	 * Name of the submission file corresponding to the results
	 * stored in this class
	 */
	private String fileName = "";
	
	
	/**
	 * Creates a ResultStorage object and sets the counts to -1. 
	 * It is highly recommended to call the setCounts() function 
	 * after using this constructor
	 * @param fname File name of the submission
	 * @param resultRaw The raw annotated html results for
	 * this submission
	 */
	public ResultStorage(String fname, String resultRaw) {
		this(fname, resultRaw, -1, -1, -1, -1);
	}
	
	
	/**
	 * Creates a ResultStorage object
	 * @param fname File name of the submission
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
	public ResultStorage(String fname, String resultRaw, int right, int wrong, int ignored, int exceptions) {
		
		this.raw = resultRaw;
		this.fileName = fname;
		
		this.setCounts(right, wrong, ignored, exceptions);
		
		// create csv line for this record
		this.csv = generateCSVLine();
		// create log entry (has to be done AFTER generateCSVHeader())
		this.logEntry = generateLogEntry();
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
	 * This function is part of the initialization routine of this
	 * class and generates a csv line corresponding to the
	 * results stored in this class
	 * @return CSV line corresponding to the results
	 */
	private String generateCSVLine() {
		String csvLine = fileName + IOUtil.CSV_DELIMITER
				+ counts[0] + IOUtil.CSV_DELIMITER
				+ counts[1] + IOUtil.CSV_DELIMITER
				+ counts[2] + IOUtil.CSV_DELIMITER
				+ counts[3];
		
		String[] statements = raw.split("<table>");
		
		int start = 0;
		// skip first empty elem, connection and driver definition
		if (statements.length > 3) start = 3;

		String status = IOUtil.CSV_DELIMITER;
		for (int i = start; i < statements.length; i++) {
			String tmp = statements[i];
			
			// parse status
			if (tmp.contains("class=\"pass\"")) {
				status += "p";
			}
			if (tmp.contains("class=\"ignore\"")) {
				status += "i";
			}
			if (tmp.contains("class=\"fail\"")) {
				status += "f";
			}
			if (tmp.contains("class=\"error\"")) {
				status += "e";
			}
			
			csvLine += status;
			status = IOUtil.CSV_DELIMITER;
		}
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

		logRaw += "\n\n-------------------- Detailed results for " 
				+ fileName + " --------------------\n";
		
		// todo: submission number!
		if (isPassed()) {
			logRaw += "No problems, everything is correct!";
		} else {
			// there were some problems, split by statement
			String[] statements = raw.split("<table>");
			
			int start = 0;
			// skip first empty element, connection and driver definition
			if (statements.length > 3) start = 3;

			for (int i = start; i < statements.length; i++) {
				String tmp = statements[i];
				if (tmp.contains("class=\"ignore\"")
						|| tmp.contains("class=\"fail\"")
						|| tmp.contains("class=\"error\"")) {
					// something did not go as expected here!
					logRaw += "\n * Statement " + (i+1) + " * \n" + tmp + "\n * * * * * * * * \n\n"; 
				}
			}
		}
		return logRaw;
	}
	
	
	
}
