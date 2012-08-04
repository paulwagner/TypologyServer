/** 
 * Request processor class that handles threads for logging, retrieval and dbupdate.
 * This class is instantiated once on startup and fed with request data references, created in the servlet.
 * So we don't have to worry about request threads on our own, just about this request class.
 * 
 * So everything concerning a request starts from here, not from the servlet!
 * 
 * @author Paul Wagner
 */
package de.typology.requests;

public interface IRequestProcessor {

	/**
	 * Main execute method which triggers loaded request.
	 */
	public abstract void processRequest(IRequest request);

}