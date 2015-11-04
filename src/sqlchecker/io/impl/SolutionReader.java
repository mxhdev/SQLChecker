package sqlchecker.io.impl;

import java.util.ArrayList;

import sqlchecker.io.AbstractFileReader;
import sqlchecker.io.IOUtil;


/**
 * Reads a solution file and parses the connection properties.
 * 
 * @author Max Hofmann
 *
 */
public class SolutionReader extends AbstractFileReader {

	
	/**
	 * The content of the given file, as string
	 */
	private StringBuilder htmlCode = new StringBuilder("");
	
	/**
	 * Stores for each statement in the file, if it contains a tag
	 */
	private ArrayList<String> tagMap = new ArrayList<String>();
	
	/*
	 * 
	 * This skips the first two tables because those tables 
	 * define the SQL driver and the connection properties. Those
	 * tables will never be influenced by student submissions
	 */
	
	/**
	 * Prefix of the connection definition in the given file
	 */
	private final String CONN_PREFIX = "<table> <tr> <td>Connect</td> <td>";
	
	/**
	 * Suffix of the connection definition in the given file
	 */
	private final String CONN_SUFFIX = "</td> </tr> </table>";
	
	
	/**
	 * This tag indicates which tag marks the metadata
	 */
	public static final String METADATA_TAG = "metadata";
	
	
	/**
	 * Connection properties parsed from the solution
	 */
	private String[] connProps = new String[4];
	
	
	
	
	/**
	 * Create a solution reader class. Stores the given path.
	 * @param path The (relative) path leading to the solution file
	 */
	public SolutionReader(String path) {
		super(path);
	}

	

	@Override
	protected void onReadLine(String line) {
		if (line.startsWith("tags=")) {
			// parse tag-list and store it in IOUtil
			line = line.substring(line.indexOf("=") + 1).replace(" ", "");
			String[] rawTags = line.split(",");
			// add the meta data tag as the first element
			IOUtil.tags = new String[rawTags.length + 1];
			IOUtil.tags[0] = METADATA_TAG;
			for (int i = 0; i < rawTags.length; i++) {
				IOUtil.tags[i+1] = rawTags[i];
			}
			// System.out.println("tag-array:\"" + Arrays.toString(IOUtil.tags) + "\"");
		} else {
			htmlCode.append("\n" + line);
			
			if (line.startsWith(CONN_PREFIX)) {
				// cut prefix & suffix
				String connLine = line.substring(line.indexOf(CONN_PREFIX) + CONN_PREFIX.length());
				connLine = connLine.substring(0, connLine.indexOf(CONN_SUFFIX));
				// parse connect parameters
				this.connProps = connLine.split("</td> <td>").clone();
			}
			// build tag-map
			if (line.startsWith("<table>")) {
				tagMap.add("");
			} else {
				// transform the line before doing the check
				// this removes everything before and after the
				// tag prefix and suffix
				String tagLine = "";
				if (line.contains("/*") && line.contains("*/")) {
					tagLine = line.substring(line.indexOf("/*"));
					tagLine = tagLine.substring(0, tagLine.indexOf("*/") + 2);
					// check the index of this (possible) tag
					int idx = IOUtil.getTagPos(tagLine);
					if (idx >= 0) {
						tagMap.set(tagMap.size() - 1, IOUtil.tags[idx]);
					}
				}
				
			}
			
		}
	}

	@Override
	protected void beforeReading(String pathToFile) {
		// empty html string
		htmlCode = new StringBuilder("");
		// clear tag map
		tagMap.clear();
		// default connection properties
		connProps = IOUtil.DEFAULT_PROPS.clone();
	}

	@Override
	protected void afterReading(String pathToFile) {
		// System.out.println(htmlCode.toString());
	}

	/**
	 * @return The tag map. This is a List which has one element
	 * for every statement beginning with <table> in the solution file.
	 * For every statement, this list contains the tag corresponding to 
	 * this statement (or "") if there was no tag 
	 */
	public ArrayList<String> getTagMap() {
		return this.tagMap;
	}
	
	/**
	 * For receiving the content of the given file as a string
	 * @return The HTML code inside the given file
	 */
	public StringBuilder getHTML() {
		return this.htmlCode;
	}
	
	
	/**
	 * 
	 * @return Returns the parsed connection properties in
	 * the following order: <br>
	 * host (default:localhost) <br>
	 * dbUser (default:root) <br>
	 * dbUserPw (default:) <br>
	 * dbName (default:dbfit) <br>
	 * Those values are also used when the parser failed at parsing
	 * those parameters from the solution file
	 */
	public String[] getConnectionProperties() {
		return this.connProps.clone();
	}
	
	
	public static void main(String[] args) {
		String fpath = "data/assignment1/solution.txt";
		SolutionReader sr = new SolutionReader(fpath);
		sr.loadFile();
		ArrayList<String> tm = sr.getTagMap();
		for (int i = 0; i < tm.size(); i++) {
			System.out.println("> " + tm.get(i));
		}

				
	}
}
