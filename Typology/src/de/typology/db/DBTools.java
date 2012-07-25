/**
 * Helper class that hold various repeating tasks in db maintanence
 *
 * @author Paul Wagner
 *
 */

package de.typology.db;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class DBTools {

	/**
	 * Append text data to db without changing weights.
	 * 
	 * TODO do this in dbconnection, not here
	 */
	public static void appendTextWithoutNorm() {		

	}

	/**
	 * Delete relationships from reference node
	 * 
	 * @param reference
	 *            Node for deleting relationships
	 */
	public static void deleteRelationships(Node reference) {
		for (Relationship rel : reference.getRelationships()) {
			rel.delete();
		}
	}

	/**
	 * Delete a node with all its relationships
	 * 
	 * @param node
	 *            Node to delete
	 */
	public static void deleteNode(Node node) {
		deleteRelationships(node);
		node.delete();
	}

}
