/*
 * ResSendFrame.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * RES_SEND_FRAME class : very simple child of CoronisFrame
 * 
 * Checks that the message is well formatted and that the WavePort has 
 * verified that the frame format was ok to be sent to the module.
 * 
 * This type of frame must be ACK.
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-06-09 23:50:13 +0200 (Tue, 09 Jun 2009) $
 * $Revision: 84 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/frames/WavePortFirmwareFrame.java $
 */
package com.coronis.frames;

import com.coronis.exception.BadlyFormattedFrameException;
import com.dipole.libs.Functions;

public class WavePortFirmwareFrame extends CoronisFrame {


	public WavePortFirmwareFrame(int command, int[] message) {
		super(command, message);            	
	}
        
        public String getFirmware() throws BadlyFormattedFrameException{
            // FIXME : this should send an exception !
           if (data[0] != 'V') throw new BadlyFormattedFrameException("Firmware data should start with the V character");
           int transmission_mode = data[1] << 8;
           transmission_mode = transmission_mode | data[2];
           int firmware = data[3] << 8;
           firmware = firmware | data[4];
           return "V-" + Functions.printHumanHex(transmission_mode, false) + "-" + Functions.printHumanHex(firmware, false);
        }
	
	public boolean mustACK() {
		return true;
	}

}
