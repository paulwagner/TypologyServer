/**
 * Main test suite that starts test suite for every package
 * 
 * @author Paul Wagner
 */
package de.typology;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ de.typology.tools.AllTestsTools.class, de.typology.db.layer.AllTestsDBLayer.class, de.typology.db.persistence.AllTestsPersistence.class, 
					  de.typology.db.retrieval.AllTestsRetrieval.class, de.typology.requests.AllTestsRequests.class})
public final class AllTestsSuite {
	
	@BeforeClass
	public static void setUpBeforeClass(){
		System.out.println("Starting test suite");
		System.out.println("Logfile for this test-suite: " + SetupHelperMethods.logfilename);
	}
	
}