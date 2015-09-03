package sqlchecker.test;

import java.sql.SQLException;
import java.util.List;

import dbfit.MySqlTest;
import dbfit.fixture.Execute;
import dbfit.fixture.Query;
import dbfit.util.DataRow;

import fit.exception.FitParseException;
import fit.Fixture;
import fit.Parse;

/**
 * Abstract class which can be used for defining test cases
 * @author Max Hofmann
 *
 */
public abstract class DBFitTest {
	
	/**
	 * Hostname of the Database endpoint (e.g. localhost)
	 */
	private final String host;
	
	/**
	 * Name of the database which should be used
	 */
	private final String db;
	
	/**
	 * Name of the database user which should be used
	 */
	private final String dbuser;
	
	/**
	 * Password of the database user which should be used
	 */
	private final String dbpw;
	
	
	
	/**
	 * Initialize a new DBFIt Test class. 
	 * The following values are applied: <br> 
	 * host=localhost, db=dbfit, user=root, pw=
	 */
	public DBFitTest() {
		this("localhost", "dbfit", "root", "");
	}
	
	/**
	 * Initialize a new DBFIt Test class. Uses the root:*empty* database user
	 * @param hostVal Hostname (e.g. localhost)
	 * @param dbVal Database name (e.g. dbfit)
	 */
	public DBFitTest(String hostVal, String dbVal) {
		this(hostVal, dbVal, "root", "");
	}
	
	
	/**
	 * Initialize a new DBFIt Test class
	 * @param hostVal Hostname (e.g. localhost)
	 * @param dbVal Database name (e.g. dbfit)
	 * @param user Username (e.g. root)
	 * @param pw Password (e.g. *empty*)
	 */
	public DBFitTest(String hostVal, String dbVal, String user, String pw) {
		host = hostVal;
		db = dbVal;
		dbuser = user;
		dbpw = pw;
	}
	
	
	/**
	 * Creates a database connection for a MySQL endpoint
	 * @return MySqlTest instance
	 * @throws SQLException
	 */
	private MySqlTest init() throws SQLException {
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
	protected abstract Fixture runQuery(MySqlTest conn);
	
	
	/**
	 * For retrieving the target result. This is the result that
	 * should be returned by the sql query
	 * @return The (target) result set as a html table
	 */
	protected abstract Parse getTarget() throws FitParseException;
	
	
	
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
	
	
	
	public void runTest() {
		MySqlTest tester = null;
		
		try {
			// init connection
			tester = init();
			
			// run query
			Fixture raw = runQuery(tester);
			
			// try to match with solution
			Parse target = getTarget();
			raw.doTable(target);
			
			//show output
			showResult(raw, target);
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
	
	
	

}
