package com.coronis.logging;

public interface SimpleLogger {
	public void debug(String message);
	public void log(String message);   
	public void info(String message);
	public void error(String message);
	public void warning(String message);
	public void mainlog(String message);
	public void frame(String message, boolean received);
}
