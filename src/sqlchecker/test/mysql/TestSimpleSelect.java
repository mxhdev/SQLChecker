package sqlchecker.test.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;





public class TestSimpleSelect {

	private static final String host = "localhost";
	private static final String db = "dbfit";
	private static final String user = "root";
	private static final String pw = "";
	
	
	public TestSimpleSelect() {
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
		
		System.out.println("Connecting via JDBC with the following URI:\n" + connStr);
		
		Connection c = DriverManager.getConnection(connStr);
		return c;
	}
	
	
	public void runTest() {
		
		String sql = "SELECT bezeichnung,preis FROM produkte";
		
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = init();
			
			Statement st = conn.createStatement();
			rs = st.executeQuery(sql);

			int columnsNumber = rs.getMetaData().getColumnCount();
			while (rs.next()) {
			    for (int i = 1; i <= columnsNumber; i++) {
			        if (i > 1) System.out.print(",  ");
			        String columnValue = rs.getString(i);
			        System.out.print(columnValue + " " + rs.getMetaData().getColumnName(i));
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
			} catch (SQLException e) {
				e.printStackTrace(System.out);
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		TestSimpleSelect tsp = new TestSimpleSelect();
		tsp.runTest();
	}

}
