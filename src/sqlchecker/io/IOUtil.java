package sqlchecker.io;

import java.util.ArrayList;


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
	
}
