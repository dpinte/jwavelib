/*
 * ReceivedMultiFrame.java
 *
 * Created on 15 octobre 2008, 14:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.coronis.frames;


import com.dipole.libs.Functions;

/**
 *
 * @author dpinte
 */
public class ReceivedMultiFrame extends CoronisFrame {
    

	public ReceivedMultiFrame(int command, int[] message) {
		super(command, message);		
	}
	
	public boolean mustACK() {
		return true;
	}
        
        public int getStatus() {
            return data[0];
        }
        
        public int getTotalFramesReceived() {
            return data[1];
        }
        
        public int getFrameIndex() {
            return data[2];
        }
        
        public boolean isAllFrameReceived() {
            return (data[2] == 1) ? true : false;
        }
        
        public String getModuleId() {
            StringBuffer stbf = new StringBuffer();
            for (int i = 0; i < 6; i++) stbf.append(Functions.printHumanHex(data[i+3], false));
            return stbf.toString();
        }
    
}
