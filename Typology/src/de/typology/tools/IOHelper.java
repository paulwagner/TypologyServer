/**
 * Helper class for input and output
 *
 * @author René Pickhardt, Paul Wagner
 *
 */

package de.typology.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javax.servlet.ServletContext;

public class IOHelper {
	private static BufferedWriter logFile = openAppendFile(System
			.getProperty("catalina.base")
			+ System.getProperty("file.separator")
			+ "logs"
			+ System.getProperty("file.separator") + "logfile.log");
	private static BufferedWriter errorLogFile = openAppendFile(System
			.getProperty("catalina.base")
			+ System.getProperty("file.separator")
			+ "logs"
			+ System.getProperty("file.separator") + "error.log");

	/**
	 * faster access to a buffered reader
	 * 
	 * @param filename
	 * @return buffered reader for file input
	 */
	public static BufferedReader openReadFile(String filename) {
		FileInputStream fstream;
		BufferedReader br = null;
		try {
			fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return br;
	}

	/**
	 * Faster access to a bufferedWriter
	 * 
	 * @param filename
	 * @return buffered writer which can be used for output
	 */
	public static BufferedWriter openWriteFile(String filename) {
		FileWriter filestream = null;
		try {
			filestream = new FileWriter(filename);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new BufferedWriter(filestream);
	}

	/**
	 * Faster access to a bufferedWriter that appends to a file
	 * 
	 * @param filename
	 * @return buffered writer which can be used for output
	 */
	public static BufferedWriter openAppendFile(String filename) {
		FileWriter filestream = null;
		try {
			filestream = new FileWriter(filename, true);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new BufferedWriter(filestream);
	}

	/**
	 * function for logging a message to standard log
	 * 
	 * @param out
	 */
	public static void log(String out) {
		try {
			Date dt = new Date();
			logFile.write(dt + " - " + out + "\n");
			logFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * function for logging an error to error log (and also to standard log with
	 * special marking).
	 * 
	 * @param out Manual error message
	 */
	public static void logError(String out) {
		log("!!!!!" + out);
		try {
			Date dt = new Date();
			errorLogFile.write(dt + " - " + out + "\n");
			errorLogFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * function for logging an exception to error log (and also to standard log with
	 * special marking).
	 * 
	 * @param myMsg Manual error message.
	 * @param out Exception to log
	 */
	public static void logErrorException(String myMsg, Throwable out) {
		String s;
		if(myMsg.isEmpty()){
			myMsg = ((Throwable) out).getMessage();
		}
		s = "EXCEPTION OCCURED: " + myMsg + "\n";
		s += stackTraceToString(((Throwable) out).getStackTrace());
		log("!!!!!" + s);
		try {
			Date dt = new Date();
			errorLogFile.write(dt + " - " + s + "\n");
			errorLogFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public static void logErrorException(Throwable out){
		logErrorException(out.getMessage(), out);
	}

	private static String stackTraceToString(StackTraceElement[] stackTrace) {
		String s = "";
		for (StackTraceElement se : stackTrace) {
			s += "      " + se.toString() + "\n";
		}
		return s;
	}

	/**
	 * function for logging a message into servlet log, which will also appear
	 * in standard log. use this method for messages concerning servlets or
	 * server (so you have it together with all tomcat messages)
	 * 
	 * @param out
	 *            String to log in context
	 * @param context
	 *            ServletContext
	 */
	public static void log(String out, ServletContext context) {
		if (context != null) {
			context.log(out);
		}
		log(out);
	}

	/**
	 * function for logging an error to servlet log, wich will also appear in
	 * standard error log. use this method for messages concerning servlets or
	 * server (so you have it together with all tomcat messages).
	 * 
	 * @param out
	 *            Manual error message
	 * @param context
	 *            The servlet context
	 */
	public static void logError(String out, ServletContext context) {
		if (context != null) {
			context.log(out);
		}
		logError(out);
	}

	/**
	 * function for logging an exception to servlet log, wich will also appear
	 * in standard error log. use this method for messages concerning servlets
	 * or server (so you have it together with all tomcat messages).
	 * 
	 * @param myMsg
	 *            Manual error message.
	 *            will be used
	 * @param out
	 *            Exception to log
	 * @param context
	 *            ServletContext
	 */
	public static void logErrorException(String myMsg, Throwable out,
			ServletContext context) {
		if (myMsg.isEmpty()) {
			myMsg = out.getMessage();
		}
		if (context != null) {
			context.log("EXCEPTION OCCURED: " + myMsg, out);
		}
		logErrorException(myMsg, out);
	}
	
	public static void logErrorException(Throwable out, ServletContext context){
		logErrorException(out.getMessage(), out, context);
	}
}