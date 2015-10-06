package sqlchecker.core;

import java.util.ArrayList;

import sqlchecker.io.IOUtil;
import sqlchecker.io.OutputWriter;
import sqlchecker.io.impl.RawSolutionReader;

public class SolutionGenerator {

	
	private String inputFile = "";
	
	private String outputFile = "";
	
	private String[] connProps = new String[4];
	
	
	/**
	 * Constructor
	 * @param inPath Path to the (raw) input file
	 * @param outPath Path to the file which should be generated
	 * @param cProps Connection properties in the following order: <br>
	 * host (default:localhost) <br>
	 * dbUser (default:root) <br>
	 * dbUserPw (default:) <br>
	 * dbName (default:dbfit) <br>
	 * 
	 */
	public SolutionGenerator(String inPath, String outPath, String[] cProps) {
		this.inputFile = inPath;
		outputFile = OutputWriter.makeUnique(outPath);
		// use the setting given
		this.connProps = cProps.clone();
	}
	
	/**
	 * Constructor using the default connection properties
	 * @param inPath Path to the (raw) input file
	 * @param outPath Path to the file which should be generated
	 */
	public SolutionGenerator(String inPath, String outPath) {
		this.inputFile = inPath;
		outputFile = OutputWriter.makeUnique(outPath);
		
		// default connection settings
		this.connProps = IOUtil.DEFAULT_PROPS;
	}
	
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
	
	public void generate() {
		// step 1, Generate tag->query mapping
		RawSolutionReader rsr = new RawSolutionReader(inputFile);
		rsr.loadFile();
		ArrayList<String[]> mapping = rsr.getMapping();
		// step 2, Execute each query
	}
	
	
	public static void main(String[] args) {
		String inPath = "data/raw.txt";
		String outPath = "data/solutions.txt";
		
		SolutionGenerator sg = new SolutionGenerator(inPath, outPath);
	}

}
