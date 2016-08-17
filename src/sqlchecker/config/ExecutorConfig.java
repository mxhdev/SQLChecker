package sqlchecker.config;

public class ExecutorConfig extends Config {

	
	private String submissionPath = "";
	
	private String solutionPath = "";
	
	private String assignmentPath = "";
	
	private boolean staticEnabled = false;
	
	
	public ExecutorConfig(String submPath, String solPath, String agnPath
			, String user, String pw, String host, String db, String resetPath
			, boolean staticEnabled) {
		
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
