package sqlchecker.config;

public class GeneratorConfig extends Config {

	private String inPath = "";
	
	private String outPath = "";
	
	private String samplePath = "";
	
	
	public GeneratorConfig(String inputPath
			, String outputPath, String samplePath, String user
			, String pw, String host, String db, String resetPath) {
		
		super(user, pw, host, db, resetPath);
		
		this.inPath = inputPath;
		this.outPath = outputPath;
		this.samplePath = samplePath;
	}
	
	
	/*
	 * Getters
	 */
	
	public String getInputPath() {
		return this.inPath;
	}
	
	
	public String getOutputPath() {
		return this.outPath;
	}
	
	public String getSamplePath() {
		return this.samplePath;
	}
	

}
