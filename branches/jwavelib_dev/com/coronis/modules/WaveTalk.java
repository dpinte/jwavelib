/*
 * WaveTalk.java
 *
 * Created on April 24, 2008, 9:16 PM
 *
 */

package com.coronis.modules;

import com.coronis.exception.CoronisException;
import com.coronis.logging.Logger;

/**
 * Class to represent a WaveTalk
 */
public class WaveTalk extends Module {

	private int _max_rssi;
	private int _min_rssi;
	
	private int module_type = 0x15;

	/**
	 * Creates a new WaveTalk
	 * 
     * @param moduleId The radio address of the module
	 */
	public WaveTalk(int[] moduleId) {
		super(moduleId);
	}

	/**
	 * Creates a new WaveTalk
	 * 
     * @param moduleId The radio address of the module
     * @param modname The module name
	 */
	public WaveTalk(int[] moduleId, String modName) {
		super(moduleId, modName);
	}
	
    public int getModuleType(){
    	return this.module_type;
    }	

	public void readType(int[] msg) throws CoronisException{
		try {
                    super.readType(msg);
                } catch (CoronisException e) {
                    // FIXME : means not the right type 
                    //         do skip it at the moment
                    Logger.warning("FIXME : WaveTalk module_type is not the correct one. Making as if ...");
                }
		
		if (this._rssi > this._max_rssi)
			this._max_rssi = this._rssi;
		if (this._min_rssi == 0 || this._rssi < this._min_rssi)
			this._min_rssi = this._rssi;
	}

	/**
	 * Get the minimum RSSI
	 * 
	 * @return An Integer
	 */
	public int getMinRSSI() {
		return _min_rssi;
	}

	/**
	 * Get the maximum RSSI
	 * 
	 * @return An Integer
	 */
	public int getMaxRSSI() {
		return _max_rssi;
	}
}
