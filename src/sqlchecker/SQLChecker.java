package sqlchecker;

import java.util.Iterator;

import dbfit.MySqlTest;
import dbfit.fixture.Execute;
import dbfit.fixture.Query;
import fit.Fixture;
import fit.Parse;
import fitnesseMain.FitNesseMain;

public class SQLChecker {
	
	
	

	public static void main(String[] args) {


		
		
		// starts the server
		try {
			// FitNesseMain.main(new String[]{});
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println("started the server");
		
		
		/*
		 * tester
		 */
		
		
		MySqlTest tester = new MySqlTest();
		System.out.println("Initialized class \"MySqlTest\" (1/2)");
		
		String dataSource = "localhost";
		String username = "dbfit_user";
		String password = "dbfit_pw";
		String database = "dbfit";
		
		try {
			tester.connect(dataSource, username, password, database);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println("Initialized class \"MySqlTest\" (2/2)");
		
		String statement = "SELECT 'test' AS x";
		statement = "!|Query| select 'test' as x|\n|x|\n|test|";
		
		statement = "!|dbfit.MySqlTest|\n\n"
				+ "!|Connect|localhost|dbfit_user|dbfit_pw|dbfit|\n\n"
				+ "!|Query| select 'test' as x|\n"
				+ "|x|\n"
				+ "|test|\n";
		
		statement = "!|Query| select 'test' as x|\n|x|\n|test|";
		statement = "select 'test' as x";
		Fixture f = tester.query(statement);
		//f = tester.commit();
		//f = tester.query("select 'test' as x");
		
		System.out.println("Called execute for statement \"\n" + statement + "\n\"");
		
		System.out.println(f.toString());
		System.out.println(f.counts());
		System.out.println(tester.counts());
		try  { 
		System.out.println(((Query) f).getDataTable().toString());
		System.out.println(((Query) f).getDataTable().getColumns().get(0).getName());
		System.out.println(((Query) f).getDataTable().getUnprocessedRows().isEmpty());
		System.out.println(((Query) f).getDataTable().getUnprocessedRows().size());
		System.out.println(((Query) f).getDataTable().getUnprocessedRows().get(0));
		} catch (Exception e) {
		//System.out.println(((Execute) f).toString());
		e.printStackTrace(System.out);
		}
		System.out.println(f.summary.get("x"));
		
		String html = "";
		
		html += "\n<table> <tr> <td>dbfit.MySqlTest</td> </tr> </table>";
		html += "<table> <tr> <td>!Connect</td> <td>localhost</td> <td>dbfit_user</td> <td>dbfit_pw</td> <td>dbfit</td> </tr> </table>";
		html += "\n<table> <tr> <td>!Query</td> <td>select 'test' as x</td> </tr> <tr> <td colspan=\"2\">x</td> </tr> <tr> <td colspan=\"2\">test</td> </tr> </table>";
		
		System.out.println("html: \n" + html);
		// Options.OPTION_DEBUG_LOG, "true");
		//tester.setOption("OPTION_DEBUG_LOG", "true");
		try {
		Parse p = new Parse(html);
		
		System.out.println("at 0,0,0 " + p.at(0, 0, 0).text());
		System.out.println("arguments:");
		Parse parameters = p.parts.parts.more;
		for (; parameters != null; parameters = parameters.more) {
		      System.out.println(Parse.unescape(parameters.body));
		    }
		
		System.out.println("* parse-html(toString): \n" + p.toString());
		System.out.println("* parse-html(leader): \n" + p.leader);
		System.out.println("* parse-html(body): \n" + p.body);
		System.out.println("* parse-html(tag): \n" + p.tag);
		System.out.println("* parse-html(trailer): \n" + p.trailer);
		System.out.println("* parse-html(end): \n" + p.end);
		// tester.setFix
		tester.doTables(p);
		System.out.println("TABLEsCounts - " + tester.counts);
		Iterator<Object> it = tester.summary.values().iterator();
		//System.out.println("T_ARGS: " + );
		while (it.hasNext()) {
			System.out.println("> " + it.next());
		}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		


		
		if (tester != null)
			try {
				tester.close();
			} catch (Exception e) {}
	}

}
