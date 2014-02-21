/*
 * WavePortFirmwareRequest.java
 *
 * Created on 4 juillet 2008, 19:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package com.coronis.modules.requests;


import com.coronis.frames.*;
import com.coronis.logging.Logger;
import com.coronis.modules.*;
import com.coronis.exception.*;

import java.io.IOException;


/**
 *
 * @author dpinte
 */
public class WavePortFirmwareRequest extends Request {

    
    /** Creates a new instance of PointToPointRequest */
    public WavePortFirmwareRequest(WavePort wpt, int[] msg, int allowedTime) {
         super(wpt, msg, allowedTime); 
    }
    
    public void subscribe() {
        _wpt.subscribe(CoronisFrame.ACK, this);
        _wpt.subscribe(CoronisFrame.RES_FIRMWARE_VERSION, this);  
        _wpt.subscribe(CoronisFrame.ERROR, this);         
    }
    
    public void unsubscribe() {
        _wpt.unsubscribe(CoronisFrame.ACK, this);
        _wpt.unsubscribe(CoronisFrame.RES_FIRMWARE_VERSION, this);  
        _wpt.unsubscribe(CoronisFrame.ERROR, this);         
    }    
    
    public boolean process() throws IOException, CoronisException {
       
        this.subscribe();
        
        _gotACK = false;
        _gotAnswer = false;
        _timeOut = false;   
        _gotCmdError = false;
        
        try {
            int time = 0;
            // three retries of the same frame can be send
            for (int i =0; i < 3; i++) {
                _wpt.send(CoronisFrame.REQ_FIRMWARE_VERSION, _message);
                time = waitForAck(time);
                if (_gotACK) break;
            }
            if (_gotACK == false) throw new NoAckException("No ACK answer received from WavePort");              
            do {
                if (_gotAnswer) return true;
                if (_gotCmdError == true) throw new CommandException("Command error. Reprocess the request");
                Thread.sleep(Request.SLEEP_TIME);
                time += Request.SLEEP_TIME;
            } while (time < _allowedTime );
             _timeOut = true;           
        } catch (InterruptedException e) {
            Logger.error("InterruptedException while waiting during set waveport parameter request");
            return false;
        }
        return _gotAnswer;
    }
    
    
    public void event(CoronisFrame crf) {
        switch (crf.getCmd()) {
            case CoronisFrame.ACK : 
                _gotACK = true;
                _wpt.unsubscribe(CoronisFrame.ACK, this);
                return;
            case CoronisFrame.RES_FIRMWARE_VERSION:               
                 _gotAnswer = true;
                receivedFrame = crf;                               
                 _wpt.unsubscribe(CoronisFrame.RES_FIRMWARE_VERSION, this);
                _wpt.unsubscribe(CoronisFrame.ERROR, this);                
                return;
            case CoronisFrame.ERROR:
                // receive a command error -> means we have to resend all the request to the waveport !
                _gotCmdError = true;
                return;                                
        }
    }
}
