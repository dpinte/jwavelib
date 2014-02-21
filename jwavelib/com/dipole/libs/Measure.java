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
 * $Date: 2008-11-19 16:43:46 +0100 (Wed, 19 Nov 2008) $
 * $Revision: 14 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/dipole/libs/Measure.java $
 */
package com.dipole.libs;

import java.util.Date;

/**
 *
 * @author did
 */
public class Measure {

    private double _value;
    private Date   _tstamp;
    private double[] _values;
    public static final String SEPARATOR = ";";

    /** Creates a new instance of Measure */
    public Measure(double val, Date timestamp) {
        _value = val;
        _tstamp = timestamp;
    }
    
    public Measure (double[] vlist, Date timestamp) {
        _values = vlist;
        _tstamp = timestamp;         
    }
    
    public int valLength() {
        if (_values != null) {
            return _values.length;
        } else return 1;
    }
    
    public double[] getValues() {
        return _values;
    }
    
    public void setValues(double[] vals) {
        _values = vals;
    }
    
    public double getValue() {
        return _value;
    }
    
    public Date getDateTime() {
    	return _tstamp;
    }
    
    public String toString() {
        StringBuffer stbf = new StringBuffer();  
        // add time in milliseconds
        //TODO : extract separator to config or method argument
        stbf.append(_tstamp.getTime() + SEPARATOR);         
        if (_values != null) {            
            for (int i = 0; i < _values.length; i++) {
                if (_values[i] == Double.NaN) stbf.append(Constants.MISSING_DATA);
                else stbf.append(_values[i]);
                if (i < (_values.length -1) )stbf.append(SEPARATOR);
            }    
        } else {                    
            if (_value == Double.NaN) stbf.append(Constants.MISSING_DATA);
            else stbf.append(_value);
        }
        return stbf.toString();
    }
}
