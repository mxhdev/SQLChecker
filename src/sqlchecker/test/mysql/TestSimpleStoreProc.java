package sqlchecker.test.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestSimpleStoreProc {


	private static final String host = "localhost";
	private static final String db = "dbfit";
	private static final String user = "root";
	private static final String pw = "";
	
	
	public TestSimpleStoreProc() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.out.println("Unable to find MySQL Driver");
			e.printStackTrace(System.out);
		}
	}
	
	
	
	private Connection init() throws SQLException {
		String connStr = "jdbc:mysql://" + host + "/" + db + "?" +
                "user=" + user + "&password=" + pw;
		
		System.out.println("Connecting via JDBC with the following URI:\n" + connStr + "\n\n");
		
		Connection c = DriverManager.getConnection(connStr);
		return c;
	}
	
	
	private void doProcedure(Connection con) throws SQLException {
		String queryDrop =
		        "DROP PROCEDURE IF EXISTS filterPrices";
			String createProcedure = "create procedure filterPrices() " +
			    "begin " +
		            "select bezeichnung, preis " +
		            "from produkte " +
		            "where preis > 500; " +
		        "end";
			
			// statements
		    Statement stmt = null;
		    Statement stmtDrop = null; 

			// drop old
			
		    try {
		        System.out.println("Calling DROP PROCEDURE (1/3)");
		        stmtDrop = con.createStatement();
		        stmtDrop.execute(queryDrop);
		    } catch (SQLException e) {
		        e.printStackTrace(System.out);
		    } finally {
		        if (stmtDrop != null)
		        {
		            stmtDrop.close();
		        }
		    }		
			
		    // create new
		    
		    try {
		    	System.out.println("\nCalling CREATE PROCEDURE (2/3)");
		        stmt = con.createStatement();
		        stmt.executeUpdate(createProcedure);
		    } catch (SQLException e) {
		        e.printStackTrace(System.out);
		    } finally {
		        if (stmt != null) { stmt.close(); }
		    }
			
	}
	
	
	public void runTest() {

		Connection conn = null;
		ResultSet rs = null;
		
		
		CallableStatement spcall = null;
		
		try {
			conn = init();
			
			doProcedure(conn);
			
			System.out.println("\nCalling *call* PROCEDURE (3/3)");
			spcall = conn.prepareCall("{call filterPrices()}");
			rs = spcall.executeQuery();
			
			System.out.println("\n\n\n*** RESULTS ***");
			int columnsNumber = rs.getMetaData().getColumnCount();
			while (rs.next()) {
			    for (int i = 1; i <= columnsNumber; i++) {
			        if (i > 1) System.out.print(",  ");
			        String columnValue = rs.getString(i);
			        System.out.print(rs.getMetaData().getColumnName(i) + "=\"" + columnValue + "\"");
			    }
			    System.out.println("");
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace(System.out);
		} finally {
			// try to close the connection
			try {
				if (conn != null) conn.close();
				if (rs != null) rs.close();
				if (spcall != null) spcall.close();
			} catch (SQLException e) {
				e.printStackTrace(System.out);
			}
		}
	}
	
	
	public static void main(String[] args) {
		TestSimpleStoreProc sptest = new TestSimpleStoreProc();
		sptest.runTest();
	}

}
