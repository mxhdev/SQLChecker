package sqlchecker.config;

public class GeneratorConfig extends Config {

	
	/**
	 * Path of the "raw" file, from which the final solution file 
	 * should be created
	 */
	private String inPath = "";
	
	/**
	 * The path of the output solution file (e.g. data/tmp/solution.txt)
	 */
	private String outPath = "";
	
	/**
	 * The path of the output sample submission for the created 
	 * solution file (e.g. data/tmp/sample.sql)
	 */
	private String samplePath = "";

	/**
	 * Path leading to the reset file. This file is important for
	 * making sure that the database is always in the same state before checking
	 * a submission
	 */
	private String resetPath = "";
	
	
	/**
	 * 
	 * @param inputPath Path of the "raw" file, from which the final solution file 
	 * should be created
	 * @param outputPath The path of the output solution file (e.g. data/tmp/solution.txt)
	 * @param samplePath The path of the output sample submission for the created 
	 * solution file (e.g. data/tmp/sample.sql)
	 * @param resetPath Path leading to the reset file. This file is important for
	 * making sure that the database is always in the same state before checking
	 * a submission
	 * @param user database user 
	 * @param pw database user password
	 * @param host database host name (e.g. localhost)
	 * @param db database name (empty = default)
	 * 
	 */
	public GeneratorConfig(String inputPath
			, String outputPath, String samplePath, String resetPath, String user
			, String pw, String host, String db) {
		
		super(user, pw, host, db);
		
		this.inPath = inputPath;
		this.outPath = outputPath;
		this.samplePath = samplePath;
		this.resetPath = resetPath;
	}
	
	
	/*
	 * Getters
	 */
	
	
	
	/**
	 * @return Path leading to the reset file. This file is important for
	 * making sure that the database is always in the same state before checking
	 * a submission
	 */
	public String getResetPath() {
		return this.resetPath;
	}
	
	
	/**
	 * 
	 * @return Path of the "raw" file, from which the final solution file 
	 * should be created
	 */
	public String getInputPath() {
		return this.inPath;
	}
	
	
	/**
	 * 
	 * @return The path of the output solution file (e.g. data/tmp/solution.txt)
	 */
	public String getOutputPath() {
		return this.outPath;
	}
	
	
	/**
	 * 
	 * @return The path of the output sample submission for the created 
	 * solution file (e.g. data/tmp/sample.sql)
	 */
	public String getSamplePath() {
		return this.samplePath;
	}
	

}
