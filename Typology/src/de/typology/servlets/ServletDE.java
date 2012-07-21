/**
 * Servlet for german language.
 * This servlet just creates a new Request object and put it into the thread pool.
 *
 * The following header information are neccessary to make the request work:
 *  POST http://localhost:8080/Typology/DE HTTP/1.1
 *	User-Agent: Fiddler
 *	Host: localhost:8080
 *	Content-Length: 140
 *
 *	json=%7B%22words%22+%3A+%5B%22Das%22%5D%2C+%22offset%22+%3A+%22geh%22%2C+%22use_primitive%22+%3A+true+%2C+%22force_primitive%22+%3A+false%7D 
 *
 *
 * @author Paul Wagner
 */
package de.typology.servlets;

import static de.typology.tools.Resources.LN_DE;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.typology.db.layer.DBLayer;
import de.typology.db.layer.PrimitiveLayer;
import de.typology.db.persistence.DBConnection;
import de.typology.db.persistence.IDBConnection;
import de.typology.requests.Request;
import de.typology.threads.ThreadContext;
import de.typology.tools.IOHelper;

/**
 * Servlet implementation class ServletDE
 */
public class ServletDE extends HttpServlet {

	private static final long serialVersionUID = -1786398776050004829L;
	public HttpServletRequest req; 

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServletDE() {
		super();
	}

	/**
	 * Initialize german database and put it to thread context
	 * 
	 * @see Servlet#init()
	 */
	public void init() throws ServletException {
		// Load German Databases and Layers into ThreadContext
		try {
			IDBConnection db = new DBConnection(); 
			ThreadContext.setDB(db, LN_DE);
			ThreadContext.setDBLayer(new DBLayer(db), LN_DE);
			ThreadContext.setPrimitiveLayer(new PrimitiveLayer(db), LN_DE);
		} catch (Exception e) {
			IOHelper.logErrorExceptionContext(e);
		}
	}

	/**
	 * Handle new request (POST):
	 * Instanciate new Request and execute it.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		IOHelper.logContext("(ServletDE.doPost()) New german request");
		Request r = new Request(LN_DE, request, response);
		r.execute();
		IOHelper.logContext("(ServletDE.doPost()) Finished german request");
	}	

}
