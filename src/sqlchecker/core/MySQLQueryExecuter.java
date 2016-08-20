package sqlchecker.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import sqlchecker.config.Config;


public class MySQLQueryExecuter extends MySQLWrapper {

	
	/**
	 * True iff foreign key constraints should be ignored (can be useful
	 * for script execution)
	 */
	private boolean ignoreFK = false;

	
	private boolean[] statusList = new boolean[0];
	
	private String errorString = "";
	
	
	
	/**
	 * Initialize this class, which allows executing a list of mysql
	 * queries
	 * @param connProps Connection properties as configuration object
	 */
	public MySQLQueryExecuter(Config dbconf) {
		// connection properties
		this(dbconf.getHost(), dbconf.getUser(), dbconf.getPw(), dbconf.getDbName());
		//super(dbconf.getHost(), dbconf.getUser(), dbconf.getPw(), dbconf.getDbName());
	}

	
	/**
	 * @param host database host name (e.g. localhost)
	 * @param user database user 
	 * @param pw database user password
	 * @param dbName database name (empty = default)
	 * 
	 */
	public MySQLQueryExecuter(String host, String user, String pw, String dbName) {
		super(host, user, pw, dbName);
	}
	
	
	/**
	 * 
	 * @return The value of the internal "ignore foreign key" flag.
	 * Iff this value is true, then the class will ignore 
	 * all foreign key constraints
	 */
	public boolean ignoredFK() {
		return this.ignoreFK;
	}
	
	/**
	 * Tells the class if foreign keys should be ignored. (Default = false)
	 * @param igno The new "ignore foreign key" value. Iff this value
	 * is true, then the class will ignore all foreign key constraints
	 */
	public void setIgnoreFK(boolean igno) {
		this.ignoreFK = igno;
	}
	
	
	
	/**
	 * Executes a list of (My)SQL queries
	 * @param queryList
	 * @return True iff no error occurred (thus, no rollback was performed)
	 * The error information can be acquired with getErrorMsg() and getStatusList()
	 */
	public boolean runSQL(ArrayList<String> queryList) {

		// init error string and status flag
		errorString = "";
		boolean success = true;

		// init status list
		statusList = new boolean[queryList.size()];
		for (int i = 0; i < statusList.length; i++) {
			statusList[i] = false;
		}

		Connection conn = null;
		Statement stmt = null;
		
		String sql = "";
		
		try {
			// init connection
			conn = init();
			conn.setAutoCommit(isAutoCommitEnabled());

			if (ignoreFK) {
				Statement stmtfk0 = conn.createStatement();
				stmtfk0.execute("SET FOREIGN_KEY_CHECKS=0");
				stmtfk0.close();
			} 
			
			// stmt = conn.createStatement();

			// run every SQL statement
			// one statement per query
			for (int i = 0; i < queryList.size(); i++) {
				stmt = conn.createStatement();
				
				sql = queryList.get(i);
				
				try {
					stmt.execute(sql);
				} finally {
					close(stmt);
				}
				
				statusList[i] = true;
			}
			
			
			if (ignoreFK) {
				Statement stmt2 = conn.createStatement();
				stmt2.execute("SET FOREIGN_KEY_CHECKS=1");
				stmt2.close();
			}
			
		} catch (SQLException sqle) {
			// store error message!
			errorString = "[MySQLQueryExecuter] ERROR For sql \n" + sql + "\n";
			StackTraceElement[] strace = sqle.getStackTrace();
			for (StackTraceElement ste : strace) {
				errorString += ste.toString() + "\n";
			}
			errorString += "\n - - - - - - - - - - \n\n";
			
			// set flag
			success = false;
			
			// try to undo everything!
			rollback(conn);
			System.out.println(sql);
			sqle.printStackTrace(System.err);
		} finally {
			// close statement object
			//close(stmt);
			// commit & close the connection
			close(conn);
		}
		
		
		return success;
		
	}
	
	/**
	 * For checking the error message of the query list which was 
	 * last executed by this class
	 * @return The error message of the query list which was 
	 * last executed by this class. This String is empty iff there was 
	 * no error
	 */
	public String getErrorMessage() {
		return this.errorString;
	}

	
	/**
	 * For checking the status list of the last query list which was
	 * executed. The list contains true for query i as long as query i
	 * was successful, if query j was unsuccessful, the all values
	 * with index >= j will be false 
	 * @return Status list of the last executed query list
	 */
	public boolean[] getStatusList() {
		return this.statusList.clone();
	}
	
	
}
