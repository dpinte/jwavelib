/*
 * TimeOutException.java
 *
 * Created on 4 juillet 2008, 17:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.coronis.exception;


/**
 * Exception thrown when a timeout has been encountered during a WavePort communication
 * 
 * @author dpinte
 */
public class TimeOutException extends CoronisException {
     
	public TimeOutException() {
		super();
	}       

	public TimeOutException(String message) {
		super(message);
	}            
}
