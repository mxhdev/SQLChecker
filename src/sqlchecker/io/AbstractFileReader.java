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
		beforeReading(fpath);
		
		try {
			readFile();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		afterReading(fpath);
	}
	
	
	public abstract void onReadLine(String line);
	
	public abstract void beforeReading(String pathToFile);
	
	public abstract void afterReading(String pathToFile);
	
}
