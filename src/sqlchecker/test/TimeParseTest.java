package sqlchecker.test;

import dbfit.util.SqlTimeParseDelegate;

public class TimeParseTest {

	
	/*
	 * 
	 * This is the code of
	 * SqlTimeParseDelegate
	 * https://github.com/dbfit/dbfit/blob/1768328f7819d4f1857d0f6dc99664e248552d0c/dbfit-java/core/src/main/java/dbfit/util/SqlTimeParseDelegate.java
	 * 
	 * 
	static final SimpleDateFormat FMT_S = new SimpleDateFormat("HH:mm:ss");
    static final SimpleDateFormat FMT_MS = new SimpleDateFormat("HH:mm:ss.S");
    
    public static Object parse(String s) throws Exception {
        return (s == null) ? null : parseTime(s);
    }

    private static Time parseTime(final String s) throws ParseException {
        SimpleDateFormat df = s.contains(".") ? FMT_MS : FMT_S;
        return new Time(df.parse(s).getTime());
    }
    */
    
	
	// an adapted version of
	// https://github.com/dbfit/dbfit/blob/1768328f7819d4f1857d0f6dc99664e248552d0c/dbfit-java/core/src/main/java/dbfit/util/SqlTimeParseDelegate.java
	// testing the time parser of dbfit
	public static void main(String[] args) {
		String[] timeStr = new String[]{"48:00:00", "'48:00:00'"};


		for (String time : timeStr) {
			System.out.println("\n\n>TESTING: " + time);
			try {
				SqlTimeParseDelegate.parse(time);
				System.out.println("Success!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
