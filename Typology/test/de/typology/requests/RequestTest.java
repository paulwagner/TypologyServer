/**
 * Test case for request class.
 * At the moment we just test errors.
 * 
 * @author Paul Wagner
 * 
 */
package de.typology.requests;

import static de.typology.tools.Resources.LN_MAX;
import static de.typology.tools.Resources.SC_ERR;
import static de.typology.tools.Resources.SC_ERR_NO_SESSION;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;

import de.typology.requests.interfaces.svr.DataObjectSvr;

import static org.easymock.EasyMock.expect;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class RequestTest {

	private static Request request; // Class under test
	// Request Interfaces to mock
	private static HttpServletRequest servReq;
	private static HttpServletResponse servResp; 

	private static Gson jsonHandler = new Gson();
	
	@Before
	public void setUp() throws Exception {
		// Create mocking interfaces
		servReq = PowerMock.createMock(HttpServletRequest.class);
		servResp = PowerMock.createMock(HttpServletResponse.class);
		// Create testing class
		request = new Request(LN_MAX, servReq, servResp);
	}

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
}
