/**
 * Request class that handles threads for logging, retrieval and dbupdate.
 * This class is instanciated directly in the doPost() Method(s).
 * So we don't have to worry about request threads on our own, just about this request class.
 * 
 * So everything concerning a request starts from here, not from the servlet!
 * 
 * TODO: Define Request as interface or abstract class definieren.
 * By that you can take other connectors like jWebSocket easily.
 *
 * @author Paul Wagner
 *
 */
package de.typology.requests;

import static de.typology.tools.Resources.SC_ERR;
import static de.typology.tools.Resources.SC_RET_INTERRUPTED;
import static de.typology.tools.Resources.SC_RET_TIMEOUT;
import static de.typology.tools.Resources.CS_TYPE_SESSION;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import de.typology.db.retrieval.IRetrieval;
import de.typology.db.retrieval.PrimitiveRetrieval;
import de.typology.threads.ThreadContext;
import de.typology.tools.ConfigHelper;
import de.typology.tools.IOHelper;

public class Request {

	public final int LANG;

	public String UID = new String("");
	public int TYPE;
	public Object[] config;

	private final HttpServletRequest requestObj;
	private final HttpServletResponse responseObj;

	// private Gson jsonHandler = new Gson();
	private Gson jsonHandler = ThreadContext.jsonHandler;

	public Request(int LANG, HttpServletRequest request,
			HttpServletResponse response) {
		this.LANG = LANG;
		this.requestObj = request;
		this.responseObj = response;
	}

	public void execute() {
		String s = requestObj.getParameter("json");
		if (s == null) {
			makeResponse(null, false, SC_ERR,
					"Unable to read data parameter. Have you declared it?");
			return;
		}

		DataObject data = jsonHandler.fromJson(s, DataObject.class);
		if (data.words == null || data.offset == null) {
			makeResponse(null, false, SC_ERR,
					"Unable to parse data parameter. Refer to wiki.typology.de for the API");
			return;
		}

		this.TYPE = data.type;
		this.UID = data.uid;
		// Session handling: Create session if necessary and handle session id
		// If we got a uid, its stored in the session, because we don't have to
		// send it again and again then
		HttpSession session = requestObj.getSession();
		String tmp = (String) session.getAttribute("uid");
		if (this.UID == null || this.UID.isEmpty()) {
			// No uid given can mean we don't have one or its already in session
			if (tmp != null && !tmp.isEmpty()) {
				this.UID = tmp;
			} else {
				this.UID = session.getId();
				this.TYPE = CS_TYPE_SESSION;
			}
		} else {
			// If we have one we store it in session unless that has been done
			// before
			if (tmp != null && !tmp.isEmpty()) {
				session.setAttribute("uid", this.UID);
			} else {
				this.UID = tmp;
			}
		}

		// Config Handling:
		// Configuration can be overridden by further requests (not like uid)
		if (data.config == null) {
			this.config = (Object[]) session.getAttribute("config");
		} else {
			session.setAttribute("config", data.config);
			this.config = data.config;
		}

		// TODO when implemented, gzip support should be set (or disabled) in
		// the config

		// TODO start new threads for logging and db maintenance

		// TODO Here should just Retrieval be instanciated, when implemented!
		IRetrieval ret = new PrimitiveRetrieval(this, LANG);
		ret.setSentence(data.words, data.offset);

		// Make a new thread for retrieval
		// By default, the new thread runs through, and that's also the time the
		// request takes.
		// But if necessary you can also interrupt the request.
		Thread t = new Thread(ret);
		t.start();
		try {
			t.join(ConfigHelper.getRET_TIMEOUT() * 1000L);
		} catch (InterruptedException e) {
			IOHelper.logError(
					"WARNING: (Request.execute()) Retrieval has been interrupted. No response.",
					ThreadContext.getServletContext());
			makeResponse(null, false, SC_RET_INTERRUPTED, "");
		}
		if (t.isAlive()) {
			ret.interrupt();
			IOHelper.logError(
					"WARNING: (Request.execute()) Retrieval has been timeouted. No response.",
					ThreadContext.getServletContext());
			makeResponse(null, false, SC_RET_TIMEOUT, "");
		}
	}

	/**
	 * This is a callback method for making a response. It is supposed to be
	 * called from within retrieval class.
	 * 
	 * @param list
	 *            a result list
	 * @param primitive
	 *            retrieval exited with primitive result
	 */
	public void makeResponse(HashMap<Integer, String> list, boolean primitive) {
		makeResponse(new DataObject(list, primitive));
	}

	/**
	 * This is a callback method for making a response. It is supposed to be
	 * called from within retrieval class.
	 * 
	 * @param list
	 *            a result list
	 * @param primitive
	 *            retrieval exited with primitive result
	 * @param status
	 *            Error status code
	 * @param msg
	 *            Error message
	 */
	public void makeResponse(HashMap<Integer, String> list, boolean primitive,
			int status, String msg) {
		makeResponse(new DataObject(list, primitive, "", status, msg));
	}

	private void makeResponse(DataObject d) {
		// JSON bauen
		String data = jsonHandler.toJson(d);
		// TODO evt. Header setzen, noch weitere infos?
		try {
			PrintWriter out = responseObj.getWriter();
			out.write(data);
			out.flush();
			out.close();
		} catch (IOException e) {
			IOHelper.logErrorException(e, ThreadContext.getServletContext());
		}
	}

}
