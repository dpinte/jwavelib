package com.coronis.frames;

public class ReqWriteParameterFrame extends CoronisFrame {


	public ReqWriteParameterFrame(int command, int[] message) {
		super(command, message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * get the parameter number
	 * @return
	 */
	public int getParameterNumber(){
		return this.data[0];
	}
	
	/**
	 * get the parameter Data
	 * @return
	 */
	public int[] getParameterData(){
		int[] paramData = new int[this.data.length - 1];
		
		for(int i = 0; i < paramData.length; i++){
			paramData[i] = this.data[i + 1];
		}
		
		return paramData;
	}
	
	/**
	 * set the DATA field
	 * @param paramNum The parameter Number
	 * @param paramData The parameter Data
	 */
	public void setData(int paramNum, int[] paramData){
		int[] tmp = new int[paramData.length + 1];
		
		tmp[0] = paramNum;
		for(int i = 1; i < tmp.length; i++){
			tmp[i] = paramData[i - 1];
		}
		
		this.data = tmp;
	}
	
	public boolean mustACK() {
		return true;
	}
}
