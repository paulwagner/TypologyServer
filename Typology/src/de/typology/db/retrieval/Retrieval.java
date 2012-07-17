/**
 * Main Retrieval class.
 * This class uses Renes DBLayer to make fast predictions.
 * We have also an instance of PrimitiveRetrieval here, which is called in the same Thread when we have no results.
 * So no new thread is started, and the callback for the response is triggered from PrimitiveRetrieval...
 * 
 * @author Rene Pickhardt
 */
package de.typology.db.retrieval;

import java.util.HashMap;

public class Retrieval implements IRetrieval {

	@Override
	public void doResponse() {
		// TODO implementieren

	}

	@Override
	public void setSentence(String[] words, String offset) {
		// TODO implementieren

	}

	@Override
	public void eval() {
		// TODO Auto-generated method stub

	}

	@Override
	public HashMap<Integer, String> getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
