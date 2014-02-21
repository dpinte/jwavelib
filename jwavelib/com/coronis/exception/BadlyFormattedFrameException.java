/*
 * BadlyFormattedFrameException.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * Coronis badly formatted frame exception : mainly raised when there was a
 * communication problem reading the serial input.
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-06-01 14:37:14 +0200 (Mon, 01 Jun 2009) $
 * $Revision: 73 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/exception/BadlyFormattedFrameException.java $
 */


package com.coronis.exception;

/**
 * This type of exception is thrown by the state machine parsing the Coronis
 * frames on the InputStream. Generally meaning that the CRC was faulty
 * 
 * @author dpinte
 */
public class BadlyFormattedFrameException extends CoronisException {

	public BadlyFormattedFrameException(String message) {
		super(message);
	}
}