package sqlchecker.core;

import java.io.File;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import dbfit.MySqlTest;
import fit.Parse;
import fit.RowFixture;
import fit.exception.FitParseException;
import sqlchecker.io.IOUtil;
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
	 * Creates a SubmissionExecuter class and stores the
	 * given parameters
	 * @param submissionPath The (relative) path to the folder, which
	 * stores the student submissions
	 * @param solutionPath The (relative) path to the solution file
	 */
	public SubmissionExecuter(String submissionPath, String solutionPath) {
		this.submPath = submissionPath;
		this.solPath = solutionPath;
	}
	
	
	/**
	 * Tests all the available submissions
	 */
	public void runCheck() {
		// get list of all submissions
		File subSrc = new File(submPath);
		File[] submissions = subSrc.listFiles();
		
		// show info
		System.out.println("Solution: \n\t" + solPath);
		System.out.println("\n" + submissions.length + " submissions found: ");
		for (File f : submissions) 
			System.out.println("\t" + f.getPath());
		
		// load tags & solution
		SolutionReader sr = new SolutionReader(solPath);
		sr.loadFile();
		String solution = sr.getHTML().toString();
		
		// Define output writer
		PrintWriter out = new PrintWriter(System.out, false);
		String[] connProps = sr.getConnectionProperties();
		
		for (int i = 0; i < submissions.length; i++) {
			
			File subm = submissions[i];
			System.out.println("\n\n[" + (i+1) + "/" + submissions.length + "] Testing: " + subm);
			
			// load a submission
			SubmissionReader subr = new SubmissionReader(subm.getPath(), IOUtil.tags);
			subr.loadFile();

			// get mapping and apply it
			ArrayList<String[]> mapping = subr.getMapping();
			String checkStr = IOUtil.applyMapping(solution, mapping);
			
			// perform the check
			try {
				runSubmission(checkStr, connProps, out);
			} catch (SQLException sqle) {
				// unable to close connection
				sqle.printStackTrace();
			}
		}
		
		// close output stream
		out.close();
	}
	
	/**
	 * Runs a submission
	 * @param sqlhtml HTML containing the submitted sql statements 
	 * @param connProps Connection properties in the following order:
	 *  (host, user, pw, dbname)
	 * @throws SQLException If the function was unable to close the sql connection
	 */
	private void runSubmission(String sqlhtml, String[] connProps, PrintWriter out) throws SQLException {
		
		MySqlTest tester = null;
		
		boolean DEBUG = true;
		
		try {
			// init connection
			tester = init(connProps[0], connProps[3], connProps[1], connProps[2]);
			
			// parse & execute the submission 
			Parse target = new Parse(sqlhtml);
			tester.doTables(target);
			
			
			System.out.println("\n* * * RESULTS * * *");
			
			System.out.println("Counts:\n\t" + tester.counts);
			
			String csv = IOUtil.getParseResult(target);
			System.out.println("CSV:\n\t" + csv);
			
			
			/*
			 * ToDo: Improve by adapting the Parse.print() function?
			 * See
			 * https://github.com/unclebob/fitnesse/blob/master/src/fit/Parse.java
			 */
			
		} catch (FitParseException fpe) {
			fpe.printStackTrace();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			// finish printing informations for this test
			out.flush();
			// close connection
			if (tester != null) tester.close();
		}
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
	 * - multiple submissions at once
	 * - multiple assignments at one? - not required?
	 * - more info/output
	 * - write output to file
	 * - javadoc
	 * HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
	 */
	
	public static void main(String[] args) {
		String submissionPath = "data/assignment1/submissions/";
		String solutionPath = "data/assignment1/solution.txt";
		
		SubmissionExecuter se = new SubmissionExecuter(submissionPath, solutionPath);
		se.runCheck();
	}

}
