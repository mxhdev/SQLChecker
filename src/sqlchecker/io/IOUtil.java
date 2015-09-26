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
	
	private static String[] rElems = new String[0];
	
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
	
	
	public static void printParse(Parse p) {
		System.out.println("--- printParse - start ---");
		// printParse(p, 1);
		// printParseAt(p);
		
		storage = "";
		printParseStr(p, 0);
		System.out.println(storage);
		
		// TODO: omit some fields/not all the data is needed => improved performance
		
		/*
		rElems = new String[15];
		for (int i = 0; i < 15; i++) {
			rElems[i] = "";
		}


		 // how does parse work? I dont know 
		printParseStor(p, 1);
		
		for (int i = 0; i < 15; i++) {
			System.out.println("** " + (i+1));
			System.out.println(rElems[i]);
		}
		*/
		System.out.println("--- printParse - end ---");
		
	}
	


	private static void printParseStr(Parse p, int iter) {
		
		// init tis 
		storage += "[L]" + p.leader + "\n";
		storage += "[Tag]" + p.tag + "\n";

		if (p.parts != null) {
			printParseStr(p.parts, iter++);
		} else {
			storage += "[B]" + p.body + "\n";
			// System.out.println("[" + iter + "] body \n\t" + p.body);
		}
		
		storage += "[E]" + p.end + "\n";
		// System.out.println("[" + iter + "] end \n\t" + p.end);
		
		if (p.more != null) {
			printParseStr(p.more, iter++);
		} else {
			storage += "[Tr]" + p.trailer + "\n";
			// System.out.println("[" + iter + "] trailer \n\t" + p.trailer);
		}

	}
	
	
	
	private static void printParseStor(Parse p, int iter) {

		// init tis 
		rElems[iter] += "[L]" + p.leader + "\n";
		rElems[iter] += "[Tag]" + p.tag + "\n";

		if (p.parts != null) {
			printParseStor(p.parts, iter++);
		} else {
			rElems[iter] += "[B]" + p.body + "\n";
			// System.out.println("[" + iter + "] body \n\t" + p.body);
		}
		
		rElems[iter] += "[E]" + p.end + "\n";
		// System.out.println("[" + iter + "] end \n\t" + p.end);
		
		if (p.more != null) {
			printParseStor(p.more, iter++);
		} else {
			rElems[iter] += "[Tr]" + p.trailer + "\n";
			// System.out.println("[" + iter + "] trailer \n\t" + p.trailer);
		}

	}
	


	
	private static void printParse(Parse p, int iter) {
		System.out.println("** [" + iter + "] **");
		System.out.println("[" + iter + "] leader \n\t" + p.leader);
		System.out.println("[" + iter + "] tag \n\t" + p.tag);

		if (p.parts != null) {
			printParse(p.parts, iter++);
		} else {
			System.out.println("[" + iter + "] body \n\t" + p.body);
		}
		
		System.out.println("[" + iter + "] end \n\t" + p.end);
		
		if (p.more != null) {
			printParse(p.more, iter++);
		} else {
			System.out.println("[" + iter + "] trailer \n\t" + p.trailer);
		}

		
		
	}
	
	
}
