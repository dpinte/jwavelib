package frame;

public interface SnifferFrameInterface {
	public final static short SNI_SYN = 0xFF;
	public final static short SNI_FROM_MOD = 0x4D;
	public final static short SNI_FROM_TER = 0x54;
	public final static short SNI_FROM_MOD_OVR = 0x4E; 
	public final static short SNI_FROM_TER_OVR = 0x55;
	public final static String NOW = "Now";
	
	public void setDateTime(String date);
	public String getDateTime(boolean complete);
	public int[] getTimeStamp();
	public String getTimeStampStr();
	public String getData();
	public int getCmd();
	public int getSniffedCrc();
	public int getCalculatedCrc();
	public String getSniffedFrame();
	public int getDirection();
	public String getDirectionStr();
	public boolean isCrcOk();
	public boolean isStxOk();
	public boolean isEtxOk();
	public boolean isTimeStampOk();
	public boolean canDisplay();
	
	public void setDisplay(boolean i);
	
}
