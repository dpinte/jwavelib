package com.dipole.jwavetool.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.dipole.jwavetool.frame.Description;
import com.dipole.jwavetool.frame.FrameRelation;

public class Common {
	
	public static final String NAME = "JWaveTool";
	public static final String VERSION = "0.9";
	
	public static final String RC_PATH = "/com/dipole/jwavetool/resources/";
	public static final String XML_PATH = "/com/dipole/jwavetool/resources/xml/";
	public static final String ICONS_16_PATH = "/com/dipole/jwavetool/resources/icons/16x16/";
	public static final String ICONS_22_PATH = "/com/dipole/jwavetool/resources/icons/22x22/";
	
	public static HashMap <Integer, Description> cmdDescription;
	public static HashMap <Integer, Description> paramDescription;
	public static HashMap <Integer, FrameRelation> cmdRelation;
		
	public static int[] integerArrayToIntArray(final Integer[] array) {
		if(array == null || array.length == 0) {
			return new int[0];
		}
		
		int[] tmp = new int[array.length];
		
		for(int i = 0; i < array.length; i++) {
			tmp[i] = array[i].intValue();
		}
		
		return tmp;
	}
	
	public static String getCurentDateTime() {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		DateFormat timeFormat = new SimpleDateFormat("hh':'mm':'ss");
		Date now = new Date();
		
		return dateFormat.format(now) + " " +timeFormat.format(now);
	}
}
