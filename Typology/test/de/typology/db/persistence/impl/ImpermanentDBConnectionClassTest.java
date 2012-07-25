/**
 * Class test case for ImpermanentDBConnection.
 * Test mainly method fillWithText() because this invokes all the action the class has.
 *
 * @author Paul Wagner
 */
package de.typology.db.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import de.typology.SetupHelperMethods;
import de.typology.tools.ConfigHelper;

public class ImpermanentDBConnectionClassTest {

	private static ImpermanentDBConnection db;

	// SETUP

	/**
	 * Initialize log and database
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SetupHelperMethods.initiateContextSupport();
		db = new ImpermanentDBConnection();
		db.fillWithText("Ein Test mit mehreren Wörtern. Und mit einem anderen Satz, aber ohne Komma. Ein Test. Ein anderes.");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.shutdown();
	}

	// TESTS

	// Readonly Tests with Nodes

	@Test
	public void node_mitWeight_2() {
		Node mit = db.getWordIndex().get(ConfigHelper.getNAME_KEY(), "mit")
				.getSingle();
		int c = (Integer) mit.getProperty(ConfigHelper.getCOUNT_KEY());
		assertEquals("Check node weight of node 'mit'", 2, c);

	}

	@Test
	public void node_hasComma_Satz() {
		for (Node n : db.getGraph().getAllNodes()) {
			if (db.isReferenceNode(n)) {
				continue;
			}
			if (((String) n.getProperty(ConfigHelper.getNAME_KEY()))
					.equals("Satz,")) {
				return;
			}
		}
		fail("The node 'Satz,' didn't show up in node list!");
	}

	@Test
	public void node_hasUmlaut_Woertern() {
		for (Node n : db.getGraph().getAllNodes()) {
			if (db.isReferenceNode(n)) {
				continue;
			}
			if (((String) n.getProperty(ConfigHelper.getNAME_KEY()))
					.equals("Wörtern")) {
				return;
			}
		}
		fail("The node 'Wörtern' didn't show up in node list!");
	}

	// Readonly Tests with relationships

	@Test
	public void relationship_NodemitEdge1_mehrerenAndeinem() {
		Node mit = db.getWordIndex().get(ConfigHelper.getNAME_KEY(), "mit")
				.getSingle();
		String s = "";

		for (Relationship r : mit.getRelationships(Direction.OUTGOING,
				db.getDn()[1])) {
			s += r.getEndNode().getProperty(ConfigHelper.getNAME_KEY()) + ".";
		}

		assertTrue("Check outgoing 1 edges from 'mit'",
				s.equals("mehreren.einem.") || s.equals("einem.mehreren."));
	}

	@Test
	public void relationship_NodeanderenEdge2_aber() {
		Node anderen = db.getWordIndex()
				.get(ConfigHelper.getNAME_KEY(), "anderen").getSingle();
		String s = "";

		for (Relationship r : anderen.getRelationships(Direction.OUTGOING,
				db.getDn()[2])) {
			s = (String) r.getEndNode().getProperty(ConfigHelper.getNAME_KEY());
			break;
		}

		assertEquals("Check outgoing 2 edges from 'anderen'", "aber", s);
	}

	@Test
	public void relationship_NodeanderenEdge3_ohne() {
		Node anderen = db.getWordIndex()
				.get(ConfigHelper.getNAME_KEY(), "anderen").getSingle();
		String s = "";

		for (Relationship r : anderen.getRelationships(Direction.OUTGOING,
				db.getDn()[3])) {
			s = (String) r.getEndNode().getProperty(ConfigHelper.getNAME_KEY());
			break;
		}

		assertEquals("Check outgoing 3 edges from 'anderen'", "ohne", s);
	}

	@Test
	public void relationship_NodeanderenEdge4_Komma() {
		Node anderen = db.getWordIndex()
				.get(ConfigHelper.getNAME_KEY(), "anderen").getSingle();
		String s = "";

		for (Relationship r : anderen.getRelationships(Direction.OUTGOING,
				db.getDn()[4])) {
			s = (String) r.getEndNode().getProperty(ConfigHelper.getNAME_KEY());
			break;
		}

		assertEquals("Check outgoing 4 edges from 'anderen'", "Komma", s);
	}

	@Test
	public void relationship_NodeWoerternEdge1_Nothing() {
		Node anderen = db.getWordIndex()
				.get(ConfigHelper.getNAME_KEY(), "Wörtern").getSingle();
		boolean haveHits = true;

		// Use iterator for avoiding warnings because of unused loop variable
		for (Iterator<Relationship> iterator = anderen.getRelationships(Direction.OUTGOING,
				db.getDn()[1]).iterator(); iterator
				.hasNext();) {
			haveHits = false;
			break;
		}

		assertTrue("Check outgoing 1 edges from 'Wörtern'", haveHits);
	}

	@Test
	public void relationship_WeightFromNodeTest_1() {
		Node Test = db.getWordIndex().get(ConfigHelper.getNAME_KEY(), "Test")
				.getSingle();
		int c = 0;

		for (Relationship r : Test.getRelationships(Direction.OUTGOING,
				db.getDn()[1])) {
			c = (Integer) r.getProperty(ConfigHelper.getCOUNT_KEY());
			break;
		}

		assertEquals("Check weight of outgoing 1 edges from 'Test'", 1, c);
	}
	

	@Test
	public void relationship_NormFromNodemit_1() {
		Node mit = db.getWordIndex().get(ConfigHelper.getNAME_KEY(), "mit")
				.getSingle();
		double c = 0;

		for (Relationship r : mit.getRelationships(Direction.OUTGOING,
				db.getDn()[1])) {
			c = (Double) r.getProperty(ConfigHelper.getREL_KEY());
			break;
		}

		assertEquals(1.0d, c, 0.01d);
	}	

	@Test
	public void relationship_NormFromNodeEin_1And0p5() {
		Node Ein = db.getWordIndex().get(ConfigHelper.getNAME_KEY(), "Ein")
				.getSingle();
		double c_Test = 0;
		double c_anderes = 0;

		for (Relationship r : Ein.getRelationships(Direction.OUTGOING,
				db.getDn()[1])) {
			if(((String) r.getEndNode().getProperty(ConfigHelper.getNAME_KEY())).equals("Test")){
				c_Test = (Double) r.getProperty(ConfigHelper.getREL_KEY()); 
			}
			if(((String) r.getEndNode().getProperty(ConfigHelper.getNAME_KEY())).equals("anderes")){
				c_anderes = (Double) r.getProperty(ConfigHelper.getREL_KEY()); 
			}
		}

		assertEquals(1.0d, c_Test, 0.01d);
		assertEquals(0.5d, c_anderes, 0.01d);
	}	
	
}
