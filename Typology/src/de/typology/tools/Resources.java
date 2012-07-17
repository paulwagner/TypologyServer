/**
 * Resources to use in application.
 * Everything here should be declared as final.
 * 
 * @author Paul Wagner
 */
package de.typology.tools;

public final class Resources {

	/**
	 * LANGUAGE FLAGS
	 */
	public static final int LN_DE = 0;
	public static final int LN_EN = 1;
	public static final int LN_MAX = 1;

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
	public static final String DEF_UID_KEY = "uid";
	public static final String DEF_DATE_KEY = "date";
	public static final int DEF_MAX_RELATIONS = 4;
	public static final int DEF_RELATIONSHIPSTORE_MEM = 100;
	public static final int DEF_PROPERTYSTORE_MEM = 150;
	public static final String DEF_CACHE_TYPE = "strong";
	public static final String DEF_DB_PATH = "";
	// RETRIEVAL CONFIG
	public static final int DEF_P1 = 5;
	public static final int DEF_P2 = 2;
	public static final int DEF_P3 = 2;
	public static final int DEF_P4 = 0;
	public static final int DEF_RESULT_SIZE = 10;
	public static final int DEF_RET_TIMEOUT = 5; // in seconds
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
	public static final int SC_RET_INTERRUPTED = 1;
	public static final int SC_RET_TIMEOUT = 2;
	
	public static final int CS_TYPE_DEV = 0;
	public static final int CS_TYPE_SESSION = 1;

}
