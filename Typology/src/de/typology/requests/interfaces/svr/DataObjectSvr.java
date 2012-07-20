/**
 * Main dataset object which is sent from server.
 * It's superclass for all server data objects
 *
 * @author Paul Wagner
 *
 */
package de.typology.requests.interfaces.svr;

import static de.typology.tools.Resources.SC_SUCC;

public class DataObjectSvr {
    public int status;
    public String msg;

    public DataObjectSvr(){
    	this(SC_SUCC, "");
    }
    
    public DataObjectSvr(int status, String msg) {
        this.msg = msg;
        this.status = status;
    }
        
}
