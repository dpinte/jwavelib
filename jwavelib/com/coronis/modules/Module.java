/*
 * Module.java
 *
 * Created on 6 novembre 2007, 17:26
 *
 * Module is the abstract class for all the Coronis modules (WaveTherm and
 * WaveFlow at the moment). It implements default behaviour for all those
 * modules.
 *
 * Every module is linked to a given WavePort, has a name and a measure
 * frequency (we assume that they all do extended datalogging).
 *
 * Modules can be used in Threads because the implement the Runnable interface.
 * We used in threads they call the getDailyData() method. This call fills an
 * instance variable called threadDst that can then be saved using the
 * saveThreadDataSet() method. This call have been extracted from the run()
 * method because it takes some time to be done (nearly 35 seconds to write the
 * file on the flash filesystem of the eWON).
 *
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2007-2008
 *
 * $Date: 2009-07-08 12:40:34 +0200 (Wed, 08 Jul 2009) $
 * $Revision: 98 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/modules/Module.java $
 */
package com.coronis.modules;

import com.coronis.exception.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.*;

import com.coronis.CoronisLib;
import com.dipole.libs.*;
import com.coronis.Config;
import com.coronis.logging.SimpleLogger;

public abstract class Module {

	protected final int[] _modid;

	protected final WaveTalk[] _repeaters;

	protected final String _moduleName;

	protected boolean _isActive;

	protected boolean _isRepeated;

	protected int _rssi = 0x00;

	protected int _moduleType;

	protected int _wakeUpFrequency;

	protected SimpleLogger _logger;
        
    protected boolean _isDataloggingModule = false;

	public WavePort waveport;

	/**
	 * Creates a new module 
	 * @param moduleId The module radio address
	 */
	public Module(int[] moduleId) {
		this(moduleId, Functions.printHumanHex(moduleId, false));
	}

	/**
	 * Creates a new module
	 * @param moduleId The module radio address
	 * @param modName The module name
	 */
	public Module(int[] moduleId, String modName) {
		this(modName, moduleId, null, null);
	}

	/**
	 * Creates a new module
	 * @param moduleId The module radio address
	 * @param wpt The WavePort
	 * @param modRepeaters An array with the repeaters
	 */
	public Module(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
		this(Functions.printHumanHex(moduleId,
				false), moduleId, wpt, modRepeaters);
	}

	/**
	 * Creates a new module
	 * @param moduleName The module name
	 * @param moduleId The module radio address
	 * @param wpt The WavePort
	 * @param modRepeaters An array with the repeaters
	 */
	public Module(String moduleName, int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
            _modid = moduleId;
            waveport = wpt;
            _repeaters = modRepeaters;
            _isRepeated = (modRepeaters != null && modRepeaters.length > 0) ? true
                            : false;
            _moduleName = moduleName;
            _isActive = true;
            try {
                _logger = Config.getLogger();
            } catch (Exception e ) {
                System.err.println(e.toString());
                System.exit(1);
            }
	}

	public boolean isRepeated() {
		return _isRepeated;
	}

	public int getRepeaterCount() {
		return (_repeaters != null) ? _repeaters.length : 0;
	}

	/**
	 * Get the repeater list
	 * @return An array with all repeaters
	 */
	public WaveTalk[] getRepeaters() {
		return this._repeaters;
	}

	public String toString() {
		return "Module is a " + getClass().getName() + " named " + _moduleName
				+ " and identification is :"
				+ Functions.printHumanHex(_modid, false);
	}

	/**
	 * Get the name of the module
	 * @return The module name
	 */
	public String getName() {
		return _moduleName;
	}

	/**
	 * Get a radio address as a string
	 * @return The moduleID
	 */
	public String getModuleId() {
		return Functions.printHumanHex(_modid, false);
	}
	
	/**
	 * Get a radio address
	 * @return The moduleID
	 */
    public int[] getRadioId() {
        return this._modid;
    } 	

    /**
     * Desactivate the module
     */
	public void deActivate() {
		_isActive = false;
	}

	/**
	 * Activate the module
	 */
	public void activate() {
		_isActive = true;
	}

	/**
	 * Check if the module is active
	 * @return true if the module is active
	 */
	public boolean isActive() {
		return _isActive;
	}

	public void stop() {
		// call a method that should raise an IOException on the serial port that is probably blocking.
		if (waveport.disconnect() == false) {
			_logger
					.error("Problem while disconnecting the serial line : report problem to support");
		}
	}

	/**
	 * Send a request to get the module type
	 * @return The module type
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public String getType() throws CoronisException, IOException,
			InterruptedIOException {
		return this.getType(waveport, _repeaters);
	}

	/**
	 * Send a request to get the module type
	 * @param wpt The WavePort
	 * @param repeaters An array with the repeaters
	 * @return The module type
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public String getType(WavePort wpt, WaveTalk[] repeaters)
			throws CoronisException, IOException, InterruptedIOException {
            int[] answer;
            //if (this instanceof WaveTalk) {
	    //	answer = wpt.query_ptp_service(msg.askType(), repeaters, this
	    //			.getModuleId());                
            //} else {
		answer = wpt.query_ptp_command(Message.askType(this._modid), repeaters, this
				.getModuleId());                
            //}
		
		return readType(answer);
	}

	/**
	 * Send a request to set the module timestamp
	 * @param timestamp The timestamp
	 * @return true if request success
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public boolean setTime(Calendar timestamp) throws CoronisException,
			IOException, InterruptedIOException {
		int[] answer = waveport.query_ptp_command(Message.askSetTime(this._modid, timestamp
				.get(Calendar.DAY_OF_MONTH), timestamp.get(Calendar.MONTH) + 1,
				timestamp.get(Calendar.YEAR) - 2000, timestamp
						.get(Calendar.DAY_OF_WEEK) - 1, timestamp
						.get(Calendar.HOUR_OF_DAY), timestamp
						.get(Calendar.MINUTE)), _repeaters, this.getModuleId());
		// check answer is 0x93
		if (answer[Message.ID_LENGTH + 0] != Message.ACK_SET_DAT) {
			throw new BadlyFormattedFrameException(
					"Trying to set time but got a wrong answer");
		}
		if (answer[Message.ID_LENGTH + 1] == 0x00) {
			// datetime was correctly written into the module
			return true;
		} else if (answer[Message.ID_LENGTH + 1] == 0xFF) {
			// error while writing the datetime to the module
			_logger.debug("Error while writing the date and time to the module");
			return false;
		} else {
			_logger.debug("Strange error : datetime was not set correctly but answer from module does not follow the protocol... ");
			return false;
		}
	}

	/**
	 * Send a request to get the module firmware version
	 * @return The firmware version
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public String getFirmware() throws CoronisException, IOException,
			InterruptedIOException {
		int[] answer = waveport.query_ptp_command(Message.askFirmware(this._modid), _repeaters, this
				.getModuleId());
		return readFirmware(answer);
	}

	/**
	 * Send a request to get the module timestamp
	 * @return The timestamp in a human readable form
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public String getDateTime() throws CoronisException, IOException,
			InterruptedIOException {
		int[] answer = waveport.query_ptp_command(Message.askDatetime(this._modid), _repeaters, this
				.getModuleId());
		return readDatetime(answer);
	}

	public String readDatetime(int[] msg) {
		StringBuffer stbf = new StringBuffer();
		stbf.append("Module id : " + Functions.printHumanHex(_modid, false));
		// check ACK
		Calendar cld;
		try {
			cld = CoronisLib.parseDateTime(msg, Message.ID_LENGTH + 1);
			//SimpleDateFormat dfmt = new SimpleDateFormat("yyyyy.MMMMM.dd HH:MM");
			stbf.append(cld.getTime().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stbf.toString();
	}

	public String readFirmware(int[] msg) {
		StringBuffer stbf = new StringBuffer();
		stbf.append("Module id : " + Functions.printHumanHex(_modid, false));
		// check ACK
		if (msg[Message.ID_LENGTH + 0] == Message.ACK_GET_FIRM) {
			// check that V is there (Ox56)
			if (msg[Message.ID_LENGTH + 1] != 0x56) {
				_logger.error("Missing V char in Firmware");
			}
			stbf.append("\n\tFirmware version:");
			stbf.append("\n\t-----------------");
			stbf.append("\n\t\tTranmission:"
					+ Functions.printHumanHex(msg[Message.ID_LENGTH + 2], true)
					+ Functions
							.printHumanHex(msg[Message.ID_LENGTH + 3], false));
			stbf.append("\n\t\tFirm : "
					+ Functions.printHumanHex(msg[Message.ID_LENGTH + 4], true)
					+ Functions
							.printHumanHex(msg[Message.ID_LENGTH + 5], false));
		}
		return stbf.toString();
	}

	public String readType(int[] msg) {
		StringBuffer stbf = new StringBuffer();
		stbf.append("Module id : " + Functions.printHumanHex(_modid, false));

		// check ACK
		if (msg[Message.ID_LENGTH + 0] == Message.ACK_GET_TYPE) {
			_moduleType = msg[Message.ID_LENGTH + 1];
			_rssi = msg[Message.ID_LENGTH + 2];
			_wakeUpFrequency = msg[Message.ID_LENGTH + 3];
			stbf.append("\n\tModule is a "
					+ CoronisLib.getModuleTypeString(_moduleType));
			stbf.append("\n\tRSSI is : " + Functions.printHumanHex(_rssi, true)
					+ " - " + CoronisLib.getRSSIPercentage(_rssi, 0x20));
			if (_rssi < 0x07) {
				stbf
						.append("\n\t WARNING : RSSI value is lower than 0x07. Module should use a repeater.");
			}
			stbf.append("\n\tWakeUp frequency is : " + _wakeUpFrequency);
		}
		return stbf.toString();
	}
    
	/**
	 * Get the RSSI
	 * @return The RSSI
	 */
    public int getRssi() {
        return _rssi;
    }
    
    /**
     * Check if the module has datalogging capabilities
     * @return true it's a datalogging module
     */
    public boolean isDataloggingModule() {
        return _isDataloggingModule;
    }

}
