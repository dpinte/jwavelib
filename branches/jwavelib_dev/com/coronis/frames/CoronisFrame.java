/*
 * CoronisFrame.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * Coronis frame class implements the definition a frame as defined in the
 * Coronis protocol. It is used to read and write frames using the
 * CoronisFrameBuilder, CoronisFrameWriter and CoronisFrameReader classes.
 *
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 *
 * $Date: 2009-07-13 14:14:39 +0200 (Mon, 13 Jul 2009) $
 * $Revision: 104 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/frames/CoronisFrame.java $
 */
package com.coronis.frames;
/*
 * TODO : add some check on the FRAME_MAX_LENGTH, etc
 */
import java.io.*;
import com.coronis.CoronisLib;
import com.dipole.libs.Functions;


/**
 * Base class for all the Coronis frames
 * @author dpinte
 *
 */
public class CoronisFrame {
    
    public static final short CRN_SYN  = 0xFF;
    public static final short CRN_STX  = 0x02;
    public static final short CRN_ETX  = 0X03;
    
    public static final short FRAME_MAX_LENGHT = 256;
    public static final short FRAME_MIN_LENGHT = 6;
    
    // Control commands
    public static final short ACK 		= 0x06;
    public static final short NAK 		= 0x15;
    public static final short ERROR 	= 0x00;
    
    // Frame exchange commands
    public static final short REQ_SEND_FRAME = 0x20;
    public static final short RES_SEND_FRAME = 0x21;
    public static final short RECEIVED_FRAME = 0x30;
    public static final short RECEIVED_FRAME_RELAYED = 0x35;
    public static final short RECEIVED_MULTIFRAME = 0x36;
    
    // Message commands
    public static final short REQ_SEND_MESSAGE = 0x22;
    public static final short END_MESSAGE_EXCHANGE = 0x37;
    
    // Service commands
    public static final short REQ_SEND_SERVICE = 0x80;
    public static final short RES_SEND_SERVICE = 0x81;
    public static final short SERVICE_RESPONSE = 0x82;
    
    // Broadcast commands
    public static final short REQ_SEND_BROADCAST = 0x28;
    public static final short REQ_SEND_BROADCAST_MESSAGE = 0x2A;
    public static final short REQ_SEND_BROADCAST_RESPONSE = 0x24;
    public static final short RECEIVED_BROADCAST_RESPONSE = 0x34;
    public static final short RECEIVED_BROADCAST_FRAME = 0x38;
    
    // polling commands
    public static final short REQ_SEND_POLLING = 0x26;
    public static final short RECEIVED_FRAME_POLLING = 0x32;
    
    // Waveport parameters
    public static final short REQ_WRIT_PARAM = 0x40;
    public static final short RES_WRIT_PARAM = 0x41;
    public static final short REQ_WRIT_AUTOCORR_STATE = 0x46;
    public static final short RES_WRIT_AUTOCORR_STATE = 0x47;
    public static final short REQ_CHANGE_UART_BAUDRATE = 0x42;
    public static final short RES_CHANGE_UART_BAUDRATE = 0x43;
    public static final short REQ_CHANGE_TX_POWER = 0x44;
    public static final short RES_CHANGE_TX_POWER = 0x45;
    public static final short REQ_SELECT_CHANNEL = 0x60;
    public static final short RES_SELECT_CHANNEL = 0x61;
    public static final short REQ_SELECT_PHYCONFIG = 0x64;
    public static final short RES_SELECT_PHYCONFIG = 0x65;
    public static final short REQ_READ_PARAM = 0x50;
    public static final short RES_READ_PARAM = 0x51;
    public static final short REQ_READ_TX_POWER = 0x54;
    public static final short RES_READ_TX_POWER = 0x55;
    public static final short REQ_READ_AUTOCORR_STATE = 0x5A;
    public static final short RES_READ_AUTOCORR_STATE = 0x5B;
    public static final short REQ_READ_CHANNEL = 0x62;
    public static final short RES_READ_CHANNEL = 0x63;
    public static final short REQ_READ_PHYCONFIG = 0x66;
    public static final short RES_READ_PHYCONFIG = 0x67;
    public static final short REQ_READ_REMOTE_RSSI = 0x68;
    public static final short RES_READ_REMOTE_RSSI = 0x69;
    public static final short REQ_READ_LOCAL_RSSI = 0x6A;
    public static final short RES_READ_LOCAL_RSSI = 0x6B;
    
    // Service type
    public static final short GET_TYPE = 0x20;
    public static final short GET_FW_VERSION = 0x28;
    public static final short RESP_GET_TYPE = 0xA0;
    public static final short RESP_GET_FW_VERSION = 0xA8;
    
    // Error commands
    public static final short TR_ERROR_FRAME = 0x31;
    
    // Waveport commands
    public static final short REQ_FIRMWARE_VERSION = 0xA0;
    public static final short RES_FIRMWARE_VERSION = 0xA1;
    
    //test
    public static final short MODE_TEST = 0xB0;
    
    protected int[] 			data;
    protected int				length;
    protected int				cmd;
    protected int				crc;
    private boolean         _isLoaded;
    
    /**
     * CoronisFrame constructor. Builds up a CoronisFrame object from the 
     * CMD, DATA and CRC par of a Coronis frame description
     * 
     * @param command CMD part of the Coronis frame
     * @param message DATA + CRC part of the Coronis frame
     */
    public CoronisFrame(int command, int[] message) {
        cmd  = command;
        message = (message != null) ? message : new int[0];
        data = message;
        _isLoaded = false;        
    }
    
    /**
     * Private method loading the message from the data object
     * and computing the CRC on it
     */
    private void load() {
        int[] crcmsg = new int[getCrcMsgLength()];
        crcmsg[0] = (int)getLength();
        crcmsg[1] = cmd;
        for (int i=0; i < data.length; i++) {
            crcmsg[2+i] = data[i];
        }
        crc  = CoronisLib.calculateCrc(crcmsg);
        _isLoaded = true;
    }
    
    /**
     * Check CRC by comparing the computed one to crcval.
     * 
     * @param crcval
     * @return true if identical, false otherwise
     */
    public boolean checkCrc(int crcval) {
        if (_isLoaded == false) load();
        return ( crcval == crc );
    }
    
    /**
     * Returns the computed CRC on the data object.
     * <p>
     * !! This is not the received CRC (but it should be the same ;-) ).
     * @return the compûted CRC from the frame
     */
    public int getCrc() {
        if (_isLoaded == false) load();
        return crc;
    }
  
    /**
     * Returns an int[] formatted as valid Coronis frame to be sent to the WavePort
     * @return the frame
     */
    public int[] getFrame() {
        if (_isLoaded == false) load();
        int[] msg = new int[getFrameLength()];
        msg[0] = CRN_SYN;
        msg[1] = CRN_STX;
        msg[2] = (short)getLength();
        msg[3] = cmd;
        for(int i=0; i< data.length; i++) {
            msg[4+i] = data[i];
        }
        // transfer 16bit crc to two shorts ! LSB and MSB are inverted !
        msg[3+data.length+1] =  crc & 0xFF;
        msg[3+data.length+2]=   crc  >>> 8;
        msg[3+data.length+3] = CRN_ETX;
        return msg;
    }
    
    /**
     * Returns the frame as human readable string
     * 
     * @return the human readable String version of the frame
     */
    public String getFrameAsString() {
    	return Functions.printHumanHex(this.getFrame(), false);
    }
    
    /**
     * Returns the data part of the frame
     * 
     * @return an int[] with the data part of the frame
     */
    public int[] getData() {
    	if (_isLoaded == false) load();
        return data;
    }
    
    /**
     * Returns the frame as a byte array valid to push into an OutputStream
     * 
     * @return a valid byte array Coronis frame
     * @throws IOException
     */
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        int[] f = getFrame();
        for (int i= 0; i < f.length; i++)
            // & 0XFF to be sure it's correctly matched in -127/+128
            dos.writeByte(f[i] & 0xFF) ;
        dos.flush();
        return bos.toByteArray();
    }
    
    /**
     * Returns the CRC message length
     * <p>
     * Length of the message is:<br>
     * <ul>
     * <li> 1 byte for the LENGTH,
     * <li> 1 byte for the CMD
     * <li> N bytes for the DATA
     * </ul>
     * 
     * @return the CRC message length
     */
    private int getCrcMsgLength(){     
        return 1 + 1 + data.length;        
    }
        
    /**
     * Returns the LENGTH field of the frame
     * <p>
     * /!\ This is the computed length not the received length
     * <p>
	 * Length of the frame is:<br>
	 * <ul>
	 * <li> 1 byte for the LENGTH,
	 * <li> 1 byte for the CMD
     * <li>N bytes for the DATA
     * <li> 2 bytes for the CRC
     * 
     * @return the LENGTH field
     */
    private int getLength() {
    	return this.getCrcMsgLength() + 2;
    }
    
    /**
     * Returns the frame length
     * <p>
	 * Length of the frame is:<br>
     * <ul>
     * <li> 1 byte for the SYNC
     * <li> 1 byte for the STX
     * <li> 1 byte for the LENGTH,
     * <li> 1 byte for the CMD
     * <li> N bytes for the DATA
     * <li> 2 bytes for the CRC
     * <li> 1 byte for the ETX 
     * </ul>
     * 
     * @return the frame length
     */    
    public int getFrameLength() {
    	return this.getLength() + 3;
    }
    
    /**
     * It this frame an ACK frame ?
     * @return true if it is an ACK frame, false otherwise
     */
    public boolean isACK() {
        return (cmd == ACK) ? true : false;
    }
    
    /**
     * Do we have to ACK this frame ? Default is no !
     * <p>
     * Should be overloaded in children classes if needing an ACK
     *  
     * @return true if frame needs to be ACK'ed, false otherwise
     */
    public boolean mustACK() {
        return false;
    }
    
    /**
     * Get the command
     * 
     * @return the command of the frame
     */
    public int getCmd() {
		return cmd;
	}    
}
