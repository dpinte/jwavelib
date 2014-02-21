package frame;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.coronis.frames.CoronisFrame;
import com.coronis.frames.ResRemoteRSSIFrame;
import com.dipole.libs.Functions;

public class SnifferResRemoteRSSIFrame 	extends ResRemoteRSSIFrame
										implements SnifferFrameInterface {
	private int[] timeStamp;
	private int[] Header;
	private int[] footer;
	private int direction;
	private boolean canDisplay;
	private String dateTime;
	
	public SnifferResRemoteRSSIFrame(int cmd, int[] msg, int[]ts,
										int[] head, int[] foot, int dir) {
		super(cmd, msg);
		
		this.timeStamp = ts;
		this.Header = head;
		this.footer = foot;
		this.direction = dir;
		this.canDisplay = true;
		
		this.setDateTime(SnifferFrameInterface.NOW);
	}

	@Override
	public void setDateTime(String date){
		if(date.equals(SnifferFrameInterface.NOW)){
			DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
			DateFormat timeFormat = new SimpleDateFormat("hh':'mm':'ss");
			Date now = new Date();
			this.dateTime = dateFormat.format(now) + " " +timeFormat.format(now);
		} else {
			this.dateTime = date;
		}
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
