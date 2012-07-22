/**
 * Test case for class ConfigHelper
 */
package de.typology.tools;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigHelperTest {

	public static String filename = System.getProperty("java.io.tmpdir")
			+ System.getProperty("file.separator") + "sampleconfig.ini";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Make testfile
		BufferedWriter wr = IOHelper.openWriteFile(filename);
		wr.write(getSampleConfig());
		wr.flush();
		wr.close();
		// Load values
		ConfigHelper.loadConfigFile(filename);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// Delete Testfile
		boolean success = (new File(filename)).delete();
		if (!success) {
			System.out
					.println("WARNING: Failed to tear down ConfigHelperTest! Sample config file not deleted.");
		}
	}

	/**
	 * Test method for
	 * {@link de.typology.tools.ConfigHelper#loadConfigFile(java.lang.String)}.
	 */
	@Test
	public void loadConfigFile_loadNameKey_word() {
		assertEquals("Check equality NAME_KEY", ConfigHelper.getNAME_KEY(),
				"word");
	}

	/**
	 * Test method for
	 * {@link de.typology.tools.ConfigHelper#loadConfigFile(java.lang.String)}.
	 */
	@Test
	public void loadConfigFile_loadResultSize_20() {
		assertEquals("Check equality RESULT_SIZE",
				ConfigHelper.getRESULT_SIZE(), 20);
	}

	/**
	 * Test method for
	 * {@link de.typology.tools.ConfigHelper#loadConfigFile(java.lang.String)}.
	 */
	@Test
	public void loadConfigFile_loadDbPath_path() {
		assertEquals("Check equality DB_PATH", ConfigHelper.getDB_PATH(),
				"/usr/local/notexisting.db");
	}

	/**
	 * Test method for
	 * {@link de.typology.tools.ConfigHelper#loadConfigFile(java.lang.String)}.
	 */
	@Test
	public void loadConfigFile_loadAppendData_true() {
		assertTrue("Check equality APPEND_NEW_DATA",
				ConfigHelper.getAPPEND_NEW_DATA());
	}

	/**
	 * Test method for
	 * {@link de.typology.tools.ConfigHelper#loadConfigFile(java.lang.String)}.
	 */
	@Test
	public void loadConfigFile_loaded_true() {
		assertTrue("Check if loaded flag is true", ConfigHelper.isLOADED());
	}

	/**
	 * Test method for
	 * {@link de.typology.tools.ConfigHelper#loadConfigFile(java.lang.String)}.
	 * 
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void loadConfigFile_loadConfigTwice_exception() throws Exception {
		ConfigHelper.loadConfigFile(filename);
	}

	/**
	 * Helper method for creating sample config file
	 */
	private static String getSampleConfig() {
		StringBuilder sb = new StringBuilder();
		sb.append("### SAMPLE CONFIG - DELETE IF NO TESTS RUNNING ###\n");
		sb.append("NAME_KEY=word\n");
		sb.append("RESULT_SIZE= 20\n");
		sb.append("DB_PATH =/usr/local/notexisting.db\n");
		sb.append("APPEND_NEW_DATA  = true\n");
		return sb.toString();
	}

}
