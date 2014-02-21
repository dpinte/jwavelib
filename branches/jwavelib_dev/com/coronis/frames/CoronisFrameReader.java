/*
 * CoronisFrameReader.java
 *
 * Created on 31 octobre 2007, 17:36
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-07-24 17:11:53 +0200 (Fri, 24 Jul 2009) $
 * $Revision: 123 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/frames/CoronisFrameReader.java $
 */

package com.coronis.frames;

import com.coronis.exception.CoronisException;
import com.coronis.exception.BadlyFormattedFrameException;
import java.io.*;

import com.coronis.logging.Logger;
import com.dipole.libs.Functions;

/**
 * Coronis frame reader class.
 * 
 * Coronis frame reader implements all the needed methods to read a frame
 * on a given inputstream (basically an opened inputstream on the serial line).
 * 
 * When reading a frame, it checks that is complies to the protocol definition,
 * then checks the CRC and finally returns the new read frame 
 * 
 * TODO : should be rewritten has a state machine.
 * 
 * @author dpinte
 */
public class CoronisFrameReader {

	private static final short SYNC_STATE = 0;
	private static final short STX_STATE = 1;
	private static final short LEN_STATE = 2;
	private static final short CMD_STATE = 3;
	private static final short DATA_STATE = 4;
	private static final short CRC_STATE = 5;
	private static final short ETX_STATE = 6;
	
	private InputStream _istream;

	public CoronisFrameReader(InputStream is) {
		_istream = is;
	}

	public void emptyBuffer() {
		/*
		 * This method is never really used with eWON applications because
		 * istream.available() is always equal to 0
		 */
		try {
			while (_istream.available() > 0) {
				_istream.read();
			}
		} catch (IOException e) {
			Logger.error("Error while emptying from inputstream");
		}
	}

	public boolean close() {
		try {
			_istream.close();
			// _istream = null;
		} catch (IOException ex) {
			Logger.error("Error while closing inputstream : " + ex.toString());
			return false;
		}
		return true;
	}

	public CoronisFrame readFrame() throws CoronisException, IOException {
		/*
		 * TODO : manage the problem of messages that are not fully in the
		 * buffer --> this must be refactored as a state machine !!!
		 * 
		 * TODO : istream.available is not running correctly on the eWON
		 * 
		 * Java byte are signed. They must be transformed as unsigned in order
		 * to be read correctly
		 */

		if (_istream == null) {
			throw new IOException("Inputstream is closed");
		}
		CoronisFrame rframe = null;
		
		int state = SYNC_STATE;
		int bitRead;
		
		int msgLength;
		int dataLength = 0;
		int dataIdx = 0;
		int crcIdx = 0;
		int crc_part1 = 0;
		int crc_part2 = 0;
		int command = 0;
		int crc = 0;
		int[] msg = null;
		boolean endFrame = false;
		
		try {
			do {
				bitRead = _istream.read();
				
				switch(state) {
					case SYNC_STATE:
						if(bitRead == CoronisFrame.CRN_SYN) {
							//_logger.debug("SYNC: "+ Functions.printHumanHex(bitRead, true));
							state = STX_STATE;
						}
						break;
						
					case STX_STATE:
						//_logger.debug("STX: "+ Functions.printHumanHex(bitRead, true));
						if(bitRead == CoronisFrame.CRN_STX) {						
							state = LEN_STATE;
						} else {
							throw new BadlyFormattedFrameException("No STX byte");
						}
						break;
						
					case LEN_STATE:
						//_logger.debug("LEN: "+ Functions.printHumanHex(bitRead, true));
						msgLength = bitRead;
						
						/* dataLength = msgLength - 1 - 1- 2 */
						dataLength = msgLength - 4;
						if(dataLength < 0) {
							throw new BadlyFormattedFrameException("Data Length cannot be negative");
						}
						msg = new int[dataLength];
						state = CMD_STATE;
						break;
						
					case CMD_STATE:
						//_logger.debug("CMD: "+ Functions.printHumanHex(bitRead, true));
						command = bitRead;
						
						state = (dataLength > 0) ? DATA_STATE : CRC_STATE;
						break;
						
					case DATA_STATE:
						//_logger.debug("DATA: "+ Functions.printHumanHex(bitRead, true) +" "+ dataIdx);
						msg[dataIdx] = bitRead;
						dataIdx++;
						
						if(dataIdx >= dataLength) {
							state = CRC_STATE;
						}
						break;
						
					case CRC_STATE:
						//_logger.debug("CRC: "+ Functions.printHumanHex(bitRead, true) +" "+ crcIdx);
						if(crcIdx == 0) {
							crc_part1 = bitRead;
						}
						
						if(crcIdx == 1) {
							crc_part2 = bitRead;
							
						}
						
						crcIdx++;
						if(crcIdx >= 2) {
							state = ETX_STATE;
						}
						break;
						
					case ETX_STATE:
						//_logger.debug("ETX: "+ Functions.printHumanHex(bitRead, true));
						if(bitRead == CoronisFrame.CRN_ETX) {							
							endFrame = true;
						} else {
							throw new BadlyFormattedFrameException("No ETX byte");
						}
						break;
				}
			} while (endFrame == false);
		} catch (IOException e) {
			Logger.debug("CoronisFrameReader :: Error while reading from inputstream - "+ e.getMessage());
			throw e;
		}
		
		// build frame
		Logger.debug(	"Build frame with : "
						+ Functions.printHumanHex(command, true) 
						+" - "
						+Functions.printHumanHex(msg, false));
		
		rframe = CoronisFrameBuilder.buildFrame(command, msg);
		Logger.frame(rframe.getFrameAsString(), true);
		
		// Validate CRC
		crc = (crc_part2 & 0xFF) << 8 | (crc_part1 & 0XFF);
		
		if (!rframe.checkCrc(crc)) {
			Logger.error(rframe.getFrameAsString());
			Logger.error("Bad CRC -" + crc + " - " + rframe.getCrc());
			throw new BadlyFormattedFrameException("Bad CRC: "+ crc +" - "+ rframe.getCrc());
		}

		return rframe;
	}
}
