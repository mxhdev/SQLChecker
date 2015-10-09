package sqlchecker.core;

import java.util.Arrays;

import sqlchecker.io.IOUtil;

public class SQLCallable {

	private String rawsql = "";
	private String name = "?";
	private String[] argumentNames = new String[0];
	
	
	public SQLCallable(String sql) {
		this.rawsql = sql;
		init();
	}
	
	private void init() {
		// store name

		// it is either a function or a procedure
		int type = IOUtil.isSQLFunction(rawsql);
		
		// add the name of the function/procedure
		String header = IOUtil.parseCallableHeader(rawsql);
		this.name = header.substring(0, header.indexOf("(")).trim();
		System.out.println("SQLCallable.name=\"" + name + "\"");
		
		// store argument names & count
		/*
		INPUT: CalcLength(IN name varchar(100), OUT STRLength int)
		NAME="CalcLength"
		INPUT-2: "IN name varchar(100), OUT STRLength int"
		TOKENS=3
		OUTPUT: CalcLength|IN name varchar(100)|OUT STRLength int|
		 */
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
		
	}
	
	public String getName() {
		return this.name;
	}

	public String getStatement() {
		return this.rawsql;
	}
	

	
	
	
	public static void main(String[] args) {
		String[] tests = new String[]{
				"CREATE FUNCTION filterProducts (gps INT) returns TEXT begin declare bez TEXT; set bez = (select bezeichnung from produkte where preis = gps); return bez;end;",
				"CREATE PROCEDURE TESTProcUC() BEGIN SELECT bezeichnung FROM produkte; END|",
				"CREATE PROCEDURE CalcLength(IN name varchar(100), OUT strlength int) set strlength =length(name);"};
		for (int i = 0; i < tests.length; i++) {
			System.out.println("\n\n- - - - - - - - - -\n");
			System.out.println("SQL:\n" + tests[i]);
			SQLCallable sqlc = new SQLCallable(tests[i]);
		}
	}

}
