/**
 * Class for accessing mysql databases using jdbc.
 * This method needs mysql-connector-java im classpath, but won't trigger any compiler error if it misses!
 * 
 * @author Paul Wagner
 */
package de.typology.rdb.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.typology.tools.IOHelper;

public class MySQLConnection {

	// PROPERTIES

	private String driver;
	private String path;
	private String user;
	private String pass;

	private Connection connection;

	// CONSTRUCTOR

	/**
	 * Constructor. When specified driver is not found, ClassNotFoundException
	 * will be thrown.
	 * 
	 * @param driver
	 *            Driver for JDBC
	 * @param path
	 *            Path for MySQL
	 * @param user
	 *            Username for MySQL
	 * @param pass
	 *            Password for MySQL
	 * @throws ClassNotFoundException
	 */
	public MySQLConnection(String path, String user, String pass)
			throws ClassNotFoundException {
		this.driver = "com.mysql.jdbc.Driver";
		this.pass = pass;
		this.path = path;
		this.user = user;

		try {
			Class.forName(this.driver);
		} catch (ClassNotFoundException e) {
			IOHelper.logErrorExceptionContext(
					"ERROR: (MySQLConnection.init()) JDBC Connector class not found. Do you have mysql-connector-java in your classpath?",
					e);
			throw e;
		}
	}

	// METHODS

	/**
	 * Opens new connection.
	 * 
	 * @throws SQLException
	 */
	public void openConnection() throws SQLException {
		try {
			this.connection = DriverManager.getConnection(this.path, this.user,
					this.pass);
		} catch (SQLException e) {
			IOHelper.logErrorExceptionContext(
					"ERROR: (MySQLConnection.openConnection()) Unable to connect to mysql database. Deamon running and config ok?",
					e);
			throw e;
		}

	}

	/**
	 * Close connection.
	 */
	public void closeConnection() {
		if (connection == null) {
			return;
		}
		try {
			connection.close();
		} catch (SQLException e) {
			IOHelper.logErrorExceptionContext(
					"ERROR: (MySQLConnection.closeConnection()) Error while closing connection.",
					e);
		}
	}

	/**
	 * Executes a given Query and returns result set. This method only supports
	 * SELECT statements!
	 * 
	 * @param query
	 * @return resultset
	 * @throws SQLException
	 */
	public ResultSet executeQuery(String query) throws SQLException {
		if (connection == null) {
			IOHelper.logErrorContext("ERROR: (MySQLConnection.executeQuery()) Connection is not open! Unable to execute Query");
			throw new SQLException(
					"(MySQLConnection.executeQuery()) connection is null");
		}
		ResultSet result = null;
		try {
			Statement smt = connection.createStatement();
			result = smt.executeQuery(query);
			smt.close();
		} catch (SQLException e) {
			IOHelper.logErrorExceptionContext(
					"ERROR: (MySQLConnection.executeQuery()) Query failed: "
							+ query + " Message: " + e.getMessage(), e);
			throw e;
		}
		return result;
	}

	/**
	 * Executes a given Updatequery and returns number of affected rows. This
	 * method only supports DELETE, UPDATE and INSERT statements!
	 * 
	 * @param query
	 * @return number of affected rows
	 * @throws SQLException
	 */
	public int executeUpdateQuery(String query) throws SQLException {
		if (connection == null) {
			IOHelper.logErrorContext("ERROR: (MySQLConnection.executeQuery()) Connection is not open! Unable to execute Update");
			throw new SQLException(
					"(MySQLConnection.executeUpdate()) connection is null");
		}
		int count = -1;
		try {
			Statement smt = connection.createStatement();
			count = smt.executeUpdate(query);
			smt.close();
		} catch (SQLException e) {
			IOHelper.logErrorExceptionContext(
					"ERROR: (MySQLConnection.executeUpdate()) Update failed: "
							+ query + " Message: " + e.getMessage(), e);
			throw e;
		}
		return count;
	}

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
	public ResultSet executeLookup(String fields, String table, String where,
			String order_by) throws SQLException {
		if (order_by != "") {
			return executeQuery("SELECT " + fields + " FROM " + table
					+ " WHERE " + where + "ORDER BY " + order_by);
		} else {
			return executeQuery("SELECT " + fields + " FROM " + table
					+ " WHERE " + where);
		}
	}

	/**
	 * Execute a delete.
	 * 
	 * @param table
	 * @param where
	 * @return number of affected rows
	 * @throws SQLException
	 */
	public int executeDelete(String table, String where) throws SQLException {
		return executeUpdateQuery("DELETE FROM " + table + " WHERE " + where);
	}

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
	public int executeUpdate(String fields, String table, String values,
			String where) throws SQLException {
		return executeUpdateQuery("UPDATE " + table + " SET (" + fields
				+ ") VALUES (" + values + ") WHERE " + where);
	}

	/**
	 * Executes an insert.
	 * 
	 * @param fields
	 * @param table
	 * @param values
	 * @return number of affected rows
	 * @throws SQLException
	 */
	public int executeInsert(String fields, String table, String values)
			throws SQLException {
		return executeUpdateQuery("INSERT INTO " + table + "(" + fields
				+ ") VALUES (" + values + ")");
	}

}
