/**
 * Various static functions that can be called from within test cases/suites and initiate static context
 * 
 * @author Paul Wagner
 */
package de.typology;

import de.typology.tools.ConfigHelper;
import de.typology.tools.IOHelper;

public class SetupHelperMethods {

	public static String logfilename = System.getProperty("java.io.tmpdir")
			+ System.getProperty("file.separator") + "testsuite.log";

	public static String configfilename = "";

	/**
	 * Load everything in static context with needed values for testing.
	 */
	public static void initiateContextSupport() {
		initiateLogSupport();
		loadConfigContext();
	}

	/**
	 * Initialize IOHelper with temp logfile
	 */
	public static void initiateLogSupport() {
		IOHelper.initializeLog(logfilename);
	}

	/**
	 * Loading ConfigHelper
	 */
	public static void loadConfigContext() {
		if (!ConfigHelper.isLOADED() && !configfilename.isEmpty()) {
			try {
				ConfigHelper.loadConfigFile(configfilename);
			} catch (Exception e) {
				System.out.println("Failed to load config file");
			}
		}
	}

}
