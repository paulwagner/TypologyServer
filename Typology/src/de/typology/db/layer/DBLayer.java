/**
 * This is the main used database layer created by Rene.
 * Please note: It has to be thread safe, because every request triggers a new thread using this layer!

 * Important: PLEASE NOTE THAT DB CAN BE CHANGED FROM OUTSIDE, SO CHECK FIRST IF IT'S RUNNING!!
 * 
 * @author Rene Pickhardt
 */
package de.typology.db.layer;

import de.typology.db.persistence.IDBConnection;

public class DBLayer implements IDBLayer {

	// PROPERTIES
	
	private IDBConnection db = null;
	
	
	// CONSTRUCTORS

	/**
	 * Constructor for injecting previously created db FIRST, and then loading the layer.
	 * 
	 * @param db the corresponding database, on wich the layer should be created
	 */
	public DBLayer(IDBConnection db) {
		injectDB(db);
		loadLayer();
	}


	// METHODS
	
	/**
	 * Inject the db from outside.
	 * This is done because the db has to be independend from the layers for the use of multiple layers.
	 * It's a bit á la spring...
	 * 
	 * @see de.typology.db.layer.IDBLayer#injectDB(IDBConnection)
	 */
	@Override
	public void injectDB(IDBConnection db) {
		if(this.db == null){
			this.db = db;
		}
	}

	/**
	 * Load layer from db.
	 * 
	 * @see de.typology.db.layer.IDBLayer#loadLayer()
	 */
	@Override
	public void loadLayer() {
		if(!db.isShutdown()){
			// implement DBLayer
		}
	}


}
