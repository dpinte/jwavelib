/**
 * 
 */
package com.coronis.modules;

import java.io.IOException;
import java.io.InterruptedIOException;

import com.coronis.CoronisLib;
import com.coronis.exception.BadlyFormattedFrameException;
import com.coronis.exception.CoronisException;
import com.coronis.exception.MissingDataException;
import com.coronis.logging.Logger;

/**
 * @author antoine
 *
 */
public abstract class WaveTherm extends DataLoggingModule {
	
	protected int valLength;	
	
	/**
	 * @param moduleId
	 * @param wpt
	 * @param modRepeaters
	 */
	public WaveTherm(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
		super(moduleId, wpt, modRepeaters);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param modName
	 * @param freqMinute
	 * @param moduleId
	 * @param modRepeaters
	 * @param wpt
	 */
	public WaveTherm(String modName, int freqMinute, int[] moduleId,
			WaveTalk[] modRepeaters, WavePort wpt) {
		super(modName, freqMinute, moduleId, modRepeaters, wpt);
		// TODO Auto-generated constructor stub
	}

	protected abstract double parseTemperature(int[] temp) throws MissingDataException;
	
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
         * - data		: temperatures					2 bytes per values
         */
		
		int baseIdx = (isMultiFrame) ? (Message.ID_LENGTH + 3) : Message.ID_LENGTH; 
        int firstDataIdx;
        int lastDataIdx;  
        int dataIdx;
        
        // check if received 0x86
        if (msg[baseIdx + 0] != Message.ACK_GET_EDT) {
            throw new BadlyFormattedFrameException(
                    "Extended datalogging ACK is not correct. Got: "
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
        	
        	Logger.debug(	"Last Datalog measure date: "
        					+ this.dataSet.getLastDataLogDate());
        	
            // Indexes are stored on two bytes;
            firstDataIdx = (msg[baseIdx + 9] << 8) | msg[baseIdx + 10];
            lastDataIdx = (msg[baseIdx + 11] << 8) | msg[baseIdx + 12]; 
            
            // data index 
            dataIdx = baseIdx + 13;
            
        } else {
            if (this.dataSet.getLastDataLogDate() == 0) {
            	throw new CoronisException("Parsing multiframe should be done starting at frame 1");
            }
            
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
        Logger.debug(	"Reading "+ dataCount +" vafules From " 
        				+ firstDataIdx +" To" + lastDataIdx);
        
        double temperature;
        long date;
        int ofset;
        int[] val = new int[this.valLength];
        for (int tempIdx = 0; tempIdx < dataCount; tempIdx++) { 
        	ofset = dataIdx + (this.valLength * tempIdx); 
                try {
                	for(int i = 0; i < valLength; i++) {
                		val[i] = msg[ofset + i];
                	}
                    temperature = this.parseTemperature(val);
                } catch (MissingDataException e) {
                    // means data is not valid anymore --> skip the rest
                    Logger.warning("Missing data read");
                    temperature = Double.NaN;
                }
                
                date = this.dataSet.getLastDataLogDate() 
                			- this.correction 
                			- (this._frequency * this.dataSet.getLength());

                Logger.debug("Add measure in dataSet: "+ temperature +" - "+ date);
                dataSet.addMeasure(temperature, date);       
        }
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#readCurrentValues(int[])
	 */
	protected abstract double[] readCurrentValues(int[] msg) throws BadlyFormattedFrameException;

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#readDatalog(int[])
	 */
	protected void readDatalog(int[] msg) throws CoronisException {
		/*
		 * Frame Format:
		 * =============
		 *     
		 * 0    6     7      8        9      57       95     101    102
		 * | ID | ACK | mode | status | TempA | Temp B | date | freq |
		 * 
         *  - ID		: module ID 					6 bytes
         *  - ACK		: applicative command ACK 		1 byte
         *  - mode		: application mode				1 byte
         *  - status	: operating status				1 byte
         *  - Temp A	: probe A table					48 bytes
         *  - Temp B	: probe B table					48 bytes
         *  - date		: last recorded measure date 	6 bytes
         *  - freq		: measure period				1 byte						 
         */        
        long timestamp;
        int frequency = 0;
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
        
        // timestamp => baseIdx + 1 + 1 + 1 + 48 + 48
        timestamp = CoronisLib.parseDateTime(msg, baseIdx + 99).getTime().getTime();
        frequency = CoronisLib.parseDataloggingFrequency(msg[msg.length - 1]);
        
        int dataIdx = baseIdx + 3;

        double  temperature;
        long date;
        int ofset;
        int[] val = new int[this.valLength];
        for (int tmpIdx = 0; tmpIdx < MAX_DATALOG_COUNT; tmpIdx++) {
        	ofset = dataIdx + (this.valLength * tmpIdx);
            try {
            	val[0] = msg[ofset];
            	val[1] = msg[ofset + 1];
            	temperature = this.parseTemperature(val);
            	date = timestamp - (frequency * tmpIdx);
            	
            	Logger.debug("Add measure in dataSet: "+ temperature +" - "+ date);
                this.dataSet.addMeasure(temperature, date);
                
            } catch (MissingDataException e) {
                // means data is not valid anymore --> skip the rest
                Logger.error("Missing data exception while parsing datalog");
                this.dataSet.setMissingValues(true);
                break;
            }
        }
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#resetDatalogging()
	 */
	public boolean resetDatalogging() throws CoronisException, IOException, InterruptedIOException {
		Logger.info("Resetting datalogging");
        this._operatingMode = 0x84;
        
        int [] answer = this.waveport.query_ptp_command(Message.resetDatalogging(this._modid,
										this._operatingMode,
										CoronisLib.getDataloggingFrequency(this._frequency)),
										this._repeaters,
										this.getModuleId());
        
        if (answer[Message.ID_LENGTH + 0] != Message.ACK_RESET_DATALOGGING) {
            throw new CoronisException("Invalid answer to reset datalogging request. Answer code is :" +
            							answer[Message.ID_LENGTH + 0]);
        }
        
        switch (answer[Message.ID_LENGTH + 1] ) {
            case 0x00: Logger.info("Datalogging has been restarted");
                       return true;
            case 0x01: Logger.info("Datalogging has stopped");
                       return false;
            case 0xFF: Logger.info("Syntax error on datalogging reset command");
                       return false; 
            case 0xFE: Logger.info("The value of several parameters (0x80,0x81,0x82,0x83,0x84) are written over the limit.");
                       return false;    
            case 0xFD: Logger.info("The current Date isn't conform with the select mode.");
                       return false;                    
            default:   Logger.info("Invalid return status from datalogging reset request...");
                       return false;  
        }
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#setMaxValues()
	 */
	protected abstract void setMaxValues();

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#startDataLoggingNow()
	 */
	public boolean startDataLoggingNow() throws InterruptedIOException,
			IOException, CoronisException {
		// TODO Auto-generated method stub
		return false;
	}

}
