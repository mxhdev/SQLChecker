package sqlchecker.core;

import sqlchecker.io.IOUtil;

public class ResultStorage {

	
	private String raw = "";
	
	/**
	 * right, wrong, ignored, exceptions
	 */
	private int[] counts = new int[4];
	
	private String csv = "";
	
	private String csvHeader = "";
	
	private String logEntry = "";
	
	private String fileName = "";
	
	public ResultStorage(String fname, String resultRaw) {
		this(fname, resultRaw, -1, -1, -1, -1);
	}
	
	public ResultStorage(String fname, String resultRaw, int right, int wrong, int ignored, int exceptions) {
		
		this.raw = resultRaw;
		this.fileName = fname;
		
		this.setCounts(right, wrong, ignored, exceptions);
		
		// create csv line for this record
		this.csv = generateCSVLine();
		// create header (has to be done AFTER generateCSVLine())
		this.csvHeader = generateCSVHeader();
		// create log entry (has to be done AFTER generateCSVHeader())
		this.logEntry = generateLogEntry();
	}
	
	
	public String getCSVLine() {
		return this.csv;
	}
	
	public String getCSVHeader() {
		return this.csvHeader;
	}
	
	public String getLogEntry() {
		return this.logEntry;
	}
	
	public String getRawText() {
		return this.raw;
	}
	
	public boolean isPassed() {
		// passed iff there are no wrong/ignored/exception entries!
		int problems = counts[1] + counts[2] + counts[3];
		return (problems == 0);
	}
	
	
	private void setCounts(int right, int wrong, int ignored, int exceptions) {
		counts[0] = right;
		counts[1] = wrong;
		counts[2] = ignored;
		counts[3] = exceptions;
	}
	
	
	
	private String generateCSVHeader() {
		String csvHead = "Submission" + IOUtil.CSV_DELIMITER
				+ "Right" + IOUtil.CSV_DELIMITER
				+ "Wrong" + IOUtil.CSV_DELIMITER
				+ "Ignored" + IOUtil.CSV_DELIMITER
				+ "Exceptions";
		
		// count amount of queries/statements
		int qnum = csv.split(IOUtil.CSV_DELIMITER).length - 5;
		for (int j = 0; j < qnum; j++) {
			csvHead += IOUtil.CSV_DELIMITER + "Query" + (j+1);
		}
		
		return csvHead; 
	}
	
	
	
	
	
	
	
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
