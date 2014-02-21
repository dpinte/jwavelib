/**
 * 
 */
package com.coronis.modules;

import com.coronis.exception.BadlyFormattedFrameException;
import com.coronis.exception.MissingDataException;
import com.coronis.logging.Logger;

/**
 * Class to represent a Dalas WaveTherm
 * <p>
 * Known limitations:<br>
 * <ul>
 * <li> Support only timestep datalogging
 * <li> Support only on sensors. If both sensors are enabled, the value are 
 * 		mixed in the dataSet
 * </ul>
 */
public class WaveThermDalas extends WaveTherm {

	private int module_type = 0x19;
	
	/**
	 * Creates a new Dalas WaveTherm
	 * 
     * @param moduleId The radio address of the module
	 * @param wpt The WavePort use with this module
	 * @param modRepeaters The repeaters to reach the module
	 */
	public WaveThermDalas(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
		super(moduleId, wpt, modRepeaters);
	}

    /**
     * Creates a new Dalas WaveTherm
     * 
     * @param modname The module name
     * @param freqMinute The datalogging frequency of the module (in minutes)
     * @param moduleId The radio address of the module
     * @param modRepeaters The repeaters to reach the module
     * @param wpt The WavePort use with this module
     */
	public WaveThermDalas(String modName, int freqMinute, int[] moduleId,
			WaveTalk[] modRepeaters, WavePort wpt) {
		super(modName, freqMinute, moduleId, modRepeaters, wpt);
		// TODO Auto-generated constructor stub
	}
	
	public int getModuleType(){
		return this.module_type;
	}

	/**
	 * See http://datasheets.maxim-ic.com/en/ds/DS18B20.pdf for details
	 */
	protected double parseTemperature(int[] temp) throws MissingDataException {
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
        int fullbyte = (temp[0] << 8) | temp[1];
        
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

	/* (non-Javadoc)
	 * @see com.coronis.modules.WaveTherm#setMaxValues()
	 */
	protected void setMaxValues() {
		this.MAX_EXTENDED_DATALOG_COUNT = 4500;    	
		this.MAX_DATA_PER_SINGLEFRAME = 59;
		this.MAX_DATALOG_COUNT = 48;
		this.valLength = 2;
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.WaveTherm#readCurrentValues(int[])
	 */
	protected double[] readCurrentValues(int[] msg) throws BadlyFormattedFrameException {
		/*
		 * Frames format:
		 * ===============
		 * 
		 * DALAS:
		 * -------
		 * 0     6     7      8        9      11      13
		 * | mod | ACK | mode | status | TempA | Temp B |
		 * 
		 * - For Temp A & B: if not set = 0x4FFF
		 * 
		 * PT-100 / PT-1000:
		 * ------------------
		 * 0     6     7      8        9       13       17
		 * | mod | ACK | mode | status | Temp A | Temp B | 
		 * 
		 * - for Temp B: if not set = 0xFFFFFFFF
		 * 
		 * - ID		: module ID					6 bytes	
		 * - ACK	: applicative command ACK	1 byte
		 * - mode	: application mode			1 byte
		 * - status	: operating status			1 byte
		 * - Temp A	: probe A					2 / 4 bytes
		 * - Temp B : probe B					2 / 4 bytes
		 * 
		 */
		double[] measures = new double[2];
		int baseIdx = Message.ID_LENGTH;
		
        // check if we got an 0x81
        if (msg[baseIdx] != Message.ACK_GET_PRB) {
        	throw new BadlyFormattedFrameException(
                    "Extended datalogging ACK is not correct. Got :"
            		+ Integer.toHexString(msg[baseIdx]) +" and received "
            		+ Integer.toHexString(Message.ACK_GET_PRB));
        }
        
        this.checkStatusAndMode(msg[baseIdx + 1], msg[baseIdx + 2]);
        
        int[] val = new int[this.valLength];
		try {
			/* read Temp A */
			val[0] = msg[baseIdx + 3];
			val[1] = msg[baseIdx + 4];
			
			if((val[0] != 0x4F) && (val[1] != 0xFF)) {
				measures[0] = this.parseTemperature(val);
			} else {
				measures[0] = Double.NaN; 
			}
				
			/* read Temp B */
			val[0] = msg[baseIdx + 5];
			val[1] = msg[baseIdx + 6];
			
			if((val[0] != 0x4F) && (val[1] != 0xFF)) {
				measures[1] = this.parseTemperature(val);
			} else {
				measures[1] = Double.NaN;
			}
		} catch (MissingDataException e) {
			Logger.info(e.getMessage());
		}
		
		return measures;
	}	
}
