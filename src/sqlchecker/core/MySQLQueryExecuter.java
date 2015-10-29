package sqlchecker.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import sqlchecker.io.IOUtil;

public class MySQLQueryExecuter extends MySQLWrapper {


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
	
	
	
	public void runSQL(ArrayList<String> queryList) {
		
		Connection conn = null;
		Statement stmt = null;
		
		try {
			// init connection
			conn = init();
			conn.setAutoCommit(true);

			stmt = conn.createStatement();

			// run every sql statement
			for (int i = 0; i < queryList.size(); i++) {
				String sql = queryList.get(i);
				stmt.execute(sql);
			}
			
		} catch (SQLException sqle) {
			// try to undo everything!
			rollback(conn);
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
