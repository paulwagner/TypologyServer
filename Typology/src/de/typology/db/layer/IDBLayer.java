/**
 * Interface that defines DBLayer. It has to be implemented by all
 * DBLayers we use, because it defines the methods that are required by
 * retrieval and all the other classes. So if something new is defined in
 * DBLayer THAT IS USED IN OTHER CLASSES you have to append it here and use
 * the type IDBLayer for your implementation.
 * 
 * Important: PLEASE NOTE THAT DB CAN BE CHANGED FROM OUTSIDE, SO CHECK FIRST IF IT'S RUNNING!!
 * 
 * @author Paul Wagner
 */
package de.typology.db.layer;

import de.typology.db.persistence.IDBConnection;

public interface IDBLayer{

	// METHODS
	
	/**
	 * Inject the global DB object reference to the layer
	 * @param db Database connection
	 */
	public void injectDB(IDBConnection db);

	/**
	 * Load the layer
	 */
	public void loadLayer();

}