/*
 * CoronisUnitTestsJ2SE.java
 *
 * Created on 31 octobre 2007, 17:56
 * 
 * CoronisUnitTestsJ2SE class is a unittest class to check the behaviour of 
 * the Coronis protocol's implementation.
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 *
 * $Date: 2009-06-09 23:46:42 +0200 (Tue, 09 Jun 2009) $
 * $Revision: 82 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/test/CoronisUnitTestsJ2SE.java $
 */
package com.coronis.test;

import com.coronis.CoronisLib;
import com.dipole.libs.Functions;
import com.coronis.exception.ConfigException;
import com.coronis.frames.CoronisFrame;
import com.coronis.modules.WaveFlow;

import junit.framework.TestCase;

public class CoronisUnitTestsJ2SE extends TestCase {



	
	public void testLogging() {
		int[] message;
		try {
			message = CoronisLib.moduleIdFromString("FF023D30051606304A44890101080908010932000107DD07D50000001D0000001D0000001D0000001D0000001D0000001D0000001D0000001D0000001D47A603");
			int cmd = message[3];
			// length of data is frame length - LEN - CMD - CRC
			int[] data = new int[message[2]-2-1-1 ];
			for( int i=0; i< data.length; i++) {
				data[i] = message[2+1+1+i];
			}
			System.out.println(Functions.printHumanHex(data, false));
			System.out.println(message.length);
			System.out.println(data.length);		
			CoronisFrame fr = new CoronisFrame(cmd, data);
			int crc = message[message.length-3] & 0xFF ;		
			crc = (message[message.length-2] & 0xFF ) << 8 | crc;
			System.out.println(Functions.printHumanHex(message, false));	
			System.out.println(fr.getMessage());
			System.out.println("CRC " + crc + " " + fr.getCrc());
			assertEquals(crc, fr.getCrc());
			int[] message2 =  CoronisLib.moduleIdFromString("3D30051606304A44890101080908010932000107DD07D50000001D0000001D0000001D0000001D0000001D0000001D0000001D0000001D0000001D");
			int coronisCrc = 0xA647;
			assertEquals(CoronisLib.calculateCrc(message2), coronisCrc);		
			WaveFlow wth1 = new WaveFlow(CoronisLib.moduleIdFromString("051906302D69") , null, null);
			try {
				System.out.println(wth1.readExtendedDatalog(fr.getBinaryMessage(), false));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		} catch (ConfigException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//int[] message =  CoronisLib.moduleIdFromString("ff027430051906302d69830481011c011a0119011701150112010e010b010c010c010c010c010c010a010c010a010a010e010e010e010f0110010f01100110011001100110011101110112011201130113011301140114011501150116011701170116011701170118011801190c0b0701082e0a146a03");
		
		
		
	}
	

	
}

