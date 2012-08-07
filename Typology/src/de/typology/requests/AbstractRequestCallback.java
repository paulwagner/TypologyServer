/**
 * Abstract class with implementation of callbacks
 * 
 * @author Paul Wagner
 */

package de.typology.requests;

import static de.typology.requests.RequestTools.fillResultSet;
import static de.typology.tools.Resources.FN_GETQUERY;
import static de.typology.tools.Resources.FN_GETRESULT;

import java.util.HashMap;

import de.typology.requests.interfaces.svr.DataObjectSvr;
import de.typology.requests.interfaces.svr.GetPrimitiveObjectSvr;
import de.typology.tools.ConfigHelper;

public abstract class AbstractRequestCallback implements IRequest {
	
	public abstract boolean storeInSession(String key, Object obj);
	public abstract void makeResponse(DataObjectSvr d);
	
	public abstract int getFunction();
	
	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#doPrimitiveRetrievalCallback()
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
	
	/* (non-Javadoc)
	 * @see de.typology.requests.IRequest#doRetrievalCallback()
	 */	
	
	@Override
	public void doRetrievalCallback(HashMap<Double, String> edges1,
			HashMap<Double, String> edges2, HashMap<Double, String> edges3,
			HashMap<Double, String> edges4) {
		if (getFunction() == FN_GETQUERY) {
			// TODO: truncate, store in session, fill dataobject and make
			// response
		}
		if (getFunction() == FN_GETRESULT) {
			// TODO: merge, truncate, store in session, fill db object and make
			// response
		}
	}	

}
