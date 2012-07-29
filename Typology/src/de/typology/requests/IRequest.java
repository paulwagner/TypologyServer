/**
 * Interface that defines methods needed in request implementation.
 * Implemented by AbstractRequest, so every request type has to extend AbstractRequest and implement this interface.
 * 
 * @author Paul Wagner
 */
package de.typology.requests;

import java.util.HashMap;

import de.typology.requests.interfaces.svr.DataObjectSvr;

public interface IRequest {

	/**
	 * Get language
	 * 
	 * @return the language
	 */
	public abstract int getLang();

	public abstract boolean setDeveloperKeyToSession(String developer_key);
	
	public abstract boolean setUlfnrToSession(int ulfnr);

	public abstract int getFunction();

	public abstract void setFunction(int function);

	public abstract int getUlfnr();

	public abstract String getDeveloperKey();

	/**
	 * Get parameter from request object or null if key not existing
	 * 
	 * @param key
	 *            Parameter key
	 * @return parameter value or null
	 */
	public abstract String getRequestParameter(String key);

	/**
	 * Method for making response to client.
	 * 
	 * @param d
	 *            Json text to response
	 */
	public abstract void makeResponse(DataObjectSvr d);

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
	public abstract void makeErrorResponse(int status, String msg);

	/**
	 * Set response status code
	 * 
	 * @param code
	 *            Response http code
	 */
	public abstract void setResponseStatus(int code);

	/**
	 * Get current session status
	 * 
	 * @return false if session is null, true otherwise
	 */
	public abstract boolean isSessionLoaded();

	/**
	 * Get session for current request and store in class. If no session is
	 * existing, session is null afterwards. If session is available, also
	 * sessionid will be loaded into class.
	 * 
	 * Read session using getSessionObject()
	 */
	public abstract void getSession();

	/**
	 * Create session for current request and store in class. If a session is
	 * already existing, it will be invalidated. After that, the new sessionid
	 * is loaded into class.
	 * 
	 * Read session using getSessionObject()
	 */
	public abstract void createSession() throws Exception;

	/**
	 * Invalidate current session
	 */
	public abstract void destroySession();

	/**
	 * Load values of session to class.
	 * 
	 * @return true if loaded data is sufficient for run
	 */
	public abstract boolean loadSession();

	/**
	 * Get Session id for current session or null if not loaded
	 * 
	 * @return sessionid or null
	 */
	public abstract String getSessionId();

	/**
	 * Store a value in the current session
	 * 
	 * @param key
	 *            Key for lookup
	 * @param obj
	 *            Object to store
	 * @return false if session is null, true otherwise
	 */
	public abstract boolean storeInSession(String key, Object obj);

	/**
	 * Lookup a value stored in the current session
	 * 
	 * @param key
	 *            Key for lookup
	 * @return value for key or null if session or key not found
	 */
	public abstract Object getSessionValue(String key);

	/**
	 * Lookup a string value stored in the current session
	 * 
	 * @param key
	 *            Key for lookup
	 * @return value for key or null if session or key not found
	 */
	public abstract String getSessionValueAsString(String key);

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
	public abstract Integer getSessionValueAsInteger(String key, int _default);
	
	/**
	 * Perform response from primitive retrieval thread
	 * 
	 */
	public abstract void doPrimitiveRetrievalCallback(HashMap<Integer, String> list);
	
	/**
	 * Perform response from retrieval thread with a queried retrieval
	 */
	public abstract void doRetrievalCallback(HashMap<Double, String> edges1,
			HashMap<Double, String> edges2, HashMap<Double, String> edges3,
			HashMap<Double, String> edges4);

}