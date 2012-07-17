/**
 * Interface that defines DBConnection. It has to be implemented by all
 * DBConnections we use, because it defines the methods that are required by
 * all the other classes.
 * 
 * @author Paul Wagner
 */
package de.typology.db.persistence;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.EmbeddedGraphDatabase;


public interface IDBConnection {

	// GETTER
	public abstract String getDB_PATH();

	// METHODS
	public abstract void closeConnection();
		
	public abstract Boolean isShutdown();
	
	public abstract EmbeddedGraphDatabase getGraph();
	
	public abstract DynamicRelationshipType[] getDn();
	
	public abstract Index<Node> getWordIndex();

}