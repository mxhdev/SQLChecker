package sqlchecker.test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;


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
	
	
	
	
	public void runTest() {
		
		String insertQuery1 = "insert into produkte(bezeichnung, preis) values ('tablet', 450)";
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			// init connection
			conn = init();
			conn.setAutoCommit(false);
			DatabaseMetaData dmd = conn.getMetaData();
			System.out.println("(Commit-DDL-automatically) evil=" + dmd.dataDefinitionCausesTransactionCommit());
			
			// insert
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT bezeichnung, preis from produkte");
			printResults(rs);
			
			stmt.executeUpdate(insertQuery1);
			
			rs = stmt.executeQuery("SELECT bezeichnung, preis from produkte");
			printResults(rs);
			
			System.out.println("Stored Procedure call! (no OUT-param)");
			boolean hasRes = stmt.execute("{call filterByPrice(50)}");
			if (hasRes) {
				printResults(stmt.getResultSet());
			} else {
				System.out.println(stmt.getUpdateCount());
			}
			
			stmt.execute("DROP PROCEDURE if exists CalcLength");
			stmt.execute("CREATE PROCEDURE CalcLength(IN name varchar(100), OUT strlength int) set strlength =length(name);");
			
			System.out.println("Stored Procedure call! (1 OUT-param)");
			// hasRes = stmt.execute("{call CalcLength(?, ?)}");
			// CallableStatement stCall = conn.prepareCall("{ call CalcLength(?,?) }");
			CallableStatement stCall = conn.prepareCall("{ call CalcLength('hello', ?) }");
			// .execute() => see below
			
			// for checking parameter types without string parsing
			ParameterMetaData pmd = stCall.getParameterMetaData();
			int count = pmd.getParameterCount();
			
			System.out.println("MODES:");
			System.out.println("\tIN=" + ParameterMetaData.parameterModeIn);
			System.out.println("\tOUT=" + ParameterMetaData.parameterModeOut);
			System.out.println("\tINOUT=" + ParameterMetaData.parameterModeInOut);
			System.out.println("\tUNKNOWN=" + ParameterMetaData.parameterModeUnknown);
			
			for (int i = 1; i <= count; i++) {
				System.out.println("[" + i + "] type=" + pmd.getParameterType(i));
				System.out.println("[" + i + "] pmode=" + pmd.getParameterMode(i));
				System.out.println("[" + i + "] typename=" + pmd.getParameterTypeName(i));
				System.out.println("[" + i + "] classname=" + pmd.getParameterClassName(i));
			}
			
			//stCall.setString(1, "HelloXy");
			
			stCall.registerOutParameter(1, java.sql.Types.INTEGER);
			
			hasRes = stCall.execute();
			System.out.println("obj=" + stCall.getObject(1));
			System.out.println("X=" + stCall.getInt(1));
			// System.out.println("X=" + stCall.getInt(2));
			
			if (hasRes) {
				printResults(stCall.getResultSet());
			} else {
				System.out.println(stCall.getUpdateCount());
			}
			
			System.out.println("exec (sp, with in and out)");
			hasRes = stmt.execute("{call CalcLength('abc', @lenxy)}");
			hasRes = stmt.execute("SELECT @lenxy");
			// hasRes = stmt.execute("SELECT * from (call CalcLength('abc', @strlen))");
			if (hasRes) {
				printResults(stmt.getResultSet());
			} else {
				System.out.println(stmt.getUpdateCount());
			}
			
			stmt.execute("DROP PROCEDURE if exists CalcLength");
			

			stmt.execute("drop function if exists sumab");
			stmt.execute("create function sumab(a decimal(16, 4), b decimal(16, 4)) returns decimal(16, 4) deterministic return a + b;");
			
			
			System.out.println("FUNCTION TEST");
			stCall = conn.prepareCall("{? = call sumab(150, 4)}");
			// geht auch fuer int
			stCall.registerOutParameter(1, Types.DECIMAL, 1);
			
			//stCall.setInt(2, 4);
			//stCall.setInt(3, 1);
			
			stCall.execute();
			System.out.println(stCall.getInt(1));
			
			
			hasRes = stmt.execute("{? = call sumab(150, 4)}");
			System.out.println("exec");
			if (hasRes) {
				printResults(stmt.getResultSet());
			} else {
				System.out.println(stmt.getUpdateCount());
			}
			
	/*
	 * http://stackoverflow.com/questions/1379146/mysql-jdbc-function-in-out-param
	 */

			
			pmd = stCall.getParameterMetaData();
			count = pmd.getParameterCount();
			for (int i = 1; i <= count; i++) {
				System.out.println("[" + i + "] type=" + pmd.getParameterType(i));
				System.out.println("[" + i + "] pmode=" + pmd.getParameterMode(i));
				System.out.println("[" + i + "] typename=" + pmd.getParameterTypeName(i));
				System.out.println("[" + i + "] classname=" + pmd.getParameterClassName(i));
			}
			stmt.execute("drop function if exists sumab");
			
			System.out.println("PROCEDURE TEST /w select but not OUT-param");
			stmt.execute("DROP PROCEDURE if exists testproc");
			
			stmt.execute("CREATE PROCEDURE testproc() BEGIN SELECT bezeichnung FROM produkte; END");
			
			//stCall = conn.prepareCall("{call testproc()}");
			hasRes = stmt.execute("{call testproc()}");
			
			if (hasRes) {
				printResults(stmt.getResultSet());
			} else {
				System.out.println(stmt.getUpdateCount());
			}
			stmt.execute("DROP PROCEDURE if exists testproc");
			
			
			System.out.println("PROCEDURE TEST /w params, but no OUT");
			stmt.execute("DROP PROCEDURE if exists procInsert");
			
			stmt.execute("CREATE PROCEDURE procInsert(in px INT) BEGIN insert into produkte(bezeichnung, preis) values ('tablet', px); END");
			
			hasRes = stmt.execute("{ call procInsert(588)}");
			if (hasRes) {
				printResults(stmt.getResultSet());
			} else {
				System.out.println(stmt.getUpdateCount());
			}
			stmt.execute("DROP PROCEDURE if exists procInsert");
			
			System.out.println("FUNCTION TEST without params");
			
			stmt.execute("drop function if exists GiveFive");
			stmt.execute("create function GiveFive() returns decimal(16, 4) deterministic return 5;");
			
			hasRes = stmt.execute("{? = call GiveFive()}");
			if (hasRes) {
				printResults(stmt.getResultSet());
			} else {
				System.out.println(stmt.getUpdateCount());
			}
			stmt.execute("drop function if exists GiveFive");
			
			// no commit, no persist?
			// except for create table/function/procedure and insert into :(
			// the following command makes changes persistent!!!!!!!
			//conn.commit();
		} catch (SQLException sqle) {
			sqle.printStackTrace(System.out);
		} finally {
			// try to undo everything!
			// NOTE: DDL can not be rolled back :(
			rollback(conn);
			// close statement object
			close(stmt);
			// close the connection
			close(conn);
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
		// delimiter
		System.out.println("- - -  - - - -  - -- - - - - ");
	}
	
	
	
	public static void main(String[] args) {
		MySQLResultTest mysql = new MySQLResultTest();
		mysql.runTest();
	}

}
