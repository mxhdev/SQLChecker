package sqlchecker.config;


public class Config {

	private String dbUser = "root";

	private String dbPw = "rootpw";

	private String dbHost = "localhost";

	private String dbName = "defaultdb";
	
	/**
	 * Path leading to the reset file. This file is important for
	 * making sure that the database is always in the same state 
	 * before checking a submission
	 */
	private String resetPath = "";

	
	public Config() {
		// Use default values
		this("root", "", "localhost", "", "reset.sql");
	}
	
	
	public Config(String resetPath) {
		this("root", "", "localhost", "", resetPath);
	}
	
	
	/**
	 * @param user Database user 
	 * @param pw Database user password
	 * @param host Database host name (e.g. localhost)
	 * @param db Database name (empty = default)
	 * @param resetPath Path leading to the reset script
	 */
	public Config(String user, String pw, String host, String db, String resetPath) {

		this.dbUser = user;
		this.dbPw = pw;

		this.dbHost = host;
		this.dbName = db;
		
		this.resetPath = resetPath;
	}
	
	

	public String getUser() {
		return this.dbUser;
	}

	public String getHost() {
		return this.dbHost;
	}

	public String getPw() {
		return this.dbPw;
	}

	public String getDbName() {
		return this.dbName;
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
	
}
