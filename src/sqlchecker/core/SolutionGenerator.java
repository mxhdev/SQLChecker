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
	
	
	/**
	 * 
	 * @param mapping
	 * @return All the functions/procedures for which this function added
	 * a drop statement. (This function adds such a statement for every
	 * table/function/procedure it encounters) 
	 */
	private ArrayList<String> addDropStatements(ArrayList<String[]> mapping) {
		
		ArrayList<String> callables = new ArrayList<String>();
		
		ArrayList<String[]> mappingNew = new ArrayList<String[]>();
		mappingNew.addAll(mapping);
		
		for (int i = 0; i < mapping.size(); i++) {
			String sql = mapping.get(i)[1];
			String sqlTmp = sql.toLowerCase().trim();
			// drop has to be added
			if (sqlTmp.startsWith("create ")) {
				String[] tokens = sqlTmp.split(" ");
				if (tokens[1].equals("table") || tokens[2].equals("table")) {
					// add a drop table statement
					sql = sql.substring(0, sql.indexOf("("));
					tokens = sql.split(" ");
					String tname = tokens[tokens.length - 1];
					// System.out.println("TABLENAME=\"" + tname + "\"");
					
					mapping.add(new String[]{"static", "DROP TABLE " + tname + ";"});
				} else {
					// it is either a function or a procedure
					int type = IOUtil.isSQLFunction(sql);
					String dropsql = "DROP ";
					if (type == 0) {
						// function
						dropsql += "FUNCTION ";
					} else if (type == 1) {
						// procedure
						dropsql += "PROCEDURE ";
					} else {
						// other
						System.out.println("[ERROR] Received Query type " 
							+ type + " (Unknown) from IOUtil.isSQLFunction."
							+ "\nNo drop statement will be added for this SQL statement");
						System.out.println(sql);
						// iterate!
						continue;
					}
					
					// add the name of the function/procedure
					String header = IOUtil.parseCallableHeader(sql);
					String cname = header.substring(0, header.indexOf("(")).trim();
					dropsql += cname + ";";
					System.out.println("DROPSQL=" + dropsql);
					
					callables.add(cname);
					mapping.add(new String[]{"static", dropsql});
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
		ArrayList<String[]> mapping = rsr.getMapping();
		ArrayList<String> callables = addDropStatements(mapping);
		for (int i = 0; i < mapping.size(); i++) {
			System.out.println(mapping.get(i)[1]);
		}
		System.out.println("\n* Callables:");
		for (int i = 0; i < callables.size(); i++) {
			System.out.println(callables.get(i));
		}
		// step 2, Execute each query
	}
	
	
	public static void main(String[] args) {
		String inPath = "data/raw.sql";
		String outPath = "data/solutions.txt";
		
		SolutionGenerator sg = new SolutionGenerator(inPath, outPath);
		sg.generate();
	}

}
