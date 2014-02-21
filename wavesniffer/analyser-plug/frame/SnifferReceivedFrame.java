package frame;

import com.coronis.frames.CoronisFrame;
import com.coronis.frames.ReceivedFrame;
import com.dipole.libs.Functions;
import common.Common;

public class SnifferReceivedFrame 	extends ReceivedFrame
									implements SnifferFrameInterface {
	private int[] timeStamp;
	private int[] Header;
	private int[] footer;
	private int direction;
	private boolean canDisplay;
	private String dateTime;

	public SnifferReceivedFrame(int cmd, int[] msg, int[]ts,
								int[] head, int[] foot, int dir) {
		super(cmd, msg);
		
		this.timeStamp = ts;
		this.Header = head;
		this.footer = foot;
		this.direction = dir;
		this.canDisplay = true;
		this.dateTime = Common.getCurentDateTime();	
	}
	
	@Override
	public void setDateTime(String date){
		this.dateTime = date;
	}
	
	@Override
	public boolean canDisplay() {
		return this.canDisplay;
	}

	@Override
	public String getData() {
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
	public void setDisplay(boolean i) {
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
		return ""+ Functions.printHumanHex(this.Header, false) + Functions.printHumanHex(this.getCmd(), false) + this.getData() + Functions.printHumanHex(this.footer, false);
	}

	@Override
	public int getCalculatedCrc() {
		return this.getCrc();
	}

	
	@Override
	public String getDateTime(boolean complete) {
		return this.dateTime;
	}
}
