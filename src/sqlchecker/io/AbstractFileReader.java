package sqlchecker.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class AbstractFileReader {

	private String fpath;
	
	public AbstractFileReader(String path) {
		this.fpath = path;
	}
	
	
	
	
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
	
	
	public void loadFile() {
		onFileStart(fpath);
		
		try {
			readFile();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		onFileEnd(fpath);
	}
	
	
	
	/**
	 * Checks if the current line marks a tag. Tags are used for mapping.
	 * Each Tag only occurs once per file. The query corresponding to a 
	 * tag is placed after the tag. A query starts in the line following 
	 * it's corresponding tag, it ends as soon as the file ends, or a 
	 * new tag is reached.
	 * @param line The line which should be checked
	 * @return True iff this line is a tag corresponding to a task.
	 * 
	 * @deprecated Not sure it this is the right place for this
	 * function
	 */
	protected boolean isTagX(String line) {
		line = line.trim();
		
		return false;
	}
	
	public abstract void onReadLine(String line);
	
	public abstract void onFileStart(String pathToFile);
	
	public abstract void onFileEnd(String pathToFile);
	
}
