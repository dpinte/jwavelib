/*
 * WaveTalk.java
 *
 * Created on April 24, 2008, 9:16 PM
 *
 */

package com.coronis.modules;

/**
 * 
 * @author did
 */
public class WaveTalk extends Module {

	private int _max_rssi;
	private int _min_rssi;

	public WaveTalk(int[] moduleId) {
		super(moduleId);
	}

	public WaveTalk(int[] moduleId, String modName) {
		super(moduleId, modName);
	}

	public String readType(int[] msg) {
		String res = super.readType(msg);
		if (this._rssi > this._max_rssi)
			this._max_rssi = this._rssi;
		if (this._min_rssi == 0 || this._rssi < this._min_rssi)
			this._min_rssi = this._rssi;
		return res;
	}

	public int getMinRSSI() {
		return _min_rssi;
	}

	public int getMaxRSSI() {
		return _max_rssi;
	}
}
