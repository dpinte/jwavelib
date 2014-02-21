package com.coronis.logging;

import com.dipole.libs.Constants;
import java.io.*;

public class BasicLogger implements SimpleLogger {
	
	private static final String STRING_SEPARATOR = "\t";
	
	private boolean     _coronisSystemOutOutput = true;
	private PrintWriter _coronisWriter;
	private long        _lastCoronisFrameTime = -1;

	public BasicLogger() {
		super();
	}
	
	public void debug(String message) {	
		if (Constants.DEBUG) {	
			System.out.println("DEBUG:: " + message);
		}
	}
	
	public void setCoronisOutputFile(String filename, boolean keepSystemOutOutput) throws IOException {
		_coronisSystemOutOutput = keepSystemOutOutput;
		_coronisWriter = new PrintWriter(new FileWriter(filename));
	}

	public void error(String message) {
		System.out.println("ERROR:: " + message);
	}

	public void frame(String message, boolean received) {
		if (Constants.DEBUG_CORONIS_FRAMES) {
			String direction = "-->";
			if (received) direction = "<--";
			long now = System.currentTimeMillis();
			String elapsedTime = (_lastCoronisFrameTime == -1) ? "-" : ""+ (now -_lastCoronisFrameTime);  
			String output = System.currentTimeMillis() + STRING_SEPARATOR
							+ elapsedTime + STRING_SEPARATOR
			                + direction + STRING_SEPARATOR 
			                + message;
			if (_coronisSystemOutOutput) System.out.println(output);
			if (_coronisWriter != null) {
				_coronisWriter.println(output);
				_coronisWriter.flush();
			}
			_lastCoronisFrameTime = now;
		}
	}

	public void info(String message) {
		System.out.println("INFO:: " + message);
	}

	public void log(String message) {
		System.out.println("LOG:: " + message);
	}

	public void mainlog(String message) {
		System.out.println("MAIN LOG:: " + message);
	}

	public void warning(String message) {
		System.out.println("WARNING:: " + message);
	}

}
