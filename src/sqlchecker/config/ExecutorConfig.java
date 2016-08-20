package sqlchecker.config;

public class ExecutorConfig {

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
	 * This is the place where the logs and results files will 
	 * be created. This variable has to end with /
	 */
	private String assignmentPath = "";

	/**
	 * Path leading to the reset file. This file is important for
	 * making sure that the database is always in the same state 
	 * before checking a submission
	 */
	private String resetPath = "";
	

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
	 * @param resetPath Path leading to the reset file. This file is important for
	 * making sure that the database is always in the same state before checking
	 * a submission
	 * 
	 */
	public ExecutorConfig(String submPath, String solPath, String agnPath
			, boolean staticEnabled, String resetPath) {
		
		// make sure the assignment path ends with /
		if (!agnPath.endsWith("/")) {
			agnPath += "/";
		}
		
		this.submissionPath = submPath;
		this.solutionPath = solPath;
		this.assignmentPath = agnPath;
		this.resetPath = resetPath;
		this.staticEnabled = staticEnabled;
	}


	/*
	 * Getters
	 */
	
	
	/**
	 * 
	 * @return The folder in which the submissions can be found
	 */
	public String getSubmissionPath() {
		return this.submissionPath;
	}

	/**
	 * 
	 * @return The path leading to the solution file which should be 
	 * used for checking submissions
	 */
	public String getSolutionPath() {
		return this.solutionPath;
	}

	/**
	 * 
	 * @return This is the place where the logs and results files will 
	 * be created. This variable has to end with /
	 */
	public String getAssignmentPath() {
		return this.assignmentPath;
	}

	/**
	 * 
	 * @return Path leading to the reset file. This file is important for
	 * making sure that the database is always in the same state before checking
	 * a submission
	 */
	public String getResetPath() {
		return this.resetPath;
	}
	
	/**
	 * 
	 * @return Should static queries be allowed in student submissions?
	 */
	public boolean getStaticEnabled() {
		return this.staticEnabled;
	}

}
