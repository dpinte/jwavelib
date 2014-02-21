/*
 * Message.java
 *
 * Created on 31 octobre 2007, 17:56
 * 
 * Message class takes care of the DATA part of a Coronis frame. It builds 
 * DATA part for a lot of pre-defined tasks (askFirmware, etc.). 
 * 
 *
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 *
 * $Date: 2009-06-01 14:37:14 +0200 (Mon, 01 Jun 2009) $
 * $Revision: 73 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/modules/Message.java $
 */
package com.coronis.modules;

import com.coronis.logging.SimpleLogger;

//FIXME : this class should be totally static !!!! This will improve performance and decrease the number of object creation and deletion
public class Message {
    /*
     * Default message format is :
     * 	- 6 bytes for the destination address,
     * 	- 1 byte for applicative commande
     *  - 151 bytes for the message content
     *  The last 152 bytes are the "Donnees utiles" mentionned in the user manual.
     */

    protected static final int ID_LENGTH = 6;
    protected int[] moduleid;
    private SimpleLogger _logger;
    
    public static final int GET_TYPE     = 0x20;
    public static final int ACK_GET_TYPE = 0xA0;
    public static final int GET_FIRMWARE = 0x28;
    public static final int ACK_GET_FIRM = 0xA8;
    public static final int GET_DATETIME = 0x12;
    public static final int ACK_GET_DTM  = 0x92;
    public static final int GET_DATALOG  = 0x03;
    public static final int ACK_GET_DTG  = 0x83;
    public static final int GET_EXT_DTG  = 0x06;
    public static final int ACK_GET_EDT  = 0x86;
    public static final int GET_PROBE_V  = 0x01;
    public static final int ACK_GET_PRB  = 0x81;
    public static final int GET_EXT_WFL  = 0x09;
    public static final int ACK_GET_EDL  = 0x89;
    public static final int SET_DATETIM  = 0x13;
    public static final int ACK_SET_DAT  = 0x93;
    
    public static final short GET_SENSOR_PARAM      = 0x10;
    public static final short ACK_GET_SENSOR_PARAM =  0x90;
    public static final short SET_SENSOR_PARAM      = 0x11;
    public static final short ACK_SET_SENSOR_PARAM  = 0x91;   
    public static final short RESET_DATALOGGING     = 0X0A;
    public static final short ACK_RESET_DATALOGGING = 0X8A;

    private Message() {        
    }
    


    public static int[] askType(int[] moduleId) {
        return Message.buildMessage(moduleId, GET_TYPE);
    }

    public static int[] askFirmware(int[] moduleId) {
        return buildMessage(moduleId, GET_FIRMWARE);
    }

    public static int[] askDatetime(int[] moduleId) {
        return buildMessage(moduleId, GET_DATETIME);
    }

    public static int[] askDataLog(int[] moduleId) {
        return buildMessage(moduleId, GET_DATALOG);
    }           

    public static int[] askCurrentValue(int[] moduleId) {
        return buildMessage(moduleId, GET_PROBE_V);
    }
    
    public static int[] askExtendedDataLog(DataLoggingModule mod, int toRead, int from) {
    	if (mod instanceof WaveFlow) {
    		// FIXME : this supports only one counter WaveFlows !
    		return askWaveFlowExtendedDataLog(mod.getRadioId(), 0x01, toRead, from);
    	} else {
	    	// Extended datalogging needs a CMD plus the number of measure to read (2 bytes) and where to start from (2 bytes)
	        // if from = 0x0000, it means start from the most recent one
	        int[] msg = new int[ID_LENGTH + 1 + 4];
	
	        // add the module identifier
	        for (int i = 0; i < ID_LENGTH; i++) {
	            msg[i] = mod.getRadioId()[i];
	        }
	        msg[ID_LENGTH] = GET_EXT_DTG;
	        msg[ID_LENGTH + 1] = toRead >>> 8;
	        msg[ID_LENGTH + 2] = toRead & 0xFF;
	        msg[ID_LENGTH + 3] = from >>> 8;
	        msg[ID_LENGTH + 4] = from & 0xFF;	       
	        return msg;
    	}
    }   
    
    public static int[] askWaveFlowExtendedDataLog(int[] moduleId, int index, int toRead, int from) {
        /* Extended datalogging needs a CMD plus :
        - the requested index ( bit 0 is index 1, bit 1 is index B, bit 2 is index C and bit 3 is index D 
        - the number of measure to read (2 bytes)
        - where to start from (2 bytes)
        If from = 0x0000, it means start from the most recent one
         */
        int[] msg = new int[ID_LENGTH + 1 + 1 + 2 + 2];

        // add the module identifier
        for (int i = 0; i < ID_LENGTH; i++) {
            msg[i] = moduleId[i];
        }

        msg[ID_LENGTH] = GET_EXT_WFL;
        msg[ID_LENGTH + 1] = index;
        msg[ID_LENGTH + 2] = toRead >>> 8;
        msg[ID_LENGTH + 3] = toRead & 0xFF;
        msg[ID_LENGTH + 4] = from >>> 8;
        msg[ID_LENGTH + 5] = from & 0xFF;
        return msg;
    }    
    
    
    /*
     * Resets the WaveFlow extended datalogging using a 0x14 command. This means the datalogging is resetted and restarted directly
     * There is no need to pass a startup hour. 
     * 
     * ! If you need to synchronise your datalogging hour in all your modules, do no use this method but use a 0x80 and 0X81 command     
     */    
    public static int[] resetWaveThermDatalogging(int[] moduleId, int frequency) {    
        // FIXME : remove the 0X84 parameter !!!
        return resetDatalogging(moduleId, 0x84, frequency);
    }
    
    /*
     * Resets the WaveFlow extended datalogging using a 0x14 command. This means the datalogging is resetted and restarted directly
     * There is no need to pass a startup hour. 
     * 
     * ! If you need to synchronise your datalogging hour in all your modules, do no use this method but use a 0x80 and 0X81 command     
     */
    public static int[] resetWaveFlowDatalogging(int[] moduleId, int frequency) {  
        // FIXME : remove the 0X14 parameter !!!
        return resetDatalogging(moduleId, 0x14, frequency);
    }
    
    public static int[] resetDatalogging(int[] moduleId, int configdata, int frequency) {        
        int[] msg = new int[ID_LENGTH + 7];   
        
        // add the module identifier
        for (int i = 0; i < ID_LENGTH; i++) {
            msg[i] = moduleId[i];
        }        
        msg[ID_LENGTH + 0] = RESET_DATALOGGING;
        msg[ID_LENGTH + 1] = configdata; // config data - 10000100       
        msg[ID_LENGTH + 2] = frequency; // 0X0E; //datalogging period - 15 minutes
        msg[ID_LENGTH + 3] = 0x00; //Beginning hour of the periodic datalogging
        msg[ID_LENGTH + 4] = 0x00; //Month day or week day (datalogging)
        msg[ID_LENGTH + 5] = 0x00; //Measure hour
        msg[ID_LENGTH + 6] = 0x00; //Beginning minute of the periodic datalogging combined with the start hour written in parameter 0x81              
        
        return msg;
    }      
    
    public static int[] askSetTime(int[] moduleId, int day, int month, int year, int dow, int hour, int minute) {
        /* Setting the datetime :
        - CMD is SET_DATETIM
        - day (1 byte)
        - month (1 byte)
        - year (-2000) (1 byte)
        - day of the week (1 byte)
        - hour (1 byte)
        - minute (1 byte)       
         */        
        
        int[] msg = new int[ID_LENGTH + 7];
        // add the module identifier
        for (int i = 0; i < ID_LENGTH; i++) {
            msg[i] = moduleId[i];
        }
        msg[ID_LENGTH ] = SET_DATETIM;
        msg[ID_LENGTH + 1] = day;
        msg[ID_LENGTH + 2] = month;
        msg[ID_LENGTH + 3] = year;
        msg[ID_LENGTH + 4] = dow;
        msg[ID_LENGTH + 5] = hour;
        msg[ID_LENGTH + 6] = minute;
        
        return msg;
    }
    
    public static int[] askDataLoggingParameters(int[] moduleId){
        // request reading :
        // - functionnal mode 0X01
        // - datalogging frequency 0X80
        
        // lenght is ID + CMD + nbr param +  (param, length param) * nbr of param
        int[] msg = new int[ID_LENGTH + 1 + 1 + (2 * 2)];  
        

        // add the module identifier
        for (int i = 0; i < ID_LENGTH; i++) {
            msg[i] = moduleId[i];
        }        
        
        msg[ID_LENGTH] =  GET_SENSOR_PARAM;  // CMD      
        msg[ID_LENGTH + 1] = 2; // number of parameters asked
        msg[ID_LENGTH + 2] = 0x01; // id of first parameter --> OPERATING_MODE
        msg[ID_LENGTH + 3] = 1; // length of first parameter
        msg[ID_LENGTH + 4] = 0x80; // id of second parameter --> MEASUREMENT_PERIOD
        msg[ID_LENGTH + 5] = 1; // length of first parameter
        
        return msg;
    }   
    
    public static int[] setDataLoggingParemeters(int[] moduleId, int operatingMode, int frequency, int startHour){
        // request writing :
        // - MEASUREMENT_PERIOD mode 0w80
        // - START_HOUR 0x81
    	// - OPERATING_MODE - 0x01
        
        // lenght is ID + CMD + nbr param +  (param, length param) * nbr of param
        int[] msg = new int[ID_LENGTH + 7];  
        

        // add the module identifier
        for (int i = 0; i < ID_LENGTH; i++) {
            msg[i] = moduleId[i];
        }        
        
        msg[ID_LENGTH] =  SET_SENSOR_PARAM;  // CMD      
        msg[ID_LENGTH + 1] = 2; // number of parameters asked
        msg[ID_LENGTH + 2] = 0x80; // id of first parameter --> OPERATING_MODE      
        msg[ID_LENGTH + 3] = 1; // length of first parameter
        msg[ID_LENGTH + 4] = frequency; // id of first parameter --> OPERATING_MODE
        msg[ID_LENGTH + 5] = 0x81; // id of second parameter --> MEASUREMENT_PERIOD
        msg[ID_LENGTH + 6] = 1; // length of first parameter
        msg[ID_LENGTH + 7] = startHour; // length of first parameter
        msg[ID_LENGTH + 5] = 0x01; // id of second parameter --> MEASUREMENT_PERIOD
        msg[ID_LENGTH + 6] = 1; // length of first parameter
        msg[ID_LENGTH + 7] = operatingMode; // length of first parameter        
        
        return msg;
    }    

    public static int[] buildMessage(int[] moduleId, int command) {
        int[] msg = new int[ID_LENGTH + 1];
        // add the module identifier
        for (int i = 0; i < ID_LENGTH; i++) {
            msg[i] = moduleId[i];
        }
        msg[ID_LENGTH] = command;
        // add the code 
        return msg;
    }
}
