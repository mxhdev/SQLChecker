package sqlchecker.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import sqlchecker.io.IOUtil;

public class MySQLQueryExecuter extends MySQLWrapper {

	
	/**
	 * True iff foreign key constraints should be ignored (can be useful
	 * for script execution)
	 */
	private boolean ignoreFK = false;

	/**
	 * Initialize this class, which allows executing a list of mysql
	 * queries
	 * @param connProps Connection properties in the following order: <br>
	 * host (default:localhost) <br>
	 * dbUser (default:root) <br>
	 * dbUserPw (default:) <br>
	 * dbName (default:dbfit) <br>
	 */
	public MySQLQueryExecuter(String[] connProps) {
		// connection properties
		super(connProps[0], connProps[1], connProps[2], connProps[3]);
	}

	
	/**
	 * Makes this class use the default connection properties
	 */
	public MySQLQueryExecuter() {
		this(IOUtil.DEFAULT_PROPS);
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
	
	public void runSQL(ArrayList<String> queryList) {
		
		Connection conn = null;
		Statement stmt = null;
		
		String sql = "";
		
		try {
			// init connection
			conn = init();
			conn.setAutoCommit(false);

			if (ignoreFK) {
				Statement stmtfk0 = conn.createStatement();
				stmtfk0.execute("SET FOREIGN_KEY_CHECKS=0");
				stmtfk0.close();
			} 
			
			stmt = conn.createStatement();

			// run every SQL statement
			for (int i = 0; i < queryList.size(); i++) {
				sql = queryList.get(i);
				stmt.execute(sql);
			}
			
			
			if (ignoreFK) {
				Statement stmt2 = conn.createStatement();
				stmt2.execute("SET FOREIGN_KEY_CHECKS=1");
				stmt2.close();
			}
			
		} catch (SQLException sqle) {
			// try to undo everything!
			rollback(conn);
			System.out.println(sql);
			sqle.printStackTrace(System.out);
		} finally {
			// close statement object
			close(stmt);
			// close the connection
			close(conn);
		}
		
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
