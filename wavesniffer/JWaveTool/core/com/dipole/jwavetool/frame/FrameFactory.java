package com.dipole.jwavetool.frame;

import java.util.ArrayList;

import com.coronis.frames.CoronisFrame;
import com.dipole.jwavetool.common.Log;

enum FrameState {
	sync, stx, len, cmd, data, crc, etx
}

public class FrameFactory {

	/**
	 * Build a frame from String
	 * @param frame		A frame in Hex format without prefix
	 * @param timeStamp	TimeStamp in this format {ts1/ts2/.../tsn}
	 * @param direction	Frame direction in hex format without prefix
	 * @return a new SnifferFrame
	 */
	public static SnifferFrameInterface buildFrameFromString(final String frame,
															final String timeStamp,
															final String direction) {
		Log.trace("Enter FrameFactory.buildFrameFromString");
		
		String[] frameByte = new String[frame.length()/2];
		ArrayList <Integer> bodyLst = new ArrayList <Integer> ();
		int[] tsArray = null;
		int[] bodyArray = null;
		int[] header = new int[3];
		int[] footer = new int[3];
		int tmpVal;
		int dataLen = 0;
		boolean end = false;
		
		/* split timeStamp string then parse it:
		 * remove {} and / chars
		 */
		String[] tsStrArray = timeStamp.substring(1, (timeStamp.length() - 1)).split("/");
		tsArray = new int[tsStrArray.length];
		
		for(int i = 0; i < tsStrArray.length; i++){
			tsArray[i] = Integer.parseInt(tsStrArray[i]);
		}
		
		/* split frame in byte */
		for(int i = 0, j = 0; i < frameByte.length; i++){
			frameByte[i] = frame.substring(j, j + 2);
			j += 2;
		}
		
		
		/* parse frame */
		FrameState state = FrameState.sync;
		for(int i = 0; (i < frameByte.length) || !end; i++){
			switch(state){
				case sync:
					tmpVal = (Integer.parseInt(frameByte[i], 16) & 0xFF);
					if(tmpVal == SnifferFrame.CRN_SYN){
						state = FrameState.stx;
						header[0] = tmpVal;
					}
					break;
					
				case stx:
					tmpVal = (Integer.parseInt(frameByte[i], 16) & 0xFF);
					header[1] = tmpVal;
					if(tmpVal == SnifferFrame.CRN_STX){
						state = FrameState.len;
					} else {
						end = true;
					}
					break;
				
				case len:
					tmpVal = (Integer.parseInt(frameByte[i], 16) & 0xFF);
					header[2] = tmpVal;
					dataLen = tmpVal - 4;
					state = FrameState.cmd;
					break;
					
				case cmd:
					tmpVal = (Integer.parseInt(frameByte[i], 16) & 0xFF);
					bodyLst.add(tmpVal);
					state = FrameState.data;
					break;
					
				case data:
					int j;
					for(j = 0; j < dataLen; j++){
						bodyLst.add((Integer.parseInt(frameByte[i + j], 16) & 0xFF));
					}
					i += j -1;
					state = FrameState.crc;
					break;
					
				case crc:
					footer[0] = (Integer.parseInt(frameByte[i], 16) & 0xFF);
					i++;
					footer[1] = (Integer.parseInt(frameByte[i], 16) & 0xFF);					
					state = FrameState.etx;
					break;
					
				case etx:
					tmpVal = (Integer.parseInt(frameByte[i], 16) & 0xFF);
					footer[2] = tmpVal;
					end = true;
					break;
					
				default:
					break;
			}
		}
		
		bodyArray = new int[bodyLst.size()];
		int i = 0;
		for(Integer value : bodyLst){
			bodyArray[i++] = value;
		}
		
		return buildFrameFromArray(header, bodyArray, footer, tsArray, Integer.valueOf(direction, 16));
	}
	
	
	/**
	 * Build a frame from arrays
	 * @param header	Array witch contain SYNC + STX + LEN fields
	 * @param body		array witch contain CMD + DATA fields
	 * @param footer	Array witch contain CRC + ETX fields
	 * @param timeStamp	Array for timeStamp
	 * @param direction Frame direction
	 * @return a new SnifferFrame
	 */
	//FIXME: - better null checking
	public static SnifferFrameInterface buildFrameFromArray(final int[] header,
															final int[] body, final int[] footer,
															final int[] timeStamp, final int direction){
		Log.trace("Enter FrameFactory.buildFrameFromArray");
		
		SnifferFrameInterface frame = null;
		int[] data = null;
		int cmd = 0;
		
		/* parse body */
		if(body.length > 0){
			cmd = body[0];
		}
		
		if(body.length > 1){
			data = new int[body.length - 1];
			System.arraycopy(body, 1, data, 0, data.length);
		} else {
			data = new int[0];
		}
		
		/* build frame */
		switch(cmd){
			case CoronisFrame.RES_SEND_SERVICE:
			case CoronisFrame.SERVICE_RESPONSE:
			case CoronisFrame.RECEIVED_FRAME:
				Log.debug("SnifferReceivedFrame: "+ cmd);
				frame = new SnifferReceivedFrame(cmd, data, timeStamp, header, footer, direction);
				break;
			
			case CoronisFrame.RECEIVED_BROADCAST_RESPONSE:
				Log.debug("SnifferReceivedBroadcastFrame: "+ cmd);
				frame = new SnifferReceivedBroadcastFrame(cmd, data, timeStamp, header, footer, direction);
				break;
				
			case CoronisFrame.REQ_WRIT_PARAM:
				Log.debug("SnifferReqWriteParameterFrame: "+ cmd);
				frame = new SnifferReqWriteParameterFrame(cmd, data, timeStamp, header, footer, direction);
				break;
				
			case CoronisFrame.RES_READ_PARAM:
            case CoronisFrame.RES_READ_CHANNEL:
            case CoronisFrame.RES_READ_PHYCONFIG:
            case CoronisFrame.RES_READ_TX_POWER:
            	Log.debug("SnifferResReadParameterFrame: "+ cmd);
            	frame = new SnifferResReadParameterFrame(cmd, data, timeStamp, header, footer, direction);
            	break;
            	
			case CoronisFrame.TR_ERROR_FRAME:
				Log.debug("SnifferTransmitionErrorFrame: "+ cmd);
				frame = new SnifferTransmitionErrorFrame(cmd, data, timeStamp, header, footer, direction);
				break;
				
			case CoronisFrame.RES_SEND_FRAME:
				Log.debug("SnifferResSendFrame: "+ cmd);
				frame = new SnifferResSendFrame(cmd, data, timeStamp, header, footer, direction);
				break;
				
			case CoronisFrame.RECEIVED_MULTIFRAME:
				Log.debug("SnifferReceivedMultiFrame: "+ cmd);
				frame = new SnifferReceivedMultiFrame(cmd, data, timeStamp, header, footer, direction);
				break;
			
			case CoronisFrame.RES_FIRMWARE_VERSION:
				Log.debug("SnifferWavePortFirmware: "+ cmd);
				frame = new SnifferWavePortFirmwareFrame(cmd, data, timeStamp, header, footer, direction);
				break;
				
			case CoronisFrame.RES_READ_LOCAL_RSSI:
			case CoronisFrame.RES_READ_REMOTE_RSSI:
				Log.debug("SnifferResRemoteRSSIFrame: "+ cmd);
				frame = new SnifferResRemoteRSSIFrame(cmd, data, timeStamp, header, footer, direction);
				//frame = new SnifferResWriteParameterFrame(cmd, data, timeStamp, header, footer, direction);
				break;
			
			case CoronisFrame.RES_CHANGE_TX_POWER:
            case CoronisFrame.RES_CHANGE_UART_BAUDRATE:
            case CoronisFrame.RES_SELECT_CHANNEL:
            case CoronisFrame.RES_SELECT_PHYCONFIG:
            case CoronisFrame.RES_WRIT_AUTOCORR_STATE:
			case CoronisFrame.RES_WRIT_PARAM:
				Log.debug("SnifferResWriteParameterFrame: "+ cmd);
				frame = new SnifferResWriteParameterFrame(cmd, data, timeStamp, header, footer, direction);
				break;
				
			case CoronisFrame.ACK:
			case CoronisFrame.NAK:
			case CoronisFrame.ERROR:				
			default:
				Log.debug("Default frame: "+ cmd);
				frame = new SnifferFrame(cmd, data, timeStamp, header, footer, direction);
				break;
		}
		
		Log.trace("Exit with: "+ frame.getSniffedFrame());
		return frame;
	}
}
