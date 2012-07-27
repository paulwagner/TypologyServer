/**
 * Request class that handles threads for logging, retrieval and dbupdate.
 * This class is instanciated directly in the doPost() Method(s).
 * So we don't have to worry about request threads on our own, just about this request class.
 * 
 * So everything concerning a request starts from here, not from the servlet!
 * 
 * TODO: Define Request as interface or abstract class definieren.
 * By that you can take other connectors like jWebSocket easily.
 *
 * @author Paul Wagner
 *
 */
package de.typology.requests;

import static de.typology.requests.RequestTools.fillResultSet;
import static de.typology.requests.RequestTools.translateFunctionName;
import static de.typology.tools.Resources.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import de.typology.requests.interfaces.client.GetPrimitiveObjectClient;
import de.typology.requests.interfaces.client.InitiateSessionObjectClient;
import de.typology.requests.interfaces.svr.DataObjectSvr;
import de.typology.requests.interfaces.svr.GetPrimitiveObjectSvr;
import de.typology.requests.interfaces.svr.InitiateSessionObjectSvr;
import de.typology.retrieval.IRetrieval;
import de.typology.retrieval.PrimitiveRetrieval;
import de.typology.threads.ThreadContext;
import de.typology.tools.ConfigHelper;
import de.typology.tools.IOHelper;

public class Request implements IRequestProcessor {

	// PROPERTIES

	private final int LANG;
	private final HttpServletRequest requestObj;
	private final HttpServletResponse responseObj;

	private int function;

	private HttpSession session;
	private String sid;

	// Primary keys of db tables are stored in session.
	// Everything else gets queried on runtime
	private String developer_key;
	private int ulfnr = -1;

	private Gson jsonHandler = ThreadContext.jsonHandler;

	// CONSTRUCTOR

	public Request(int LANG, HttpServletRequest request,
			HttpServletResponse response) {
		this.LANG = LANG;
		this.requestObj = request;
		this.responseObj = response;
	}

	// GETTERS

	/**
	 * Get sessionid of loaded request
	 * 
	 * @return the sid
	 */
	public final String getSid() {
		return sid;
	}

	/**
	 * Get developer key of loaded request
	 * 
	 * @return the developer_key
	 */
	public final String getDeveloper_key() {
		return developer_key;
	}

	/**
	 * Get user number of loaded request.
	 * 
	 * @return the ulfnr
	 */
	public final int getUlfnr() {
		return ulfnr;
	}

	// FUNCTION
	
	/**
	 * Main execute method which triggers loaded request.
	 * 
	 * @see de.typology.requests.IRequestProcessor#execute()
	 */
	@Override
	public void execute() {

		String s = requestObj.getParameter("do");
		if (s == null || s.isEmpty()) {
			makeErrorResponse(SC_ERR,
					"Unable to read function. Have you declared it?");
			return;
		}
		this.function = translateFunctionName(s);

		// If we don't have a session and no initiatesession request, we don't
		// want to do anything
		if(this.function != FN_INITIATESESSION){
			this.session = this.requestObj.getSession(false);
			if(this.session == null){
				makeErrorResponse(SC_ERR_NO_SESSION,
						"You need to create a session first using method initiateSession()");
				return;
			}			
			if (!loadSession()) {
				makeErrorResponse(
						SC_ERR_NO_SESSION,
						"Failed to load session necessary data from session. Perhaps you should create a new one...");
				return;
			}
		}
		
		// Hop into requested function
		switch (this.function) {
		case FN_GETPRIMITIVE:
			getPrimitive();
			return;
		case FN_INITIATESESSION:
			initiateSession();
			return;
		case -1:
			makeErrorResponse(SC_ERR,
					"Unknown function. Refer to wiki.typology.de for the API");
			return;
		default:
			makeErrorResponse(SC_ERR,
					"Known but unregistered function. Refer to wiki.typology.de for the API");
			return;
		}
	}

	/**
	 * API function getPrimitive(); This method creates new thread to make
	 * logging independent from retrieval time.
	 */
	private void getPrimitive() {
		String s = requestObj.getParameter("data");

		GetPrimitiveObjectClient data = jsonHandler.fromJson(s,
				GetPrimitiveObjectClient.class);
		if (data.offset == null) {
			makeErrorResponse(
					SC_ERR,
					"Unable to parse data parameter. Offset is not avaiable. Refer to wiki.typology.de for the API");
			return;
		}

		// TODO start new threads for logging, we dont need db maintenance with
		// primitive retrieval

		IRetrieval ret = new PrimitiveRetrieval(this, LANG);
		ret.setSentence(null, data.offset);

		startRetrievalThread(ret);
	}

	/**
	 * API function initiateSession(); This method doesn't create new threads,
	 * because the client needs to know when the session has been created.
	 */
	private void initiateSession() {

		// Load input data
		String s = requestObj.getParameter("data");

		InitiateSessionObjectClient data = jsonHandler.fromJson(s,
				InitiateSessionObjectClient.class);
		if (data == null || data.dkey == null || data.dkey.isEmpty()) {
			makeErrorResponse(
					SC_ERR_INSUFFICIENT_REQUEST_DATA,
					"Insufficient request data. We need at least the developer key. Refer to wiki.typology.de for the API");
			return;
		}		
		
		try{
			this.session = this.requestObj.getSession();
			this.sid = getSessionId();
			
			// TODO connect to mysql database through RDBSessionConnector and check if developer key is valid. For now every key is valid.
			this.developer_key = data.dkey;
			storeInSession("developer_key", this.developer_key);
		
			// TODO if we have an uid key we have to create or update the user with given userinfo and config (using RDBSessionConnector) and write to this.ulfnr
			
			// TODO now create session with developer link (optional with user link) using RDBSessionConnector
		
		} catch (Exception e){
			// If there is an error, destroy session or it will remain in db forever!
			if(this.session != null){
				this.session.invalidate();
			}
		}
		
		// If everything successful make response
		InitiateSessionObjectSvr response = new InitiateSessionObjectSvr();
		makeResponse(response);
		
	}

	// CALLBACKS

	/**
	 * Perform response from retrieval thread
	 * 
	 * @see de.typology.requests.IRequestProcessor#doRetrievalCallback(java.util.HashMap,
	 * java.util.HashMap, java.util.HashMap, java.util.HashMap)
	 */
	@Override
	public void doRetrievalCallback(HashMap<Double, String> edges1,
			HashMap<Double, String> edges2, HashMap<Double, String> edges3,
			HashMap<Double, String> edges4) {
		if (function == FN_GETQUERY) {
			// TODO: truncate, store in session, fill dataobject and make
			// response
		}
		if (function == FN_GETRESULT) {
			// TODO: merge, truncate, store in session, fill db object and make
			// response
		}
	}

	/**
	 * Perform response from primitive retrieval thread
	 * 
	 * @see
	 * de.typology.requests.IRequestProcessor#doPrimitiveRetrievalCallback(java.util.
	 * HashMap)
	 */
	@Override
	public void doPrimitiveRetrievalCallback(HashMap<Integer, String> list) {
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		if (list.size() > ConfigHelper.getRESULT_SIZE()) {
			storeInSession("list.primitive", list);
		} else {
			storeInSession("list.primitive", null);
		}
		fillResultSet(list, result, 0);

		GetPrimitiveObjectSvr data = new GetPrimitiveObjectSvr();
		data.list = result;
		data.totalcount = list.size();
		makeResponse(data);
	}

	// RESPONSE

	/**
	 * Method for making response to client.
	 * 
	 * @param d
	 *            DataObject to send to client
	 */
	private void makeResponse(DataObjectSvr d) {
		// TODO Maybe we have to parse d to its original type if gson can't
		// handle it that way
		String data = jsonHandler.toJson(d);
		// TODO set header?
		try {
			PrintWriter out = responseObj.getWriter();
			out.write(data);
			out.flush();
			out.close();
		} catch (IOException e) {
			IOHelper.logErrorExceptionContext(e);
		}
	}

	/**
	 * Wrapper method for quickly make error response if something happens
	 * inside retrieval class. If an error occurs in any child thread, use
	 * doCallback methods to catch this.
	 * 
	 * @param status
	 *            The status code
	 * @param msg
	 *            The message
	 */
	private void makeErrorResponse(int status, String msg) {
		this.responseObj
				.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		makeResponse(new DataObjectSvr(status, msg));
	}

	// HELPERS

	/**
	 * Start a prepared retrieval in new thread and handles it's timeout
	 * 
	 * @param ret
	 *            The retrieval to be started.
	 */
	private void startRetrievalThread(IRetrieval ret) {
		// Make a new thread for retrieval
		// By default, the new thread runs without affecting request time.
		// But if necessary you can also interrupt the request.
		Thread t = new Thread(ret);
		t.start();
		try {
			t.join(ConfigHelper.getRET_TIMEOUT() * 1000L);
		} catch (InterruptedException e) {
			String msg = "WARNING: (Request.execute(" + this.function
					+ ")) Retrieval has been interrupted (sid: " + this.sid
					+ ")";
			IOHelper.logErrorContext(msg);
			makeErrorResponse(SC_WRN_RET_INTERRUPTED, "");
		}
		if (t.isAlive()) {
			ret.interrupt();
			String msg = "WARNING: (Request.execute(" + this.function
					+ ")) Retrieval has been timeouted (sid: " + this.sid + ")";
			IOHelper.logErrorContext(msg);
			makeErrorResponse(SC_WRN_RET_TIMEOUT, "");
		}
	}

	/**
	 * Store a value in the current session
	 * 
	 * @param key
	 *            Key for lookup
	 * @param obj
	 *            Object to store
	 * @return false if session is null, true otherwise
	 */
	private boolean storeInSession(String key, Object obj) {
		if (this.session != null) {
			this.session.setAttribute(key, obj);
			return true;
		}
		return false;
	}

	/**
	 * Lookup a value stored in the current session
	 * 
	 * @param key
	 *            Key for lookup
	 * @return value for key or null if session or key not found
	 */
	private Object getSessionValue(String key) {
		if (this.session != null) {
			return this.session.getAttribute(key);
		}
		return null;
	}

	/**
	 * Lookup a string value stored in the current session
	 * 
	 * @param key
	 *            Key for lookup
	 * @return value for key or null if session or key not found
	 */
	private String getSessionValueAsString(String key) {
		return (String) getSessionValue(key);
	}

	/**
	 * Lookup an integer value stored in the current session. If it wasn't
	 * successful (key not found or cast failure), the default value will be
	 * returned
	 * 
	 * @param key
	 *            Key for lookup
	 * @param _default
	 *            Default return value
	 * @return value for key or default
	 */
	private Integer getSessionValueAsInteger(String key, int _default) {
		Object o = getSessionValue(key);
		if (o == null) {
			return _default;
		}
		try {
			return (Integer) o;
		} catch (ClassCastException e) {
			return _default;
		}
	}

	/**
	 * Get id of current session
	 * 
	 * @return sessionid or null if session is null
	 */
	private String getSessionId() {
		if (this.session != null) {
			return this.session.getId();
		}
		return null;
	}

	/**
	 * Load values of session to class.
	 * 
	 * @return true if loaded data is sufficient for run
	 */
	private boolean loadSession() {
		if (this.session != null) {
			this.sid = session.getId();
			this.ulfnr = getSessionValueAsInteger("ulfnr", -1);
			this.developer_key = getSessionValueAsString("developer_key");
			if (this.developer_key == null) {
				return false;
			}
			return true;
		}
		return false;
	}
}
