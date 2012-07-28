/**
 * Interface for relational database systems.
 * 
 * @author Paul Wagner
 */
package de.typology.rdb.persistence;

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
	 * Executes a given Query and returns result set. This method only supports
	 * SELECT statements!
	 * 
	 * @param query
	 * @return resultset
	 * @throws SQLException
	 */
	public abstract ResultSet executeQuery(String query) throws SQLException;

	/**
	 * Executes a given Updatequery and returns number of affected rows. This
	 * method only supports DELETE, UPDATE and INSERT statements!
	 * 
	 * @param query
	 * @return number of affected rows
	 * @throws SQLException
	 */
	public abstract int executeUpdateQuery(String query) throws SQLException;

	/**
	 * Executes a simple lookup.
	 * 
	 * @param fields
	 * @param table
	 * @param where
	 * @param order_by
	 * @return resultset
	 * @throws SQLException
	 */
	public abstract ResultSet executeLookup(String fields, String table,
			String where, String order_by) throws SQLException;

	/**
	 * Execute a delete.
	 * 
	 * @param table
	 * @param where
	 * @return number of affected rows
	 * @throws SQLException
	 */
	public abstract int executeDelete(String table, String where)
			throws SQLException;

	/**
	 * Execute an update.
	 * 
	 * @param fields
	 * @param table
	 * @param values
	 * @param where
	 * @return number of affected rows
	 * @throws SQLException
	 */
	public abstract int executeUpdate(String fields, String table,
			String values, String where) throws SQLException;

	/**
	 * Executes an insert.
	 * 
	 * @param fields
	 * @param table
	 * @param values
	 * @return number of affected rows
	 * @throws SQLException
	 */
	public abstract int executeInsert(String fields, String table, String values)
			throws SQLException;

}