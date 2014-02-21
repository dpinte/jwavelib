
package com.coronis.logging;

/**
 * Logging Helper class.
 * <p>
 * By default, the logger is a StdLogger.<br>
 * To change it, use setLogger(SimpleLogger);
 * 
 * @author antoine
 */
public class Logger {
	public static boolean DEBUG = false;
	public static boolean DEBUG_CORONIS_FRAMES = false;
	
	private static SimpleLogger logger = new StdLogger();
	
	/**
	 * Set an other logger than the default
	 * 
	 * @param newLogger The logger to use
	 */
	public static void setLogger(SimpleLogger newLogger) {
		logger = newLogger;
	}
	
	/**
	 * Send a 'DEBUG::' message
	 * 
	 * @param message The message to log
	 */
	public static void debug(String message) {
		if(DEBUG)
			logger.debug(message);
	}
	
	/**
	 * Send a 'ERROR::' message
	 * 
	 * @param message The message to log
	 */
	public static void error(String message) {
		logger.error(message);
	}
	
	
	/**
	 * send a frame to the logging output
	 * 
	 * @param message The frame as a string
	 * @param received true if the frame has ben received
	 */
	public static void frame(String message, boolean received) {
		if(DEBUG_CORONIS_FRAMES)
			logger.frame(message, received);
	}
	
	/**
	 * Send a 'INFO::' message
	 * 
	 * @param message The message to log
	 */
	public static void info(String message) {
		logger.info(message);
	}
	
	/**
	 * SEND a 'LOG::' message
	 * 
	 * @param message The message to log
	 */
	public static void log(String message) {
		logger.log(message);
	}
	
	/**
	 * Send a 'MAINLOG::' message
	 * 
	 * @param message The message to log
	 */
	public static void mainLog(String message) {
		logger.mainLog(message);
	}
	
	/**
	 * Send a 'WARNING::' message
	 * 
	 * @param message The message to log
	 */
	public static void warning(String message) {
		logger.warning(message);
	}
	
}
