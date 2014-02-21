/*
 * Measure.java
 *
 * Created on 7 novembre 2007, 16:01
 *
 * Measure class is a very simple class holding a pair of (datetime, value).
 *  
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-07-30 17:48:33 +0200 (Thu, 30 Jul 2009) $
 * $Revision: 148 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/dipole/libs/Measure.java $
 */
package com.dipole.libs;

import java.util.Date;

/**
 * Class to represent a Measure 
 */
public class Measure {
    //TODO : extract separator to config or method argument  
    public static final String SEPARATOR = ";";
    
    private double value;
    private long   tstamp;
    private double[] values;

    /**
     * Creates a new instance of Measure
     *    
     * @param value The measure value
     * @param timestamp The measure timestamp
     */
    public Measure(double value, long timestamp) {
    	this.value = value;
    	this.tstamp = timestamp;
    }
    
    /**
     * Creates a new instance of Measure
     * 
     * @param values The measure values
     * @param timestamp the measure timestamp
     */
    public Measure (double[] values, long timestamp) {
        this.values = values;
        this.tstamp = timestamp;         
    }
    
    /**
     * Get the number of value in the measure
     * 
     * @return An Integer
     */
    public int valLength() {
        if (this.values != null) {
            return this.values.length;
        } else return 1;
    }
    
    /**
     * get the values of the measure
     * 
     * @return A double array
     */
    public double[] getValues() {
        return this.values;
    }
    
    /**
     * Set the values of the measure
     * 
     * @param values The measure values
     */
    public void setValues(double[] values) {
       this.values = values;
    }
    
    /**
     * Get the value of the measure
     * 
     * @return A double
     */
    public double getValue() {
        return this.value;
    }
    
    /**
     * Get the timestamp of the value as a Date
     * 
     * @return A Date
     */
    public Date getDateTime() {
    	return new Date(tstamp);
    }
    
    /**
     * Get the timestamp of the value
     * 
     * @return A long
     */
    public long getTimeStamp() {
    	return this.tstamp;
    }
    
    /**
     * Print the measure with is values and timestamp
     */
    public String toString() {
    	return this.toString(false);
    }
    
    /**
     * Print the measure with is values and timestamp
     * 
     * @param humanDate true to print a human readable timestamp
     * @return
     */
    public String toString(boolean humanDate) {
        StringBuffer stbf = new StringBuffer();

        if(humanDate) {
        	stbf.append(new Date(this.tstamp).toString());
        } else {
        	stbf.append(this.tstamp);
        }
        
        stbf.append(SEPARATOR);
        
        if (this.values != null) {            
            for (int i = 0; i < this.values.length; i++) {
                if (this.values[i] == Double.NaN) {
                	stbf.append(Constants.MISSING_DATA);
                } else {
                	stbf.append(this.values[i]);
                }
                
                if (i < (this.values.length -1)) {
                	stbf.append(SEPARATOR);
                }
            }    
        } else {                    
            if (this.value == Double.NaN){
            	stbf.append(Constants.MISSING_DATA);
            } else {
            	stbf.append(this.value);
            }
        }
        
        return stbf.toString();
    }
}
