/**
 * Class for accessing mysql databases using jdbc.
 * This method needs mysql-connector-java im classpath, but won't trigger any compiler error if it misses!
 * 
 * @author Paul Wagner
 */
package de.typology.rdb.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
	 * @see de.typology.rdb.persistence.IRDBConnection#getPreparedStatement(java.lang.String)
	 */
	@Override
	public PreparedStatement getPreparedStatement(String qry) throws SQLException{
		checkConnection();
		return connection.prepareStatement(qry, Statement.RETURN_GENERATED_KEYS);
	}
	
	/* (non-Javadoc)
	 * @see de.typology.rdb.persistence.IRDBConnection#executePreparedQuery(java.sql.PreparedStatement)
	 */
	@Override
	public ResultSet executePreparedQuery(PreparedStatement stm) throws SQLException {
		checkConnection();
		ResultSet result = null;
		try {
			result = stm.executeQuery();
		} catch (SQLException e) {
			IOHelper.logErrorExceptionContext(
					"ERROR: (MySQLConnection.executeQuery()) Query failed. Message: " + e.getMessage(), e);
			throw e;
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see de.typology.rdb.persistence.IRDBConnection#executePreparedUpdateQuery(java.sql.PreparedStatement)
	 */
	@Override
	public int executePreparedUpdateQuery(PreparedStatement stm) throws SQLException {
		checkConnection();
		int count = -1;
		try {
			count = stm.executeUpdate();
		} catch (SQLException e) {
			IOHelper.logErrorExceptionContext(
					"ERROR: (MySQLConnection.executeUpdate()) Update failed. Message: " + e.getMessage(), e);
			throw e;
		}
		return count;
	}
	
	/* (non-Javadoc)
	 * @see de.typology.rdb.persistence.IRDBConnection#executePreparedRowQuery(java.sql.PreparedStatement)
	 */
	@Override
	public int executePreparedRowQuery(PreparedStatement stm) throws SQLException {
		checkConnection();
		int count = -1;
		try {
			stm.executeUpdate();
			ResultSet set = stm.getGeneratedKeys();
			if(set.next()){
				count = set.getInt(1);
			}
		} catch (SQLException e) {
			IOHelper.logErrorExceptionContext(
					"ERROR: (MySQLConnection.executeUpdate()) Update failed. Message: " + e.getMessage(), e);
			throw e;
		}
		return count;
	}

	/**
	 * Check if connection is valid. If not, SQLException will be thrown
	 * @throws SQLException
	 */
	private void checkConnection() throws SQLException {
		if (connection == null) {
			IOHelper.logErrorContext("ERROR: (MySQLConnection.executeQuery()) Connection is not open! Unable to execute Update");
			throw new SQLException(
					"(MySQLConnection.executeUpdate()) connection is null");
		}
	}	

}
