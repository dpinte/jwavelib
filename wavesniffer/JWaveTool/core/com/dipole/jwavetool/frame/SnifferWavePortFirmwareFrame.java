package com.dipole.jwavetool.frame;

import com.coronis.frames.CoronisFrame;
//import com.coronis.frames.WavePortFirmwareFrame;
import com.dipole.libs.Functions;
import com.dipole.jwavetool.common.Common;

public class SnifferWavePortFirmwareFrame	//extends WavePortFirmwareFrame
											extends CoronisFrame
											implements SnifferFrameInterface {
	private int[] timeStamp;
	private int[] Header;
	private int[] footer;
	private int direction;
	private boolean canDisplay;
	private String dateTime;
	
	public SnifferWavePortFirmwareFrame(final int cmd, final int[] msg,
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
	
	public String getFirmware(){
		if (data[0] != 'V') System.out.println("Firmware data should start with the V character");
        int transmission_mode = data[1] << 8;
        transmission_mode = transmission_mode | data[2];
        int firmware = data[3] << 8;
        firmware = firmware | data[4];
        return "V-" + Functions.printHumanHex(transmission_mode, false) + "-" + Functions.printHumanHex(firmware, false);
	}
	@Override
	public void setDateTime(final String date) {
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
