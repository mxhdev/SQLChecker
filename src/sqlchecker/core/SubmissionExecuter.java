package sqlchecker.core;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import dbfit.MySqlTest;
import fit.Parse;
import fit.exception.FitParseException;
import sqlchecker.io.IOUtil;
import sqlchecker.io.OutputWriter;
import sqlchecker.io.impl.SolutionReader;
import sqlchecker.io.impl.SubmissionReader;



/**
 * Executes the submissions which are stored in the given directory
 * and prints the result/status
 * 
 * @author Max Hofmann
 *
 */
public class SubmissionExecuter {

	/**
	 * (Relative) Path to the folder which stores all the submissions
	 * which should be checked
	 */
	private String submPath = "";
	
	/**
	 * (Relative) Path to the file which contains the solution
	 */
	private String solPath = "";
	
	/**
	 * Path for this assignment (e.g. data/assignment1/)
	 */
	private String agnPath = "";
	
	/**
	 * Creates a SubmissionExecuter class and stores the
	 * given parameters
	 * @param agnPath The (relative) path to the folder, which
	 * stores all the assignment-data
	 */
	public SubmissionExecuter(String assignmentPath) {
		this.agnPath = assignmentPath;
		this.submPath = assignmentPath + "/submissions/";
		this.solPath = assignmentPath + "/solution.txt";
	}
	
	
	/**
	 * Tests all the available submissions
	 */
	public void runCheck() {
		// get list of all submissions
		File subSrc = new File(submPath);
		File[] submissions = subSrc.listFiles();
		
		// all lines of the csv file
		ArrayList<String> csvLines = new ArrayList<String>();
		// log (contains errors for each submission)
		ArrayList<String> logContent = new ArrayList<String>();
		
		
		// show info
		System.out.println("Solution: \n\t" + solPath);
		System.out.println("\n" + submissions.length + " submissions found: ");
		for (File f : submissions) 
			System.out.println("\t" + f.getPath());
		
		// load tags & solution
		SolutionReader sr = new SolutionReader(solPath);
		sr.loadFile();
		String solution = sr.getHTML().toString();
		

		// add csv header
		csvLines.add(IOUtil.generateCSVHeader(sr.getTagMap()));
		
		
		// Define output writer
		//PrintWriter out = new PrintWriter(System.out, false);
		String[] connProps = sr.getConnectionProperties();
		
		for (int i = 0; i < submissions.length; i++) {
			
			File subm = submissions[i];
			String fname = subm.getName();
			
			System.out.println("\n\n[" + (i+1) + "/" + submissions.length + "] Testing: " + subm);
			
			// load a submission
			SubmissionReader subr = new SubmissionReader(subm.getPath(), IOUtil.tags);
			subr.loadFile();

			// get mapping and apply it
			ArrayList<String[]> mapping = subr.getMapping();
			String checkStr = IOUtil.applyMapping(solution, mapping);
			
			// perform the check
			ResultStorage rs = null;
			try {
				rs = runSubmission(fname, checkStr, connProps);
			} catch (SQLException sqle) {
				// unable to close connection
				sqle.printStackTrace();
			}
			
			if (rs == null) {
				// some sql exception occurred
				logContent.add("Error for file " + fname);
				csvLines.add(fname + IOUtil.CSV_DELIMITER + "?");
				continue;
			}
			
			// add csv line
			csvLines.add(rs.getCSVLine());
			// add log entry
			logContent.add(rs.getLogEntry());
		}
		
		/*
		 * write/show content
		 */
		System.out.println("\n\nWriting content to > CSV < file:\n");
		String summaryPath = this.agnPath + "summary.csv";
		summaryPath = OutputWriter.makeUnique(summaryPath);
		System.out.println("\t" + summaryPath + "\n");
		/*
		for (int i = 0; i < csvLines.size(); i++)  {
			System.out.println(csvLines.get(i));
		}*/
		try {
			OutputWriter summaryWriter = new OutputWriter(summaryPath, csvLines);
			summaryWriter.writeLines();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		
		System.out.println("\n\nWriting content to > LOG < file:\n");
		String logPath = this.agnPath + "mistakes.log";
		logPath = OutputWriter.makeUnique(logPath);
		System.out.println("\t" + logPath + "\n");
		/*
		for (int i = 0; i < logContent.size(); i++)  {
			System.out.println(logContent.get(i));
		}
		*/
		
		try {
			OutputWriter logWriter = new OutputWriter(logPath, logContent);
			logWriter.writeLines();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		/*
		 * perform plagiat check
		 * 
		 */
		
		//generate Testdate until the parsing of the comments isn't implemented
		
		PlagiatTest erste = new PlagiatTest(123456, "Dies ist ein Tést-String");
		PlagiatTest zweite = new PlagiatTest(123459, "dies ist ein                          test-string");
		PlagiatTest dritte = new PlagiatTest(123458, "Hier steht ein ganz anderer Kommentar");
		PlagiatTest vierte = new PlagiatTest(123460, "Hier steht ein gan anderer Kommentar");
		
		ArrayList<PlagiatTest> list = new ArrayList<PlagiatTest>();
		list.add(erste);
		list.add(zweite);
		list.add(dritte);
		list.add(vierte);
		//End of generating test data
		
		//calling generatePlagiatList function
		ArrayList<String> resLis = PlagiatTest.generatePlagiatList(list);
		// generate unique filename of plagiat reprt
		String fname = this.agnPath + "PlagiatReport.csv";
		fname = OutputWriter.makeUnique(fname);
		
		System.out.println("Filename of PlagiatReport: "+fname);
		// write report file 
		try {
			OutputWriter plagiatWriter = new OutputWriter(fname, resLis);
			plagiatWriter.writeLines();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	
	/**
	 * Runs a submission
	 * @param fileName Name of the file which was loaded
	 * @param sqlhtml HTML containing the submitted sql statements 
	 * @param connProps Connection properties in the following order:
	 *  (host, user, pw, dbname)
	 * @throws SQLException If the function was unable to close the sql connection
	 */
	private ResultStorage runSubmission(String fileName, String sqlhtml, String[] connProps) throws SQLException {
		
		MySqlTest tester = null;

		ResultStorage rs = null;
		try {
			// init connection
			tester = init(connProps[0], connProps[3], connProps[1], connProps[2]);
			
			// parse & execute the submission 
			Parse target = new Parse(sqlhtml);
			tester.doTables(target);
			
			
			System.out.println("\n* * * RESULTS * * *");
			
			// right, wrong, ignored, exception
			System.out.println("Counts:\n\t" + tester.counts);
			
			String result = IOUtil.getParseResult(target);
			
			rs = new ResultStorage(fileName, result
					, tester.counts.right, tester.counts.wrong
					, tester.counts.ignores, tester.counts.exceptions);

		} catch (FitParseException fpe) {
			fpe.printStackTrace();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			// close connection
			if (tester != null) tester.close();
		}
		
		return rs;
	}
	
	
	
	/**
	 * Creates a database connection for a MySQL endpoint
	 * @return MySqlTest instance
	 * @throws SQLException If no database connection could be created
	 * (This usually happens when the mysql service is not running)
	 */
	private MySqlTest init(String host, String db, String dbuser, String dbpw) throws SQLException {
		// init test
		
		System.out.println("Connection with values host=" + host + ", db=" + db + ", user=" + dbuser + ", pw=" + dbpw);
		
		MySqlTest tester = new MySqlTest();
		tester.connect(host, dbuser, dbpw, db);
		
		return tester;
	}
	
	
	
	/*
	 * HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
	 * 
	 * CURRENT MAIN CLASS
	 * 
	 * TODO
	 * - generated solutions.txt automated ?
	 * - javadoc
	 * HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
	 */
	
	public static void main(String[] args) {
		String submissionPath = "data/assignment1/submissions/";
		String solutionPath = "data/assignment1/solution.txt";
		String agnPath = "data/assignment1/";
		
		SubmissionExecuter se = new SubmissionExecuter(agnPath);
		se.runCheck();
	}

}
