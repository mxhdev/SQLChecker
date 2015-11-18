package sqlchecker.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import sqlchecker.io.IOUtil;
import sqlchecker.io.OutputWriter;
import sqlchecker.io.impl.SolutionReader;
import sqlchecker.io.impl.SubmissionReader;

public abstract class AbstractSubmissionExecuter {

	/**
	 * (Relative) Path to the folder which stores all the submissions
	 * which should be checked
	 */
	private String submPath = "";
	
	/**
	 * (Relative) Path to the file which contains the solution
	 */
	private String solPath = "";
	
	/**
	 * Path for this assignment (e.g. data/assignment1/)
	 */
	private String agnPath = "";
	
	/**
	 * Path leading to the reset script
	 */
	private String resetScript = "";
	
	
	
	public AbstractSubmissionExecuter(String assignmentPath, String resetPath) {
		this.agnPath = assignmentPath;
		this.submPath = assignmentPath + "/submissions/";
		this.solPath = assignmentPath + "/solution.txt";
		this.resetScript = resetPath;
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
		
		// initialize name and student id list
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> studentIds = new ArrayList<String>();
		
		// read the lines of the meta data of this submission
		String[] mdLines = rawMd.split("\n");
		for (String md : mdLines) {
			String[] data = md.split(IOUtil.CSV_DELIMITER);
			// if there is a name AND a studentId
			if (data.length > 1) {
				names.add(data[0]);
				studentIds.add(data[1]);
			}
		}
		
		// store the meta data
		sr.setMatrikelnummer(studentIds);
		sr.setName(names);
		
		// return new version of this object
		return sr;
	}
	
	
	
	
	/**
	 * Tests all the available submissions
	 */
	public void runCheck() {
		// get list of all submissions
		ArrayList<File> submFileList = IOUtil.fetchFiles(submPath);
		File[] submissions = submFileList.toArray(new File[submFileList.size()]);
		// show info
		System.out.println("Solution: \n\t" + solPath);
		System.out.println("\n" + submissions.length + " submissions found: ");
		for (File f : submissions) 
			System.out.println("\t" + f.getPath());
/*
		// load tags & solution
		SolutionReader sr = new SolutionReader(solPath);
		sr.loadFile();
		String solution = sr.getHTML().toString();
	*/	
		
		// submission list for plagiat check
		ArrayList<SubmissionReader> subCom = new ArrayList<SubmissionReader>();
		// store list of results for writing them to a file
		ArrayList<ResultStorage> rsList = new ArrayList<ResultStorage>();
		
		// load tags & solution
		SolutionReader sr = new SolutionReader(solPath);
		sr.loadFile();

		for (int i = 0; i < submissions.length; i++) {
			File subm = submissions[i];

			System.out.println("\n\n[" + (i+1) + "/" + submissions.length + "] Testing: " + subm);
			
			// load a submission
			SubmissionReader subr = new SubmissionReader(subm.getPath(), IOUtil.tags);
			subr.loadFile();
			
			// Set Name and StudentId for Submission
			subr = generateMetadata(subr);
			subCom.add(subr);
			
			ResultStorage rs = workSubmission(sr, subr);
			rsList.add(rs);
		}
		
		
		workResults(rsList);
		
		
		
		
		/*
		 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		 * 
		 * perform plagiat check (start)
		 * 
		 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		 */
		ArrayList<String> tags = sr.getTagMap();
		ArrayList<String> exercises = new ArrayList<String>();
		int qnum = tags.size();
		for(int o = 0; o < qnum ;o++){
			if(!tags.get(o).isEmpty()){
				exercises.add(tags.get(o));
			}
		}
		ArrayList<String> resLis = PlagiatTest.extractComments(subCom, exercises);

		// generate unique filename of duplicate report
		String fname = this.agnPath + "PlagiatReport.csv";
		fname = OutputWriter.makeUnique(fname);
		
		System.out.println("Writing content to > PlagiatReport < file: \n \n \t"+fname);
		// write report file 
		try {
			OutputWriter plagiatWriter = new OutputWriter(fname, resLis);
			plagiatWriter.writeLines();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		
		/*
		 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		 * 
		 * perform plagiat check (end)
		 * 
		 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		 */
		
		
		
	}
	
	
	
	
	
	
	

	protected abstract ResultStorage workSubmission(SolutionReader sr, SubmissionReader subr);
	
	protected abstract void workResults(ArrayList<ResultStorage> results);
	
	
}
