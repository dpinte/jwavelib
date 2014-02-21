/**
 * 
 * @author Bertrand Antoine
 *
 */
package com.dipole.jwavetool.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

import com.dipole.jwavetool.frame.FrameFactory;
import com.dipole.jwavetool.frame.FrameContainer;
import com.dipole.jwavetool.frame.SnifferFrameInterface;

public class CsvHandler {

	//public CsvHandler() {}

	/**
	 * SAve each record in the frame container to a csv file
	 * FIXME: handle " char: OOo put " on each fields
	 * @param filePath	The path of the csv file
	 */
	public static void saveContainerToCsv(final String filePath) {
		Log.trace("Enter CsvHandler.saveContainerToCsv: "+ filePath);
		
		FrameContainer container = FrameContainer.getInstance();
		ArrayList <SnifferFrameInterface> frameList = container.getFrameList();
		FileWriter writer;
		StringBuffer buffer;
		
		try {
			writer = new FileWriter(filePath);
			
			writer.append("date,");
			writer.append("Frame,");
			writer.append("Direction,");
			writer.append("TimeStamp");
			writer.append('\n');
			
			writer.flush();
			
			buffer = new StringBuffer();
			for(SnifferFrameInterface frame : frameList) {
				
				writer.append(frame.getDateTime(true));
				writer.append(',');
				writer.append(frame.getSniffedFrame());
				writer.append(',');
				writer.append(Integer.toHexString(frame.getDirection()));
				writer.append(',');
				
				buffer.append('{');
				
				/* put all timeStamp in a array followed by a '/' */
				for(int tstp : frame.getTimeStamp()) {
					buffer.append(tstp);
					buffer.append('/');
				}
				
				/* remove the last '/' */
				if(buffer.charAt(buffer.length() - 1) == '/') {
					buffer.deleteCharAt(buffer.length() - 1);
				}

				buffer.append('}');
				
				writer.append(buffer.toString());
				writer.append('\n');
				
				writer.flush();
				buffer.delete(0, buffer.length());
			}
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.trace("Quit CsvHandler.saveContainerToCsv");
	}
	
	/**
	 * Read a csv file and load each record in the frame container
	 * @param filePath	The path of the csv file
	 */
	public static void loadContainerFromCsv(final String filePath) {
		Log.trace("Enter CsvHandler.loadContainerFromCsv: "+ filePath);
		
		FrameContainer container = FrameContainer.getInstance();
		SnifferFrameInterface frame;
		DataInputStream dis;
		BufferedReader reader;
		String line;
		String[] strArr;
		boolean firstLine = true;
		
		//FIXME: add a test to check if the file is valid
		try {
			dis = new DataInputStream(new FileInputStream(filePath));
			reader = new BufferedReader(new InputStreamReader(dis));
			
			/* read the file line per line until the end */
			while((line = reader.readLine()) != null) {
				/* ignore the first line */
				if (!firstLine) {
					strArr = line.split(",");
					
					Log.debug(strArr[0] +" "+ strArr[1] +" "+ strArr[2] +" "+ strArr[3] +" ");
					
					frame = FrameFactory.buildFrameFromString(strArr[1], strArr[3], strArr[2]);
					frame.setDateTime(strArr[0]);
					
					container.addFrame(frame);
				} else {
					firstLine = false;
				}
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.trace("Quit CsvHandler.loadContainerFromCsv\n");
	}
}
