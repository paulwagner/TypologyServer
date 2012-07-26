/**
 * Dataset for function initiateSession which is sent from server
 *
 * @author Paul Wagner
 *
 */
package de.typology.requests.interfaces.svr;

import static de.typology.tools.Resources.VERSION_NUMBER;

public class InitiateSessionObjectSvr extends DataObjectSvr {
	
	public Double version;
	
	/**
	 * @param sid Session id
	 */
	public InitiateSessionObjectSvr(){
		this.version = VERSION_NUMBER;
	}
	
}
