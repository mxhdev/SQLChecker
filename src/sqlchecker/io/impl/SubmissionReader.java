package sqlchecker.io.impl;

import java.util.ArrayList;

import sqlchecker.io.AbstractFileReader;
import sqlchecker.io.IOUtil;


/**
 * Reads a student submission and extracts a TAG->SubmissionSQL mapping
 * 
 * @author Max Hofmann
 *
 */
public class SubmissionReader extends AbstractFileReader {

	
	/**
	 * Index of the currently active tag
	 */
	private int pos = -1;

	/**
	 * Stores the mapping (TAG-->SubmissionSQL)
	 */
	private ArrayList<String[]> tagMappings = new ArrayList<String[]>();
	private String matrikelnummer;
	private String name;
	
	
	
	/**
	 * Create a submission reader class, store the given path
	 * and init the mapping by using the given tags
	 * @param path Path of a submission file
	 * @param tags The tags which this class should look for
	 * in the given file
	 */
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
	public void beforeReading(String pathToFile) {
		// init something (here: counter)
		pos = -1;
		// clear mappings
		for (int i = 0; i < tagMappings.size(); i++) {
			tagMappings.get(i)[1] = "";
		}
	}



	@Override
	public void afterReading(String pathToFile) {
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
	 * For receiving the mapping which was extracted from the
	 * given file
	 * @return The tag-query mapping which was read from the given
	 * submission
	 */
	public ArrayList<String[]> getMapping() {
		return this.tagMappings;
	}
	
	
	
	
	public String getMatrikelnummer() {
		return matrikelnummer;
	}



	public void setMatrikelnummer(String matrikelnummer) {
		this.matrikelnummer = matrikelnummer;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public static void main(String[] args) {
		String fpath = "data/assignment1/submissions/s1.sql";
		String[] tags = new String[]{"1a", "1b", "1c"}; 
		SubmissionReader sr = new SubmissionReader(fpath, tags);
		sr.loadFile();
		
		sr.loadFile();
	}



	
}
