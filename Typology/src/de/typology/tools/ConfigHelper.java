/**
 * Helper to handle constants and config files
 *
 * All values are global static values used in complet
 * To use config file instead of standard values call loadConfigFile()
 *
 * Note that PATH variables can be overwritten by calling classes!
 *
 * @author Paul Wagner
 *
 */

package de.typology.tools;

import java.io.FileInputStream;
import java.util.Properties;
import static de.typology.tools.Resources.*;

public final class ConfigHelper {

	
	// PROPERTIES
	
	private static boolean LOADED = false;

	// MAIN CONFIG
	private static String NAME_KEY = DEF_NAME_KEY;
	private static String COUNT_KEY = DEF_COUNT_KEY;
	private static String REL_KEY = DEF_REL_KEY;
	private static String UID_KEY = DEF_UID_KEY;
	private static String DATE_KEY = DEF_DATE_KEY;
	// RETRIEVAL CONFIG
	private static int P1 = DEF_P1;
	private static int P2 = DEF_P2;
	private static int P3 = DEF_P3;
	private static int P4 = DEF_P4;
	private static int MAX_RELATIONS = DEF_MAX_RELATIONS;
	private static int RESULT_SIZE = DEF_RESULT_SIZE;
	private static int RET_TIMEOUT = DEF_RET_TIMEOUT;
	// SERVER SETTINGS (Neo4J)
	private static int RELATIONSHIPSTORE_MEM = DEF_RELATIONSHIPSTORE_MEM;
	private static int PROPERTYSTORE_MEM = DEF_PROPERTYSTORE_MEM;
	private static String CACHE_TYPE = DEF_CACHE_TYPE;
	private static String DB_PATH = DEF_DB_PATH;	
	// MAINTANENCE SETTINGS
	private static Boolean APPEND_NEW_DATA = DEF_APPEND_NEW_DATA;
	private static Boolean NORM_AFTER_APPEND = DEF_NORM_AFTER_APPEND;
	private static Boolean PERSONALIZE_DATABASE = DEF_PERSONALIZE_DATABASE;

	
	// METHODS
	
	/**
	 * Loading config values by config file.
	 * Note that this can be done just ONCE on runtime, because of thread issues.
	 * So make sure it's the right file...
	 * 
	 * @param config_file
	 */
	public static void loadConfigFile(String config_file) {
		if (!config_file.isEmpty() && !LOADED) {
			try {
				Properties p = new Properties();
				p.load(new FileInputStream(config_file));
				
				// MAIN CONFIG
				DB_PATH = p.getProperty("DB_PATH", DB_PATH);
				NAME_KEY = p.getProperty("NAME_KEY", NAME_KEY);
				UID_KEY = p.getProperty("UID_KEY", UID_KEY);
				COUNT_KEY = p.getProperty("COUNT_KEY", COUNT_KEY);
				REL_KEY = p.getProperty("RELLOC_KEY", REL_KEY);
				DATE_KEY = p.getProperty("DATE_KEY", DATE_KEY);
				
				// RETRIEVAL CONFIG
				P1 = Integer.parseInt(p.getProperty("P1", P1 + ""));
				P2 = Integer.parseInt(p.getProperty("P2", P2 + ""));
				P3 = Integer.parseInt(p.getProperty("P3", P3 + ""));
				P4 = Integer.parseInt(p.getProperty("P4", P4 + ""));
				RESULT_SIZE = Integer.parseInt(p.getProperty("RESULT_SIZE", RESULT_SIZE + ""));
				RET_TIMEOUT = Integer.parseInt(p.getProperty("RET_TIMEOUT", RET_TIMEOUT + ""));
				MAX_RELATIONS = Integer.parseInt(p.getProperty("MAX_RELATIONS",
						MAX_RELATIONS + ""));
				
				// SERVER SETTINGS (Neo4J)				
				RELATIONSHIPSTORE_MEM = Integer.parseInt(p.getProperty(
						"RELATIONSHIPSTORE_MEM", RELATIONSHIPSTORE_MEM + ""));
				PROPERTYSTORE_MEM = Integer.parseInt(p.getProperty(
						"PROPERTYSTORE_MEM", PROPERTYSTORE_MEM + ""));
				CACHE_TYPE = p.getProperty("CACHE_TYPE", CACHE_TYPE);
				
				// MAINTANENCE SETTINGS
				if (p.getProperty("APPEND_NEW_DATA", APPEND_NEW_DATA.toString())
						.toUpperCase().equals("TRUE")) {
					APPEND_NEW_DATA = true;
				} else {
					APPEND_NEW_DATA = false;
				}
				if (p.getProperty("NORM_AFTER_APPEND",
						NORM_AFTER_APPEND.toString()).toUpperCase()
						.equals("TRUE")) {
					NORM_AFTER_APPEND = true;
				} else {
					NORM_AFTER_APPEND = false;
				}
				if (p.getProperty("PERSONALIZE_DATABASE",
						PERSONALIZE_DATABASE.toString()).toUpperCase()
						.equals("TRUE")) {
					PERSONALIZE_DATABASE = true;
				} else {
					PERSONALIZE_DATABASE = false;
				}
				
				LOADED = true;
			} catch (Exception e) {
				IOHelper.logError("(ConfigHelper) Error parsing config file...");
			}
		}

	}


	/**
	 * @return the lOADED
	 */
	public static final boolean isLOADED() {
		return LOADED;
	}


	/**
	 * @return the nAME_KEY
	 */
	public static final String getNAME_KEY() {
		return NAME_KEY;
	}


	/**
	 * @return the cOUNT_KEY
	 */
	public static final String getCOUNT_KEY() {
		return COUNT_KEY;
	}


	/**
	 * @return the rEL_KEY
	 */
	public static final String getREL_KEY() {
		return REL_KEY;
	}


	/**
	 * @return the uID_KEY
	 */
	public static final String getUID_KEY() {
		return UID_KEY;
	}


	/**
	 * @return the dATE_KEY
	 */
	public static final String getDATE_KEY() {
		return DATE_KEY;
	}


	/**
	 * @return the p1
	 */
	public static final int getP1() {
		return P1;
	}


	/**
	 * @return the p2
	 */
	public static final int getP2() {
		return P2;
	}


	/**
	 * @return the p3
	 */
	public static final int getP3() {
		return P3;
	}


	/**
	 * @return the p4
	 */
	public static final int getP4() {
		return P4;
	}

	/**
	 * @return the result size
	 */
	public static final int getRESULT_SIZE() {
		return RESULT_SIZE;
	}

	/**
	 * @return the retrieval timeout
	 */
	public static final int getRET_TIMEOUT() {
		return RET_TIMEOUT;
	}

	/**
	 * @return the mAX_RELATIONS
	 */
	public static final int getMAX_RELATIONS() {
		return MAX_RELATIONS;
	}


	/**
	 * @return the rELATIONSHIPSTORE_MEM
	 */
	public static final int getRELATIONSHIPSTORE_MEM() {
		return RELATIONSHIPSTORE_MEM;
	}


	/**
	 * @return the pROPERTYSTORE_MEM
	 */
	public static final int getPROPERTYSTORE_MEM() {
		return PROPERTYSTORE_MEM;
	}


	/**
	 * @return the cACHE_TYPE
	 */
	public static final String getCACHE_TYPE() {
		return CACHE_TYPE;
	}


	/**
	 * @return the dB_PATH
	 */
	public static final String getDB_PATH() {
		return DB_PATH;
	}


	/**
	 * @return the aPPEND_NEW_DATA
	 */
	public static final Boolean getAPPEND_NEW_DATA() {
		return APPEND_NEW_DATA;
	}


	/**
	 * @return the nORM_AFTER_APPEND
	 */
	public static final Boolean getNORM_AFTER_APPEND() {
		return NORM_AFTER_APPEND;
	}


	/**
	 * @return the pERSONALIZE_DATABASE
	 */
	public static final Boolean getPERSONALIZE_DATABASE() {
		return PERSONALIZE_DATABASE;
	}
	
	
}
