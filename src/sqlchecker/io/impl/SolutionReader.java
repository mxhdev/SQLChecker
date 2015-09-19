package sqlchecker.io.impl;

import java.util.Arrays;

import sqlchecker.io.AbstractFileReader;
import sqlchecker.io.IOUtil;


public class SolutionReader extends AbstractFileReader {

	private StringBuilder htmlCode = new StringBuilder("");
	
	private final String CONN_PREFIX = "<table> <tr> <td>Connect</td> <td>";
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
				String[] props = line.split("</td> <td>");
			}
			
		}
	}

	@Override
	public void onFileStart(String pathToFile) {
		// empty html string
		htmlCode = new StringBuilder("");
		// default connection properties
		connProps = DEFAULT_PROPS.clone();
	}

	@Override
	public void onFileEnd(String pathToFile) {
		// System.out.println(htmlCode.toString());
	}

	
	/**
	 * @return The html code inside the given file
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
