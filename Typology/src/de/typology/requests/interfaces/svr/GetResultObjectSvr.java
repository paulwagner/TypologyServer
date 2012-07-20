/**
 * Dataset for function getResult which is sent from server
 *
 * @author Paul Wagner
 *
 */
package de.typology.requests.interfaces.svr;

import java.util.HashMap;


public class GetResultObjectSvr extends DataObjectSvr {
	
	public HashMap<Double, Integer> list;
	public int totalcount;	
	

	public GetResultObjectSvr(){
		super();
	}
	
	public GetResultObjectSvr(int status, String msg){
		super(status, msg);
	}
	
	
	/**
	 * @param list the list to set
	 */
	public final void setList(HashMap<Double, Integer> list) {
		this.list = list;
	}
	
	/**
	 * @param totalcount the totalcount to set
	 */
	public final void setTotalcount(int totalcount) {
		this.totalcount = totalcount;
	}

}
