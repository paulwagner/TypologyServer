package de.typology.threads;

import static de.typology.tools.Resources.LN_MAX;

import javax.servlet.ServletContext;

import com.google.gson.Gson;

import de.typology.db.layer.IDBLayer;
import de.typology.db.persistence.IDBConnection;

/**
 * Context class, provides static access to threaded classes. In order to use
 * connectors besides servlets (eg jWebSocket) we implement our own context, and
 * don't use the ServletContext
 * 
 * @author Paul Wagner
 */
public class ThreadContext {

	// PROPERTIES

	/**
	 * Global array of DB objects. DBConnection is immutable and references
	 * cannot be changed on runtime, so it should be thread safe...
	 */
	private static final IDBConnection[] dbs = new IDBConnection[LN_MAX + 1];
	private static final IDBLayer[] dbLayers = new IDBLayer[LN_MAX + 1];
	private static final IDBLayer[] primitiveLayers = new IDBLayer[LN_MAX + 1];

	/**
	 * Global jsonHandler.
	 * 
	 * TODO: I used to instanciate a jsonHandler for every request, but it seems
	 * that Gson stores an instance of each thread and I got memory leak
	 * warnings on server shutdown. So either one jsonHandler has to be
	 * sufficient (I don't even know if it works with multiple threads) or we
	 * have to get rid of it.
	 * EDIT: Tomcat 7.x should fix the problem
	 * (http://wiki.apache.org/tomcat/MemoryLeakProtection) but I still got this
	 * error (pw).
	 */
	public static final Gson jsonHandler = new Gson();

	/**
	 * ServletContext
	 * 
	 * In threads, the ServletContext is mainly used for logging.
	 */
	private static ServletContext servletContext = null; 
	
	// GETTERS AND SETTERS

	/**
	 * Setter for global db objects. Reference can only be changed when db is
	 * null (for initialization) to make this thread safe.
	 * 
	 * @param db
	 *            the db layer
	 * @param lang
	 *            the used language
	 */
	public static boolean setDBLayer(IDBLayer db, final int LANG) {
		if (LANG > LN_MAX) {
			return false;
		}
		if (ThreadContext.dbLayers[LANG] == null) {
			ThreadContext.dbLayers[LANG] = db;
			return true;
		}
		return false;
	}

	/**
	 * @return db the db layer
	 */
	public static IDBLayer getDbLayer(final int LANG) {
		if (LANG > LN_MAX) {
			return null;
		}
		return ThreadContext.dbLayers[LANG];
	}

	/**
	 * @param db
	 *            the primitive db layer
	 * @param lang
	 *            the used language
	 */
	public static boolean setPrimitiveLayer(IDBLayer db, final int LANG) {
		if (LANG > LN_MAX) {
			return false;
		}
		if (ThreadContext.primitiveLayers[LANG] == null) {
			ThreadContext.primitiveLayers[LANG] = db;
			return true;
		}
		return false;
	}

	/**
	 * @return db the primitive layer
	 */
	public static IDBLayer getPrimitiveLayer(final int LANG) {
		if (LANG > LN_MAX) {
			return null;
		}
		return ThreadContext.primitiveLayers[LANG];
	}

	/**
	 * @param db
	 *            the primitive db layer
	 * @param lang
	 *            the used language
	 */
	public static boolean setDB(IDBConnection db, final int LANG) {
		if (LANG > LN_MAX) {
			return false;
		}
		if (ThreadContext.dbs[LANG] == null) {
			ThreadContext.dbs[LANG] = db;
			return true;
		}
		return false;
	}

	/**
	 * @return db the primitive layer
	 */
	public static IDBConnection getDB(final int LANG) {
		if (LANG > LN_MAX) {
			return null;
		}
		return ThreadContext.dbs[LANG];
	}
	
	
	/**
	 * Get ServletContext
	 * 
	 * @return servletContext
	 */
	public static ServletContext getServletContext(){
		return servletContext;
	}
	
	/**
	 * Set ServletContext.
	 * This is only allowed if it wasn't set before.
	 * 
	 * @param sc The ServletContext
	 */
	public static void setServletContext(ServletContext sc){
		if(servletContext == null){
			servletContext = sc;
		}	
	}

}
