/**
 * Test case for DBConnection. This test really creates a database on your drive,
 * which is uncommon for a test, but the only thing we can test here.
 * 
 * TODO think over test again. dependency to DBTools is not nice... -> as test helper method
 * TODO also test norming
 * 
 * @author Paul Wagner
 */
package de.typology.db.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.typology.SetupHelperMethods;
import de.typology.tools.ConfigHelper;

public class DBConnectionTest {

	public static DBConnection db;
	public static String filename = System.getProperty("java.io.tmpdir")
			+ System.getProperty("file.separator") + "testdb.db";

	// SETUP

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SetupHelperMethods.initiateContextSupport();

		// Delete File because of persistence
		File file = new File(filename);
		if (file.exists()) {
			if (!deleteDirectory(file)) {
				fail("Previous db file couldn't be deleted, can't perform tests!");
			}
		}
		db = new DBConnection(filename);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.shutdown();
		File file = new File(filename);
		if (file.exists()) {
			if (!deleteDirectory(file)) {
				fail("Previous db file couldn't be deleted, can't perform tests!");
			}
		}
	}

	// TESTS

	@Test
	public void getGraph_isGraphAccessible_notNull(){
		assertNotNull("Check if created graph object is not null", db.getGraph());
	}
	
	@Test
	public void getWordIndey_isWordIndexAccessible_notNull(){
		assertNotNull("Check if created word index object is not null", db.getWordIndex());
	}	

	@Test
	public void getDn_isRTAccessible_lengthDefined(){
		assertNotNull("Check if created relationship types are defined", db.getDn());
		assertEquals("Check if created relationship types are at the right length", db.getDn().length, ConfigHelper.getMAX_RELATIONS() + 1);
	}	

	@Test
	public void getNeo4jVersion_retrieveVersion_notEmpty() {
		assertFalse("Check if library has loaded", db.getNeo4JVersion()
				.isEmpty());
	}

	@Test
	public void getRefNode_referenceNode_equalsGraphRefNode() {
		assertEquals("Check if reference node equals the graph one", db.getReferenceNode(), db.getGraph().getReferenceNode());
	}

	// HELPER

	private static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

}
