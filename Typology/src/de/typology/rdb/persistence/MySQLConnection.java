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

import de.typology.tools.ConfigHelper;
import de.typology.tools.IOHelper;

public class MySQLConnection implements IRDBConnection {

	// PROPERTIES

	private String driver;
	private String host;
	private int port;
	private String database;
	private String user;
	private String pass;

	private Connection connection;

	// CONSTRUCTOR

	/**
	 * Constructor. When specified driver is not found, ClassNotFoundException
	 * will be thrown. Config values will be loaded from ConfigHelper
	 * 
	 * @throws ClassNotFoundException
	 */
	public MySQLConnection() throws ClassNotFoundException {
		this.driver = "com.mysql.jdbc.Driver";
		this.host = ConfigHelper.getMYSQL_HOST();
		this.port = 3306;
		this.pass = ConfigHelper.getMYSQL_PASS();
		this.database = ConfigHelper.getMYSQL_DB_MAIN();
		this.user = ConfigHelper.getMYSQL_USER();

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

	/* (non-Javadoc)
	 * @see de.typology.rdb.persistence.IRDBConnection#openConnection()
	 */
	@Override
	public void openConnection() throws SQLException {
		try {
			this.connection = DriverManager.getConnection("jdbc:mysql://"
					+ this.host + ":" + this.port + "/" + this.database + "?"
					+ "user=" + this.user + "&password=" + this.pass);
		} catch (SQLException e) {
			IOHelper.logErrorExceptionContext(
					"ERROR: (MySQLConnection.openConnection()) Unable to connect to mysql database. Deamon running and config ok?",
					e);
			throw e;
		}

	}

	/* (non-Javadoc)
	 * @see de.typology.rdb.persistence.IRDBConnection#closeConnection()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see de.typology.rdb.persistence.IRDBConnection#executeQuery(java.lang.String)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see de.typology.rdb.persistence.IRDBConnection#executeUpdateQuery(java.lang.String)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see de.typology.rdb.persistence.IRDBConnection#executeLookup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see de.typology.rdb.persistence.IRDBConnection#executeDelete(java.lang.String, java.lang.String)
	 */
	@Override
	public int executeDelete(String table, String where) throws SQLException {
		return executeUpdateQuery("DELETE FROM " + table + " WHERE " + where);
	}

	/* (non-Javadoc)
	 * @see de.typology.rdb.persistence.IRDBConnection#executeUpdate(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int executeUpdate(String fields, String table, String values,
			String where) throws SQLException {
		return executeUpdateQuery("UPDATE " + table + " SET (" + fields
				+ ") VALUES (" + values + ") WHERE " + where);
	}

	/* (non-Javadoc)
	 * @see de.typology.rdb.persistence.IRDBConnection#executeInsert(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int executeInsert(String fields, String table, String values)
			throws SQLException {
		return executeUpdateQuery("INSERT INTO " + table + "(" + fields
				+ ") VALUES (" + values + ")");
	}

}
