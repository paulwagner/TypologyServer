package de.typology.requests;

import static de.typology.tools.Resources.*;

public class RequestTools {
	
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
		if(u_name.equals("GEMETRICS")){
			return FN_GETMETRICS;			
		}
		if(u_name.equals("STOREMETRICS")){
			return FN_STOREMETRICS;			
		}
		if(u_name.equals("STORETEXT")){
			return FN_STORETEXT;			
		}
		return -1;
	}

}
