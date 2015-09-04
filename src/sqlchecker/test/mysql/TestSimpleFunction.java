package sqlchecker.test.mysql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestSimpleFunction {


	private static final String host = "localhost";
	private static final String db = "dbfit";
	private static final String user = "root";
	private static final String pw = "";
	
	
	public TestSimpleFunction() {
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
	
	
	private void doFunction(Connection con) throws SQLException {
		String queryDrop =
		        "DROP FUNCTION IF EXISTS filterProducts";
			String createFunction = "CREATE FUNCTION filterProducts (fpid INT) "+
			    "returns TEXT " +
		        "begin "+
		            "declare bez TEXT; " +
		            "set bez = (select bezeichnung from produkte where pid= fpid); " +
		            "return bez; " +
		        "end;; ";
		        
			
			// statements
		    Statement stmt = null;
		    Statement stmtDrop = null; 

			// drop old
			
		    try {
		        System.out.println("Calling DROP Function (1/3)");
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
		    	System.out.println("\nCalling CREATE Function (2/3) \n");
		    	System.out.println(createFunction);
		        stmt = con.createStatement();
		        stmt.executeUpdate(createFunction);
		    } catch (SQLException e) {
		        e.printStackTrace(System.out);
		    } finally {
		        if (stmt != null) { stmt.close(); }
		    }
			
	}
	
	
	public void runTest() {

		Connection conn = null;
		ResultSet rs = null;
		Integer fpid = 1;
		
		
		CallableStatement spcall = null;
		
		try {
			conn = init();
			
			doFunction(conn);
			
			System.out.println("\nCalling *call* FUNCTION (3/3)");
			spcall = conn.prepareCall("SELECT filterProducts("+fpid+")");
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
		TestSimpleFunction sptest = new TestSimpleFunction();
		sptest.runTest();
	}

}
