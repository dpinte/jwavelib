package com.coronis.test;

//J2ME has no java.text package
//import java.text.SimpleDateFormat;
//import java.util.GregorianCalendar;
import java.util.Calendar;

import com.coronis.CoronisLib;
import com.coronis.exception.ConfigException;

import junit.framework.TestCase;

public class CoronisLibTest extends TestCase {

	/*
	 * Verify the conversion between frequencies in milliseconds and Coronis
	 * frequencies
	 */
	public void testGetCoronisFrequency() {

		try {
			// 15 minutes frequency must match (0x03 << 2) | 0x01
			assertEquals(0x0D, CoronisLib.getDataloggingFrequency(15 * 1000 * 60));
			// 31h30 minutes frequency must match (0x3F << 2) | 0x11
			assertEquals(0xFF, CoronisLib
					.getDataloggingFrequency(63 * 30 * 1000 * 60));
			// 45 minutes frequency must match (0x03 << 2) | 0x10
			assertEquals(0x0E, CoronisLib.getDataloggingFrequency(45 * 1000 * 60));
			// 4 minutes frequency must match (0x04 << 2) | 0x00
			assertEquals(0x10, CoronisLib.getDataloggingFrequency(4 * 1000 * 60));
		} catch (ConfigException e) {
			fail("Error during test :" + e.toString());
		}
	}

	public void testCalculateCrc() {
		/*
		 * Test if computing CRC on frames does work according to the
		 * documentation
		 */
		// Official example from CORONIS documentation
		int[] message = { 0x0B, 0x20, 0x43, 0x06, 0x01, 0x00, 0x00, 0x02, 0x01 };
		int crc = CoronisLib.calculateCrc(message);
		assertTrue(crc == 0x41D2);
		// Another test with sample coming from tests in another project
		int[] message1 = { 0x0B, 0x20, 0x01, 0x19, 0x05, 0x30, 0x00, 0x8B, 0x01 };
		assertTrue(CoronisLib.calculateCrc(message1) == 0xF460);

		try {
			int[] message4 = CoronisLib
					.moduleIdFromString("7430051906302d69830481016c016d0170016c016801660160016101610162016201620161015f0160015e015b0159015801550152014c014901480144013c013c013c013b013a013901390139013801380138013901380138013901380138013701370137013701360135150b070310100a");
			// Crc should be 0xA487
			assertEquals(CoronisLib.calculateCrc(message4), 0xA487);
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void testRSSIPercentage() {
		assertEquals(100, CoronisLib.getRSSIPercentage(0x20, 0x20));
		assertEquals(100, CoronisLib.getRSSIPercentage(0x32, 0x20));
		assertEquals(0, CoronisLib.getRSSIPercentage(0x00, 0x20));
	}

	public void testParseDataloggingFrequency() {
		/*
		 * Checks that the CoronisLib.parseDataloggingFrequency is running correctly
		 */
		try {
			assertEquals(15 * 60 * 1000, CoronisLib
					.parseDataloggingFrequency(CoronisLib
							.getDataloggingFrequency(15 * 60 * 1000)));
			assertEquals(5 * 60 * 1000, CoronisLib
					.parseDataloggingFrequency(CoronisLib
							.getDataloggingFrequency(5 * 60 * 1000)));
			assertEquals(1 * 60 * 1000, CoronisLib
					.parseDataloggingFrequency(CoronisLib
							.getDataloggingFrequency(1 * 60 * 1000)));
			assertEquals(30 * 60 * 1000, CoronisLib
					.parseDataloggingFrequency(CoronisLib
							.getDataloggingFrequency(30 * 60 * 1000)));
			assertEquals(60 * 60 * 1000, CoronisLib
					.parseDataloggingFrequency(CoronisLib
							.getDataloggingFrequency(60 * 60 * 1000)));
			assertEquals(65 * 60 * 1000, CoronisLib
					.parseDataloggingFrequency(CoronisLib
							.getDataloggingFrequency(65 * 60 * 1000)));
			
			// Testing 48 * 5 minutes = 4 hours in milliseconds !
			int abyte = (0x30 << 2) | 0x1;
			int freq = CoronisLib.parseDataloggingFrequency(abyte);		
			assertEquals(freq, 240 * 60 * 1000);
			
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testCoronisDateTime() {
		/*
		 * Test if parsing dates coming from a Coronis frame does work according to the documentation
		 * ! J2ME has no java.text package : cannot use the following packages
		 *   import java.text.SimpleDateFormat; 
		 *   import java.util.GregorianCalendar; 
		 */
		try {
			//SimpleDateFormat dfmt = new SimpleDateFormat(CoronisLib.DATEFORMAT);			
			int[] message = { 0x92, 0x01, 0x0B, 0x07, 0x00, 0x0A, 0x19};
			Calendar cld = CoronisLib.parseDateTime(message, 1);		
			//TODO : GregorianCalendar must be changed to Calendar
            //Calendar cld2 = new GregorianCalendar(2007,11-1,1,10,25);
            Calendar cld2 = CoronisLib.createCalendar(2007, 11, 1, 10, 25);
			System.out.println(cld.getTime());
            System.out.println(cld2.getTime());					
			assertTrue(cld2.getTime().getTime() - cld.getTime().getTime()  < 10);
			int[] message2 = {0x92, 0x05, 0x0B, 0x07, 0x01, 0x0C, 0x00};
			cld = CoronisLib.parseDateTime(message2, 1);			
			//cld2 = new GregorianCalendar(2007,11-1,5,12,0);
            cld2 = CoronisLib.createCalendar(2007, 11, 5, 12, 0);
            //System.out.println(cld.getTime().toString());
            //System.out.println(cld2.getTime().toString());
			assertEquals(cld2.getTime(), cld.getTime());			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	public void testModuleIdParsing() {
		int[] modid = {0x05, 0x19, 0x06, 0x30, 0x2D, 0x69};
		String identification = "051906302D69";
		try {
			int[] comid = CoronisLib.moduleIdFromString(identification);
			for (int i =0; i < modid.length; i++) {
				assertEquals(modid[i], comid[i]);
			}
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

}
