package sqlchecker.io.impl;

import java.util.ArrayList;

import sqlchecker.io.AbstractFileReader;
import sqlchecker.io.IOUtil;



public class SubmissionReader extends AbstractFileReader {

	private int pos = -1;

	
	private ArrayList<String[]> tagMappings = new ArrayList<String[]>();
	
	
	public SubmissionReader(String path, String[] tags) {
		super(path);
		
		IOUtil.tags = tags.clone();
		
		// initialize mapping
		for (String tag : tags) {
			tagMappings.add(new String[]{tag, ""});
		}
	}



	@Override
	public void onReadLine(String line) {
		line = line.trim();
		// check if it is a task tag
		int tmpPos = IOUtil.getTagPos(line);
		if (tmpPos >= 0) {
			// new tag found!
			pos = tmpPos;
		} else {
			// use (already found) tag
			if (pos < 0) {
				// no tag found yet
				System.out.println("WARNING: Did not find initial tag for \"" + line + "\", trying again");
				return;
			}
			// append at proper position
			if (!tagMappings.get(pos)[1].isEmpty())
				line = "\n" + line;
			tagMappings.get(pos)[1] += line;

		} /*else {
			// empty line
		}*/
	}

	
	
	
	
	@Override
	public void onFileStart(String pathToFile) {
		// init something (here: counter)
		pos = -1;
		// clear mappings
		for (int i = 0; i < tagMappings.size(); i++) {
			tagMappings.get(i)[1] = "";
		}
	}



	@Override
	public void onFileEnd(String pathToFile) {
		 //reset everything / print stuff
		/*
		System.out.println("\n\t> - - - - - - - - - - <\n");
		
		for (int i = 0; i < tagMappings.size(); i++) {
			String[] mapping = tagMappings.get(i);
			System.out.println("> tag=\"" + mapping[0] + "\"");
			System.out.println(mapping[1]);
		}
		
		System.out.println("\n\t> - - - - - - - - - - <\n");
		*/
	}
	
	
	/**
	 * 
	 * @return The tag-query mapping which was read from the given
	 * submission
	 */
	public ArrayList<String[]> getMapping() {
		return this.tagMappings;
	}
	
	
	
	public static void main(String[] args) {
		String fpath = "data/assignment1/submissions/s1.sql";
		String[] tags = new String[]{"1a", "1b", "1c"}; 
		SubmissionReader sr = new SubmissionReader(fpath, tags);
		sr.loadFile();
		
		sr.loadFile();
	}



	
}
