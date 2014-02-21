package com.coronis.test.waveport;

import java.io.*;
import java.util.LinkedList;

import com.coronis.Config;
import com.coronis.frames.*;
import com.coronis.exception.CoronisException;
import com.coronis.logging.BasicLogger;
import com.dipole.libs.Constants;

import junit.framework.TestCase;

public class WavePortTest extends TestCase {
	
	protected void setUp() throws Exception {
		Config.setLogger(new BasicLogger());
		Constants.DEBUG = true;
		Constants.DEBUG_CORONIS_FRAMES = true;
		
	}	
	
	public void testVirtualWavePort(){
		VirtualWavePort wpt = new VirtualWavePort("virtual waveport", null);
		assertEquals("virtual waveport", wpt.getName());
		LinkedList st = new LinkedList();
		int[] firmware = {0x56, 0x00, 0xB6, 0x04, 0x03};
		try {
			st.addLast(new Action(200,  CoronisFrameBuilder.getACK()));
			st.addLast(new Action(200, CoronisFrameBuilder.buildFrame(CoronisFrame.RES_FIRMWARE_VERSION, firmware)));
		} catch (CoronisException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		wpt.load_response(st);
		wpt.connect();		
		try {			
			assertTrue(wpt.checkConnection());
			assertEquals(wpt.version, "V-B6-0403");
		} catch (InterruptedIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoronisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
