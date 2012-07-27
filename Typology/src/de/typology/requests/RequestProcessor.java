package de.typology.requests;

import static de.typology.requests.RequestTools.translateFunctionName;
import static de.typology.tools.Resources.FN_GETPRIMITIVE;
import static de.typology.tools.Resources.FN_INITIATESESSION;
import static de.typology.tools.Resources.SC_ERR;
import static de.typology.tools.Resources.SC_ERR_INSUFFICIENT_REQUEST_DATA;
import static de.typology.tools.Resources.SC_ERR_NO_SESSION;
import static de.typology.tools.Resources.SC_WRN_RET_INTERRUPTED;
import static de.typology.tools.Resources.SC_WRN_RET_TIMEOUT;

import com.google.gson.Gson;

import de.typology.requests.interfaces.client.GetPrimitiveObjectClient;
import de.typology.requests.interfaces.client.InitiateSessionObjectClient;
import de.typology.requests.interfaces.svr.InitiateSessionObjectSvr;
import de.typology.retrieval.IRetrieval;
import de.typology.retrieval.PrimitiveRetrieval;
import de.typology.threads.ThreadContext;
import de.typology.tools.ConfigHelper;
import de.typology.tools.IOHelper;

public class RequestProcessor implements IRequestProcessor {

	// MEMBERS have to be stateless!!
	
	private final Gson jsonHandler = ThreadContext.jsonHandler;
	
	//private final IRetrievalFactory retrievalFactory;
	
	// CONSTRUCTOR
	
	//public RequestProcessor(IRetrievalFactory factory){
	//	this.retrievalFactory = factory;
	//}
	
	// FUNCTIONS
	
	/**
	 * Main execute method which triggers loaded request.
	 * 
	 * @see de.typology.requests.IRequestProcessor#execute()
	 */
	@Override
	public void processRequest(IRequest request) {

		String s = request.getRequestParameter("do");
		if (s == null || s.isEmpty()) {
			request.makeErrorResponse(SC_ERR,
					"Unable to read function. Have you declared it?");
			return;
		}
		request.setFunction(translateFunctionName(s));

		// If we don't have a session and no initiatesession request, we don't
		// want to do anything
		if(request.getFunction() != FN_INITIATESESSION){
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
		}
		
		// Hop into requested function
		switch (request.getFunction()) {
		case FN_GETPRIMITIVE:
			getPrimitive(request);
			return;
		case FN_INITIATESESSION:
			initiateSession(request);
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
	 */
	private void getPrimitive(IRequest request) {
		String s = request.getRequestParameter("data");

		GetPrimitiveObjectClient data = jsonHandler.fromJson(s,
				GetPrimitiveObjectClient.class);
		if (data.offset == null) {
			request.makeErrorResponse(SC_ERR,
					"Unable to parse data parameter. Offset is not avaiable. Refer to wiki.typology.de for the API");
			return;
		}

		// TODO start new threads for logging, we dont need db maintenance with
		// primitive retrieval

		IRetrieval ret = new PrimitiveRetrieval(request, request.getLang());
		ret.setSentence(null, data.offset);

		startRetrievalThread(request, ret);
	}

	/**
	 * API function initiateSession(); This method doesn't create new threads,
	 * because the client needs to know when the session has been created.
	 */
	private void initiateSession(IRequest request) {

		// Load input data
		String s = request.getRequestParameter("data");

		InitiateSessionObjectClient data = jsonHandler.fromJson(s,
				InitiateSessionObjectClient.class);
		if (data == null || data.dkey == null || data.dkey.isEmpty()) {
			request.makeErrorResponse(SC_ERR_INSUFFICIENT_REQUEST_DATA,
					"Insufficient request data. We need at least the developer key. Refer to wiki.typology.de for the API");
			return;
		}		
		
		try{
			request.createSession();
			
			// TODO connect to mysql database through RDBSessionConnector and check if developer key is valid. For now every key is valid.
			request.setDeveloperKeyToSession(data.dkey);
		
			// TODO if we have an uid key we have to create or update the user with given userinfo and config (using RDBSessionConnector) and write to this.ulfnr
			
			// TODO now create session with developer link (optional with user link) using RDBSessionConnector
		
		} catch (Exception e){
			// If there is an error, destroy session if existing or it will remain in db forever!
			request.destroySession();
			request.makeErrorResponse(SC_ERR, "Session creation failed! Nothing you can do here, please be patient until error is resolved.");
			return;
		}
		
		// If everything successful make response
		InitiateSessionObjectSvr response = new InitiateSessionObjectSvr();
		request.makeResponse(response);
		
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
