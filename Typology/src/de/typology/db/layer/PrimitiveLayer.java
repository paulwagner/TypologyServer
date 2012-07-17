/**
 * This is the primitive database layer.
 * Please note: It has to be thread safe, because every request triggers a new thread using this layer!
 * 
 * Important: PLEASE NOTE THAT DB CAN BE CHANGED FROM OUTSIDE, SO CHECK FIRST IF IT'S RUNNING!!
 * 
 * @author Paul Wagner
 */
package de.typology.db.layer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import org.neo4j.graphdb.Node;

import de.typology.db.persistence.IDBConnection;
import de.typology.tools.ConfigHelper;
import de.typology.tools.SBVEntry;

public class PrimitiveLayer implements IDBLayer {

	// PROPERTIES
	private IDBConnection db = null;
	private HashMap<Integer, String> nodeMap = new HashMap<Integer, String>();
	
	
	// CONSTRUCTORS
	public PrimitiveLayer(IDBConnection db){
		injectDB(db);
		loadLayer();
	}
	
	
	// GETTERS
	
	/**
	 * Get map with frequently used words.
	 * 
	 * @return clone of nodeMap
	 */
	public HashMap<Integer, String> getNodeMap(){
		return new HashMap<Integer, String>(nodeMap);
	}
	
	
	// METHODS
	
	/**
	 * Inject the db from outside.
	 * This is done because the db has to be independend from the layers for the use of multiple layers.
	 * It's a bit á la spring...
	 * 
	 * @param db the db to inject
	 * @see de.typology.db.layer.IDBLayer#injectDB()
	 */
	@Override
	public void injectDB(IDBConnection db) {
		if(this.db == null){
			this.db = db;
		}
		
	}

	/**
	 * Load primitive Layer
	 * 
	 * @see de.typology.db.layer.IDBLayer#loadLayer()
	 */
	@Override
	public void loadLayer() {
		if(!db.isShutdown()){
			// Get all nodes and fill them into a map, which is basically our layer
			Vector<SBVEntry> set = new Vector<SBVEntry>();
			Long refId = db.getGraph().getReferenceNode().getId();
			String cnt = ConfigHelper.getCOUNT_KEY();
			String name = ConfigHelper.getNAME_KEY();
			for(Node n : db.getGraph().getAllNodes()){
				if(refId == n.getId() || !n.hasProperty(cnt) || !n.hasProperty(name)){
					continue;
				}
				Integer c = (Integer) n.getProperty(cnt);
				String s = (String) n.getProperty(name);
				SBVEntry e = new SBVEntry(s, c, Integer.class);
				set.add(e);
			}
			Collections.sort(set);
			
			int c = 0;
			for(SBVEntry e : set){
//				if(c > 70000){
//					break;
//				}
				nodeMap.put(c, e.key);
				c++;
			}
			set = null;
		}		
	}
}
