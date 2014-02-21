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
 * $Date: 2009-07-08 12:40:34 +0200 (Wed, 08 Jul 2009) $
 * $Revision: 98 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/CoronisLib.java $
 */
package com.coronis;

import com.coronis.exception.*;
import com.dipole.libs.Functions;

import java.util.*;


public class CoronisLib {

	public static String version = "1.0.3";
	
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
     * Parses a Dallas temperature from an MSB and LSB to provide a double value as result
     * 
     * See http://datasheets.maxim-ic.com/en/ds/DS18B20.pdf for details
     * 
     * @param MSB
     * @param LSB
     * @return
     * @throws MissingDataException
     */
    public static double parseDallasTemp(int MSB, int LSB) throws MissingDataException {
        /*
         * From : http://datasheets.maxim-ic.com/en/ds/DS18B20.pdf The core
         * functionality of the DS18B20 is its direct-to-digital temperature
         * sensor. The resolution of the temperature sensor is user-configurable
         * to 9, 10, 11, or 12 bits, corresponding to increments of 0.5°C,
         * 0.25°C, 0.125°C, and 0.0625°C, respectively. The default resolution
         * at power-up is 12-bit. The DS18B20 powers-up in a low-power idle
         * state; to initiate a temperature measurement and A-to-D conversion,
         * the master must issue a Convert T [44h] command. Following the
         * conversion, the resulting thermal data is stored in the 2-byte
         * temperature register in the scratchpad memory and the DS18B20 returns
         * to its idle state. If the DS18B20 is powered by an external supply,
         * the master can issue “read time slots? (see the 1- WIRE BUS SYSTEM
         * section) after the Convert T command and the DS18B20 will respond by
         * transmitting 0 while the temperature conversion is in progress and 1
         * when the conversion is done. If the DS18B20 is powered with parasite
         * power, this notification technique cannot be used since the bus must
         * be pulled high by a strong pullup during the entire temperature
         * conversion. The bus requirements for parasite power are explained in
         * detail in the POWERING THE DS18B20 section of this datasheet. The
         * DS18B20 output temperature data is calibrated in degrees centigrade;
         * for Fahrenheit applications, a lookup table or conversion routine
         * must be used. The temperature data is stored as a 16-bit
         * sign-extended two’s complement number in the temperature register
         * (see Figure 2). The sign bits (S) indicate if the temperature is
         * positive or negative: for positive numbers S = 0 and for negative
         * numbers S = 1. If the DS18B20 is configured for 12-bit resolution,
         * all bits in the temperature register will contain valid data. For
         * 11-bit resolution, bit 0 is undefined. For 10-bit resolution, bits 1
         * and 0 are undefined, and for 9-bit resolution bits 2, 1 and 0 are
         * undefined. TEMPERATURE REGISTER FORMAT - Power of 2 for each byte LS
         * Byte bit 7 bit 6 bit 5 bit 4 bit 3 bit 2 bit 1 bit 0 3 2 1 0 -1 -2 -3
         * -4 MS Byte bit 15 bit 14 bit 13 bit 12 bit 11 bit 10 bit 9 bit 8 S S
         * S S 6 5 4
         * 
         */
        // merge MSB and LSB
        int fullbyte = (MSB << 8) | LSB;

        int NODATA = (0xFF << 8) | 0xFF;
        if (fullbyte == NODATA) {
            throw new MissingDataException("Data is missing");
        }
        // Sign check
        boolean isNegative = false;
        if ((fullbyte & (0xF0 << 8)) > 0) {
            // this is a negative value --> compute the extended two's complement
            fullbyte = (~fullbyte) + 1;
            isNegative = true;
        }

        double temperature = 0;
        // Math.pow does not exist in J2ME ! --> Math.pow(2,-2) is converted to it's real value'
        temperature += (fullbyte & 1) * 0.0625;
        temperature += ((fullbyte & 2) >> 1) * 0.125;
        temperature += ((fullbyte & 4) >> 2) * 0.25;
        temperature += ((fullbyte & 8) >> 3) * 0.5;
        temperature += ((fullbyte & 16) >> 4) * 1;
        temperature += ((fullbyte & 32) >> 5) * 2;
        temperature += ((fullbyte & 64) >> 6) * 4;
        temperature += ((fullbyte & 128) >> 7) * 8;
        temperature += ((fullbyte & 256) >> 8) * 16;
        temperature += ((fullbyte & 512) >> 9) * 32;
        temperature += ((fullbyte & 1024) >> 10) * 64;
        temperature += ((fullbyte & 0x2048) >> 11) * 128;
        if (temperature > 125 ){
            throw new MissingDataException("Invalid data exception. Temperature is higher than 125 degrees centigrade");
        } 
        if (isNegative && temperature > 55) {
            throw new MissingDataException("Invalid data exception. Temperature is lower than -55 degrees centigrade");
        }
        return (isNegative) ? -temperature : temperature;
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
     * 
     * More generally, this function allow to convert a String into its int[] value
     * @param identification
     * @return
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
     * @return
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
     * @param moduleType
     * @return
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


