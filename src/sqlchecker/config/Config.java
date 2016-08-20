package sqlchecker.config;


public class Config {

	protected String dbUser = "root";

	protected String dbPw = "rootpw";

	protected String dbHost = "localhost";

	protected String dbName = "defaultdb";
	

	public Config() {
		// Use default values
		this.dbUser = "root";
		this.dbPw = "";
		this.dbHost = "localhost";
		this.dbName = "";
	}
	
	
	/**
	 * @param user Database user 
	 * @param pw Database user password
	 * @param host Database host name (e.g. localhost)
	 * @param db Database name (empty = default)
	 */
	public Config(String user, String pw, String host, String db) {

		this.dbUser = user;
		this.dbPw = pw;

		this.dbHost = host;
		this.dbName = db;
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
}
