/**
 * Resources to use in application.
 * Everything here should be declared as final.
 * 
 * @author Paul Wagner
 */
package de.typology.tools;

public final class Resources {

	/**
	 * VERSION NUMBERS
	 */
	public static final Double VERSION_NUMBER = 1.1;
	public static final String VERSION_STRING = "1.1";
	public static final Double CLIENT_SUPPORTED_NUMBER = 0.3;
	public static final String CLIENT_SUPPORTED_STRING = "0.3";
	public static final Double CLIENT_MINIMUM_NUMBER = 0.3;
	public static final String CLIENT_MINIMUM_STRING = "0.3";
	
	/**
	 * LANGUAGE FLAGS
	 */
	public static final int LN_DE = 0;
	public static final int LN_EN = 1;
	public static final int LN_MAX = 1;
	
	/**
	 * FUNCTION FLAGS
	 */
	public static final int FN_INITIATESESSION = 0;
	public static final int FN_GETQUERY = 1;
	public static final int FN_GETRESULT = 2;
	public static final int FN_GETPRIMITIVE = 3;
	public static final int FN_GETMORE = 4;
	public static final int FN_ENDSESSION = 5;
	public static final int FN_STORETEXT = 6;
	public static final int FN_STOREMETRICS = 7;
	public static final int FN_GETMETRICS = 8;
	

	/**
	 * SERVLET CONTEXT FLAGS
	 */
	
	/**
	 * DEFAULT CONFIG VALUES
	 */
	// KEYS
	public static final String DEF_NAME_KEY = "word";
	public static final String DEF_COUNT_KEY = "cnt";
	public static final String DEF_REL_KEY = "rel_loc";
	public static final String DEF_DATE_KEY = "date";
	// RETRIEVAL CONFIG
	public static final int DEF_P1 = 5;
	public static final int DEF_P2 = 2;
	public static final int DEF_P3 = 2;
	public static final int DEF_P4 = 0;
	public static final int DEF_MAX_RELATIONS = 4;
	public static final int DEF_RESULT_SIZE = 10;
	public static final int DEF_RETRIEVAL_SIZE = 50;
	public static final int DEF_RET_TIMEOUT = 10; // in seconds
	// NEO4J Settions
	public static final int DEF_RELATIONSHIPSTORE_MEM = 100;
	public static final int DEF_PROPERTYSTORE_MEM = 150;
	public static final String DEF_CACHE_TYPE = "strong";
	public static final String DEF_DB_PATH = "";
	// MYSQL SETTINGS
	public static final String DEF_MYSQL_HOST = "localhost";
	public static final String DEF_MYSQL_DB_LOG = "typology";
	public static final String DEF_MYSQL_USER = "root";
	public static final String DEF_MYSQL_PASS = "";
	// MAINTANENCE SETTINGS
	// Generally turn on/off every append of outside data
	public static final Boolean DEF_APPEND_NEW_DATA = true;
	// Norms directly after new data has been appended
	public static final Boolean DEF_NORM_AFTER_APPEND = true;
	// Flag to turn off personalized data appending (but not personalized
	// requests, if data already aviable!)
	public static final Boolean DEF_PERSONALIZE_DATABASE = true;
	
	/**
	 * COMMUNICATION FLAGS
	 */
	public static final int SC_ERR  = -1;
	public static final int SC_SUCC = 0;

	public static final int SC_WRN_RET_INTERRUPTED = 1;
	public static final int SC_WRN_RET_TIMEOUT = 2;
	
	public static final int SC_ERR_NO_SESSION = 3;
	public static final int SC_ERR_TOO_OLD_VERSION = 4;
	public static final int SC_ERR_TOO_NEW_VERSION = 5;
	
}
