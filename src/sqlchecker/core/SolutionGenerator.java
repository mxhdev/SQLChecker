package sqlchecker.core;

import sqlchecker.io.OutputWriter;

public class SolutionGenerator {

	
	private String inputFile = "";
	
	private String outputFile = "";
	
	public SolutionGenerator(String inPath, String outPath) {
		this.inputFile = inPath;
		outputFile = OutputWriter.makeUnique(outPath);
	}
	
	
	public void generate() {
		/*
		 * 1) Generate tag->query mapping
		 * 
		 * 2) Execute each query
		 * 2.1) Store result of each query
		 * 
		 * 3) Write HTML to file
		 * 3.1) Detect query command (query, execute, executeddl??)
		 * 3.2) Convert query results to html
		 * 3.3) Write to file
		 */
	}
	
	
	public static void main(String[] args) {
		String inPath = "data/raw.txt";
		String outPath = "data/solutions.txt";
		
		SolutionGenerator sg = new SolutionGenerator(inPath, outPath);
	}

}
