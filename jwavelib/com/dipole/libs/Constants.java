/*
 * Constants.java
 *
 * Created on 31 octobre 2007, 17:56
 *
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * Constants class defined generic constants through all the application.
 *
 * $Date: 2008-11-19 16:43:46 +0100 (Wed, 19 Nov 2008) $
 * $Revision: 14 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/dipole/libs/Constants.java $
 */

package com.dipole.libs;

public class Constants {
	public static boolean DEBUG                = false;
	public static boolean DEBUG_CORONIS_FRAMES = false;
        
        // Wake-up time is the number of milliseconds between wake up of the mainloop
        // Default is every minute
        public static int     WAKE_UP_TIME         = 1000 * 60; // 1 minute
        public static String  DEFAULT_FTP_PORT     = "21";
        
        public static String MISSING_DATA = "MISSING";
        
        public static String EWON_OUTPUT_DIRECTORY = "/usr/coronis";
}
