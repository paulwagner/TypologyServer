package de.typology.db;

import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import de.typology.SetupHelperMethods;
import de.typology.db.persistence.impl.ImpermanentDBConnection;

public class DBToolsTest {

	public static ImpermanentDBConnection db;

	@BeforeClass
	public static void setUpBeforeClass() {
		SetupHelperMethods.initiateContextSupport();
	}

	@Before
	public void setUp() throws Exception {
		db = new ImpermanentDBConnection();
	}

	@After
	public void tearDown() throws Exception {
		db.shutdown();
	}

	@Test
	public void deleteRelationship_createAndDeleteRelationship() {
		Transaction tx = db.getGraph().beginTx();
		Node n1 = db.getGraph().createNode();
		Node n2 = db.getGraph().createNode();
		n1.createRelationshipTo(n2, db.getDn()[1]);

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
		Transaction tx = db.getGraph().beginTx();
		Node n1 = db.getGraph().createNode();
		Node n2 = db.getGraph().createNode();
		n1.createRelationshipTo(n2, db.getDn()[1]);

		DBTools.deleteNode(n1);
		DBTools.deleteNode(n2);
		tx.success();
		tx.finish();

		boolean hasNodes = false;
		for (Iterator<Node> iterator = db.getGraph().getAllNodes().iterator(); iterator
				.hasNext();) {
			if (db.getGraph().getReferenceNode().getId() != iterator.next()
					.getId()) {
				hasNodes = true;
				break;
			}
		}

		assertFalse("Check if tested nodes got deleted", hasNodes);
	}
}
