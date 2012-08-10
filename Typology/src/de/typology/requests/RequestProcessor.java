/**
 * Request processor that executes a request.
 * This implementation uses an object of IRequest to execute request and is therefore connector-independent.
 * Just implement IRequest for any connection method and then call processRequest().
 * 
 * @author Paul Wagner
 * 
 */
package de.typology.requests;

import static de.typology.requests.RequestTools.translateFunctionName;
import static de.typology.tools.Resources.FN_GETPRIMITIVE;
import static de.typology.tools.Resources.FN_INITIATESESSION;
import static de.typology.tools.Resources.FN_CLOSESESSION;
import static de.typology.tools.Resources.SC_ERR;
import static de.typology.tools.Resources.SC_ERR_INSUFFICIENT_REQUEST_DATA;
import static de.typology.tools.Resources.SC_ERR_NO_SESSION;
import static de.typology.tools.Resources.SC_WRN_RET_INTERRUPTED;
import static de.typology.tools.Resources.SC_WRN_RET_TIMEOUT;

import com.google.gson.Gson;

import de.typology.requests.interfaces.client.GetPrimitiveObjectClient;
import de.typology.requests.interfaces.client.InitiateSessionObjectClient;
import de.typology.requests.interfaces.svr.DataObjectSvr;
import de.typology.requests.interfaces.svr.InitiateSessionObjectSvr;
import de.typology.retrieval.IRetrieval;
import de.typology.retrieval.IRetrievalFactory;
import de.typology.threads.ThreadContext;
import de.typology.tools.ConfigHelper;
import de.typology.tools.IOHelper;

public class RequestProcessor implements IRequestProcessor {

	// MEMBERS have to be stateless!!
	
	private final Gson jsonHandler = ThreadContext.jsonHandler;
	
	private final IRetrievalFactory retrievalFactory;
	
	// CONSTRUCTOR
	
	public RequestProcessor(IRetrievalFactory factory){
		this.retrievalFactory = factory;
	}
	
	// FUNCTIONS
	
	/*
	 * @see de.typology.requests.IRequestProcessor#execute()
	 */
	@Override
	public void processRequest(IRequest request) {
		request.getSession(); // First thing to do is load session to context if available

		String s = request.getRequestParameter("do");
		if (s == null || s.isEmpty()) {
			request.makeErrorResponse(SC_ERR,
					"Unable to read function. Have you declared it?");
			return;
		}
		request.setFunction(translateFunctionName(s));
		int function = request.getFunction();
		
		// If we don't have a session and no initiatesession request, we don't
		// want to do anything
		if(function != FN_INITIATESESSION && function != -1){
			if(!request.isSessionLoaded()){
				request.makeErrorResponse(SC_ERR_NO_SESSION,
						"You need to create a session first using method initiateSession()");
				return;
			}			
			if (!request.loadSession()) {
				request.makeErrorResponse(SC_ERR_NO_SESSION,
						"Failed to load session necessary data from session. Perhaps you should create a new one...");
				return;
			}
			request.storeInSession("function", function);
		}
		
		// Hop into requested function
		switch (function) {
		case FN_GETPRIMITIVE:
			getPrimitive(request);
			return;
		case FN_INITIATESESSION:
			initiateSession(request);
			return;
		case FN_CLOSESESSION:
			closeSession(request);
			return;			
		case -1:
			request.makeErrorResponse(SC_ERR,
					"Unknown function. Refer to wiki.typology.de for the API");
			return;
		default:
			request.makeErrorResponse(SC_ERR,
					"Known but unregistered function. Refer to wiki.typology.de for the API");
			return;
		}
	}

	/**
	 * API function getPrimitive(); This method creates new thread to make
	 * logging independent from retrieval time.
	 * 
	 * @param request current request
	 */
	private void getPrimitive(IRequest request) {
		String s = request.getRequestParameter("data");

		GetPrimitiveObjectClient data = jsonHandler.fromJson(s,
				GetPrimitiveObjectClient.class);
		String offset = "";
		if (data != null && data.offset != null) {
			offset = data.offset;
		}

		IRetrieval ret = retrievalFactory.getInstanceOfPrimitiveRetrieval(request);
		ret.setSentence(null, offset);

		startRetrievalThread(request, ret);
	}

	/**
	 * API function initiateSession(); This method doesn't create new threads,
	 * because the client needs to know when the session has been created.
	 * 
	 * @param request current request
	 */
	private void initiateSession(IRequest request) {
		// Load input data
		String s = request.getRequestParameter("data");

		InitiateSessionObjectClient data = jsonHandler.fromJson(s,
				InitiateSessionObjectClient.class);
		if (data == null || data.dkey == null || data.dkey.isEmpty()) {
			request.makeErrorResponse(SC_ERR_INSUFFICIENT_REQUEST_DATA,
					"Insufficient request data. We need at least a valid developer key. Refer to wiki.typology.de for the API");
			return;
		}		
		
		try{			
			request.createSession();
			int dlfnr = ThreadContext.getMySQLSessionConnector().checkDeveloperKey(data.dkey);
			if(dlfnr <= 0){
				request.makeErrorResponse(SC_ERR_INSUFFICIENT_REQUEST_DATA,
						"Insufficient request data. Given developer key is invalid!");
				return;				
			}
			request.setDeveloperKeyToSession(dlfnr);
		
			if(data.uid != null && data.userpass != null && !data.uid.isEmpty() && !data.userpass.isEmpty()){
				int ulfnr = ThreadContext.getMySQLSessionConnector().getOrCreateUlfnr(dlfnr, data.uid, data.userpass);
				request.setUlfnrToSession(ulfnr);
			}
			
		} catch (Exception e){
			request.destroySession();
			request.makeErrorResponse(SC_ERR, "Session creation failed! Nothing you can do here, please be patient until error is resolved.");
			return;
		}
		
		// If everything successful make response
		InitiateSessionObjectSvr response = new InitiateSessionObjectSvr();
		request.makeResponse(response);
	}
	
	/**
	 * API function closeSession()
	 * 
	 * @param request current request
	 */
	private void closeSession(IRequest request) {
		request.destroySession();
		// If everything successful make response
		request.makeResponse(new DataObjectSvr());
	}	

	// HELPERS

	/**
	 * Start a prepared retrieval in new thread and handles it's timeout
	 * 
	 * @param ret
	 *            The retrieval to be started.
	 */
	private void startRetrievalThread(IRequest request, IRetrieval ret) {
		// Make a new thread for retrieval
		// By default, the new thread runs without affecting request time.
		// But if necessary you can also interrupt the request.
		Thread t = new Thread(ret);
		t.start();
		try {
			t.join(ConfigHelper.getRET_TIMEOUT() * 1000L);
		} catch (InterruptedException e) {
			String msg = "WARNING: (Request.execute(" + request.getFunction()
					+ ")) Retrieval has been interrupted (sid: " + request.getSessionId()
					+ ")";
			IOHelper.logErrorContext(msg);
			request.makeErrorResponse(SC_WRN_RET_INTERRUPTED, "");
		}
		if (t.isAlive()) {
			ret.interrupt();
			String msg = "WARNING: (Request.execute(" + request.getFunction()
					+ ")) Retrieval has been timeouted (sid: " + request.getSessionId() + ")";
			IOHelper.logErrorContext(msg);
			request.makeErrorResponse(SC_WRN_RET_TIMEOUT, "");
		}
	}

}
