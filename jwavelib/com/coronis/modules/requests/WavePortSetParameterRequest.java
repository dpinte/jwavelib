/*
 * WavePortRequest.java
 *
 * Created on 4 juillet 2008, 18:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.coronis.modules.requests;


import com.coronis.frames.*;
import com.coronis.modules.*;
import com.coronis.exception.*;

import java.io.IOException;

/**
 *
 * @author dpinte
 */
public class WavePortSetParameterRequest extends Request {
    
 
    // example of message
    // 40 07 01 07 15 07 C0 03 5B 4D 29 03
    // int[] msg = {0x05, 0x19, 0x06, 0x30, 0x2D, 0x69, 0x01 };
    
    /** Creates a new instance of PointToPointRequest */
    public WavePortSetParameterRequest(WavePort wpt, int[] msg, int allowedTime) {
        super(wpt, msg, allowedTime);      
    }
    
    public void subscribe() {
        // subscribe for ACK, RES_SEND_FRAME and RECEIVED_FRAME
        _wpt.subscribe(CoronisFrame.CMD_ACK, this);
        _wpt.subscribe(CoronisFrame.RES_WRIT_PARAM, this);          
        _wpt.subscribe(CoronisFrame.CMD_ERROR, this);         
        _wpt.subscribe(CoronisFrame.TR_ERROR_FRAME, this); 
    }
    
    public void unsubscribe() {
        _wpt.unsubscribe(CoronisFrame.CMD_ACK, this);
        _wpt.unsubscribe(CoronisFrame.RES_WRIT_PARAM, this);
        _wpt.unsubscribe(CoronisFrame.CMD_ERROR, this); 
        _wpt.unsubscribe(CoronisFrame.TR_ERROR_FRAME, this); 
    }
    
    public boolean process() throws IOException, CoronisException {
              
        this.subscribe();
        
        _gotACK = false;
        _gotAnswer = false;
        _timeOut = false;     
        _gotCmdError = false;
        
        int spentTime = 0;
        try {
           
            // three retries of the same frame can be send
            for (int i =0; i < 3; i++) {
                spentTime = 0;
                _wpt.send(CoronisFrame.REQ_WRIT_PARAM, _message);
                spentTime = waitForAck(spentTime);
                if (_gotACK) break;
            }
            if (_gotACK == false) throw new NoAckException("No ACK answer received from WavePort");             
            do {
                if (_gotAnswer) return true;
                if (_gotCmdError == true) {
                    _logger.debug("CMD_ERROR during WavePortSetParameterRequest --> throwing TransmissionException");
                    throw new TransmissionException("Command error. Reprocess the request");
                }
                Thread.sleep(Request.SLEEP_TIME);
                spentTime += Request.SLEEP_TIME;
            } while (spentTime < _allowedTime );
             _timeOut = true;           
        } catch (InterruptedException e) {
            _logger.error("InterruptedException while waiting during set waveport parameter request");
            return false;
        }
        _logger.debug("Write parameter most probably timeout ..."  + spentTime + "/" + _allowedTime);
        return _gotAnswer;
    }  
    
    public void event(CoronisFrame crf) {
        switch (crf.getCmd()) {
            case CoronisFrame.CMD_ACK : 
                _gotACK = true;
                _wpt.unsubscribe(CoronisFrame.CMD_ACK, this);
                return;
            case CoronisFrame.RES_WRIT_PARAM:               
                 _gotAnswer = true;
                receivedFrame = crf;                               
                 _wpt.unsubscribe(CoronisFrame.RES_WRIT_PARAM, this);
                 _wpt.unsubscribe(CoronisFrame.CMD_ERROR, this);
                 _wpt.unsubscribe(CoronisFrame.TR_ERROR_FRAME, this);
                return;
            case CoronisFrame.CMD_ERROR:
                // receive a command error -> means we have to resend all the request to the waveport !
                _gotCmdError = true;
                _logger.debug("CMD_ERROR while trying to set waveport internal parameters");
                return;  
            case CoronisFrame.TR_ERROR_FRAME:
                // receive a Transmission error frame -> means we have to resend all the request to the waveport !
                try {
                    _logger.error(((TransmissionErrorFrame)crf).getErrorMessage());
                } catch (CoronisException e) {
                    _logger.error("Error while printing transmission error message :" + e.getMessage());
                }
                // FIXME : this should set a _gotTransmissionError boolean. It's not a CMD_ERROR !
                _gotCmdError = true;
                return;                    
        }
    }
}
