package sqlchecker.io;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import fit.Parse;


/**
 * General I/O Utility class. This class supports various static
 * utility functions. This class also stores the tags which were
 * parsed by the SolutionReader class. This field is required for
 * parsing a correct mapping by using the SubmissionReader class.
 * 
 * @author Max Hofmann
 *
 */
public class IOUtil {

	
	
	/**
	 * Default connection properties, in case something went wrong 
	 * at parsing those
	 */
	public static final String[] DEFAULT_PROPS = new String[]{"localhost", "root", "", "dbfit"};
	
	/**
	 * Prefix of a tag
	 */
	public static final String TAG_PREFIX = "/*";
	
	/**
	 * Suffix of a tag 
	 */
	public static final String TAG_SUFFIX = "*/";

	/**
	 * Delimiter used in all CSV files of this application
	 */
	public static final String CSV_DELIMITER = ";";
	
	/**
	 * Temporary storage for the results of a submission
	 */
	private static String storage = "";
	
	/**
	 * A static list of the currently valid tags
	 */
	public static String[] tags = new String[0];
	
	
	/**
	 * Checks if a tag was found
	 * @param line The line which should be checked
	 * @return The index of this tag in the IOUtil.tags array,
	 * -1 if the current line is no (known) tag. This might be because 
	 * of a typo or because the given line is part of a SQL statement
	 */
	public static int getTagPos(String line) {
		
		// sample tags: /*a2b*/, /* a2B*/
		
		line = line.replace(" ", "");
		
		// either tag prefix or suffix is incorrect
		if (((!line.startsWith(TAG_PREFIX)) || (!line.endsWith(TAG_SUFFIX))))
			return -1;
		
		for (int i = 0; i < tags.length; i++) {
			String tag = tags[i];
			if (line.equalsIgnoreCase(TAG_PREFIX + tag + TAG_SUFFIX))
				return i;
		}
		
		// no tags were matching
		return -1;
	}
	
	
	/**
	 * 
	 * @param line The line in which this method should look
	 * for a tag
	 * @return The tag which is surrounded by the correct 
	 * prefix and suffix, null if this line neither starts 
	 * with the correct prefix not ends with the correct suffix
	 */
	public static String getTag(String line) {
		// sample tags: /*a2b*/, /*a2B*/

		// either tag prefix or suffix is incorrect
		if (((!line.startsWith(TAG_PREFIX)) || (!line.endsWith(TAG_SUFFIX))))
			return null;
		
		// take the string surrounded by the prefix and suffix
		line = line.substring(
				line.indexOf(TAG_PREFIX) + TAG_PREFIX.length(), 
				line.indexOf(TAG_SUFFIX));
		
		return line;
	}
	
	
	/**
	 * This function is part of the initialization routine of this
	 * class and generates the header/first line of the result csv file
	 * @return The first line of the csv file as String
	 */
	public static String generateCSVHeader(ArrayList<String> tagMap) {
		String csvHead = "Submission" + IOUtil.CSV_DELIMITER
				+ "Right" + IOUtil.CSV_DELIMITER
				+ "Wrong" + IOUtil.CSV_DELIMITER
				+ "Ignored" + IOUtil.CSV_DELIMITER
				+ "Exceptions";
		
		// count amount of queries/statements
		// csv.split(IOUtil.CSV_DELIMITER).length - 5;
		int qnum = tagMap.size() - 2;
		for (int j = 0; j < qnum; j++) {
			csvHead += IOUtil.CSV_DELIMITER + "Query" + (j+1);
			// Check if there is a tag for this statement
			// This means that this statement corresponds to a task
			// of the assignment
			if (!tagMap.get(j + 2).isEmpty()) {
				csvHead += " (" + tagMap.get(j + 2) + ")";
			}
		}
		
		return csvHead; 
	}
	
	
	/**
	 * Replaces each tag with it's respective SQL query
	 * @param solutionHTML The html code in which the replacements
	 * should be done
	 * @param mapping The mapping (tag->SQL)
	 * @return HTML code in which all tags were replaced with it's 
	 * respective SQL
	 */
	public static String applyMapping(String solutionHTML, ArrayList<String[]> mapping) {
		String result = solutionHTML;
		
		for (int i = 0; i < mapping.size(); i++) {
			
			String[] m = mapping.get(i);
			String tag = TAG_PREFIX + m[0] + TAG_SUFFIX;
			
			// replace placeholder with actual SQL query
			result = result.replace(tag, m[1]);
		}
		
		return result;
	}
	
	
	public static String getParseResult(Parse p) {

		// get result as string
		storage = "";
		printParseStr(p, 0);
		
		return storage;
		
	}
	

	/**
	 * Stores the annotated parse String in a class
	 * variable. This makes sure that the output is stored
	 * in the correct order. <br>
	 * This function was adapted from the Parse.print() function
	 * in the fitnesse github repository
	 * @param p Parse object which should be stored
	 * @param iter Iteration counter, start at 0
	 * @see https://github.com/unclebob/fitnesse/blob/master/src/fit/Parse.java
	 */
	private static void printParseStr(Parse p, int iter) {
		
		// init this 
		storage += p.leader; //"[L]" + p.leader; // + "\n";
		storage += p.tag; //"[Tag]" + p.tag; // + "\n";

		if (p.parts != null) {
			printParseStr(p.parts, iter++);
		} else {
			storage += p.body; // "[B]" + p.body; // + "\n";
			// System.out.println("[" + iter + "] body \n\t" + p.body);
		}
		
		storage += p.end; // "[E]" + p.end; // + "\n";
		// System.out.println("[" + iter + "] end \n\t" + p.end);
		
		if (p.more != null) {
			printParseStr(p.more, iter++);
		} else {
			storage += p.trailer; //"[Tr]" + p.trailer; // + "\n";
			// System.out.println("[" + iter + "] trailer \n\t" + p.trailer);
		}

	}
	
	
	public static String parseCallableHeader(String sql) {
		
		// see https://dev.mysql.com/doc/refman/5.0/en/create-procedure.html
		
		System.out.println("INPUT: \n" + sql);
		
		sql = sql.toLowerCase();
		
		boolean isFunction = sql.contains(" function ");
		System.out.println("isFunction=" + isFunction);
		
		int headerStartIdx = -1;
		int headerEndIdx = sql.length();
		if (isFunction) {
			headerStartIdx = sql.indexOf(" function ") + " function ".length();
			// i.e. RETURNS int(8)
			// returns is also part of a correct mysql function definition
			headerEndIdx = sql.indexOf(" returns "); 
		} else {
			headerStartIdx = sql.indexOf(" procedure ") + " procedure ".length();
			// check for enclosing brackets!
			headerEndIdx = sql.indexOf("(") + 1;
			int count = 1;
			System.out.println("[ " + headerEndIdx + " >" + sql.substring(0, headerEndIdx));
			while (count != 0) {
				if (headerEndIdx == sql.length()) {
					System.out.println("Reached EndOfLine, possibly malformed query!");
					break;
				}
				if (sql.charAt(headerEndIdx) == ')') count--;
				if (sql.charAt(headerEndIdx) == '(') count++;
				headerEndIdx++;
			}
			System.out.println("[ " + headerEndIdx + " >" + sql.substring(0, headerEndIdx));
			
		}
		System.out.println("s=" + headerStartIdx + ", e=" + headerEndIdx);
		String header = sql.substring(headerStartIdx, headerEndIdx);
		
		header = header.replace("\n", "").trim();
		System.out.println("Header(1)=\"" + header + "\"");
		
		return header;
		
	}
	
	
	
	public static String[] getHeaderTokens(String header) {
		//Sample Input: functionName({params comma separated})
		
		System.out.println("INPUT: " + header);
		
		int counter = 0;
		ArrayList<String> tokens = new ArrayList<String>();

		// method name
		String name = header.substring(0, header.indexOf('('));
		System.out.println("NAME=\"" + name + "\"");
		tokens.add(name);
		
		header = header.substring(header.indexOf('(') + 1, header.lastIndexOf(')'));
		System.out.println("INPUT-2: \"" + header + "\"");
		
		for (int i = 0; i < header.length(); i++) {
			char c = header.charAt(i);
			// check the character
			if (c == '(') {
				counter++;
			} else if (c == ')') {
				counter--;
			} else if (c == ',') { 
				// new token!
				if (counter == 0) {
					tokens.add("");
				}
			} 
			
			// if ( ((c == ',') && (counter != 0)) || (c != ","))
			// if ( (c != ',') || (counter != 0) )
			if ( !((c == ',') && (counter == 0)) ) {
				// if the list only stores a name
				if (tokens.size() < 2) tokens.add("");
				// some other char, add it to the top of the list
				String t = tokens.get(tokens.size() - 1);
				t += c;
				tokens.set(tokens.size() - 1, t);
			}
			
		}

		System.out.println("TOKENS=" + tokens.size());
		String[] tokenArray = (tokens.toArray(new String[tokens.size()]));
		for (int i = 0; i < tokens.size(); i++)
			tokenArray[i] = tokenArray[i].trim();
		
		return tokenArray;
	}


	
	public static void main(String[] args) {
		System.out.println("Table test");
		String test = "<table> x </table> yy <table> x2 </table>";
		String[] res = test.split("<table>");
		for(String r : res) {
			System.out.println("-s-");
			System.out.println(r);
			System.out.println("-e-");
		}
		
		System.out.println("\n\nFunction/Procedure parse test");
		String[] tests = new String[]{"CREATE PROCEDURE testproc() BEGIN SELECT bezeichnung FROM produkte; END|",
				"CREATE PROCEDURE CalcLength(IN name varchar(100), OUT strlength int) set strlength =length(name);",
				"CREATE PROCEDURE CalcLength(IN name varchar(100)"
				+ ", OUT strlength int) "
				+ "set strlength =length(name);",
				"CREATE PROCEDURE CalcLength(IN name varchar(100)\n"
				+ ", OUT strlength int) \n"
				+ "set strlength =length(name);",
				
				"CREATE FUNCTION filterProducts (gps INT) returns TEXT begin declare bez TEXT; set bez = (select bezeichnung from produkte where preis = gps); return bez;end;",
				"create function sumab(a decimal(6, 2), b decimal(6, 2)) returns decimal(6, 4) deterministic return a + b;",
				"create function sumab(a decimal(6, 2), b decimal(6, 2)) "
				+ "returns decimal(6, 4) "
				+ "deterministic "
				+ "return a + b;",
				"create function sumab(c decimal(6, 2), d decimal(6, 2)) \n "
				+ "returns decimal(6, 4) deterministic \n"
				+ "return c + d;"};
		
		for (String t : tests) {
			IOUtil.parseCallableHeader(t);
			System.out.println("\n");
		}
		
		System.out.println("\n\nHeader Token parse test");
		
		tests = new String[]{"sumab(a decimal(6, 2), b decimal(6, 2))",
				"CalcLength(IN name varchar(100), OUT strlength int)",
				"testproc()",
				"blabla( v1 kappa(a,b) , OUT c cy(x,3), inout bigint(3))"};
		
		for (String t : tests) {
			String[] tmp = IOUtil.getHeaderTokens(t);
			for (String s : tmp)
				System.out.print(s + "|");
			System.out.println("\n");
		}
		
		
	}
	
}
