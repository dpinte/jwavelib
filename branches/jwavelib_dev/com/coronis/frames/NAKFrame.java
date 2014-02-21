/*
 * NAKFrame.java
 *
 * Created on April 25, 2008, 11:39 AM
 *
 * NAK frame class : very simple child of CoronisFrame
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2008-11-19 13:39:28 +0100 (Wed, 19 Nov 2008) $
 * $Revision: 13 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/frames/NAKFrame.java $
 */

package com.coronis.frames;

/**
 *
 * @author did
 */
public class NAKFrame extends CoronisFrame {
    
	public NAKFrame(int cmd, int[] msg) {
		super(cmd, msg);
	}
        
        
}