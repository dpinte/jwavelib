/*
 * Config.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * TODO : this class should be moved outside of the JWaveLib Core !
 *
 * Config class implements a basic property file reader with some specific 
 * methods related to the reading of configuration a Coronis network.
 * 
 
 * 
 * $Date: 2009-07-08 12:40:34 +0200 (Wed, 08 Jul 2009) $
 * $Revision: 98 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/Config.java $
 */
package com.coronis;

import com.coronis.exception.ConfigException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import com.coronis.logging.*;
import com.coronis.modules.*;
import com.dipole.libs.Constants;

/**
 * <p>
 * The Config class is a class used to easily manage a hashtable
 * of configuration parameters. It is mainly used to the lack of 
 * Property class support in J2ME implementation of the eWON. It is also used 
 * to store and share global attributes for the application like the logger.
 * 
 * <p>
 * The format of a configuration file is the following :<br>
 * -----------------------------------------------------<br>
 * <p>
 * dailycall.hour=0<br>
 * dailycall.min=5<br>
 * sitename=testsite<br>
 * modules=wth1,wth2,wfl3<br>
 * repeaters=wtk1,wtk2<br>
 * wth1.type=wavetherm<br>
 * wth1.frequency=15<br>
 * wth1.id=051906302D69<br>
 * wth2.type=wavetherm<br>
 * wth2.frequency=15<br>
 * wth2.id=0519063028B5<br>
 * wfl3.id=051606304A44<br>
 * wfl3.type=waveflow<br>
 * wfl3.frequency=15<br>
 * wtk1.id=051606304A44<br>
 * wtk1.type=wavetalk<br>
 * wtk2.id=051606304A44<br>
 * wtk2.type=wavetalk<br>
 * #wfl4.id=051606304A42<br>
 * #wfl4.type=waveflow<br>
 * #wfl4.frequency=15<br>
 * ftp.host=10.0.0.2<br>
 * ftp.login=login<br>
 * ftp.pzd=password<br>
 * ftp.port=21<br>
 * keep.days=5<br>
 * debug.coronis=1<br>
 * debug.all=1<br>
 *
 * <p>
 * -----------------------------------------------------<br>
 * <p>
 * Comment lines starts with a #. 
 * 
 * <p>
 * A split method has been added in order to be able to split java String. This
 * J2ME version does not have an implementation for String.split().
 * 
 * <p>
 * Copyright : Dipole Consulting SPRL 2008
 
 * @author Didrik Pinte <dpinte@dipole-consulting.com>
 */
public class Config {

	public static String SITENAME = "sitename";
	public static String DAILYCALLHOUR = "dailycall.hour";
	public static String DAILYCALLMIN = "dailycall.min";
	public static String MODULES = "modules";
	public static String REPEATERS = "repeaters";
	public static String MODULE_TYPE = ".type";
	public static String MODULE_FREQ = ".frequency";
	public static String MODULE_ID = ".id";
	public static String MODULE_REP = ".repeaters";
	public static String FTPHOST = "ftp.host";
	public static String FTPLOGIN = "ftp.login";
	public static String FTPPZD = "ftp.pzd";
	public static String FTPPORT = "ftp.port";
	public static String KEEPDAYS = "keep.days";
	public static String DEBUG_ALL = "debug.all";
	public static String DEBUG_CORONIS = "debug.coronis";
	public static String HARDWARE_KEY = "hardware.key";

	public static int DEFAULT_KEEP_DAYS = 5;
	private static SimpleLogger logger;

	private Hashtable config;

	/**
	 * Returns a com.coronis.logging.SimpleLogger interface if existing, else
	 * throws an Exception
	 * 
	 * TODO : should throw a more specific exception
	 * 
	 * @return com.coronis.logging.SimpleLogger a logger interface
	 * @throws Exception when no logger initialized using the setLogger method
	 */
	public static SimpleLogger getLogger() throws Exception {
		if (logger == null)
			throw new Exception("No logger initialized");
		return logger;
	}
	
	/**
	 * Sets the logger used by this config object
	 * 
	 * @param log SimpleLogger interface instance
	 */
	public static void setLogger(SimpleLogger log) {
		logger = log;
	}

	/**
	 * Default constructor for the Config class.
	 */
	public Config() {
		config = new Hashtable();
	}
	
	/**
	 * Load configuration from a property file. ! Because J2ME for eWON has no
	 * java.util.Properties, we read a file formated as a property file into a
	 * HashTable
	 * 
	 * @param inputStream
	 */
	public void load(InputStream inputStream) {
		// Load the file into a Vector of Strings
		InputStream istream = inputStream;
		StringBuffer buf = new StringBuffer();
		Vector lines = new Vector();
		int c;
		try {
			while ((c = istream.read()) != -1) {
				char ch = (char) c;
				if (ch == '\n' || ch == '\r') {
					lines.addElement(buf.toString());
					buf.delete(0, buf.length());
				} else
					buf.append(ch);
			}
			if (buf.length() > 0)
				lines.addElement(buf.toString());
		} catch (IOException e) {
			logger.error("CONFIG :: IOException while reading configuration file :"
							+ e.toString());
		}
		// Parse the Vector and load config into the hashtable
		for (Enumeration configlines = lines.elements(); configlines
				.hasMoreElements();) {
			String cline = (String) configlines.nextElement();
			// Pass the lines with # that are comments
			if (cline.startsWith("#"))
				continue;
			if (cline.indexOf("=") == -1)
				continue;
			// Split the line on the "="
			String[] tokens = Config.split(cline, '=');
			if (tokens.length > 2) {
				// TODO : raise error
				logger.error("Bad format in config file on line : -" + cline
						+ "-. Skipping it !");
				continue;
			} else {
				config.put(tokens[0].toLowerCase(), tokens[1]);
			}
		}
		checkDebug();
	}
	
	public String toString() {
		return config.toString();
	}
	
	/**
	 * Check if debug is activated for the general debugging (debug.all) or the
	 * coronis debugging (debug.coronis).
	 */
	public void checkDebug() {
		// if configuration variables are set to 1 --> debugging is activated.
		if (config.containsKey("debug.all")) {
			Constants.DEBUG = (Integer.parseInt((String) config.get(DEBUG_ALL)) == 1);
			if (Constants.DEBUG == true)
				logger.mainlog("Full debugging is activated");
		}
		if (config.containsKey("debug.coronis")) {
			Constants.DEBUG_CORONIS_FRAMES = (Integer.parseInt((String) config
					.get(DEBUG_CORONIS)) == 1);
			if (Constants.DEBUG_CORONIS_FRAMES == true)
				logger.mainlog("Coronis frame debugging activated");
		}
	}
	
	/**
	 * Load a WaveTalk object using the repeater name.
	 * @param repeaterName : the name of the repeater in the configuration file
	 * @return com.coronis.modules.WaveTalk
	 * @throws ConfigException
	 */
	public WaveTalk getRepeater(String repeaterName) throws ConfigException {
		// Check if config is ok for the given repeater name
		if (!config.containsKey(repeaterName + MODULE_TYPE))
			throw new ConfigException("Missing " + MODULE_TYPE + " for "
					+ repeaterName + " in configuration file");
		if (!config.containsKey(repeaterName + MODULE_ID))
			throw new ConfigException("Missing " + MODULE_ID + " for "
					+ repeaterName + " in configuration file");
		int[] repeaterId = CoronisLib.moduleIdFromString((String) config
				.get(repeaterName + MODULE_ID));
		return new WaveTalk(repeaterId, repeaterName);
	}
	
	/**
	 * Returns a Module object using a module name, a waveport and a list of available
	 * repeaters
	 * 
	 * @param moduleName : the name of the module in the config file
	 * @param wpt : a WavePort object
	 * @param repeaters : the list of available repeaters in the configuration
	 * @return com.coronis.modules.Module
	 * @throws ConfigException
	 */
	public Module getModule(String moduleName, WavePort wpt,
			WaveTalk[] repeaters) throws ConfigException {
		// Check if config is ok for the given module name
		if (!config.containsKey(moduleName + MODULE_TYPE))
			throw new ConfigException("Missing " + MODULE_TYPE + " for "
					+ moduleName + " in configuration file");
		if (!config.containsKey(moduleName + MODULE_FREQ))
			throw new ConfigException("Missing " + MODULE_FREQ + " for "
					+ moduleName + " in configuration file");
		if (!config.containsKey(moduleName + MODULE_ID))
			throw new ConfigException("Missing " + MODULE_ID + " for "
					+ moduleName + " in configuration file");

		// Load module type, frequency and identificator
		String mtype = ((String) config.get(moduleName + MODULE_TYPE))
				.toLowerCase();
		int freq = Integer.parseInt((String) config.get(moduleName
				+ MODULE_FREQ));
		int[] modid = CoronisLib.moduleIdFromString((String) config
				.get(moduleName + MODULE_ID));

		WaveTalk[] moduleRepeaters = null;
		if (config.containsKey(moduleName + MODULE_REP)) {
			
			if (repeaters == null) {
				throw new ConfigException("No repeaters available in configuration but module " + moduleName + " uses repeaters ...");
			}
			// module has some repeaters configured
			String repStr = (String) config.get(moduleName + MODULE_REP);
			String[] repStrArray = Config.split(repStr, ',');
			if (repStrArray.length > CoronisLib.MAX_REPEATERS) {
				logger.warning("Too much repeaters configured for module "
						+ moduleName + ". Maximum " + CoronisLib.MAX_REPEATERS
						+ " repeaters are authorized ");
			}
			// initialize the moduleRepeaters array with a maximum of
			// MAX_REPEATERS.
			moduleRepeaters = new WaveTalk[(repStrArray.length > CoronisLib.MAX_REPEATERS) ? CoronisLib.MAX_REPEATERS
					: repStrArray.length];

			// loop on the valid repeaters in the splitted string
			for (int i = 0; i < moduleRepeaters.length; i++) {
				// find the reference to the repeaters list
				// SimpleLogger.debug(repStrArray[i]);
				boolean found = false;
				for (int j = 0; j < repeaters.length; j++) {
					// SimpleLogger.debug(repeaters[j].getName());
					if (repStrArray[i].equals(repeaters[j].getName())) {
						moduleRepeaters[i] = repeaters[j];
						found = true;
						break;
					}
				}
				if (found == false)
					throw new ConfigException("Repeater " + repStrArray[i]
							+ " is not defined in configuration file.");
			}
		}

		// Create new module from configuration
		Module mod = null;
		// introspection is not available in J2ME environemnts
		if (mtype.startsWith("wavetherm")) {
			mod = new WaveTherm(moduleName, freq, modid, moduleRepeaters, wpt);
		} else if (mtype.startsWith("waveflow")) {
			mod = new WaveFlow(moduleName, freq, modid, moduleRepeaters, wpt);
		} else if (mtype.startsWith("wavetank")) {
			mod = new WaveTank(moduleName, freq, modid, moduleRepeaters, wpt);
		}
		return mod;
	}
	
	/**
	 * Returns an array with the repeaters defined in the config.
	 * 
	 * If config contains the key repeaters, the list of repeaters 
	 * declared in this key is loaded. Otherwise all the config is 
	 * parsed trying to find all the available wavetalk modules
	 * 
	 * @return WaveTalk[]
	 * @throws ConfigException
	 */
	public WaveTalk[] getRepeaterArray() throws ConfigException {
		String[] repeaters;
		if (config.containsKey(REPEATERS)) {
			String repStr = (String) config.get(REPEATERS);
			// stores only lowercase module name because the key using modules
			// name are stored in lowercase !
			repeaters = Config.split(repStr.toLowerCase(), ',');
		} else {
			// parse all the file to get the module.type
			// thus for each key finishing with .type, add it to mods and
			// first parse and store modules to a dynamic structure
			Vector repVector = new Vector();
			int repCount = 0;
			for (Enumeration e = config.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				if (key.endsWith(MODULE_TYPE)) {
					if (((String) config.get(key)) == "wavetalk") {
						repVector.addElement(key.substring(0, key
								.indexOf(MODULE_TYPE)));
						repCount++;
					}
				}
			}
			repeaters = new String[repCount];
			// then add their name to the mods array
			for (int i = 0; i < repeaters.length; i++) {
				repeaters[i] = (String) repVector.elementAt(i);
			}
		}
		WaveTalk[] repeaterArray = new WaveTalk[repeaters.length];
		for (int i = 0; i < repeaters.length; i++) {
			repeaterArray[i] = getRepeater(repeaters[i]);
		}
		logger.debug(repeaterArray.length + " repeaters loaded");
		return repeaterArray;
	}
	
	/**
	 * Returns an array of module based on the content of the config.
	 * 
	 * If the config contains a "modules" key, the list of modules 
	 * name in this key is loaded.
	 * Otherwise, the full config is parsed searching for modules.
	 *  
	 * @param wpt : a WavePort instance
	 * @param repeaters : an array listing the available repeaters
	 * @return Module[] : an array of module
	 * @throws ConfigException
	 */
	public Module[] getModuleArray(WavePort wpt, WaveTalk[] repeaters)
			throws ConfigException {
		String[] mods;
		if (config.containsKey(MODULES)) {
			String modules = (String) config.get(MODULES);
			// stores only lowercase modoule name because the key using modules
			// name are stored in lowercase !
			mods = Config.split(modules.toLowerCase(), ',');

		} else {
			// parse all the file to get the module.type
			// thus for each key finishing with .type, add it to mods and
			// first parse and store modules to a dynamic structure
			Vector modsVector = new Vector();
			int modCount = 0;
			for (Enumeration e = config.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				if (key.endsWith(MODULE_TYPE)) {
					modsVector.addElement(key.substring(0, key
							.indexOf(MODULE_TYPE)));
					modCount++;
				}
			}
			mods = new String[modCount];
			// then add their name to the mods array
			for (int i = 0; i < mods.length; i++) {
				mods[i] = (String) modsVector.elementAt(i);
			}
		}
		Module[] modules = new Module[mods.length];
		for (int i = 0; i < mods.length; i++) {
			modules[i] = getModule(mods[i], wpt, repeaters);
		}
		return modules;
	}
	
	/**
	 * Returns the daily call hour from config
	 * 
	 * @return the daily call's hour
	 * @throws ConfigException
	 */
	public int getDailyCallHour() throws ConfigException {
		if (!config.containsKey(DAILYCALLHOUR))
			throw new ConfigException("Missing " + DAILYCALLHOUR
					+ " in configuration file");
		return Integer.parseInt((String) config.get(DAILYCALLHOUR));
	}
	
	/**
	 * Returns minute of the hour for the daily call
	 * 
	 * @return the minute of the hour for the daily call
	 * @throws ConfigException
	 */
	public int getDailyCallMinute() throws ConfigException {
		if (!config.containsKey(DAILYCALLMIN))
			throw new ConfigException("Missing " + DAILYCALLMIN
					+ " in configuration file");
		return Integer.parseInt((String) config.get(DAILYCALLMIN));
	}
	
	/**
	 * Returns the maximum number of data storage days.
	 * Default value is 5 days.
	 * 
	 * @return the number of storage days (defaults to 5)
	 * @throws ConfigException
	 */
	public int getDaysToKeepFiles() throws ConfigException {
		if (!config.containsKey(KEEPDAYS))
			return DEFAULT_KEEP_DAYS;
		return Integer.parseInt((String) config.get(KEEPDAYS));
	}
	
	/**
	 * Returns the site name defined in config
	 * 
	 * @return a String containing the site name
	 * @throws ConfigException
	 */
	public String getSiteName() throws ConfigException {
		if (!config.containsKey(SITENAME))
			throw new ConfigException("Missing " + SITENAME
					+ " in configuration file");
		return (String) config.get(SITENAME);
	}
	
	/**
	 * Test if has ftp host in config file
	 * @return True if an ftp host is provided
	 */
	public boolean hasFtpHost() {
		return config.containsKey(FTPHOST);
	}
	
	/**
	 * Returns the ftp host from config file
	 * @return Ftp host as String
	 * @throws ConfigException
	 */
	public String getFtpHost() throws ConfigException {
		if (!config.containsKey(FTPHOST))
			throw new ConfigException("Missing " + FTPHOST
					+ " in configuration file");
		return (String) config.get(FTPHOST);
	}
	
	/**
	 * Returns the ftp login provided in config file
	 * @return Ftp login as String
	 * @throws ConfigException
	 */
	public String getFtpLogin() throws ConfigException {
		if (!config.containsKey(FTPLOGIN))
			throw new ConfigException("Missing " + FTPLOGIN
					+ " in configuration file");
		return (String) config.get(FTPLOGIN);
	}
	
	/**
	 * Returns the ftp password provided in config file 
	 * @return Ftp password as String
	 * @throws ConfigException
	 */
	public String getFtpPassword() throws ConfigException {
		if (!config.containsKey(FTPPZD))
			throw new ConfigException("Missing " + FTPPZD
					+ " in configuration file");
		return (String) config.get(FTPPZD);
	}

	/**
	 * Returns the ftp port, 22 as default
	 * @return String ftp port
	 * @throws ConfigException
	 */
	public String getFtpPort() throws ConfigException {
		if (!config.containsKey(FTPPORT)) {
			logger.log("No ftp port defined. Using default port: "
					+ Constants.DEFAULT_FTP_PORT);
			return Constants.DEFAULT_FTP_PORT;
		}
		return (String) config.get(FTPPORT);
	}

	/**
	 * Return the hardware key provided in the config file
	 * @return Hardware key as String
	 */
	public String getHardwareKey() {
		if (!config.containsKey(HARDWARE_KEY)) {
			logger.log("Missing " + HARDWARE_KEY + " in configuration file");
			return "";
		}
		return (String) config.get(HARDWARE_KEY);
	}

	/**
	 * J2ME implementation of a split method for String object
	 * @param str, a String object
	 * @param separatorChar, a separator char
	 * @return String[]
	 */
	private static String[] split(String str, char separatorChar) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return null;
		}
		Vector list = new Vector();
		int i = 0;
		int start = 0;
		boolean match = false;
		while (i < len) {
			if (str.charAt(i) == separatorChar) {
				if (match) {
					list.addElement(str.substring(start, i).trim());
					match = false;
				}
				start = ++i;
				continue;
			}
			match = true;
			i++;
		}
		if (match) {
			list.addElement(str.substring(start, i).trim());
		}
		String[] arr = new String[list.size()];
		list.copyInto(arr);
		return arr;
	}
	
	/**
	 * Simple get on the config keys
	 * @param key as String
	 * @return Object
	 */
	public Object get(String key) {
		if (config.containsKey(key))
			return config.get(key);
		else
			return null;
	}
	
	/**
	 * Set key-value in config 
	 * @param key as String
	 * @param value as String
	 */
	public void set(String key, String value) {
		config.put(key, value);
	}
}
