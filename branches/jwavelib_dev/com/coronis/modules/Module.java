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
 * $Date: 2010-08-13 16:26:29 +0200 (Fri, 13 Aug 2010) $
 * $Revision: 167 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/modules/Module.java $
 */
package com.coronis.modules;

import com.coronis.exception.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.*;

import com.coronis.CoronisLib;
import com.dipole.libs.*;
import com.coronis.logging.Logger;

public abstract class Module {
	
    protected final int[] _modid;
    protected final WaveTalk[] _repeaters;
    protected final String _moduleName;
    protected boolean _isActive;
    protected boolean _isRepeated;
    protected int _rssi = -1;
    protected int _moduleType;
    protected int _wakeUpFrequency;
    protected boolean _isDataloggingModule = false;
    protected int transmission;
    protected int firmware;
    protected WavePort waveport;

    /**
     * Creates a new module 
     * 
     * @param moduleId The module radio address
     */
    public Module(int[] moduleId) {
        this(moduleId, Functions.printHumanHex(moduleId, false));
    }

    /**
     * Creates a new module
     * 
     * @param moduleId The module radio address
     * @param modName The module name
     */
    public Module(int[] moduleId, String modName) {
        this(modName, moduleId, null, null);
    }
        
    /**
     * Creates a new module
     * 
     * @param moduleId The module radio address
     * @param wpt The WavePort
     * @param modRepeaters An array with the repeaters
     */
    public Module(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
        this(Functions.printHumanHex(moduleId, false), moduleId, wpt, modRepeaters);
    }

    /**
     * Creates a new module
     * 
     * @param moduleName The module name
     * @param moduleId The module radio address
     * @param wpt The WavePort
     * @param modRepeaters An array with the repeaters
     */
    public Module(String moduleName, int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
        _modid = moduleId;
        waveport = wpt;
        _repeaters = modRepeaters;
        _isRepeated = (modRepeaters != null && modRepeaters.length > 0) ? true : false;
        _moduleName = moduleName;
        _isActive = true;
    }

    public boolean isRepeated() {
        return _isRepeated;
    }

    public int getRepeaterCount() {
        return (_repeaters != null) ? _repeaters.length : 0;
    }

    /**
     * Get the repeater list
     * 
     * @return An array with all repeaters
     */
    public WaveTalk[] getRepeaters() {
        return this._repeaters;
    }

    public String toString() {
        return "Module is a "+ getClass().getName() 
                + " named "+ this._moduleName
                + " and identification is :"+ Functions.printHumanHex(_modid, false);
    }

    /**
     * Get the name of the module
     * 
     * @return The module name
     */
    public String getName() {
        return _moduleName;
    }

    /**
     * Get a radio address as a string
     * 
     * @return The moduleID
     */
    public String getModuleId() {
        return Functions.printHumanHex(_modid, false);
    }

    /**
     * Get a radio address
     * 
     * @return The moduleID
     */
    public int[] getRadioId() {
        return this._modid;
    } 	
    
    /**
     * 
     * @return the module type
     */
    public abstract int getModuleType();

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
     * 
     * @return true if the module is active
     */
    public boolean isActive() {
        return _isActive;
    }

    public void stop() {
        // call a method that should raise an IOException on the serial port that is probably blocking.
        if (waveport.disconnect() == false) {
            Logger.error("Problem while disconnecting the serial line : report problem to support");
        }
    }

    /**
     * Send a request to get the module type
     * 
     * @return The module type
     * @throws CoronisException
     * @throws IOException
     * @throws InterruptedIOException
     * 
     * FIXME : add a check to see if the type of the module is the right one
     * regarding the class type !
     */
    public String getType() throws CoronisException, IOException, InterruptedIOException {
        return this.getType(this.waveport, this._repeaters);
    }

    /**
     * Send a request to get the module type
     * 
     * @param wpt The WavePort
     * @param repeaters An array with the repeaters
     * @return The module type
     * @throws CoronisException
     * @throws IOException
     * @throws InterruptedIOException
     */
    public String getType(WavePort wpt, WaveTalk[] repeaters) throws CoronisException, IOException, InterruptedIOException {
    	
    	StringBuffer stbf = new StringBuffer();

        this.readType(wpt.query_ptp_command(Message.askType(this._modid),
                                                        repeaters,
                                                        this.getModuleId()));

        stbf.append("Module id : "+ Functions.printHumanHex(_modid, false));
        stbf.append("\n\tType: ");
        stbf.append(CoronisLib.getModuleTypeString(this._moduleType));	
        stbf.append("\n\tRSSI: ");
        stbf.append(Functions.printHumanHex(_rssi, true));
        stbf.append(" - " + CoronisLib.getRSSIPercentage(_rssi, 0x20));
        if (_rssi < 0x07) {
            stbf.append("\n\t WARNING : RSSI value is lower than 0x07. Module should use a repeater.");
        }
        stbf.append("\n\tWakeUp frequency is : ");
        stbf.append(this._wakeUpFrequency);

        return stbf.toString();
    }

    /**
     * Send a request to set the module timestamp
     * 
     * @param timestamp The timestamp
     * @return true if request success
     * @throws CoronisException
     * @throws IOException
     * @throws InterruptedIOException
     */
    public boolean setTime(Calendar timestamp) throws CoronisException, IOException, InterruptedIOException {
            int[] answer = waveport.query_ptp_command(Message.askSetTime(   this._modid,
                                                                            timestamp.get(Calendar.DAY_OF_MONTH),
                                                                            timestamp.get(Calendar.MONTH) + 1,
                                                                            timestamp.get(Calendar.YEAR) - 2000,
                                                                            timestamp.get(Calendar.DAY_OF_WEEK) - 1,
                                                                            timestamp.get(Calendar.HOUR_OF_DAY),
                                                                            timestamp.get(Calendar.MINUTE)),
                                                    this._repeaters,
                                                    this.getModuleId());
            
            // check answer is 0x93
            if (answer[Message.ID_LENGTH + 0] != Message.ACK_SET_DAT) {
                throw new BadlyFormattedFrameException("Trying to set time but got a wrong answer");
            }

            if (answer[Message.ID_LENGTH + 1] == 0x00) {
                // datetime was correctly written into the module
                return true;
            } else if (answer[Message.ID_LENGTH + 1] == 0xFF) {
                // error while writing the datetime to the module
                Logger.debug("Error while writing the date and time to the module");
                return false;
            } else {
                Logger.debug("Strange error : datetime was not set correctly but answer from module does not follow the protocol... ");
                return false;
            }
    }

    /**
     * Send a request to get the module firmware version
     * 
     * @return The firmware version
     * @throws CoronisException
     * @throws IOException
     * @throws InterruptedIOException
     */
    public String getFirmware() throws CoronisException, IOException, InterruptedIOException {
        StringBuffer stbf = new StringBuffer();

        int[] answer = waveport.query_ptp_command(Message.askFirmware(this._modid),
                                                                        this._repeaters,
                                                                        this.getModuleId());

        stbf.append("Module id : " + Functions.printHumanHex(_modid, false));
        stbf.append("\n\tFirmware version:");
        stbf.append(readFirmware(answer));

        return stbf.toString();
    }

    /**
     * Send a request to get the module timestamp
     * 
     * @return The timestamp in a human readable form
     * @throws CoronisException
     * @throws IOException
     * @throws InterruptedIOException
     */
    public String getDateTime() throws CoronisException, IOException, InterruptedIOException {
        StringBuffer stbf = new StringBuffer();

        int[] answer = waveport.query_ptp_command(Message.askDatetime(this._modid),
                                                                        this._repeaters,
                                                                        this.getModuleId());

        stbf.append("Module id : ");
        stbf.append(Functions.printHumanHex(_modid, false));
        stbf.append("\n\t");
        stbf.append("DateTime: ");
        stbf.append(readDatetime(answer));

        return stbf.toString();
    }

    /**
     * 
     * @param msg
     * @return
     */
    protected String readDatetime(int[] msg) {
        // FIXME: check ACK and throws an exception
        Calendar cld = CoronisLib.parseDateTime(msg, Message.ID_LENGTH + 1);

        return cld.getTime().toString();
    }

    /**
     * 
     * @param msg
     * @return
     */
    protected String readFirmware(int[] msg) {
        StringBuffer stbf = new StringBuffer();

        // check ACK
        if (msg[Message.ID_LENGTH + 0] == Message.ACK_GET_FIRM) {
            // check that V is there (Ox56)
            // FIXME : should raise an exception
            if (msg[Message.ID_LENGTH + 1] != 0x56) {
                Logger.error("Missing V char in Firmware");
            }

            this.transmission = (msg[Message.ID_LENGTH + 2] << 8) | 
                                msg[Message.ID_LENGTH + 3];
            this.firmware = (	msg[Message.ID_LENGTH + 4] << 8) | 
                                msg[Message.ID_LENGTH + 5];

            stbf.append("V-");
            stbf.append(Functions.printHumanHex(this.transmission, false));
            stbf.append("-");
            stbf.append(Functions.printHumanHex(this.firmware, false));
        }
        return stbf.toString();
    }

    /**
     * Parse message to read the module type, the RSSI and the WakeUp freq
     * 
     * @param msg
     */
    protected void readType(int[] msg) throws CoronisException {
        if (msg[Message.ID_LENGTH + 0] == Message.ACK_GET_TYPE) {
            this._moduleType = msg[Message.ID_LENGTH + 1];
            this._rssi = msg[Message.ID_LENGTH + 2];
            this._wakeUpFrequency = msg[Message.ID_LENGTH + 3];
        }
        if (this._moduleType != this.getModuleType()) {
        	throw new CoronisException("The type given by the RF module does not match your declared type. Read " + Functions.printHumanHex(this._moduleType, false) + " but should be " + Functions.printHumanHex(this.getModuleType(), false));
        }
    }

    /**
     * Get the RSSI
     * @return The RSSI
     */
    public int getRssi() {
    	if (_rssi == -1) {
    		Logger.warning("Issue a getType call to update the RSSI value");
    	}
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
