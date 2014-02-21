/*
 * ResRemoteRSSIFrame.java
 *
 * Created on 23 juin 2008, 13:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.coronis.frames;

/**
 *
 * @author dpinte
 */
public class ResRemoteRSSIFrame extends CoronisFrame {
    
    /** Creates a new instance of ResRemoteRSSIFrame */
	public ResRemoteRSSIFrame(int command, int[] message) {
		super(command, message);
		// TODO Auto-generated constructor stub
	}
    
    public double getRSSI() {
        /*
         * DATA contains 1 byte with RSSI in %
         */
        return (data[0] * 1.0 / 0x2F) * 100;
    }
    
    public boolean mustACK() {
        return true;
    }
    
    
}
