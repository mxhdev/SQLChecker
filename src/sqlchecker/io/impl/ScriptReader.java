package sqlchecker.io.impl;

import java.util.ArrayList;

import sqlchecker.config.Config;
import sqlchecker.core.MySQLQueryExecuter;
import sqlchecker.io.AbstractFileReader;



/**
 * This class reads the given MySQL script file and executes every
 * query that was found
 * @author Max Hofmann
 *
 */
public class ScriptReader extends AbstractFileReader {

	
	public static final String DEFAULT_DELIM = "/*static*/";
	
	/**
	 * Delimiter string which separates the queries in the script
	 * file
	 */
	private String delim = "/*static*/";
	
	
	/**
	 * List of queries found in the file
	 */
	private ArrayList<String> queryList = new ArrayList<String>();
	
	
	/*
	 * It is recommended to leave the "db" (database name) field 
	 * empty, because the database which has to be changed will 
	 * probably be dropped by one of the first queries. Afterwards, 
	 * the selected database would no longer exist.
	 */
	
	/**
	 * Connection properties which should be used for
	 * executing the script after reading it.<br>
	 * Instead of using the supplied database name, this module
	 * will always use an empty "" database name 
	 */
	private Config dbConf;
	
	
	/**
	 * @param sPath Path leading to the script file
	 * @param delimiter Delimiter string. This string marks the
	 * start of a new SQL query. Examples:<br>
	 * ; <br>
	 * static-tag <br>
	 * @param connProps Connection properties as configuration object
	 */
	public ScriptReader(String sPath, String delimiter, Config connProps) {
		// store path
		super(sPath);
		
		// store query delimiter
		this.delim = delimiter;
		
		// save connection properties
		this.dbConf = connProps;
		//this.dbconf = connProps.clone();
		// dbname has to be empty!
		//this.conn[3] = "";
	}
	
	
	/**
	 * Uses default connection properties
	 * @param sPath Path leading to the script file
	 * @param delimiter Delimiter string. This string marks the
	 * start of a new SQL query. Examples:<br>
	 * ; <br>
	 * static-tag <br>
	 */
	/*public ScriptReader(String sPath, String delimiter) {
		this(sPath, delimiter, IOUtil.DEFAULT_PROPS);
	}
	*/


	@Override
	protected void onReadLine(String line) {
		if (line.equals(delim)) {
			queryList.add("");
		} else {
			if (queryList.isEmpty()) {
				System.out.println("[ScriptReader] WARNING: No initial tag found for line\n\t\"" + line + "\"");
			} else {
				// append the just read line
				String tmp = queryList.get(queryList.size() - 1);
				tmp = tmp + "\n" + line;
				queryList.set(queryList.size() - 1, tmp);
				
			}
		}
	}


	@Override
	protected void beforeReading(String pathToFile) {
		queryList.clear();
	}


	@Override
	protected void afterReading(String pathToFile) {
		
		/*
		System.out.println("> " + queryList.size() + " queries found!");
		for (int i = 0; i < queryList.size(); i++) {
			System.out.println("- - - - - - - - - [" + (i+1) + "/" + queryList.size() + "] - - - - - - \n\n");
			System.out.println(queryList.get(i));
			System.out.println("\n\n");
		}
		*/
		
		
		// Run Script with database=""
		// Do not select a database, because the database which
		// has to be changed will probably be dropped in the first
		// step
		
		MySQLQueryExecuter exec = new MySQLQueryExecuter(dbConf.getHost(), dbConf.getUser(), dbConf.getPw(), dbConf.getDbName());
		exec.setIgnoreFK(true); // make sure to ignore fk, to avoid errors
		exec.setAutoCommit(true);
		exec.runSQL(queryList);
	}
	
	
	
	/**
	 * 
	 * @return The SQL commands read from the SQL script file
	 */
	public ArrayList<String> getSQLCommands() {
		return this.queryList;
	}
	
	
	
	public static void main(String[] args) {
		String path = "data/reset.sql";
		String delim = "/*static*/";
		
		/*
		String[] connProps = new String[4];
		connProps[0] = "localhost";
		connProps[1] = "root";
		connProps[2] = "";
		// IMPORTANT for index 3: DONT SELECT A DATABASE! 
		// default="dbfit";
		connProps[3] = ""; 
		*/
		
		Config dbconf = new Config("root", "", "localhost", "");
		
		
		ScriptReader sr = new ScriptReader(path, delim, dbconf);
		sr.loadFile();
	}
}
