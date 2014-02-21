package com.coronis.frames;

public class ReqSendBroadcastFrame extends CoronisFrame {

	public ReqSendBroadcastFrame(int command, int[] message) {
		super(command, message);
	}
	
	public boolean mustACK() {
		return true;
	}
	
	/**
	 * Get the broadcast group number to query
	 * 
	 * @return An Integer array
	 */
	public int[] getGroup() {
		int[] grp = new int[6];
		
		for(int i = 0; i < grp.length; i++){
			grp[i] = this.data[i];
		}
		
		return grp;
	}
	
	/**
	 * Get the data to transmit
	 * 
	 * @return An Integer Array
	 */
	public int[] getTransmitedData() {
		int[] trsData = new int[this.data.length - 6];
		
		for(int i = 0; i < trsData.length; i++){
			trsData[i] = this.data[i + 6];
		}
		
		return trsData;
	}
}
