package sqlchecker.io;

import java.util.ArrayList;
import java.util.Arrays;

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
	 * @return The index of this tag, -1 if the current line is no (known) 
	 * tag. This might be because of a typo or because the given
	 * line is part of a SQL statement
	 */
	public static int getTagPos(String line) {
		final String TAG_PREFIX = "/*";
		final String TAG_SUFFIX = "*/";
		
		// sample tag: /*a2b*/
		
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
		
		// generate csv
		String csv = getCSVLine(storage);
		return csv;
		
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
	
	
	private static String getCSVLine(String raw) {
		String csvLine = "";
		
		
		// post processing!
		String[] statements = raw.split("<table>");
		
		int start = 0;
		// skip first empty elem, connection and driver definition
		if (statements.length > 3) start = 3;

		String status = "";
		for (int i = start; i < statements.length; i++) {
			String tmp = statements[i];
			// parse status
			
			if (tmp.contains("class=\"pass\"")) {
				status += "p";
			}
			if (tmp.contains("class=\"ignore\"")) {
				status += "i";
			}
			if (tmp.contains("class=\"fail\"")) {
				status += "f";
			}
			if (tmp.contains("class=\"error\"")) {
				status += "e";
			}
			
			csvLine += status;
			// show status
			status = CSV_DELIMITER;
			
			/*
			//System.out.println("\t status=" + status);
			if (i > start) {
				status = CSV_DELIMITER + status; 
			}*/
		}
		return csvLine;
	}
	
	/*
	public static void main(String[] args) {
		String test = "<table> x </table> yy <table> x2 </table>";
		String[] res = test.split("<table>");
		for(String r : res) {
			System.out.println("-s-");
			System.out.println(r);
			System.out.println("-e-");
		}
	}
	*/
}
