package sqlchecker;

import java.util.ArrayList;

import sqlchecker.test.DBFitTest;
import sqlchecker.test.impl.TestSelect;

public class TestExecuter {

	
	/*
	 * 
use dbfit;
create table Produkte (
pid int not null auto_increment primary key,
bezeichnung varchar(512) not null,
preis decimal(16, 2)
)

use dbfit;
insert into produkte(bezeichnung, preis)
values ('big pc', 1500), ('phone', 430);
	 */
	
	private ArrayList<DBFitTest> testCases = new ArrayList<DBFitTest>();
	
	/**
	 * Adds a test case to the test queue
	 * @param t A test case (DBFit implementation)
	 */
	public void addTest(DBFitTest t) {
		this.testCases.add(t);
	}
	
	
	/**
	 * Executes all queued-up tests and clears the queue at the end
	 */
	public void runTests() {
		for (int i = 0; i < testCases.size(); i++) {
			System.out.println("\n\n\n>>>>>  Test #" + (i+1) + "  <<<<<");
			testCases.get(i).runTest();
		}
		// clear list!
		testCases.clear();
	}
	
	
	public static void main(String[] args) {
		TestExecuter te = new TestExecuter();
		
		// amount of test cases
		int size = 5;
		
		// create simple select test cases
		ArrayList<String[]> testCases = new ArrayList<String[]>();
		
		/*
		 * define test cases (select)
		 */
		testCases.add(new String[]{"select 'test' as y"
				, "<table> <tr><td></td></tr> <tr> <td>x</td> </tr> <tr> <td>test</td> </tr> </table>"
				});
		
		testCases.add(new String[]{"select 'test' as x"
				, "<table> <tr><td></td></tr> <tr> <td>x</td> </tr> <tr> <td>test</td> </tr> </table>"
				});
		
		testCases.add(new String[]{"insert into Produkte values (3,'maus',15)"
				, "<table> "
				+ "<tr><td></td></tr> "
				+ "<tr> <td>bezeichnung</td> </tr> "
				+ "<tr> <td>big pc</td> </tr> "
				+ "<tr> <td>phone</td> </tr> "
				+ "</table>"
				});

		testCases.add(new String[]{"select bezeichnung, preis from Produkte"
				, "<table> "
				+ "<tr><td></td></tr> "
				+ "<tr> <td>bezeichnung</td> <td>preis</td> </tr>" 
				+ "<tr> <td>big pc</td> <td>1500</td> </tr> " // also works with .00
				+ "<tr> <td>phone</td> <td>430</td> </tr> "
				+ "</table>"
				});
		
		testCases.add(new String[]{"select sum(preis) from Produkte"
				, "<table> "
				+ "<tr><td></td></tr> "
				+ "<tr> <td>sum(preis)</td> </tr>" 
				+ "<tr> <td>1930</td> </tr> "
				+ "</table>"
				});
		
		
		// create all test cases
		for (int i = 0; i < size; i++) {
			String[] tcase = testCases.get(i);
			DBFitTest ts = new TestSelect(tcase[0], tcase[1]);
			te.addTest(ts);
		}
		
		// run tests
		te.runTests();
	}

}
