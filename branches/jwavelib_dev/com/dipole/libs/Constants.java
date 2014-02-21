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
 * $Date: 2009-07-28 12:42:29 +0200 (Tue, 28 Jul 2009) $
 * $Revision: 134 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/dipole/libs/Constants.java $
 */

package com.dipole.libs;

public class Constants {        
    // Wake-up time is the number of milliseconds between wake up of the mainloop
    // Default is every minute
    public static int     WAKE_UP_TIME         = 1000 * 60; // 1 minute
    public static String  DEFAULT_FTP_PORT     = "21";

    public static String MISSING_DATA = "MISSING";

    public static String EWON_OUTPUT_DIRECTORY = "/usr/coronis";
}
