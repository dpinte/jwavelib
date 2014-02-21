/**
 * WaveTank.java
 * 
 * Created on 23 octobre 2008, 09:00
 *
 * Author : Didrik Pinte <dpinte@dipole-consulting.com>
 * Copyright : Dipole Consulting SPRL 2007-2008
 * 
 * This is the WaveTank class implementation. 
 *
 * $Date: 2009-07-06 16:36:05 +0200 (Mon, 06 Jul 2009) $
 * $Revision: 95 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/modules/WaveTank.java $
 */
package com.coronis.modules;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Calendar;
import java.util.Date;

import com.coronis.CoronisLib;
import com.coronis.exception.BadlyFormattedFrameException;
import com.coronis.exception.CoronisException;
import com.coronis.exception.MissingDataException;
import com.dipole.libs.DataSet;
import com.dipole.libs.Functions;

/**
 * @author dpinte
 *
 */
public class WaveTank extends DataLoggingModule {

	private double _currentValue;
	private long _currentValueTimeStamp;

	
	/**
	 * @param moduleId
	 * @param wpt
	 * @param modRepeaters
	 */
	public WaveTank(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
		super(moduleId, wpt, modRepeaters);
        this.setMaxValues();        
	}

	/**
	 * @param modName
	 * @param freqMinute
	 * @param moduleId
	 * @param modRepeaters
	 * @param wpt
	 */
	public WaveTank(String modName, int freqMinute, int[] moduleId,
			WaveTalk[] modRepeaters, WavePort wpt) {
		super(modName, freqMinute, moduleId, modRepeaters, wpt);		
		
	}
	
    protected void setMaxValues() {
    	// Maximum number of data stored in the datalog table for WaveFlow !
        this.MAX_EXTENDED_DATALOG_COUNT = 4500;    	
    	this.MAX_DATA_PER_SINGLEFRAME = 59;
    	this.MAX_DATA_PER_FRAME = 48;    	
    }
    	
    private double readLevel(int[] msg, int atPos) {
        int value = ((msg[atPos] & 0xFF) << 8) ;
        value = value | (msg[atPos + 1] & 0xFF);
        return  value * 1.0 / 0x0FFF;   	
    }

    public double getCurrentValue(int[] msg) throws CoronisException{
    	/*
    	 * Data in the msg has the following structure :
    	 *  - module id / 6 bytes
    	 *  - ACK / 1 byte
    	 *  - operating mode / 1 byte
    	 *  - application status / 1 byte
    	 *  - MSB measurement value / 1 byte
    	 *  - LSB measurement value / 1 byte
    	 */
        if (msg[Message.ID_LENGTH + 0] != Message.ACK_GET_PRB) {
            throw new BadlyFormattedFrameException("Datalogging ACK is not correct. Is" + msg[Message.ID_LENGTH + 0] + " and should be " + Message.ACK_GET_DTG);
        }
        int tempindex = Message.ID_LENGTH +  3;
        _currentValueTimeStamp = System.currentTimeMillis();
    	_currentValue =  this.readLevel(msg,  tempindex);
    	return _currentValue;
    }
    
	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#readCurrentValue(int[])
	 */
    public String readCurrentValue(int[] msg) throws CoronisException {
        StringBuffer stbf = new StringBuffer();
        stbf.append("Module id : " + Functions.printHumanHex(_modid, false));            
        try {
        	double level = this.getCurrentValue(msg);
        	stbf.append("\n\t" + level + "%");
        } catch (MissingDataException e) {
        	stbf.append("\n\t DATA FORMAT ERROR");
        }
        return stbf.toString();
    }

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#readDatalog(int[])
	 */
	public String readDatalog(int[] msg) throws CoronisException {
	       /*
         * Frame is structured as following : 
         * 	- 1 byte ACK_GET_DTG
         * 	- 1 byte functional mode
         * 	- 1 byte applicative status
         * 	- 96 bytes for data
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
        for (int tempindex = 0; tempindex < (2 * this.MAX_DATA_PER_FRAME); tempindex += 2) {
            double level = this.readLevel(msg,  dataIdx + tempindex);
            Date dt = new Date(timestamp.getTime().getTime() - (frequency * tempindex / 2));
            dst.addMeasure(level, dt);
            stbf.append("\n\t" + level + " at " + dt.toString());            
        }

        return stbf.toString();
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#readExtendedDatalog(int[], boolean)
	 */
	public int readExtendedDatalog(int[] msg, boolean isMultiFrame)
			throws CoronisException {
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
        double level;
        for (int tempindex = 0; tempindex < dataCount; tempindex += 1) {                                          
                level = this.readLevel(msg,  dataIdx + (2 * tempindex));                       
                dt = new Date(tval - correction - (_frequency * dataCounter++));   
                // TODO : this can be improved : no need to check all the dates to store only the most recent one
                // could use only the first data from the file
                if (dt.getTime() > LAST_DATALOG_DATE) {
                    _logger.info("Updating last datalog date to " + dt.toString());
                    LAST_DATALOG_DATE = dt.getTime();
                    LAST_DATALOG_VALUE = level;
                }                
                dst.addMeasure(level, dt);           
        }

        return lastDataIdx;
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#resetDatalogging()
	 */
	public boolean resetDatalogging() throws CoronisException, IOException,
			InterruptedIOException {
		
		// FIXME : waiting an anwer from the Coronis support to know if they are a direct restart command such as the one for
		// WaveTherm and WaveFlow's
		
		int[] answer;
		
		// Stop datalogging if needed we don't know if it's enabled or if it was enabled
		if ( (_dataloggingParameterLoaded == false) || ((this._operatingMode & 0x08) != DataLoggingModule.DATALOG_DEACTIVATED)) {
			_logger.debug("Stopping datalogging");
			answer = waveport.query_ptp_command(Message.setDataLoggingParemeters(this._modid, DataLoggingModule.DATALOG_DEACTIVATED, 0x00, 0X00), this._repeaters, this._moduleName);
	        if (! checkParameterAnswer(answer)) {
	        	_logger.error("Stopping datalogging did not work has expected");
	        	return false;
	        }
		} else {
			_logger.debug("Datalogging was not activated");
		}
		
    	// Reset parameters and restart datalogging
		answer = waveport.query_ptp_command(
				Message.setDataLoggingParemeters(this._modid, 
						DataLoggingModule.DATALOG_PEMARNENT_LOOP | DataLoggingModule.DATALOG_TIME_STEPS, 
						CoronisLib.getDataloggingFrequency(this._frequency), 
						Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1), 
				this._repeaters, 
				this._moduleName);
		
        return checkParameterAnswer(answer);     	
	}
	
	private boolean checkParameterAnswer(int[] answer) throws CoronisException{
		boolean succeeded = true;
		// check ACK
		if (answer[Message.ID_LENGTH + 1] == Message.ACK_SET_SENSOR_PARAM) {
			int numberOfParameters = answer[Message.ID_LENGTH + 2];
			for (int i = 0; i < numberOfParameters; i++) {
				// check status of each updated parameter
				int pos = Message.ID_LENGTH + 3 + (numberOfParameters * 2);
				int parameterNumber = answer[pos];
				int parameterWriteStatus = answer[pos+1];
				if (parameterWriteStatus == 0xFF) {
					_logger.error("Error while writing parameter " + parameterNumber + " while resetting datalogging");
					succeeded = false;
				}
			}
		} else throw new CoronisException("No ACK to the set parameter request");	
		return succeeded;
	}

}
