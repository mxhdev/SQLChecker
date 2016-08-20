package sqlchecker.test;

import java.util.ArrayList;

import sqlchecker.config.Config;
import sqlchecker.core.DBFitFacade;
import sqlchecker.io.impl.SimpleFileReader;

public class DBFitFacadeTest {
	
	
	private String fileName = "";
	
	private Config cProps;
	
	private ArrayList<String> names = new ArrayList<String>();
	
	private ArrayList<String> mnr = new ArrayList<String>();
	
	public DBFitFacadeTest(String fileName, Config connProps, ArrayList<String> names, ArrayList<String> mnr) {
		this.fileName = fileName;
		
		this.cProps = connProps;
		
		this.names.clear();
		this.names.addAll(names);
		
		this.mnr.clear();
		this.mnr.addAll(mnr);
	}
	
	
	
	public void runTest() {
		
		SimpleFileReader sfr = new SimpleFileReader(fileName);
		sfr.loadFile();
		String fContent = sfr.getFileContent(); 
		
		DBFitFacade dbf = new DBFitFacade(fileName, cProps);
		
		try {
			dbf.runSubmission(fContent, this.names, this.mnr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("> Test finished <");
	}
	
	

	public static void main(String[] args) {
		//String[] connProps = new String[]{"localhost", "root", "start", "krankenhaus"};
		
		ArrayList<String> name = new ArrayList<String>();
		name.add("TestName");
		
		ArrayList<String> mnr = new ArrayList<String>();
		mnr.add("123456");
		
		String fname = "private/tests/testFachname.txt";
		
		
		/*
		 * Execute the test
		 */
		
		Config conf = new Config("root", "", "localhost", "krankenhaus");
		
		DBFitFacadeTest tester = new DBFitFacadeTest(fname, conf, name, mnr);
		tester.runTest();
		
	}

}
