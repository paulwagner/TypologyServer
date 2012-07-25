/**
 * Test case for RequestTools class.
 * 
 * @author Paul Wagner
 * 
 */
package de.typology.requests;

import static de.typology.requests.RequestTools.fillResultSet;
import static de.typology.requests.RequestTools.translateFunctionName;
import static de.typology.tools.Resources.FN_ENDSESSION;
import static de.typology.tools.Resources.FN_GETMETRICS;
import static de.typology.tools.Resources.FN_GETMORE;
import static de.typology.tools.Resources.FN_GETPRIMITIVE;
import static de.typology.tools.Resources.FN_GETQUERY;
import static de.typology.tools.Resources.FN_GETRESULT;
import static de.typology.tools.Resources.FN_INITIATESESSION;
import static de.typology.tools.Resources.FN_STOREMETRICS;
import static de.typology.tools.Resources.FN_STORETEXT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import de.typology.tools.ConfigHelper;

public class RequestToolsTest {

	private static HashMap<Integer, String> smallerList = new HashMap<Integer, String>();
	private static HashMap<Integer, String> biggerList = new HashMap<Integer, String>();
	
	// SETUP
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		for(int i = 0; i < ConfigHelper.getRESULT_SIZE() / 2; i++){
			smallerList.put(i, "testdata");
		}
		for(int i = 0; i < ConfigHelper.getRESULT_SIZE() * 5; i++){
			biggerList.put(i, "testdata");
		}		
	}

	// TESTS
	
	// fillResultSet
	
	@Test
	public void fillResultSet_fillSmallerList_listIsComplete(){
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		fillResultSet(smallerList, result, 0);
		assertEquals("Check if result list is as big as the small starting list", smallerList.size(), result.size());
	}

	@Test
	public void fillResultSet_fillBiggerList_listIsFull(){
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		fillResultSet(biggerList, result, 0);
		assertEquals("Check if result list is full", ConfigHelper.getRESULT_SIZE(), result.size());
	}

	@Test
	public void fillResultSet_fillBiggerListWithStarting_listIsFullAndStarting(){
		int starting = ConfigHelper.getRESULT_SIZE() / 2;
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		fillResultSet(biggerList, result, starting);
		assertEquals("Check if result list is full", ConfigHelper.getRESULT_SIZE(), result.size());
		assertNotNull("Check if first result entry is the starting one", biggerList.get(starting));
	}

	
	// translateFunctionName()
	
	@Test
	public void translateFunctionName_translateGetQuery() {
		assertEquals("Check if functionname translation for GETQUERY is correct", FN_GETQUERY, translateFunctionName("getquery"));
	}

	@Test
	public void translateFunctionName_translateGetResult() {
		assertEquals("Check if functionname translation for GETRESULT is correct", FN_GETRESULT, translateFunctionName("getresult"));
	}

	@Test
	public void translateFunctionName_translateInitiateSession() {
		assertEquals("Check if functionname translation for INITIATESESSION is correct", FN_INITIATESESSION, translateFunctionName("initiatesession"));
	}

	@Test
	public void translateFunctionName_translateEndSession() {
		assertEquals("Check if functionname translation for ENDSESSION is correct", FN_ENDSESSION, translateFunctionName("endsession"));
	}

	@Test
	public void translateFunctionName_translateGetPrimitive() {
		assertEquals("Check if functionname translation for GETPRIMITIVE is correct", FN_GETPRIMITIVE, translateFunctionName("getprimitive"));
	}

	@Test
	public void translateFunctionName_translateGetMore() {
		assertEquals("Check if functionname translation for GETMORE is correct", FN_GETMORE, translateFunctionName("getmore"));
	}

	@Test
	public void translateFunctionName_translateGetMetrics() {
		assertEquals("Check if functionname translation for GEMETRICS is correct", FN_GETMETRICS, translateFunctionName("getmetrics"));
	}

	@Test
	public void translateFunctionName_translateStoreMetrics() {
		assertEquals("Check if functionname translation for STOREMETRICS is correct", FN_STOREMETRICS, translateFunctionName("storemetrics"));
	}

	@Test
	public void translateFunctionName_translateStoreText() {
		assertEquals("Check if functionname translation for STORETEXT is correct", FN_STORETEXT, translateFunctionName("storetext"));
	}	
}
