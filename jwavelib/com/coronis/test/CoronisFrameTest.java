package com.coronis.test;

import com.coronis.frames.CoronisFrame;

import junit.framework.TestCase;

public class CoronisFrameTest extends TestCase {

	public void testCoronisFrameCreation() {
		/*
		 * Test if frame creation does work according to the documentation
		 */
		int[] message = { 0x43, 0x06, 0x01, 0x00, 0x00, 0x02, 0x01};
		int command = 0x20; 
		CoronisFrame myframe = new CoronisFrame(command, message);
		int[] desiredOutput = {0xFF, 0x02, 0x0B, 0x20, 0x43, 0x06, 0x01, 0x00, 0x00, 0x02, 0x01, 0xD2, 0x41, 0x03};
		int[] output = myframe.getFrame();
		for (int i = 0; i < output.length; i++) {
			assertEquals(output[i], desiredOutput[i]);
		}	
	}
	
}
