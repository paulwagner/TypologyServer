/**
 * Represents data sent from client/server
 *
 * @author Paul Wagner
 *
 */
package de.typology.requests;

import java.util.HashMap;
import static de.typology.tools.Resources.SC_SUCC;
import static de.typology.tools.Resources.CS_TYPE_SESSION;

public class DataObject {
    // TO SERVER
    public String[] words;
    public String offset;
    public String uid = new String("");
    public int type = CS_TYPE_SESSION;
    public Object[] config;
    
    // FROM SERVER
    public HashMap<Integer, String> result;
    public boolean primitive;
    public int status;
    public String msg;

    // Server Construct
    public DataObject(HashMap<Integer, String> result, boolean primitive, String uid, int status, String msg) {
        this.result = result;
        this.words = null;
        this.offset = "";
        this.primitive = primitive;
        this.uid = uid;
        this.msg = msg;
        this.status = status;
    }
    
    public DataObject(HashMap<Integer, String> result, boolean primitive, String uid) {
    	this(result, primitive, uid, SC_SUCC, "");
    }

    public DataObject(HashMap<Integer, String> result, boolean primitive) {
    	this(result, primitive, "", SC_SUCC, "");
    }
    
    @Override
    public String toString() {
        if (result == null) {
            return "";
        }
        String s = "";
        for (Integer val : result.keySet()) {
            s += result.get(val) + "(" + val + "), ";
        }
        return s;
    }

    public String toSentence() {
        String s = "";
        for (int i = 0; i < words.length; i++) {
            s += words[i] + " ";
        }
        s += offset;
        return s;
    }
}
