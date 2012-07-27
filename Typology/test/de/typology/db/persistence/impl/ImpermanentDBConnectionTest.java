package de.typology.db.persistence.impl;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import de.typology.SetupHelperMethods;
import de.typology.tools.ConfigHelper;

public class ImpermanentDBConnectionTest {

	private static ImpermanentDBConnection db;
	
	// SETUP
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SetupHelperMethods.initiateContextSupport();
		db = new ImpermanentDBConnection();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.shutdown();
	}

	// TESTS

	@Test
	public void getGraph_isGraphAccessible_notNull(){
		assertNotNull("Check if created graph object is not null", db.getGraph());
	}
	
	@Test
	public void getWordIndex_isWordIndexAccessible_notNull(){
		assertNotNull("Check if created word index object is not null", db.getWordIndex());
	}	

	@Test
	public void getDn_isRTAccessible_lengthDefined(){
		assertNotNull("Check if created relationship types are defined", db.getDn());
		assertEquals("Check if created relationship types are at the right length", db.getDn().length, ConfigHelper.getMAX_RELATIONS() + 1);
	}	

	@Test
	public void isReferenceNode_referenceNode_true() {
		assertTrue("Check if reference node returns true", db.isReferenceNode(db.getGraph().getReferenceNode()));
	}

	@Test
	public void isReferenceNode_anotherNode_false() {
		Transaction tx = db.getGraph().beginTx();
		Node n = db.getGraph().createNode();
		assertFalse("Check if another node returns false", db.isReferenceNode(n));
		n.delete();
		tx.success();
		tx.finish();
	}
	
}
