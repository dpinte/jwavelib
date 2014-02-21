/*
 * ResWriteParameterFrame.java
 *
 * Created on April 25, 2008, 11:07 AM
 *
 * RES_WRITE_PARAM class : very simple child of CoronisFrame
 * 
 * Checks that the message is well formatted and that the WavePort has 
 * verified that the frame format was ok to be sent to the module.
 * 
 * This type of frame must be ACK.
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-04-09 09:57:32 +0200 (Thu, 09 Apr 2009) $
 * $Revision: 40 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/frames/ResWriteParameterFrame.java $
 */
package com.coronis.frames;

/**
 *
 * @author did
 */
public class ResWriteParameterFrame extends CoronisFrame {
    
	public ResWriteParameterFrame(int command, int[] message) {
		super(command, message);		
	}

	public boolean getStatus() {
		/*
		 * STATUS
                 *  - 0x00 update OK
                 *  - 0x01 update error
		 */
		return (data[0] == 0) ? true : false;
	}        
        
    public boolean mustACK() {
    	return true;
    }
}
