/**
 * Request data class that holds just the necessary data to process a request.
 * Here shouldn't be any request implementation, just the necessary data/session handling.
 *
 * @author Paul Wagner
 *
 */
package de.typology.requests;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.typology.requests.interfaces.svr.DataObjectSvr;
import de.typology.threads.ThreadContext;
import de.typology.tools.IOHelper;

public class Request extends AbstractRequestCallback {

	// PROPERTIES

	private final int LANG;
	private final HttpServletRequest requestObj;
	private final HttpServletResponse responseObj;
	private HttpSession session;

	private int function;

	// Primary keys of db tables are stored in session.
	// Everything else gets queried on runtime
	private String developer_key;
	private int ulfnr = -1;
	private String sid;

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
	}

	// METHODS

	// Class getter/setter

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#getLang()
	 */
	@Override
	public final int getLang() {
		return this.LANG;
	}

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#setDeveloperKeyToSession(java.lang.String)
	 */
	@Override
	public boolean setDeveloperKeyToSession(String developer_key) {
		this.developer_key = developer_key;
		return storeInSession("developer_key", developer_key);
	}
	
	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#seUlfnrToSession(java.lang.Integer)
	 */	
	@Override
	public boolean setUlfnrToSession(int ulfnr){
		this.ulfnr = ulfnr;
		return storeInSession("ulfnr", ulfnr);
	}
	
	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#getFunction()
	 */
	@Override
	public int getFunction(){
		return this.function;
	}
	
	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#setFunction(int)
	 */
	@Override
	public void setFunction(int function){
		this.function = function;
	}
	
	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#getUlfnr()
	 */
	@Override
	public int getUlfnr(){
		return this.ulfnr;
	}
	
	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#getDeveloperKey()
	 */
	@Override
	public String getDeveloperKey(){
		return this.developer_key;
	}

	// Request/Response

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#getRequestParameter(java.lang.String)
	 */
	@Override
	public String getRequestParameter(String key) {
		return this.requestObj.getParameter(key);
	}

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#makeResponse(de.typology.requests.interfaces.svr.DataObjectSvr)
	 */
	@Override
	public void makeResponse(DataObjectSvr d) {
		String data = ThreadContext.jsonHandler.toJson(d);
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
	
	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#makeErrorResponse(int, java.lang.String)
	 */
	@Override
	public void makeErrorResponse(int status, String msg) {
		setResponseStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		makeResponse(new DataObjectSvr(status, msg));
	}
	
	
	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#setResponseStatus(int)
	 */
	@Override
	public void setResponseStatus(int code){
		this.responseObj.setStatus(code);
	}

	// Session creation/destruction

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#isSessionLoaded()
	 */
	@Override
	public boolean isSessionLoaded() {
		return (this.session != null);
	}

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#getSession()
	 */
	@Override
	public void getSession() {
		this.session = this.requestObj.getSession(false);
		if (this.session != null) {
			this.sid = this.session.getId();
		}
	}

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#createSession()
	 */
	@Override
	public void createSession() throws Exception {
		destroySession();
		this.session = this.requestObj.getSession();
		if (this.session == null) {
			throw new Exception("Couldn't create session!");
		}
		this.sid = this.session.getId();
	}

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#destroySession()
	 */
	@Override
	public void destroySession() {
		if (this.session != null) {
			this.session.invalidate();
			this.sid = null;
		}
	}

	// Session loading

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#loadSession()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#getSessionId()
	 */
	@Override
	public String getSessionId() {
		return this.sid;
	}

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#storeInSession(java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean storeInSession(String key, Object obj) {
		if (this.session != null) {
			this.session.setAttribute(key, obj);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#getSessionValue(java.lang.String)
	 */
	@Override
	public Object getSessionValue(String key) {
		if (this.session != null) {
			return this.session.getAttribute(key);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#getSessionValueAsString(java.lang.String)
	 */
	@Override
	public String getSessionValueAsString(String key) {
		return (String) getSessionValue(key);
	}

	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#getSessionValueAsInteger(java.lang.String, int)
	 */
	@Override
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
