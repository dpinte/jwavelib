package com.coronis.test;

import com.coronis.CoronisLib;
import com.coronis.exception.ConfigException;
import com.dipole.libs.Functions;

import junit.framework.TestCase;

public class InternalTest extends TestCase {
	/*
	 * Internal tests that must not go to clients
	 */

	public void testComputeHardwareKey() {
		/*
		 * Test the computation of a hardware key based on the eWON serial
		 * number
		 */
		String msg = "0725-0002-73";
		int[] msgInt = new int[msg.length()];
		for (int i = 0; i < msgInt.length; i++) {
			msgInt[i] = msg.charAt(i) & 0xFF;
		}
		int crc = CoronisLib.calculateCrc(msgInt);
		System.out.println("Serial crc is :"
				+ Functions.printHumanHex(crc, true));
		String key = "E949";
		try {
			int[] keys = CoronisLib.moduleIdFromString(key);
			int crc2 = (keys[0] & 0xFF) << 8;
			crc2 = crc2 | keys[1];
			assertEquals(crc, crc2);
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void testGenerateHardwareKey() {
		/*
		 * This is not a real test but what needed to be an application to
		 * generate the hardware key for eWON based on their serial number
		 * 
		 * TODO : extract this outside of tests
		 */
		String msg = "0727-0012-73";
		int[] msgInt = new int[msg.length()];
		for (int i = 0; i < msgInt.length; i++) {
			msgInt[i] = msg.charAt(i) & 0xFF;
		}
		int crc = CoronisLib.calculateCrc(msgInt);
		System.out.println("Serial crc is :"
				+ Functions.printHumanHex(crc, true));
	}

}
