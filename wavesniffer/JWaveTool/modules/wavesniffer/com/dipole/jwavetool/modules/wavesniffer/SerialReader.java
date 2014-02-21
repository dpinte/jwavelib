package com.dipole.jwavetool.modules.wavesniffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TooManyListenersException;

import com.coronis.frames.CoronisFrame;

import com.dipole.jwavetool.frame.FrameContainer;
import com.dipole.jwavetool.frame.FrameFactory;
import com.dipole.jwavetool.frame.SnifferFrameInterface;
import com.dipole.jwavetool.modules.AbstractModule;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

enum FrameState {
	sync, stx, len, cmd, data, crc, etx
}

public class SerialReader implements 	Runnable,
										SerialPortEventListener {

	private AbstractModule parent;
	private FrameContainer container;
	private InputStream streamIn;
	private String portName;
	private int baudrate;
	private CommPortIdentifier sPortID;
	private SerialPort sPort;
	private ArrayList<Integer> modBody = new ArrayList<Integer>();
	private ArrayList<Integer> modTimeStamp = new ArrayList<Integer>();
	private ArrayList<Integer> terBody = new ArrayList<Integer>();
	private ArrayList<Integer> terTimeStamp = new ArrayList<Integer>();
	private FrameState modState = FrameState.sync;
	private FrameState terState = FrameState.sync;
	private int modLen = 0;
	private int terLen = 0;
	private int[] modHead = new int[3];
	private int[] modFoot = new int[3];
	private int[] terHead = new int[3];
	private int[] terFoot = new int[3];
	private boolean isSynchronized = false;
	private boolean isConnected = false;
	
	public SerialReader() {}

	public SerialReader(AbstractModule parent, FrameContainer container, String portName, int baudrate){
		this.parent = parent;
		this.portName = portName;
		this.baudrate = baudrate;
		this.container = container;
	}
	
	public void connect(){
		try {
			this.sPortID = CommPortIdentifier.getPortIdentifier(this.portName);
			this.sPort = (SerialPort)this.sPortID.open("WaveSniffer Analyser", 1000);
			
			this.sPort.setInputBufferSize(1000);
			//this.sPort.disableReceiveTimeout();
			//this.sPort.disableReceiveThreshold();
			
			this.sPort.setSerialPortParams(this.baudrate,
											SerialPort.DATABITS_8,
											SerialPort.STOPBITS_1,
											SerialPort.PARITY_NONE);
			
			this.sPort.addEventListener(this);
			this.sPort.notifyOnDataAvailable(true);
			
			this.streamIn = this.sPort.getInputStream();
			
			this.isConnected = true;
		} catch (NoSuchPortException e){
			this.parent.fireModulerError(this.parent.getModuleName(),
										e.toString());
			
		} catch (PortInUseException e){
			this.parent.fireModulerError(this.parent.getModuleName(),
										e.toString());
			
		} catch (UnsupportedCommOperationException e){
			this.parent.fireModulerError(this.parent.getModuleName(),
										e.toString());
			
		} catch (TooManyListenersException e) {
			this.parent.fireModulerError(this.parent.getModuleName(),
										e.toString());
			
		} catch (IOException e) {
			this.parent.fireModulerError(this.parent.getModuleName(),
										e.toString());
		}
	}
	
	public void disconnect(){
		if(this.isConnected){
			try {
				this.streamIn.close();
			} catch (IOException e) {
				//e.printStackTrace();
				this.parent.fireModulerError(this.parent.getModuleName(),
												e.toString());
			}
			
			this.sPort.close();
			this.sPort = null;
			this.isConnected = false;
		}
		this.parent.fireModuleStatus(this.parent.getModuleName(),
									"Serial port closed");
	}
	
	@Override
	public void run() {
		this.connect();
		
		if(this.isConnected){
			this.parent.fireModuleStatus(this.parent.getModuleName(),
										"Waiting fo synchronization with sniffer");
			try{
				while(true){
					Thread.sleep(10000);
				}
			} catch (InterruptedException e){
				this.disconnect();
			}
		}
	}

	// TODO: add timeout
	public boolean waitForSync() throws IOException {
		int rByte;
		boolean sync = false;
		
		while (!sync) {
			rByte = this.streamIn.read();
			if (rByte == SnifferFrameInterface.SNI_SYN) {
				rByte = this.streamIn.read();

				if (rByte == SnifferFrameInterface.SNI_SYN) {
					rByte = this.streamIn.read();

					if (rByte == SnifferFrameInterface.SNI_SYN) {
						sync = true;
					}
				}
			}
		}
		return sync;
	}
	
	public void readFrame() throws IOException {
		boolean gotFrame = false;
		int curDir;
		int[] rByte = new int[3];
		FrameState frameState;
		ArrayList<Integer> tmpBody;
		ArrayList<Integer> tmpTimeStamp;
		int tmpLen;
		int[] tmpHead = new int[3];
		int[] tmpFoot = new int[3];

		rByte[0] = this.streamIn.read();
		rByte[1] = this.streamIn.read();
		rByte[2] = this.streamIn.read();
		//System.out.println(""+ rByte[0] +" "+ rByte[1] +" "+ rByte[2]);
		
		if ((rByte[0] & 0xFF) == SnifferFrameInterface.SNI_FROM_MOD) {
			curDir = SnifferFrameInterface.SNI_FROM_MOD;
			frameState = this.modState;
			tmpBody = this.modBody;
			tmpTimeStamp = this.modTimeStamp;
			tmpLen = this.modLen;
			
			for(int i = 0; i < 3; i++){
				tmpHead[i] = this.modHead[i];
				tmpFoot[i] = this.modFoot[i];
			}
		} else {
			curDir = SnifferFrameInterface.SNI_FROM_TER;
			frameState = this.terState;
			tmpBody = this.terBody;
			tmpTimeStamp = this.terTimeStamp;
			tmpLen = this.terLen;
			
			for(int i = 0; i < 3; i++){
				tmpHead[i] = this.terHead[i];
				tmpFoot[i] = this.terFoot[i];
			}
		}

		switch (frameState) {
			case sync:
			//	System.out.println("    enter SYNC");
				if ((rByte[1] & 0xFF) == CoronisFrame.CRN_SYN) {
			//		System.out.println("    SYNC OK");
					tmpHead[0] = (rByte[1] & 0xFF);
					frameState = FrameState.stx;
					tmpTimeStamp.add(rByte[2] & 0xFF);
				}
				break;

			case stx:
			//	System.out.println("    enter STX");
				frameState = FrameState.len;
				tmpHead[1] = (rByte[1] & 0xFF);
				tmpTimeStamp.add(rByte[2] & 0xFF);
				
				if(tmpHead[1] != CoronisFrame.CRN_STX){
					frameState = FrameState.sync;
					gotFrame = true;
				}
				break;

			case len:
			//	System.out.print("    enter LEN");
				frameState = FrameState.cmd;
				tmpLen = (rByte[1] & 0xFF) - 4;
				tmpHead[2] = (rByte[1] & 0xFF);
				tmpTimeStamp.add(rByte[2] & 0xFF);
				break;

			case cmd:
			//	System.out.println("    enter CMD");
				tmpBody.add((rByte[1] & 0xFF));
				tmpTimeStamp.add(rByte[2] & 0xFF);
				if(tmpLen > 0){
					frameState = FrameState.data;
				} else {
					frameState = FrameState.crc;
				}
				break;

			case data:
			//	System.out.println("    enter DATA");
				tmpBody.add(rByte[1] & 0xFF);
				tmpTimeStamp.add(rByte[2] & 0xFF);
				tmpLen--;
				if (tmpLen <= 0) {
					tmpLen = 0;
					frameState = FrameState.crc;
				}
				break;

			case crc:
			//	System.out.println("    enter CRC");
				tmpFoot[tmpLen] = (rByte[1] & 0xFF);
				tmpTimeStamp.add(rByte[2] & 0xFF);
				tmpLen++;
				if(tmpLen == 2){
					frameState = FrameState.etx;
				}
				break;
				
			case etx:
			//	System.out.println("    enter ETX");
				frameState = FrameState.sync;
				tmpFoot[2] = (rByte[1] & 0xFF);
				tmpTimeStamp.add(rByte[2] & 0xFF);
				gotFrame = true;
				break;
		}
		
		if(gotFrame){
			int[] bodyArray = new int[tmpBody.size()];
			int i = 0;
			for(Integer value : tmpBody){
				bodyArray[i++] = value;
			}
			int[] tsArray = new int[tmpTimeStamp.size()];
			i = 0;
			for(Integer value : tmpTimeStamp){
				tsArray[i++] = value;
			}
			
			SnifferFrameInterface frame = FrameFactory.buildFrameFromArray(tmpHead, bodyArray, tmpFoot, tsArray, curDir);
			
			this.container.addFrame(frame);
			tmpBody.clear();
			tmpTimeStamp.clear();
			tmpFoot = new int[3];
			tmpHead = new int[3];
			tmpLen = 0;
		}
		
		if (curDir == SnifferFrameInterface.SNI_FROM_MOD) {
			this.modState = frameState;
			this.modLen = tmpLen;
			
			for(int i = 0; i < 3; i++){
				this.modHead[i] = tmpHead[i];
				this.modFoot[i] = tmpFoot[i];
			}
		} else {
			this.terState = frameState;
			this.terLen = tmpLen;
			
			for(int i = 0; i < 3; i++){
				this.terHead[i] = tmpHead[i];
				this.terFoot[i] = tmpFoot[i];
			}
		}
	}
	
	public void setStreamIn(InputStream stream){
		this.streamIn = stream;
	}
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		switch(event.getEventType()){
			case SerialPortEvent.BI:
			case SerialPortEvent.CD:
			case SerialPortEvent.PE:
			case SerialPortEvent.FE:
			case SerialPortEvent.OE:
			case SerialPortEvent.DSR:
			case SerialPortEvent.CTS:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				break;
				
			case SerialPortEvent.DATA_AVAILABLE:
				try {
					if(this.isSynchronized){
						this.readFrame();
					} else {
						this.isSynchronized = this.waitForSync();
						this.parent.fireModuleStatus(this.parent.getModuleName(), 
													"Synchronized with sniffer");
					}
				} catch (IOException e){
					this.parent.fireModulerError(this.parent.getModuleName(), 
												"Data Available: "+ e.toString());
				}
				break;	
		}
	}
}
