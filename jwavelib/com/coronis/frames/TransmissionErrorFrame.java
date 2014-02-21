/*
 * ACKFrame.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * ACK frame class : very simple child of CoronisFrame
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2008-11-19 13:39:28 +0100 (Wed, 19 Nov 2008) $
 * $Revision: 13 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/frames/TransmissionErrorFrame.java $
 */
package com.coronis.frames;
import com.dipole.libs.Functions;
import com.coronis.exception.*;

public class TransmissionErrorFrame extends CoronisFrame {
    
        public static final int PTP_MODE   = 0x01;
        public static final int RELAY_MODE = 0x02;
        
	public TransmissionErrorFrame(int cmd, int[] msg) {
		super(cmd, msg);
	}
        
        
        public int getExchangeMode() throws CoronisException {
            switch(data[0]) {
                case PTP_MODE : return PTP_MODE;
                case RELAY_MODE : return RELAY_MODE;
                default: throw new BadlyFormattedFrameException("Invalid EXCHANGE_MODE in error frame");
            }          
        }
        
        public boolean isPtpMode() throws CoronisException {
            return getExchangeMode() == PTP_MODE;
        }
        
        public String getErrorString() throws CoronisException {
            if (isPtpMode()) {                
                if (data[1] == 0x01) return "RF acknowledgement not received from remote module";
                else if (data[1] == 0x02) return "RF response not received from remote module";
                else throw new CoronisException("TransmissionErrorFrame - Invalid error type set in POINT_TO_POINT error message. Value was " + Functions.printHumanHex(data[1], true));
            } else {
                // thus in RELAY_MODE
                if (data[1] != 0x02) throw new CoronisException("TransmissionErrorFrame - Invalid default value for relay mode set in RELAY_MODE error frame");
                else {
                    switch (data[2]) {
                        case 0x03 : return "No response from third repeater";
                        case 0x02 : return "No response from second repeater";
                        case 0x01 : return "No response from first repeater";
                        case 0x00 : return "No response from end-point module";
                        default   : throw new CoronisException("TransmissionErrorFrame - Invalid RELAY_COUNTER variable in RELAY_MODE error frame");
                    }                        
                }                
            }
        }
        
        public String getErrorMessage() throws CoronisException{           
            return (isPtpMode() ? "point-to-point mode" : "relay mode") + " - " + getErrorString() ;
        }
        
	public boolean mustACK() {
		return true;
	}        
}
