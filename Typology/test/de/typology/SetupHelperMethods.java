/**
 * Various static functions that can be called from within test cases/suites and initiate static context
 */
package de.typology;

import de.typology.tools.IOHelper;

public class SetupHelperMethods {
	
	public static String logfilename = System.getProperty("java.io.tmpdir")
			+ System.getProperty("file.separator") + "testsuite.log";
	
	/**
	 * Initialize IOHelper with temp logfile
	 */
	public static void initiateLogSupport() {
		IOHelper.initializeLog(logfilename);
	}

}
