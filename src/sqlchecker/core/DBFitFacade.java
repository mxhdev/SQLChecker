package sqlchecker.core;

import java.sql.SQLException;
import java.util.ArrayList;

import sqlchecker.io.impl.ScriptReader;
import dbfit.MySqlTest;
import fit.Parse;
import fit.exception.FitParseException;


/**
 * This is a facade class which provides easy to use functions 
 * for checking a students submission via DBFit
 * @author Max Hofmann
 *
 */
public class DBFitFacade {

	/**
	 * File name/path of the file which is being checked
	 */
	private String fileName = "";
	
	/**
	 * Connection properties in the following order:
	 *  (host, user, pw, dbname)
	 */
	private String[] connProps = new String[0];
	
	/**
	 * Temporary storage for the results of a submission
	 */
	private String storage = "";
	
	private String resetPath = "";
	
	/**
	 * Initialize a DBFit facade object
	 * @param fName File name of the submission that should be checked
	 * @param resetScript Path to the reset script which should be executed
	 * before running any of the actual queries. If the given file does 
	 * not exist, then there will be no reset queries executed
	 * @param cProps Connection properties in the following order:
	 *  (host, user, pw, dbname)
	 */
	public DBFitFacade(String fName, String resetScript, String[] cProps) {
		this.fileName = fName;
		this.connProps = cProps.clone();
		this.resetPath = resetScript;
	}
	
	
	
	
	
	
	/**
	 * Runs a submission
	 * @param sqlhtml HTML containing the submitted SQL statements, name and student id(s) of the submitter. 
	 * @throws SQLException If the function was unable to close the sql connection
	 */
	public ResultStorage runSubmission(String sqlhtml, ArrayList<String> name, ArrayList<String> matrikelnummer) throws SQLException {
		
		MySqlTest tester = null;

		ResultStorage rs = null;
		try {
			// init connection
			tester = init();
			
			// parse & execute the submission 
			Parse target = new Parse(sqlhtml);
			tester.doTables(target);
			
			
			System.out.println("\n* * * RESULTS * * *");
			
			// right, wrong, ignored, exception
			System.out.println("Counts:\n\t" + tester.counts);
			
			String result = getParseResult(target);
			
			rs = new ResultStorage(fileName, name, matrikelnummer, result
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
	private MySqlTest init() throws SQLException {
		// Initialize test

		String host = connProps[0];
		String db = connProps[3];
		String dbuser = connProps[1];
		String dbpw = connProps[2];
		
		System.out.println("Executing reset with values \n\thost=" + host + "\n\tdb=" + db + "\n\tuser=" + dbuser + "\n\tpw=" + dbpw + "\n\tscript=" + resetPath);;
		
		ScriptReader sr = new ScriptReader(resetPath, ScriptReader.DEFAULT_DELIM, connProps);
		sr.loadFile();
		
		System.out.println("Connection with values \n\thost=" + host + "\n\tdb=" + db + "\n\tuser=" + dbuser + "\n\tpw=" + dbpw);
		
		MySqlTest tester = new MySqlTest();
		tester.connect(host, dbuser, dbpw, db);
		
		return tester;
	}
	
	
	
	
	

	private String getParseResult(Parse p) {

		// get result as string
		storage = "";
		printParseStr(p, 0);
		
		return storage;
		
	}
	

	/**
	 * Stores the annotated parse String in a class
	 * variable. This makes sure that the output is stored
	 * in the correct order. <br>
	 * This function was adapted from the Parse.print() function
	 * in the fitnesse github repository
	 * @param p Parse object which should be stored
	 * @param iter Iteration counter, start at 0
	 * @see https://github.com/unclebob/fitnesse/blob/master/src/fit/Parse.java
	 */
	private void printParseStr(Parse p, int iter) {
		 
		storage += p.leader; 
		storage += p.tag;

		if (p.parts != null) {
			printParseStr(p.parts, iter++);
		} else {
			storage += p.body; 
		}
		
		storage += p.end; 
		
		if (p.more != null) {
			printParseStr(p.more, iter++);
		} else {
			storage += p.trailer;
		}

	}
	

	
	
	
	
	
}
