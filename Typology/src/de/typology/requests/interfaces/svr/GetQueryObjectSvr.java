/**
 * Dataset for function getQuery which is sent from server
 *
 * @author Paul Wagner
 *
 */
package de.typology.requests.interfaces.svr;

import java.util.HashMap;


public class GetQueryObjectSvr extends DataObjectSvr {
	
	public HashMap<Double, Integer> list1;
	public HashMap<Double, Integer> list2;
	public HashMap<Double, Integer> list3;
	public HashMap<Double, Integer> list4;
	public int totalcount1;
	public int totalcount2;
	public int totalcount3;
	public int totalcount4;
	
	
	public GetQueryObjectSvr(){
		super();
	}
	
	public GetQueryObjectSvr(int status, String msg){
		super(status, msg);
	}

	/**
	 * @param list1 the list1 to set
	 */
	public final void setList1(HashMap<Double, Integer> list1) {
		this.list1 = list1;
	}

	/**
	 * @param list2 the list2 to set
	 */
	public final void setList2(HashMap<Double, Integer> list2) {
		this.list2 = list2;
	}

	/**
	 * @param list3 the list3 to set
	 */
	public final void setList3(HashMap<Double, Integer> list3) {
		this.list3 = list3;
	}

	/**
	 * @param list4 the list4 to set
	 */
	public final void setList4(HashMap<Double, Integer> list4) {
		this.list4 = list4;
	}

	/**
	 * @param totalcount1 the totalcount1 to set
	 */
	public final void setTotalcount1(int totalcount1) {
		this.totalcount1 = totalcount1;
	}

	/**
	 * @param totalcount2 the totalcount2 to set
	 */
	public final void setTotalcount2(int totalcount2) {
		this.totalcount2 = totalcount2;
	}

	/**
	 * @param totalcount3 the totalcount3 to set
	 */
	public final void setTotalcount3(int totalcount3) {
		this.totalcount3 = totalcount3;
	}

	/**
	 * @param totalcount4 the totalcount4 to set
	 */
	public final void setTotalcount4(int totalcount4) {
		this.totalcount4 = totalcount4;
	}
	

}
