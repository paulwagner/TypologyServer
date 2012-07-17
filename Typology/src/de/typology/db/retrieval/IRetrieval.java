/**
 * Interface that defines Retrieval. It has to be implemented by all
 * Retrievals we use, because it defines the methods that are required by
 * Request and all the other classes. So if something new is defined in
 * Retrieval THAT IS USED IN OTHER CLASSES you have to append it here and use
 * the type IRetrieval for your implementation.
 * 
 * @author Paul Wagner
 */

package de.typology.db.retrieval;

import java.util.HashMap;

import de.typology.requests.Request;
import de.typology.tools.Resources;

public interface IRetrieval extends Runnable {

	// PROPERTIES

	/**
	 * Request object that is used to callback for a response.
	 * 
	 * @see Request#Request(int, javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public Request requestObj = null;
	public int lang = Resources.LN_MAX;

	// METHODS

	/**
	 * Method that does the response. You shouldn't do the response by yourself,
	 * but by using this method, which should call requestObj.makeResponse()
	 * 
	 * @see Request#makeResponse(java.util.HashMap)
	 */
	public void doResponse();
	
	/**
	 * Method that sets the sentence for retrieval
	 */
	public void setSentence(String[] words, String offset);
	
	/**
	 * Method for evaluating the retrieval
	 */
	public void eval();
	
	/**
	 * Method for getting back the result as a Hashmap.
	 * The Hashmap shouldn't contain Node objects because they should stay within the controlled retrieval class.
	 */
	public HashMap<Integer, String> getResult();
	

}
