/*
 * MissingDataException.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * Missing data exception class.
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-06-01 14:37:14 +0200 (Mon, 01 Jun 2009) $
 * $Revision: 73 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/exception/MissingDataException.java $
 */
package com.coronis.exception;

/**
 * Exception thrown when some missing data are found where it should not appear !
 * 
 * @author dpinte
 */
public class MissingDataException extends CoronisException {

	public MissingDataException() {
		// TODO Auto-generated constructor stub
	}

	public MissingDataException(String arg0) {
		super(arg0);	
	}

}
