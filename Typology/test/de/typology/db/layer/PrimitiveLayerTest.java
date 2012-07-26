/**
 * Test case for primitivelayer class
 * 
 * @author Paul Wagner
 */
package de.typology.db.layer;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import de.typology.SetupHelperMethods;
import de.typology.db.persistence.IDBConnection;
import de.typology.tools.ConfigHelper;

@RunWith(PowerMockRunner.class)
//@PrepareForTest({IDBConnection.class}) <-- triggers cache type soft not found error in neo4j database factory
public class PrimitiveLayerTest {

	public static IDBConnection db;
	public static PrimitiveLayer layer;
	public static GraphDatabaseService graph;
	
	
	// SETUP
	
	/**
	 * Setup db connection before layer test
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SetupHelperMethods.initiateContextSupport();
		
		graph = new TestGraphDatabaseFactory()
		.newImpermanentDatabaseBuilder().newGraphDatabase();
		Transaction tx = graph.beginTx();
		createNodeProperties("Das", 3);
		createNodeProperties("allein", 1);
		createNodeProperties("ein", 2);
		createNodeProperties("Sind", 2);
		tx.success();
		tx.finish();
	}
	
	private static void createNodeProperties(String name, int count){
		Node n = graph.createNode();
		n.setProperty(ConfigHelper.getNAME_KEY(), name);
		n.setProperty(ConfigHelper.getCOUNT_KEY(), count);	
	}
	
	@Before
	public void setUp(){
		db = PowerMock.createMock(IDBConnection.class);
	}

	// TESTS
	
	@Test
	public void loadLayer_dbIsRunning_mapSizegreaterZero() {
		expect(db.isShutdown()).andReturn(false);
		expect(db.getGraph()).andReturn((EmbeddedGraphDatabase) graph);	
		expect(db.getGraph()).andReturn((EmbeddedGraphDatabase) graph);	
		replay(db);
		layer = new PrimitiveLayer(db);
		verify(db);

		assertTrue(layer.getNodeMap().size() > 0);
	}
	
	@Test
	public void loadLayer_dbIsShutdown_mapSizeEqualsZero(){
		expect(db.isShutdown()).andReturn(true);
		replay(db);
		layer = new PrimitiveLayer(db);
		verify(db);

		assertEquals(layer.getNodeMap().size(), 0);
	}
	
	@Test
	public void loadLayer_dbIsRunning_firstWordDas() {
		expect(db.isShutdown()).andReturn(false);
		expect(db.getGraph()).andReturn((EmbeddedGraphDatabase) graph);	
		expect(db.getGraph()).andReturn((EmbeddedGraphDatabase) graph);	
		replay(db);
		layer = new PrimitiveLayer(db);
		verify(db);

		HashMap<Integer, String> map = layer.getNodeMap();
		String first = "";
		for (Entry<Integer, String> e : map.entrySet()) {
			first = e.getValue();
			break;
		}
		assertEquals("Check if first map entry equals expected", "Das", first);
	}
	
	@Test
	public void loadLayer_lastWord_allein() {
		expect(db.isShutdown()).andReturn(false);
		expect(db.getGraph()).andReturn((EmbeddedGraphDatabase) graph);	
		expect(db.getGraph()).andReturn((EmbeddedGraphDatabase) graph);	
		replay(db);
		layer = new PrimitiveLayer(db);
		verify(db);

		HashMap<Integer, String> map = layer.getNodeMap();
		String last = "";
		for (Entry<Integer, String> e : map.entrySet()) {
			last = e.getValue();
		}
		assertEquals("Check if first map entry equals expected", "allein", last);
	}
	
	@Test
	public void loadLayer_equalValuesSortByKey_einBeforeSind() {
		expect(db.isShutdown()).andReturn(false);
		expect(db.getGraph()).andReturn((EmbeddedGraphDatabase) graph);	
		expect(db.getGraph()).andReturn((EmbeddedGraphDatabase) graph);	
		replay(db);
		layer = new PrimitiveLayer(db);
		verify(db);

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
