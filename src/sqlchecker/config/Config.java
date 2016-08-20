package sqlchecker.config;


public class Config {

	protected String dbUser = "root";

	protected String dbPw = "rootpw";

	protected String dbHost = "localhost";

	protected String dbName = "defaultdb";
	

	public Config() {
		// Default values
		this.dbUser = "root";
		this.dbPw = "";
		this.dbHost = "localhost";
		this.dbName = "";
	}
	
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
