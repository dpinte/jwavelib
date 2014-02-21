/*
 * NoAckException.java
 *
 * Created on 9 septembre 2008, 17:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.coronis.exception;

/**
 * Exception thrown when a NACK frame is received.
 * 
 * @author dpinte
 */
public class NoAckException extends CoronisException {
     
	public NoAckException() {
		super();
	}       

	public NoAckException(String message) {
		super(message);
	}            
}
