package sqlchecker.io;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

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
	
	

	/**
	 * Checks, if the SQL statement is a Function or procedure, or 
	 * something else
	 * @param sql The SQL statement which should be checked
	 * @return 0, if the SQL statement is a function, 
	 * 1 if it is a procedure, 
	 * 2 if it is neither of them
	 */
	public static int isSQLFunction(String sql) {
		// this is a safer isFunction check!
		
		sql = sql.toLowerCase();
		String sqlHead = sql.substring(0, sql.indexOf("(")).trim();
		System.out.println("sqlHead=\"" + sqlHead + "\"");
		String[] sqlHeadTokens = sqlHead.split(" ");
		boolean isFunction = sqlHeadTokens[sqlHeadTokens.length - 2].equals("function");
		boolean isProcedure = sqlHeadTokens[sqlHeadTokens.length - 2].equals("procedure");
		
		int status = -1;
		
		if (isFunction) {
			// function
			status = 0;
			System.out.println("Marked as \"FUNCTION\"");
		} else if (isProcedure) {
			// procedure
			status = 1;
			System.out.println("Marked as \"PROCEDURE\"");
		} else {
			// neither function, nor procedure
			status = 2;
			System.out.println("Marked as \"OTHER\"");
		}
		return status;
	}

	
	public static String parseCallableHeader(String sql) {
		
		// see https://dev.mysql.com/doc/refman/5.0/en/create-procedure.html
		
		System.out.println("INPUT: \n" + sql);

		String sqlNew = sql.toLowerCase();
		
		boolean isFunction = (isSQLFunction(sql) == 0);
		/*
		// this is a safer isFunction check!
		String sqlHead = sql.substring(0, sql.indexOf(")"));
		String[] sqlHeadTokens = sqlHead.split(" ");
		boolean isFunction = sqlHeadTokens[sqlHeadTokens.length - 2].equals("function");
		*/
		
		// boolean isFunction = sql.contains(" function ");
		System.out.println("isFunction=" + isFunction);
		
		int headerStartIdx = -1;
		int headerEndIdx = sqlNew.length();
		if (isFunction) {
			headerStartIdx = sqlNew.indexOf(" function ") + " function ".length();
			// i.e. RETURNS int(8)
			// returns is also part of a correct mysql function definition
			headerEndIdx = sqlNew.indexOf("returns "); 
		} else {
			headerStartIdx = sqlNew.indexOf(" procedure ") + " procedure ".length();
			// check for enclosing brackets!
			headerEndIdx = sqlNew.indexOf("(") + 1;
			int count = 1;
			System.out.println("[ " + headerEndIdx + " >" + sql.substring(0, headerEndIdx));
			while (count != 0) {
				if (headerEndIdx == sql.length()) {
					System.out.println("Reached EndOfLine, possibly malformed query!");
					break;
				}
				if (sqlNew.charAt(headerEndIdx) == ')') count--;
				if (sqlNew.charAt(headerEndIdx) == '(') count++;
				headerEndIdx++;
			}
			System.out.println("[ " + headerEndIdx + " >" + sqlNew.substring(0, headerEndIdx));
			
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
			// charAt is a constant time operation!
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

	
	
	/**
	 * Searches for all the files in the given directory and all of
	 * its sub-directories. Note: This function uses features which
	 * were introduced in Java 8/1.8 <br>
	 * @see https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html <br>
	 * @see https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html <br>
	 * @see https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#walk-java.nio.file.Path-java.nio.file.FileVisitOption...- <br>
	 * @see https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#filter-java.util.function.Predicate-
	 * @param path The path which will be the root node of the file
	 * tree
	 * @return A list of all non-folder files in the given directory
	 * and all of its sub-directories. This function does not guarantee
	 * any order of the returned list.
	 */
	public static ArrayList<File> fetchFiles(String path) {
		ArrayList<Path> pathList = new ArrayList<Path>();
		
		// search for all files
		Predicate<Path> isDir = Files::isDirectory;
		try {
			Files.walk(Paths.get(path)).filter(isDir.negate()).forEach(pathList::add);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
		// convert
		ArrayList<File> fileList = new ArrayList<File>();
		for (int i = 0; i < pathList.size(); i++) {
			fileList.add(pathList.get(i).toFile());
		}
		
		return fileList;
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
		String[] tests = new String[]{"CREATE PROCEDURE TESTProcUC() BEGIN SELECT bezeichnung FROM produkte; END|",
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
				+ "return c + d;",
				"create DEFINER= Max function sumab(a decimal(6, 2), b decimal(6, 2)) returns decimal(6, 4) deterministic return a + b;"};
		
		for (String t : tests) {
			IOUtil.parseCallableHeader(t);
			System.out.println("\n");
		}
		
		System.out.println("\n\nHeader Token parse test");
		
		tests = new String[]{"sumab(a decimal(6, 2), b decimal(6, 2))",
				"CalcLength(IN name varchar(100), OUT STRLength int)",
				"testproc()",
				"blabla( v1 kappa(a,b) , OUT c cy(x,3), inout bigint(3))"};
		
		for (String t : tests) {
			String[] tmp = IOUtil.getHeaderTokens(t);
			for (String s : tmp)
				System.out.print(s + "|");
			System.out.println("\n");
		}
		
		
		// test fetchFile
		final String path = "data/";
		ArrayList<File> allFiles = IOUtil.fetchFiles(path);//new ArrayList<Path>();
		
		System.out.println("Fetching files from \"" + path + "\"");
		System.out.println(allFiles.size() + " file(s) found:");
		
		for (int i = 0; i < allFiles.size(); i++) {
			File f = allFiles.get(i);
			System.out.println("> " + f.getPath());
		}
		
	}
	
}
