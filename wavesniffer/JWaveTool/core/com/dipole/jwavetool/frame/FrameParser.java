package com.dipole.jwavetool.frame;

import java.util.Arrays;

import com.coronis.frames.CoronisFrame;
import com.coronis.frames.ResWriteParameterFrame;
import com.dipole.jwavetool.common.Common;
import com.dipole.jwavetool.common.Log;
import com.dipole.libs.Functions;

public class FrameParser {

	public static final int AWAKENING_PERIOD = 0x00;
	public static final int WAKEUP_TYPE = 0x01;
	public static final int WAKEUP_LENGTH = 0x02;
	public static final int WAVECARD_POLLING_GROUP = 0x03;
	public static final int RADIO_ACKNOWLEGDE = 0x04;
	public static final int RADIO_ADDRESS = 0x05;
	public static final int RELAY_ROUTE_STATUS = 0x06; 
	public static final int RELAY_ROUTE = 0x07;
	public static final int POLLING_ROUTE = 0x08;
	public static final int GROUP_NUMBER = 0x09;
	public static final int POLLING_TIME = 0x0A;
	public static final int RADIO_USER_TIMEOUT = 0x0C;
	public static final int EXCHANGE_STATUS = 0x0E;
	public static final int SWITCH_MODE_SATUS = 0x10;
	public static final int WAVECARD_MULTICAST_GROUP = 0x16;
	public static final int BCST_RECEPTION_TIMEOUT = 0x17;
	
	/**
	 * Parse data field of a frame
	 * @param container The frame container
	 * @param frameIndex The frame index in the container
	 * @return
	 */
	public static String[][] parseData(final SnifferFrameInterface frame) {
		Log.trace("frame.frameAnalyser.parseData: "+ frame.getSniffedFrame());
		
		String[][] parData = null;
		
		switch(frame.getCmd()) {
			case CoronisFrame.RES_SEND_SERVICE:
			case CoronisFrame.RES_SEND_FRAME:
				parData = new String[1][2];
				
				parData[0][0] = "Status";
				if(((SnifferResSendFrame)frame).getStatus()) {
					parData[0][1] = "Transmission OK (0x00)";
				} else {
					parData[0][1] = "Transmission Error (0x01)";
				}
				break;
				
			case CoronisFrame.RECEIVED_MULTIFRAME:
				parData = new String[5][2];
				
				parData[0][0] = "Status";
				if(((SnifferReceivedMultiFrame)frame).getStatus() == 1) {
					parData[0][1] = "Transmission OK (0x00)";
				} else {
					parData[0][1] = "Transmission Error (0x01)";
				}
				
				parData[1][0] = "Total of frames";
				parData[1][1] = Integer.toString(((SnifferReceivedMultiFrame)frame).getTotalFramesReceived());
				
				parData[2][0] = "Frame index";
				parData[2][1] = Integer.toString(((SnifferReceivedMultiFrame)frame).getFrameIndex());
				
				parData[3][0] = "Radio address";
				parData[3][1] = ((SnifferReceivedMultiFrame)frame).getModuleId();
				
				parData[4][0] = "Received data";
				parData[4][1] = "N/A";
				break;
		
			case CoronisFrame.RECEIVED_FRAME:
				parData = new String[2][2];
				
				parData[0][0] = "Radio address";
				parData[0][1] = ((SnifferReceivedFrame)frame).getModuleId();
				
				parData[1][0] = "Received data";
				parData[1][1] = "N/A";
				break;
				
			case CoronisFrame.RECEIVED_BROADCAST_RESPONSE:
				parData = new String[5][2];
				
				parData[0][0] = "Status";
				if(((SnifferReceivedBroadcastFrame)frame).getStatus()) {
					parData[0][1] = "Transmission OK (0x00)";
				} else {
					parData[0][1] = "Transmission Error (0x01)";
				}
				
				parData[1][0] = "Total of frames";
				parData[1][1] = Integer.toString(((SnifferReceivedBroadcastFrame)frame).getTotalFrameReceived());
				
				parData[2][0] = "Frame index";
				parData[2][1] = Integer.toString(((SnifferReceivedBroadcastFrame)frame).getFrameIndex());
				
				parData[3][0] = "radio Address";
				parData[3][1] = ((SnifferReceivedBroadcastFrame)frame).getModuleId();
				
				parData[4][0] = "Received Data";
				parData[4][1] = Functions.printHumanHex(((SnifferReceivedBroadcastFrame)frame).getReceivedData(), false);
				break;
				
			case CoronisFrame.RES_READ_PARAM:
				parData = new String[2][2];
				
				if(((SnifferResReadParameterFrame)frame).getStatus()) {
					parData[0][1] = "Read OK (0x00)";
				} else {
					parData[0][1] = "Read Error (0x00)";
				}
				
				//FIXME: parse value. Need to get last REQ_READ_RADIO_PARAM to figure out whitch parameter has been read
				parData[1][0] = "Parameter value";
				parData[1][0] = Functions.printHumanHex(((SnifferResReadParameterFrame)frame).getParameterData(), false);
				break;
				
			case CoronisFrame.REQ_WRIT_PARAM:
				parData = new String[2][2];
				int paramNum = ((SnifferReqWriteParameterFrame)frame).getParameterNumber();
            	int[] paramData = ((SnifferReqWriteParameterFrame)frame).getParameterData();
				
				parData[0][0] = "Parameter";
				parData[0][1] = Common.paramDescription.get(paramNum).getName();
				
				parData[1][0] = "Value";
				parData[1][1] = parseParameterValue(paramNum, paramData);
				break;
			
			case CoronisFrame.RES_SELECT_PHYCONFIG:
			case CoronisFrame.RES_SELECT_CHANNEL:
			case CoronisFrame.RES_CHANGE_TX_POWER:
			case CoronisFrame.RES_CHANGE_UART_BAUDRATE:
			case CoronisFrame.RES_WRIT_AUTOCORR_STATE:
			case CoronisFrame.RES_WRIT_PARAM:
				parData = new String[1][2];
				
				parData[0][0] = "Status";
				if(((ResWriteParameterFrame)frame).getStatus()) {
					parData[0][1] = "Update OK (0x00)";
				} else {
					parData[0][1] = "Update Error (0x01)";
				}
				break;
				
			case CoronisFrame.RES_READ_LOCAL_RSSI:
			case CoronisFrame.RES_READ_REMOTE_RSSI:
				parData = new String[1][2];
				
				parData[0][0] = "RSSI level";
				parData[0][1] = Double.toString(((SnifferResRemoteRSSIFrame)frame).getRSSI());
				break;
			
			case CoronisFrame.RES_FIRMWARE_VERSION:
				parData = new String[1][2];
				
				parData[0][0] = "Firmware Version";
				parData[0][1] = ((SnifferWavePortFirmwareFrame)frame).getFirmware();
				break;
			
			case CoronisFrame.REQ_SEND_BROADCAST:
				parData = new String[2][2];
				
				parData[0][0] = "Broadcast group";
				parData[0][1] = Functions.printHumanHex(((SnifferReqSendBroadcastFrame)frame).getGroup(), false);
				
				parData[1][0] = "Transmitted data";
				parData[1][1] = Functions.printHumanHex(((SnifferReqSendBroadcastFrame)frame).getTransmitedData(), false);
				break;
				
			case CoronisFrame.SERVICE_RESPONSE:
			case CoronisFrame.RES_READ_AUTOCORR_STATE:
			case CoronisFrame.RES_READ_CHANNEL:
			case CoronisFrame.RES_READ_PHYCONFIG:
			case CoronisFrame.RES_READ_TX_POWER:
			case CoronisFrame.REQ_READ_PARAM: 
			case CoronisFrame.REQ_SEND_FRAME:
			case CoronisFrame.REQ_SEND_SERVICE:
				Log.error("Frame type: "+ Functions.printHumanHex(frame.getCmd(), true) +" not implemented");
				parData = new String[1][2];
				
				parData[0][0] = "N/A";
				parData[0][1] = "Data parsing not implement for this frame type";				
				break;
				
			case CoronisFrame.REQ_READ_PHYCONFIG:
			case CoronisFrame.REQ_READ_CHANNEL:
			case CoronisFrame.REQ_READ_TX_POWER:
			case CoronisFrame.REQ_READ_AUTOCORR_STATE:
			case CoronisFrame.REQ_FIRMWARE_VERSION:
			case CoronisFrame.ACK:
			case CoronisFrame.NAK:
				parData = new String[1][2];
				
				parData[0][0] = "N/A";
				parData[0][1] = "No DATA field";
				break;
			default:
				Log.info("Unknown cmd: "+ Functions.printHumanHex(frame.getCmd(), true));
				break;
		}
		
		return parData;
	}

	/**
	 * Parse the parameter value
	 * @param paramNum parameter number
	 * @param paramVal integer array with parameter data
	 * @return A string with the parser parameter value
	 */
	public static String parseParameterValue(int paramNum, final int[] paramVal) {
		String value = null;
		
		if(paramVal == null || paramVal.length == 0) {
			value = "N/A";
			paramNum = 0xFF;
		}
		
		switch(paramNum){
			case AWAKENING_PERIOD:
				if(paramVal[0] == 0) {
					value = "20ms";
				} else {
					value = Integer.toString(paramVal[0] * 100) +"ms";
				}
				break;
				
			case WAKEUP_TYPE:
				if(paramVal[0] == 1) {
					value = "Short wake-up: 50ms";
				} else {
					value = "Long wake-up";
				}
				break;
				
			case WAKEUP_LENGTH:
				int tmp = paramVal[1] << 8 | paramVal[0];
				value = Integer.toString(tmp);
				break;
				
			case WAVECARD_POLLING_GROUP:
			case GROUP_NUMBER:
				value = Integer.toString(paramVal[0]) +
						" ("+ Functions.printHumanHex(paramVal[0], true) +")";
				break;
				
			case RADIO_ACKNOWLEGDE:
				if(paramVal[0] == 1) {
					value = "No acknowledgment";
				} else {
					value = "With acknowledgment";
				}
				break;
				
			case RADIO_ADDRESS:
				value = Functions.printHumanHex(paramVal, false);
				break;
				
			case RELAY_ROUTE_STATUS:
				if(paramVal[0] == 1) {
					value = "Relay route trasmition activated";
				} else {
					value = "Relay route trasmition desactivated";
				}
				break;
				
			case RELAY_ROUTE:
				int nbRep = paramVal[0];
				int[] copy;
				
				if(nbRep != 0) {	
					value = "";
					int from = 1;
					for(int i = 0; i < nbRep; i++) {
						copy = Arrays.copyOfRange(paramVal, from, from + 6);
						value += Functions.printHumanHex(copy, false) +",";
						from += 6;
					}
				} else {
					value = "No repeater";
				}
				break;
				
			case POLLING_ROUTE:
				int nbRepPol = paramVal[0];
				int[] copyPol;
				
				if(nbRepPol != 0) {	
					value = "";
					int from = 1;
					for(int i = 0; i < nbRepPol; i++) {
						copyPol = Arrays.copyOfRange(paramVal, from, from + 6);
						value += Functions.printHumanHex(copyPol, false) +",";
						from += 6;
					}
				} else {
					value = "No modules to poll";
				}
				break;
				
			case POLLING_TIME:				
			case RADIO_USER_TIMEOUT:
			case BCST_RECEPTION_TIMEOUT:
				value = Integer.toString(paramVal[0] * 100) +"ms";
				break;
				
			case EXCHANGE_STATUS:
				switch(paramVal[0]) {
					case 0:
						value = "Status and error frames desactivated";
						break;
						
					case 1:
						value = "Error frame activated";
						break;
						
					case 2:
						value = "Status frame activated";
						break;
						
					case 3:
						value = "Both status and error frames activated";
						break;
						
					default:
						break;
				}
				break;
				
			case SWITCH_MODE_SATUS:
				if(paramVal[0] == 1) {
					value = "Automatic selection activated";
				} else {
					value = "Automatic selection desactivated";
				}
				break;
				
			case WAVECARD_MULTICAST_GROUP:
				if(paramVal[0] == 0xFF) {
					value = "No group selected (0xFF)";
				} else {
					value = Integer.toString(paramVal[0]) +
							" ("+ Functions.printHumanHex(paramVal[0], true) +")";
				}
				break;
				
			default:
				value = "Unknow parameter";
		}
		
		return value;
	}
}
