package sqlchecker.io.impl;

import java.util.ArrayList;

import sqlchecker.io.AbstractFileReader;
import sqlchecker.io.IOUtil;

public class RawSolutionReader extends AbstractFileReader {

	
	
	private String defTag = "static";
	
	
	ArrayList<String[]> sqlMapping = new ArrayList<String[]>();
	
	
	public RawSolutionReader(String pathToFile) {
		super(pathToFile);
	}
	
	public RawSolutionReader(String pathToFile, String defaultTag) {
		super(pathToFile);
		// this tag marks non-student submitted (independent) sql
		this.defTag = defaultTag;
	}
	
	@Override
	public void onReadLine(String line) {
		String tag = IOUtil.getTag(line);
		// check if a tag was found
		if (tag != null) {
			sqlMapping.add(new String[]{tag, ""});
		} else {
			// append to last seen tag
			int idx = sqlMapping.size() - 1;
			
			if (idx < 0) {
				// sql without a tag
				System.out.println(" - - - - - ");
				System.out.println("[WARNING] RawSolutionReader: \n"
						+ "Initial tag missing. Every SQL statement "
						+ "has to have a tag before it is defined!\n");
				System.out.println("Problem at: \n\t" + line);
				System.out.println(" - - - - - \n\n");
				return;
			}
			
			// append the sql
			String[] tmp = sqlMapping.get(idx);
			// append a line break if required
			if (!tmp[1].isEmpty()) tmp[1] += "\n";
			// append sql and update the list
			tmp[1] += line;
			//?? sqlMapping.set(idx, tmp.clone());
			
		}
	}

	@Override
	public void beforeReading(String pathToFile) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterReading(String pathToFile) {
		for (int i = 0; i < sqlMapping.size(); i++) {
			String[] tmp = sqlMapping.get(i);
			System.out.println("[" + (i+1) + "] TAG=" + tmp[0]);
			System.out.println("[" + (i+1) + "] SQL=");
			System.out.println(tmp[1]);
		}
	}

	
	public static void main(String[] args) {
		String path = "data/raw.sql";
		RawSolutionReader rsr = new RawSolutionReader(path);
		rsr.loadFile();
	}

}
