package sqlchecker.io.impl;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sqlchecker.core.CalculateSimilarity;
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
	 * List of student email addresses for every student who contributed to this submission
	 */
	private ArrayList<String> studentMails = new ArrayList<String>();
	
	private String formatError = "";
	
	private int tagMistakes = 0;
	
	
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
		String checkLine = line.replaceAll("[^\\x00-\\x7F]", "");
		int tmpPos = IOUtil.getTagPos(line);
		if (tmpPos >= 0) {
			// new tag found!
			pos = tmpPos;
			if(!checkLine.equals(IOUtil.TAG_PREFIX + IOUtil.tags[tmpPos] + IOUtil.TAG_SUFFIX)){
				tagMistakes++;
			}
		} else if (checkLine.equals(IOUtil.TAG_PREFIX + "static" + IOUtil.TAG_SUFFIX)) {
			// static tag, add an empty map
			tagMappings.add(new String[]{"static", "", ""});
			pos = tagMappings.size() - 1;
		} else {
			// use (already found) tag
			if (pos < 0) {
				// no tag found yet
				//System.out.println("WARNING: Did not find initial tag for \"" + line + "\", trying again");
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
			//Get Text(SQL and comments) of exercise
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

		// extract the proper SQL statements
		// omit the meta data tag mapping for authors
		float maxChange = 1;
		for (int i = 1; i < tagMappings.size(); i++) {
			String[] m = tagMappings.get(i);
			String newSQL = extractSQL(m[1]);
			// check for format error!
			if (!newSQL.equals(m[1])) {
				maxChange = Math.min(maxChange, CalculateSimilarity.similarityStringsCosine(newSQL, m[1]));
				m[1] = newSQL;
			}
			// apply changes!
			tagMappings.set(i, m);
		}
		this.formatError = String.valueOf((1 - maxChange));
		
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

	
	public static String extractSQL(String sql) {
		final String[] START_TRIGGERS = new String[]{"create", "select", "insert into", "alter", "update"};
		final String[] CREATE_TRIGGERS = new String[]{"function", "procedure", "view", "table"};
		
		int start = sql.length() - 1;
		int end = sql.length() - 1;
		
		String sqlOut = "";
		
		String rawSQL = sql.toLowerCase();
		
		boolean isCreate = false;
		boolean wasUpdated = false;
		for (String trigger : START_TRIGGERS) {
			// stop as soon as one of the triggers was found
			// the keyword at the top-most position will be chosen
			if (rawSQL.contains(trigger)) {
				int startNew = rawSQL.indexOf(trigger);
				// take the minimum
				if (startNew < start) {
					start = startNew;
					// check for create!
					isCreate = trigger.equals("create");
					wasUpdated = true;
				}
			}
		}
		
		// if no start was found, start at position 0
		if (!wasUpdated) {
			start = 0;
		}

		// for sub-queries!!
		if ((start > 0) && (!isCreate)) {
			String prefix = sql.substring(0, start-1);
			start = Math.max(0, prefix.lastIndexOf(";") + 1);
		}
		
		if (isCreate) {
			
			int posMin = sql.length();
			String startType = "";

			// cut the strings so everything before the start is away
			sqlOut = sql.substring(start);
			rawSQL = rawSQL.substring(start);

			for (String trigger : CREATE_TRIGGERS) {
				if (rawSQL.contains(trigger)) {
					// distance in amount of lines
					int newPos = rawSQL.indexOf(trigger);
					if (newPos > -1) {
						// the trigger occurs AFTER the start
						if (newPos < posMin) {
							posMin = newPos;
							startType = trigger;
						}
					}
				}
			}
			
			
			
			
			// check for the type
			if (startType.equals("function") || startType.equals("procedure")) {
				// use END
				// watch out for endif and end if
				rawSQL = rawSQL.replace("end if", "exx if");
				rawSQL = rawSQL.replace("endif", "exxif");
				
				if (rawSQL.contains("end")) {
					end = rawSQL.indexOf("end") + 3;
				} else {
					end = rawSQL.indexOf(";");
				}
				
				rawSQL = rawSQL.replace("exx if", "end if");
				rawSQL = rawSQL.replace("exxif", "endif");
			} else {
				if (rawSQL.indexOf(";") < 0) {
					end = rawSQL.length();
				} else {
					end = rawSQL.indexOf(";") + 1;
				}
				// use ;
			}
			
			return sqlOut.substring(0, end);
		} else {
			// select or insert into or something which does
			// never end with the "END" keyword
			
			sqlOut = sql.substring(start);
			
			if (sqlOut.indexOf(";") < 0) {
				end = sqlOut.length();
			} else {
				end = sqlOut.indexOf(";") + 1;
			}
			
			return sqlOut.substring(0, end);
		}
		
		
	}


	public String getFormatError() {
		return this.formatError;
	}
	
	public int getTagMistakes(){
		return this.tagMistakes;
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
	public ArrayList<String[]> getStaticMapping() {
		/*ArrayList<String> sql = new ArrayList<String>();
		for (int i = 0; i < staticMappings.size(); i++) {
			// (tag,sql) tuples, we only want the sql part
			sql.add(staticMappings.get(i)[1]);
		}
		
		return sql;
		*/
		return this.staticMappings;
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

	
	public void setEMails(ArrayList<String> emails) {
		this.studentMails.clear();
		this.studentMails.addAll(emails);
	}
	
	
	public ArrayList<String> getEMails() {
		return this.studentMails;
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
		/*String fpath = "data/assignment2/submissions/AA_Musterloesung.sql";
		fpath = "data/assignment3/submissions/utf8Bug.txt";
		String[] tags = new String[]{"1a", "1b", "1c", "1d"}; 
		SubmissionReader sr = new SubmissionReader(fpath, tags);
		sr.loadFile();
		*/
		
		String testStr = "use krankenhaus;\n" +
				"DROP TABLE IF EXISTS pfleger;\n" +
				"CREATE VIEW verschreibt;\n" +
				"DROP VIEW IF EXISTS verschreibt;";
		System.out.println("\n> TEST = \n" + testStr);
		
		String out = SubmissionReader.extractSQL(testStr);
		System.out.println("\n> OUT = \n" + out);
		
		System.out.println("\n\n - - - - - \n\n");
		
		
		testStr = "use krankenhaus;\n" +
				"DROP PROCEDURE IF EXISTS warenwert;\n" +
				"CREATE PROCEDURE warenwert(int xy) ;\n" +
				"begin \n" +
				"SELECT * FROM TEST \n" +
				"END\n" +
				"DROP PROCEDURE IF EXISTS warenwert;";
		System.out.println("\n> TEST = \n" + testStr);
		
		out = SubmissionReader.extractSQL(testStr);
		System.out.println("\n> OUT = \n" + out);
		
		System.out.println("\n\n - - - - - \n\n");
		
		
		testStr = "use krankenhaus;\n" +
				"DROP PROCEDURE IF EXISTS warenwert;\n" +
				"CREATE PROCEDURE warenwert(int xy) ;\n" +
				"begin \n" +
				"SELECT * FROM TEST \n" +
				"END\n" +
				"SELECT * from patienten;";
		System.out.println("\n> TEST = \n" + testStr);
		
		out = SubmissionReader.extractSQL(testStr);
		System.out.println("\n> OUT = \n" + out);
		
		System.out.println("\n\n - - - - - \n\n");
		
		
		testStr = "use krankenhaus;\n" +
				"DROP PROCEDURE IF EXISTS warenwert;\n" +
				"SELECT * FROM warenwert(2); \n" +
				"CREATE TABLE test values ();";
		System.out.println("\n> TEST = \n" + testStr);
		
		out = SubmissionReader.extractSQL(testStr);
		System.out.println("\n> OUT = \n" + out);
		
		System.out.println("\n\n - - - - - \n\n");
		
		
		testStr = "use krankenhaus;\n" +
				"DROP PROCEDURE IF EXISTS warenwert;\n" +
				"CREATE PROCEDURE warenwert(int xy) ;\n" +
				"begin \n" +
				"SELECT * FROM TEST \n" +
				"END IF; \n" +
				"END\n" +
				"SELECT * from patienten;";
		System.out.println("\n> TEST = \n" + testStr);
		
		out = SubmissionReader.extractSQL(testStr);
		System.out.println("\n> OUT = \n" + out);
		
		System.out.println("\n\n - - - - - \n\n");
		
		
		testStr = "use krankenhaus;\n" +
				"(SELECT * from test1) \n" +
				"UNION \n" +
				"(SELECT * from test2); \n" +
				"SELECT * from patienten;";
		System.out.println("\n> TEST = \n" + testStr);
		
		out = SubmissionReader.extractSQL(testStr);
		System.out.println("\n> OUT = \n" + out);
		
		System.out.println("\n\n - - - - - \n\n");
		
		
		testStr = "DELIMITER $$\n" +
				"use krankenhaus $$ \n" +
				"CREATE FUNCTION \n" +
				"(SELECT * from test2); \n" +
				"END$$";
		System.out.println("\n> TEST = \n" + testStr);
		
		out = SubmissionReader.extractSQL(testStr);
		System.out.println("\n> OUT = \n" + out);
		
		System.out.println("\n\n - - - - - \n\n");
		
		
	}



	
}
