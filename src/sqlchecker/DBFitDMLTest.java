package sqlchecker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbfit.MySqlTest;
import dbfit.api.DBEnvironment;
import dbfit.fixture.Commit;
import dbfit.fixture.Execute;
import dbfit.fixture.ExecuteDdl;
import dbfit.fixture.Query;
import dbfit.util.DataRow;
import dbfit.util.sql.PreparedStatements;
import fit.exception.FitParseException;
import fitnesse.junit.FitNesseRunner;
import fit.Fixture;
import fit.Parse;

/**
 * DML Test class
 * -> Does not work 
 * @author Max Hofmann
 *
 */
public class DBFitDMLTest {
	
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
	
	private ArrayList<String> tests = new ArrayList<String>();
	
	/**
	 * Creates a database connection for a MySQL endpoint
	 * @return MySqlTest instance
	 * @throws SQLException
	 */
	private MySqlTest init() throws SQLException {
		// init test
		
		// index 0
		tests.add("Drop table if exists DMLTestTable");
		
		// index 1
		tests.add("Create table DMLTestTable(col1 VARCHAR(16), col2 INT(16))");
		
		// index 2
		tests.add("Insert into DMLTestTable(`col1`, `col2`) values ('CD', 5)");
		
		// index 3 & 4
		tests.add("Select * from DMLTestTable");
		tests.add("<table>"
				+ "<tr> <td></td> <td></td> </tr>"
				+ "<tr> <td>col1</td> <td>col2</td> </tr>"
				+ "<tr> <td>cd</td> <td>5</td> </tr>"
				+ "</table>");
		
		// index 5,6,7
		tests.add("Insert into DMLTestTable(`col1`, `col2`) values ('tv', 800)");
		tests.add("Insert into DMLTestTable(`col1`, `col2`) values ('chair', 80)");
		tests.add("Insert into DMLTestTable(`col1`, `col2`) values ('big pc', 1600)");
		
		// index 8 & 9
		tests.add("Select * from DMLTestTable");
		tests.add("<table>"
				+ "<tr> <td></td> <td></td> </tr>"
				+ "<tr> <td>col1</td> <td>col2</td> </tr>"
				+ "<tr> <td>cd</td> <td>5</td> </tr>"
				+ "<tr> <td>tv</td> <td>800</td> </tr>"
				+ "<tr> <td>chair</td> <td>80</td> </tr>"
				+ "<tr> <td>big pc</td> <td>1600</td> </tr>"
				+ "</table>");
		
		// index 10
		tests.add("UPDATE DMLTestTable SET col2 = 2500 WHERE col1= 'tv'");
		
		// index 11 & 12
		tests.add("Select * from DMLTestTable");
		tests.add("<table>"
				+ "<tr> <td></td> <td></td> </tr>"
				+ "<tr> <td>col1</td> <td>col2</td> </tr>"
				+ "<tr> <td>cd</td> <td>5</td> </tr>"
				+ "<tr> <td>tv</td> <td>2500</td> </tr>" // changed!
				+ "<tr> <td>chair</td> <td>80</td> </tr>"
				+ "<tr> <td>big pc</td> <td>1600</td> </tr>"
				+ "</table>");
		
		// index 13
		tests.add("DELETE from DMLTestTable where col2 > 2000");
		
		// index 14 & 15
		tests.add("Select * from DMLTestTable");
		tests.add("<table>"
				+ "<tr> <td></td> <td></td> </tr>"
				+ "<tr> <td>col1</td> <td>col2</td> </tr>"
				+ "<tr> <td>cd</td> <td>5</td> </tr>"
				+ "<tr> <td>chair</td> <td>80</td> </tr>"
				+ "<tr> <td>big pc</td> <td>1600</td> </tr>"
				+ "</table>");
		
		// index 16
		tests.add("Drop table if exists DMLTestTable");
		
		System.out.println("Connection with values host=" + host + ", db=" + db + ", user=" + dbuser + ", pw=" + dbpw);
		
		MySqlTest tester = new MySqlTest();
		tester.connect(host, dbuser, dbpw, db);
		
		return tester;
	}
	
	
	/**
	 * Calls either tester.query or tester.execute, or..?
	 * @param conn The database connection
	 * @return The result as a fixture
	 */
	protected Fixture runQuery(MySqlTest conn) {
		
		String compilation = "";
		
		compilation += "<table> <tr> <td>dbfit.MySqlTest</td> </tr> </table>\n";
		
		compilation += "\n<table> <tr> <td>Connect</td> <td>localhost</td> <td>root</td> <td></td> <td>dbfit</td> </tr> </table>\n";
		
		compilation += "\n<table><tr><td>Execute Ddl</td> <td>Drop table if exists DMLTestTable</td></tr></table> \n";
		
		compilation += "\n<table><tr><td>Execute Ddl</td> <td>Create table DMLTestTable(col1 VARCHAR(16), col2 INT(16))</td></tr></table> \n";
		
		compilation += "\n<table><tr><td>Execute</td> <td>Insert into DMLTestTable(`col1`, `col2`) values ('CD', 5)</td></tr></table> \n";
		
		compilation += "\n<table>"
						+ "<tr> <td>Query</td> <td>Select * from DMLTestTable</td> </tr>"
						+ "<tr> <td>col1</td> <td>col2</td> </tr>"
						+ "<tr> <td>CD</td> <td>5</td> </tr>"
						+ "</table>\n";

		
		compilation += "\n<table><tr><td>Execute</td> <td>" + tests.get(5) + "</td></tr></table> \n";
		
		compilation += "\n<table><tr><td>Execute</td> <td>" + tests.get(6) + "</td></tr></table> \n";
		
		compilation += "\n<table><tr><td>Execute</td> <td>" + tests.get(7) + "</td></tr></table> \n";
		
		compilation += "\n<table>"
				+ "<tr> <td>Query</td> <td>" + tests.get(8) + "</td> </tr>"
				+ "<tr> <td>col1</td> <td>col2</td> </tr>"
				+ "<tr> <td>CD</td> <td>5</td> </tr>"
				+ "<tr> <td>tv</td> <td>800</td> </tr>"
				+ "<tr> <td>chair</td> <td>80</td> </tr>"
				+ "<tr> <td>big pc</td> <td>1600</td> </tr>"
				+ "</table>\n";
		
		compilation += "\n<table><tr><td>Execute</td> <td>" + tests.get(10) + "</td></tr></table> \n";
		
		compilation += "\n<table>"
				+ "<tr> <td>Query</td> <td>" + tests.get(11) + "</td> </tr>"
				+ "<tr> <td>col1</td> <td>col2</td> </tr>"
				+ "<tr> <td>cd</td> <td>5</td> </tr>"
				+ "<tr> <td>tv</td> <td>2500</td> </tr>" // changed!
				+ "<tr> <td>chair</td> <td>80</td> </tr>"
				+ "<tr> <td>big pc</td> <td>1600</td> </tr>"
				+ "</table>\n";
		
		compilation += "\n<table><tr><td>Execute</td> <td>" + tests.get(13) + "</td></tr></table> \n";
		
		compilation += "\n<table>"
				+ "<tr> <td>Query</td> <td>" + tests.get(14) + "</td> </tr>"
				+ "<tr> <td>col1</td> <td>col2</td> </tr>"
				+ "<tr> <td>cd</td> <td>5</td> </tr>"
				+ "<tr> <td>chair</td> <td>80</td> </tr>"
				+ "<tr> <td>big pc</td> <td>1600</td> </tr>"
				+ "</table>\n";
		
		compilation += "\n<table><tr><td>Execute Ddl</td> <td>" + tests.get(16) + "</td></tr></table> \n";
		
		
		
		System.out.println("\n------------------------------\n");
		System.out.println(compilation);
		System.out.println("\n------------------------------\n\n");
		try  {
			Parse target = new Parse(compilation);
			conn.doTables(target);
			System.out.println("==> " + conn.counts);

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
		
		
		
		conn.executeDdl(tests.get(0));
		conn.executeDdl(tests.get(1));
		conn.execute(tests.get(2));
		Fixture f = conn.query(tests.get(3));
		
		String xy = PreparedStatements.buildStoredProcedureCall("asdf", 2);
		System.out.println(xy);
		
		
		
		//TODO
		return f;
	}
	
	
	/**
	 * For retrieving the target result. This is the result that
	 * should be returned by the sql query
	 * @return The (target) result set as a html table
	 */
	protected Parse getTarget() throws FitParseException {
		//TODO
		Parse p = new Parse(tests.get(4));
		
		return p;
	}
	
	
	public void runTest() {
		
		MySqlTest tester = null;
		
		String compilation = "";
		
		
		
		try {

			// init connection
			tester = init();

			
			// TEST CASES - START
			
			compilation += "<table> <tr> <td>dbfit.MySqlTest</td> </tr> </table>\n";
			
			compilation += "\n<table> <tr> <td>Connect</td> <td>localhost</td> <td>root</td> <td></td> <td>dbfit</td> </tr> </table>\n";
			
			compilation += "\n<table><tr><td>Execute Ddl</td> <td>Drop table if exists DMLTestTable</td></tr></table> \n";
			
			compilation += "\n<table><tr><td>Execute Ddl</td> <td>Create table DMLTestTable(col1 VARCHAR(16), col2 INT(16))</td></tr></table> \n";
			
			compilation += "\n<table><tr><td>Execute</td> <td>Insert into DMLTestTable(`col1`, `col2`) values ('CD', 5)</td></tr></table> \n";
			
			compilation += "\n<table>"
							+ "<tr> <td>Query</td> <td>Select * from DMLTestTable</td> </tr>"
							+ "<tr> <td>col1</td> <td>col2</td> </tr>"
							+ "<tr> <td>CD</td> <td>5</td> </tr>"
							+ "</table>\n";

			
			compilation += "\n<table><tr><td>Execute</td> <td>" + tests.get(5) + "</td></tr></table> \n";
			
			compilation += "\n<table><tr><td>Execute</td> <td>" + tests.get(6) + "</td></tr></table> \n";
			
			compilation += "\n<table><tr><td>Execute</td> <td>" + tests.get(7) + "</td></tr></table> \n";
			
			compilation += "\n<table>"
					+ "<tr> <td>Query</td> <td>" + tests.get(8) + "</td> </tr>"
					+ "<tr> <td>col1</td> <td>col2</td> </tr>"
					+ "<tr> <td>CD</td> <td>5</td> </tr>"
					+ "<tr> <td>tv</td> <td>800</td> </tr>"
					+ "<tr> <td>chair</td> <td>80</td> </tr>"
					+ "<tr> <td>big pc</td> <td>1600</td> </tr>"
					+ "</table>\n";
			
			compilation += "\n<table><tr><td>Execute</td> <td>" + tests.get(10) + "</td></tr></table> \n";
			
			compilation += "\n<table>"
					+ "<tr> <td>Query</td> <td>" + tests.get(11) + "</td> </tr>"
					+ "<tr> <td>col1</td> <td>col2</td> </tr>"
					+ "<tr> <td>CD</td> <td>5</td> </tr>"
					+ "<tr> <td>tv</td> <td>2500</td> </tr>" // changed!
					+ "<tr> <td>chair</td> <td>80</td> </tr>"
					+ "<tr> <td>big pc</td> <td>1600</td> </tr>"
					+ "</table>\n";
			
			compilation += "\n<table><tr><td>Execute</td> <td>" + tests.get(13) + "</td></tr></table> \n";
			
			compilation += "\n<table>"
					+ "<tr> <td>Query</td> <td>" + tests.get(14) + "</td> </tr>"
					+ "<tr> <td>col1</td> <td>col2</td> </tr>"
					+ "<tr> <td>CD</td> <td>5</td> </tr>"
					+ "<tr> <td>chair</td> <td>80</td> </tr>"
					+ "<tr> <td>big pc</td> <td>1600</td> </tr>"
					+ "</table>\n";
			
			compilation += "\n<table><tr><td>Execute Ddl</td> <td>" + tests.get(16) + "</td></tr></table> \n";
			
			System.out.println("\n------------------------------\n");
			System.out.println(compilation);
			System.out.println("\n------------------------------\n\n");
			
			// TEST CASES - END
			
			
			Parse target = new Parse(compilation);
			tester.doTables(target);
			System.out.println("\n\n\n==> " + tester.counts);
			
		} catch (SQLException sqle) {
			sqle.printStackTrace(System.out);
		} catch (FitParseException fpe) {
			fpe.printStackTrace(System.out);
		} finally {
			// close the connection if it was initialized
			if (tester != null) {
				try {
					tester.close();
				} catch (SQLException sqle2) {
					sqle2.printStackTrace(System.out);
				}
			}
		}
		
		
		
	}
	
	

	
	private void showResult(Fixture resultRaw, Parse target) throws SQLException {
		
		System.out.println("\n\n----------------------------------------\n");
		
		System.out.println("* leader:   " + target.parts.more.leaf().leader);
		System.out.println("* tag:      " + target.parts.more.leaf().tag);
		System.out.println("* body:   \n" + target.parts.more.leaf().body);
		System.out.println("* trailer:  " + target.parts.more.leaf().trailer);
		System.out.println("* end:      " + target.parts.more.leaf().end);
		
		System.out.println("* more:     " + target.parts.more.leaf().more);
		System.out.println("* parts:    " + target.parts.more.leaf().parts);
		
		System.out.println("\n----------------------------------------\n\n");
		
		
		// counts
		System.out.println("* COUNTS: \n\t" + resultRaw.counts());
				
		// print results
		List<DataRow> results = null;
		if (resultRaw instanceof Query) { 
			results = ((Query) resultRaw).getDataTable().getUnprocessedRows();
			// print result of query
			System.out.println("* Displaying " + results.size() + " row(s)");
			for (int i = 0; i < results.size(); i++) {
				System.out.println("\t" + results.get(i));
			}
		} else if (resultRaw instanceof Execute) {
			// results = ((Execute) resultRaw).
			System.out.println("[EXECUTE] " + ((Execute) resultRaw));
		} else {
			System.out.println("Unknown result-type: " + resultRaw);
		}
				
	}
	
	
	public static void main(String[] args) {
		DBFitDMLTest test = new DBFitDMLTest();
		
		test.runTest();
	}
	

}
