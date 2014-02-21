/*
 * ErrorFrame.java
 *
 * Created on 2 juillet 2008, 14:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.coronis.frames;

/**
 *Class for CMD_ERROR frames (0x00)
 * @author dpinte
 */
public class ErrorFrame extends CoronisFrame {
    
	public ErrorFrame(int cmd, int[] msg) {
		super(cmd, msg);
	}
        
	public boolean mustACK() {
		return true;
	}           
        
}
