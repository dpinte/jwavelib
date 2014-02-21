package reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import frame.FrameFactory;
import frame.FrameContainer;
import frame.SnifferFrameInterface;

public class CsvFileReader extends AbstractReader {

	private String filePath;
	private BufferedReader reader;
	private boolean eof = false;
	private boolean firstLine = true;

	public CsvFileReader(String filePath, FrameContainer container){
		this.filePath = filePath;
		this.container = container;
	}
	
	public void run(){
		this.fireReaderStatus("ReaderStarted");
		
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(this.filePath));
			this.reader = new BufferedReader(new InputStreamReader(dis));
		} catch (IOException e){
			this.fireReaderError("Unable to onpenFile"+ this.filePath);
		}
		this.fireReaderStatus("file openned");
		
		try {
			while(!this.eof){
				this.container.addFrame(this.readFrame());
			}
		} catch (IOException e){
			this.fireReaderError(e.toString());
		}
		
		try{
			this.reader.close();
		} catch (IOException e){
			this.fireReaderError(e.toString());
		}
		
	}
	
	public SnifferFrameInterface readFrame() throws IOException {
		SnifferFrameInterface frame = null;
		String line;
		String[] strArr;
		
		line = this.reader.readLine();
		if(line == null) {
			this.eof = true;
		} else {
			/* ignore the first line in a csv */
			if(this.firstLine){
				this.firstLine = false;
			} else {
				strArr = line.split(",");
				frame = FrameFactory.buildFrameFromString(strArr[1], strArr[3], strArr[2]);
				frame.setDateTime(strArr[0]);
			}
		}
		
		return frame;
	}
}
