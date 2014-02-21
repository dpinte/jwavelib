package com.dipole.jwavetool.frame;

public interface SnifferFrameInterface {

	public final static int SNI_SYN = 0xFF;
	public final static int SNI_FROM_MOD = 0x4D;
	public final static int SNI_FROM_TER = 0x54;
	public final static int SNI_FROM_MOD_OVR = 0x4E; 
	public final static int SNI_FROM_TER_OVR = 0x55;
	public final static String NOW = "Now";
	
	/**
	 * Set the date of frame creation
	 * @param date	A string witch represent a date
	 */
	public void setDateTime(String date);
	
	/**
	 * Get the date od date creation
	 * @param complete
	 * @return A string witch represent the date
	 */
	public String getDateTime(boolean complete);
	
	/**
	 * Get timestamp values for each frame bytes
	 * @return An integer array 
	 */
	public int[] getTimeStamp();
	
	/**
	 * Get timestamp values for each frame bytes
	 * @return A string
	 */
	public String getTimeStampStr();
	
	/**
	 * Get the DATA field of the frame
	 * @return A human readable representation of the DATA field
	 */
	public String getDataAsString();
	
	/**
	 * Get the CMD field of a frame
	 * @return The frame command
	 */
	public int getCmd();
	
	/**
	 * Get the CRC field
	 * @return The CRC
	 */
	public int getSniffedCrc();
	
	/**
	 * Calculate the CRC of the frame
	 * @return The CRC
	 */
	public int getCalculatedCrc();
	
	/**
	 * Get the complete frame
	 * @return A human readable representation of the frame
	 */
	public String getSniffedFrame();
	
	/**
	 * Get the direction/source of the frame
	 * @return The direction/source
	 */
	public int getDirection();
	
	/**
	 * Get the direction/source of the frame
	 * @return A string witch represent the Direction/source
	 */
	public String getDirectionStr();
	
	/**
	 * Check the CRC field
	 * @return True is CRC is Correct
	 */
	public boolean isCrcOk();
	
	/**
	 * Check the STX field
	 * @return True is STX is correct
	 */
	public boolean isStxOk();
	
	/**
	 * Check the ETX field
	 * @return True if ETX is correct
	 */
	public boolean isEtxOk();
	
	/**
	 * Check The timeStamp
	 * @return True if all timestamps are correct
	 */
	public boolean isTimeStampOk();
	
	/**
	 * Check if a frame can be displayed
	 * @return True if the frame can be displayed
	 */
	public boolean canDisplay();
	
	/**
	 * 
	 * @param i True if the frame can be displayed 
	 */
	public void setDisplay(boolean i);
	
}
