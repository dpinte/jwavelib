package com.coronis.test.frames;

import junit.framework.TestCase;

import com.coronis.exception.CoronisException;
import com.coronis.frames.CoronisFrameBuilder;
import com.coronis.frames.CoronisFrame;
import com.coronis.frames.ACKFrame;
import com.coronis.frames.ResSendFrame;
import com.coronis.frames.ReceivedFrame;
import com.coronis.frames.ReceivedMultiFrame;

public class CoronisFrameBuilderTest extends TestCase {

	public void testBuildAckFrame() {
		CoronisFrame cfr;
		// Test ACK
		int[] msg = {};
		try {
			cfr = CoronisFrameBuilder.buildFrame(CoronisFrame.CMD_ACK, msg);
			assertTrue(cfr instanceof ACKFrame);
		} catch (CoronisException e) {
			fail("CoronisException : " + e.toString());
		}
	}
		
	public void testBuildResSendFrame() {
		CoronisFrame cfr;
		// Test ACK
		int[] msg = {};
		// Test ResSendFrame		
		try {
			cfr = CoronisFrameBuilder.buildFrame(CoronisFrame.RES_SEND_FRAME, msg);
			assertTrue(cfr instanceof ResSendFrame);
		} catch (CoronisException e) {
			fail("CoronisException : " + e.toString());
		}
	}
	
	public void testResSendServiceBuildFrame() {
		CoronisFrame cfr;
		// Test ACK
		int[] msg = {};
		// Test ResSendFrame		
		try {
			cfr = CoronisFrameBuilder.buildFrame(CoronisFrame.RES_SEND_SERVICE, msg);
			assertTrue(cfr instanceof ReceivedFrame);
		} catch (CoronisException e) {
			fail("CoronisException : " + e.toString());
		}
	}
	
	public void testServiceResponseBuildFrame() {
		CoronisFrame cfr;
		// Test ACK
		int[] msg = {};
		// Test ResSendFrame		
		try {
			cfr = CoronisFrameBuilder.buildFrame(CoronisFrame.SERVICE_RESPONSE, msg);
			assertTrue(cfr instanceof ReceivedFrame);
		} catch (CoronisException e) {
			fail("CoronisException : " + e.toString());
		}
	}
	
	public void testReceivedFrameBuildFrame() {
		CoronisFrame cfr;
		// Test ACK
		int[] msg = {};
		// Test ResSendFrame		
		try {
			cfr = CoronisFrameBuilder.buildFrame(CoronisFrame.RECEIVED_FRAME, msg);
			assertTrue(cfr instanceof ReceivedFrame);
		} catch (CoronisException e) {
			fail("CoronisException : " + e.toString());
		}
	}
	
	public void testReceivedMultiFrameBuildFrame() {
		CoronisFrame cfr;
		// Test ACK
		int[] msg = {};
		// Test ResSendFrame		
		try {
			cfr = CoronisFrameBuilder.buildFrame(CoronisFrame.RECEIVED_MULTIFRAME, msg);
			assertTrue(cfr instanceof ReceivedMultiFrame);
		} catch (CoronisException e) {
			fail("CoronisException : " + e.toString());
		}
	}

	public void testGetACK() {
		CoronisFrame cfr;
		cfr = CoronisFrameBuilder.getACK();
		assertTrue(cfr instanceof ACKFrame);
	}

}
