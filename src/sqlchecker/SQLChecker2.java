package sqlchecker;



import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import com.sun.media.jfxmedia.logging.Logger;

import dbfit.MySqlTest;
import dbfit.fixture.Execute;
import dbfit.fixture.Query;
import dbfit.util.DataRow;
import fit.Fixture;
import fit.Parse;
import fitnesseMain.FitNesseMain;

public class SQLChecker2 {
	
	
	

	public static void main(String[] args) {



		
		/*
		 * tester
		 */
		
		
		MySqlTest tester = new MySqlTest();
		System.out.println("\nInitialized class \"MySqlTest\" (1/2) \n");
		
		String dataSource = "localhost";
		String username = "dbfit_user";
		String password = "dbfit_pw";
		String database = "dbfit";
		
		try {
			tester.connect(dataSource, username, password, database);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println("\nInitialized class \"MySqlTest\" (2/2) \n");


		String statement = "select 'test' as x";
		
		//statement = "<table> <tr> <td>!Query</td> <td>select 'test' as x</td> </tr></table>";
		
		System.out.println("\n\nCalling execute for statement \n" + statement + "\n\n");
	
		
		
		Fixture f = tester.query(statement);

		List<DataRow> results = null;
		
		try  { 
			//f.doTable(new Parse("<table> <tr> <td>!Query</td> <td>select 'test' as x</td> </tr> <tr> <td colspan=\"2\">x</td> </tr> <tr> <td colspan=\"2\">test</td> </tr> </table>"));
			Parse goal = new Parse("<table> <tr><td></td></tr> <tr> <td>x</td> </tr> <tr> <td>test</td> </tr> </table>");
			f.doTable(goal);
			
			System.out.println("\n\n----------------------------------------\n");
			
			System.out.println("leader:   " + goal.parts.more.leaf().leader);
			System.out.println("tag:      " + goal.parts.more.leaf().tag);
			System.out.println("body:     " + goal.parts.more.leaf().body);
			System.out.println("trailer:  " + goal.parts.more.leaf().trailer);
			System.out.println("end:      " + goal.parts.more.leaf().end);
			
			System.out.println("more:     " + goal.parts.more.leaf().more);
			System.out.println("parts:    " + goal.parts.more.leaf().parts);

			System.out.println("\n----------------------------------------\n\n");

			results = ((Query) f).getDataTable().getUnprocessedRows();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		// counts
		System.out.println("* COUNTS: \n\t" + f.counts());
		// print result of query
		if (results != null)  {
			System.out.println("* Displaying " + results.size() + " row(s)");
			for (int i = 0; i < results.size(); i++) {
				System.out.println("\t" + results.get(i));
			}
		}
		
		
		// close connection
		if (tester != null)
			try {
				tester.close();
			} catch (Exception e) {}
	}

}

