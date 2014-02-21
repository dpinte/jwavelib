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
 * $Date: 2009-07-06 16:39:52 +0200 (Mon, 06 Jul 2009) $
 * $Revision: 96 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/modules/WaveFlow.java $
 */
package com.coronis.modules;

import com.coronis.exception.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.*;

import com.coronis.CoronisLib;
import com.dipole.libs.*;

public class WaveFlow extends DataLoggingModule {


    public static final int MAX_DATALOGSTORE = 24;
    
    private double lastCounterValue;  

    public WaveFlow(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
        super(moduleId, wpt, modRepeaters);
    }

    public WaveFlow(String modname, int freqMinute, int[] moduleId, WaveTalk[] modRepeaters, WavePort wpt) {
        super(modname, freqMinute, moduleId, modRepeaters, wpt);
    }
    
    public void setLastCounterValue(double val) {
    	this.lastCounterValue = val;
    }
    
    protected void setMaxValues() {
        // Maximum number of data stored in the datalog table for WaveFlow !
        // FIXME : this must be corrected for multi-counter WaveFlows !
        this.MAX_EXTENDED_DATALOG_COUNT = 2100;    	
    	this.MAX_DATA_PER_SINGLEFRAME = 29;
    }
           
    
    public boolean resetDatalogging() throws CoronisException, IOException, InterruptedIOException  {
        _logger.info("Resetting datalogging");

        int[] answer = waveport.query_ptp_command(Message.resetWaveFlowDatalogging(_modid,
        								CoronisLib.getDataloggingFrequency(_frequency)),
        								_repeaters, this.getModuleId());
        
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
    
    public boolean retrieveFromDatalogging(int measureToRead, int fromIdx) throws CoronisException,
            IOException, InterruptedIOException   {
    	
    	// save the last index Value for the current call to the lastCounterValue variable in order to be able to compute the last value
    	lastCounterValue = LAST_DATALOG_VALUE;
    	_logger.debug("Last index saved : " + lastCounterValue);
    	
    	boolean res = super.retrieveFromDatalogging(measureToRead, fromIdx);
        
    	_logger.debug("New index saved : " + LAST_DATALOG_VALUE);
    	// add the differential consumption to the dataset
        dst = addDifferentialConsumption(dst, lastCounterValue);
              
        return res;        
    }
    
    public DataSet getPartialDataSet() {        
        return dst;
    }    
    
    public DataSet addDifferentialConsumption(DataSet dst, double lstCntValue){
        // Process the dataset to update the differential consumption value
        Enumeration e = dst.enumerate();
        if (e.hasMoreElements()) {
            Measure val = (Measure)e.nextElement();
            Measure nextval;         
            do {
                if (e.hasMoreElements()) nextval = (Measure)e.nextElement();
                else nextval = new Measure(lstCntValue, null);
                double[] measures = new double[2];
                measures[0] = val.getValue();
                if ( val.getValue() == Double.NaN || nextval.getValue() == Double.NaN) measures[1] = Double.NaN;
                else 
                	//if (val.getValue() >= nextval.getValue()) 
                	measures[1] = val.getValue() - nextval.getValue();                
                //else measures[1] = val.getValue() + (max - nextval.getValue());  
                val.setValues(measures);
                val = nextval;
            } while(e.hasMoreElements());
            if (lstCntValue >= 0) {
                // last element --> thus no more elements
                nextval = new Measure(lstCntValue, null);
                double[] measures = new double[2];
                measures[0] = val.getValue();
                if ( val.getValue() == Double.NaN || nextval.getValue() == Double.NaN) measures[1] = Double.NaN;
                else 
                	//if (val.getValue() >= nextval.getValue()) 
                	measures[1] = val.getValue() - nextval.getValue();                
                //else measures[1] = val.getValue() + (max - nextval.getValue());            
                val.setValues(measures);                   
            }
        } 
        return dst;
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
            throw new BadlyFormattedFrameException("Datalogging ACK is not correct");
        }

        int timestampIdx = Message.ID_LENGTH + 1 + 1 + 1 + 96;
        Calendar timestamp = null;
        int frequency = 0;

        timestamp = CoronisLib.parseDateTime(msg, timestampIdx);
        frequency = CoronisLib.parseDataloggingFrequency(msg[msg.length - 1]);


        int dataIdx = Message.ID_LENGTH + 1 + 1 + 1;
        dst = new DataSet(_modid);
        
        for (int tempindex = 0; tempindex < (4 * MAX_DATALOGSTORE); tempindex += 4) {
            double index = this.parseValue(msg, dataIdx + tempindex);            
            Date dt = new Date(timestamp.getTime().getTime() - (frequency * tempindex / 4));
            dst.addMeasure(index, dt);
            stbf.append("\n\t" + index + " at " + dt.toString());
        }

        return stbf.toString();
    }
    
    public long parseValue(int[] data, int offset) {
    	return this.parseValue(data[offset], data[offset+1], data[offset+2], data[offset+3]);
    }
    
    public long parseValue(int byte0, int byte1, int byte2, int byte3) { 
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

    public String readCurrentValue(int[] msg) throws CoronisException {
    	/*
    	 *  FIXME : this should return a float/double/long and not a string ! 
    	 */
        StringBuffer stbf = new StringBuffer();
        stbf.append("Module id : " + Functions.printHumanHex(_modid, false));
        if (msg[Message.ID_LENGTH + 0] != Message.ACK_GET_PRB) {
            throw new BadlyFormattedFrameException("Datalogging ACK is not correct. Is" + msg[Message.ID_LENGTH + 0] + " and should be " + Message.ACK_GET_DTG);
        }
        int tempindex = 3;
        long index = this.parseValue(msg, Message.ID_LENGTH + tempindex);              
        stbf.append("\nt\t" + index + "m3");

        return stbf.toString();
    }
    
    public int readExtendedDatalog(int[] msg, boolean isMultiFrame) throws CoronisException {
        /*
         * Single Frame is structured as following : 
         *  !!! FRAME 1
         * 	- 1 byte ACK_GET_EDT
         * 	- 1 byte frame number
         * 	- 1 byte frame count
         * 	- 7 bytes for last measure date and time (with seconds management !!!)
         * 	- 1 byte for index read
         * 	- 2 bytes first measurement ID sent in the frame
         *      - 2 bytes last measurement ID sent in the frame
         * 	- 4 bytes * number of measurement asked
         * !!! FRAME N structure is not the same but we do not support MultiFrame !
         *
         * Multi frame has three bytes more before the module id that is before the ACK_GET_EDT
         * and when frame number is not 1, there is no timestamp in the frame !
         *
         * First frame :
         *  |    0    +1        +2         +3          +9      +10
         *  |    6     7         8          9           15      16
         *  | ID | ACK | NbFrame | TotFrame | timestamp | index | values ...
         * Next frames :
         *  |    0    +1        +2         +3       +4
         *  |    6     7         8          9       10
         *  | ID | ACK | NbFrame | TotFrame | index | values ...         
         */
        
        // baseIdx is the number of bytes where the ACK_GET_EDL starts (just after the module ID)
        // in multiframe mode 0x36, there is three bytes more before the module ID
        int baseIdx = (isMultiFrame) ? (Message.ID_LENGTH + 3) : Message.ID_LENGTH;
        int counterIdx;
        
        if (msg[baseIdx + 0] != Message.ACK_GET_EDL) {
            throw new BadlyFormattedFrameException(
                    "Extended datalogging ACK is not correct. Waiting :" + Integer.toHexString(Message.ACK_GET_EDL) + " and received " + Integer.toHexString(msg[Message.ID_LENGTH + 0]));
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
            int timestampIdx = baseIdx + 3; 
             // read last data timestamp from first frame
            extdlg_tstamp = CoronisLib.parseDateTime(msg, timestampIdx); 
            dst.setLastMeasureDate(extdlg_tstamp);
            // counter index             
            counterIdx = baseIdx +  10 ;            

        } else {
            if (extdlg_tstamp == null) throw new CoronisException("Parsing multiframe should be done starting at frame 1"); 
            // data index  
            counterIdx = baseIdx +  3 ;                        
        }

        // Indexes are stored on two bytes - MSB first;
        int firstDataIdx = (msg[counterIdx + 1] << 8) | msg[counterIdx + 2];
        int lastDataIdx = (msg[counterIdx + 3] << 8) | msg[counterIdx + 4];              

        // check that we read the correct index        
        int counter = msg[counterIdx];
        if (counter != 0x1) {
            // FIXME : must support multi counters
            throw new BadlyFormattedFrameException("Multi counter waveflow is not supported. Seems to have " + counter + " counters " + (counterIdx));
        }  
        
        		
        latestIdx = (firstDataIdx > latestIdx) ? firstDataIdx : latestIdx;        
        
        int dataCount = (firstDataIdx - lastDataIdx) + 1 ;
        _logger.debug("From " + firstDataIdx + " To " + lastDataIdx + ". Read " + dataCount + " rows");
        
        // dataIdx is at position counterIdx + 1 + 2 bytes * (lastIndex + firstIndex) = 5 bytes
        int dataIdx = counterIdx + 5;

        if (dst == null) {
            dst = new DataSet(_modid);
        }
       
        long tval = extdlg_tstamp.getTime().getTime();
        Date dt;
         
        for (int tempindex = 0; tempindex < dataCount; tempindex++) {
        	long index = this.parseValue(msg, dataIdx + tempindex * 4);   
                                                     
            dt = new Date(tval - correction - (_frequency * dataCounter++));   
            // TODO : this can be improved : no need to check all the dates to store only the most recent one
            // could use only the first data from the file                 
            if (dt.getTime() > LAST_DATALOG_DATE) {                
                LAST_DATALOG_DATE = dt.getTime();
                LAST_DATALOG_VALUE = index;
                _logger.info("Updating last datalog date to " + dt.toString());                
            }
            dst.addMeasure(index, dt);
        }                     
        return lastDataIdx;
    }      
}
