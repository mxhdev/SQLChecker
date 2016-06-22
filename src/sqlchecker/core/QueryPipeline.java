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

public class QueryPipeline extends MySQLWrapper {

	
	
	
	
	
	
	private ArrayList<String[]> mapping = new ArrayList<String[]>();
	
	private ArrayList<SQLCallable> calls = new ArrayList<SQLCallable>();
	
	
	/**
	 * Creates a QueryPipeline object which executes all the queries 
	 * and creates appropriate HTML strings
	 * @param sqlmapping The (tag, SQL) mapping list
	 * @param sqlcallables List of all stores procedures and functions
	 * that can be called
	 * @param connProps Connection properties in the following order: <br>
	 * host (default:localhost) <br>
	 * dbUser (default:root) <br>
	 * dbUserPw (default:) <br>
	 * dbName (default:dbfit) <br>
	 */
	public QueryPipeline(ArrayList<String[]> sqlmapping, ArrayList<SQLCallable> sqlcallables, String[] connProps) {
		
		// connection properties
		super(connProps[0], connProps[1], connProps[2], connProps[3]);

		// store the mapping
		mapping.clear();
		mapping.addAll(sqlmapping);
		
		// store the available callable things
		calls.clear();
		calls.addAll(sqlcallables);
	}
	
	
	/**
	 * Creates a QueryPipeline object which executes all the queries
	 * and creates appropriate HTML strings. Using this constructor
	 * will make the class use the default connection properties
	 * @param sqlmapping The (tag, SQL) mapping list
	 * @param sqlcallables List of all stores procedures and functions
	 * that can be called
	 */
	public QueryPipeline(ArrayList<String[]> sqlmapping, ArrayList<SQLCallable> sqlcallables) {
		this(sqlmapping, sqlcallables, IOUtil.DEFAULT_PROPS);
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
					html += generateHTML(q, sqlc, headerCols, res, mapping.get(i)); // one Callable = 1 line
					
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
	 * @param header The header of the result table (variables are
	 * annotated with "@varname"
	 * @param result The result of executing this call
	 * @param mcurrent Current mapping, this is used for checking if
	 * the current mapping is static of depends on the student
	 * submission. This mapping is a (tag, SQL) tuple
	 * @return HTML Version of the result merged with the input
	 */
	private String generateHTML(String sql, SQLCallable call
			, String[] header, ArrayList<String[]> result
			, String[] mcurrent) {
		String html = "";
		boolean isStatic = mcurrent[0].equalsIgnoreCase("static");
		
		// all the arguments of the call
		String[] data = SQLCallable.parseCallData(sql);
		if (call.isFunction()) {
			// Function, just print the result set as a HTML table
			// The first array of the result set list contains the table header
			
			// print result (including header)
			for (int i = 0; i < result.size(); i++) {
				String[] row = result.get(i);
				html += "\n\t<tr>";
				for (String r : row) {
					html += "\n\t\t<td>" + r + "</td>";
				}
				html += "\n\t</tr>";
			}
			
			
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
					if (isStatic)
						html += "\n\n<table>"
								+ "\n\t<tr>"
								+ "\n\t\t<td>Execute</td>"
								+ "\n\t\t<td>call " + sql + "</td>"
								+ "\n\t</tr>";
					else
						html += "\n\n<table>"
								+ "\n\t<tr>"
								+ "\n\t\t<td>Execute</td>"
								+ "\n\t\t<td>call " + IOUtil.TAG_PREFIX + mcurrent[1] + IOUtil.TAG_SUFFIX + "</td>"
								+ "\n\t</tr>";
					// html += "!| Execute | call " + sql + " |";
					// !| Execute | call procInsert(122)|
				} else {
					// Special Case: No out parameters, but there are still
					// results because the procedure calls a SELECT statement
					if (isStatic)
						html += "\n\n<table>"
								+ "\n\t<tr>"
								+ "\n\t\t<td>Query</td>"
								+ "\n\t\t<td>call " + sql + "</td>"
								+ "\n\t</tr>";
					else
						html += "\n\n<table>"
								+ "\n\t<tr>"
								+ "\n\t\t<td>Query</td>"
								+ "\n\t\t<td>call " + IOUtil.TAG_PREFIX + mcurrent[1] + IOUtil.TAG_SUFFIX + "</td>"
								+ "\n\t</tr>";
					
					// There might be some result
					// If there is no result then the following loop will
					// obviously not do anything
					for (int i = 0; i < result.size(); i++) {
						String[] row = result.get(i);
						// html += "| ";
						html += "\n\t<tr>";
						for (String relem : row) {
							// html += relem + " | ";
							html += "\n\t\t<td>" + relem + "</td>";
						}
						html += "\n\t</tr>";
					}
				}
				html += "\n</table>\n";
			} else {
				// Print the call
				if (isStatic)
					html += "\n\n<table>"
							+ "\n\t<tr>"
							+ "\n\t\t<td>Execute Procedure</td>"
							+ "\n\t\t<td>" + call.getName() + "</td>"
							+ "\n\t</tr>";
				else
					html += "\n\n<table>"
							+ "\n\t<tr>"
							+ "\n\t\t<td>Execute Procedure</td>"
							+ "\n\t\t<td>" + IOUtil.TAG_PREFIX + mcurrent[1] + IOUtil.TAG_SUFFIX + "</td>"
							+ "\n\t</tr>";
				// html += "!| Execute Procedure | " + call.getName() + " |\n";
				// Print the header 
				// html += "| ";
				html += "\n\t<tr>";
				for (int i = 0; i < header.length; i++) {
					String h = header[i];
					if (h.startsWith("@")) {
						// convert mysql syntax to dbfit syntax
						// @val => val?
						h = h.substring(1) + "?";
					}
					html += "\n\t\t<td>" + h + "</td>";
					// html += h + " |";
				}
				html += "\n\t</tr>";
				// html += "\n";
				
				// Print the results
				// the sp/function has at least one out or inout parameter
				// IF THERE IS A OUT/INOT PARAMETER THEN THERE IS ALSO SOME OUTPUT
				int imark = 0; // the current position in the input data array
				int omark = 0; // the current position in the output list
				// start at pos 1 because the first line is the table header
				for (int j = 1; j < result.size(); j++) {
					// for each line...
					html += "\n\t<tr>";
					// print row while looking at header structure
					for (int i = 0; i < header.length; i++) {
						String h = header[i];
						if (h.startsWith("@")) {
							// out/input
							// current line, omark-column
							System.out.println("(out)rsize=" + result.size());
							System.out.println("j=" + j);
							System.out.println("omark=" + omark);
							// html += result.get(j)[omark];
							String resTmp = result.get(j)[omark];
							// remove leading/trailing " and '
							if (resTmp.charAt(0) == resTmp.charAt(resTmp.length() - 1)) {
								if ((resTmp.charAt(0) == '\'') || (resTmp.charAt(0) == '\"'))
									resTmp = resTmp.substring(1, resTmp.length()-1);
							}
							html += "\n\t\t<td>" + resTmp + "</td>";
							omark++;
						} else {
							// in
							System.out.println("(in)dsize=" + data.length);
							System.out.println("data=" + Arrays.toString(data));
							System.out.println("j=" + j);
							System.out.println("imark=" + imark);
							String dataTmp = data[imark];
							// remove leading/trailing " and '
							if (dataTmp.charAt(0) == dataTmp.charAt(dataTmp.length() - 1)) {
								if ((dataTmp.charAt(0) == '\'') || (dataTmp.charAt(0) == '\"'))
									dataTmp = dataTmp.substring(1, dataTmp.length()-1);
							}
							// html += data[imark];
							html += "\n\t\t<td>" + dataTmp + "</td>";
							imark++;
						}
						// html += " | ";
					}
					// html += "\n";
					html += "\n\t</tr>";
				}
				
				html += "\n</table>\n";
			}
		} /* else {
			// a normal query
		} */
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
		// stores result HTML
		String html = ""; 
		
		boolean hasRes = false;
		ArrayList<String[]> resRaw = new ArrayList<String[]>();
		
		ResultSet rs = null;

		for (int i = 0; i < mapping.size(); i++) {
			
			hasRes = false;
			resRaw = new ArrayList<String[]>();
			String qtag = mapping.get(i)[0];
			String query = mapping.get(i)[1];
			
			
			
			boolean errorExpected = qtag.equalsIgnoreCase("static.error");
			boolean isStatic = (qtag.equalsIgnoreCase("static") || errorExpected);
			
			// add error label
			if (errorExpected) {
				html += "\n<!--error-->\n";
			}
			
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
				// Don't execute queries which are expected to
				// produce an error
				if (!errorExpected) {
					hasRes = stmt.execute(query);
				}
				
				// Fetch results
				if (hasRes) {
					rs = stmt.getResultSet();
					resRaw = storeResultSet(rs);
				} else {
					System.out.println("> " + stmt.getUpdateCount() + " rows affected!");
				}
				
				// store results in html string
				
				// determine which command should be used
				String command = IOUtil.getDBFitCommand(query);
				
				System.out.println("> QUERY:\n" + query);
				System.out.println("> COMMAND:\n" + command);
				
				// Print the header with the command
				// and the query or its corresponding tag
				if (isStatic) {
					html += "\n\n<table>"
						+ "\n\t<tr>"
						+ "\n\t\t<td>" + command + "</td>"
						+ "\n\t\t<td>" + query + "</td>"
						+ "\n\t</tr>";
					System.out.println("[static, " + qtag + "]");
				} else {
					html += "\n\n<table>"
							+ "\n\t<tr>"
							+ "\n\t\t<td>" + command + "</td>"
							+ "\n\t\t<td>" + IOUtil.TAG_PREFIX + qtag + IOUtil.TAG_SUFFIX + "</td>"
							+ "\n\t</tr>";
					System.out.println("[non-static, " + qtag + "]");
				}
				// Print result table
				for (int i2 = 0; i2 < resRaw.size(); i2++) {
					String[] row = resRaw.get(i2);
					html += "\n\t<tr>";
					for (String relem : row) {
						html +=	"\n\t\t<td>" + relem + "</td>"; //relem + " | ";
					}
					html += "\n\t</tr>";
				}
				html += "\n</table>\n\n";

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
				
				
				// this is the list of all calls in the current mapping element
				for (int j = 0; j < qlist.length; j++) {
					// generate a plan with SET and SELECT statements
					String q = qlist[j];
					ArrayList<String> planTmp = generateQueryList(q, idx);
					
					// Function/procedure and SET/SELECT calls
					for (int k = 0; k < planTmp.size(); k++) {
						if (!errorExpected) {
							hasRes = stmt.execute(planTmp.get(k));
						}
					}
					
					// Fetch result! The last statement is always the one
					// which produces the final result
					if (hasRes) {
						rs = stmt.getResultSet();
						resRaw = storeResultSet(rs);
					} else {
						System.out.println("> " + stmt.getUpdateCount() + " rows affected!");
					}
					
					// create function header
					if (sqlc.isFunction()) {
						// functions are put in "one" table
						html += "\n\n<table>"
								+ "\n\t<tr>"
								+ "\n\t\t<td>Query</td>";
						// write to complete call or just the tag
						// NOTE: "...AS" renaming doesn't work here due to the generateQueryList implementation
						if (isStatic)
							html += "\n\t\t<td>SELECT " + q + "</td>";
						else
							html += "\n\t\t<td>SELECT " + IOUtil.TAG_PREFIX + qtag + IOUtil.TAG_SUFFIX + "</td>";
						html += "\n\t</tr>";
						
					}
					
					// Build result
					// One Callable = 1 line
					// Contains a dump of the result (1st line is headers)
					// The calls might be split up in multiple test cases
					html += generateHTML(q, sqlc, headerCols, resRaw, mapping.get(i).clone()); 
					
					// for debugging, save the complete plan
					queryList.addAll(planTmp);
					
					if (sqlc.isFunction()) 
						html += "</table>\n\n";
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


	
	
	
}
