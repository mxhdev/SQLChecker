package sqlchecker.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

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
	
	
	
	
	/**
	 * Generates a series of SQL statements which execute the callables
	 * defined in the currently seen mapping
	 * @param sqlStr The string/mapping found in the input file
	 * @param callIdx The index of the callable
	 * @return A list of SQL statements
	 */
	private ArrayList<String> generateQueryList(String sqlStr, int callIdx) {
		ArrayList<String> queries = new ArrayList<String>();
		SQLCallable ca = calls.get(callIdx);
		
		
		
		if (ca.isFunction()) {
			// stored function calls
			for (String sql : sqlStr.split("\n")) {
				String newSQL = "{ ? = call " + sql + " }";
				queries.add(newSQL);
			}
		} else if (ca.isProcedure()) {
			// stored procedure calls
			if (!ca.hasOutParameter()) {
				for (String sql : sqlStr.split("\n")) {
					String newSQL = "{ call " + sql + " }";
					queries.add(newSQL);
				}
			} else {
				// procedure with OUT or INOUT parameters
				// the difficult part
				queries.addAll(ca.prepareInOutCall(sqlStr));
			}
		} else {
			System.out.println("[ERROR] QueryPipeline.generateQueryList "
					+ "- Unknown callable type for query \n" + sqlStr 
					+ "\nNot doing anything!");
		}
		
		return queries;
	}
	
	/**
	 * Simulates query execution via mysql.execute() without
	 * actually running the queries. This function only logs, what
	 * it would run, IF it would be using the MySQL connection
	 */
	private void dryRun() {
		String html = "";
		for (int i = 0; i < mapping.size(); i++) {
			String query = mapping.get(i)[1];
			// query is one mapping (might have multiple calls)
			System.out.println("\n\n - - - - - - - - - -\n");
			System.out.println("Executing query: ");
			System.out.println(query + "\n-EndOfQuery-");
			System.out.println("\n=> Plan of execution:");
			// check query type!
			int idx = isCallable(query);
			System.out.println("[0/X] Check for callable");
			// todo!
			if (idx < 0) {
				// not a callable statement
				System.out.println("> Not a callable");
				System.out.println("[1/1] Run execute(\"\n" + query +"\n\")");
				// TODO: Run it
				// TODO: Build result
			} else {
				
				String[] headerCols = calls.get(idx).generateResultHeader();
				System.out.println("> Result table header:");
				System.out.println(Arrays.toString(headerCols));
				
				ArrayList<String> queryList = new ArrayList<String>();
				System.out.println("> Callable found at index " + idx + ", generating plan");
				// ASSUMPTION!! each line calls the same function
				String[] qlist = query.split("\n");
				for (int j = 0; j < qlist.length; j++) {
					String q = qlist[j];
					ArrayList<String> planTmp = generateQueryList(q, idx);
					// TODO: function/procedure call
					ArrayList<String[]> res = new ArrayList<String[]>();
					
					// Build result
					// Contains a dump of the result (1st line is headers)
					html += generateHTML(q, calls.get(idx), headerCols, res); // one Callable = 1 line
					
					// for debugging
					queryList.addAll(planTmp);
					
				}
				
				
				
				
				for (int j = 0; j < queryList.size(); j++) {
					System.out.println("[" + (j+1) + "/" + queryList.size() + "] Run execute:" + queryList.get(j));
				}
				
				
				
				
			}
			
		}
	}
	
	
	/**
	 * 
	 * @param sql One call of a stored procedure or
	 * function (i.e. PlusEins(51) 
	 * @param call The SQLCallable, which the SQL statement corresponds
	 * to
	 * @param result The result of executing this call
	 * @return HTML Version of the result merged with the input
	 */
	private String generateHTML(String sql, SQLCallable call, String[] header, ArrayList<String[]> result) {
		String html = "";
		// all the arguments of the call
		String[] data = SQLCallable.parseCallData(sql);
		if (call.isFunction()) {
			// Function, easy part
			// print input
			for (String d : data) {
				html += ("| " + d);
			}
			// Result of running a function is always ONE value
			// Look at: Second row, First field
			String output = result.get(1)[0];
			html += "| " + output + " |";
			
		} else if (call.isProcedure()) {
			// Stored Procedure
			if ( (!call.getDirection().isOutOrInout()) ) {
				//TODO: What if there is an empty result
				// THen this function does not contain a select
				// and might contain an INSERT INTO statement
				// see allTests->procInsert PROBLEM
				
				// Special Case: No out parameters, but there are still
				// results because the procedure calls a SELECT statement
				html += "! | Query | call " + sql + " |\n";
				// There might be some result
				// If there is no result then the following loop will
				// obviously not do anything
				for (int i = 0; i < result.size(); i++) {
					String[] row = result.get(i);
					html += "| ";
					for (String relem : row) {
						html += relem + " | ";
					}
					html += "\n";
				}
			}
		}
		return html;
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
				SQLResultStorage tmpres = handleQuery(query, conn); // BUG!
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
			int idx = isCallable(sql); // BUG -> DOES NOT WORK LIKE THAT
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
		
		//Test 1: Can't be a callable because every callable
		// has an opening bracket
		if (!sql.contains("(")) return -1;
		
		// Test 2: Starting keyword
		// Calls can't start with one of the following strings
		String sqlLower = sql.toLowerCase();
		if (sqlLower.startsWith("create ") ||
				sqlLower.startsWith("delete ") || 
				sqlLower.startsWith("drop ") ||
				sqlLower.startsWith("insert into ") ||
				sqlLower.startsWith("select ") ||
				sqlLower.startsWith("update "))
			return -1;
		
		// extract name
		sql = sql.substring(0, sql.indexOf("("));
		
		for (int i = 0; i < calls.size(); i++) {
			// name comparison!!
			String callStr = calls.get(i).getName();
			//System.out.println("\"" + callStr + "\" vs \"" + sql + "\"");
			if (callStr.equals(sql)) {
				// System.out.println("\t\tSTATUS=SUCCESS!");
				return i;
			} else {
				//System.out.println("\t\tSTATUS=FAIL!");
			}
		}

		return -1;
	}
	
	
	
	public ArrayList<String[]> run() {
		// step 0 - init
		ArrayList<SQLResultStorage> results = new ArrayList<SQLResultStorage>();
		// just do a dry run - for testing purposes
		dryRun();
		// step 1 - detect type (is it a callable, or a list of them?)
		// step 2 - execute & store the results somehow
		// step 3 - return it
		return null;
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
