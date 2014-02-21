package com.coronis.frames;

import com.dipole.libs.Functions;

public class ReceivedBroadcastResponseFrame extends CoronisFrame {

	public ReceivedBroadcastResponseFrame(int command, int[] message) {
		super(command, message);
	}

	/**
	 * get frame status:
	 *    - 0 => OK
	 *    - 1 => total received frame higher than 255
	 * @return
	 */
	public boolean getStatus() {
		/*
		 * DATA contains 1 byte that is the emission status
		 * 	- 0 is emission frame format OK
		 *  - 1 is emission frame format error
		 */
		return (data[0] == 0) ? true : false;
	}
	
	/**
	 * get the total of received frame
	 * @return
	 */
	public int getTotalFrameReceived() {
		return this.data[1];
	}
	
	/**
	 * get the current frame index
	 * @return
	 */
	public int getFrameIndex() {
		return this.data[2];
	}
	
	/**
	 * get the moduleID
	 * @return
	 */
	public String getModuleId() {
        StringBuffer stbf = new StringBuffer();
        for (int i = 0; i < 6; i++) stbf.append(Functions.printHumanHex(data[i+3], false));
        return stbf.toString();
    }
	
	/**
	 * get the received data
	 * @return
	 */
	public int[] getReceivedData() {
		int[] recData = new int[this.data.length - 9];
		
		for(int i = 0; i < recData.length; i++){
			recData[i] = this.data[i + 9];
		}
		
		return recData;
	}
	
	public boolean mustACK(){
		return true;
	}
}
