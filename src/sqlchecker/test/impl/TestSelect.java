package sqlchecker.test.impl;

import dbfit.MySqlTest;
import fit.Fixture;
import fit.Parse;
import fit.exception.FitParseException;
import sqlchecker.test.DBFitTest;

public class TestSelect extends DBFitTest {

	private final String sqlQuery;
	
	private final String htmlResult;
	
	
	/**
	 * Initialize a select-test.The following values are applied: <br> 
	 * host=localhost, db=dbfit, user=root, pw=
	 * @param query SQL query
	 * @param result The expected result as html
	 */
	public TestSelect(String query, String result) {
		super();
		
		sqlQuery = query;
		htmlResult = result;
	}
	
	
	/**
	 * Initialize a select-test. Uses the root:*empty* database user
	 * @param query SQL query
	 * @param result The expected result as html
	 * @param host Hostname (e.g. localhost)
	 * @param db Database name (e.g. dbfit)
	 */
	public TestSelect(String query, String result, String host, String db) {
		super(host, db);
		
		sqlQuery = query;
		htmlResult = result;
	}
	
	
	/**
	 * Initialize a select-test
	 * @param query SQL query
	 * @param result The expected result <b>as html</b>
	 * @param host Hostname (e.g. localhost)
	 * @param db Database name (e.g. dbfit)
	 * @param user Username (e.g. root)
	 * @param pw Password (e.g. *empty*)
	 */
	public TestSelect(String query, String result, String host, String db, String user, String pw) {
		super(host, db, user, pw);
		
		sqlQuery = query;
		htmlResult = result;
	}
	
	
	@Override
	protected Fixture runQuery(MySqlTest conn) {
		// String query = "select 'test' as y";
		Fixture f = conn.query(sqlQuery);
		
		return f;
	}
	

	@Override
	protected Parse getTarget() throws FitParseException {
		/*Parse target = new Parse("<table> <tr><td></td></tr> <tr> <td>x</td> "
				+ "</tr> <tr> <td>test</td> </tr> </table>");
		*/
		Parse target = new Parse(htmlResult);
		return target;
	}

	
}
