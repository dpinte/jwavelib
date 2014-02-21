package com.coronis;

//import java.io.File;
//import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.coronis.exception.ConfigException;
import com.coronis.logging.Logger;
import com.coronis.modules.Module;
import com.coronis.modules.WaveFlow;
import com.coronis.modules.WavePort;
import com.coronis.modules.WaveSense4_20;
import com.coronis.modules.WaveSense5V;
import com.coronis.modules.WaveTalk;
import com.coronis.modules.WaveTank;
import com.coronis.modules.WaveThermDalas;
import com.coronis.modules.WaveThermPT100;

import com.dipole.libs.Constants;

/**
 * The Config class is a class used to easily manage a hashtable
 * of configuration parameters. It is mainly used to the lack of 
 * Property class support in J2ME implementation of the eWON. It is also used 
 * to store and share global attributes for the application like the logger.
 * 
 * <p>
 * The format of a configuration file is the following :<br>
 * -----------------------------------------------------<br>
 * <p>
 * com.port=com0<br>
 * dailycall.hour=0<br>
 * dailycall.min=5<br>
 * sitename=sample<br>
 * modules=wth1,wth2,wfl3<br>
 * repeaters=wtk1,wtk2<br>
 * wth1.type=wavetherm<br>
 * wth1.frequency=15<br>
 * wth1.id=051906302D69<br>
 * wth1.repeaters=wtk2,wtk1<br>
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
	public static final String COMPORT = "com.port";
	public static final String SITENAME = "sitename";
	public static final String DAILYCALLHOUR = "dailycall.hour";
	public static final String DAILYCALLMIN = "dailycall.min";
	public static final String MODULES = "modules";
	public static final String REPEATERS = "repeaters";
	public static final String MODULE_TYPE = ".type";
	public static final String MODULE_FREQ = ".frequency";
	public static final String MODULE_ID = ".id";
	public static final String MODULE_REP = ".repeaters";
	public static final String FTPHOST = "ftp.host";
	public static final String FTPLOGIN = "ftp.login";
	public static final String FTPPZD = "ftp.pzd";
	public static final String FTPPORT = "ftp.port";
	public static final String KEEPDAYS = "keep.days";
	public static final String DEBUG_ALL = "debug.all";
	public static final String DEBUG_CORONIS = "debug.coronis";
	public static final String HARDWARE_KEY = "hardware.key";
	public static final String QUERY_RETRIES = "query.retries";

	public static char SEPARATOR_CHAR = ',';
	public static int DEFAULT_RETRIES = 3;
	public static int DEFAULT_KEEP_DAYS = 5;
	public static String DEFAULT_COM_PORT = "com0";

	private Hashtable config;
	private boolean ignoreConfigError = false;
	
	public Config() {
		this.config = new Hashtable();
	}
	
	public Config(boolean ignoreConfigError) {
		this();
		this.ignoreConfigError = ignoreConfigError;
	}
	
	/**
	 * Load configuration from a property file. 
	 * <p>
	 * ! Because J2ME for eWON has no java.util.Properties,
	 * we read a file formated as a property file into a
	 * HashTable
	 * 
	 * @param configPath Path of the configuration file
	 * @throws ConfigException
	 * @throws FileNotFoundException 
	 * @throws IOException
	 */
        /*
	public void load(String configPath) throws ConfigException, IOException {
                // FIXME : FileInputStram not present in J2ME
		FileInputStream fis = new FileInputStream(new File(configPath));
		this.load(fis);
	}
	*/
        
	/**
	 * Load configuration from an input stream.
	 * <p>
	 * ! Because J2ME for eWON has no java.util.Properties,
	 * we read a file formated as a property file into a
	 * HashTable
	 * 
	 * @param inputStream
	 * @throws ConfigException 
	 * @throws IOException 
	 */
	public void load(InputStream inputStream) throws ConfigException, IOException {
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
			throw new IOException("IOException while reading configuration file :"+ e.toString());
		}
		
		// Parse the Vector and load config into the hashtable
		for (Enumeration configlines = lines.elements(); configlines.hasMoreElements();) {
			String cline = (String) configlines.nextElement();
                        
			// Pass the lines with # that are comments
			if (cline.startsWith("#") || cline.equals("") || cline.startsWith("\n") || cline.startsWith("\r"))
				continue;
			
			if (cline.indexOf("=") == -1) {
				if(this.ignoreConfigError) {
					Logger.error("Bad format in config file on line : "+ cline +" => skip it");
				} else {
					throw new ConfigException("Bad format in config file on line : "+ cline);
				}
			}
			
			// Split the line on the "="
			String[] tokens = this.split(cline, '=');
			
			if (tokens.length > 2) {
				if(this.ignoreConfigError) {
					Logger.error("Bad format in config file on line : -"+ cline);
				} else {
					throw new ConfigException("Bad format in config file on line : -"+ cline);
				}
			} else {
				this.config.put(tokens[0].toLowerCase(), tokens[1]);
			}
		}
		
		this.setDefault();
		this.checkDebug();
	}
	
	/**
	 * Set default values if missing in the config file
	 */
	private void setDefault() {
		if(!this.hasKey(FTPPORT)) {
			this.setValue(FTPPORT, Constants.DEFAULT_FTP_PORT);
		}
		if(!this.hasKey(COMPORT)) {
			this.setValue(COMPORT, Config.DEFAULT_COM_PORT);
		}
	}
	
	/**
	 * Check if debug is activated for the general debugging (debug.all) or the
	 * coronis debugging (debug.coronis).
	 */
	public void checkDebug() {
		// if configuration variables are set to 1 --> debugging is activated.
		try {
			if (this.getIntValue(DEBUG_ALL) == 1) {
				Logger.DEBUG = true;
				Logger.mainLog("Debugging is activated");
			}
		} catch (ConfigException e) { 
			// do nothing
		}

		try {
			if (this.getIntValue(DEBUG_CORONIS) == 1) {
				Logger.DEBUG_CORONIS_FRAMES = true;
				Logger.mainLog("Coronis frame debugging activated");
			}
		} catch (ConfigException e) {
			// do nothing
		}
	}
	
	/**
	 * Create a module with all informations found in the config
	 * @param moduleName The name of the module to create
	 * @param wpt The WavePort connected to the module
	 * @return The module
	 * @throws ConfigException Throws ConfigException if a some informations are missing
	 */
	public Module getModule(String moduleName, WavePort wpt) throws ConfigException {
		Module mod = null;
		
		try {
			/* get all module info from config */
			int freq = this.getIntValue(moduleName + MODULE_FREQ);
			int []modID = CoronisLib.moduleIdFromString(this.getStringValue(moduleName + MODULE_ID));
			String modType = this.getStringValue(moduleName + MODULE_TYPE).toLowerCase();
			WaveTalk[] repeaters = null;
			
			/* Check for repeaters */
			if(this.hasKey(moduleName + MODULE_REP)) {
				String[] repLst = this.split(this.getStringValue(moduleName + MODULE_REP), ',');
				
				/* if repLst has not been loaded, create them */
				if(repLst != null) {
					repeaters = new WaveTalk[repLst.length];
					
					for(int i = 0; i < repLst.length; i++) {
						try {
							repeaters[i] = this.getRepeater(repLst[i]);
						} catch (ConfigException e) {
							if(this.ignoreConfigError) {
								Logger.error("Invalid repeater: "+ e.getMessage() +" => skip it");
							} else {
								throw new ConfigException("Invalid repeater: "+ e.getMessage());
							}
						}
					}	
				}
			}
			
			/* create the module */
			if(modType.equals("waveflow")) {
				mod = new WaveFlow(moduleName, freq, modID, repeaters, wpt);
			} else if(modType.equals("wavetherm")) {
				mod = new WaveThermDalas(moduleName, freq, modID, repeaters, wpt);
			} else if(modType.equals("wavethermPT100") || modType.equals("wavethermPT100")) {
				mod = new WaveThermPT100(moduleName, freq, modID, repeaters, wpt);
			} else if(modType.equals("wavetank")) {
				mod = new WaveTank(moduleName, freq, modID, repeaters, wpt);
			} else if(modType.equals("wavesense4-20")) {
				mod = new WaveSense4_20(moduleName, freq, modID, repeaters, wpt);
			} else if(modType.equals("wavesense5v")) {
				mod = new WaveSense5V(moduleName, freq, modID, repeaters, wpt);
			}
			
		} catch (ConfigException e) {
			if(this.ignoreConfigError) {
				Logger.error(e.getMessage() +" for module "+ moduleName);
			} else {
				throw new ConfigException(e.getMessage() +" for module "+ moduleName);
			}
		}

		return mod;
	}
	
	/**
	 * Returns an array of module based on the content of the config.
	 * 
	 * If the config contains a "modules" key, the list of modules 
	 * name in this key is loaded.
	 * Otherwise, the full config is parsed searching for modules.
	 *  
	 * @param wpt : a WavePort instance
	 * @return Module[] : an array of module
	 * @throws ConfigException
	 */
	public Module[] getModuleArray(WavePort wpt) throws ConfigException {
		Module[] modules = null;
		String[] mods = null;
		
		if (this.hasKey(MODULES)) {
			// stores only lowercase modoule name because the key using modules
			// name are stored in lowercase !
			try {
				mods = this.getStringArrayValue(MODULES, SEPARATOR_CHAR);
			} catch (ConfigException e) {
				// should never append, so just log
				Logger.error(e.getMessage());
			}

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
		
		modules = new Module[mods.length];
		for (int i = 0; i < mods.length; i++) {
			modules[i] = this.getModule(mods[i], wpt);
		}
		
		return modules;
	}
	
	/**
	 * Create a WaveTalk with all informations found in the config
	 * @param moduleName
	 * @return The repeater
	 */
	public WaveTalk getRepeater(String moduleName) throws ConfigException {
		WaveTalk rep = null;
		
		try {
			int[] modID = CoronisLib.moduleIdFromString(this.getStringValue(moduleName + MODULE_ID));
			String modType = this.getStringValue(moduleName + MODULE_TYPE).toLowerCase();
			
			rep = new WaveTalk(modID, moduleName);
		} catch (ConfigException e) {
			if(this.ignoreConfigError) {
				Logger.error(e.getMessage() +" for module "+ moduleName +" => Skip it");
			} else {
				throw new ConfigException(e.getMessage() +" for module "+ moduleName);
			}
		}
		
		return rep;
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
		WaveTalk[] repeaters = null;
		String[] rep = null;
		
		if (this.hasKey(REPEATERS)) {
			try {
				rep = this.getStringArrayValue(REPEATERS, SEPARATOR_CHAR);
			} catch (ConfigException e) {
				// should never append, so just log
				Logger.error(e.getMessage());
			}
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
			rep = new String[repCount];
			// then add their name to the mods array
			for (int i = 0; i < rep.length; i++) {
				rep[i] = (String) repVector.elementAt(i);
			}
		}
		
		repeaters = new WaveTalk[rep.length];
		for (int i = 0; i < rep.length; i++) {
			repeaters[i] = getRepeater(rep[i]);
		}
		
		return repeaters;
	}
	
	/**
	 * Check if The keyword exist in the config
	 * @param key The keyword to check
	 * @return true if keyword has been found
	 */
	public boolean hasKey(String key) {
		return this.config.containsKey(key);
	}
	
	/**
	 * Get a value as a String
	 * 
	 * @param key The config keyword
	 * @return The config value
	 * @throws ConfigException Throw a ConfigException if the keyword is not found
	 */
	public String getStringValue(String key) throws ConfigException {
		if (config.containsKey(key)) {
			return (String) this.config.get(key);
		} else {
			throw new ConfigException("Key '"+ key +"' not found");
		}
	}
	
	/**
	 * Get a value as an Integer
	 * 
	 * @param key The config keyword
	 * @return The config value
	 * @throws ConfigException Throw a ConfigException if the keyword is not found
	 */
	public int getIntValue(String key) throws ConfigException {
		return Integer.parseInt(this.getStringValue(key));
	}
	
	/**
	 * Get a value as a String Array
	 * @param key The config keyword
	 * @param separatorChar The separator
	 * @return The config values
	 * @throws ConfigException Throw a ConfigException if the keyword is not found
	 */
	public String[] getStringArrayValue(String key, char separatorChar) throws ConfigException {
		return this.split(this.getStringValue(key), separatorChar);
	}
	
	/**
	 * Record a String value in the config file
	 * 
	 * @param key The keyword associate to the value
	 * @param value The value
	 */
	public void setValue(String key, String value) {
		this.config.put(key, value);
	}
	
	/**
	 * Record an Integer value the config file
	 * 
	 * @param key The keyword associate to the value
	 * @param value The value
	 */
	public void setValue(String key, int value) {
		this.setValue(key, Integer.toString(value));
	}
	
	/**
	 * Record a String array value in the config file
	 * 
	 * @param key The keyword associate to the value
	 * @param values The value
	 */
	public void setValue(String key, String[] values) {
		StringBuffer buff = new StringBuffer();
		
		for(int i = 0; i < values.length; i++) {
			buff.append(values[i]);
			
			if (i < values.length -1)
				buff.append(',');
		}
		
		this.setValue(key, buff.toString());
	}
	
	/**
	 * J2ME implementation of a split method for String object
	 * @param str, a String object
	 * @param separatorChar, a separator char
	 * @return String[]
	 */
	private String[] split(String str, char separatorChar) {
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
	
	public String toString() {
		return config.toString();
	}
}
