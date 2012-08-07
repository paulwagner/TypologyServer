/**
 * Method test case for Primitive Retrieval.
 * 
 * @author Paul Wagner
 */
package de.typology.retrieval;

import static de.typology.tools.Resources.LN_MAX;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.typology.SetupHelperMethods;
import de.typology.db.layer.PrimitiveLayer;
import de.typology.requests.IRequest;
import de.typology.requests.IRequestProcessor;
import de.typology.retrieval.PrimitiveRetrieval;
import de.typology.threads.ThreadContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ThreadContext.class, PrimitiveLayer.class, IRequestProcessor.class})
public class PrimitiveRetrievalTest {

	// MEMBERS
	
	private static PrimitiveRetrieval retrieval;

	private static HashMap<Integer, String> map = new HashMap<Integer, String>();
	private static PrimitiveLayer layer;
	private static IRequest request; 

	// SETUP
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SetupHelperMethods.initiateContextSupport();
		map.put(1, "Das");
		map.put(2, "ist");
		map.put(3, "hat");
		map.put(4, "kann");
		map.put(5, "nicht");
		map.put(6, "hatschi");
	}
	
	@Before
	public void setUp(){
		// Instantiate test class
		//request = new RequestTestImpl();
		request = PowerMock.createMock(IRequest.class);
		retrieval = new PrimitiveRetrieval(request, LN_MAX);
	}

	// TESTS
	
	@Test
	public void eval_doRetrievalHat_HatHatschi() {
		// Prepare layer mock for testing
		layer = PowerMock.createMock(PrimitiveLayer.class);
		expect(layer.getNodeMap()).andReturn(map); // Prepare layer with our map

		// Mock layer in
		mockStatic(ThreadContext.class);
		expect(ThreadContext.getPrimitiveLayer(LN_MAX)).andReturn(layer);

		// Start mocking
		replayAll();
		retrieval.setSentence(null, "ha");
		retrieval.eval();
		verifyAll();

		// Check result
		HashMap<Integer, String> result = retrieval.getResult();
		String s = "";
		for (Entry<Integer, String> e : result.entrySet()) {
			s += e.getKey() + ":" + e.getValue() + "-";
		}

		assertEquals("Check if result is hat and hatschi", "0:hat-1:hatschi-", s);
	}

	@Test
	public void eval_doRetrievalTe_Empty() {
		// Prepare layer mock for testing
		layer = PowerMock.createMock(PrimitiveLayer.class);
		expect(layer.getNodeMap()).andReturn(map); // Prepare layer with our map

		// Mock layer in
		mockStatic(ThreadContext.class);
		expect(ThreadContext.getPrimitiveLayer(LN_MAX)).andReturn(layer);

		// Start mocking
		replayAll();
		retrieval.setSentence(null, "Te");
		retrieval.eval();
		verifyAll();

		// Check result
		HashMap<Integer, String> result = retrieval.getResult();

		assertTrue("Check if result is empty", result.size() == 0);
	}
	
	@Test
	public void eval_doRetrievalnicht_nicht() {
		// Prepare layer mock for testing
		layer = PowerMock.createMock(PrimitiveLayer.class);
		expect(layer.getNodeMap()).andReturn(map); // Prepare layer with our map

		// Mock layer in
		mockStatic(ThreadContext.class);
		expect(ThreadContext.getPrimitiveLayer(LN_MAX)).andReturn(layer);

		// Start mocking
		replayAll();
		retrieval.setSentence(null, "nicht");
		retrieval.eval();
		verifyAll();

		// Check result
		HashMap<Integer, String> result = retrieval.getResult();
		String s = "";
		for (Entry<Integer, String> e : result.entrySet()) {
			s += e.getKey() + ":" + e.getValue() + "-";
		}

		assertEquals("Check if result is just nicht", "0:nicht-", s);
	}	

	@Test
	public void doResponse_isNotInterrupted_makeCallback(){
		// Expect callback
		request.doPrimitiveRetrievalCallback(null);		
		// Start mocking
		replay(request);
		retrieval.doResponse();
		verify(request);
	}

	@Test
	public void doResponse_isInterrupted_dontMakeCallback(){
		// Interrupt retrieval
		retrieval.interrupt();		
		// Start mocking
		replay(request);
		retrieval.doResponse();
		verify(request);
	}
}
