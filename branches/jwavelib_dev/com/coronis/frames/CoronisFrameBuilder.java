/*
 * CoronisFrameBuilder.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * Coronis frame builder : facility class building frame using a given CMD.
 * See the Coronis protocol definition for more details about this.
 * 
 * Raises UnsupportedFrameException when reading a frame that is not supported by 
 * this version of the code. At the moment, only ACK, RES_SEND_FRAME, 
 * RECEIVED_FRAME and NAK are allowed.
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 */
package com.coronis.frames;

import com.coronis.exception.UnsupportedFrameException;
import com.coronis.exception.CoronisException;

/**
 * Factory class creating Coronis frames based on their CMD byte
 * @author dpinte
 */
public class CoronisFrameBuilder {
	
    public static CoronisFrame buildFrame(int cmd, int[] msg) throws CoronisException {
		CoronisFrame newFrame = null;
		switch (cmd) 
		{
			case CoronisFrame.ACK: 				
				newFrame = new ACKFrame(cmd, msg);
				break;
				
			case CoronisFrame.RES_SEND_FRAME :
			case CoronisFrame.RES_SEND_SERVICE:
				newFrame = new ResSendFrame(cmd, msg);
				break;

			case CoronisFrame.RECEIVED_BROADCAST_RESPONSE:
				newFrame = new ReceivedBroadcastResponseFrame(cmd, msg);
				break;
					
			case CoronisFrame.SERVICE_RESPONSE:
			case CoronisFrame.RECEIVED_FRAME:
				newFrame = new ReceivedFrame(cmd, msg);
				break;
			
			case CoronisFrame.RECEIVED_MULTIFRAME:
				newFrame = new ReceivedMultiFrame(cmd, msg);
				break;
				
            case CoronisFrame.RES_FIRMWARE_VERSION:                                
                newFrame = new WavePortFirmwareFrame(cmd, msg);
                break;
                
            case CoronisFrame.RES_CHANGE_TX_POWER:
            case CoronisFrame.RES_CHANGE_UART_BAUDRATE:
            case CoronisFrame.RES_SELECT_CHANNEL:
            case CoronisFrame.RES_SELECT_PHYCONFIG:
            case CoronisFrame.RES_WRIT_AUTOCORR_STATE:
            case CoronisFrame.RES_WRIT_PARAM:
            	newFrame = new ResWriteParameterFrame(cmd, msg);
                break;
           
            case CoronisFrame.RES_READ_PARAM:
            case CoronisFrame.RES_READ_CHANNEL:
            case CoronisFrame.RES_READ_PHYCONFIG:
            case CoronisFrame.RES_READ_TX_POWER:
            	newFrame = new ResReadParameterFrame(cmd, msg);
            	break;
            	
            case CoronisFrame.RES_READ_LOCAL_RSSI:
            case CoronisFrame.RES_READ_REMOTE_RSSI:
                newFrame = new ResRemoteRSSIFrame(cmd, msg);
                break;
                
            case CoronisFrame.REQ_WRIT_PARAM:
            	newFrame = new ReqWriteParameterFrame(cmd, msg);
            	break;
            	
			case CoronisFrame.NAK:								
                // FIXME : not managed correctly in the rest of the code
                newFrame =  new NAKFrame(cmd, msg);
                break;
                
            case CoronisFrame.TR_ERROR_FRAME:         
                newFrame =  new TransmissionErrorFrame(cmd, msg);
                break;
                
            case CoronisFrame.ERROR:
               // Atfer a phone call with Mr Rabaud from Coronis (13.06.2008), this must be considered as a NAK !
                //throw new UnsupportedFrameException("Command error (0x0)");
                newFrame = new ErrorFrame(cmd, msg);
                break;
                
            /*
             *  Frame type without specific implementation
             *  TODO: to be implemented if needed
             */
            case CoronisFrame.REQ_SEND_FRAME:
            case CoronisFrame.REQ_SEND_BROADCAST:
            case CoronisFrame.REQ_SEND_BROADCAST_MESSAGE:
            case CoronisFrame.REQ_SEND_BROADCAST_RESPONSE:
            case CoronisFrame.REQ_SEND_MESSAGE:
            case CoronisFrame.REQ_SEND_POLLING:
            case CoronisFrame.RECEIVED_FRAME_POLLING:
            case CoronisFrame.RECEIVED_FRAME_RELAYED:
            case CoronisFrame.RECEIVED_BROADCAST_FRAME:
            case CoronisFrame.END_MESSAGE_EXCHANGE:
            case CoronisFrame.REQ_CHANGE_TX_POWER:
            case CoronisFrame.REQ_CHANGE_UART_BAUDRATE:
            case CoronisFrame.REQ_SELECT_CHANNEL:
            case CoronisFrame.REQ_SELECT_PHYCONFIG:
            case CoronisFrame.REQ_WRIT_AUTOCORR_STATE:
            case CoronisFrame.REQ_READ_PARAM:
            case CoronisFrame.REQ_READ_CHANNEL:
            case CoronisFrame.REQ_READ_PHYCONFIG:
            case CoronisFrame.REQ_READ_TX_POWER:
            case CoronisFrame.REQ_READ_LOCAL_RSSI:
            case CoronisFrame.REQ_READ_REMOTE_RSSI:
            	newFrame = new CoronisFrame(cmd, msg);
            	break;
            	
			default:
				throw new UnsupportedFrameException(Integer.toHexString(cmd) +" is not a valid Coronis command");							
		}
		return newFrame;
	}
	
	public static ACKFrame getACK(){		
		return new ACKFrame(CoronisFrame.ACK, null);
	}
}
