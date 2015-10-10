package sqlchecker.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dbfit.environment.MySqlProcedureParametersParser;
import dbfit.environment.ParamDescriptor;
import dbfit.util.Direction;
import sqlchecker.io.IOUtil;

public class SQLCallable {

	private String rawsql = "";
	private String name = "?";
	/**
	 * 0=function,1=procedure,2=neither
	 */
	private int type = -1;
	//private String[] argumentNames = new String[0];
	
	ArrayList<ParamDescriptor> args = new ArrayList<ParamDescriptor>();
	
	
	public SQLCallable(String sql) {
		this.rawsql = sql;
		args = new ArrayList<ParamDescriptor>();
		// init all variables
		init();
	}
	
	private void init() {
		// store name

		// it is either a function or a procedure
		this.type = IOUtil.isSQLFunction(rawsql);
		
		// add the name of the function/procedure
		String header = IOUtil.parseCallableHeader(rawsql);
		this.name = header.substring(0, header.indexOf("(")).trim();
		System.out.println("SQLCallable.name=\"" + name + "\"");
		
		// store argument names & count
		/*
		 * taken from:
		 * https://github.com/dbfit/dbfit/blob/07ee62e316f5bada4f3ae7effc94f2e38e35547c/dbfit-java/mysql/src/main/java/dbfit/environment/MySqlProcedureParametersParser.java
		 * tests:
		 * https://github.com/dbfit/dbfit/blob/07ee62e316f5bada4f3ae7effc94f2e38e35547c/dbfit-java/mysql/src/test/java/dbfit/environment/MySqlProcedureParametersParserTest.java
		 */
		String paramStr = header.substring(header.indexOf("(")+1, header.length() - 1);
		System.out.println("pExp=\"" + paramStr + "\"");
		MySqlProcedureParametersParser parser = new MySqlProcedureParametersParser();
		List<ParamDescriptor> argsraw = parser.parseParameters(paramStr);
		for (int i = 0; i < argsraw.size(); i++) {
			ParamDescriptor pd = argsraw.get(i);
			System.out.println(">" + pd.name);
			System.out.println(">" + pd.type);
			System.out.println(">" + pd.direction);
			System.out.println(">" + pd.toString());
			System.out.println("> - - <");
		}
		// avoid references
		this.args.addAll(argsraw);
		/*
		CREATE PROCEDURE CalcLength(IN name varchar(100), OUT strlength int) set strlength =length(name);
		sqlHead="create procedure calclength"
		Marked as "PROCEDURE"
		isFunction=false
		[ 28 >CREATE PROCEDURE CalcLength(
		[ 68 >create procedure calclength(in name varchar(100), out strlength int)
		s=17, e=68
		Header(1)="CalcLength(IN name varchar(100), OUT strlength int)"
		SQLCallable.name="CalcLength"
		INPUT: CalcLength(IN name varchar(100), OUT strlength int)
		NAME="CalcLength"
		INPUT-2: "IN name varchar(100), OUT strlength int"
		TOKENS=3
		ARGS=[name, strlength]
		 */
		/*
		String[] tokens = IOUtil.getHeaderTokens(header);
		// each token, except the first one stores a parameter
		this.argumentNames = new String[tokens.length - 1];
		for (int i = 1; i < tokens.length; i++) {
			if (type == 0) {
				// function
				this.argumentNames[i-1] = tokens[i].split(" ")[0];
			} else if (type == 1) {
				// procedure
				this.argumentNames[i-1] = tokens[i].split(" ")[1];
			}
		}
		
		System.out.println("ARGS=" + Arrays.toString(argumentNames));
		*/
		
		
	}
	
	public String getName() {
		return this.name;
	}

	public String getStatement() {
		return this.rawsql;
	}
	
	/**
	 * 
	 * @return The type of this SQL statement. 
	 * 0=function, 1=procedure, 2=neither of them
	 */
	public int getType() {
		return this.type;
	}

	public boolean isFunction() {
		return (this.type == 0);
	}
	
	public boolean isProcedure() {
		return (this.type == 1);
	}
	
	/**
	 * 
	 * @return True iff one of the arguments has the direction 
	 * OUT or INOUT
	 */
	public boolean hasOutParameter() {
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).direction.isOutOrInout())
				return true;
		}
		return false;
	}
	
	
	public ArrayList<ParamDescriptor> getParameters() {
		// avoid references
		ArrayList<ParamDescriptor> newArgs = new ArrayList<ParamDescriptor>();
		newArgs.addAll(args);
		
		return newArgs;
	}
	
	
	
	public static String[] parseCallData(String call) {
		ArrayList<String> data = new ArrayList<String>();
		data.add("");
		
		// X(a,b) => a,b
		call = call.substring(call.indexOf("(") + 1, call.length()-1);
		System.out.println("stripped=\"" + call + "\"");
		
		char lastSeen = '?'; // ' or "" 
		int counter = 0;
		for (int i = 0; i < call.length(); i++) {
			char current = call.charAt(i);
			if (current == ',') {
				if (counter == 0) {
					// Separator detected!
					// Trim previous element
					String tmp = data.get(data.size() - 1);
					tmp = tmp.trim();
					data.set(data.size() - 1, tmp);
					// new element
					data.add("");
				} else {
					// the , is surrounded by " or '
					String tmp = data.get(data.size()-1);
					tmp += ",";
					data.set(data.size() - 1, tmp);
				}
			} else {
				// simply append the current char
				String tmp = data.get(data.size()-1);
				tmp += current;
				data.set(data.size() - 1, tmp);
				// update the counter
				if (counter != 0) {
					if (current == lastSeen) {
						// closing quotation
						counter = 0;
					}
				} else {
					if ( (current == '\"') || (current == '\'') )
					// beginning quotation
					counter = 1;
					lastSeen = current;
				}
			}
		}
		// trim last element
		String tmp = data.get(data.size() - 1);
		tmp = tmp.trim();
		data.set(data.size() - 1, tmp);
		
		return (data.toArray(new String[data.size()]));
	}
	
	
	public static void main(String[] args) {
		String[] tests = new String[]{
				"CREATE FUNCTION filterProducts (gps INT) returns TEXT begin declare bez TEXT; set bez = (select bezeichnung from produkte where preis = gps); return bez;end;",
				"CREATE FUNCTION filterProductsTEXT (gps VARCHAR(15)) returns TEXT begin declare bez TEXT; set bez = (select bezeichnung from produkte where preis = gps); return bez;end;",
				"CREATE PROCEDURE TESTProcUC() BEGIN SELECT bezeichnung FROM produkte; END|",
				"CREATE PROCEDURE CalcLength(IN name varchar(100), OUT strlength int) set strlength =length(name);",
				"CREATE PROCEDURE PlusEins(INOUT val int) set val = val + 1"};
		for (int i = 0; i < tests.length; i++) {
			System.out.println("\n\n- - - - - - - - - -\n");
			System.out.println("SQL:\n" + tests[i]);
			SQLCallable sqlc = new SQLCallable(tests[i]);
		}
		
		System.out.println("\n\n\nTest Data Parsing\n");
		
		tests = new String[]{"Procedure(5, 2, 612)", 
				"Func1(\"x\", 'e\', 3, '4')",
				"Func2(\"'hello world!' x\", 'so\"e\', 3, '42')"};
		for (int i = 0; i < tests.length; i++) {
			String[] tmpArgs = SQLCallable.parseCallData(tests[i]);
			System.out.println("INPUT:\n\t" + tests[i]);
			System.out.println("OUTPUT:\n\t" + Arrays.toString(tmpArgs));
			System.out.println("");
		}
		
		
	}

}