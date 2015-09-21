package sqlchecker.core;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import dbfit.MySqlTest;
import fit.Parse;
import fit.exception.FitParseException;
import sqlchecker.io.IOUtil;
import sqlchecker.io.impl.SolutionReader;
import sqlchecker.io.impl.SubmissionReader;

public class SubmissionExecuter {

	
	String submPath = "";
	String solPath = "";
	
	
	public SubmissionExecuter(String submissionPath, String solutionPath) {
		this.submPath = submissionPath;
		this.solPath = solutionPath;
	}
	
	
	public void runCheck() {
		// load tags & solution
		SolutionReader sr = new SolutionReader(solPath);
		sr.loadFile();
		String solution = sr.getHTML().toString();
		
		// load a submission
		SubmissionReader subr = new SubmissionReader(submPath, IOUtil.tags);
		subr.loadFile();
		ArrayList<String[]> mapping = subr.getMapping();
		
		String checkStr = IOUtil.applyMapping(solution, mapping);

		try {
			// try to run the submission
			runSubmission(checkStr, sr.getConnectionProperties());
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
	/**
	 * Runs a submission
	 * @param sqlhtml HTML containing the submitted sql statements 
	 * @param connProps Connection properties in the following order:
	 *  (host, user, pw, dbname)
	 * @throws SQLException If the function was unable to close the sql connection
	 */
	private void runSubmission(String sqlhtml, String[] connProps) throws SQLException {
		
		MySqlTest tester = null;
		PrintWriter out = null;
		
		final boolean DEBUG = true;
		
		try {
			tester = init(connProps[0], connProps[3], connProps[1], connProps[2]);
			
			Parse target = new Parse(sqlhtml);
			tester.doTables(target);
			
			
			System.out.println("\n\n\n * * * RESULTS * * *");
			
			System.out.println("==> " + tester.counts);
			
			/*
			out = new PrintWriter(System.out, true){
				@Override
				public void print(String s) {
					if (s.endsWith("td>"))
						super.print("\t\t" + s);
					else if (s.endsWith("tr>"))
						super.print("\t" + s);
					else 
						super.print(s);
					
				}
			};*/
			if (DEBUG) {
				out = new PrintWriter(System.out, true);
				target.print(out);
			}
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
			if (out != null) out.close();
			if (tester != null) tester.close();
		}
	}
	
	
	
	/**
	 * Creates a database connection for a MySQL endpoint
	 * @return MySqlTest instance
	 * @throws SQLException
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
		String submissionPath = "data/assignment1/submissions/s1.sql";
		String solutionPath = "data/assignment1/solution.txt";
		
		SubmissionExecuter se = new SubmissionExecuter(submissionPath, solutionPath);
		se.runCheck();
	}

}
