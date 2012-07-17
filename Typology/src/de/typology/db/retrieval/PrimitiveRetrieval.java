/**
 * Primitive Retrieval class.
 * This class uses the primitiveLayer to make primitive predictions.
 * 
 * @author Paul Wagner
 */
package de.typology.db.retrieval;

import java.util.HashMap;

import de.typology.db.layer.PrimitiveLayer;
import de.typology.requests.Request;
import de.typology.threads.ThreadContext;
import de.typology.tools.ConfigHelper;

public class PrimitiveRetrieval implements IRetrieval, Runnable {

	// PROPERTIES
	
	public Request requestObj;
	
	private String word;
	private final int lang;
	private HashMap<Integer, String> resultMap = new HashMap<Integer, String>();
	
	
	// CONSTRUCTORS
	
	public PrimitiveRetrieval(Request requestObj, int lang){
		this.requestObj = requestObj;
		this.lang = lang;
	}

	
	// METHODS
	
	/**
	 * Set sentence.
	 * 
	 * @param words not used
	 * @param offset offset of typed word
	 */
	@Override
	public void setSentence(String[] words, String offset) {
		this.word = offset;
	}

	/**
	 * Evaluate retrieval.
	 * This method fills resultMap.
	 */
	@Override
	public void eval() {
		//TODO Testen
		PrimitiveLayer db = (PrimitiveLayer) ThreadContext.getPrimitiveLayer(this.lang);
		HashMap<Integer, String> map = db.getNodeMap();
		
		//Iterate through until enough results
		int c = 0;
		resultMap.clear();
		for(String s : map.values()){
			if(c >= ConfigHelper.getRESULT_SIZE()){
				break;
			}
			if(s.startsWith(word)){
				resultMap.put(c, s);
				c++;
			}
		}
	}

	@Override
	public void doResponse() {
		requestObj.makeResponse(resultMap, true);
		
	}

	@Override
	public HashMap<Integer, String> getResult() {
		return resultMap;
	}


	/**
	 * Run method invokes the eval() method and the response method.
	 */
	@Override
	public void run() {
		eval();
		doResponse();
	}
	
	
}
