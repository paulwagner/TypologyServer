/**
 * Method test case for request class.
 * This is not a class test by definition, because we have just one method.
 * 
 * @author Paul Wagner
 * 
 */
package de.typology.requests;

import static de.typology.tools.Resources.FN_CLOSESESSION;
import static de.typology.tools.Resources.FN_GETPRIMITIVE;
import static de.typology.tools.Resources.FN_INITIATESESSION;
import static de.typology.tools.Resources.SC_ERR;
import static de.typology.tools.Resources.SC_ERR_INSUFFICIENT_REQUEST_DATA;
import static de.typology.tools.Resources.SC_ERR_NO_SESSION;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.powermock.api.easymock.PowerMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;

import de.typology.rdb.connectors.MySQLSessionConnector;
import de.typology.requests.interfaces.client.GetPrimitiveObjectClient;
import de.typology.requests.interfaces.client.InitiateSessionObjectClient;
import de.typology.requests.interfaces.svr.DataObjectSvr;
import de.typology.requests.interfaces.svr.InitiateSessionObjectSvr;
import de.typology.retrieval.IRetrieval;
import de.typology.retrieval.IRetrievalFactory;
import de.typology.threads.ThreadContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ThreadContext.class})
public class RequestProcessorTest {

	// MEMBERS
	
	private static RequestProcessor processor;
	// Request Interfaces to mock
	private static IRequest request;
	private static IRetrievalFactory retrievalFactory;

	private static Gson jsonHandler = new Gson();
	
	// SETUP
	
	@Before
	public void setUp() throws Exception {
		// Create mocking interfaces
		request = PowerMock.createMock(IRequest.class);
		retrievalFactory = PowerMock.createMock(IRetrievalFactory.class);
		// Create testing class
		processor = new RequestProcessor(retrievalFactory);
	}

	// TESTS
	
	/**
	 * If do parameter is empty, makeErrorReponse should be called with code SC_ERR
	 */
	@Test
	public void processRequest_emptyDoParameter_SCERR() {
		// Prepare request for mocking
		request.getSession();
		expect(request.getRequestParameter("do")).andReturn("");
		request.makeErrorResponse(SC_ERR,
				"Unable to read function. Have you declared it?");
		
		// Run mock
		replay(request);
		processor.processRequest(request);
		verify(request);
	}

	/**
	 * If do parameter is null, makeErrorReponse should be called with code SC_ERR
	 */
	@Test
	public void processRequest_nullDoParameter_SCERR() {
		// Prepare request for mocking
		request.getSession();
		expect(request.getRequestParameter("do")).andReturn(null);
		request.makeErrorResponse(SC_ERR,
				"Unable to read function. Have you declared it?");
		
		// Run mock
		replay(request);
		processor.processRequest(request);
		verify(request);
	}
	
	/**
	 * If session is null but no initiate session is called, makeError is expected 
	 */
	@Test
	public void processRequest_noInitiateSession_SCERRNOSESSION() {
		// Prepare request for mocking
		request.getSession();
		// Just for expected function calls
		expect(request.getRequestParameter("do")).andReturn("doesn't matter what");
		request.setFunction(-1);
		// Return something other than initiatesession
		expect(request.getFunction()).andReturn(FN_INITIATESESSION + 1);
		expect(request.isSessionLoaded()).andReturn(false);
		request.makeErrorResponse(SC_ERR_NO_SESSION,
				"You need to create a session first using method initiateSession()");
		
		// Run mock
		replay(request);
		processor.processRequest(request);
		verify(request);
	}	
	
	/**
	 * If session cannot be loaded, makeError is expected
	 */
	@Test
	public void processRequest_nullSession_SCERRNOSESSION(){
		// Prepare request for mocking
		request.getSession();
		// Just for expected function calls
		expect(request.getRequestParameter("do")).andReturn("doesn't matter what");
		request.setFunction(-1);
		// Return something other than initiatesession
		expect(request.getFunction()).andReturn(FN_INITIATESESSION + 1);
		expect(request.isSessionLoaded()).andReturn(true);
		expect(request.loadSession()).andReturn(false);
		request.makeErrorResponse(SC_ERR_NO_SESSION,
				"Failed to load session necessary data from session. Perhaps you should create a new one...");

		// Run mock
		replay(request);
		processor.processRequest(request);
		verify(request);	
	}
	
	/**
	 * If functionname is not kown, makeError is expected
	 */
	@Test
	public void processRequest_functionUnknown_SCERR() {
		// Prepare request for mocking
		request.getSession();
		// Just for expected function calls
		expect(request.getRequestParameter("do")).andReturn("doesn't matter what");
		request.setFunction(-1);
		// Return unknown function
		expect(request.getFunction()).andReturn(-1);
		request.makeErrorResponse(SC_ERR,
				"Unknown function. Refer to wiki.typology.de for the API");
		
		// Run mock
		replay(request);
		processor.processRequest(request);
		verify(request);	
	}
	
	/**
	 * If functionname is not registered, makeError is expected
	 */
	@Test
	public void processRequest_functionUnregistered_SCERR() {
		// Prepare request for mocking
		request.getSession();
		// Just for expected function calls
		expect(request.getRequestParameter("do")).andReturn("doesn't matter what");
		request.setFunction(-1);
		// Return unregistered function
		expect(request.getFunction()).andReturn(-2);
		expect(request.isSessionLoaded()).andReturn(true);
		expect(request.loadSession()).andReturn(true);
		expect(request.storeInSession("function", -2)).andReturn(true);
		request.makeErrorResponse(SC_ERR,
				"Known but unregistered function. Refer to wiki.typology.de for the API");
		
		// Run mock
		replay(request);
		processor.processRequest(request);
		verify(request);	
	}	
	
	/**
	 * If initiatesession is called without dataobject, makeError is expected
	 */
	@Test
	public void processRequest_initiateSessionWithoutData_SCERRINSUFFICIENTREQUESTDATA() {
		// Prepare request for mocking
		request.getSession();
		// Just for expected function calls
		expect(request.getRequestParameter("do")).andReturn("initiatesession");
		request.setFunction(FN_INITIATESESSION);
		// Return initiatesession function
		expect(request.getFunction()).andReturn(FN_INITIATESESSION);
		// Return request parameter
		expect(request.getRequestParameter("data")).andReturn(null);
		request.makeErrorResponse(SC_ERR_INSUFFICIENT_REQUEST_DATA,
				"Insufficient request data. We need at least a valid developer key. Refer to wiki.typology.de for the API");		
		
		// Run mock
		replay(request);
		processor.processRequest(request);
		verify(request);	
	}	
	
	/**
	 * If initiatesession is called without developerkey, makeError is expected
	 */
	@Test
	public void processRequest_initiateSessionWithoutDeveloperKey_SCERRINSUFFICIENTREQUESTDATA() {
		// Prepare request for mocking
		request.getSession();
		// Just for expected function calls
		expect(request.getRequestParameter("do")).andReturn("initiatesession");
		request.setFunction(FN_INITIATESESSION);
		// Return initiatesession function
		expect(request.getFunction()).andReturn(FN_INITIATESESSION);
		// Return request parameter
		InitiateSessionObjectClient data = new InitiateSessionObjectClient();
		data.uid="userid";
		data.version = 1.2;
		data.dkey = "";
		expect(request.getRequestParameter("data")).andReturn(jsonHandler.toJson(data));
		request.makeErrorResponse(SC_ERR_INSUFFICIENT_REQUEST_DATA,
				"Insufficient request data. We need at least a valid developer key. Refer to wiki.typology.de for the API");		
		
		// Run mock
		replay(request);
		processor.processRequest(request);
		verify(request);	
	}		
	
	/**
	 * If initiatesession is called correctly, some function calls are expected
	 */
	@Test
	public void processRequest_initiateSession_callExpectedMethods() throws Exception{
		// Prepare request for mocking
		request.getSession();
		// Just for expected function calls
		expect(request.getRequestParameter("do")).andReturn("initiatesession");
		request.setFunction(FN_INITIATESESSION);
		// Return initiate function
		expect(request.getFunction()).andReturn(FN_INITIATESESSION);
		// Return request parameter
		InitiateSessionObjectClient data = new InitiateSessionObjectClient();
		data.uid="userid";
		data.version = 1.2;
		data.dkey = "developerkey";
		expect(request.getRequestParameter("data")).andReturn(jsonHandler.toJson(data));
		// Expected method calls
		request.createSession();
		// Mock MySQL session connector in
		MySQLSessionConnector sessionConnector = PowerMock.createMock(MySQLSessionConnector.class);
		expect(sessionConnector.checkDeveloperKey("developerkey")).andReturn(5);
		mockStatic(ThreadContext.class);
		expect(ThreadContext.getMySQLSessionConnector()).andReturn(sessionConnector);
		// Expected method calls
		expect(request.setDeveloperKeyToSession(5)).andReturn(true);
		request.makeResponse(isA(InitiateSessionObjectSvr.class));
		
		// Run mock
		replayAll();
		processor.processRequest(request);
		verifyAll();			
	}

	/**
	 * If initiatesession is called correctly, some function calls are expected
	 */
	@Test
	public void processRequest_initiateSessionThrowsException_SCERRNOSESSION() throws Exception{
		// Prepare request for mocking
		request.getSession();
		// Just for expected function calls
		expect(request.getRequestParameter("do")).andReturn("initiatesession");
		request.setFunction(FN_INITIATESESSION);
		// Return initiatesession function
		expect(request.getFunction()).andReturn(FN_INITIATESESSION);
		// Return request parameter
		InitiateSessionObjectClient data = new InitiateSessionObjectClient();
		data.uid="userid";
		data.version = 1.2;
		data.dkey = "developerkey";
		expect(request.getRequestParameter("data")).andReturn(jsonHandler.toJson(data));
		// CreateSession throws exception
		request.createSession();
		expectLastCall().andThrow(new Exception());
		// Expected method calls
		request.destroySession();
		request.makeErrorResponse(SC_ERR, "Session creation failed! Nothing you can do here, please be patient until error is resolved.");		
		
		// Run mock
		replay(request);
		processor.processRequest(request);
		verify(request);			
	}	
	
	/**
	 * If getPrimitive is called without offset, makeError is expected
	 */
	@Test
	public void processRequest_getPrimitiveWithoutData_callPrimitiveRetrievalWithEmptyOffset() {
		// Prepare request for mocking
		request.getSession();
		// Just for expected function calls
		expect(request.getRequestParameter("do")).andReturn("getprimitive");
		request.setFunction(FN_GETPRIMITIVE);
		// Return getprimitive function
		expect(request.getFunction()).andReturn(FN_GETPRIMITIVE);
		expect(request.isSessionLoaded()).andReturn(true);
		expect(request.loadSession()).andReturn(true);
		expect(request.storeInSession("function", FN_GETPRIMITIVE)).andReturn(true);		
		// Return request parameter
		expect(request.getRequestParameter("data")).andReturn(jsonHandler.toJson(null));
		// Prepare retrieval for mocking
		IRetrieval ret = PowerMock.createMock(IRetrieval.class);
		ret.setSentence(null, "");
		ret.run();
		// Prepare requestprocessor for mocking
		expect(retrievalFactory.getInstanceOfPrimitiveRetrieval(request)).andReturn(ret);
		
		// Run mock
		replayAll();
		processor.processRequest(request);
		verifyAll();	
	}
	
	/**
	 * If getPrimitive is called correctly, primitiveRetrieval.run() should be called
	 */
	@Test
	public void processRequest_getPrimitive_callPrimitiveRetrieval(){
		// Prepare request for mocking
		request.getSession();
		// Just for expected function calls
		expect(request.getRequestParameter("do")).andReturn("getprimitive");
		request.setFunction(FN_GETPRIMITIVE);
		// Return getprimitive function
		expect(request.getFunction()).andReturn(FN_GETPRIMITIVE);
		expect(request.isSessionLoaded()).andReturn(true);
		expect(request.loadSession()).andReturn(true);
		expect(request.storeInSession("function", FN_GETPRIMITIVE)).andReturn(true);		
		// Return request parameter
		GetPrimitiveObjectClient data = new GetPrimitiveObjectClient();
		data.offset = "da";
		expect(request.getRequestParameter("data")).andReturn(jsonHandler.toJson(data));
		// Prepare retrieval for mocking
		IRetrieval ret = PowerMock.createMock(IRetrieval.class);
		ret.setSentence(null, "da");
		ret.run();
		// Prepare requestprocessor for mocking
		expect(retrievalFactory.getInstanceOfPrimitiveRetrieval(request)).andReturn(ret);
		
		// Run mock
		replayAll();
		processor.processRequest(request);
		verifyAll();	
	}
	
	/**
	 * If closeSession is called correctly, some function calls are expected
	 */
	@Test
	public void processRequest_closeSession_callExpectedMethods() throws Exception{
		// Prepare request for mocking
		request.getSession();
		// Just for expected function calls
		expect(request.getRequestParameter("do")).andReturn("closesession");
		request.setFunction(FN_CLOSESESSION);
		// Return initiate function
		expect(request.getFunction()).andReturn(FN_CLOSESESSION);
		expect(request.isSessionLoaded()).andReturn(true);
		expect(request.loadSession()).andReturn(true);	
		expect(request.storeInSession("function", FN_CLOSESESSION)).andReturn(true);
		// Expected method calls
		request.destroySession();
		request.makeResponse(isA(DataObjectSvr.class));
		
		// Run mock
		replayAll();
		processor.processRequest(request);
		verifyAll();			
	}	
	
}
