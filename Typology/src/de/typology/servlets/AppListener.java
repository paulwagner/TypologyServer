/**
 * Application Listener.
 * Use this Listener to execute code on application startup or shutdown.
 * 
 * @author Paul Wagner
 *
 */

package de.typology.servlets;

import static de.typology.tools.Resources.LN_MAX;

import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.typology.db.persistence.IDBConnection;
import de.typology.rdb.persistence.IRDBConnection;
import de.typology.rdb.persistence.MySQLConnection;
import de.typology.threads.ThreadContext;
import de.typology.tools.ConfigHelper;
import de.typology.tools.IOHelper;

public class AppListener implements ServletContextListener {

	/**
	 * Code at application initialization.
	 * 
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		// Initialize log
		IOHelper.initializeTomcatLog();
		// Load config file
		ServletContext sc = arg0.getServletContext();
		String s = null;
		if (sc != null) {
			ThreadContext.setServletContext(sc);
			IOHelper.logContext("(AppListener.contextInitialized()) Initializing Application...");
			s = sc.getInitParameter("configfile");
			if (s != null && !s.isEmpty()) {
				try {
					ConfigHelper.loadConfigFile(s);
				} catch (Exception e) {
					IOHelper.logErrorExceptionContext("WARNING: (AppListener.init()) Unable to load configfile...", e);
				}
			}
			try {
				IRDBConnection rdb = new MySQLConnection();
				rdb.openConnection();
				ThreadContext.setRDB(rdb);
			} catch (ClassNotFoundException e) {
				IOHelper.logErrorExceptionContext("ERROR: (AppListener.init()) Unable to load MySQL connector! Unable to start up...", e);
				throw new NullPointerException("ERROR: (AppListener.init()) Unable to load MySQL connector! Unable to start up...");
			} catch(SQLException e) {
				IOHelper.logErrorExceptionContext("ERROR: (AppListener.init()) Unable to connect to MySQL database! Unable to start up...", e);				
				throw new NullPointerException("ERROR: (AppListener.init()) Unable to connect to MySQL database! Unable to start up...");
			}
		} else {
			IOHelper.logError("(AppListener.contextInitialized) ServletContext couldn't be loaded. Unable to start up...");
			throw new NullPointerException("Unable to load ServletContext");
		}
	}

	/**
	 * Code at application shutdown.
	 * 
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		IOHelper.logContext("(AppListener.contextDestroyed()) Shutdown Application...");
		ThreadContext.getRDB().closeConnection();
		for (int i = 0; i <= LN_MAX; i++) {
			IDBConnection db = ThreadContext.getDB(i);
			if(db != null){
				db.shutdown();
			}
		}
	}

}
