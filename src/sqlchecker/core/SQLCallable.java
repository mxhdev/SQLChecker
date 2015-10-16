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
	 * @return True iff at least one of the sp/function parameters
	 * has the type OUT or INOUT
	 */
	public boolean isOutOrInout() {
		
		for (int i = 0; i < args.size(); i++) {
			// For each parameter...
			if (args.get(i).direction.isOutOrInout())
				return true;
		}
		
		// No out/inout parameters found
		return false;
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
	
	
	/**
	 * Generates a result header for the arguments of this
	 * callable object
	 * @return A string which contains all arguments (in order)
	 * INOUT arguments occur twice: Once as an IN argument and
	 * once as an OUT argument. The last element stores the function
	 * output and is named "@"
	 */
	public String[] generateResultHeader() {
		ArrayList<String> cols = new ArrayList<String>();
		
		for (int i = 0; i < args.size(); i++) {
			ParamDescriptor pd = args.get(i);
			// check for the type of this argument
			if (pd.direction == Direction.INPUT) {
				cols.add(pd.name);
			} else if (pd.direction == Direction.OUTPUT) {
				cols.add("@" + pd.name);
			} else if (pd.direction == Direction.INPUT_OUTPUT) {
				cols.add(pd.name);
				cols.add("@" + pd.name);
			}
		}

		// function return value
		if (isFunction()) cols.add("@");
		
		// convert to array!
		return (cols.toArray(new String[cols.size()]));
	}
	
	
	/**
	 * 
	 * @return The parameters defined in the definition of callable
	 */
	public ArrayList<ParamDescriptor> getParameters() {
		// avoid references
		ArrayList<ParamDescriptor> newArgs = new ArrayList<ParamDescriptor>();
		newArgs.addAll(args);
		
		return newArgs;
	}
	
	
	
	/**
	 * Parses the data given by the call of this function or stored 
	 * procedure
	 * @param call The call itself, this is the name, followed
	 * by the arguments in brackets. Variables (for OUT parameters)
	 * start with "@". Examples: <br>
	 * CalcLength("HelloWorld", @strlength), <br>
	 * PlusEins(15) <- The one argument of this function
	 * has the type INOUT <br>
	 * SumAB(5, 4) <- Two IN parameters, stored function which returns 9
	 * @return The data/arguments defined in the given call. Examples: <br>
	 * CalcLength("HelloWorld", @strlength) becomes ["HelloWorld", @strlength] <br>
	 * SumAB(5, 4) becomes [5, 4]
	 */
	public static String[] parseCallData(String call) {
		ArrayList<String> data = new ArrayList<String>();
		data.add("");
		
		// check for empty argument list
		if (call.indexOf(")") == call.indexOf("(") + 1)
			return (new String[0]);
		
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
	
	
	
	/**
	 * Generates all the required SQL SET statements for the INOUT
	 * parameters in this call. This function also replaces the
	 * INOUT data arguments with their appropriate variables. These
	 * are the variables which are used in the previously generated
	 * SQL SET statements.
	 * @param call The call which should be prepared for execution
	 * @return A list with all the preparing SQL statements.
	 * The 2nd last element in the list is the call itself with 
	 * correct variable names. The last element is a SELECT
	 * statement which queries the value of the INOUT and OUT
	 * variables which were used in the call
	 */
	public ArrayList<String> prepareInOutCall(String call) {
		// contains SQL set commands for appropriate arguments
		// ASSUMPTION!! DATA.length == args.length !!!!
		ArrayList<String> sqlsets = new ArrayList<String>();
		String[] data = SQLCallable.parseCallData(call);
		String newCall = this.name + "(";
		String sqlGetter = "SELECT ";
		int outputColumns = 0;
		
		for (int i = 0; i < args.size(); i++) {
			// for every argument
			ParamDescriptor pd = args.get(i);
			// Check if it an INOUT argument
			// Only those require to set some variables
			// before performing the call
			// Also generate a new call, which contains placeholders
			// for all INOUT arguments
			String colId = "";
			String tmpSQL = "";
			if (pd.direction == Direction.INPUT_OUTPUT) {
				String sql = "SET @" + pd.name + " = " + data[i];
				sqlsets.add(sql);
				// replace current data value with variable name
				colId = "@" + pd.name;
				data[i] = colId;
			} else {
				colId = data[i];
			}
			// Deal with separators
			if (i > 0) {
				colId = ", " + colId;
			}
			if (outputColumns > 0) {
				tmpSQL = ", ";
			}
			// Check if the algorithm has encountered a variable
			// Variable could either be just added (INOUT)
			// or been there before (OUT). Variables always start 
			// with @
			if (data[i].charAt(0) == '@') {
				tmpSQL += data[i];
				// append the just created part to the
				// SELECT statement
				sqlGetter += tmpSQL;
				outputColumns++;
			}
			// Apply variable replacement in call
			newCall += colId;
		}
		
		// The 2nd last element is the call itself
		sqlsets.add("{ call " + newCall + ") }");
		
		// The last element is a select and produces the final
		// result set
		sqlsets.add(sqlGetter);
		
		// if there are no inout arguments, then the list will be empty
		return sqlsets;
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
				"Func2(\"'hello world!' x\", 'so\"e\', 3, '42')",
				"EmptyFunc1()",
				"EmptyFunc2(\"\")"};
		for (int i = 0; i < tests.length; i++) {
			String[] tmpArgs = SQLCallable.parseCallData(tests[i]);
			System.out.println("INPUT:\n\t" + tests[i]);
			System.out.println("OUTPUT:\n\t" + Arrays.toString(tmpArgs));
			System.out.println("TOKENS FOUND:\n\t" + tmpArgs.length);
			System.out.println("");
		}
		
		
	}

}
