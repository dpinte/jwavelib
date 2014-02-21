/*
 * UnsupportedFrameException.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * Coronis unsupported frame exception is raised when reading frame types that
 * are not supported by this version of the code.
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-06-01 14:37:14 +0200 (Mon, 01 Jun 2009) $
 * $Revision: 73 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/exception/TransmissionException.java $
 */
package com.coronis.exception;

import com.coronis.frames.CoronisFrame;
import com.coronis.frames.TransmissionErrorFrame;

/**
 * Thrown when a transmission exception has been received (0x31)
 * @author dpinte
 *
 */
public class TransmissionException extends CoronisException {
    
    private CoronisFrame cframe;

	public TransmissionException(String message) {
		super(message);
	}
        
	public TransmissionException(String message, CoronisFrame cfr) {
		super(message);
                cframe = cfr;
	}
        

    public String getMessage() {
        if (cframe != null && cframe instanceof TransmissionErrorFrame) {
            try {
                return super.getMessage() + "\n" + ((TransmissionErrorFrame)cframe).getErrorMessage();
            } catch (CoronisException ex) {
                return ex.toString();
            }
        } else return super.getMessage();
    }
}
