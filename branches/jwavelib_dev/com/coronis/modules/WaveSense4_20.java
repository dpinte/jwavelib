/**
 * 
 */
package com.coronis.modules;

/**
 * Class to represent a WaveSense module
 */
public class WaveSense4_20 extends WaveSense {

	private int module_type = 0x23;
	
	/**
	 * Creates a new WaveSense 4-20 mA
	 * 
     * @param moduleId The radio address of the module
	 * @param wpt The WavePort use with this module
	 * @param modRepeaters The repeaters to reach the module
	 */
	public WaveSense4_20(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
		super(moduleId, wpt, modRepeaters);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a new WaveSense 4-20 mA
	 * 
     * @param modname The module name
     * @param freqMinute The datalogging frequency of the module (in minutes)
     * @param moduleId The radio address of the module
     * @param modRepeaters The repeaters to reach the module
     * @param wpt The WavePort use with this module
	 */
	public WaveSense4_20(String modName, int freqMinute, int[] moduleId, WaveTalk[] modRepeaters, WavePort wpt) {
		super(modName, freqMinute, moduleId, modRepeaters, wpt);
		// TODO Auto-generated constructor stub
	}
	
    public int getModuleType(){
    	return this.module_type;
    }	

	/**
	 * Parse the value:
	 * <p>
	 * Possible result: <ul>
	 * 					<li> 0.0        : No sensors of sernsors fault (0xEEEE)
	 * 					<li> Double.NaN : Value out of range (> 20mA)
	 * 					</ul>
	 * 
	 * @param MSB MSB byte
	 * @param LSB LSB byte
	 * @return The value in mA
	 */
	protected double parseValue(int MSB, int LSB) {
		double res;

    	/*
    	 * The level is a 12-bit value.
    	 * max = 0x0FFF (100%)
    	 */
		if((MSB == 0xEE) && (LSB == 0XEE)) {
    		res = 0;
    	} else if (MSB > 0x0F) {
    		res = Double.NaN;
    	} else {
    		int value = (MSB << 8) | (LSB & 0xFF);
    		res = ((double) value / 256) + 4;
    	}

    	return res;
	}
}
