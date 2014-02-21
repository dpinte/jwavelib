package com.dipole.jwavetool.frame;

import com.coronis.frames.CoronisFrame;
import com.coronis.frames.ReqSendBroadcastFrame;
import com.dipole.jwavetool.common.Common;
import com.dipole.libs.Functions;

public class SnifferReqSendBroadcastFrame 	extends ReqSendBroadcastFrame
											implements SnifferFrameInterface {
	private int[] timeStamp;
	private int[] Header;
	private int[] footer;
	private int direction;
	private boolean canDisplay;
	private String dateTime;

	public SnifferReqSendBroadcastFrame(final int cmd, final int[] msg,
			final int[]ts, final int[] head,
			final int[] foot, final int dir) {
			super(cmd, msg);

		this.timeStamp = ts;
		this.Header = head;
		this.footer = foot;
		this.direction = dir;
		this.canDisplay = true;
		this.dateTime = Common.getCurentDateTime();	
	}

	@Override
	public void setDateTime(final String date){
		this.dateTime = date;
	}
	
	@Override
	public boolean canDisplay() {
		return this.canDisplay;
	}
	
	@Override
	public String getDataAsString() {
		return Functions.printHumanHex(this.data, false);
	}
	
	@Override
	public int getSniffedCrc() {
		return this.footer[1] << 8 | this.footer[0];
	}
	
	@Override
	public int[] getTimeStamp() {
		return this.timeStamp;
	}
	
	@Override
	public String getTimeStampStr() {
		// TODO Auto-generated method stub
		return "";
	}
	
	@Override
	public boolean isTimeStampOk() {
		return FrameAnalyser.checkTimestamp(this.timeStamp);
	}
	
	@Override
	public boolean isCrcOk() {
		return this.checkCrc(this.getSniffedCrc());
	}
	
	@Override
	public boolean isEtxOk() {
		return (this.footer[2] == CoronisFrame.CRN_ETX)? true : false;
	}
	
	@Override
	public boolean isStxOk() {
		return (this.Header[1] == CoronisFrame.CRN_STX)? true : false;
	}
	
	@Override
	public void setDisplay(final boolean i) {
		this.canDisplay = i;		
	}
	
	@Override
	public int getDirection() {
		return this.direction;
	}
	
	@Override
	public String getDirectionStr() {
		// TODO Auto-generated method stub
		return "";
	}
	
	@Override
	public int getCmd() {
		return this.cmd;
	}
	
	@Override
	public String getSniffedFrame() {
		return Functions.printHumanHex(this.Header, false) +
		Functions.printHumanHex(this.cmd, false) + 
		Functions.printHumanHex(this.data, false) + 
		Functions.printHumanHex(this.footer, false);
	}
	
	@Override
	public int getCalculatedCrc() {
		return this.getCrc();
	}
	
	@Override
	public String getDateTime(final boolean complete) {
		return this.dateTime;
	}
}
