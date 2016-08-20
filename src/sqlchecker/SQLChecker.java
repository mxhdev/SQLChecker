package sqlchecker;

import sqlchecker.config.ConfigReader;
import sqlchecker.config.ExecuterConfig;
import sqlchecker.config.GeneratorConfig;
import sqlchecker.core.SolutionGenerator;
import sqlchecker.core.SubmissionExecuter;

public class SQLChecker {
	
	/*
	 * >> Structure
	 * 
	 * 1. Read solution
	 * - As string
	 * - Recognize placeholders (e.g. %q1%)
	 * a) read solution & its tags
	 * b) read submission(s)
	 * c) perform mapping
	 * d) run
	 * 
	 * 2. Read student's submission
	 * - Recognize query start and query end (let student use template?)
	 * 
	 * 3. Mapping PlaceholderId (soultion) --> QueryNumber (submission)
	 * - Ignore DELIMITER, custom delimiters are not needed
	 * 
	 * 4. Execute & Build Result document (html or xml?)
	 * 
	 * 
	 * >> Pipeline:
	 * 
	 * Input
	 * - Template & Content
	 * 
	 * 		||
	 * 
	 * Transform/Execute
	 * - Via a script/program
	 * 
	 * 		||
	 * 
	 * Output
	 * - Report (html/xml)
	 */
	
	
	
	public static void showHelp() {
		System.out.println("\nSyntax:"); 
		System.out.println("gen|exec settingsFile [key=value[ key=value[ ...]]]");
		System.out.println("Key/value pairs can be used for overwriting the settings inside the .properties file");
	}
	
	
	
	public static void main(String[] args) {
		if (args.length >= 2) {
			// read arguments
			ConfigReader reader = new ConfigReader(args);
			
			if (args[0].equals("exec")) {
				// Initialize the configuration container
				ExecuterConfig execconf = reader.getConfig(ExecuterConfig.class);
				// run the submission executor and check 
				// the submissions
				SubmissionExecuter se = new SubmissionExecuter(execconf);
				se.runCheck();
				
			} else if (args[0].equals("gen")) {
				// Initialize the configuration container
				GeneratorConfig genconf = reader.getConfig(GeneratorConfig.class);
				// run the solution generator and create 
				// the solution file from some raw file
				SolutionGenerator sg = new SolutionGenerator(genconf);
				sg.generate();
			}
		} else {
			System.out.println("ERROR: Not enough arguments given!");
			showHelp();
		}
	}

}
