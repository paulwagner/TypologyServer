/**
 * Servlet for german language.
 * This servlet just creates a new Request object and put it into the thread pool.
 *
 * The following header information are neccessary to make the request work:
 * 
POST http://localhost:8080/Typology/DE?do=initiatesession HTTP/1.1
User-Agent: Fiddler
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded; charset=UTF-8

data=%7Bdkey%3A%22developerkey%22%2C+uid%3A%22userid%22%2C+version%3A1.2%7D 

POST http://localhost:8080/Typology/DE?do=getprimitive HTTP/1.1
User-Agent: Fiddler
Host: localhost:8080
Content-Length: 68
Content-Type: application/x-www-form-urlencoded; charset=UTF-8
Connection: keep-alive
Cookie: JSESSIONID=570935F92B33E9137E13CC480A4C33DD

data=%7Boffset%3A%22dev%22%2C+uid%3A%22userid%22%2C+version%3A1.2%7D
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
import de.typology.requests.IRequest;
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
		IOHelper.logContext("(ServletDE.init()) Starting up...");
		try {
			IDBConnection db = new DBConnection();
			ThreadContext.setDB(db, LN_DE);
			ThreadContext.setDBLayer(new DBLayer(db), LN_DE);
			ThreadContext.setPrimitiveLayer(new PrimitiveLayer(db), LN_DE);
		} catch (Exception e) {
			IOHelper.logErrorExceptionContext(e);
			throw new ServletException(e);
		}
		IOHelper.logContext("(ServletDE.init()) Startup completed");
	}

	/**
	 * Handle new request (POST): Instantiate new Request and execute it.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		IRequest requestObject;
		try {
			requestObject = new Request(LN_DE, request, response);
		} catch (Exception e) {
			IOHelper.logErrorException(e);
			return;
		}
		ThreadContext.getRequestProcessor().processRequest(requestObject);
	}

}
