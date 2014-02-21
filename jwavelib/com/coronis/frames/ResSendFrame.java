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
 * $Date: 2008-11-19 13:39:28 +0100 (Wed, 19 Nov 2008) $
 * $Revision: 13 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/frames/ResSendFrame.java $
 */
package com.coronis.frames;

public class ResSendFrame extends CoronisFrame {

	public ResSendFrame(int command, int[] message) {
		super(command, message);
		// TODO Auto-generated constructor stub
	}
	
	
	public boolean checkMessage() {
		/*
		 * DATA contains 1 byte that is the emission status
		 * 	- 0 is emission frame format OK
		 *  - 1 is emission frame format error
		 */
		return (data[0] == 0) ? true : false;
	}

	public boolean getStatus() {
		/*
		 * DATA contains 1 byte that is the emission status
		 * 	- 0 is emission frame format OK
		 *  - 1 is emission frame format error
		 */
		return (data[0] == 0) ? true : false;
	}
	
	public boolean mustACK() {
		return true;
	}

}
