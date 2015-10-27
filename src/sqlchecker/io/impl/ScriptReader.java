package sqlchecker.io.impl;

import java.util.ArrayList;

import sqlchecker.core.MySQLQueryExecuter;
import sqlchecker.io.AbstractFileReader;
import sqlchecker.io.IOUtil;




public class ScriptReader extends AbstractFileReader {

	private String delim = "/*static*/";
	
	
	private ArrayList<String> queryList = new ArrayList<String>();
	
	private String[] conn = new String[4];
	
	/**
	 * @param sPath Path leading to the script file
	 * @param delimiter Delimiter string. This string marks the
	 * start of a new SQL query. Examples:<br>
	 * ; <br>
	 * static-tag <br>
	 * @param connProps Connection properties in the following order: <br>
	 * host (default:localhost) <br>
	 * dbUser (default:root) <br>
	 * dbUserPw (default:) <br>
	 * dbName (default:dbfit) <br>
	 */
	public ScriptReader(String sPath, String delimiter, String[] connProps) {
		// store path
		super(sPath);
		// store query delimiter
		this.delim = delimiter;
		// save connection properties
		this.conn = connProps.clone();
		
	}
	
	
	/**
	 * Uses default connection properties
	 * @param sPath Path leading to the script file
	 * @param delimiter Delimiter string. This string marks the
	 * start of a new SQL query. Examples:<br>
	 * ; <br>
	 * static-tag <br>
	 */
	public ScriptReader(String sPath, String delimiter) {
		this(sPath, delimiter, IOUtil.DEFAULT_PROPS);
	}
	


	@Override
	public void onReadLine(String line) {
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
	public void beforeReading(String pathToFile) {
		queryList.clear();
	}


	@Override
	public void afterReading(String pathToFile) {
		
		System.out.println("> " + queryList.size() + " queries found!");
		for (int i = 0; i < queryList.size(); i++) {
			System.out.println("- - - - - - - - - [" + (i+1) + "/" + queryList.size() + "] - - - - - - \n\n");
			System.out.println(queryList.get(i));
			System.out.println("\n\n");
		}
		
		
		
		// Run Script with database=""
		// Do not select a database, because the database which
		// has to be changed will probably be droped in the first
		// step
		System.out.println("\n> Starting to execute the queries\n");
		
		MySQLQueryExecuter exec = new MySQLQueryExecuter(conn);
		exec.runSQL(queryList);
		
		System.out.println("\n> Finished executing the queries\n");
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
		
		String[] connProps = new String[4];
		connProps[0] = "localhost";
		connProps[1] = "root";
		connProps[2] = "";
		connProps[3] = ""; // DONT SELECT A DATABASE! "dbfit";
		
		ScriptReader sr = new ScriptReader(path, delim, connProps);
		sr.loadFile();
	}
}
