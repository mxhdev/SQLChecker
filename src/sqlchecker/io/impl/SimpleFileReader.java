package sqlchecker.io.impl;

import sqlchecker.io.AbstractFileReader;

public class SimpleFileReader extends AbstractFileReader {

	private String fContent = "";
	
	public SimpleFileReader(String path) {
		super(path);
	}

	@Override
	protected void onReadLine(String line) {
		fContent += line + "\n";
	}

	@Override
	protected void beforeReading(String pathToFile) {
		fContent = "";
	}

	@Override
	protected void afterReading(String pathToFile) {}
	
	
	/**
	 * Only call this function after reading a file
	 * @return The content of the file as a String, lines are
	 * separated by \n
	 */
	public String getFileContent() {
		return fContent;
	}

}
