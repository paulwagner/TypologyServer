/**
 * Interface for relational database systems.
 * 
 * @author Paul Wagner
 */
package de.typology.rdb.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IRDBConnection {

	/**
	 * Opens new connection.
	 * 
	 * @throws SQLException
	 */
	public abstract void openConnection() throws SQLException;

	/**
	 * Close connection.
	 */
	public abstract void closeConnection();

	/**
	 * Get a prepared statement with specified query
	 * 
	 * @param qry the query with '?' placeholders
	 * @return the prepared statement
	 * @throws SQLException
	 */
	public PreparedStatement getPreparedStatement(String qry) throws SQLException;	
	
	/**
	 * Executes a given Query and returns result set. This method only supports
	 * SELECT statements!
	 * 
	 * @param stm Prepared statement
	 * @return resultset
	 * @throws SQLException
	 */
	public abstract ResultSet executePreparedQuery(PreparedStatement stm) throws SQLException;

	/**
	 * Executes a given Updatequery and returns number of affected rows. This
	 * method only supports UPDATE statements!
	 * 
	 * @param stm Prepared statement
	 * @return number of affected rows
	 * @throws SQLException
	 */
	public abstract int executePreparedUpdateQuery(PreparedStatement stm) throws SQLException;

	/**
	 * Executes a given Rowquery and returns inserted or deletet primary key. This
	 * method only supports INSERT and DELETE statements!
	 * 
	 * @param stm Prepared statement
	 * @return number of affected rows
	 * @throws SQLException
	 */
	public abstract int executePreparedRowQuery(PreparedStatement stm) throws SQLException;

}