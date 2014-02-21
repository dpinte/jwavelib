package cli;

import common.Settings;

//import reader.ReaderManager;

import events.FrameContainerListener;
import events.ReaderErrorListener;
import events.ReaderStatusListener;

import frame.FrameContainer;

public class CLI implements FrameContainerListener, ReaderStatusListener, ReaderErrorListener {
	private FrameContainer container;
	//private ReaderManager readers;
	private Settings settings;
	private int nbFrame;
	
	public CLI(FrameContainer container, Settings settings) {
		this.container = container;
	//	this.readers = new ReaderManager(this.container);
		this.settings = settings;
		this.nbFrame = 0;
		
		this.container.addFrameContainerListener(this);
		this.run();
	}
	
	public void run(){
	//	this.readers.loadSerialReader(	this.container,
	//									this.settings.getString(Settings.SPORT_NAME),
	//									this.settings.getInt(Settings.SPORT_BAUD),
	//									this, this);
	}
	
	public void close(){
	//	this.readers.closeSerialReader();
	}
	
	public void frameAdded(){
		System.out.println(	this.nbFrame +" : "+ 
							this.container.getLastFrame().getSniffedFrame() + " | "+
							this.container.getLastFrame().getDirection());
		this.nbFrame++;
	}
	
	public void readerStatus(String message){
		System.out.println("    "+ message);
	}
	
	public void readerError(String message){
		System.err.println("    "+ message);
	}

	@Override
	public void framesFiltered(int[] i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void containerCleared() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void framesHighlighted(int[] frameInd) {
		// TODO Auto-generated method stub
		
	}
}
