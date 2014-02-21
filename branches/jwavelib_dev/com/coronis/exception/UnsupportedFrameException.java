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
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/exception/UnsupportedFrameException.java $
 */
package com.coronis.exception;

import com.coronis.frames.CoronisFrame;

/**
 * Thrown by te CoronisFrameBuilder when the CMD is not supported
 * @author dpinte
 */
public class UnsupportedFrameException extends CoronisException {

	public UnsupportedFrameException(String message) {
		super(message);
	}
        
	public UnsupportedFrameException(String message, CoronisFrame cfr) {
		super(message);
	}  
}