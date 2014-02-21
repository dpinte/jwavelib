package reader;

import events.ReaderErrorListener;
import events.ReaderStatusListener;
import frame.FrameContainer;

public class ReaderManager {
	public static final int CSV_FILE = 1;
	
	private FrameContainer container;
	private Thread serialThread;
	private SerialReader serialReader;
	private CsvFileReader csvReader;
	
	public ReaderManager(FrameContainer container){
		this.container = container;
	}
	
	public void loadFileReader(int fileType, String filePath,
								ReaderStatusListener statusL, ReaderErrorListener errorL){
		switch(fileType){
			case CSV_FILE:
				this.csvReader = new CsvFileReader(filePath, this.container);
				this.csvReader.addReaderErrorListener(errorL);
				this.csvReader.addReaderStatusListener(statusL);
				this.csvReader.run();
				break;
		}
	}
	
	public void loadSerialReader(FrameContainer container, String portName, int baudrate,
									ReaderStatusListener statusL, ReaderErrorListener errorL){
		this.serialReader = new SerialReader(container, portName, baudrate);
		this.serialReader.addReaderErrorListener(errorL);
		this.serialReader.addReaderStatusListener(statusL);
		
		this.serialThread = new Thread(serialReader);
		this.serialThread.start();
	}
	
	public void closeSerialReader(){
		this.serialThread.interrupt();
	}
}
