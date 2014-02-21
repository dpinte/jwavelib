/*
 * WaveTherm.java
 *
 * Created on 31 octobre 2007, 17:56
 *
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * This is the WaveTherm class implementation. This version only supports 
 * DALLAS probes, no PT100 or PT1000 !
 *
 * $Date: 2009-07-06 16:39:52 +0200 (Mon, 06 Jul 2009) $
 * $Revision: 96 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/modules/WaveTherm.java $
 */
package com.coronis.modules;

import com.coronis.CoronisLib;
import com.coronis.exception.*;
import com.dipole.libs.*;

import java.util.Calendar;
import java.util.Date;
// J2ME does not have the java.text package
//import java.text.SimpleDateFormat;
import java.io.InterruptedIOException;
import java.io.IOException;

//FIXME : add a check on the ApplicationStatus byte to check the battery bit !

public class WaveTherm extends DataLoggingModule {
	   
    private double _currentValue;
    private long _currentValueTimeStamp;

    public WaveTherm(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
        super(moduleId, wpt, modRepeaters);            
    }

    public WaveTherm(String modname, int freqMinute, int[] moduleId, WaveTalk[] modRepeaters, WavePort wpt) {
        super(modname, freqMinute, moduleId, modRepeaters, wpt);    
    }
    
    protected void setMaxValues() {
    	// Maximum number of data stored in the datalog table for WaveFlow !
        this.MAX_EXTENDED_DATALOG_COUNT = 4500;    	
    	this.MAX_DATA_PER_SINGLEFRAME = 59;
    	this.MAX_DATA_PER_FRAME = 48;
    }
        
    public boolean resetDatalogging() throws CoronisException, IOException, InterruptedIOException  {
        _logger.info("Resetting datalogging");

        int[] answer = waveport.query_ptp_command(
        					Message.resetWaveThermDatalogging(_modid,
        														CoronisLib.getDataloggingFrequency(_frequency)),
        														_repeaters,
        														this.getModuleId());
        
        if (answer[Message.ID_LENGTH + 0] != Message.ACK_RESET_DATALOGGING) {
            throw new CoronisException("Invalid answer to reset datalogging request. Answer code is :" +
            							answer[Message.ID_LENGTH + 0]);
        }
        
        switch (answer[Message.ID_LENGTH + 1] ) {
            case 0x00: _logger.info("Datalogging has been restarted");
                       return true;
            case 0x01: _logger.info("Datalogging has stopped");
                       return false;
            case 0xFF: _logger.info("Syntax error on datalogging reset command");
                       return false; 
            case 0xFE: _logger.info("The value of several parameters (0x80,0x81,0x82,0x83,0x84) are written over the limit.");
                       return false;    
            case 0xFD: _logger.info("The current Date isn't conform with the select mode.");
                       return false;                    
            default:   _logger.info("Invalid return status from datalogging reset request...");
                       return false;  
        }
    }

    private double readTemperature(int[] msg, int atPos) throws MissingDataException {
        int MSB = msg[atPos];
        int LSB = msg[atPos + 1];
        return CoronisLib.parseDallasTemp(MSB, LSB);           

    }
    
    public double getCurrentValue(int[] msg) throws CoronisException{
        if (msg[Message.ID_LENGTH + 0] != Message.ACK_GET_PRB) {
            throw new BadlyFormattedFrameException("Datalogging ACK is not correct. Is" + msg[Message.ID_LENGTH + 0] + " and should be " + Message.ACK_GET_DTG);
        }
        int tempindex = Message.ID_LENGTH +  3;
        _currentValueTimeStamp = System.currentTimeMillis();
    	_currentValue =  this.readTemperature(msg,  tempindex);
    	return _currentValue;
    }

    public String readCurrentValue(int[] msg) throws CoronisException {
        StringBuffer stbf = new StringBuffer();
        stbf.append("Module id : " + Functions.printHumanHex(_modid, false));            
        try {
        	double temperature = this.getCurrentValue(msg);
        	stbf.append("\n\t" + temperature + "�C");
        } catch (MissingDataException e) {
        	stbf.append("\n\t DATA FORMAT ERROR");
        }
        return stbf.toString();
    }

    public String readDatalog(int[] msg) throws CoronisException {
        /*
         * Frame is structured as following : 
         * 	- 1 byte ACK_GET_DTG
         * 	- 1 byte functional mode
         * 	- 1 byte applicative status
         * 	- 48 bytes for probe A
         * 	- 48 bytes for probe B
         * 	- 6 bytes for last measure date and time
         * 	- measure frequency
         */
        StringBuffer stbf = new StringBuffer();
        stbf.append("Module id : " + Functions.printHumanHex(_modid, false));
        if (msg[Message.ID_LENGTH + 0] != Message.ACK_GET_DTG) {
            throw new BadlyFormattedFrameException("Datalogging ACK is not correct. Is" + 
            		                                Functions.printHumanHex(msg[Message.ID_LENGTH + 0], false) + 
            		                                " and should be " + 
            		                                Message.ACK_GET_DTG);
        }

        int timestampIdx = Message.ID_LENGTH + 1 + 1 + 1 + 48 + 48;
        Calendar timestamp = null;
        int frequency = 0;

        timestamp = CoronisLib.parseDateTime(msg, timestampIdx);
        frequency = CoronisLib.parseDataloggingFrequency(msg[msg.length - 1]);


        int dataIdx = Message.ID_LENGTH + 1 + 1 + 1;
        dst = new DataSet(_modid);
        // loop i on 2 * DALLAS_MAX_DATALOGSTORE because each temperature is stored on two bytes
        for (int tempindex = 0; tempindex < (2 * MAX_DATA_PER_FRAME); tempindex += 2) {
            try {

                double temperature = this.readTemperature(msg,  dataIdx + tempindex);
                Date dt = new Date(timestamp.getTime().getTime() - (frequency * tempindex / 2));
                dst.addMeasure(temperature, dt);
                stbf.append("\n\t" + temperature + " at " + dt.toString());
            } catch (MissingDataException e) {
                // means data is not valid anymore --> skip the rest
                _logger.error("Missing data exception while parsing datalog");
                break;
            }
        }

        return stbf.toString();
    }

    public int readExtendedDatalog(int[] msg, boolean isMultiFrame) throws CoronisException {
        /*
         * Frame is structured as following : 
         *  !!! FRAME 1
         * 	- 1 byte ACK_GET_EDT
         * 	- 1 byte frame number
         * 	- 1 byte frame count
         * 	- 6 bytes for last measure date and time
         * 	- 2 bytes first measurement ID sent in the frame
         *  - 2 bytes last measurement ID sent in the frame
         * 	- 2 bytes * number of measurement asked
         * !!! FRAME N
         *
         * Multi frame has three bytes more before the module id that is before the ACK_GET_EDT
         * and when frame number is not 1, there is not timestamp in the frame !*
         *
         * First frame :
         *  |    0    +1        +2         +3          +9          +11      +13
         *  |    6     7         8          9           15         17        19
         *  | ID | ACK | NbFrame | TotFrame | timestamp | firstidx | lastidx | data ...
         * Next frames :
         *  |    0    +1        +2         +3         +5        +7
         *  |    6     7         8          9         11        13
         *  | ID | ACK | NbFrame | TotFrame | firstidx | lastidx | data ....         
         */
        
        int baseIdx = (isMultiFrame) ? (Message.ID_LENGTH + 3) : Message.ID_LENGTH;
        int timestampIdx = baseIdx + 3; 
        int firstDataIdx;
        int lastDataIdx;  
        int dataIdx;
        
        if (msg[baseIdx + 0] != Message.ACK_GET_EDT) {
            throw new BadlyFormattedFrameException("Extended datalogging ACK is not correct. It should be " 
                                 + Functions.printHumanHex(Message.ACK_GET_EDT, false)
                                 + " and got :" + Functions.printHumanHex(msg[Message.ID_LENGTH + 0], false));
        }

        // check that frame number is not 0xFF --> means error p52
        // and check is not > 1
        if (msg[baseIdx + 1] == 0xFF) {
            throw new MissingDataException("Error while asking for extended datalog measurements. Most probably due to a extended datalogging configuration problem or the extended datalogging has not been started.");
        }

        if (dst == null) {
            dst = new DataSet(_modid);
        }        
              
        // depending if it's the first frame or not 
        if (msg[baseIdx + 1] == 1) {             
             // read last data timestamp from first frame
            extdlg_tstamp = CoronisLib.parseDateTime(msg, timestampIdx); 
            dst.setLastMeasureDate(extdlg_tstamp);
            // Indexes are stored on two bytes;
            firstDataIdx = (msg[timestampIdx + 6] << 8) | msg[timestampIdx + 7];
            lastDataIdx = (msg[timestampIdx + 8] << 8) | msg[timestampIdx + 9];  
            // data index 
            dataIdx = baseIdx + 13;
        } else {
            if (extdlg_tstamp == null) throw new CoronisException("Parsing multiframe should be done starting at frame 1");            
            // there is no timestamp on frames with index > 1
            firstDataIdx = (msg[timestampIdx + 0] << 8) | msg[timestampIdx + 1];
            lastDataIdx = (msg[timestampIdx + 2] << 8) | msg[timestampIdx + 3];              
            // data index 
            dataIdx = baseIdx + 7;            
        }
        	       
        latestIdx = (firstDataIdx > latestIdx) ? firstDataIdx : (latestIdx);       

        int dataCount = (firstDataIdx - lastDataIdx) + 1;
        _logger.debug("From " + firstDataIdx + " To " + lastDataIdx + " Read " + dataCount + " data");
               
        long tval = extdlg_tstamp.getTime().getTime();
        
        Date dt;
        double temperature;
        for (int tempindex = 0; tempindex < dataCount; tempindex += 1) {                                          
                try {
                    temperature = this.readTemperature(msg,  dataIdx + (2 * tempindex));
                } catch (MissingDataException e) {
                    // means data is not valid anymore --> skip the rest
                    _logger.warning("Missing data read");
                    temperature = Double.NaN;
                }                
                dt = new Date(tval - correction - (_frequency * dataCounter++));   
                // TODO : this can be improved : no need to check all the dates to store only the most recent one
                // could use only the first data from the file
                if (dt.getTime() > LAST_DATALOG_DATE) {
                    LAST_DATALOG_DATE = dt.getTime();
                    LAST_DATALOG_VALUE = temperature;
                _logger.info("Updating last datalog date to " + dt.toString() + " value is " + temperature);                                 
                }                
                dst.addMeasure(temperature, dt);           
        }

        return lastDataIdx;
    }
}
