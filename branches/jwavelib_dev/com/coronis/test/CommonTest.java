/**
 * 
 */
package com.coronis.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.dipole.libs.DataSet;

/**
 * @author antoine
 *
 */
public class CommonTest {

	public static DataSet buildDataSetFromCsv(InputStream stream) {
		BufferedReader reader;
		String line = null;
		String[] lineArr;
		Double val;
		Long date;
		DataSet dataSet = new DataSet(null);
		
		try {
			reader = new BufferedReader(new InputStreamReader(stream));
			
			while((line = reader.readLine()) != null) {
				lineArr = line.split(";");
				
				val = new Double(lineArr[1]);
				date = new Long(lineArr[0]);
				
				dataSet.addMeasure(val.doubleValue(), date.longValue());
			}
			
			//System.out.println(dataSet.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dataSet;
	}
	
	public static int[] msgFromHexString(String msg) {
		int[] intMsg = new int[msg.length() / 2];
		
		/* split frame in byte */
		for(int i = 0, j = 0; i < intMsg.length; i++){
			intMsg[i] = Integer.valueOf(msg.substring(j, j + 2), 16);
			j += 2;
		}

		return intMsg;
	}
}
