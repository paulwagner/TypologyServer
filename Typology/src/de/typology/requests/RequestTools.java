package de.typology.requests;

import static de.typology.tools.Resources.*;

import java.util.HashMap;

import de.typology.tools.ConfigHelper;

public class RequestTools {
	
	/**
	 * Translate api function names to integer value
	 * 
	 * @param name function name
	 * @return function integer value
	 */
	public static int translateFunctionName(String name){
		String u_name = name.toUpperCase();
		if(u_name.equals("GETQUERY")){
			return FN_GETQUERY;			
		}
		if(u_name.equals("GETRESULT")){
			return FN_GETRESULT;			
		}
		if(u_name.equals("INITIATESESSION")){
			return FN_INITIATESESSION;			
		}
		if(u_name.equals("ENDSESSION")){
			return FN_ENDSESSION;			
		}
		if(u_name.equals("GETPRIMITIVE")){
			return FN_GETPRIMITIVE;			
		}
		if(u_name.equals("GETMORE")){
			return FN_GETMORE;			
		}
		if(u_name.equals("GETMETRICS")){
			return FN_GETMETRICS;			
		}
		if(u_name.equals("STOREMETRICS")){
			return FN_STOREMETRICS;			
		}
		if(u_name.equals("STORETEXT")){
			return FN_STORETEXT;			
		}
		if(u_name.equals("CLOSESESSION")){
			return FN_CLOSESESSION;			
		}
		return -1;
	}
	
	/**
	 * Fills map result with specified values of given retrieval list.
	 * If starting number is out of range, result will be empty.
	 * 
	 * @param list the full retrieval list
	 * @param result map where they should be stored
	 * @param starting number of entry to start with (0-terminated)
	 */
	public static void fillResultSet(HashMap<Integer, String> list,
			HashMap<Integer, String> result, int starting) {
		int c = 0;
		int r = 0;
		result.clear();
		if(starting < 0){
			return;
		}
		for (java.util.Map.Entry<Integer, String> e : list.entrySet()) {
			if(r >= ConfigHelper.getRESULT_SIZE()){
				break;
			}
			if(c >= starting){
				result.put(e.getKey(), e.getValue());
				r++;
			}
			c++;
		}
	}

}
