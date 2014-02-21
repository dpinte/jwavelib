package com.coronis.test.virtualComp;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;

import com.coronis.frames.CoronisFrame;
import com.coronis.logging.Logger;
import com.coronis.modules.WavePort;
import com.coronis.modules.WavePortEventProcessor;
import com.dipole.libs.Functions;

public class VirtualWavePort extends WavePort {
	
	private boolean _connected = false;
	private byte[] _input;
	private LinkedList _responses;
	
	 /** Creates a new instance of StandardSerialWavePort */
    public VirtualWavePort(String name, String sport) {
		super(name, sport);
		_input = new byte[CoronisFrame.FRAME_MAX_LENGHT];
	}
	
	@Override
	public boolean connect() {		
		_connected = ! _connected;
		if (_connected) {
	        this.openStreams();    
	        //this.startListener();
		}
		return _connected;
	}
	
    public void startListener() {
        super.startListener();
        System.out.println("Listener started");
    }	
	
    public void openStreams() {
        
        // Opening the streams    	
        outputStream = new ByteArrayOutputStream();
        inputStream = new ByteArrayInputStream(_input);       
        
        fwriter = new VirtualCoronisFrameWriter(outputStream, this);       
        return;
    }	

	@Override
	public boolean disconnect() {
		_connected = ! _connected;
		return _connected;
	}

	public void load_response(LinkedList commands) {
		/*
		 * Load a vector of sleep - CMD to answer
		 */
		this._responses = commands;
		
	}
	
	@Override
	public void run() {
		System.out.println("In the run method");
		if (_responses == null) {
			System.out.println("Error : now responses loaded");
			return;
		}
		//		 this method starts an infinite loop that will permanently read input from the waveport
        _isListening = true;
        do {
            try {
            	Action action;
            	try {
            		action = (Action)this._responses.removeFirst();
            	} catch (NoSuchElementException  e) {
            		System.out.println("No more responses");
            		return;
            	} 
                //wait
            	try {
                    Thread.sleep(action.waittime);
                } catch (InterruptedException ex) {                        
                }   
            	// do action
            	CoronisFrame cfr = action.cfr;
            	System.out.println(action.cfr);
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
                        Logger.debug("EVT " + Functions.printHumanHex(cfr.getCmd(), false));
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
                Logger.warning("IOException in listener - probably stopping thread");
                break; 
            }
        } while(_isListening == true);
        System.out.println("Out of the method ...");
        return;
	}

	@Override
	public void stopListener() {
        _isListening = false;
        Logger.debug("Waiting for listener to stop : " + _isListening);
        try {
        	inputListener.interrupt();
        	Thread.sleep(25);        	
        	if (inputListener.isAlive()) {
        		System.out.println("Thread still alive ...");
        		inputListener.join();        		
        	}        	            
        } catch (InterruptedException ex) {
            Logger.error("InterruptedException while stopping thread" + ex.getMessage());
        }        
        Logger.debug("Listener stopped");
	}

}
