package sqlchecker.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class ConfigReader {

	/**
	 * Stores the values in the configuration file as key, value pairs
	 */
	private final Map<String, String> settings = new HashMap<String, String>();
	
	
	private static final String DB_USER = "db.user";
	
	private static final String DB_PW = "db.pw";
	
	private static final String DB_NAME = "db.name";
	
	private static final String DB_HOST = "db.host";
	
	private static final String DB_RESETSCRIPT = "db.resetscript";
	
	
	
	private static final String GENERATOR_INFILE = "gen.infile";
	
	private static final String GENERATOR_OUTFILE = "gen.outfile";
	
	private static final String GENERATOR_SAMPLEFILE = "gen.samplefile";
	
	
	
	private static final String EXECUTOR_SUBMISSIONS = "exec.submissions";
	
	private static final String EXECUTOR_SOLUTION = "exec.solution";
	
	private static final String EXECUTOR_OUTPATH = "exec.outpath";
	
	private static final String EXECUTOR_ALLOW_STATIC = "exec.allowstatic";
	
	
	
	
	
	private String fpath = "settings.properties";
	private String[] args = new String[0];
	
	
	
	public ConfigReader(String[] args) {
		
		// set defaults
		settings.put(DB_USER, "root");
		settings.put(DB_PW, "");
		settings.put(DB_HOST, "localhost");
		settings.put(DB_NAME, "");
		settings.put(DB_RESETSCRIPT, "reset.sql");
		
		
		settings.put(GENERATOR_INFILE, "raw.sql");
		settings.put(GENERATOR_OUTFILE, "solution.txt");
		settings.put(GENERATOR_SAMPLEFILE, "submissions/sample.sql");
		
		settings.put(EXECUTOR_SUBMISSIONS, "submissions/");
		settings.put(EXECUTOR_SOLUTION, "solution.txt");
		settings.put(EXECUTOR_OUTPATH, "out/");
		settings.put(EXECUTOR_ALLOW_STATIC, "false");
		
		/*
		 * default values
		 * # db.* is only relevant for gen
db.user=root
db.pw=
db.host=localhost
db.name=

db.resetscript=reset.sql

gen.infile=raw.sql
gen.outfile=solution.txt
gen.samplefile=submissions/sample.sql

exec.submissions=submissions/
exec.solution=solution.txt
exec.outpath=out/
exec.allowstatic=false
		 */
		
		// store arguments
		this.args = args;
		
		// set configuration file name
		this.fpath = args[1];
	}
	



	public <T extends Config> T getConfig(Class<T> type) {
		
		// load configuration from file
		loadFile();
		
		// check for additional modifiers in arguments
		loadArguments();
		
		T conf = null;
		
		
		
		if (type.equals(ExecuterConfig.class)) {
			boolean staticAllowed = Boolean.parseBoolean(settings.get(EXECUTOR_ALLOW_STATIC));
			conf = type.cast(new ExecuterConfig(settings.get(EXECUTOR_SUBMISSIONS)
					, settings.get(EXECUTOR_SOLUTION)
					, settings.get(EXECUTOR_OUTPATH)
					, staticAllowed
					, settings.get(DB_RESETSCRIPT)));
			
		} else if (type.equals(GeneratorConfig.class)) {
			
			conf = type.cast(new GeneratorConfig(settings.get(GENERATOR_INFILE)
					, settings.get(GENERATOR_OUTFILE)
					, settings.get(GENERATOR_SAMPLEFILE)
					, settings.get(DB_USER)
					, settings.get(DB_PW)
					, settings.get(DB_HOST)
					, settings.get(DB_NAME)
					, settings.get(DB_RESETSCRIPT)));
		} else {
			System.err.println("Unable to load config for class " + type.getName());
		}
		
		return conf;
	}
	
	
	
	private void extractProperty(String line) {
		// key value pairs are separated by "="
		int idx = line.indexOf("=");
		String key = line.substring(0, idx).toLowerCase();
		String value = line.substring(idx + 1, line.length());
		settings.put(key, value);
		System.out.println("Found config key=\"" + key + "\", value=\"" + value + "\"");
	}
	
	
	/**
	 * Reads a file. For each line, this function does the
	 * following: <br>
	 * - Trim the line (as String) <br>
	 * - If the line is not empty: <br> 
	 * 	insert into internal data structure
	 * @throws IOException
	 */
	private void readFile() throws IOException {
		
		BufferedReader bf = null;
		FileInputStream fs = null;
		
		String line = "";
		
		try {
			fs = new FileInputStream(fpath);
			bf = new BufferedReader(new InputStreamReader(fs));
			
			while ((line = bf.readLine()) != null) {
				line = line.trim();
				
				// for each line
				// ignore empty lines
				// allow comments starting with #
				if (!(line.isEmpty() || line.startsWith("#") )) {
					extractProperty(line);
				}
				
			}
			
		} catch (IOException ioe) {
			System.err.println("An error occured while trying to read file " + fpath);
			ioe.printStackTrace(System.err);
			
		} finally {
			if (fs != null) fs.close();
			if (bf != null) bf.close();
		}
	}
	
	
	
	/**
	 * Calls the readFile function. 
	 * If the given file name does not exist, then the default
	 * settings will be loaded.
	 */
	private void loadFile() {

		// only read the file if it exists!
		if ( (new File(fpath)).exists() ) {
			
			// actions before reading - none -
			
			try {
				readFile();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			// actions after reading - none -
		
		} else {
			System.err.println("File \"" + fpath + "\" does not exist!");
		}
	}

	
	
	
	private void loadArguments() {
		// ARGS : gen|exec filename PARAMETERS
		// PARAMETERS : key=value [key=value ...]
		for (int i = 2; i < args.length; i++) {
			String prop = args[i];
			if (prop.contains("=")) {
				extractProperty(prop);
			}
		}
	}

	
	
	
}

