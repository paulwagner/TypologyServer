package de.typology.db.persistence;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({de.typology.db.persistence.impl.ImpermanentDBConnectionTest.class, de.typology.db.persistence.DBConnectionTest.class})
public class AllTestsPersistence {

}
