package sqlchecker.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class MySQLWrapper {


	/**
	 * Hostname of the Database endpoint (e.g. localhost)
	 */
	protected String host = "localhost";

	/**
	 * Name of the database which should be used
	 */
	protected String db = "dbfit";

	/**
	 * Name of the database user which should be used
	 */
	protected String dbuser = "root";

	/**
	 * Password of the database user which should be used
	 */
	protected String dbpw = "";

	private static boolean autocommit = false;
	
	/**
	 * Initialize the SQL wrapper class
	 * @param inHost Database host name
	 * @param inUser User name
	 * @param inPw Password of user
	 * @param inDb Name of database which should be used
	 */
	public MySQLWrapper(String inHost, String inUser, String inPw, String inDb) {
		
		// save connection properties
		this.host = inHost;
		this.dbuser = inUser;
		this.dbpw = inPw;
		this.db = inDb;
		
		// Try to load the driver class
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
	}



	/**
	 * 
	 * @return True iff Auto-Commit is enabled
	 */
	protected boolean isAutoCommitEnabled() {
		return this.autocommit;
	}
	
	public void setAutoCommit(boolean b) {
		this.autocommit = b;
	}
	

	protected Connection init() throws SQLException {

		System.out.println("Connection with values host=" + host + ", db=" + db + ", user=" + dbuser + ", pw=" + dbpw);
		
		final int PORT = 3306;
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + PORT + "/" + db, dbuser, dbpw);
		} catch (SQLException sqle) {
			sqle.printStackTrace(System.out);
		}


		return conn;
	}



	protected void rollback(Connection conn) {
		try {
			if (conn != null) {
				if (!autocommit) {
					if (!conn.isClosed()) {
						System.out.println("calling rollback!");
						conn.rollback();
					} else {
						System.out.println("Connection already closed");
					}
				}
			} else {
				System.out.println("Connection is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	protected void close(AutoCloseable ac) {
		try {
			if (ac != null) {
				// Also commit the connection (if autoCommit=false)
				if ( (!autocommit) && (ac instanceof Connection)) {
					((Connection) ac).commit();
				}
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


	
	

	
	protected ArrayList<String[]> storeResultSet(ResultSet rs) throws SQLException {
		ArrayList<String[]> rtable = new ArrayList<String[]>();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int ccount = rsmd.getColumnCount();
		
		/*
		 * TEST:
		 * SELECT preis as pxy
		 * ColumnName = preis
		 * ColumnLabel = pxy
		 * SELECT preis
		 * ColumnName = preis
		 * ColumnLabel = preis
		 */
		
		// header
		String[] head = new String[ccount];
		for (int i = 1; i <= ccount; i++) {
			// head[i-1] = rsmd.getColumnName(i) + " aka " + rsmd.getColumnLabel(i); 
			head[i-1] = rsmd.getColumnLabel(i);
		}
		rtable.add(head);
		
		// table content
		while (rs.next()) {
			String[] row = new String[ccount];
		    for (int i = 1; i <= ccount; i++) {
		    	row[i-1] = rs.getString(i);
		    }
		    rtable.add(row);
		}
		
		// close the result set
		close(rs);
		
		/*
		System.out.println("- - -  - rstor (start)  - - - - - ");
		
		for (int i = 0; i < rtable.size(); i++) {
			System.out.println(Arrays.toString(rtable.get(i)));
		}
		
		System.out.println("- - -  - rstor (end)  - - - - -\n\n ");
		*/
		
		return rtable;
	}
	



}
