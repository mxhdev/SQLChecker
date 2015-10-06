package sqlchecker.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * MySQL test class. This class tests persistence and shows a 
 * simple implementation for executing queries
 * 
 * @author Max Hofmann
 *
 */
public class MySQLResultTest {


	
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
	

	public MySQLResultTest() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	private Connection init() throws SQLException {
		
		System.out.println("Connection with values host=" + host + ", db=" + db + ", user=" + dbuser + ", pw=" + dbpw);
		
		Connection conn = null;
		
		String url = "jdbc:mysql://localhost/" + db + "?user=" + dbuser + "?password=" + dbpw;
		System.out.println("URL: " + url);
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
	
	
	
	
	public void runTest() {
		
		String insertQuery1 = "insert into produkte(bezeichnung, preis) values ('tablet', 450)";
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			// init connection
			conn = init();
			conn.setAutoCommit(false);
			
			// insert
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT bezeichnung, preis from produkte");
			printResults(rs);
			
			stmt.executeUpdate(insertQuery1);
			
			rs = stmt.executeQuery("SELECT bezeichnung, preis from produkte");
			printResults(rs);
			
			// no commit, no persist
			
			// the following command makes changes persistent!!!!!!!
			//conn.commit();
		} catch (SQLException sqle) {
			// try to undo everything!
			rollback(conn);
			sqle.printStackTrace(System.out);
			
		} finally {
			close(stmt);
			close(conn);
			/*
			// close the connection if it was initialized
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqle2) {
					sqle2.printStackTrace(System.out);
				}
			}
			*/
		}
		
		
		
	}
	
	


	private void printResults(ResultSet rs) throws SQLException{
		//Ensure we start with first row
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		
		while (rs.next()) {
		    for (int i = 1; i <= columnsNumber; i++) {
		        if (i > 1) System.out.print(",  ");
		        String columnValue = rs.getString(i);
		        System.out.print(columnValue + " " + rsmd.getColumnName(i));
		    }
		    System.out.println("");
		}
		// close the result set
		close(rs);
		System.out.println("- - -  - - - -  - -- - - - - ");
	}
	
	
	
	public static void main(String[] args) {
		MySQLResultTest mysql = new MySQLResultTest();
		mysql.runTest();
	}

}
