package com.coronis.logging;

import java.io.*;

/**
 * A logger witch write all logging message to a file
 * 
 * @author antoine
 *
 */
public class FileLogger implements SimpleLogger {
	
	private static final String STRING_SEPARATOR = "\t";
	
	private boolean     _coronisSystemOutOutput = true;
	private PrintWriter _coronisWriter;
	private long        _lastCoronisFrameTime = -1;
	
	/**
	 * Creates a new FileLogger
	 * 
	 * @param filename The file path for the output
	 * @param keepSystemOutOutput true is an output to stdout is wanted
	 * @throws IOException
	 */
	public FileLogger(String filename, boolean keepSystemOutOutput) throws IOException {
		_coronisSystemOutOutput = keepSystemOutOutput;
		_coronisWriter = new PrintWriter(new FileWriter(filename));
	}
	
	public void debug(String message) {
		if(this._coronisSystemOutOutput) {
			System.out.println("DEBUG:: "+ message);
		}
		
		if(this._coronisWriter != null) {
			this._coronisWriter.println("DEBUG:: "+ message);
		}
	}

	public void error(String message) {
		if(_coronisSystemOutOutput) {
			System.out.println("ERROR:: "+ message);
		}
		
		if(_coronisWriter != null) {
			_coronisWriter.println("ERROR:: "+ message);
			_coronisWriter.flush();
		}	
	}

	public void frame(String message, boolean received) {
		String direction = (received) ? "<--" : "-->"; 
		
		long now = System.currentTimeMillis();
		String elapsedTime = (_lastCoronisFrameTime == -1) ? "-" : ""+ (now -_lastCoronisFrameTime);  
		String output = System.currentTimeMillis() + STRING_SEPARATOR
						+ elapsedTime + STRING_SEPARATOR
		                + direction + STRING_SEPARATOR 
		                + message;
		if (_coronisSystemOutOutput)
				System.out.println(output);
		
		if (_coronisWriter != null) {
			_coronisWriter.println(output);
			_coronisWriter.flush();
		}
		_lastCoronisFrameTime = now;
	}

	public void info(String message) {
		if(_coronisSystemOutOutput) {
			System.out.println("INFO:: "+ message);
		}
		
		if(_coronisWriter != null) {
			_coronisWriter.println("INFO:: "+ message);
			_coronisWriter.flush();
		}
	}

	public void log(String message) {
		if(_coronisSystemOutOutput) {
			System.out.println("LOG:: "+ message);
		}
		
		if(_coronisWriter != null) {
			_coronisWriter.println("LOG:: "+ message);
			_coronisWriter.flush();
		}
	}

	public void mainLog(String message) {
		if(_coronisSystemOutOutput) {
			System.out.println("MAIN LOG:: "+ message);
		}
		
		if(_coronisWriter != null) {
			_coronisWriter.println("MAIN LOG:: "+ message);
			_coronisWriter.flush();
		}
	}

	public void warning(String message) {
		if(_coronisSystemOutOutput) {
			System.out.println("WARNING:: "+ message);
		}
		
		if(_coronisWriter != null) {
			_coronisWriter.println("WARNING:: "+ message);
		}
	}
}
