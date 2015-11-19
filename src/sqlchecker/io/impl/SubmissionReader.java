package sqlchecker.io.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * Does not contain static mappings after afterReading() finished
	 * executing
	 */
	private ArrayList<String[]> tagMappings = new ArrayList<String[]>();

	
	/**
	 * Equivalent to the tagMappings list, but contains only
	 * static mappings
	 */
	private ArrayList<String[]> staticMappings = new ArrayList<String[]>();
	
	/**
	 * List of student ids for every student who contributed to this submission
	 */
	private ArrayList<String> matrikelnummer;
	
	/**
	 * List of student names for every student who contributed to this submission
	 */
	private ArrayList<String> name;

	
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
			tagMappings.add(new String[]{tag, "", ""});
		}
	}



	@Override
	protected void onReadLine(String line) {
		// check if it is a task tag
		int tmpPos = IOUtil.getTagPos(line);
		if (tmpPos >= 0) {
			// new tag found!
			pos = tmpPos;
		} else if (line.equals(IOUtil.TAG_PREFIX + "static" + IOUtil.TAG_SUFFIX)) {
			// static tag, add an empty map
			tagMappings.add(new String[]{"static", "", ""});
			pos = tagMappings.size() - 1;
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
	protected void beforeReading(String pathToFile) {
		// init something (here: counter)
		pos = -1;
		// clear mappings
		for (int i = 0; i < tagMappings.size(); i++) {
			tagMappings.get(i)[1] = "";
		}
	}



	@Override
	protected void afterReading(String pathToFile) {
		
		for(int i = 0; i < tagMappings.size(); i++){
			String[] content = tagMappings.get(i);
			String allText = "";
			String comment = "";
			StringBuffer cleanedSQL = new StringBuffer();
			allText = content[1];
			//Get Text of exercise
			Pattern p = Pattern.compile("(?m)(?:#|--).*|(/\\*[\\w\\W]*?(?=\\*/)\\*/)");
			//find all comments which have the tags '#' or '--' or '/* Comment */'
			Matcher mComment = p.matcher(allText);
			
			while (mComment.find()) {
				comment = comment + mComment.group();
			}
			//Extract all the comments
			Matcher mSQL = p.matcher(allText);
			
			while(mSQL.find()){
				mSQL.appendReplacement(cleanedSQL, "");
			}
			//Extract the SQL Statement without the comments
			mSQL.appendTail(cleanedSQL);
			//System.out.println("[c]> " + Arrays.toString(content));
			content[1] = cleanedSQL.toString();
			content[2] = comment.replaceAll("(#|--|/\\*|\\*/)", "");
			//Delete all the comment tags
			tagMappings.set(i, content);
		}


		// fill staticMapping list
		staticMappings.clear();
		ArrayList<String[]> newMapping = new ArrayList<String[]>();
		for (int i = 0; i < tagMappings.size(); i++) {
			String[] map = tagMappings.get(i);
			// split into 2 lists (static / non-static)
			if (map[0].equals("static")) {
				staticMappings.add(map.clone());
			} else {
				newMapping.add(map.clone());
			}
		}
		
		// re-fill normal mapping list
		tagMappings.clear();
		tagMappings.addAll(newMapping);
	}

	
	/**
	 * For receiving the mapping which was extracted from the
	 * given file
	 * @return The tag-query mapping which was read from the given
	 * submission. This does not contain static tag mappings
	 */
	public ArrayList<String[]> getMapping() {
		return this.tagMappings;
	}
	
	
	/**
	 * Takes all the sql statements which are associated with a static
	 * tag
	 * @return
	 */
	public ArrayList<String> getStaticMapping() {
		ArrayList<String> sql = new ArrayList<String>();
		for (int i = 0; i < staticMappings.size(); i++) {
			// (tag,sql) tuples, we only want the sql part
			sql.add(staticMappings.get(i)[1]);
		}
		
		return sql;
	}
	
	
	public ArrayList<String> getMatrikelnummer() {
		return matrikelnummer;
	}



	public void setMatrikelnummer(ArrayList<String> matrikelnummer) {
		this.matrikelnummer = matrikelnummer;
	}



	public ArrayList<String> getName() {
		return name;
	}



	public void setName(ArrayList<String> name) {
		this.name = name;
	}

	
	
	public static String generateStaticHTML(String[] connProps, ArrayList<String> queries) {
		String dbfit = "";
		
		// generate header
		
		dbfit += IOUtil.generateDBFitHeader(connProps);


		// actual queries
		
		for (int i = 0; i < queries.size(); i++) {
			String sql = queries.get(i);
			
			// determine dbfit command
			String command = IOUtil.getDBFitCommand(sql);
			
			// generate HTML
			dbfit += "\n\n<table>"
					+ "\n\t<tr>"
					+ "\n\t\t<td>" + command + "</td>"
					+ "\n\t\t<td>" + sql + "</td>"
					+ "\n\t</tr>"
					+ "\n</table>\n\n";
			dbfit += "<table>"
					+ "\n\t<tr>"
					+ "\n\t\t<td>Commit</td>"
					+ "\n\t</tr>"
					+ "\n</table>\n\n";
		}
		
		return dbfit;
	}




	public static void main(String[] args) {
		String fpath = "data/assignment2/submissions/AA_Musterloesung.sql";
		fpath = "data/assignment3/submissions/utf8Bug.txt";
		String[] tags = new String[]{"1a", "1b", "1c", "1d"}; 
		SubmissionReader sr = new SubmissionReader(fpath, tags);
		sr.loadFile();
	}



	
}
