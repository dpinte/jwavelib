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
 * $Date: 2008-11-19 16:43:46 +0100 (Wed, 19 Nov 2008) $
 * $Revision: 14 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/dipole/libs/DataSet.java $
 */
package com.dipole.libs;


import java.util.*;

public class DataSet {
	
	private int[] modId;
        private Vector dataset;
        private Calendar lastMeasureDate;
        private boolean isLastMeasureDateSet = false;
       
	public DataSet(int[] moduleId) {
		modId = moduleId;
                // TODO : extract the hardcode value
                dataset = new Vector(96);
        }
        
        public void setLastMeasureDate(Calendar cld) {
            lastMeasureDate = cld;
            isLastMeasureDateSet = true;
        }
        
        public Calendar getLastMeasureDate() {
            return (isLastMeasureDateSet) ? lastMeasureDate : Calendar.getInstance();
        }
        
        public void addMeasure(double value, Date tstamp) {
            dataset.addElement(new Measure(value, tstamp));
        }

        public void addMeasure(double[] values, Date tstamp) {
            dataset.addElement(new Measure(values, tstamp));
        }
        
        public int getLength() {
            return dataset.size();
        }
        
        public Measure getMeasure(int index) {
            return (Measure)dataset.elementAt(index);
        }
        
        public Enumeration enumerate() {
            return dataset.elements();
        }
        
        public String getHeader() {
            StringBuffer stbf = new StringBuffer();
            stbf.append("\"TimeInt\";"); 
            if (dataset.size() == 0) return stbf.toString();
            int valcount = ((Measure)dataset.elementAt(0)).valLength();
            for (int i =0; i < valcount; i++) {
                stbf.append("\"Value" + i + "\";");
            }
            // remove trailing ;
            stbf.deleteCharAt(stbf.length() -1);
            stbf.append("\n");
            return stbf.toString();

      }
        
        public String toString() {
        	StringBuffer stbf = new StringBuffer();
        	stbf.append("Module is :" + Functions.printHumanHex(modId, false) + "\n");
        	for (Enumeration e = enumerate(); e.hasMoreElements();) {
        		stbf.append("\t" +   e.nextElement().toString() + "\n");
        	}
        	return stbf.toString();
        }
        
        public String getModuleId(){ 
        	return Functions.printHumanHex(modId, false);
        }   
        
        public Vector getMeasures() {
            return dataset;
        }
}
