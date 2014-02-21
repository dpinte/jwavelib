/*
 * Request.java
 *
 * Created on 7 juillet 2008, 12:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.coronis.modules.requests;

import com.coronis.modules.*;
import com.coronis.frames.*;
import com.coronis.exception.*;

import java.io.*;
        
        
/**
 *
 * @author dpinte
 */
public abstract class Request implements WavePortEventProcessor {
  
    public static int SLEEP_TIME     = 10;
    public static int ACK_SLEEP_TIME = 5;
    
    protected WavePort _wpt;
    protected int _allowedTime; 
    protected boolean _timeOut;
    protected int[] _message;
    protected String _moduleId;
    protected CoronisFrame receivedFrame;
    
    protected boolean _gotAnswer;
    protected boolean _gotCmdError;
    protected boolean _gotACK;
    protected boolean _isReqSendFrameError;
    protected boolean _gotTransmissionError;
    
    
    /** Creates a new instance of Request */
    public Request() {}
    
    public Request(WavePort wpt, int[] msg, int allowedTime) {
        this();
        _wpt = wpt;       
        _message = msg;
        _allowedTime = allowedTime;                     
    }    
    
    public Request(WavePort wpt, int[] msg, int allowedTime, String moduleId) {
        this(wpt, msg, allowedTime);              
        _moduleId = moduleId;        
    }
    
    public abstract void subscribe();
    public abstract void unsubscribe();
    public abstract boolean process() throws IOException, CoronisException;
    
    public CoronisFrame getAnswer() {
        if(_gotAnswer) {
            return receivedFrame;
        } else return null;
    }    
    
    public boolean isTimeOut() {
        return _gotAnswer || _timeOut;
    }    
    
    protected int waitForAck(int time) throws InterruptedException{      
        int initialTime = time;    
        do {
                if (_gotACK) return time;
                Thread.sleep(ACK_SLEEP_TIME);
                time += ACK_SLEEP_TIME;
            } while (time < initialTime + WavePort.ACK_RECEIVED_TIMEOUT); 
        return time;
    }    
}
