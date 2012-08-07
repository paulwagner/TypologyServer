/**
 * Connector class for accessing session handling mysql features.
 */
package de.typology.rdb.connectors;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.typology.rdb.persistence.MySQLConnection;
import de.typology.tools.IOHelper;

public class MySQLSessionConnector implements IRDBSessionConnector {
	
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
	
	/* (non-Javadoc)
	 * @see de.typology.rdb.connectors.IRDBSessionConnector#checkDeveloperKey(java.lang.String)
	 */
	@Override
	public int checkDeveloperKey(String key){
		String qry = "SELECT dlfnr FROM Developer d WHERE d.develkey = ?";
		ResultSet result = null;
		try {
			PreparedStatement stm = database.getPreparedStatement(qry);
			stm.setString(1, key);
			result = database.executePreparedQuery(stm);
			if(result.next()){
				int tmp = result.getInt(1); 
				stm.close();
				return tmp;
			}
		} catch (SQLException e) {
			IOHelper.logErrorException("(MySQLSessionConnector.isValidDeveloperKey()) Error executing query " + qry, e);
		}
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see de.typology.rdb.connectors.IRDBSessionConnector#getOrCreateUlfnr(int, java.lang.String, java.lang.String)
	 */
	@Override
	public int getOrCreateUlfnr(int dlfnr, String uid, String userpass){
		String qry = "SELECT ulfnr FROM User WHERE dlfnr = ? AND uid = ? AND userpass = MD5(?)";
		try{
			PreparedStatement stm = database.getPreparedStatement(qry);
			stm.setInt(1, dlfnr);
			stm.setString(2, uid);
			stm.setString(3, userpass);			
			ResultSet set = database.executePreparedQuery(stm);
			if(set.next()){
				int result = set.getInt(1);
				stm.close();
				return result;
			}
		} catch (SQLException e){
			IOHelper.logErrorException("(MySQLSessionConnector.getOrCreateUlfnr()) Error executing query " + qry, e);						
		}
		
		int key = -1;
		String qry_insert = "INSERT INTO User(dlfnr, uid, userpass) VALUES(?, ?, MD5(?))";
		try{
			PreparedStatement stm = database.getPreparedStatement(qry_insert);
			stm.setInt(1, dlfnr);
			stm.setString(2, uid);
			stm.setString(3, userpass);			
			key = database.executePreparedRowQuery(stm);
			stm.close();
			if(key > 0){
				return key;
			}
		} catch (SQLException e){
			IOHelper.logErrorException("(MySQLSessionConnector.getOrCreateUlfnr()) Error executing query " + qry, e);			
		}
		return -1;
	}

}