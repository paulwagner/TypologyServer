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
@Suite.SuiteClasses({ de.typology.tools.AllTestsTools.class, de.typology.db.layer.AllTestsDBLayer.class })
public final class AllTestsSuite {
	
	@BeforeClass
	public static void setUpBeforeClass(){
		System.out.println("Logfile for this test: " + SetupHelperMethods.logfilename);
	}
	
}