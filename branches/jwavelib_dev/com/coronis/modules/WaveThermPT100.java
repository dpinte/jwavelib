/**
 * 
 */
package com.coronis.modules;

import java.io.IOException;
import java.io.InterruptedIOException;

import com.coronis.exception.BadlyFormattedFrameException;
import com.coronis.exception.CoronisException;
import com.coronis.exception.MissingDataException;
import com.coronis.logging.Logger;

/**
 * Class to represent a PT100 or a PT1000 WaveTherm
 * <p>
 * Known limitations:<br>
 * <ul>
 * <li> Support only time stemp datalogging
 * <li> Support only on sensors. If both sensors are enabled, the value are 
 * 		mixed in the dataSet
 * </ul>
 */
public class WaveThermPT100 extends WaveTherm {

	private int module_type = 0x29;
	
	/**
	 * Creates a new PT100 or PT1000 WaveTherm
	 * 
     * @param moduleId The radio address of the module
	 * @param wpt The WavePort use with this module
	 * @param modRepeaters The repeaters to reach the module
	 */
	public WaveThermPT100(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
		super(moduleId, wpt, modRepeaters);
		// TODO Auto-generated constructor stub
	}

    /**
     * Creates a new PT100 or PT1000 WaveTherm
     * 
     * @param modname The module name
     * @param freqMinute The datalogging frequency of the module (in minutes)
     * @param moduleId The radio address of the module
     * @param modRepeaters The repeaters to reach the module
     * @param wpt The WavePort use with this module
     */
	public WaveThermPT100(String modName, int freqMinute, int[] moduleId,
			WaveTalk[] modRepeaters, WavePort wpt) {
		super(modName, freqMinute, moduleId, modRepeaters, wpt);
		// TODO Auto-generated constructor stub
	}
	
    public int getModuleType(){
    	return this.module_type;
    }	
	
	/**
	 * Parse the temperature for a PT100 or a PT1000 probe
	 * <p>
	 * byteArra must be untouched. So the byte order must be LSB first.<br>
	 * See WaveTherm documentation for more information
	 * 
	 * @param byteArr An array with the received bytes.
	 * @return The temperature
	 */
	protected double parseTemperature(int[] temp) throws MissingDataException {
		int bits = temp[3] << 24 | temp[2] << 16 | temp[1] << 8 | temp[0];
		
		Float val = new Float(Float.intBitsToFloat(bits));
		return val.doubleValue();
	}

	/* (non-Javadoc)
	 * @see com.coronis.modules.WaveTherm#setMaxValues()
	 */
	protected void setMaxValues() {
		this.MAX_EXTENDED_DATALOG_COUNT = 2000;
		this.MAX_DATA_PER_SINGLEFRAME = 29;
		this.MAX_DATALOG_COUNT = 24;
		this.valLength = 4;
	}

	/**
     * Request the current value for all sensors as described 
     * in the user guides.
     * <p>
     * The order of each item follow the name of the sensors:<br>
     * 	<ul>
     * 	<li>the item 0 is sensor A
     * 	<li>the item 1 is sensor B
     * 	</ul>
     * 	<p>
     * If a sensor is not activated or it's measure is missing, 
     * the value = Double.NaA
     *  
     * @return An Array of measure
     * @throws IOException 
     * @throws CoronisException 
     * @throws InterruptedIOException 
     */
	// FIXME: set the precision
	public double[] getCurrentValues () throws	InterruptedIOException, CoronisException, IOException {
		double[] values;

		Logger.debug(this.getModuleId() +": request Current values");
        values = readCurrentValues(this.waveport.query_ptp_command(Message.askWaveThermPT100CurrentValue(this._modid, 0x00),
                                                                  this._repeaters,
                                                                  this.getModuleId()));
		
		return values;
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
		int val[];
		int baseIdx = Message.ID_LENGTH;
		
        // check if we got an 0x81
        if (msg[baseIdx] != Message.ACK_GET_PRB) {
        	throw new BadlyFormattedFrameException(
                    "Extended datalogging ACK is not correct. Got :"
            		+ Integer.toHexString(msg[baseIdx]) +" and received "
            		+ Integer.toHexString(Message.ACK_GET_PRB));
        }
        
        this.checkStatusAndMode(msg[baseIdx + 1], msg[baseIdx + 2]);
        
		try {
			val = new int[4];
			/* read Temp A */
			val[0] = msg[baseIdx + 3];
			val[1] = msg[baseIdx + 4];
			val[2] = msg[baseIdx + 5];
			val[3] = msg[baseIdx + 6];
			
			if((val[0] != 0xFF) && (val[1] != 0xFF) && 
				(val[2] != 0xFF) && (val[3] != 0xFF))
				measures[0] = this.parseTemperature(val);
			else
				measures[0] = Double.NaN;
			
			/* read Temp B */
			val[0] = msg[baseIdx + 7];
			val[1] = msg[baseIdx + 8];
			val[2] = msg[baseIdx + 9];
			val[3] = msg[baseIdx + 10];
			
			if((val[0] != 0xFF) && (val[1] != 0xFF) && 
				(val[2] != 0xFF) && (val[3] != 0xFF))
				measures[1] = this.parseTemperature(val);
			else
				measures[1] = Double.NaN;
		} catch (MissingDataException e) {
			Logger.info(e.getMessage());
		}
		
		return measures;
	}
}
