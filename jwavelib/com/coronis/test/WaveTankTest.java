package com.coronis.test;

import com.coronis.exception.CoronisException;
import com.coronis.modules.WaveTank;
import com.coronis.logging.BasicLogger;
import com.coronis.Config;
import com.coronis.CoronisLib;
import com.dipole.libs.*;
import junit.framework.TestCase;

public class WaveTankTest extends TestCase {

	public void setUp() {
		/*
		 * Sets the logger into DEBUG mode
		 */
		Config.setLogger(new BasicLogger());
		Constants.DEBUG = true;
	}
	
	public void testModuleType() {
		/*
		 * WaveTank were previously identified as a specific hardware but now they are identified as WaveSense
		 */
		assertEquals("WaveTank", CoronisLib.getModuleTypeString(0x3A));
		assertEquals("WaveSense 0-5V", CoronisLib.getModuleTypeString(0x22));
	}
	
	public void testReadCurrentValue() {
		/*
		 * Test reading current value from a WaveTank frame
		 */
		
		int moduleid[] = {0x05, 0x22, 0x08, 0x30, 0x06, 0x7c};
		WaveTank wtk = new WaveTank(moduleid, null, null);
		
		try {
			// test with 100%
			int message[] = {0x05, 0x22, 0x08, 0x30, 0x06, 0x7c, 0x81, 0x00, 0x00, 0x0F, 0xFF};
			assertEquals(1.0, wtk.getCurrentValue(message), 0.001);
			// test with 0%
			message[9] = 0x00;
			message[10] = 0x00;
			assertEquals(0.0, wtk.getCurrentValue(message), 0.001);
			// test with 25%
			message[9] = 0x04;
			message[10] = 0x00;
			assertEquals(0.25, wtk.getCurrentValue(message), 0.001);			
			// test with 50%
			message[9] = 0x08;
			message[10] = 0x00;
			assertEquals(0.50, wtk.getCurrentValue(message), 0.001);		
			// test with 75%
			message[9] = 0x0C;
			message[10] = 0x00;
			assertEquals(0.75, wtk.getCurrentValue(message), 0.001);				
		} catch (CoronisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
}
