package de.typology.rdb.connectors;

import static org.junit.Assert.*;

import java.sql.ResultSet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import com.mysql.jdbc.PreparedStatement;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.anyObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import de.typology.SetupHelperMethods;
import de.typology.rdb.persistence.IRDBConnection;
import de.typology.rdb.persistence.MySQLConnection;

public class MySQLSessionConnectorTest {

	// MEMBERS
	public static MySQLConnection mysql;
	public static MySQLSessionConnector connector;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SetupHelperMethods.initiateLogSupport();
	}

	@Before
	public void setUp() throws Exception {
		mysql = PowerMock.createMock(MySQLConnection.class);
		connector = new MySQLSessionConnector(mysql);				
	}

	@Test
	public void checkDeveloperKey_checkCorrectKey_executeRightSQLStatement() throws Exception {
		// Expectations
		PreparedStatement stm = PowerMock.createMock(PreparedStatement.class);		
		expect(mysql.getPreparedStatement("SELECT dlfnr FROM Developer d WHERE d.develkey = ?")).andReturn(stm);
		stm.setString(1, "mykey");
		ResultSet result = PowerMock.createMock(ResultSet.class);
		expect(mysql.executePreparedQuery(stm)).andReturn(result);
		expect(result.next()).andReturn(true);
		expect(result.getInt(1)).andReturn(5);
		stm.close();
		
		replayAll();
		int r = connector.checkDeveloperKey("mykey");
		verifyAll();
		
		assertEquals("Check if returned developer key is the expected", 5, r);
	}
	
	@Test
	public void checkDeveloperKey_checkWrongKey_returnsign1() throws Exception {
		// Expectations
		PreparedStatement stm = PowerMock.createMock(PreparedStatement.class);		
		expect(mysql.getPreparedStatement("SELECT dlfnr FROM Developer d WHERE d.develkey = ?")).andReturn(stm);
		stm.setString(1, "mykey");
		ResultSet result = PowerMock.createMock(ResultSet.class);
		expect(mysql.executePreparedQuery(stm)).andReturn(result);
		expect(result.next()).andReturn(false);
		
		replayAll();
		int r = connector.checkDeveloperKey("mykey");
		verifyAll();
		
		assertEquals("Check if returned developer key is the expected", -1, r);
	}
	
	@Test
	public void getOrCreateUlfnr_userExisting_getUser() throws Exception {
		// Expectations
		PreparedStatement stm = PowerMock.createMock(PreparedStatement.class);		
		expect(mysql.getPreparedStatement("SELECT ulfnr FROM User WHERE dlfnr = ? AND uid = ? AND userpass = MD5(?)")).andReturn(stm);
		stm.setInt(1, 5);
		stm.setString(2, "myuid");
		stm.setString(3, "myuserpass");
		ResultSet result = PowerMock.createMock(ResultSet.class);
		expect(mysql.executePreparedQuery(stm)).andReturn(result);
		expect(result.next()).andReturn(true);
		expect(result.getInt(1)).andReturn(50);
		stm.close();
		
		replayAll();
		int r = connector.getOrCreateUlfnr(5, "myuid", "myuserpass");
		verifyAll();
		
		assertEquals("Check if returned ulfnr is expected", 50, r);
	}

	@Test
	public void getOrCreateUlfnr_userNotExisting_createUser() throws Exception {
		// Notify that user not existing
		PreparedStatement stm = PowerMock.createMock(PreparedStatement.class);		
		expect(mysql.getPreparedStatement((String) anyObject())).andReturn(stm);
		stm.setInt(1, 5);
		stm.setString(2, "myuid");
		stm.setString(3, "myuserpass");
		ResultSet result = PowerMock.createMock(ResultSet.class);
		expect(mysql.executePreparedQuery(stm)).andReturn(result);
		expect(result.next()).andReturn(false);
		
		// Expectations
		PreparedStatement stm2 = PowerMock.createMock(PreparedStatement.class);		
		expect(mysql.getPreparedStatement("INSERT INTO User(dlfnr, uid, userpass) VALUES(?, ?, MD5(?))")).andReturn(stm2);
		stm2.setInt(1, 5);
		stm2.setString(2, "myuid");
		stm2.setString(3, "myuserpass");
		expect(mysql.executePreparedRowQuery(stm2)).andReturn(8);
		stm2.close();
		
		replayAll();
		int r = connector.getOrCreateUlfnr(5, "myuid", "myuserpass");
		verifyAll();
		
		assertEquals("Check if returned ulfnr is expected", 8, r);
	}
	
}
