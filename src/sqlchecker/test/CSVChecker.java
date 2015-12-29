package sqlchecker.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import sqlchecker.io.IOUtil;
import sqlchecker.io.impl.SimpleFileReader;

public class CSVChecker {

	public static final String LINE_SEPARATOR = "\n";
	
	private String path = "";
	
	
	
	
	
	public CSVChecker(String filePath) {
		this.path = filePath;
	}
	
	
	public void start() {
		
		// fetch files
		
		ArrayList<File> fListRaw = IOUtil.fetchFiles(path);
		
		// filter (only summary files)
		ArrayList<File> fList = new ArrayList<File>();
		
		for (int i = 0; i < fListRaw.size(); i++) {
			File f = fListRaw.get(i);
			// only add summary files
			if (f.getPath().contains("summary_")) {
				fList.add(f);
			}
		}
		
		System.out.println("\n\nList of CSV summary files\n");
		
		ArrayList<SimpleFileReader> fContents = new ArrayList<SimpleFileReader>();
		for (int i = 0; i < fList.size(); i++) {
			
			File f = fList.get(i);
			String p = f.getPath();
			
			// show path
			System.out.println(p);
			
			// Read content of every csv file
			SimpleFileReader sfr = new SimpleFileReader(p);
			sfr.loadFile();
			fContents.add(sfr);
		}
		
		System.out.println("\nRead " + fContents.size() + " files\n");
		
		
		// compare files
		
		for (int i = 0; i < fContents.size(); i++) {
			
			for (int j = i+1; j < fContents.size(); j++) {
				
				String diff = "";
				
				// load both filereader objects
				SimpleFileReader sfri = fContents.get(i);
				SimpleFileReader sfrj = fContents.get(j);
				
				// load content of both, as lines
				String[] ci = sfri.getFileContent().split(LINE_SEPARATOR);
				String[] cj = sfrj.getFileContent().split(LINE_SEPARATOR);
				
				System.out.println("\nComparing\n\t" 
					+ "1 = " + sfri.getFilePath() + "\n\t"
					+ "2 = " + sfrj.getFilePath());
				
				// line count
				int lines = Math.min(ci.length, cj.length); 
				if (ci.length != cj.length) {
					System.out.println("Detected different amount of lines \n" 
						+ "(1) " + ci.length + " vs (2)" + cj.length);
				}
				System.out.println("Reading lines 1 - " + lines);
				
				for (int li = 0; li < lines; li++) {
					// for each line in both files
					String si = ci[li];
					String sj = cj[li];
					
					// do string comparison
					boolean isEqual = si.equals(sj);
					if (!isEqual) {
						System.out.println("[ " + li + " ]");
						if (!diff.isEmpty()) diff += ", ";
						diff += String.valueOf(li);
					}
					
				}
				
				int diffCount = 0;
				
				if (!diff.isEmpty())
					diffCount = diff.split(",").length;
				
				System.out.println("Found " + diffCount + " difference(s):");
				System.out.println(diff);
				
			}
		}
		
		
		
		
		
	}
	
	
	public static void main(String[] args) {
		
		final String path = "private/kh_b3/email_test28122015/";
		
		CSVChecker csvc = new CSVChecker(path);
		csvc.start();
		
	}

}
