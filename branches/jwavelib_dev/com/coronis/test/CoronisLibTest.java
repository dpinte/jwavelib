/**
 * 
 */
package com.coronis.test;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Ignore;
import org.junit.Test;

import com.coronis.CoronisLib;
import com.coronis.exception.ConfigException;
import com.coronis.exception.MissingDataException;

/**
 * @author antoine
 *
 */
public class CoronisLibTest {

	/**
	 * Test method for {@link com.coronis.CoronisLib#calculateCrc(int[])}.
	 */
	@Test
	public final void testCalculateCrc() {
		// Official example from CORONIS documentation
		int[] message = { 0x0B, 0x20, 0x43, 0x06, 0x01, 0x00, 0x00, 0x02, 0x01 };
		assertEquals(0x41D2, CoronisLib.calculateCrc(message));
		
		// Another test with sample coming from tests in another project
		int[] message1 = { 0x0B, 0x20, 0x01, 0x19, 0x05, 0x30, 0x00, 0x8B, 0x01 };
		assertEquals(0xF460, CoronisLib.calculateCrc(message1));
		
	}

	/**
	 * Test method for {@link com.coronis.CoronisLib#parseDateTime(int[], int)}.
	 */
	@Test
	@Ignore
	public final void testParseDateTime() {
		int[] message = { 0x92, 0x01, 0x0B, 0x07, 0x00, 0x0A, 0x19};
		Calendar cld = CoronisLib.parseDateTime(message, 1);
		Calendar cld2 = new GregorianCalendar(2007,11-1,1,10,25);
		System.out.println(cld2.getTimeInMillis()+ "  " + cld.getTimeInMillis());
		System.out.println(cld2.compareTo(cld));
		
		assertEquals(cld2.getTime().getTime(), cld.getTime().getTime());
	}

	/**
	 * Test method for {@link com.coronis.CoronisLib#createCalendar(int, int, int, int, int)}.
	 */
	@Test
	@Ignore
	public final void testCreateCalendar() {
		fail("Not yet implemented"); // TODO
	}


	/**
	 * Test method for {@link com.coronis.CoronisLib#parseDataloggingFrequency(int)}.
	 * @throws ConfigException 
	 */
	@Test
	public final void testParseDataloggingFrequency() throws ConfigException {
			assertEquals(15 * 60 * 1000,
						CoronisLib.parseDataloggingFrequency(CoronisLib
									.getDataloggingFrequency(15 * 60 * 1000)));
			
			assertEquals(5 * 60 * 1000,
						CoronisLib.parseDataloggingFrequency(CoronisLib
									.getDataloggingFrequency(5 * 60 * 1000)));
			
			assertEquals(1 * 60 * 1000, 
						CoronisLib.parseDataloggingFrequency(CoronisLib
									.getDataloggingFrequency(1 * 60 * 1000)));
			
			assertEquals(30 * 60 * 1000,
						CoronisLib.parseDataloggingFrequency(CoronisLib
									.getDataloggingFrequency(30 * 60 * 1000)));
			
			assertEquals(60 * 60 * 1000,
						CoronisLib.parseDataloggingFrequency(CoronisLib
									.getDataloggingFrequency(60 * 60 * 1000)));
			
			assertEquals(65 * 60 * 1000,
						CoronisLib.parseDataloggingFrequency(CoronisLib
									.getDataloggingFrequency(65 * 60 * 1000)));
			
			// Testing 48 * 5 minutes = 4 hours in milliseconds !
			int abyte = (0x30 << 2) | 0x1;
			int freq = CoronisLib.parseDataloggingFrequency(abyte);		
			assertEquals(freq, 240 * 60 * 1000);
	}

	/**
	 * Test method for {@link com.coronis.CoronisLib#getDataloggingFrequency(int)}.
	 * @throws ConfigException 
	 */
	@Test
	public final void testGetDataloggingFrequency() throws ConfigException {
		// 15 minutes frequency must match (0x03 << 2) | 0x01
		assertEquals(0x0D, CoronisLib.getDataloggingFrequency(15 * 1000 * 60));
		
		// 31h30 minutes frequency must match (0x3F << 2) | 0x11
		assertEquals(0xFF, CoronisLib.getDataloggingFrequency(63 * 30 * 1000 * 60));
		
		// 45 minutes frequency must match (0x03 << 2) | 0x10
		assertEquals(0x0E, CoronisLib.getDataloggingFrequency(45 * 1000 * 60));
		
		// 4 minutes frequency must match (0x04 << 2) | 0x00
		assertEquals(0x10, CoronisLib.getDataloggingFrequency(4 * 1000 * 60));
	}

	/**
	 * Test method for {@link com.coronis.CoronisLib#moduleIdFromString(java.lang.String)}.
	 * @throws ConfigException 
	 */
	@Test
	public final void testModuleIdFromString() throws ConfigException {
		int[] modid = {0x05, 0x19, 0x06, 0x30, 0x2D, 0x69};
		String identification = "051906302D69";
		int[] comid = CoronisLib.moduleIdFromString(identification);
		
		for (int i =0; i < modid.length; i++) {
			assertEquals(modid[i], comid[i]);
		}
	}

	/**
	 * Test method for {@link com.coronis.CoronisLib#getRSSIPercentage(int, int)}.
	 */
	@Test
	public final void testGetRSSIPercentage() {
		assertEquals(100, CoronisLib.getRSSIPercentage(0x20, 0x20));
		assertEquals(100, CoronisLib.getRSSIPercentage(0x32, 0x20));
		assertEquals(0, CoronisLib.getRSSIPercentage(0x00, 0x20));
	}

	/**
	 * Test method for {@link com.coronis.CoronisLib#getModuleTypeString(int)}.
	 */
	@Test
	public final void testGetModuleTypeString() {
		assertEquals("Wavetherm Dallas", CoronisLib.getModuleTypeString(0x19));
		assertEquals("Unknown module. Typs is 0x0A", CoronisLib.getModuleTypeString(10));
	}

}
