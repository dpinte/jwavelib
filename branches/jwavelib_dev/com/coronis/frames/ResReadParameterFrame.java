 package com.coronis.frames;

public class ResReadParameterFrame extends CoronisFrame {

	public ResReadParameterFrame(int command, int[] message) {
		super(command, message);
		// TODO Auto-generated constructor stub
	}

	public boolean getStatus() {
		/*
		 * STATUS
         *  - 0x00 read OK
         *  - 0x01 read error
		 */
		return (data[0] == 0) ? true : false;
	}        
    
	public int[] getParameterData(){
		int[] param = new int[this.data.length - 1];
		for(int i = 0; i < param.length; i++){
			param[i] = this.data[i + 1];
		}
		return param;
	}
  
	public boolean mustACK() {
    	return true;
    }
}
