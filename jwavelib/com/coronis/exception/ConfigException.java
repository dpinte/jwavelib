/*
 * ConfigException.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * Configuration exception class
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-06-01 14:37:14 +0200 (Mon, 01 Jun 2009) $
 * $Revision: 73 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/exception/ConfigException.java $
 */
package com.coronis.exception;

/**
 * Exception thrown when their is something not 
 * configured correctly (missing paramater, bad value, etc.)
 * 
 * @author dpinte
 */
public class ConfigException extends CoronisException {
    
    public ConfigException() {}
    
    public ConfigException(String arg0) { 
        super(arg0);
    }

}
