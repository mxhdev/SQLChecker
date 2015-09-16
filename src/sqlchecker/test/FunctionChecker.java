package sqlchecker.test;



import java.sql.SQLException;
import java.util.List;

import dbfit.DatabaseTest;
import dbfit.MySqlTest;
import dbfit.api.DBEnvironment;
import dbfit.util.DataRow;
import fit.Fixture;
import fit.Parse;

public class FunctionChecker {
	
	
	

	public static void main(String[] args) {
		
		MySqlTest connection = new MySqlTest();
		System.out.println("\nInitialized class \"MySqlTest\" (1/2) \n");
		
		String dataSource = "localhost";
		String username = "dbfit_user";
		String password = "dbfit_pw";
		String database = "dbfit";
		
		try {
			connection.connect(dataSource, username, password, database);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println("\nInitialized class \"MySqlTest\" (2/2) \n");


		String createFunction = "CREATE FUNCTION filterProducts (gps INT) returns TEXT begin declare bez TEXT; set bez = (select bezeichnung from produkte where preis = gps); return bez;end;";
		System.out.println("\n\nCalling execute for statement \n" + createFunction + "\n\n");
		String dropFunction = "DROP FUNCTION IF EXISTS filterProducts;";
		String procName = "filterProducts";
		String para = "gps";
		String value = "1500";
		
		
		//connection.setParameter(para, value);
		Fixture dropFunc = connection.execute(dropFunction);
		Fixture createFunc = connection.execute(createFunction);
		Fixture f = connection.executeProcedure(procName);

		
		
		try  { 
			Parse goalDrop = new Parse("<table> <tr><td></td></tr> <tr> <td></td><td></td> </tr></table>");
			// Parse goalDrop = new Parse("<table> <tr><td></td></tr> <tr> <td>Execute</td><td>DROP FUNCTION IF EXISTS filterProducts;</td> </tr></table>");
			Parse goalCreateFunction = new Parse("<table> <tr><td></td></tr> <tr> <td></td><td></td> </tr></table>");
			// Parse goalCreateFunction = new Parse("<table> <tr><td></td></tr> <tr> <td>Execute</td><td>CREATE FUNCTION filterProducts (gps INT) returns TEXT begin declare bez TEXT; set bez = (select bezeichnung from produkte where preis = gps); return bez;end;</td> </tr></table>");
			Parse goalResult = new Parse("<table> <tr><td></td></tr> <tr> <td>gps</td><td>?</td> </tr> <tr> <td>1500</td><td>big pc</td> </tr> </table>");
			System.out.println("Drop existing Function 1");
			dropFunc.doTable(goalDrop);
			System.out.println("Creating function 1");
			createFunc.doTable(goalCreateFunction);
			System.out.println("Comparing results 1");
			f.doTable(goalResult);
			
			System.out.println("\n\n----------------------------------------\n");
			
			System.out.println("leader:   " + goalResult.parts.more.leaf().leader);
			System.out.println("tag:      " + goalResult.parts.more.leaf().tag);
			System.out.println("body:     " + goalResult.parts.more.leaf().body);
			System.out.println("trailer:  " + goalResult.parts.more.leaf().trailer);
			System.out.println("end:      " + goalResult.parts.more.leaf().end);
			
			System.out.println("more:     " + goalResult.parts.more.leaf().more);
			System.out.println("parts:    " + goalResult.parts.more.leaf().parts);

			System.out.println("\n----------------------------------------\n\n");

//			results = ((ExecuteProcedure) f).
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		
		// counts
		System.out.println("* COUNTS: \n\t" + f.counts());

		// close connection
		if (connection != null)
			try {
				connection.close();
			} catch (Exception e) {}
// ----------------------------------------------------------------------------------------
		//Hier neue Funktion 2
		MySqlTest conFun2 = new MySqlTest();
		
		try {
			conFun2.connect(dataSource, username, password, database);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		String createFunc2 = "create function sumab (a decimal(6, 2), b decimal(6, 2)) returns decimal(6, 2) deterministic return a + b;";
		System.out.println("\n\nCalling execute for statement \n" + createFunction + "\n\n");
		String dropFunc2 = "DROP FUNCTION IF EXISTS sumab;";
		String procName2 = "sumab";
		String para1 = "a";
		String value1 = "15.00";
		String para2 = "b";
		String value2 = "13.00";
		
		//conFun2.setParameter(para1, value1);
		//conFun2.setParameter(para2, value2);
		Fixture dropFunction2 = conFun2.execute(dropFunc2);
		Fixture createFunction2 = conFun2.execute(createFunc2);
		Fixture f2 = conFun2.executeProcedure(procName2);

		
		
		try  { 
			Parse goalDrop2 = new Parse("<table> <tr><td></td></tr> <tr> <td></td><td></td> </tr></table>");
			Parse goalCreateFunction2 = new Parse("<table> <tr><td></td></tr> <tr> <td></td><td></td> </tr></table>");
			Parse goalResult2 = new Parse("<table> <tr><td></td></tr> <tr> <td>a</td><td>b</td><td>?</td> </tr> <tr> <td>2</td><td>2</td><td>4</td> </tr> </table>");
			System.out.println("Drop existing Function 2");
			dropFunction2.doTable(goalDrop2);
			System.out.println("Creating function 2");
			createFunction2.doTable(goalCreateFunction2);
			System.out.println("Comparing results 2");
			f2.doTable(goalResult2);
			
			System.out.println("\n\n----------------------------------------\n");
			
			System.out.println("leader:   " + goalResult2.parts.more.leaf().leader);
			System.out.println("tag:      " + goalResult2.parts.more.leaf().tag);
			System.out.println("body:     " + goalResult2.parts.more.leaf().body);
			System.out.println("trailer:  " + goalResult2.parts.more.leaf().trailer);
			System.out.println("end:      " + goalResult2.parts.more.leaf().end);
			
			System.out.println("more:     " + goalResult2.parts.more.leaf().more);
			System.out.println("parts:    " + goalResult2.parts.more.leaf().parts);

			System.out.println("\n----------------------------------------\n\n");

//			results = ((ExecuteProcedure) f).
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		
		// counts
		System.out.println("* COUNTS: \n\t" + f2.counts());

		
		
		if (conFun2 != null)
			try {
				conFun2.close();
			} catch (Exception e) {}
	}

}

