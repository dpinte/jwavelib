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
 * $Date: 2009-09-17 20:35:39 +0200 (Thu, 17 Sep 2009) $
 * $Revision: 160 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/modules/Message.java $
 */
package com.coronis.modules;

public class Message {
    /*
     * Default message format is :
     * 	- 6 bytes for the destination address,
     * 	- 1 byte for applicative commande
     *  - 151 bytes for the message content
     *  The last 152 bytes are the "Donnees utiles" mentionned in the user manual.
     */

	// Parameter acces
    /**
     * Module parameter read Request (0x10)
     */
    public static final short GET_SENSOR_PARAM      = 0x10;
    /**
     * Module parameter read Response (0x90)
     */
    public static final short ACK_GET_SENSOR_PARAM =  0x90;
    /**
     * Module parameter write Request (0x11)
     */
    public static final short SET_SENSOR_PARAM      = 0x11;
    /**
     * Module parameter write Response (0x91)
     */
    public static final short ACK_SET_SENSOR_PARAM  = 0x91;
    
    // Module informations
    /**
     * Module type read Request (0x20)
     */
    public static final int GET_TYPE     = 0x20;
    /**
     * Moduletype read Response (0xA0)
     */
    public static final int ACK_GET_TYPE = 0xA0;
    /**
     * Firmware read Request (0x28)
     */
    public static final int GET_FIRMWARE = 0x28;
    /**
     * Firmware read Response (0xA8)
     */
    public static final int ACK_GET_FIRM = 0xA8;
    /**
     * Date and Time read Request (0x12)
     */
    public static final int GET_DATETIME = 0x12;
    /**
     * Date and Time read Response (0x92)
     */
    public static final int ACK_GET_DTM  = 0x92;
    /**
     * Date and Time write Request (0x13)
     */
    public static final int SET_DATETIM  = 0x13;
    /**
     * Date and Time write Response (0x93)
     */
    public static final int ACK_SET_DAT  = 0x93;
    
    // probes information, calibration and init
    // TODO: add probes info commands definition
    
    // Table and index handling
    /**
     * Datalogging read Request (0x03)
     */
    public static final int GET_DATALOG  = 0x03;
    /**
     * Datalogging read Response (0x83)
     */
    public static final int ACK_GET_DTG  = 0x83;
    /**
     * Advanced dataloging read Request (0x06)
     */
    public static final int GET_EXT_DTG  = 0x06;
    /**
     * Advanced dataloging read Response (0x86)
     */
    public static final int ACK_GET_EDT  = 0x86;
    /**
     * Probes immediate read Request (0x01)
     */
    public static final int GET_PROBE_V  = 0x01;
    /**
     * Probes immediate read Response (0x81)
     */
    public static final int ACK_GET_PRB  = 0x81;
    /**
     * (0x09)
     */
    public static final int GET_EXT_WFL  = 0x09;
    /**
     * (0x89)
     */
    public static final int ACK_GET_EDL  = 0x89;
    /**
     * (0x0A)
     */
    public static final short RESET_DATALOGGING     = 0X0A;
    /**
     * (0xBA)
     */
    public static final short ACK_RESET_DATALOGGING = 0X8A;
    
    protected static final int ID_LENGTH = 6;

    /**
     * Creates a message to request the type of the module
     * @param moduleId The ID of the module
     * @return The message
     */
    public static int[] askType(int[] moduleId) {
        return Message.buildMessage(moduleId, GET_TYPE);
    }

    /**
     * Creates a message to request the firmware of the module
     * 
     * @param moduleId The ID of the module
     * @return The message
     */
    public static int[] askFirmware(int[] moduleId) {
        return buildMessage(moduleId, GET_FIRMWARE);
    }

    /**
     * Creates a message to request the internal date and time of the module
     * 
     * @param moduleId The ID of the module
     * @return The message
     */
    public static int[] askDatetime(int[] moduleId) {
        return buildMessage(moduleId, GET_DATETIME);
    }

    /**
     * Creates a message to request the DataLogging table of the module
     * 
     * @param moduleId The ID of the module
     * @return The message
     */
    public static int[] askDataLog(int[] moduleId) {
        return buildMessage(moduleId, GET_DATALOG);
    }           

    /**
     * Creates a message to request the current sensors value of the module
     * 
     * @param moduleId The ID of the module
     * @return The message
     */
    public static int[] askCurrentValue(int[] moduleId) {
        return buildMessage(moduleId, GET_PROBE_V);
    }
    
    /**
     * creates a message to request the current sensors value for a WaveTherm 
     * PT100 or PT1000
     * 
     * @param moduleId The ID of the module
     * @param precision The precision
     * @return The message
     */
    public static int[] askWaveThermPT100CurrentValue(int[] moduleId, int precision) {
    	int[] msg = new int[ID_LENGTH + 2];
    	
    	// add the module identifier
        for (int i = 0; i < ID_LENGTH; i++) {
            msg[i] = moduleId[i];
        }
        
    	msg[ID_LENGTH] = GET_PROBE_V;
    	msg[ID_LENGTH + 1] = precision;
    	
    	return msg;
    }
    /**
     * Creates a message to request the Advanced DataLogging table
     * 
     * @param mod The object to represent the module
     * @param toRead The number of value to read
     * @param from The starting index
     * @return The message
     */
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
    
    /**
     * Creates a message to stop the dataLogging
     * 
     * @param moduleId The ID of the module
     * @param opMode The operation byte to write
     * @return The message
     */
    public static int[] stopDataLogging(int[] moduleId, int opMode) {
    	int[] msg = new int[ID_LENGTH + 5];
    	
        // add the module identifier
        for (int i = 0; i < ID_LENGTH; i++) {
            msg[i] = moduleId[i];
        }        
        
        msg[ID_LENGTH] =  SET_SENSOR_PARAM;  // CMD
        msg[ID_LENGTH + 1] = 1;
        msg[ID_LENGTH + 2] = 0x01;
        msg[ID_LENGTH + 3] = 1;
        msg[ID_LENGTH + 4] = (opMode & 0xF3);
        
    	return msg;
    }
    
    /**
     * Creates a message to restart the dataLogging
     * 
     * @param moduleId The ID of the module
     * @param configdata The operation mode byte
     * @param frequency The frequency
     * @return The message
     */
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
    
    /**
     * Creates a message to update the internal date and time of the module
     * 
     * @param moduleId The ID of the module
     * @param day The day
     * @param month The month
     * @param year The year
     * @param dow The Day of The week
     * @param hour the hour
     * @param minute The minute
     * @return The message
     */
    public static int[] askSetTime(int[] moduleId, int day, int month, int year, int dow, int hour, int minute) {        
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
    
    /**
     * Creates the message to get the module Parameters:
     * <p>
     * <ul>
     * <li> OERATING_MODE (0x01)
     * <li> MEASUREMENT_PERIOD (0x80)
     * <li> APPLICATION_STATUS (0x20)
     * </ul>
     * 
     * @param moduleId The ID of the module
     * @return The message
     */
    public static int[] askDataLoggingParameters(int[] moduleId){        
        // lenght is ID + CMD + nbr param +  (param, length param) * nbr of param
        int[] msg = new int[ID_LENGTH + 8];  
        

        // add the module identifier
        for (int i = 0; i < ID_LENGTH; i++) {
            msg[i] = moduleId[i];
        }        
        
        msg[ID_LENGTH] =  GET_SENSOR_PARAM;  // CMD      
        msg[ID_LENGTH + 1] = 3;		// number of parameters asked
        msg[ID_LENGTH + 2] = 0x01;	// id of first parameter --> OPERATING_MODE
        msg[ID_LENGTH + 3] = 1; 	// length of first parameter
        msg[ID_LENGTH + 4] = 0x80;	// id of second parameter --> MEASUREMENT_PERIOD
        msg[ID_LENGTH + 5] = 1; 	// length of first parameter
        msg[ID_LENGTH + 6] = 0x20;
        msg[ID_LENGTH + 7] = 1;
        
        return msg;
    }   
    
    /**
     * Creates the message to set the DataLogging parameters:
     * <p>
     * <ul>
     * <li> MEASUREMENT_PERIOD (0x80)
     * <li> START_HOUR
     * <li> OPERATING_MODE
     * </ul>
     * 
     * @param moduleId ID of the module
     * @param operatingMode The DataLogging operating mode
     * @param frequency The DataLogging frequency
     * @param startHour The DataLogging stating hour
     * @return the message
     */
    public static int[] setDataLoggingParemeters(int[] moduleId, int operatingMode, int frequency, int startHour){        
        // lenght is ID + CMD + nbr param +  (param, length param) * nbr of param
        int[] msg = new int[ID_LENGTH + 8];  
        

        // add the module identifier
        for (int i = 0; i < ID_LENGTH; i++) {
            msg[i] = moduleId[i];
        }        
        
        msg[ID_LENGTH] =  SET_SENSOR_PARAM;  // CMD      
        msg[ID_LENGTH + 1] = 2;		// number of parameters asked
        msg[ID_LENGTH + 2] = 0x80;	// id of first parameter --> MEASUREMENT_PERIOD      
        msg[ID_LENGTH + 3] = 1;		// length of the parameter
        msg[ID_LENGTH + 4] = frequency;
        /*
        msg[ID_LENGTH + 5] = 0x81;	// id of second parameter --> START_HOUR
        msg[ID_LENGTH + 6] = 1; 	// length of the parameter
        msg[ID_LENGTH + 7] = startHour;
        */
        msg[ID_LENGTH + 5] = 0x01;	// id of second parameter --> OPERATING_MODE
        msg[ID_LENGTH + 6] = 1; 	// length of the parameter
        msg[ID_LENGTH + 7] = operatingMode;       
        
        return msg;
    }    

    /**
     * Creates a message
     * 
     * @param moduleId The ID of the module
     * @param command The message command
     * @return The message
     */
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
