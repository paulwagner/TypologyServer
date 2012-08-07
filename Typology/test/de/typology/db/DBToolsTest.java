/**
 * Method test case for DBTools.
 * 
 * @author Paul Wagner
 */
package de.typology.db;

import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import de.typology.SetupHelperMethods;

public class DBToolsTest {

	private GraphDatabaseService graph;

	// SETUP
	
	private static enum RelType implements RelationshipType
	{
	    TEST
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		SetupHelperMethods.initiateContextSupport();
	}

	@Before
	public void setUp() throws Exception {
		graph = new TestGraphDatabaseFactory()
		.newImpermanentDatabaseBuilder().newGraphDatabase();
	}

	@After
	public void tearDown() throws Exception {
		graph.shutdown();
	}

	// TESTS
	
	@Test
	public void deleteRelationship_createAndDeleteRelationship() {
		Transaction tx = graph.beginTx();
		Node n1 = graph.createNode();
		Node n2 = graph.createNode();
		n1.createRelationshipTo(n2, RelType.TEST);

		DBTools.deleteRelationships(n1);

		boolean hasRelationships = false;
		for (Iterator<Relationship> iterator = n1.getRelationships().iterator(); iterator
				.hasNext();) {
			hasRelationships = true;
			break;
		}

		tx.success();
		tx.finish();

		assertFalse("Check if tested node has no relationships anymore",
				hasRelationships);
	}

	@Test
	public void deleteNode_createAndDeleteNodeWithRelationships() {
		Transaction tx = graph.beginTx();
		Node n1 = graph.createNode();
		Node n2 = graph.createNode();
		n1.createRelationshipTo(n2, RelType.TEST);

		DBTools.deleteNode(n1);
		DBTools.deleteNode(n2);
		tx.success();
		tx.finish();

		boolean hasNodes = false;
		for (Iterator<Node> iterator = graph.getAllNodes().iterator(); iterator
				.hasNext();) {
			if (graph.getReferenceNode().getId() != iterator.next()
					.getId()) {
				hasNodes = true;
				break;
			}
		}

		assertFalse("Check if tested nodes got deleted", hasNodes);
	}
}
