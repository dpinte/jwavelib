/*
 * Functions.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * Functions is a static class grouping convenient functions used everywere in 
 * the libs.
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-07-08 12:40:34 +0200 (Wed, 08 Jul 2009) $
 * $Revision: 98 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/dipole/libs/Functions.java $
 */

package com.dipole.libs;

import java.util.*;

/**
 *
 * @author did
 */
public class Functions {
    
    public static String formatInt(int val) {
        return (val < 10) ? ("0" + val) : String.valueOf(val);
    }    
    
    /**
     * Convert an array of byte into a human readable Hexadecimal string
     * @param msg The array ob byte
     * @param with_prefix Add prefix
     * @return A human readable string of the array
     */
    public static String printHumanHex(int[] msg, boolean with_prefix) {
        String prefix = (with_prefix == true) ? "0x" : "";
        StringBuffer stbf = new StringBuffer();
        for (int i = 0; i < msg.length; i++) {
            String val = Integer.toHexString(msg[i]).toUpperCase();
            if (val.length() % 2 > 0) {
                stbf.append(prefix + "0" + val);
            } else {
                stbf.append(prefix + val);
            }
        }
        return stbf.toString();
    }
    
    /**
     * Convert a byte into a human readable Hexadecimal string
     * @param msg The byte
     * @param with_prefix Add prefix
     * @return A human readable string of the byte
     */
    public static String printHumanHex(int msg, boolean with_prefix) {
        String prefix = (with_prefix == true) ? "0x" : "";
        StringBuffer stbf = new StringBuffer();
        String val = Integer.toHexString(msg).toUpperCase();
        if (val.length() % 2 > 0) {
            stbf.append(prefix + "0" + val);
        } else {
            stbf.append(prefix + val);
        }
        return stbf.toString();
    }    
    
    public static String formatDate(Date dt) {
        Calendar cld = Calendar.getInstance();
        cld.setTime(dt);
        StringBuffer stbf = new StringBuffer();
        int day = cld.get(Calendar.DAY_OF_MONTH);
        int month = cld.get(Calendar.MONTH);
        int year = cld.get(Calendar.YEAR);
        int hour = cld.get(Calendar.HOUR_OF_DAY);
        int minute = cld.get(Calendar.MINUTE);
        stbf.append(formatInt(day));
        stbf.append("/");
        stbf.append(formatInt(month));
        stbf.append("/");
        stbf.append(formatInt(year));
        stbf.append(" ");
        stbf.append(formatInt(hour));
        stbf.append(":");
        stbf.append(formatInt(minute));
        stbf.append(":00");

        return stbf.toString();
    }    
}
