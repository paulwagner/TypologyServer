/**
 * Method test case for Request class
 * 
 * @author Paul Wagner
 */
package de.typology.requests;

import static de.typology.tools.Resources.LN_MAX;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.typology.requests.interfaces.svr.DataObjectSvr;
import de.typology.requests.interfaces.svr.GetPrimitiveObjectSvr;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class RequestTest {
	
	private static IRequest request; // Class under test
	// Request Interfaces to mock
	private static HttpServletRequest servReq;
	private static HttpServletResponse servResp; 

	@Before
	public void setUp() throws Exception {
		// Create mocking interfaces
		servReq = PowerMock.createMock(HttpServletRequest.class);
		servResp = PowerMock.createMock(HttpServletResponse.class);
		// Create testing class
		request = new Request(LN_MAX, servReq, servResp);		
	}

	
	/**
	 * If response object in constructor is null, exception has to be thrown
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void constructor_requestObjIsNull_Exception() throws Exception {
		request = new Request(LN_MAX, null, servResp);		
	}
	
	/**
	 * If request object in constructor is null, exception has to be thrown
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void constructor_responseObjIsNull_Exception() throws Exception {
		request = new Request(LN_MAX, servReq, null);		
	}
	
	/**
	 * Check if DataObjectSvr gets correctly serialized
	 */
	@Test
	public void makeResponse_responseDataObjectSvr_getJsonTextInStream(){
		// Prepare response for mocking
		StringWriter buffer = new StringWriter();		 
		PrintWriter p = new PrintWriter(buffer);
		try {
			expect(servResp.getWriter()).andReturn(p);
		} catch (IOException e) {
			fail("Something got wrong writing to response stream");
		}
		
		// Prepare test object
		DataObjectSvr data = new DataObjectSvr();
		data.msg = "message";
		data.status = 0;
		
		// Start testing
		replay(servResp);
		request.makeResponse(data);
		verify(servResp);
		
		// Evaluate result
		String result = buffer.toString();
		assertTrue("Check if result text is correctly serialized", result.contains("\"msg\":\"message\""));
		assertTrue("Check if result text is correctly serialized", result.contains("\"status\":0"));		
	}
	
	/**
	 * Check if GetPrimitiveObjectSvr gets correctly serialized
	 */
	@Test
	public void makeResponse_responseGetPrimitiveObjectSvr_getJsonTextInStream(){
		// Prepare response for mocking
		StringWriter buffer = new StringWriter();		 
		PrintWriter p = new PrintWriter(buffer);
		try {
			expect(servResp.getWriter()).andReturn(p);
		} catch (IOException e) {
			fail("Something got wrong writing to response stream");
		}
		
		// Prepare test object
		GetPrimitiveObjectSvr data = new GetPrimitiveObjectSvr();
		data.msg = "message";
		data.status = 0;
		data.list = new HashMap<Integer, String>();
		data.totalcount = 5;
		
		// Start testing
		replay(servResp);
		request.makeResponse(data);
		verify(servResp);
		
		// Evaluate result
		String result = buffer.toString();
		assertTrue("Check if result text is correctly serialized", result.contains("\"msg\":\"message\""));
		assertTrue("Check if result text is correctly serialized", result.contains("\"status\":0"));		
		assertTrue("Check if result text is correctly serialized", result.contains("\"list\":{}"));
		assertTrue("Check if result text is correctly serialized", result.contains("\"totalcount\":5"));		
	}	
	
	/**
	 * Check if error response triggers http status code 500
	 */
	@Test
	public void makeErrorResponse_responseDataObjectSvr_httpStatus500(){
		// Prepare response for mocking
		servResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		StringWriter buffer = new StringWriter();		 
		PrintWriter p = new PrintWriter(buffer);
		try {
			expect(servResp.getWriter()).andReturn(p);
		} catch (IOException e) {
			fail("Something got wrong writing to response stream");
		}
		
		// Start testing
		replay(servResp);
		request.makeErrorResponse(2, "message");
		verify(servResp);
	}
	
	/**
	 * Check if setResponseStatus() sets the status as it should
	 */
	@Test
	public void setResponseStatus_setStatus_statusIsSet(){
		servResp.setStatus(100);
		replay(servResp);
		request.setResponseStatus(100);
		verify(servResp);
	}
	
	/**
	 * If getSession() discovers an active session, session id should be loaded
	 */
	@Test
	public void getSession_hasActiveSession_sidNotNull(){
		// Prepare faked session object
		HttpSession session = PowerMock.createMock(HttpSession.class);
		expect(session.getId()).andReturn("mySuperSessionId");
		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(session);
		
		replayAll();
		request.getSession();
		verifyAll();
		
		assertEquals("Check if session is loaded", "mySuperSessionId", request.getSessionId());
		
	}

	/**
	 * If getSession() discovers no active session, session id should be null
	 */
	@Test
	public void getSession_hasNoActiveSession_sidIsNull(){
		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(null);
		
		replay(servReq);
		request.getSession();
		verify(servReq);
		
		assertNull("Check if session is not loaded", request.getSessionId());
	}
	
	/**
	 * If an old session is available, createSession() has to invalidate it.
	 */
	@Test(expected = Exception.class)
	public void createSession_oldSessionAvailable_invalidateOldSession() throws Exception{
		// Prepare faked session object (old session)
		HttpSession session = PowerMock.createMock(HttpSession.class);
		// Sessionid is not important for test, but the call has to be answered
		expect(session.getId()).andReturn("mySuperSessionId");		
		session.invalidate(); // Session have to be invalidated
		
		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(session);
		// This is for createSession call, where our test case ends. Mock in null will trigger exception.
		expect(servReq.getSession()).andReturn(null);
		
		replayAll();
		request.getSession();
		request.createSession();
		verifyAll();
	}
	
	/**
	 * A new session should be created and the session id loaded
	 */
	@Test
	public void createSession_noOldSession_loadSid(){
		// Prepare faked session object (old session)
		HttpSession session = PowerMock.createMock(HttpSession.class);
		expect(session.getId()).andReturn("mySuperSessionId");		

		// Prepare faked request object
		expect(servReq.getSession()).andReturn(session);
		
		replayAll();
		try {
			request.createSession();
		} catch (Exception e) {
			fail("Unexpected exception thrown");
		}
		verifyAll();
	}
	
	/**
	 * destroySession should invalidate session and null sessionid
	 */
	@Test
	public void destroySession_destroySession_sessionAndSidAreNull(){
		// Prepare faked session object
		HttpSession session = PowerMock.createMock(HttpSession.class);
		expect(session.getId()).andReturn("mySuperSessionId");		
		session.invalidate(); // Session have to be invalidated
		
		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(session);
		
		replayAll();
		request.getSession();
		request.destroySession();
		verifyAll();
		
		assertNull("Check if sessionid is null after destroying session", request.getSessionId());
	}
	
	/**
	 * loadSession with session should load all values
	 */
	public void loadSession_sessionIsValid_returnTrue(){
		// Prepare faked session object
		HttpSession session = PowerMock.createMock(HttpSession.class);
		expect(session.getId()).andReturn("mySuperSessionId");		
		expect(session.getAttribute("ulfnr")).andReturn(5);
		expect(session.getAttribute("developer_key")).andReturn("mySuperDeveloperkey");

		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(session);
		
		replayAll();
		request.getSession();
		boolean r = request.loadSession();
		verifyAll();
		
		assertEquals("Check if loaded ulfnr match", 5, request.getUlfnr());
		assertEquals("Check if loaded developer key match", "mySuperDeveloperkey", request.getDeveloperKey());		
		assertTrue("Check if result of loadSession() is true", r);
	}
	
	/**
	 * loadSession with session but no developer key should load values but return false
	 */
	@Test
	public void loadSession_noDeveloperKey_returnFalse(){
		// Prepare faked session object
		HttpSession session = PowerMock.createMock(HttpSession.class);
		expect(session.getId()).andReturn("mySuperSessionId");		
		expect(session.getAttribute("ulfnr")).andReturn(5);
		expect(session.getAttribute("dlfnr")).andReturn(null);

		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(session);
		
		replayAll();
		request.getSession();
		boolean r = request.loadSession();
		verifyAll();
		
		assertEquals("Check if loaded ulfnr match", 5, request.getUlfnr());
		assertFalse("Check if result of loadSession() is false", r);		
	}
	
	/**
	 * loadSession without session should return false
	 */
	@Test
	public void loadSession_noSession_returnFalse(){
		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(null);
		
		replayAll();
		request.getSession();
		boolean r = request.loadSession();
		verifyAll();
		
		assertFalse("Check if result of loadSession() is false", r);		
	}
	
	/**
	 * storeInSession should call setAttribute if session is available
	 */
	@Test
	public void storeInSession_sessionAvailable_returnTrue(){
		// Prepare faked session object
		HttpSession session = PowerMock.createMock(HttpSession.class);
		expect(session.getId()).andReturn("mySuperSessionId");		
		session.setAttribute("test", "test");

		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(session);
		
		replayAll();
		request.getSession();
		boolean r = request.storeInSession("test", "test");
		verifyAll();
		
		assertTrue("Check if result of storeInSession() is true", r);
	}
	
	/**
	 * storeInSession should return false if session is null
	 */
	@Test
	public void storeInSession_sessionIsNull_returnFalse(){
		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(null);
		
		replayAll();
		request.getSession();
		boolean r = request.storeInSession("test", "test");
		verifyAll();
		
		assertFalse("Check if result of storeInSession() is false", r);
	}
	
	/**
	 * getSessionValue should call getAttribute if session is available
	 */
	@Test
	public void getSessionValue_sessionAvailable_returnSessionValue(){
		// Prepare faked session object
		HttpSession session = PowerMock.createMock(HttpSession.class);
		expect(session.getId()).andReturn("mySuperSessionId");		
		expect(session.getAttribute("testkey")).andReturn("testvalue");		

		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(session);
		
		replayAll();
		request.getSession();
		String r = (String) request.getSessionValue("testkey");
		verifyAll();
		
		assertEquals("Check if result of getSessionValue() equals expected", "testvalue", r);
	}
	
	/**
	 * getSessionValue should return null if session not available
	 */
	@Test
	public void getSessionValue_sessionIsNull_returnNull(){
		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(null);
		
		replayAll();
		request.getSession();
		Object r = request.getSessionValue("testkey");
		verifyAll();
		
		assertNull("Check if result of getSessionValue() is null", r);
	}
	
	/**
	 * getSessionValueAsInteger should return default if key not found
	 */
	@Test
	public void getSessionValueAsInteger_valueIsNull_returnDefault(){
		// Prepare faked session object
		HttpSession session = PowerMock.createMock(HttpSession.class);
		expect(session.getId()).andReturn("mySuperSessionId");		
		expect(session.getAttribute("testkey")).andReturn(null);		

		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(session);
		
		replayAll();
		request.getSession();
		int r = request.getSessionValueAsInteger("testkey", -99);
		verifyAll();
		
		assertEquals("Check if default result of getSessionValueAsInteger() equals expected", -99, r);
	}
	
	/**
	 * getSessionValueAsInteger should return default if found value cannot be casted
	 */
	@Test
	public void getSessionValueAsInteger_valueIsUncastable_returnDefault(){
		// Prepare faked session object
		HttpSession session = PowerMock.createMock(HttpSession.class);
		expect(session.getId()).andReturn("mySuperSessionId");		
		expect(session.getAttribute("testkey")).andReturn(new DataObjectSvr());		

		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(session);
		
		replayAll();
		request.getSession();
		int r = request.getSessionValueAsInteger("testkey", -99);
		verifyAll();
		
		assertEquals("Check if default result of getSessionValueAsInteger() equals expected", -99, r);
	}
	
	/**
	 * getSessionValueAsInteger should return requested value if found value is an integer
	 */
	@Test
	public void getSessionValueAsInteger_valueIsInteger_returnValue(){
		// Prepare faked session object
		HttpSession session = PowerMock.createMock(HttpSession.class);
		expect(session.getId()).andReturn("mySuperSessionId");		
		expect(session.getAttribute("testkey")).andReturn(10);		

		// Prepare faked request object
		expect(servReq.getSession(false)).andReturn(session);
		
		replayAll();
		request.getSession();
		int r = request.getSessionValueAsInteger("testkey", -99);
		verifyAll();
		
		assertEquals("Check if result of getSessionValueAsInteger() equals expected", 10, r);
	}		
	
}
