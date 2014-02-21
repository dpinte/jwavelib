package com.coronis.logging;

public interface SimpleLogger {
	/**
	 * Send a 'DEBUG::' message
	 * 
	 * @param message The message to log
	 */
	public void debug(String message);
	
	/**
	 * Send a 'LOG::' message
	 * 
	 * @param message The message to log
	 */
	public void log(String message);
	
	/**
	 * Send a 'INFO::' message
	 * 
	 * @param message The message to log
	 */
	public void info(String message);
	
	/**
	 * Send a 'ERROR::' message
	 * 
	 * @param message The message to log
	 */
	public void error(String message);
	
	/**
	 * Send a 'WARNING::' message
	 * 
	 * @param message The message to log
	 */
	public void warning(String message);
	
	/**
	 * Send a 'MAIN LOG::' message
	 * 
	 * @param message The message to log
	 */
	public void mainLog(String message);
	
	/**
	 * Send a 'FRAME::' message
	 * 
	 * @param message The message to log
	 */
	public void frame(String message, boolean received);
}
