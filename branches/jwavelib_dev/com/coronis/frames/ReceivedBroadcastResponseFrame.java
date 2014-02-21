package com.coronis.frames;

import com.dipole.libs.Functions;

public class ReceivedBroadcastResponseFrame extends CoronisFrame {

	public ReceivedBroadcastResponseFrame(int command, int[] message) {
		super(command, message);
	}

	/**
	 * Get frame status:<ul>
	 * 					<li> O : OK
	 * 					<li> 1 : total received frame higher than 255
	 * 					</ul>
	 *
	 * @return true if OK
	 */
	public boolean getStatus() {
		return (data[0] == 0) ? true : false;
	}
	
	/**
	 * Get the total of received frame
	 * 
	 * @return An Integer
	 */
	public int getTotalFrameReceived() {
		return this.data[1];
	}
	
	/**
	 * Get the current frame index
	 * 
	 * @return An Integer
	 */
	public int getFrameIndex() {
		return this.data[2];
	}
	
	/**
	 * Get the moduleID
	 * 
	 * @return A String
	 */
	public String getModuleId() {
        StringBuffer stbf = new StringBuffer();
        for (int i = 0; i < 6; i++) stbf.append(Functions.printHumanHex(data[i+3], false));
        return stbf.toString();
    }
	
	/**
	 * Get the received data
	 * 
	 * @return An Integer array
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
