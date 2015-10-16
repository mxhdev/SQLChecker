package sqlchecker.core;

import java.util.ArrayList;


/**
 * Defines a table with custom captions
 * @author Max Hofmann
 * @deprecated Not required! Not used!
 */
public class SQLResultStorage {

	private ArrayList<String[]> rows = new ArrayList<String[]>();
	
	private String[] header = new String[0];
	
	private String sql = "";
	
	public SQLResultStorage(String sqlQuery) {
		// init everything
		rows.clear();
		header = new String[0];
		// store the query which has caused the results
		this.sql = sqlQuery;
	}
	
	
	public void addRow(String[] r) {
		this.rows.add(r);
	}
	
	public ArrayList<String[]> getRows() {
		return this.rows;
	}
	
	
	public void setHeader(String[] h) {
		this.header = h.clone();
	}
	
	public String[] getHeader() {
		return this.header.clone();
	}
	
	
	/*
	 * public String buildHTML ???
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
