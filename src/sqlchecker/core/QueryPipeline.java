package sqlchecker.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import sqlchecker.io.IOUtil;

public class QueryPipeline {

	
	/**
	 * Hostname of the Database endpoint (e.g. localhost)
	 */
	private final String host = "localhost";
	
	/**
	 * Name of the database which should be used
	 */
	private final String db = "dbfit";
	
	/**
	 * Name of the database user which should be used
	 */
	private final String dbuser = "root";
	
	/**
	 * Password of the database user which should be used
	 */
	private final String dbpw = "";
	
	
	
	
	private ArrayList<String[]> mapping = new ArrayList<String[]>();
	
	private ArrayList<SQLCallable> calls = new ArrayList<SQLCallable>();
	
	
	public QueryPipeline(ArrayList<String[]> sqlmapping, ArrayList<SQLCallable> sqlcallables) {
		// store the mapping
		mapping.clear();
		mapping.addAll(sqlmapping);
		// store the available callable things
		calls.clear();
		calls.addAll(sqlcallables);

		// Try to load the driver class
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	

	private Connection init() throws SQLException {
		
		System.out.println("Connection with values host=" + host + ", db=" + db + ", user=" + dbuser + ", pw=" + dbpw);
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + db, dbuser, dbpw);
		} catch (SQLException sqle) {
			sqle.printStackTrace(System.out);
		}
		
		return conn;
	}
	
	

	private void rollback(Connection conn) {
		try {
			if (conn != null) {
				if (!conn.isClosed()) {
					System.out.println("calling rollback!");
					conn.rollback();
				} else {
					System.out.println("Connection already closed");
				}
			} else {
				System.out.println("Connection is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private void close(AutoCloseable ac) {
		try {
			if (ac != null) {
				ac.close();
			} else {
				System.out.println("[WARNING] close() "
						+ "AutoClosable \"ac\" can not be "
						+ "closed because it is already null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void runSQL(ArrayList<SQLResultStorage> results) {
		
		Connection conn = null;
		//Statement stmt = null;
		ResultSet rs = null;
		
		try {
			// init connection
			conn = init();
			conn.setAutoCommit(false);
			
			// create statement object
			//stmt = conn.createStatement();
			
			for (int i = 0; i < mapping.size(); i++) {
				String query = mapping.get(i)[1];
				// also execute "drop" statements
				// special parsing for stuff that is a callable
				SQLResultStorage tmpres = handleQuery(query, conn);
				results.add(tmpres);
			}
			
		} catch (SQLException sqle) {
			// try to undo everything!
			rollback(conn);
			sqle.printStackTrace(System.out);
			
		} finally {
			// close statement object
			//close(stmt);
			// close the connection
			close(conn);
		}
	}
	
	
	
	
	private SQLResultStorage handleQuery(String sql, Connection conn) {
		SQLResultStorage resultFinal = new SQLResultStorage(sql);
		Statement stmt = null;
		try {
			int idx = isCallable(sql);
			// todo!
			if (idx < 0) {
				// not a callable statement
				stmt = conn.createStatement();
			} else {
				// function/procedure call
			}
			
			
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			close(stmt);
		}
		
		return resultFinal;
	}
	
	
	/**
	 * 
	 * @param sql
	 * @return Index of the statement inside callable list, -1 if
	 * it was not found
	 */
	private int isCallable(String sql) {

		for (int i = 0; i < calls.size(); i++) {
			String callStr = calls.get(i).getStatement();
			if (callStr.equals(sql)) {
				return i;
			}
		}

		return -1;
	}
	
	
	
	public ArrayList<String[]> run() {
		// step 0 - init
		ArrayList<SQLResultStorage> results = new ArrayList<SQLResultStorage>();
		
		// step 1 - detect type (is it a callable, or a list of them?)
		// step 2 - execute & store the results somehow
		// step 3 - return it
		return null;
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
