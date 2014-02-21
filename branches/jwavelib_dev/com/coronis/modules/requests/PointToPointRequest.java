/*
 * PointToPointRequest.java
 *
 * Created on 4 juillet 2008, 17:13
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
public class PointToPointRequest extends Request {
    
    private boolean _subscribed = false;
    
    /** Creates a new instance of PointToPointRequest */
    public PointToPointRequest(WavePort wpt, int[] msg, int allowedTime, String moduleId) {
        super(wpt, msg, allowedTime, moduleId);
    }
    
    public void subscribe() {
        // subscribe for ACK, RES_SEND_FRAME and RECEIVED_FRAME, ERROR and TR_ERROR_FRAME
        _wpt.subscribe(CoronisFrame.ACK, this);
        _wpt.subscribe(CoronisFrame.RES_SEND_FRAME, this);
        _wpt.subscribe(CoronisFrame.RECEIVED_FRAME, this);
        _wpt.subscribe(CoronisFrame.ERROR, this);     
        _wpt.subscribe(CoronisFrame.TR_ERROR_FRAME, this);      
        _subscribed = true;
    }
    
    public void unsubscribe() {
         if (_subscribed == false) return;
        _wpt.unsubscribe(CoronisFrame.ACK, this);
        _wpt.unsubscribe(CoronisFrame.RES_SEND_FRAME, this);
        _wpt.unsubscribe(CoronisFrame.RECEIVED_FRAME, this);
        _wpt.unsubscribe(CoronisFrame.ERROR, this);        
        _wpt.unsubscribe(CoronisFrame.TR_ERROR_FRAME, this);     
        _subscribed = false;
    }    
    
    public boolean process() throws IOException, CoronisException {
        
        this.subscribe();
        
        _gotACK = false;
        _gotAnswer = false;
        _isReqSendFrameError = false;
        _timeOut = false;
        _gotCmdError = false;
        _gotTransmissionError = false;
        
        Logger.debug("Timeout is : " + _allowedTime / 1000 + " seconds");
        try {
            int time = 0;
            // three retries of the same frame can be send
            for (int i =0; i < 3; i++) {
                _wpt.send(CoronisFrame.REQ_SEND_FRAME, _message);
                time = waitForAck(time);
                if (_gotACK) break;
            } 
            if (_gotACK == false) throw new NoAckException("No ACK answer received from WavePort"); 
            do {
                if (_isReqSendFrameError == true) throw new CoronisException("Bad RES_SEND_FRAME status");
                if (_gotCmdError == true) throw new CommandException("Command error. Reprocess the request");
                if (_gotTransmissionError == true) throw new TransmissionException("Transmission error. Reprocess the request");
                if (_gotAnswer) return true;
                Thread.sleep(Request.SLEEP_TIME);
                time += Request.SLEEP_TIME;
            } while (time < _allowedTime );
             _timeOut = true;           
        } catch (InterruptedException e) {
            Logger.error("InterruptedException while waiting during PTP request");
            return false;
        }
        this.unsubscribe();
        return _gotAnswer;
    }        
    
    public void event(CoronisFrame crf) {
        switch (crf.getCmd()) {
            case CoronisFrame.ACK : 
                _gotACK = true;
                _wpt.unsubscribe(CoronisFrame.ACK, this);
                return;
            case CoronisFrame.RES_SEND_FRAME: 
                if (((ResSendFrame)crf).getStatus() == false) {
                    // the status was not ok --> change the boolean value to false, and remove unneeded listeners
                    _isReqSendFrameError = true;
                    _wpt.unsubscribe(CoronisFrame.ERROR, this);
                    _wpt.unsubscribe(CoronisFrame.TR_ERROR_FRAME, this);                    
                }
                _wpt.unsubscribe(CoronisFrame.RES_SEND_FRAME, this);
                return;
            case CoronisFrame.RECEIVED_FRAME:
                String moduleDestination = ((ReceivedFrame) crf).getModuleId();
                if (moduleDestination.equals(_moduleId)) {
                    _gotAnswer = true;
                    receivedFrame = crf;                               
                    _wpt.unsubscribe(CoronisFrame.RECEIVED_FRAME, this);    
                    _wpt.unsubscribe(CoronisFrame.ERROR, this);
                    _wpt.unsubscribe(CoronisFrame.TR_ERROR_FRAME, this);                     
                } else {
                    Logger.debug("Frame is not for me ... I am " + _moduleId + " and frame is for " + moduleDestination);
                }
                return;
            case CoronisFrame.ERROR:
                // receive a command error -> means we have to resend all the request to the waveport !
                _gotCmdError = true;
                return;
            case CoronisFrame.TR_ERROR_FRAME:
                // receive a Transmission error frame -> means we have to resend all the request to the waveport !
                try {
                	String message =((TransmissionErrorFrame)crf).getErrorMessage(); 
                    Logger.error("Trasnmission error :" + message);
                } catch (CoronisException e) {
                    Logger.error("Error while print transmission error message :" + e.getMessage());
                }
                _gotTransmissionError = true;
                return;                
        }
    }      
}
