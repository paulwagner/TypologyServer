package de.typology.requests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AbstractRequestCallbackTest.class, RequestTest.class, RequestProcessorTest.class, RequestToolsTest.class })
public class AllTestsRequests {

}
