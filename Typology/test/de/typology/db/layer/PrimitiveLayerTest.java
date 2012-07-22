/**
 * Test case for primitivelayer class
 * 
 * @author Paul Wagner
 */
package de.typology.db.layer;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.typology.SetupHelperMethods;
import de.typology.db.persistence.IDBConnection;
import de.typology.db.persistence.impl.ImpermanentDBConnection;

public class PrimitiveLayerTest {

	public static IDBConnection db;
	public static PrimitiveLayer layer;
	
	
	// SETUP
	
	/**
	 * Setup db connection before layer test
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SetupHelperMethods.initiateContextSupport();
		db = new ImpermanentDBConnection();
		((ImpermanentDBConnection) db).fillWithText("Das sind zwei Worte. Das ist ein Wort. Das sollte am häufigsten sein. sind zwei Worte. ist ein Wort. sollte nicht am häufigsten sein. allein ist nur nur nicht.");
		layer = new PrimitiveLayer(db);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.shutdown();
	}


	// TESTS
	
	/**
	 * Test method for {@link de.typology.db.layer.PrimitiveLayer#loadLayer()}.
	 * Check if layer map has any data stored.
	 */
	@Test
	public void loadLayer_layerMapSize_greaterZero() {
		assertTrue(layer.getNodeMap().size() > 0);
	}
	
	/**
	 * Test method for {@link de.typology.db.layer.PrimitiveLayer#loadLayer()}.
	 * Check if first word with current test database is "Das"
	 */
	@Test
	public void loadLayer_firstWord_Das() {
		HashMap<Integer, String> map = layer.getNodeMap();
		String first = "";
		for (Entry<Integer, String> e : map.entrySet()) {
			first = e.getValue();
			break;
		}
		assertEquals("Check if first map entry equals expected", "Das", first);
	}
	
	/**
	 * Test method for {@link de.typology.db.layer.PrimitiveLayer#loadLayer()}.
	 * Check if last word with current test database is "allein"
	 */
	@Test
	public void loadLayer_lastWord_allein() {
		HashMap<Integer, String> map = layer.getNodeMap();
		String last = "";
		for (Entry<Integer, String> e : map.entrySet()) {
			last = e.getValue();
		}
		assertEquals("Check if first map entry equals expected", "allein", last);
	}
	
	/**
	 * Test method for {@link de.typology.db.layer.PrimitiveLayer#loadLayer()}.
	 * Check if words with equal values sort by key
	 */
	@Test
	public void loadLayer_equalValuesSortByKey_einBeforeSind() {
		HashMap<Integer, String> map = layer.getNodeMap();
		String word = "";
		for (Entry<Integer, String> e : map.entrySet()) {
			word = e.getValue();
			if(word.equals("ein") || word.equals("sind")){
				break;
			}
		}
		assertEquals("Check if 'ein' is before 'sind'", "ein", word);
	}
	

}
