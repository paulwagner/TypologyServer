/**
 * Dataset for function getPrimitive which is sent from server
 *
 * @author Paul Wagner
 *
 */
package de.typology.requests.interfaces.svr;

import java.util.HashMap;


public class GetPrimitiveObjectSvr extends DataObjectSvr {
	
	public HashMap<Integer, String> list;
	public int totalcount;
	
	
	public GetPrimitiveObjectSvr(){
		super();
	}
	
	public GetPrimitiveObjectSvr(int status, String msg){
		super(status, msg);
	}

	/**
	 * @param list the list to set
	 */
	public final void setList(HashMap<Integer, String> list) {
		this.list = list;
	}

	/**
	 * @param totalcount the totalcount to set
	 */
	public final void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}

	
	

}
