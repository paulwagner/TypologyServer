/**
 * Method test case for AbstractRequestCallback
 * 
 * @author Paul Wagner
 */
package de.typology.requests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import de.typology.requests.interfaces.svr.DataObjectSvr;
import de.typology.tools.ConfigHelper;

public class AbstractRequestCallbackTest {

	public AbstractRequestCallback request;

	@Before
	public void setUp() throws Exception {
		// Instantiate abstract request and implement necessary methods for testing the callback methods.
		// This is quite like creating an own mock object...
		request = new AbstractRequestCallback() {
			
			public String storeInSessionKey = "";
			public Object storeInSessionObject = null;
			
			@Override 
			public boolean setUlfnrToSession(int ulfnr) {
				return false;
			}
			
			@Override
			public void setResponseStatus(int code) {
				
			}
			
			@Override
			public void setFunction(int function) {
				
			}
			
			@Override
			public boolean setDeveloperKeyToSession(int dlfnr) {
				return false;
			}
			
			@Override
			public void makeErrorResponse(int status, String msg) {
				
			}
			
			@Override
			public boolean loadSession() {
				return false;
			}
			
			@Override
			public boolean isSessionLoaded() {
				return false;
			}
			
			@Override
			public int getUlfnr() {
				return 0;
			}
			
			@Override
			public String getSessionValueAsString(String key) {
				return this.storeInSessionKey;
			}
			
			@Override
			public Integer getSessionValueAsInteger(String key, int _default) {
				return 0;
			}
			
			@Override
			public Object getSessionValue(String key) {
				return this.storeInSessionObject;
			}
			
			@Override
			public String getSessionId() {
				return null;
			}
			
			@Override
			public void getSession() {
				
			}
			
			@Override
			public String getRequestParameter(String key) {
				return null;
			}
			
			@Override
			public int getLang() {
				return 0;
			}
			
			@Override
			public int getDeveloperKey() {
				return 0;
			}
			
			@Override
			public void destroySession() {
				
			}
			
			@Override
			public void createSession() throws Exception {
				
			}
			
			@Override
			public boolean storeInSession(String key, Object obj) {
				this.storeInSessionKey = key;
				this.storeInSessionObject = obj;
				return true;
			}
			
			@Override
			public void makeResponse(DataObjectSvr d) {
			}
			
			@Override
			public int getFunction() {
				return 0;
			}
		};		
	}

	@Test
	public void doPrimitiveRetrievalCallback_listSizeEqualsResultSize_dontStoreInSession() {
		HashMap<Integer, String> dummy = new HashMap<Integer, String>();
		for(int i = 0; i < ConfigHelper.getRESULT_SIZE(); i++){
			dummy.put(i, "test");
		}
		
		// Start mock
		replayAll();
		request.doPrimitiveRetrievalCallback(dummy);
		verifyAll();
		
		// Verify result
		assertEquals("Check if session key is correct", "list.primitive", request.getSessionValueAsString(""));
		assertNull("Check if session object is null", request.getSessionValue(""));
	}
	
	@Test
	public void doPrimitiveRetrievalCallback_listSizeGreaterResultSize_storeInSession(){
		HashMap<Integer, String> dummy = new HashMap<Integer, String>();
		for(int i = 0; i < ConfigHelper.getRESULT_SIZE() + 5; i++){
			dummy.put(i, "test");
		}
		
		// Start mock
		replayAll();
		request.doPrimitiveRetrievalCallback(dummy);
		verifyAll();
		
		// Verify result
		assertEquals("Check if session key is correct", "list.primitive", request.getSessionValueAsString(""));
		assertSame("Check if session object is the list", dummy, request.getSessionValue(""));		
	}

}
