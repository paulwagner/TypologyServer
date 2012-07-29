/**
 * Connector class for accessing session handling mysql features.
 */
package de.typology.rdb.connectors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.typology.rdb.persistence.MySQLConnection;

public class MySQLSessionConnector {
	
	// MEMBERS
	
	/**
	 * Don't change db object in this class, because it could be used in other connectors as well!
	 */
	private final MySQLConnection database;
	
	// CONSTRUCTORS
	
	/**
	 * Create Connector with specified rdb object
	 * @param db
	 */
	public MySQLSessionConnector(MySQLConnection db){
		this.database = db;
	}
	
	// METHODS
	
	/**
	 * Check if given developer key is a valid one
	 */
	public boolean isValidDeveloperKey(String key){
		String qry = "SELECT * FROM Developer d WHERE d.develkey = MD5(?)";
		ResultSet result = null;
		try {
			PreparedStatement stm = database.getPreparedStatement(qry);
			stm.setString(0, key);
			result = database.executePreparedQuery(stm);
			if(result.next()){
				return true;
			}
		} catch (SQLException e) {
			return false;
		}
		return false;
	}
	
	/**
	 * Gets ulfnr with specified keys. Creates new user if not existing.
	 * 
	 * @param develkey Developerkey
	 * @param uid developer user id
	 * @return ulfnr of inserted row or existing user specified by params
	 */
	public int getOrCreateUlfnr(String develkey, String uid){
		String qry = "SELECT ulfnr FROM User WHERE develkey = ? AND uid = ?";
		try{
			PreparedStatement stm = database.getPreparedStatement(qry);
			stm.setString(0, develkey);
			stm.setString(1, uid);
			ResultSet set = database.executePreparedQuery(stm);
			if(set.next()){
				return set.getInt(1);
			}
		} catch (SQLException e){}
		
		int key = -1;
		String qry_insert = "INSERT INTO User(develkey, uid) VALUES(?, ?)";
		try{
			PreparedStatement stm = database.getPreparedStatement(qry_insert);
			stm.setString(0, develkey);
			stm.setString(1, uid);
			key = database.executePreparedRowQuery(stm);
			if(key > 0){
				return key;
			}
		} catch (SQLException e){}
		return -1;
	}

}
