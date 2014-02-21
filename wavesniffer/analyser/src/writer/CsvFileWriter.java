package writer;

import java.io.FileWriter;
import java.io.IOException;

import frame.*;

public class CsvFileWriter {
	private String filePath;
	private FrameContainer container;
	
	public CsvFileWriter(String filePath, FrameContainer container) {
		this.filePath = filePath;
		this.container = container;
	}

	public void exportToCsv(){
		try{
			FileWriter writer = new FileWriter(this.filePath);
			
			writer.append("date,");
			writer.append("Frame,");
			writer.append("Direction,");
			writer.append("TimeStamp");
			writer.append('\n');
			
			writer.flush();
			
			for(int i = 0; i < this.container.getTotalFrames(); i++){
				SnifferFrameInterface frame = this.container.getFrameAt(i);
				
				writer.append(frame.getDateTime(true));
				writer.append(',');
				writer.append(frame.getSniffedFrame());
				writer.append(',');
				writer.append(Integer.toHexString(frame.getDirection()));
				writer.append(',');
				
				String tsStr = "{";
				int[] tsTmp = frame.getTimeStamp();
				for(int tsI = 0; tsI < tsTmp.length; tsI++){
					tsStr = tsStr + Integer.toString(tsTmp[tsI]);
					if(tsI < tsTmp.length - 1){
						tsStr = tsStr + "/";
					}
				}
				tsStr = tsStr + "}";
				
				writer.append(tsStr);
				writer.append('\n');
				
				writer.flush();
			}
			
			writer.close();
		} catch (IOException e){
			System.err.println(e.toString());
		}
	}
	
	public void setFilePath(String filePath){
		this.filePath = filePath;
	}
	
	public void setContainer(FrameContainer container){
		this.container = container;
	}
}
