/*
 * CommandException.java
 *
 * Created on 15 septembre 2008, 13:05
 *
 * Thrown only when a 0x00 frame is received from the WavePort
 */

package com.coronis.exception;

import com.coronis.frames.CoronisFrame;

/**
 * Exception thrown when a 0x00 has been received from the WavePort.
 * 
 * A 0x00 is a CMD_ERROR and has no clear explanation from the Coronis documentation
 * 
 * @author dpinte
 */
public class CommandException extends CoronisException {

	public CommandException(String message) {
		super(message);
	}
        
	public CommandException(String message, CoronisFrame cfr) {
		super(message);
	}     
    
}
