/*
* DataSet.java
*
* Created on 31 octobre 2007, 17:36
*
* DataSet is a generic class to hold a set of measure read from a Coronis
* module. It contains a Vector of Measure objects with an initial measure date.
* 
* Author : Didrik Pinte <dpinte@itae.be>
* Copyright : Dipole Consulting SPRL 2008
* 
* $Date: 2009-07-30 13:16:24 +0200 (Thu, 30 Jul 2009) $
* $Revision: 144 $
* $Author: abertrand $
* $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/dipole/libs/DataSet.java $
*/
package com.dipole.libs;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Date;

/**
 * Class to represent a DataLogging table.
 * <p>
 * There are some MetaData that can be useful:<br>
 * <ul>
 * <li> lastReceivedIndex : The index of the more recent received value 
 * 							(default: 0)
 * <li> lastDataLogDate   : The timestamp of the more recent value in the
 * 							DataLogging table (default: 0)
 * <li> creationDate      : The creation timestamp of this DataSet.
 *                          It's updated when clear() as been called
 * <li> dataLogType       : The type of this dataSet.
 * <li> missingValue      : If the dataSet contains some missing values
 * <li> lastMeasure       : The more recent measure received.
 *                          updated ONLY when clean() has been called.
 * </ul>
 *
 */
public class DataSet {
    public static final String ADVANCED_DATALOG = "advanced datalog";
    public static final String DATALOG = "datalog";
    
    private int[] modId;
    private Vector dataSet;

    private int lastReceivedIndex = 0;
    private long lastDataLogDate = 0;
    private long creationDate;
    private String dataLogType = "unknown";
    private boolean missingValues = false;
    private Measure lastMeasure;

    public DataSet(int[] moduleId) {
        this.modId = moduleId;
        // TODO : extract the hardcode value
        this.dataSet = new Vector(96);

        this.creationDate = new Date().getTime();
        this.lastMeasure = new Measure(Double.NaN, this.creationDate);
    }

    /**
     * Add a measure in the DataSet
     * 
     * @param value The measure value
     * @param tstamp The measure timestamp
     */
    public void addMeasure(double value, long tstamp) {
        this.dataSet.addElement(new Measure(value, tstamp));
    }

    /**
     * Add a measure in the DataSet
     * 
     * @param values The measure value
     * @param tstamp The measure timestamp
     */
    public void addMeasure(double[] values, long tstamp) {
        dataSet.addElement(new Measure(values, tstamp));
    }

    /**
     * Get the number of measure recorded in the dataSet
     * 
     * @return length of the dataSet
     */
    public int getLength() {
        return dataSet.size();
    }

    /**
     * Get a measure in the dataSet
     * 
     * @param index the index of the measure in the DataSet
     * @return a measure
     */
    public Measure getMeasure(int index) {
        return (Measure)dataSet.elementAt(index);
    }

    /**
     * Enumerate all elements in the dataSet
     * 
     * @return java.util.Enumeration
     */
    public Enumeration enumerate() {
        return this.dataSet.elements();
    }

    /**
     * Print the dataSet headers
     * 
     * @return A string
     */
    public String getHeader() {
        StringBuffer stbf = new StringBuffer();
        stbf.append("\"TimeInt\";");

        if (this.dataSet.size() > 0) {
            int valcount = ((Measure)dataSet.elementAt(0)).valLength();

            for (int i =0; i < valcount; i++) {
                stbf.append("\"Value" + i + "\";");
            }

            // remove trailing ;
            stbf.deleteCharAt(stbf.length() -1);
            stbf.append("\n");
        }
        return stbf.toString();
    }

    public String toString() {
    	return this.toString(false);
    }
    
    /**
     * Print the dataSet content
     * 
     * @param humanDate true to have a human readable timeStamp
     * @return A String
     */
    public String toString(boolean humanDate) {
    StringBuffer stbf = new StringBuffer();
    stbf.append("Module is :" + Functions.printHumanHex(modId, false) + "\n");

    for (Enumeration e = this.enumerate(); e.hasMoreElements();) {
    	stbf.append("\t" +   ((Measure) e.nextElement()).toString(humanDate) + "\n");
    }

    return stbf.toString();
    }

    /**
     * Get the moduleId of the module 
     * 
     * @return The moduleId as a String
     */
    public String getModuleId(){ 
        return Functions.printHumanHex(this.modId, false);
    }

    /**
     * Get all measures
     *
     * @return java.util.Vector
     */
    public Vector getMeasures() {
        return this.dataSet;
    }

    /**
     * Clear the dataSet and reset all meta data
     */
    public void clear() {
    	this.creationDate = new Date().getTime();
    	
    	if(this.getLength() > 0) {
    		this.lastMeasure = this.getMeasure(0);
    	} else {
    		this.lastMeasure = new Measure(Double.NaN, this.lastDataLogDate);
    	}
    	
        this.missingValues = false;
        
        this.dataSet.removeAllElements();
    }

    /**
     * Get the index in the advanced datalog table for the last recorded measure.
     * 
     * @return an index
     */
    public int getLastReceivedIndex() {
    	return this.lastReceivedIndex;
    }

    /**
     * Set the last received value index
     *
     * @param index
     */
    public void setLastReceivedIndex(int index) {
        this.lastReceivedIndex = index;
    }

    /**
     * Get the the date of the last recorded measure in the datalog
     * 
     * @return timestamp of the last measure 
     */
    public long getLastDataLogDate() {
        return this.lastDataLogDate;
    }

    /**
     * Set the date of the last recorded value in the datalog
     * 
     * @param date
     */
    public void setLastDataLogDate(long date) {
        if(this.getLastDataLogDate() < date)
            this.lastDataLogDate = date;
    }

    /**
     * Get the creation of the dataSet 
     * @return The timeStamp of the dataSet creation
     */
    public long getCreationDate() {
        return this.creationDate;
    }

    /**
     * Get the DataSet type
     * 
     * @return the type as a string
     */
    public String getDataSetType() {
        return this.dataLogType;
    }

    /**
     * Set the dataSet type
     * <p>
     * possible values:	<ul>
     *                  <li>ADVANCED_DATALOG
     *                  <li>DATALOG
     *                  </ul>
     * @param type
     */
    public void setDataSetType(String type) {
        this.dataLogType = type;
    }

    /**
     * Get the more recent measure
     * <p>
     * This field is updated only when the dataSet is cleared.<br>
     * When the DataSet is created, measure return a Long.NaN value and 
     * the curent timestamp
     * 
     * @return A Measure
     */
    public Measure getLastMeasure() {
    	return this.lastMeasure;
    }
    
    /**
     * Set missing value flag
     *
     * @param missing true if some values are missing
     */
    public void setMissingValues(boolean missing) {
        this.missingValues = missing;
    }
    
    /**
     * Check if some value are missing
     *
     * @return true if values missing 
     */
    public boolean hasMissingValues() {
        return this.missingValues;
    }
}
