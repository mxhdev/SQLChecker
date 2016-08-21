package sqlchecker.core;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import sqlchecker.config.Config;
import sqlchecker.config.ExecuterConfig;
import sqlchecker.io.IOUtil;
import sqlchecker.io.OutputWriter;
import sqlchecker.io.impl.ScriptReader;
import sqlchecker.io.impl.SolutionReader;
import sqlchecker.io.impl.SubmissionReader;



/**
 * Executes the submissions which are stored in the given directory
 * and prints the result/status
 * 
 * @author Max Hofmann
 */
public class SubmissionExecuter {

	
	
	private ExecuterConfig conf;
	
	
	protected static int resultLimit = 100;
	
	
	
	public SubmissionExecuter(ExecuterConfig confIn) {
		this.conf = confIn;
	}
	
	
	
	
	/**
	 * Generates meta data for a student submission. It expects that 
	 * the meta data is marked with the tag defined in 
	 * SolutionReader.METADATA_TAG. The data corresponding
	 * to this tag is a list of students who worked on this submission.
	 * There should be exactly one student name and id per line. The
	 * student name and id are separated with the delimiter defined
	 * in IOUtil.CSV_DELIMITER. The first field is the name, the
	 * second name is the student id
	 * @param sr The submission reader which contains the tags and
	 * which should receive meta data from its meta data tag
	 * @return The updated version of the given submission. This
	 * contains the name and student id of all contributors of every
	 * student which has worked on this submission
	 */
	private SubmissionReader generateMetadata(SubmissionReader sr) {
		ArrayList<String[]> mapping = sr.getMapping();
		String rawMd = "";
		// look up the data which corresponds to the meta data tag
		for (int i = 0; i < mapping.size(); i++) {
			String[] m = mapping.get(i);
			if (m[0].equals(SolutionReader.METADATA_TAG)) {
				rawMd = m[1];
				break;
			}
		}
		
		// initialize name, student id and emails list
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> studentIds = new ArrayList<String>();
		ArrayList<String> emails = new ArrayList<String>();
		
		// read the lines of the meta data of this submission
		String[] mdLines = rawMd.split("\n");
		for (String md : mdLines) {
			if (md.contains("@")) {
				// parse email
				emails.add(md.trim());
			} else {
				// parse name / studentId
				String[] data = md.split(IOUtil.CSV_DELIMITER);
				// if there is a name AND a studentId
				if (data.length > 1) {
					names.add(data[0].trim());
					studentIds.add(data[1].trim());
				}
			}
		}
		
		// store the meta data
		sr.setMatrikelnummer(studentIds);
		sr.setName(names);
		sr.setEMails(emails);
		
		// return new version of this object
		return sr;
	}
	
	/**
	 * Tests all the available submissions
	 */
	public void runCheck() {
		
		// read parameters
		String agnPath = conf.getAssignmentPath();
		String solPath = conf.getSolutionPath();
		boolean staticEnabled = conf.getStaticEnabled();
		
		// get list of all submissions
		ArrayList<File> submFileList = IOUtil.fetchFiles(conf.getSubmissionPath());
		File[] submissions = submFileList.toArray(new File[submFileList.size()]);
		
		// all lines of the CSV file
		ArrayList<String> csvLines = new ArrayList<String>();
		// log (contains errors for each submission)
		ArrayList<String> logContent = new ArrayList<String>();
		
		// store all mail addresses
		ArrayList<String> allMails = new ArrayList<String>();
		
		// show info
		System.out.println("Solution: \n\t" + solPath);
		System.out.println("\n" + submissions.length + " submissions found: ");
		for (File f : submissions) 
			System.out.println("\t" + f.getPath());
		
		// load tags & solution
		SolutionReader sr = new SolutionReader(solPath);
		sr.loadFile();
		String solution = sr.getHTML().toString();
		

		
		//Generate ArrayList for duplicate Submission testing
		ArrayList<SubmissionReader> subCom = new ArrayList<SubmissionReader>();
		
		// Define output writer
		//PrintWriter out = new PrintWriter(System.out, false);
		// host, user, pw, dbname, resetscipt 
		// The resetscript field of this config object contains 
		// a default value, so it should not be used here. 
		// Instead, the reset script path should be retrieved from the conf object
		
		Config connProps = sr.getConnectionProperties();
		//System.out.println("[SubmissionExecuter] Properties: \n\thost=" + connProps[0] + "\n\tdb=" + connProps[1] + "\n\tuser=" + connProps[2] + "\n\tpw=" + connProps[3] + "\n\tscript=" + resetScript);
		
		csvLines.add(IOUtil.generateCSVHeader(sr.getTagMap()));
		
		for (int i = 0; i < submissions.length; i++) {
			
			

			// reset the database first
			// -System.out.println("Executing reset with values \n\thost=" + connProps[0] + "\n\tdb=" + connProps[1] + "\n\tuser=" + connProps[2] + "\n\tpw=" + connProps[3] + "\n\tscript=" + resetScript);;
			
			ScriptReader resetter = new ScriptReader(conf.getResetPath(), ScriptReader.DEFAULT_DELIM, connProps);
			resetter.loadFile();
			
			
			
			File subm = submissions[i];
			String fpath = subm.getPath();
			
			System.out.println("\n\n[" + (i+1) + "/" + submissions.length + "] Testing: " + subm);
			
			// load a submission
			SubmissionReader subr = new SubmissionReader(subm.getPath(), IOUtil.tags);
			subr.loadFile();
			
			// Set Name and Matrikelnummer of Submission
			subr = generateMetadata(subr);
			
			allMails.addAll(subr.getEMails());
			
			//add submission to submission list for duplicate check
			subCom.add(subr);

			// init dbfit checker facade
			DBFitFacade checker = new DBFitFacade(fpath, connProps);
			
			// execute static mapping if enabled
			ArrayList<String[]> staticQueriesAll = subr.getStaticMapping();
			ArrayList<String> staticQueries =  new ArrayList<String>();
			for (int j = 0; j < staticQueriesAll.size(); j++) {
				// (tag,sql) tuples, we only want the sql part
				staticQueries.add(staticQueriesAll.get(j)[1]);
			}
			
			ResultStorage staticRs = null;
			
			if (staticEnabled) {
				System.out.println("\n> Starting to execute the " + staticQueries.size() + " > STATIC < queries of the current student submission\n");
				String staticString = SubmissionReader.generateStaticHTML(connProps, staticQueries);
				// Execute via DBFit facade
				try {
					staticRs = checker.runSubmission(staticString, subr.getName(), subr.getMatrikelnummer());
				} catch (SQLException sqle) {
					// unable to close connection
					sqle.printStackTrace();
				}
			}
			
/*
			// Add CSV header (if one submission is static, then others are too)
			if (i == 0)
				csvLines.add(IOUtil.generateCSVHeader(sr.getTagMap(), staticRs, staticQueries.size()));
	*/		
			// get mapping and apply it
			ArrayList<String[]> mapping = subr.getMapping();
			String checkStr = IOUtil.applyMapping(solution, mapping);


			// perform the check
			ResultStorage rs = null;
			try {
				// rs = runSubmission(fname, checkStr, connProps);
				rs = checker.runSubmission(checkStr, subr.getName(), subr.getMatrikelnummer());
			} catch (SQLException sqle) {
				// unable to close connection
				sqle.printStackTrace();
			}
			
			if (rs == null) {
				// some SQL exception occurred
				logContent.add("Error for file " + fpath);
				csvLines.add(fpath + IOUtil.CSV_DELIMITER + "?");
				continue;
			}
			
			// The result set is NOT null
			rs.setFormatError(subr.getFormatError());
			
			rs.setTagMistakes(subr.getTagMistakes());
			
			if (staticRs != null)  {
				rs.setStaticResults(staticRs, staticQueries.size());
			}
			
			// add csv line
			csvLines.add(rs.getCSVLine());
			// add log entry
			logContent.add(rs.getLogEntry());
			
			
			
		}
		
		String mailList = "";
		int mailCount = allMails.size();
		System.out.println("\n\nFound " + mailCount + " email adresses\n");
		// Show all email addresses
		for (int i = 0; i < mailCount; i++) {
			if (i > 0) mailList += ", ";
			mailList += allMails.get(i);
		}
		logContent.add(mailList);
		
		
		/*
		 * write/show content
		 */
		System.out.println("\n\nWriting content to > CSV < file:\n");
		String summaryPath = agnPath + "summary.csv";
		summaryPath = OutputWriter.makeUnique(summaryPath);
		System.out.println("\t" + summaryPath + "\n");
		/*
		for (int i = 0; i < csvLines.size(); i++)  {
			System.out.println(csvLines.get(i));
		}*/
		try {
			OutputWriter summaryWriter = new OutputWriter(summaryPath, csvLines);
			summaryWriter.writeLines();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		
		System.out.println("\n\nWriting content to > LOG < file:\n");
		String logPath = agnPath + "mistakes.log";
		logPath = OutputWriter.makeUnique(logPath);
		System.out.println("\t" + logPath + "\n");
		/*
		for (int i = 0; i < logContent.size(); i++)  {
			System.out.println(logContent.get(i));
		}
		*/
		
		try {
			OutputWriter logWriter = new OutputWriter(logPath, logContent);
			logWriter.writeLines();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		ArrayList<String> tags = sr.getTagMap();
		ArrayList<String> exercises = new ArrayList<String>();
		int qnum = tags.size();
		for(int o = 0; o < qnum ;o++){
			if(!tags.get(o).isEmpty()){
				exercises.add(tags.get(o));
			}
		}
		// run plagiarism check and get the reports
		ArrayList<ArrayList<String>> reports = PlagiatTest.extractComments(subCom, exercises, staticEnabled);
		ArrayList<String> plagiat = reports.get(1);
		ArrayList<String> comment = reports.get(0);
		
		// generate unique filename of duplicate report
		String fnamePlagiat = agnPath + "PlagiatReport.csv";
		fnamePlagiat = OutputWriter.makeUnique(fnamePlagiat);
		
		String fnameComment = agnPath + "CommentReport.csv";
		fnameComment = OutputWriter.makeUnique(fnameComment);
		
		System.out.println("Writing content to > PlagiatReport < file: \n \n \t"+fnamePlagiat);
		// write report file 
		try {
			OutputWriter plagiatWriter = new OutputWriter(fnamePlagiat, plagiat);
			plagiatWriter.writeLines();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		System.out.println("\n Writing content to > CommentReport < file: \n \n \t"+fnameComment);
		// write report file 
		try {
			OutputWriter commentWriter = new OutputWriter(fnameComment, comment);
			commentWriter.writeLines();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	public static void main(String[] args) {
		// allow static queries in student submissions
		boolean allowStatic = false;
		//int resultLimit = 100;
		
		String agnPath = "data/assignment3/";
		String resetPath = "data/assignment2/airportReset.sql";
		
		agnPath = "data/functest/";
		
		resetPath = agnPath + "reset.sql";
		String submissionPath = agnPath + "submissions/";
		String solPath = agnPath + "solution.txt";
		
		ExecuterConfig exconf = new ExecuterConfig(submissionPath, solPath, agnPath, allowStatic, resetPath);
		
		
		//SubmissionExecuter se = new SubmissionExecuter(agnPath, resetPath, allowStatic, resultLimit);
		SubmissionExecuter se = new SubmissionExecuter(exconf);
		se.runCheck();
	}

}
