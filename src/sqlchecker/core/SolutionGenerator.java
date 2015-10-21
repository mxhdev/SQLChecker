package sqlchecker.core;

import java.util.ArrayList;

import sqlchecker.io.IOUtil;
import sqlchecker.io.OutputWriter;
import sqlchecker.io.impl.RawSolutionReader;

public class SolutionGenerator {

	
	private String inputFile = "";
	
	private String outputFile = "";
	
	/**
	 * Connection properties in the following order: <br>
	 * host (default:localhost) <br>
	 * dbUser (default:root) <br>
	 * dbUserPw (default:) <br>
	 * dbName (default:dbfit) <br>
	 */
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
	
	
	
	private ArrayList<SQLCallable> generateCallables(ArrayList<String[]> mapping) {
		
		ArrayList<SQLCallable> callables = new ArrayList<SQLCallable>();
		
		for (int i = 0; i < mapping.size(); i++) {
			String sql = mapping.get(i)[1];
			String sqlTmp = sql.toLowerCase().trim();
			
			// make sure this is a function or procedure
			if (sqlTmp.startsWith("create ")) {
				String[] tokens = sqlTmp.split(" ");
				if (!tokens[1].equals("table") && !tokens[2].equals("table")) {
					
					SQLCallable sqlc = new SQLCallable(mapping.get(i)[1]);
					callables.add(sqlc);
				}
			}
		}
		
		// return mapping;
		return callables;
	}
	
	
	/*
	 * 1) Generate tag->query mapping
	 * 
	 * 2) Execute each query & ROLLBACK!!
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
		
		// extract Callables
		ArrayList<String[]> mapping = rsr.getMapping();
		ArrayList<SQLCallable> callables = generateCallables(mapping);
		for (int i = 0; i < mapping.size(); i++) {
			System.out.println(mapping.get(i)[1]);
			System.out.println("=> " + mapping.get(i)[1].split("\n").length + " lines");
		}
		System.out.println("\n* Callables:");
		for (int i = 0; i < callables.size(); i++) {
			System.out.println(callables.get(i).getName());
		}
		
		
		String tagStr = extractTags(mapping);
		
		// step 1.2 - write the header information
		String html = "";
		// tags=a,b,c,d
		// tags
		html += "tags=" + tagStr + "\n\n";
		// driver name
		html += "\n<table>"
				+ "\n\t<tr>"
				+ "\n\t\t<td>dbfit.MySqlTest</td>"
				+ "\n\t</tr>"
				+ "\n</table>\n";
		/* Connection properties in the following order: <br>
	 * host (default:localhost) <br>
	 * dbUser (default:root) <br>
	 * dbUserPw (default:) <br>
	 * dbName (default:dbfit) <br>
	 */
		html += "\n<table>"
				+ "\n\t<tr>"
				+ "\n\t\t<td>Connect</td>"
				+ "\n\t\t<td>" + connProps[0] + "</td>"
				+ "\n\t\t<td>" + connProps[1] + "</td>"
				+ "\n\t\t<td>" + connProps[2] + "</td>"
				+ "\n\t\t<td>" + connProps[3] + "</td>"
				+ "\n\t</tr>"
				+ "\n</table>\n";
		
		System.out.println("HEADER:");
		System.out.println("H H H H H H H H H H H H H H H H H H H H");
		System.out.println(html);
		System.out.println("H H H H H H H H H H H H H H H H H H H H");
		// step 2, Execute each query
		QueryPipeline qp = new QueryPipeline(mapping, callables);
		
		html += qp.run();
	}
	
	
	
	public static String extractTags(ArrayList<String[]> mapping) {
		String tStr = "";
		ArrayList<String> tags = new ArrayList<String>();
		String IGNORE = "static";
		
		for (int i = 0; i < mapping.size(); i++) {
			String tagTmp = mapping.get(i)[0];
			// static will be ignored
			if (!tagTmp.equalsIgnoreCase(IGNORE)) {
				// avoid duplicates
				if (tags.indexOf(tagTmp) < 0) tags.add(tagTmp);
			}
		}
		
		// build string
		for (int i = 0; i < tags.size(); i++) {
			// comma separated tag list
			tStr += tags.get(i);
			if (i < (tags.size() - 1)) tStr += ",";
		}
		
		return tStr;
	}
	
	
	public static void main(String[] args) {
		String inPath = "data/raw.sql";
		String outPath = "data/solutions.txt";
		
		SolutionGenerator sg = new SolutionGenerator(inPath, outPath);
		sg.generate();
	}

}
