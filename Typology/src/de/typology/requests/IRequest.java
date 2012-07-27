/**
 * Interface for request Objects and JUnit test of request classes.
 * 
 * @author Paul Wagner
 */
package de.typology.requests;

import java.util.HashMap;

public interface IRequest {

	/**
	 * Execute request.
	 */
	public abstract void execute();

	/**
	 * Callback method for Retrieval. Truncates and merges lists, then makes
	 * response
	 * 
	 * @param edges
	 *            _Full_ edges lists
	 */
	public abstract void doRetrievalCallback(HashMap<Double, String> edges1,
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