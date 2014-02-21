/**
 * 
 */
package com.coronis.test.virtualComp;

import com.coronis.frames.CoronisFrame;

/**
 * @author antoine
 *
 */
public class VirtualCoronisFrame extends CoronisFrame {
	private boolean badSTX = false;
	private boolean badETX = false;
	private boolean badLEN = false;
	
	private int badLen;
	/**
	 * @param command
	 * @param message
	 */
	public VirtualCoronisFrame(int command, int[] message) {
		super(command, message);
		// TODO Auto-generated constructor stub
	}

	public void setCRC(int crc) {
		this.crc = crc;
	}
	
	public void setBadSTX() {
		this.badSTX = true;
	}
	
	public void setBadETX(){
		this.badETX = true;
	}
	
	public void setMessageLength(int len) {
		this.badLen = len;
		this.badLEN = true;
	}
	
	public int[] getFrame() {
        int[] msg = new int[getFrameLength()];
        msg[0] = CRN_SYN;
        
        if(badSTX) {
        	msg[1] = 0x03;
        } else {
        	msg[1] = CRN_STX;
        }
        
        if(this.badLEN) {
        	msg[2] = this.badLen;
		} else {
			msg[2] = (short)this.data.length + 4;
		}
        msg[3] = cmd;
        
        for(int i=0; i< data.length; i++) {
            msg[4+i] = data[i];
        }
        
        // transfer 16bit crc to two shorts ! LSB and MSB are inverted !
        msg[3+data.length+1] =  crc & 0xFF;
        msg[3+data.length+2]=   crc  >>> 8;
        
        if(this.badETX) {
        	msg[3+data.length+3] = 0x02;
        } else {
        	msg[3+data.length+3] = CRN_ETX;
        }
        return msg;
    }
}