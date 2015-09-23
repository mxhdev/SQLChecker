package sqlchecker.io.impl;

import java.util.Arrays;

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
	 * Prefix of the connection definition in the given file
	 */
	private final String CONN_PREFIX = "<table> <tr> <td>Connect</td> <td>";
	
	/**
	 * Suffix of the connection definition in the given file
	 */
	private final String CONN_SUFFIX = "</td> </tr> </table>";
	
	/**
	 * Connection properties parsed from the solution
	 */
	private String[] connProps = new String[4];
	
	/**
	 * Default connection properties, in case something went wrong 
	 * at parsing those
	 */
	private String[] DEFAULT_PROPS = new String[]{"localhost", "root","","dbfit"};
	
	
	/**
	 * Create a solution reader class. Stores the given path.
	 * @param path The (relative) path leading to the solution file
	 */
	public SolutionReader(String path) {
		super(path);
	}

	

	@Override
	public void onReadLine(String line) {
		if (line.startsWith("tags=")) {
			// parse tag-list and store it in IOUtil
			line = line.substring(line.indexOf("=") + 1).replace(" ", "");
			IOUtil.tags = line.split(",");
			// System.out.println("tag-array:\"" + Arrays.toString(IOUtil.tags) + "\"");
		} else {
			htmlCode.append("\n" + line);

			if (line.startsWith(CONN_PREFIX)) {
				// cut prefix & suffix
				line = line.substring(line.indexOf(CONN_PREFIX) + CONN_PREFIX.length());
				line = line.substring(0, line.indexOf(CONN_SUFFIX));
				// parse connect parameters
				this.connProps = line.split("</td> <td>").clone();
			}
			
		}
	}

	@Override
	public void beforeReading(String pathToFile) {
		// empty html string
		htmlCode = new StringBuilder("");
		// default connection properties
		connProps = DEFAULT_PROPS.clone();
	}

	@Override
	public void afterReading(String pathToFile) {
		// System.out.println(htmlCode.toString());
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


		
	}
}
