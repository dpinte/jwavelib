/*
 * CoronisLib.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * CoronisLib is a class with only static methods implementing a toolbox for 
 * the Coronis protocol (crc, date and time parsing, reading module id from 
 * string, etc.)
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2010-08-13 16:26:29 +0200 (Fri, 13 Aug 2010) $
 * $Revision: 167 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/CoronisLib.java $
 */
package com.coronis;

import com.coronis.exception.*;
import com.dipole.libs.Functions;

import java.util.*;


public class CoronisLib {

	public static String version = "1.1.0-dev";
	
	/**
	 * TODO : this should be configurable
	 */
    public static String DATEFORMAT = "dd/MM/yyyy HH:mm";
    
    /**
     * MAX_REPEATERS authorized for the system. 
     * This is a constant constraint from Coronis
     */
    public static int MAX_REPEATERS = 3;

    /**
     * Compute the 16 bit Coronis CRC on a message
     * @param message
     * @return the 16 bit CRC 
     */
    public static int calculateCrc(int[] message) {
        /* Official CRC function proposed by CORONIS in their documentation
         * Implementation cannot be done using byte types because their range is between -128 and 127.
         * Thus, we are working with shorts 
         */

        int crc = 0;
        int byteCount;
        int messageLength = message.length;
        int theByte;
        int theBit;
        final int poly = 0x8408;

        for (byteCount = 0; byteCount < messageLength; byteCount++) {
            theByte = message[byteCount];
            crc ^= theByte;
            for (theBit = 0; theBit < 8; theBit++) {
                if ((crc & 0x1) > 0) {
                    crc >>>= 1;
                    crc ^= poly;
                } else {
                    crc >>>= 1;
                }
            }
        }
        return crc;
    }

    /**
     * Parses a Coronis date from an array of int starting at offset.
     * @param msg : int[] message
     * @param offset : offset in the message where to start reading the date
     * @return java.util.Calendar
     */
    public static Calendar parseDateTime(int[] msg, int offset) {
        return CoronisLib.createCalendar(
        		msg[offset + 2] + 2000, 
        		msg[offset + 1], 
        		msg[offset], 
        		msg[offset + 4], 
        		msg[offset + 5]);
    }

    /**
     * Returns a calendar instance using a date and hour definition
     * @param year
     * @param month
     * @param dayOfMonth
     * @param hour
     * @param minute
     * @return java.util.Calendar
     */
    public static Calendar createCalendar(int year, int month, int dayOfMonth,
            int hour, int minute) {
        // GregorianCalendar is 0 based for month !
        Calendar cld = Calendar.getInstance();
        cld.set(Calendar.YEAR, year);
        cld.set(Calendar.MONTH, month - 1);
        cld.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cld.set(Calendar.HOUR_OF_DAY, hour);
        cld.set(Calendar.MINUTE, minute);
        cld.set(Calendar.SECOND, 0);
        return cld;
    }

    /**
     * Extract the datalogging frequency from a byte and returns the frequency in milliseconds
     * @param abyte frequency byte
     * @return frequency in milliseconds
     */
    public static int parseDataloggingFrequency(int abyte) {
        /*
         * Returns the frequency in milliseconds
         */
        // timeUnit is the last two bits of the byte
        int timeUnit = abyte & 0x3;
        // measurePeriod is the first 6 bits of the byte
        int measurePeriod = abyte >> 2;
        int dtlFreq = 0;
        switch (timeUnit) {
            case 0x0:
                dtlFreq = 1 * measurePeriod;
                break;
            case 0x1:
                dtlFreq = 5 * measurePeriod;
                break;
            case 0x2:
                dtlFreq = 15 * measurePeriod;
                break;
            case 0x3:
                dtlFreq = 30 * measurePeriod;
        }
        return dtlFreq * 60 * 1000;
    }
    
    /**
     * Returns the datalogging frequency byte for the given milliseconds frequency
     * @param freqmilli frequency in milliseconds
     * @return datalogging frequency byte
     * @throws ConfigException
     */
    public static int getDataloggingFrequency(int freqmilli) throws ConfigException {
    	int minutes = freqmilli / (1000 * 60);    	
    	if (minutes > 63 * 30) {
    		throw new ConfigException("Maximal frequency is 31h 30'. Please correct your configuration file");
    	}
    	
    	if ((minutes % 30 == 0) && (minutes / 30 != 1)) {      
    		return ((minutes / 30) <<  2) | 0x03;
    	} else if ((minutes % 15 == 0) && (minutes / 15 != 1)) {    	
    		return 0x02 | ((minutes / 15) <<  2);
    	} else if ((minutes % 5 == 0) && (minutes / 5 != 1)) {
    		return 0x01 | ((minutes / 5) <<  2);
    	} else {
    		return 0x00 | (minutes <<  2);
    	}
    }

    /**
     * Convert string module id into a table of int as used in the Coronis protocol.
     * <p>
     * More generally, this function allow to convert a String into its int[] value
     * 
     * @param identification
     * @return The moduleID as a int array
     * @throws ConfigException
     */
    public static int[] moduleIdFromString(String identification) throws ConfigException{
        if (identification.length() % 2 > 0) {
        	throw new ConfigException("moduleIdFromString : Module id must be even");
        }
        int[] modid = new int[identification.length() / 2];
        for (int i = 0; i < identification.length(); i += 2) {
            String substr = identification.substring(i, i + 2);
            modid[i / 2] = Integer.parseInt(substr, 16);
        }

        return modid;
    }
    
    /**
     * Convert an RSSI byte into an RSSI percentage. 
     * 
     * RSSI percentage has an upper bound of 100%.
     * @param rssi
     * @param maximum
     * @return An integer to represent RSSI percentage
     */
    public static int getRSSIPercentage(int rssi, int maximum) {
        int pct = (int)Math.ceil( (rssi * 1.0 / maximum) * 100 );
        // percentage cannot be higher than 100%
        // if maximum is 0x32, Coronis support says that all the values 
        // between 0x20 and 0x32 are only saturation. Thus, code uses a maximum 
        // value of 0X20 --> that means we can have value > 100.
        return (pct > 100) ? 100 : pct;
    }
    
    /**
     * Return the module type name using its module type byte
     * 
     * @param moduleType
     * @return A module type as a String
     */
    public static String getModuleTypeString(int moduleType) {
    	switch (moduleType) {
	        case 0x19:
	            return ("Wavetherm Dallas");
	        case 0x33:
	            return ("Wavetherm Dallas (US version)");
	        case 0x29:
	            return ("Wavetherm PT100");
	        case 0x28:
	            return ("Wavetherm PT1000");
	        case 0x16:
	            return ("Waveflow");
	        case 0x15:
	            return ("WaveTalk"); 
            case 0x22:
                return ("WaveSense 0-5V / WaveTank");
            case 0x23:
                return ("WaveSense 4-20mA");
	        case 0x3A:
	            return ("WaveTank");
	        default:
	            return ("Unknown module. Typs is " + Functions.printHumanHex(moduleType, true));
	    }    	
    }
}


