/*
 * ReceivedFrame.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * Received frame class : very simple child of CoronisFrame
 * 
 * This type of frame must be ACK.
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-06-01 14:37:14 +0200 (Mon, 01 Jun 2009) $
 * $Revision: 73 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/frames/ReceivedFrame.java $
 */
package com.coronis.frames;

import com.dipole.libs.Functions;

public class ReceivedFrame extends CoronisFrame {


	public ReceivedFrame(int command, int[] message) {
		super(command, message);
		// TODO Auto-generated constructor stub
	}
	
	public boolean mustACK() {
		return true;
	}
        
        public String getModuleId() {
            StringBuffer stbf = new StringBuffer();
            for (int i = 0; i < 6; i++) stbf.append(Functions.printHumanHex(data[i], false));
            return stbf.toString();
        }

}
