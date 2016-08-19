package sqlchecker.config;

public class ExecutorConfig extends Config {

	/**
	 * The folder in which the submissions can be found
	 */
	private String submissionPath = "";
	
	/**
	 * The path leading to the solution file which should be 
	 * used for checking submissions
	 */
	private String solutionPath = "";
	
	/**
	 * This is the place where the logs and results files will be created
	 */
	private String assignmentPath = "";
	
	
	/**
	 * Should static queries be allowed in student submissions?
	 */
	private boolean staticEnabled = false;
	
	
	/**
	 * Creates a configuration container
	 * @param submPath The folder in which the submissions can be found
	 * @param solPath The path leading to the solution file which should be 
	 * used for checking submissions
	 * @param agnPath This is the place where the logs and results files will be created
	 * @param staticEnabled Should static queries be allowed in student submissions?
	 * @param user database user
	 * @param pw database user password
	 * @param host database host name (e.g. localhost)
	 * @param db database name (empty = default)
	 * @param resetPath Path leading to the reset file. This file is important for
	 * making sure that the database is always in the same state before checking
	 * a submission
	 * 
	 */
	public ExecutorConfig(String submPath, String solPath, String agnPath
			, boolean staticEnabled , String user, String pw, String host
			, String db, String resetPath) {
		
		super(user, pw, host, db, resetPath);
		
		this.submissionPath = submPath;
		this.solutionPath = solPath;
		this.assignmentPath = agnPath;
		this.staticEnabled = staticEnabled;
	}
	
	
	/*
	 * Getters
	 */
	
	public String getSubmissionPath() {
		return this.submissionPath;
	}
	
	public String getSolutionPath() {
		return this.solutionPath;
	}
	
	public String getAssignmentPath() {
		return this.assignmentPath;
	}
	
	public boolean getStaticEnabled() {
		return this.staticEnabled;
	}

}
