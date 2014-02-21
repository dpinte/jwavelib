package com.coronis.test;

import com.coronis.CoronisLib;

import junit.framework.TestCase;

public class WaveThermTest extends TestCase {
	
	public void testWaveThermDallas() {
		/*
		 * Test if reading temperatures coming from frames does work according to the documentation
		 */			
		try {
			// Test with value +125°C bits : 0000 0111 1101 0000  hexa : 0x07D0
			int MSB = 0x07;
			int LSB = 0xD0;
			double value = CoronisLib.parseDallasTemp(MSB, LSB);		
			assertTrue(value== 125.0);
			// Test with value +85°C 0000 0101 0101 0000 0x0550
			MSB = 0x05;
			LSB = 0x50;
			value = CoronisLib.parseDallasTemp(MSB, LSB);
			assertTrue(value == 85.0);
			//Test with value -10.125°C 1111 1111 0101 1110 0xFF5E
			MSB = 0xFF;
			LSB = 0x5E;
			value = CoronisLib.parseDallasTemp(MSB, LSB);
			assertTrue(value == -10.125);
			//Test with value -55°C 1111 1100 1001 0000 0xFC90
			MSB = 0xFC;
			LSB = 0x90;
			value = CoronisLib.parseDallasTemp(MSB, LSB);
			assertTrue(value == -55.0);
			// Test with -25.0625   +125°C 1111 1110 0110 1111 0xFE6F
			MSB = 0xFE;
			LSB = 0x6F;
			value = CoronisLib.parseDallasTemp(MSB, LSB);
			assertTrue(value == -25.0625);
			MSB = 0x01;
			LSB = 0x41;
			value = CoronisLib.parseDallasTemp(MSB, LSB);
			assertTrue(value == 20.0625);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
