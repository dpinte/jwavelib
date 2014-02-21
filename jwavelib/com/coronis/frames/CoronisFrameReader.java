/*
 * CoronisFrameReader.java
 *
 * Created on 31 octobre 2007, 17:36
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-06-01 14:37:14 +0200 (Mon, 01 Jun 2009) $
 * $Revision: 73 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/frames/CoronisFrameReader.java $
 */

package com.coronis.frames;

import com.coronis.exception.CoronisException;
import com.coronis.exception.BadlyFormattedFrameException;
import java.io.*;

import com.coronis.Config;
import com.coronis.logging.SimpleLogger;
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

	private InputStream _istream;
	private SimpleLogger _logger;

	public CoronisFrameReader(InputStream is) {
		_istream = is;
		try {
			_logger = Config.getLogger();
		} catch (Exception e) {
			System.err.println(e.toString());
			System.exit(1);
		}
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
			_logger.error("Error while emptying from inputstream");
		}
	}

	public boolean close() {
		try {
			_istream.close();
			// _istream = null;
		} catch (IOException ex) {
			_logger.error("Error while closing inputstream : " + ex.toString());
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

		int syn;
		int stx;
		int msgLength;
		int dataLength;
		int command;
		int crc;
		int[] msg;
		int etx;

		try {
			// read until found SYN

			do {
				// if (istream.available() > 0) syn = istream.read();
				// else return null;
				syn = _istream.read();
				// FIXME : this must be called in J2SE environment with RXTX !
				// if (Thread.interrupted()) {
				// Thread.currentThread().interrupt();
				// return null;
				// }
			} while (syn != CoronisFrame.CRN_SYN);

			// Found a SYN --> read and store until found
			// Read STX, LENGTH and CMD
			// if (istream.available() > 0) stx = istream.read();
			// else return null;
			stx = _istream.read();
			// Check STX
			if (stx != CoronisFrame.CRN_STX) {
				throw new BadlyFormattedFrameException("No STX byte");
			}
			// Read LENGTH
			// if (istream.available() > 0) msgLength = istream.read();
			// else return null;
			msgLength = _istream.read();
			// Read CMD
			// if (istream.available() > 0) command = istream.read();
			// else return null;
			command = _istream.read();

			// Read DATA
			dataLength = msgLength - 2 - 1 - 1;
			if (dataLength < 0) {
				_logger
						.error("DataLength cannot be negative. " + " SYN :"
								+ Functions.printHumanHex(syn, false)
								+ " STX :"
								+ Functions.printHumanHex(stx, false)
								+ " LEN :"
								+ Functions.printHumanHex(msgLength, false)
								+ " CMD is :"
								+ Functions.printHumanHex(command, false));
			}
			msg = new int[dataLength];
			for (int i = 0; i < dataLength; i++) {
				// if (istream.available() > 0) msg[i] = istream.read();
				// else return null;
				msg[i] = _istream.read();
			}
			// Read CRC
			int crc_part1 = _istream.read();
			int crc_part2 = _istream.read();
			crc = crc_part1 & 0XFF;
			crc = ((crc_part2 & 0xFF) << 8) | crc;
			etx = _istream.read();

			rframe = CoronisFrameBuilder.buildFrame(command, msg);

			_logger.frame(rframe.getMessage(), true);
			
			// Validate CRC
			if (!rframe.checkCrc(crc)) {
				_logger.error(rframe.getMessage());
				_logger.error("Bad CRC -" + crc + " - " + rframe.getCrc());
				throw new BadlyFormattedFrameException("Bad CRC ");
			}

			// Check ETX
			if (etx != CoronisFrame.CRN_ETX) {
				throw new BadlyFormattedFrameException("No ETX byte");
			}
		} catch (IOException e) {
			_logger
					.debug("CoronisFrameReader :: Error while reading from inputstream - "
							+ e.getMessage());
			throw e;
		}
		return rframe;
	}
}
