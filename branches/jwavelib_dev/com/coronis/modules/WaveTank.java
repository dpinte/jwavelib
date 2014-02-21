/**
 * 
 */
package com.coronis.modules;

/**
 * Class to represent a WaveTank module
 */
public class WaveTank extends WaveSense {

	private int module_type = 0x3A;
	/**
	 * Creates a new WaveTank module
	 * 
     * @param moduleId The radio address of the module
	 * @param wpt The WavePort use with this module
	 * @param modRepeaters The repeaters to reach the module
	 */
	public WaveTank(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
		super(moduleId, wpt, modRepeaters);
	}

	/**
	 * Creates a new WaveTank module
	 * 
     * @param modname The module name
     * @param freqMinute The datalogging frequency of the module (in minutes)
     * @param moduleId The radio address of the module
     * @param modRepeaters The repeaters to reach the module
     * @param wpt The WavePort use with this module
	 */
	public WaveTank(String modName, int freqMinute, int[] moduleId, WaveTalk[] modRepeaters, WavePort wpt) {
		super(modName, freqMinute, moduleId, modRepeaters, wpt);
	}
	
    public int getModuleType(){
    	return this.module_type;
    }	

	/* (non-Javadoc)
	 * @see com.coronis.modules.WaveSense#parseValue(int, int)
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
    		res = 100 * ((double)value / 0x0FFF);
    	}

    	return res;
	}
}
