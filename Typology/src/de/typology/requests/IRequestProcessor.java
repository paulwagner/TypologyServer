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

import java.util.HashMap;

public interface IRequestProcessor {

	/**
	 * Execute request.
	 */
	public abstract void processRequest(Request request);

	/**
	 * Callback method for Retrieval. Truncates and merges lists, then makes
	 * response
	 * 
	 * @param function function number that retrieval is running at. Used for callback.
	 * @param edges
	 *            _Full_ edges lists
	 */
	public abstract void doRetrievalCallback(int function, HashMap<Double, String> edges1,
			HashMap<Double, String> edges2, HashMap<Double, String> edges3,
			HashMap<Double, String> edges4);

	/**
	 * Callback method for PrimitiveRetrieval. Truncates list, then makes
	 * response
	 * 
	 * @param list
	 *            _Full_ result list
	 */
	public abstract void doPrimitiveRetrievalCallback(
			HashMap<Integer, String> list);

}