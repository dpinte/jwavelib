/**
 * 
 */
package com.coronis.logging;

/**
 * A logger witch write all logging message to stdout
 * 
 * @author antoine
 *
 */
public class StdLogger implements SimpleLogger {
	private static final String STRING_SEPARATOR = "\t";
	private long lastCoronisFrameTime = -1;
	
	public void debug(String message) {
		System.out.println("DEGUG:: "+ message);
	}

	public void error(String message) {
		System.out.println("ERROR:: "+ message);
	}

	public void frame(String message, boolean received) {
		String direction = (received) ? "<--" : "-->"; 
		
		long now = System.currentTimeMillis();
		String elapsedTime = (this.lastCoronisFrameTime == -1) 
								? "-" : ""+ (now - this.lastCoronisFrameTime);  
		String output = System.currentTimeMillis() + STRING_SEPARATOR
						+ elapsedTime + STRING_SEPARATOR
		                + direction + STRING_SEPARATOR 
		                + message;

		System.out.println(output);
		
		this.lastCoronisFrameTime = now;
	}

	public void info(String message) {
		System.out.println("INFO:: "+ message);
	}

	public void log(String message) {
		System.out.println("LOG:: "+ message);
	}

	public void mainLog(String message) {
		System.out.println("MAINLOG:: "+ message);
	}

	public void warning(String message) {
		System.out.println("WARNING:: "+ message);
	}

}
