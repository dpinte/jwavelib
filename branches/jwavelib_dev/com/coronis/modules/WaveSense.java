/**
 * 
 */
package com.coronis.modules;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Calendar;

import com.coronis.CoronisLib;
import com.coronis.exception.BadlyFormattedFrameException;
import com.coronis.exception.CoronisException;
import com.coronis.exception.MissingDataException;
import com.coronis.logging.Logger;

/**
 * Class to represent a WaveSense module
 */
public abstract class WaveSense extends DataLoggingModule {

	protected int module_type = 0x00;
	
	/**
	 * Creates a new WaveSense module
	 * 
     * @param moduleId The radio address of the module
	 * @param wpt The WavePort use with this module
	 * @param modRepeaters The repeaters to reach the module
	 */
	public WaveSense(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
		super(moduleId, wpt, modRepeaters);
	}

	/**
	 * Creates a new WaveSense module
	 * 
     * @param modname The module name
     * @param freqMinute The datalogging frequency of the module (in minutes)
     * @param moduleId The radio address of the module
     * @param modRepeaters The repeaters to reach the module
     * @param wpt The WavePort use with this module
	 */
	public WaveSense(String modName, int freqMinute, int[] moduleId,WaveTalk[] modRepeaters, WavePort wpt) {
		super(modName, freqMinute, moduleId, modRepeaters, wpt);
	}

	/**
	 * Parse the value
	 * 
	 * @param MSB MSB byte
	 * @param LSB LSB byte
	 * @return The value
	 */
	protected abstract double parseValue(int MSB, int LSB);
	
	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#readAdvancedDataLog(int[], boolean)
	 */
	protected void readAdvancedDataLog(int[] msg, boolean isMultiFrame) throws CoronisException {
		 /*
         * Frame Format:
         * ==============
         * 
         * - Single frame:
         * ----------------
         * - The frame as the same structure than the first Multi-Frame
         * 
         * - Multi frames :
         * -----------------
         * - Only the first frame have a date field
         * - 3 more byte before ID
         *
         * First frame :
         *  |    0    +1        +2         +3     +9         +11      +13
         *  |    6     7         8          9     15         17        19
         *  | ID | ACK | NbFrame | TotFrame | date | firstIdx | lastIdx | data |
         * 
         * Next frames :
         *  |    0    +1        +2         +3         +5        +7
         *  |    6     7         8          9         11        13
         *  | ID | ACK | NbFrame | TotFrame | firstIdx | lastIdx | data |
         *  
         * - ID			: module ID						6 bytes
         * - ACK		: applicative command ACK		1 byte
         * - NbFrame	: frame number					1 byte
         * - TotFrame	: total frame					1 byte
         * - date		: last logged measure date		6 bytes
         * - firstIdx	: first received index			2 bytes
         * - lastIdx	: last received index			2 bytes
         * - data		: levels						2 bytes per values
         */
        
        int baseIdx = (isMultiFrame) ? (Message.ID_LENGTH + 3) : Message.ID_LENGTH;
        int firstDataIdx;
        int lastDataIdx;  
        int dataIdx;
        
        // check if received 0x86
        if (msg[baseIdx + 0] != Message.ACK_GET_EDT) {
            throw new BadlyFormattedFrameException(
                    "Extended datalogging ACK is not correct. Got :"
            		+ Integer.toHexString(msg[baseIdx + 0])
            		+" but need "
            		+ Integer.toHexString(Message.ACK_GET_EDT));
        }

        // check that frame number is not 0xFF --> means error p52
        // and check is not > 1
        if (msg[baseIdx + 1] == 0xFF) {
            throw new MissingDataException("Error while asking for extended datalog measurements. Most probably due to a extended datalogging configuration problem or the extended datalogging has not been started.");
        }   
              
        // depending if it's the first frame or not 
        if (msg[baseIdx + 1] == 1) {             
        	// read last data timestamp from first frame
        	this.dataSet.setLastDataLogDate(CoronisLib.parseDateTime(msg, baseIdx + 3).getTime().getTime());
        	Logger.debug("Last Datalog measure date: "+ this.dataSet.getLastDataLogDate());
        	
            // Indexes are stored on two bytes;
            firstDataIdx = (msg[baseIdx + 9] << 8) | msg[baseIdx + 11];
            lastDataIdx = (msg[baseIdx + 12] << 8) | msg[baseIdx + 13];
            
            // data index 
            dataIdx = baseIdx + 13;
        } else {
            if (this.dataSet.getLastDataLogDate() == 0)
            	throw new CoronisException("Parsing multiframe should be done starting at frame 1");    
            
            // there is no timestamp on frames with index > 1
            firstDataIdx = (msg[baseIdx + 3] << 8) | msg[baseIdx + 4];
            lastDataIdx = (msg[baseIdx + 5] << 8) | msg[baseIdx + 6]; 
            
            // data index 
            dataIdx = baseIdx + 7;            
        }      

        /* store the last data index reported in the frame to 
         * keep index position
         */
        this.dataSet.setLastReceivedIndex(lastDataIdx);
        
        int dataCount = (firstDataIdx - lastDataIdx) + 1;
        Logger.debug(	"From " + firstDataIdx + " To " + lastDataIdx
        				+ " Read " + dataCount + " data");
                
        long date;
        double level;
        int ofset;
        for (int tmpIdx = 0; tmpIdx < dataCount; tmpIdx += 1) {
        	ofset = dataIdx + (2 * tmpIdx);
	        level = this.parseValue(msg[ofset], msg[ofset+1]);                     
	        date = this.dataSet.getLastDataLogDate()
	        			- correction 
	        			- (_frequency * this.dataSet.getLength());   
	
	        Logger.debug("Add measure in dataSet: "+ level +" - "+ date);
	        this.dataSet.addMeasure(level, date);           
        }
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#readCurrentValues(int[])
	 */
	protected double[] readCurrentValues(int[] msg) throws BadlyFormattedFrameException {
		/*
		 * Frame format:
		 * ==============
		 * 
		 * 0    6     7      8        9        10        11   
		 * | ID | ACK | mode | status | MSB val | LSB val |
		 * 
		 * - ID			: module ID					6 byte
		 * - ACK		: applicative commond ACK	1 byte
		 * - mode		: operating mode			1 byte
		 * - status		: application status		1 byte
		 * - MSB val	: 							1 byte
		 * - LSB val	:							1 byte
		 */
		double[] measure = new double[1];
		int baseIdx = Message.ID_LENGTH;
		
		// check if we got an 0x81
        if (msg[baseIdx] != Message.ACK_GET_PRB) {
        	throw new BadlyFormattedFrameException(
                    "Extended datalogging ACK is not correct. Got :"
            		+ Integer.toHexString(msg[baseIdx]) +" and received "
            		+ Integer.toHexString(Message.ACK_GET_PRB));
        }
        
        this.checkStatusAndMode(msg[baseIdx + 1], msg[baseIdx + 2]);
        
		measure[0] = this.parseValue(msg[9], msg[10]);
		return measure;
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#readDatalog(int[])
	 */
	protected void readDatalog(int[] msg) throws CoronisException {
		/*
		 * Frame format
		 * =============
		 * 
		 * 0    6     7      8        9     105    111    112
		 * | ID | ACK | mode | status | data | date | freq |
		 * 
		 * - ID		: module ID					6 bytes
		 * - ACK	: applicative command ACK	1 byte
		 * - mode	: operating mode			1 byte
		 * - status	: aplication status			1 byte
		 * - data	: levels					96 bytes
		 * - date	: last logged date			6 bytes
		 * - freq	: measure period			1 byte
		 */
		int baseIdx = Message.ID_LENGTH;
		
		// check if we got an 0x83
        if (msg[baseIdx] != Message.ACK_GET_DTG) {
        	throw new BadlyFormattedFrameException(
                    "Extended datalogging ACK is not correct. Got :"
            		+ Integer.toHexString(msg[baseIdx])
            		+" but need "
            		+ Integer.toHexString(Message.ACK_GET_DTG));
        }
		
        this.checkStatusAndMode(msg[baseIdx + 1], msg[baseIdx + 2]);
        
        // timestamp => baseIdx + 1 + 1 + 1 + 96
		Calendar timestamp = CoronisLib.parseDateTime(msg, baseIdx + 99);
		int frequency = CoronisLib.parseDataloggingFrequency(msg[msg.length - 1]);
		
		int dataIdx = baseIdx + 3;
		
		double level;
		long date;
		int ofset;
		for (int tmpIdx = 0; tmpIdx < (2 * MAX_DATALOG_COUNT); tmpIdx += 2) {
			ofset = dataIdx + tmpIdx;
			level = this.parseValue(msg[ofset],msg[ofset + 1]);
			date = timestamp.getTime().getTime() - (frequency * tmpIdx / 2);
			
			Logger.debug("Add measure in dataSet: "+ level +" - "+ date);
			this.dataSet.addMeasure(level, date);
		}

	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#resetDatalogging()
	 */
	public boolean resetDatalogging() throws CoronisException, IOException, InterruptedIOException {
		// FIXME : waiting an anwer from the Coronis support to know if they are a direct restart command such as the one for
		// WaveTherm and WaveFlow's
		
		int[] answer;
		
		// Stop datalogging if needed we don't know if it's enabled or if it was enabled
		if ( (_dataloggingParameterLoaded == false) || ((this._operatingMode & 0x08) != DataLoggingModule.DATALOG_DEACTIVATED)) {
			Logger.debug("Stopping datalogging");
			answer = waveport.query_ptp_command(Message.setDataLoggingParemeters(this._modid, DataLoggingModule.DATALOG_DEACTIVATED, 0x00, 0X00), this._repeaters, this._moduleName);
		    if (! checkParameterAnswer(answer)) {
		    	Logger.error("Stopping datalogging did not work has expected");
		    	return false;
		    }
		} else {
			Logger.debug("Datalogging was not activated");
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
					Logger.error("Error while writing parameter " + parameterNumber + " while resetting datalogging");
					succeeded = false;
				}
			}
		} else throw new CoronisException("No ACK to the set parameter request");	
		return succeeded;
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#setMaxValues()
	 */
	protected void setMaxValues(){
		this.MAX_DATALOG_COUNT = 48;
		this.MAX_EXTENDED_DATALOG_COUNT = 4500;
		this.MAX_DATA_PER_SINGLEFRAME = 59;
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#startDataLoggingNow()
	 */
	public boolean startDataLoggingNow() throws InterruptedIOException, IOException, CoronisException {
		this._operatingMode = 0x84;
		
		int [] answer = this.waveport.query_ptp_command(Message.resetDatalogging(	this._modid,
																					this._operatingMode,
																					CoronisLib.getDataloggingFrequency(this._frequency)),
														this._repeaters,
														this.getModuleId());
		
		boolean ret = (answer[Message.ID_LENGTH + 1] == 0x00) ? true : false;
		return ret;
	}

}
