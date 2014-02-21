/**
 * 
 */
package com.coronis.modules;

/**
 * Class to represent a WaveSense module
 */
public class WaveSense5V extends WaveSense {

	private int module_type = 0x22;
	
	/**
	 * Creates a new WaveSense 0-5V
	 * 
     * @param moduleId The radio address of the module
	 * @param wpt The WavePort use with this module
	 * @param modRepeaters The repeaters to reach the module
	 */
	public WaveSense5V(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
		super(moduleId, wpt, modRepeaters);
	}

	/**
	 * Creates a new WaveSense 0-5V
	 * 
     * @param modname The module name
     * @param freqMinute The datalogging frequency of the module (in minutes)
     * @param moduleId The radio address of the module
     * @param modRepeaters The repeaters to reach the module
     * @param wpt The WavePort use with this module
	 */
	public WaveSense5V(String modName, int freqMinute, int[] moduleId,WaveTalk[] modRepeaters, WavePort wpt) {
		super(modName, freqMinute, moduleId, modRepeaters, wpt);
	}
	
    public int getModuleType(){
    	return this.module_type;
    }	

	/**
	 * Parse the value:
	 * <p>
	 * return Double.NaN if value out of range (> 5V)
	 * 
	 * @param MSB MSB byte
	 * @param LSB LSB byte
	 * @return The value in Volt
	 */
	protected double parseValue(int MSB, int LSB) {
		double res;

    	/*
    	 * The level is a 12-bit value.
    	 * max = 0x0FFF (100%)
    	 */
    	if(MSB > 0x0F) {
    		res = Double.NaN;
    	} else {
    		int value = (MSB << 8) | (LSB & 0xFF);
    		res = ((double) value) / 819;
    	}

    	return res;
	}
}
