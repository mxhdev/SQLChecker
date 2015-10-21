package sqlchecker.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
				SQLCallable sqlc = calls.get(idx);
				String[] qlist = query.split("\n");
				if (sqlc.isFunction()) {
					// functions are put in "one" table
					html += "!| (> F <) Execute Procedure| " + sqlc.getName() + " |\n";
				}
				for (int j = 0; j < qlist.length; j++) {
					String q = qlist[j];
					ArrayList<String> planTmp = generateQueryList(q, idx);
					// TODO: function/procedure call
					ArrayList<String[]> res = new ArrayList<String[]>();
					
					// Build result
					// Contains a dump of the result (1st line is headers)
					html += generateHTML(q, sqlc, headerCols, res); // one Callable = 1 line
					
					// for debugging
					queryList.addAll(planTmp);
					
				}
				
				html += "\n";
				
				
				
				
				for (int j = 0; j < queryList.size(); j++) {
					System.out.println("[" + (j+1) + "/" + queryList.size() + "] Run execute:" + queryList.get(j));
				}
				
				
				
				
				
			}
			
			
		}
		System.out.println("HTML: \n- - - -\n" + html + "\n- - - -\n\n");
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
			for (int i = 0; i < data.length; i++) {
				html += "| " + data[i];
			}
			/* for (String d : data) {
				html += "| " + d;
			} */
			// Result of running a function is always ONE value
			// Look at: Second row, First field
			String output = "unknown";
			if (result.size() > 1) output = result.get(1)[0];
			html += "| " + output + " |";
			
		} else if (call.isProcedure()) {
			// Stored Procedure
			if ( (!call.isOutOrInout()) ) {
				// THen this function does not contain a select
				// and might contain an INSERT INTO statement
				// see allTests->procInsert
				if (result.isEmpty()) {
					/*
					 * NOTE:
					 * There could be a problem if there is a out/inout param and no output!
					 */
					// probably an INSERT INTO statement, use execute!
					html += "!| Execute | call " + sql + " |";
					// !| Execute | call procInsert(122)|
				} else {
					// Special Case: No out parameters, but there are still
					// results because the procedure calls a SELECT statement
					html += "!| Query | call " + sql + " |\n";
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
			} else {
				// Print the call
				html += "!| Execute Procedure | " + call.getName() + " |\n";
				// Print the header 
				html += "| ";
				for (int i = 0; i < header.length; i++) {
					String h = header[i];
					if (h.startsWith("@")) {
						// convert mysql syntax to dbfit syntax
						// @val => val?
						h = h.substring(1) + "?";
					}
					html += h + " |";
				}
				html += "\n";
				// Print the results
				// the sp/function has at least one out or inout parameter
				// IF THERE IS A OUT/INOT PARAMETER THEN THERE IS ALSO SOME OUTPUT
				int imark = 0; // the current position in the input data array
				int omark = 0; // the current position in the output list
				// start at pos 1 because the first line is the table header
				for (int j = 1; j < result.size(); j++) {
					// for each line...
					html += "| ";
					for (int i = 0; i < header.length; i++) {
						String h = header[i];
						if (h.startsWith("@")) {
							// out/input
							// current line, omark-column
							System.out.println("(out)rsize=" + result.size());
							System.out.println("j=" + j);
							System.out.println("omark=" + omark);
							html += result.get(j)[omark];
							omark++;
						} else {
							// in
							System.out.println("(in)dsize=" + data.length);
							System.out.println("data=" + Arrays.toString(data));
							System.out.println("j=" + j);
							System.out.println("imark=" + imark);
							html += data[imark];
							imark++;
						}
						html += " | ";
					}
					html += "\n";
				}
			}
		} else {
			// a normal query! TODO
		}
		html += "\n";
		return html;
	}
	
	
	public String runSQL() {
		
		Connection conn = null;
		Statement stmt = null;
		
		String html = "";
		
		try {
			// init connection
			conn = init();
			conn.setAutoCommit(false);

			stmt = conn.createStatement();

			html = runAndBuildHTML(stmt);
			System.out.println("HTML: \n- - - -\n" + html + "\n- - - -\n\n");
			
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
		
		return html;
	}
	
	
	
	
	
	private String runAndBuildHTML(Statement stmt) throws SQLException {
		// stores result html
		String html = ""; 
		
		boolean hasRes = false;
		ArrayList<String[]> resRaw = new ArrayList<String[]>();
		
		ResultSet rs = null;
		
		for (int i = 0; i < mapping.size(); i++) {
			
			resRaw = new ArrayList<String[]>();
			String query = mapping.get(i)[1];
			
			// query is one mapping (might have multiple calls)
			System.out.println("\n\n - - - - - - - - - -\n");
			System.out.println("Executing query: ");
			System.out.println(query + "\n-EndOfQuery-");
			System.out.println("\n=> Plan of execution:");
			
			// check query type! Is it a callable?
			System.out.println("[0/X] Check for callable");
			int idx = isCallable(query);
			
			// not a callable
			if (idx < 0) {
				// not a callable statement
				System.out.println("> Not a callable");
				System.out.println("[1/1] Run execute(\"\n" + query +"\n\")");
				
				// Run it
				hasRes = stmt.execute(query);
				
				// Fetch results
				if (hasRes) {
					rs = stmt.getResultSet();
					resRaw = storeResultSet(rs);
				} else {
					System.out.println("> " + stmt.getUpdateCount() + " rows affected!");
				}
				
				// store results in html string
				// header
				String queryLower = query.toLowerCase();
				if (queryLower.startsWith("select ")) {
					html += "!| Query | " + query + " |";
				} else {
					html += "!| Execute | " + query + " |";
				}
				html += "\n";
				// Actual result table
				for (int i2 = 0; i2 < resRaw.size(); i2++) {
					String[] row = resRaw.get(i2);
					html += "| ";
					for (String relem : row) {
						html += relem + " | ";
					}
					html += "\n";
				}
				html += "\n";

			} else {
				// it is a callable statement
				String[] headerCols = calls.get(idx).generateResultHeader();
				
				System.out.println("> Result table header:");
				System.out.println(Arrays.toString(headerCols));
				
				ArrayList<String> queryList = new ArrayList<String>();
				System.out.println("> Callable found at index " + idx + ", generating plan");
				
				// ASSUMPTION!! each line calls the same function
				SQLCallable sqlc = calls.get(idx);
				String[] qlist = query.split("\n");
				
				// Print function call header already, because
				// successive function calls are merged into
				// one table in the solution.txt file
				if (sqlc.isFunction()) {
					// functions are put in "one" table
					html += "!| Execute Procedure | " + sqlc.getName() + " |\n";
				}
				// this is the list of all calls in the current mapping element
				for (int j = 0; j < qlist.length; j++) {
					// generate a plan with SET and SELECT statements
					String q = qlist[j];
					ArrayList<String> planTmp = generateQueryList(q, idx);
					
					// Function/procedure and SET/SELECT calls
					for (int k = 0; k < planTmp.size(); k++) {
						hasRes = stmt.execute(planTmp.get(k));
					}
					
					// Fetch result!
					if (hasRes) {
						rs = stmt.getResultSet();
						resRaw = storeResultSet(rs);
					} else {
						System.out.println("> " + stmt.getUpdateCount() + " rows affected!");
					}
					
					// Build result
					// One Callable = 1 line
					// Contains a dump of the result (1st line is headers)
					// The calls might be split up in multiple test cases
					html += generateHTML(q, sqlc, headerCols, resRaw); 
					
					// for debugging, save the complete plan
					queryList.addAll(planTmp);
					
				}
				// Add a blank line between every test case
				html += "\n";
				
				// Show the complete list of executed SQL queries
				for (int j = 0; j < queryList.size(); j++) {
					System.out.println("[" + (j+1) + "/" + queryList.size() + "] Run execute:" + queryList.get(j));
				}
				
			}
		}
		
		return html;
	}
	
	
	
	
	private ArrayList<String[]> storeResultSet(ResultSet rs) throws SQLException {
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
		
		
		System.out.println("- - -  - rstor (start)  - - - - - ");
		
		for (int i = 0; i < rtable.size(); i++) {
			System.out.println(Arrays.toString(rtable.get(i)));
		}
		
		System.out.println("- - -  - rstor (end)  - - - - -\n\n ");
		
		
		return rtable;
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
	
	
	
	public String run() {
		// step 0 - init
		String compiledHTML = "";
		
		// just do a dry run - for testing purposes
		// dryRun();
		
		// step 1 - detect type (is it a callable, or a list of them?)
		// step 2 - execute & store the results somehow
		compiledHTML = runSQL();
		
		// step 3 - return it
		return compiledHTML;
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
