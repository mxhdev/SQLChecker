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
	 * @return The index of this tag in the IOUtil.tags array,
	 * -1 if the current line is no (known) tag. This might be because 
	 * of a typo or because the given line is part of a SQL statement
	 */
	public static int getTagPos(String line) {
		final String TAG_PREFIX = "/*";
		final String TAG_SUFFIX = "*/";
		
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