package sqlchecker;


public class SQLChecker {
	
	/*
	 * >> Structure
	 * 
	 * 1. Read solution
	 * - As string
	 * - Recognize placeholders (e.g. %q1%)
	 * a) read solution & its tags
	 * b) read submission(s)
	 * c) perform mapping
	 * d) run
	 * 
	 * 2. Read student's submission
	 * - Recognize query start and query end (let student use template?)
	 * 
	 * 3. Mapping PlaceholderId (soultion) --> QueryNumber (submission)
	 * - Ignore DELIMITER, custom delimiters are not needed
	 * 
	 * 4. Execute & Build Result document (html or xml?)
	 * 
	 * 
	 * >> Pipeline:
	 * 
	 * Input
	 * - Template & Content
	 * 
	 * 		||
	 * 
	 * Transform/Execute
	 * - Via a script/program
	 * 
	 * 		||
	 * 
	 * Output
	 * - Report (html/xml)
	 */
	

	public static void main(String[] args) {
		System.out.println("Not yet implemented!");
	}

}
