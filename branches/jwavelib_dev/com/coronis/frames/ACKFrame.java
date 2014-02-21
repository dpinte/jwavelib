/*
 * ACKFrame.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * ACK frame class : very simple child of CoronisFrame
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-06-01 14:37:14 +0200 (Mon, 01 Jun 2009) $
 * $Revision: 73 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/frames/ACKFrame.java $
 */
package com.coronis.frames;

/**
 * ACKFrame class
 * @author dpinte
 */
public class ACKFrame extends CoronisFrame {
	public ACKFrame(int cmd, int[] msg) {
		super(cmd, msg);
	}
}
