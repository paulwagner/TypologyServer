/**
 * Method test case for RetrievalFactory
 * 
 * @author Paul Wagner
 */
package de.typology.retrieval;

import static de.typology.tools.Resources.LN_MAX;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.typology.requests.IRequest;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IRequest.class})
public class RetrievalFactoryTest {
	
	// MEMBERS
	
	public static RetrievalFactory factory = new RetrievalFactory();
	public static IRequest request;

	// SETUP
	
	@Before
	public void setUp(){
		request = PowerMock.createMock(IRequest.class);
		expect(request.getLang()).andReturn(LN_MAX);
	}	
	
	// TESTS
	
	@Test
	public void getInstanceOfPrimitiveRetrieval_getInstance_InstanceOfPrimitiveRetrieval() {
		assertTrue("Check if instance is of PrimitiveRetrieval", factory.getInstanceOfPrimitiveRetrieval(request) instanceof PrimitiveRetrieval);
	}

	@Test
	public void getInstanceOfRetrieval_getInstance_InstanceOfRetrieval() {
		assertTrue("Check if instance is of Retrieval", factory.getInstanceOfRetrieval(request) instanceof Retrieval);
	}

}
