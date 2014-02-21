package common;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.lf5.LF5Appender;

public final class Log {
	private static Logger logger = Logger.getLogger("Analyser");
	private static boolean set = false;
	
	private Log(){}
	
	public static void useConsole(final boolean GUI){
		if(!set){
			if(GUI){
				Logger.getRootLogger().addAppender(new LF5Appender());
			} else {
				Logger.getRootLogger().addAppender(new ConsoleAppender(new SimpleLayout()));
			}
			set = true;
		}
		
		//Logger.getRootLogger().setLevel(Level.INFO);
		Logger.getRootLogger().setLevel(Level.ALL);
	}
	
	/*
	public static void usefile(final String filePath){
		if(!set){
			set = true;
		}
		
		Logger.getRootLogger().setLevel(Level.INFO);
	}
	*/
	
	/**
	 * Log information
	 * @param message Object to display
	 */
	public static void info(final Object message){
		if(logger.isInfoEnabled()){
			logger.info(message);
		}
	}
	
	/**
	 * Log Errors
	 * @param message Object to display
	 */
	public static void error(final Object message){
		logger.error(message);
	}
	
	/**
	 * Log fatal errors
	 * @param message Object to display
	 */
	public static void fatal(final Object message){
		logger.fatal(message);
	}
	
	/**
	 * Log trace informations
	 * @param message Object to display
	 */
	public static void trace(final Object message){
		if(logger.isTraceEnabled()){
			logger.trace(message);
		}
	}
	
	/**
	 * Log debug informations
	 * @param message Object to display
	 */
	public static void debug(final Object message){
		if(logger.isDebugEnabled()){
			logger.debug(message);
		}
	}
}
