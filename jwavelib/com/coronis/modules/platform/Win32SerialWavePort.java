/*
 * Win32SerialWavePort.java
 *
 * Created on 31 octobre 2007, 17:56
 * 
 * Win32SerialWavePort is the J2SE implementation of the abstract WavePort
 * class to be used with the the WinJCom library. This implementation is
 * for Windows platforms.
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 *
 * $Date: 2009-06-01 14:37:14 +0200 (Mon, 01 Jun 2009) $
 * $Revision: 73 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/modules/platform/Win32SerialWavePort.java $
 */

package com.coronis.modules.platform;


import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.*;
import java.util.*;

import com.dipole.libs.Functions;



import com.coronis.exception.CoronisException;
import com.coronis.frames.CoronisFrame;
import com.coronis.frames.CoronisFrameReader;
import com.coronis.frames.CoronisFrameWriter;
import com.coronis.logging.SimpleLogger;
import com.coronis.modules.WavePort;
import com.coronis.modules.WavePortEventProcessor;
/**
 *
 * @author did
 */
public class Win32SerialWavePort extends WavePort {

	
	private 	 	CommPortIdentifier 	SPORT_ID;	
	private 	 	SerialPort 		SPORT;
	private         SimpleLogger    logger;
	
    /** Creates a new instance of StandardSerialWavePort */
    public Win32SerialWavePort(String name, String sport) {
		super(name, sport);
	}
	
    public void listAvailable() {
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		while(portEnum.hasMoreElements()) {
			CommPortIdentifier cid = (CommPortIdentifier)portEnum.nextElement();
			logger.log(cid.getName());
		}  	
    }
    
    public CommPortIdentifier getPortIdentifierFromName() {
    	Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
    	while(portEnum.hasMoreElements()){
    		CommPortIdentifier identifier = (CommPortIdentifier)portEnum.nextElement();
		    if (identifier.getName().startsWith(serialPort)) {
			  logger.log("Available port name="+identifier.getName()+" type="+identifier.getPortType());
			  return identifier;
		    }
	    }  
	    return null;
    }
    
	public boolean connect() {
		// Get the serial port and open it
		
		try {
//			Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
//			while(portEnum.hasMoreElements()) {
//				CommPortIdentifier cid = (CommPortIdentifier)portEnum.nextElement();
//				logger.log(cid.getName());
//			}
			SPORT_ID = CommPortIdentifier.getPortIdentifier(serialPort);
		} catch (NoSuchPortException e) {
			logger.error("NoSuchPortException :: " + serialPort);
		}
				
		try {
		    // SPORT_ID.open parameters :
			// - appname - Name of application making this call. This name will become the owner of the port. Useful when resolving ownership contention. 
		    // - timeout - time in milliseconds to block waiting for port open.
			// see javax.comm.CommPortIdentifier class documentation
			SPORT = (SerialPort) SPORT_ID.open("Waveport connection", 100);
		} catch (PortInUseException e) {
			logger.error("PortInUseException :: Port is already in use");
		}

		logger.info(SPORT.toString());
		// Set the port parameters

	    try {
	    	// CORONIS protocol requires : 
	    	//	- asynchrone link RS232 or TTL between host and waveport
	    	//  - 8 bits data, 1 stop bit, no parity
	    	//  - speed : 9600 BAUDS	
	    	logger.log("...");
	    	SPORT.setSerialPortParams(9600, 
	    				       SerialPort.DATABITS_8, 
	    				       SerialPort.STOPBITS_1, 
	    				       SerialPort.PARITY_NONE);
	    	// no flow control on the communication !
	    	SPORT.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			SPORT.enableReceiveTimeout(20);
			SPORT.enableReceiveThreshold(0);	    	
	    //} catch (UnsupportedCommOperationException e) {
	    } catch (Exception e) {
	    	logger.error("UnsupportedCommOperationException :: problem setting the port parameters\n");
	    	try {
				SPORT.setSerialPortParams(9600, 
					       SerialPort.DATABITS_8, 
					       SerialPort.STOPBITS_1, 
					       SerialPort.PARITY_NONE);
				// no flow control on the communication !
		    	SPORT.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);	
		    	
			} catch (UnsupportedCommOperationException e1) {
				// TODO Auto-generated catch block
				logger.error("UnsupportedCommOperationException");
				e1.printStackTrace();
			}
	    }	
	    
//	    try {
//			SPORT.enableReceiveTimeout(5);
//			if (SPORT.isReceiveTimeoutEnabled() == false) {
//				logger.error("Read timeout on serial port is not enabled. Read is blocking ...");
//			}
//		} catch (UnsupportedCommOperationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
			System.out.print("Baudrate : " + SPORT.getBaudRate());			
			   
		isConnected = true;
        this.openStreams();    
        this.startListener();
        return true;		
	}
	
	   public void openStreams() {
	        
	        // Opening the streams
	        try {
	            outputStream = SPORT.getOutputStream();
	        } catch (IOException e) {
	            logger.error("IOException :: error while opening the serial outputstream");
	        }
	        
	        
	        try {
	            inputStream = SPORT.getInputStream();
	        } catch (IOException e) {
	        	logger.error("IOException :: error while opening the serial inputstream");
	        }
	        
	        // Port is opened - initiating frame reader and writer
	        
	        fwriter = new CoronisFrameWriter(outputStream);
	        freader = new CoronisFrameReader(inputStream);
	        freader.emptyBuffer();
	        return;
	    }	
		
	    public void closeStreams() {
	        freader.close();        
	        fwriter.close();
	        System.out.println("Stream closed");
	        return;
	    }		
	
	public synchronized boolean disconnect() {
        if (isConnected) {
        	freader.close();
        	fwriter.close();
    		SPORT.close();   
    		isConnected = false;
        }        
		return true;
	}

	   public void run() {
	        // this method starts an infinite loop that will permanently read input from the waveport
	        _isListening = true;
	        do {
	            try {
	                // read a new coronis frame
	                CoronisFrame cfr = freader.readFrame();
	                if (cfr == null) {
	                	continue;
	                }
	                if (Thread.interrupted()) return;
	                // do something with it
	                if (cfr.mustACK()) {
	                    try {
	                        Thread.sleep(WavePort.ACK_RETURN_TIME);
	                    } catch (InterruptedException ex) {                        
	                    }                    
	                    fwriter.sendACK();
	                }
	                // send events
	                synchronized(eventSubscribers) {
	                    Integer event = new Integer(cfr.getCmd());
	                    if (eventSubscribers.containsKey(event)){
	                        logger.debug("EVT " + Functions.printHumanHex(cfr.getCmd(), false));
	                        Vector subscribers = (Vector)eventSubscribers.get(event);
	                        Enumeration e = subscribers.elements();
	                        while (e.hasMoreElements()) {
	                            WavePortEventProcessor pcs = (WavePortEventProcessor)e.nextElement();
	                            if (pcs.isTimeOut()) subscribers.removeElement(pcs);
	                            else pcs.event(cfr);
	                        }
	                    }                    
	                }
	                                
	            } catch (IOException ex) {
	                logger.warning("IOException in listener - probably stopping thread");
	                break;
	            } catch (CoronisException ex) {
	                logger.error("CoronisException in listener :" + ex.getMessage());
	            }    				
	        } while(_isListening == true);
	        return;
	    }	
	    
	    public void stopListener() {
	        _isListening = false;
	        logger.debug("Waiting for listener to stop : " + _isListening);
	        try {
	        	inputListener.interrupt();
	        	Thread.sleep(25);        	
	        	if (inputListener.isAlive()) {
	        		System.out.println("Thread still alive ...");
	        		inputListener.join();        		
	        	}        	            
	        } catch (InterruptedException ex) {
	            logger.error("InterruptedException while stopping thread" + ex.getMessage());
	        }        
	        logger.debug("Listener stopped");
	    }    

	
	
}
