/**
 * Test case for request class.
 * At the moment we just test errors.
 * 
 * TODO also test normal behaviour in mocking iretrieval in and check which instance is running when eval() is called (in new thread).
 * but for this there has to be an opportunity to return a method, or mocking in null object and expect nullpointerexception?
 * TODO declare as class test
 * 
 * @author Paul Wagner
 * 
 */
package de.typology.requests;

import static de.typology.tools.Resources.LN_MAX;
import static de.typology.tools.Resources.SC_ERR;
import static de.typology.tools.Resources.SC_ERR_NO_SESSION;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;

import de.typology.requests.interfaces.svr.DataObjectSvr;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class RequestTest {

	private static Request request; // Class under test
	// Request Interfaces to mock
	private static HttpServletRequest servReq;
	private static HttpServletResponse servResp; 

	private static Gson jsonHandler = new Gson();
	
	// SETUP
	
	@Before
	public void setUp() throws Exception {
		// Create mocking interfaces
		servReq = PowerMock.createMock(HttpServletRequest.class);
		servResp = PowerMock.createMock(HttpServletResponse.class);
		// Create testing class
		request = new Request(LN_MAX, servReq, servResp);
	}

	// TESTS
	
	@Test
	public void execute_emptyDoParameter_SCERR() {
		// Prepare request for mocking
		expect(servReq.getParameter("do")).andReturn("");
		// Expectation of response:
		servResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		// Prepare response for mocking
		StringWriter buffer = new StringWriter();		 
		PrintWriter p = new PrintWriter(buffer);
		try {
			expect(servResp.getWriter()).andReturn(p);
		} catch (IOException e) {
			fail("Something got wrong writing to response stream");
		}
		
		// Run mock
		replayAll();
		request.execute();
		verifyAll();
						
		// Response code should be SC_ERR
		String result = buffer.toString();
		DataObjectSvr d = jsonHandler.fromJson(result, DataObjectSvr.class);
		assertEquals("Check if response code is correct", SC_ERR, d.status);		
	}

	@Test
	public void execute_nullDoParameter_SCERR() {
		// Prepare request for mocking
		expect(servReq.getParameter("do")).andReturn(null);
		// Expectation of response:
		servResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		// Prepare response for mocking
		StringWriter buffer = new StringWriter();		 
		PrintWriter p = new PrintWriter(buffer);
		try {
			expect(servResp.getWriter()).andReturn(p);
		} catch (IOException e) {
			fail("Something got wrong writing to response stream");
		}
		
		// Run mock
		replayAll();
		request.execute();
		verifyAll();
						
		// Response code should be SC_ERR
		String result = buffer.toString();
		DataObjectSvr d = jsonHandler.fromJson(result, DataObjectSvr.class);
		assertEquals("Check if response code is correct", SC_ERR, d.status);		
	}
	
	@Test
	public void execute_noInitiateSession_SCERRNOSESSION() {
		// Prepare request for mocking
		expect(servReq.getParameter("do")).andReturn("somethingexceptinitiatesession");
		expect(servReq.getSession(false)).andReturn(null);
		// Expectation of response:
		servResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		// Prepare response for mocking
		StringWriter buffer = new StringWriter();		 
		PrintWriter p = new PrintWriter(buffer);
		try {
			expect(servResp.getWriter()).andReturn(p);
		} catch (IOException e) {
			fail("Something got wrong writing to response stream");
		}
		
		// Run mock
		replayAll();
		request.execute();
		verifyAll();
						
		// Response code should be SC_ERR_NO_SESSION
		String result = buffer.toString();
		DataObjectSvr d = jsonHandler.fromJson(result, DataObjectSvr.class);
		assertEquals("Check if response code is correct", SC_ERR_NO_SESSION, d.status);		
	}	
	
	@Test
	public void execute_nullSession_SCERRNOSESSION() {
		// Prepare request for mocking
		expect(servReq.getParameter("do")).andReturn("INITIATESESSION");
		expect(servReq.getSession(false)).andReturn(null);
		
		// Expectation of response:
		servResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		// Prepare response for mocking
		StringWriter buffer = new StringWriter();		 
		PrintWriter p = new PrintWriter(buffer);
		try {
			expect(servResp.getWriter()).andReturn(p);
		} catch (IOException e) {
			fail("Something got wrong writing to response stream");
		}
		
		// Run mock
		replayAll();
		request.execute();
		verifyAll();
						
		// Response code should be SC_ERR_NO_SESSION
		String result = buffer.toString();
		DataObjectSvr d = jsonHandler.fromJson(result, DataObjectSvr.class);
		assertEquals("Check if response code is correct", SC_ERR_NO_SESSION, d.status);		
	}	
	
	@Test
	public void execute_loadSessionWithoutFunction_SCERR() {
		// Prepare session for mocking
		HttpSession session = PowerMock.createMock(HttpSession.class);
		expect(session.getId()).andReturn("myspecialsessionid");
		expect(session.getAttribute("developer_key")).andReturn("myspecialdeveloperid");
		expect(session.getAttribute("ulfnr")).andReturn(5);
				
		// Prepare request for mocking
		expect(servReq.getParameter("do")).andReturn("notavalidfunctionname");
		expect(servReq.getSession(false)).andReturn(session);
		
		// Expectation of response:
		servResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		// Prepare response for mocking
		StringWriter buffer = new StringWriter();		 
		PrintWriter p = new PrintWriter(buffer);
		try {
			expect(servResp.getWriter()).andReturn(p);
		} catch (IOException e) {
			fail("Something got wrong writing to response stream");
		}
		
		// Run mock
		replayAll();
		request.execute();
		verifyAll();
						
		// Response code should be SC_ERR
		String result = buffer.toString();
		DataObjectSvr d = jsonHandler.fromJson(result, DataObjectSvr.class);
		assertEquals("Check if response code is correct", SC_ERR, d.status);	
		assertEquals("Check if loaded session id is correct", "myspecialsessionid", request.getSid());
		assertEquals("Check if loaded developer key is correct", "myspecialdeveloperid", request.getDeveloper_key());
		assertEquals("Check if loaded user number is correct", 5, request.getUlfnr());
	}	

}
