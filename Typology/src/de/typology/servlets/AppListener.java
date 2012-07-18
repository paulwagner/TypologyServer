/**
 * Application Listener.
 * Use this Listener to execute code on application startup or shutdown.
 * 
 * @author Paul Wagner
 *
 */

package de.typology.servlets;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.typology.db.persistence.IDBConnection;
import de.typology.threads.ThreadContext;
import de.typology.tools.ConfigHelper;
import de.typology.tools.IOHelper;
import static de.typology.tools.Resources.LN_MAX;

public class AppListener implements ServletContextListener {

	/**
	 * Code at application initialization.
	 * 
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		// Load config file
		ServletContext sc = arg0.getServletContext();
		String s = null;
		if (sc != null) {
			IOHelper.log("(AppListener.contextInitialized()) Initializing Application...", sc);
			s = sc.getInitParameter("configfile");
			if (s != null && !s.isEmpty()) {
				ConfigHelper.loadConfigFile(s);
			}
			ThreadContext.setServletContext(sc);
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
		ServletContext sc = arg0.getServletContext();
		IOHelper.log("(AppListener.contextDestroyed()) Shutdown Application...", sc);
		for (int i = 0; i <= LN_MAX; i++) {
			IDBConnection db = ThreadContext.getDB(i);
			if(db != null){
				db.closeConnection();
			}
		}
	}

}
