package sqlchecker.config;


public class Config {

	protected String dbUser = "root";

	protected String dbPw = "rootpw";

	protected String dbHost = "localhost";

	protected String dbName = "defaultdb";

	protected String resetPath = "";

	public Config(String user, String pw, String host, String db, String resetPath) {

		this.dbUser = user;
		this.dbPw = pw;

		this.dbHost = host;
		this.dbName = db;

		this.resetPath = resetPath;
	}


	public String getResetPath() {
		return this.resetPath;
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
