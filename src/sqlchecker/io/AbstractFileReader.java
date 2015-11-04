package sqlchecker.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Abstract file reader class which supports functions which
 * are called before, after and white reading
 * 
 * @author Max Hofmann
 *
 */
public abstract class AbstractFileReader {

	/**
	 * (Relative) path to the file which should be read
	 */
	private String fpath;
	
	
	/**
	 * Create class instance and store the given path
	 * @param path
	 */
	public AbstractFileReader(String path) {
		this.fpath = path;
	}
	
	
	
	/**
	 * Reads a file. For each line, this function does the
	 * following: <br>
	 * - Trim the line (as String) <br>
	 * - If the line is not empty: <br> 
	 * 	Call onReadLine()
	 * @throws IOException
	 */
	private void readFile() throws IOException {
		
		BufferedReader bf = null;
		FileInputStream fs = null;
		
		String line = "";
		
		try {
			fs = new FileInputStream(fpath);
			bf = new BufferedReader(new InputStreamReader(fs));
			
			while ((line = bf.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty()) onReadLine(line);
			}
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (fs != null) fs.close();
			if (bf != null) bf.close();
		}
	}
	
	
	
	/**
	 * Calls the readFile function. Before calling this function,
	 * this method will call beforeReading(FILEPATH). After reading
	 * the file, this method will call afterReading(FPATH). This function
	 * will do nothing if the given file does not exist
	 */
	public void loadFile() {

		// only read the file if it exists!
		if ( (new File(fpath)).exists() ) {
			
			beforeReading(fpath);
			
			try {
				readFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			afterReading(fpath);
		
		} else {
			System.out.println("WARNING: [AbstractFileReader] File \"" + fpath + "\" does not exist!");
		}
	}
	
	
	/**
	 * @return The path of the file which will be read or was already read
	 */
	public String getFilePath() {
		return this.fpath;
	}
	
	/**
	 * Gets called for every non-empty line which is read
	 * @param line The currently read line
	 */
	public abstract void onReadLine(String line);
	
	/**
	 * Gets called before the reading process starts
	 * @param pathToFile Path of the file which will be read
	 */
	public abstract void beforeReading(String pathToFile);
	
	/**
	 * Gets called after the reading process ended
	 * @param pathToFile Path of the file which was read
	 */
	public abstract void afterReading(String pathToFile);
	
}
