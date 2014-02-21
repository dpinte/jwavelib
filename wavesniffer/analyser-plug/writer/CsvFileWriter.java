package writer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import common.Log;

import frame.*;

public class CsvFileWriter {
	private String filePath;
	private FrameContainer container;
	
	public CsvFileWriter(final String filePath, final FrameContainer container) {
		this.filePath = filePath;
		this.container = container;
	}

	public void exportToCsv(){
		//SnifferFrameInterface frame;
		final ArrayList <SnifferFrameInterface> frameList = this.container.getFrameList();
		FileWriter writer;
		StringBuffer buffer;
		
		try{
			writer = new FileWriter(this.filePath);
			
			writer.append("date,");
			writer.append("Frame,");
			writer.append("Direction,");
			writer.append("TimeStamp");
			writer.append('\n');
			
			writer.flush();
			
			for(SnifferFrameInterface frame : frameList){
				
				writer.append(frame.getDateTime(true));
				writer.append(',');
				writer.append(frame.getSniffedFrame());
				writer.append(',');
				writer.append(Integer.toHexString(frame.getDirection()));
				writer.append(',');
				
				buffer = new StringBuffer();
				buffer.append('{');
				
				/* put all timeStamp in a array followed by a '/' */
				for(int tstp : frame.getTimeStamp()){
					buffer.append(tstp);
					buffer.append('/');
				}
				
				/* remove the last '/' */
				if(buffer.charAt(buffer.length() - 1) == '/') {
					buffer.deleteCharAt(buffer.length() - 1);
				}

				writer.append(buffer.toString());
				writer.append('\n');
				
				writer.flush();
			}
			
			writer.close();
		} catch (IOException e){
			Log.fatal(e.getMessage());
		}
	}
	
	public void setFilePath(final String filePath){
		this.filePath = filePath;
	}
	
	public void setContainer(final FrameContainer container){
		this.container = container;
	}
}
