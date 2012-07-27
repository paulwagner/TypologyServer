/**
 * Request data class that holds just the necessary data to process a request.
 * Here shouldn't be any request implementation!
 *
 * TODO define this as interface
 *
 * @author Paul Wagner
 *
 */
package de.typology.requests;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Request {

	// PROPERTIES

	private final int LANG;
	private final HttpServletRequest requestObj;
	private final HttpServletResponse responseObj;
	private HttpSession session;

	public int function;

	// Primary keys of db tables are stored in session.
	// Everything else gets queried on runtime
	public String developer_key;
	public int ulfnr = -1;
	public String sid;

	// CONSTRUCTOR

	public Request(int LANG, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		this.LANG = LANG;
		this.requestObj = request;
		this.responseObj = response;

		if (this.requestObj == null) {
			throw new Exception("Request object is null!");
		}
		if (this.responseObj == null) {
			throw new Exception("Response object is null!");
		}
		getSession();
	}

	// GETTERS

	/**
	 * Get http request object
	 * 
	 * @return the http request object
	 */
	public final HttpServletRequest getRequestObj() {
		return this.requestObj;
	}

	/**
	 * Get http response object
	 * 
	 * @return the http response object
	 */
	public final HttpServletResponse getResponseObj() {
		return this.responseObj;
	}

	/**
	 * Get language
	 * 
	 * @return the language
	 */
	public final int getLang() {
		return this.LANG;
	}

	// METHODS

	// Class getter/setter
	
	public boolean setDeveloperKeyToSession(String developer_key){
		this.developer_key = developer_key;
		return storeInSession("developer_key", developer_key);
	}
	
	// Request
	
	/**
	 * Get parameter from request object or null if key not existing
	 * 
	 * @param key
	 *            Parameter key
	 * @return parameter value or null
	 */
	public String getRequestParameter(String key) {
		return this.requestObj.getParameter(key);
	}
	
	// Session creation/destruction
	
	/**
	 * Get current session object. You have to load session first using getSession()
	 * 
	 * @return current HttpSession
	 */
	public HttpSession getSessionObject(){
		return this.session;
	}

	/**
	 * Get session for current request and store in class.
	 * If no session is existing, session is null afterwards.
	 * If session is available, also sessionid will be loaded into class.
	 * 
	 * Read session using getSessionObject()
	 */
	public void getSession() {
		this.session = this.requestObj.getSession(true);
		if(this.session != null){
			this.sid = this.session.getId();
		}
	}
	
	/**
	 * Create session for current request and store in class.
	 * If a session is already existing, it will be invalidated.
	 * After that, the new sessionid is loaded into class.
	 * 
	 * Read session using getSessionObject()
	 */
	public void createSession() throws Exception {
		if(this.session != null){
			session.invalidate();
		}
		this.session = this.requestObj.getSession();
		if(this.session == null){
			throw new Exception("Couldn't create session!");
		}
		this.sid = this.session.getId();		
	}	
	
	/**
	 * Invalidate current session
	 */
	public void destroySession(){
		if(this.session != null){
			this.session.invalidate();
			this.sid = null;
		}
	}
	
	// Session loading
		
	/**
	 * Load values of session to class.
	 * 
	 * @return true if loaded data is sufficient for run
	 */
	public boolean loadSession() {
		if (this.session != null) {
			this.ulfnr = getSessionValueAsInteger("ulfnr", -1);
			this.developer_key = getSessionValueAsString("developer_key");
			if (this.developer_key == null) {
				return false;
			}
			return true;
		}
		return false;
	}	
	

	// Session get/store
		
	/**
	 * Get Session id for current session or null if not loaded
	 * 
	 * @return sessionid or null
	 */
	public String getSessionId(){
		return this.sid;
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
	public boolean storeInSession(String key, Object obj) {
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
	public Object getSessionValue(String key) {
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
	public String getSessionValueAsString(String key) {
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
	public Integer getSessionValueAsInteger(String key, int _default) {
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
	

}
