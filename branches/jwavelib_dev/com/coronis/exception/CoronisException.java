/*
 * CoronisException.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * Base class for all the Coronis exceptions
 * 
 * Author : Didrik Pinte <dpinte@dipole-consulting.com>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-06-01 14:37:14 +0200 (Mon, 01 Jun 2009) $
 * $Revision: 73 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/exception/CoronisException.java $
 */

package com.coronis.exception;

/**
 * Base Coronis exception for the JWaveLib. 
 * That type of exception informs about a problem using 
 * the Coronis protocol.
 * 
 * @author Didrik Pinte <dpinte@dipole-consulting.com>
 */
public class CoronisException extends Exception{
    
	public CoronisException() {
    }
    
    public CoronisException(String arg0) {
        super(arg0);
    }
}
