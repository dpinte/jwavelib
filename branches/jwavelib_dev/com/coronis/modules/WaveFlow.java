/*
 * WaveFlow.java
 *
 * Created on 31 octobre 2007, 17:56
 * 
 * This is the WaveFlow class implementation. At the moment, it only supports
 * WaveFlow with only one counter.
 *
 * Author : Didrik Pinte <dpinte@itae.be> 
 * Copyright : Dipole Consulting SPRL 2008
 *
 * $Date: 2010-08-13 16:26:29 +0200 (Fri, 13 Aug 2010) $
 * $Revision: 167 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/modules/WaveFlow.java $
 */
package com.coronis.modules;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Calendar;

import com.coronis.exception.*;
import com.coronis.logging.Logger;
import com.coronis.CoronisLib;

/**
 * Class to represent a waveFlow mode
 * <p>
 * <Known limitations:<br>
 * <ul>
 * <li> Support only the standard WaveFlow
 * <li> Support only on index. All datalogging request report values for the
 *      index A
 * <li> Only time step datalogging is supported
 * </ul>
 *
 */
public class WaveFlow extends DataLoggingModule {

	private int module_type = 0x16;
	
    /**
     * Creates a new WaveFlow module
     * 
     * @param moduleId The radio address of the module
	 * @param wpt The WavePort use with this module
	 * @param modRepeaters The repeaters to reach the module
     */
    public WaveFlow(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
        super(moduleId, wpt, modRepeaters);
    }

    /**
     * Creates a new WaveFlow module
     * 
     * @param modname The module name
     * @param freqMinute The datalogging frequency of the module (in minutes)
     * @param moduleId The radio address of the module
     * @param modRepeaters The repeaters to reach the module
     * @param wpt The WavePort use with this module
     */
    public WaveFlow(String modname, int freqMinute, int[] moduleId, WaveTalk[] modRepeaters, WavePort wpt) {
        super(modname, freqMinute, moduleId, modRepeaters, wpt);
    }
    
    public int getModuleType(){
    	return this.module_type;
    }

    protected void setMaxValues() {
        // Maximum number of data stored in the datalog table for WaveFlow !
        // FIXME : this must be corrected for multi-counter WaveFlows !
        this.MAX_EXTENDED_DATALOG_COUNT = 2100;    	
    	this.MAX_DATA_PER_SINGLEFRAME = 29;
    	this.MAX_DATALOG_COUNT = 24;
    }
           
    public boolean resetDatalogging() throws CoronisException, IOException, InterruptedIOException  {
        Logger.info("Resetting datalogging");

        int [] answer = this.waveport.query_ptp_command(Message.resetDatalogging(	this._modid,
																					0x14,
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

    protected long parseValue(int[] data, int offset) {
    	return this.parseValue(data[offset], data[offset+1], data[offset+2], data[offset+3]);
    }
    
    protected long parseValue(int byte0, int byte1, int byte2, int byte3) { 
    	/*
    	 * Parses the 4 bytes of a Waveflow index value. Indexes are stored on 4 bytes.
    	 * 
    	 * Because Integer.MAX_VALUE is less than 2^32-1, we need to use long !
    	 */
        long i1 = ((long)byte0) << 3 * 8;
        long i2 = ((long)byte1) << 2 * 8;
        long i3 = (long)byte2 << 8;
        long i4 = (long)byte3;
        return i1 + i2 + i3 + i4;    	
    }
    
    /* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#readAdvancedDataLog(int[], boolean)
	 */
    // TODO: add support for more than 1 counter
	protected void readAdvancedDataLog(int[] msg, boolean isMultiFrame) throws CoronisException {
        /*
         * Frame Format:
         * ==============
         * 
         * - Single frame:
         * ----------------
         * - The frame as the same structure than the first Multi Frame
         * 
         * - Multi frames :
         * -----------------
         * - Only the first frame have a date field
         * - 3 more byte before ID
         *
         * First frame :
         * |    0    +1        +2         +3     +9      +10
         * |    6     7         8          9     15      16
         * | ID | ACK | NbFrame | TotFrame | date | index | firstIdx | lastIdx | values |
         * 
         * Next frames :
         * |    0    +1        +2         +3       +4
         * |    6     7         8          9       10
         * | ID | ACK | NbFrame | TotFrame | index | fisrtIdx | lastIdx | values |
         * 
         * - ID			: module ID					6 bytes
         * - ACK		: applicative command ACK	1 byte
         * - NbFrame	: frame number				1 byte
         * - TotFrame	: total of frames			1 byte
         * - date		: last logged measure date	6 bytes
         * - firstIdx	: first received index		2 bytes
         * - lastIdx	: last received index		2 bytes
         * - values		: measures					2 bytes per values
         */
		
        // baseIdx is the number of bytes where the ACK_GET_EDL starts (just after the module ID)
        // in multiframe mode 0x36, there is three bytes more before the module ID
        int baseIdx = (isMultiFrame) ? (Message.ID_LENGTH + 3) : Message.ID_LENGTH;
        int firstDataIdx, lastDataIdx;
        int counterIdx;
        
        // check if received 0x89
        if (msg[baseIdx + 0] != Message.ACK_GET_EDL) {
            throw new BadlyFormattedFrameException(
                    "Extended datalogging ACK is not correct. Got :"
            		+ Integer.toHexString(msg[baseIdx + 0])
            		+" but need "
            		+ Integer.toHexString(Message.ACK_GET_EDL));
        }

        // check that frame number is not 0xFF --> means error p52
        // and check is not > 1
        if (msg[baseIdx + 1] == 0xFF) {
            throw new MissingDataException("Error while asking for extended datalog measurements. Most probably due to a extended datalogging configuration problem or the extended datalogging has not been started.");
        }        
        
        // depending if it's the first frame or not 
        if (msg[baseIdx + 1] == 1) {
            int timestampIdx = baseIdx + 3; 
             // read last data timestamp from first frame
            this.dataSet.setLastDataLogDate(CoronisLib.parseDateTime(msg, timestampIdx).getTime().getTime());
            
            // counter index             
            counterIdx = baseIdx + 10;
        } else {
            if (this.dataSet.getLastDataLogDate() == 0) {
            	throw new CoronisException("Parsing multiframe should be done starting at frame 1"); 
            }
            // data index  
            counterIdx = baseIdx + 3;                        
        }

        // Indexes are stored on two bytes - MSB first;
        firstDataIdx = (msg[counterIdx + 1] << 8) | msg[counterIdx + 2];
        lastDataIdx = (msg[counterIdx + 3] << 8) | msg[counterIdx + 4];              

        // check that we read the correct index        
        int counter = msg[counterIdx];
        if (counter != 0x1) {
            // FIXME : must support multi counters
            throw new BadlyFormattedFrameException("Multi counter waveflow is not supported. Seems to have " + counter + " counters " + (counterIdx));
        }        
        
        /* store the last data index reported in the frame to 
         * keep index position
         */
        this.dataSet.setLastReceivedIndex(lastDataIdx);
        
        int dataCount = (firstDataIdx - lastDataIdx) + 1 ;
        Logger.debug(	"From " + firstDataIdx + " To "
        				+ lastDataIdx + ". Read " + dataCount + " rows");
        
        // dataIdx is at position counterIdx + 1 + 2 bytes * (lastIndex + firstIndex) = 5 bytes
        int dataIdx = counterIdx + 5;
       
        long date;
        long index;
        for (int tmpIdx = 0; tmpIdx < dataCount; tmpIdx++) {
        	index = this.parseValue(msg, dataIdx + tmpIdx * 4);   
                                                     
            date = this.dataSet.getLastDataLogDate() 
            			- this.correction 
            			- (this._frequency * this.dataSet.getLength());
            
            Logger.debug("Add measure in dataSet: "+ index +" - "+ date);
            
            this.dataSet.addMeasure(index, date);
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
		 * 0    6     7      8        9        13        17
		 * | ID | ACK | mode | status | Index A | Index B |
		 * 
		 * - ID			: module ID
		 * - ACK		: applicative command ACK
		 * - mode		: operating mode
		 * - status		: applicati
		 * - Index A	:
		 * - Index B	:
		 */
		double[] measures = new double[2];

		// check if we got an 0x81
        if (msg[Message.ID_LENGTH + 0] != Message.ACK_GET_PRB) {
        	throw new BadlyFormattedFrameException(
                    "Extended datalogging ACK is not correct. Got :"
            		+ Integer.toHexString(msg[Message.ID_LENGTH + 0]) +" and received "
            		+ Integer.toHexString(Message.ACK_GET_PRB));
        }
        
		measures[0] = this.parseValue(msg[9], msg[10], msg[11], msg[12]);
		measures[1] = this.parseValue(msg[13], msg[13], msg[15], msg[16]);
		
		return measures;
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#readDatalog(int[])
	 */
	// FIXME : something specila with more than 1 counter ??
	protected void readDatalog(int[] msg) throws CoronisException {
		/*
		 * Frame format:
		 * ==============
		 * 
		 * 0    6     7      8        9       105    111    112 
		 * | ID | ACK | mode | status | values | date | freq |
		 * 
		 * - ID		: module ID					6 bytes
		 * - ACK	: applicative command ACK	1 byte
		 * - mode	: operating  mode			1 byte
		 * - status	: application status		1 byte
		 * - values	: storage table				96 bytes
		 * - date	: last logged measure date	6 bytes
		 * - freq	: measure period			1 byte
         */
		
		// check if we got an 0x83
        if (msg[Message.ID_LENGTH + 0] != Message.ACK_GET_DTG) {
        	throw new BadlyFormattedFrameException(
                    "Extended datalogging ACK is not correct. Got :"
            		+ Integer.toHexString(msg[Message.ID_LENGTH + 0])
            		+" but need "
            		+ Integer.toHexString(Message.ACK_GET_DTG));
        }

        int timestampIdx = Message.ID_LENGTH + 1 + 1 + 1 + 96;
        Calendar timestamp = null;
        int frequency = 0;

        timestamp = CoronisLib.parseDateTime(msg, timestampIdx);
        frequency = CoronisLib.parseDataloggingFrequency(msg[msg.length - 1]);

        int dataIdx = Message.ID_LENGTH + 1 + 1 + 1;
        
        double index;
        long date;
        for (int tempIdx = 0; tempIdx < (4 * MAX_DATALOG_COUNT); tempIdx += 4) {
            index = this.parseValue(msg, dataIdx + tempIdx);            
            date = timestamp.getTime().getTime() - (frequency * tempIdx / 4);
            
            Logger.debug("Add measure in dataSet: "+ index +" - "+ date);
            this.dataSet.addMeasure(index, date);
        }		
	}
	
	// TODO: implements readCurentValues
	public double[] getGlobalCurentValue() {
		return null;
	}
	
	// TODO: implements readGlobalCurentValues
	protected double[] readGlobalCurentValues(int[] msg) {
		/*
		 * Frame format:
		 * 
		 * WaveFlow 4-entries:
		 * --------------------
		 * 
		 * 0        6     7      8        9         13        17        21        25
		 * | mod ID | ACK | mode | status |  Index A | Index B | Index C | Index D |
		 * 
		 * WaveFlow standad, other, ...:
		 * ------------------------------
		 * 
		 * 0        6     7      8        9         13        17            21            25
		 * | mod ID | ACK | mode | status |  Index A | Index B | Index H2O A | Index H2O B |
		 * 
		 */
		return null;
	}

	
	/* (non-Javadoc)
	 * @see com.coronis.modules.DataLoggingModule#startDataLoggingNow()
	 */	
	// TODO : change 0x14
	public boolean startDataLoggingNow() throws InterruptedIOException, IOException, CoronisException {
		this._operatingMode = 0x14;
		
		int [] answer = this.waveport.query_ptp_command(Message.resetDatalogging(	this._modid,
																					this._operatingMode,
																					CoronisLib.getDataloggingFrequency(this._frequency)),
														this._repeaters,
														this.getModuleId());
		
		boolean ret = (answer[Message.ID_LENGTH + 1] == 0x00) ? true : false;
		return ret;
	}      
}
