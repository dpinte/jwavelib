/**
 * 
 */
package com.coronis.test.modules;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.LinkedList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.coronis.exception.CoronisException;
import com.coronis.frames.CoronisFrame;
import com.coronis.frames.CoronisFrameBuilder;
import com.coronis.test.virtualComp.Action;
import com.coronis.test.virtualComp.VirtualWavePort;

/**
 * @author antoine
 *
 */
public class WavePortTest {
	private static VirtualWavePort wpt;
	private static LinkedList<Action> st;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		wpt = new VirtualWavePort("virtual waveport", null);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		wpt = null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		st = new LinkedList<Action>();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		st = null;
	}

	/**
	 * Test method for {@link com.coronis.modules.WavePort#getName()}.
	 */
	@Test
	public final void testGetName() {
		assertEquals("virtual waveport", wpt.getName());
	}
	
	/**
	 * Test method for {@link com.coronis.modules.WavePort#checkConnection()}.
	 * @throws CoronisException 
	 * @throws IOException 
	 * @throws InterruptedIOException 
	 */
	@Test
	public final void testCheckConnection() throws CoronisException, InterruptedIOException, IOException {
		int[] firmware = {0x56, 0x00, 0xB6, 0x04, 0x03};
		st.addLast(new Action(200,  CoronisFrameBuilder.getACK()));
		st.addLast(new Action(200, CoronisFrameBuilder.buildFrame(CoronisFrame.RES_FIRMWARE_VERSION, firmware)));
		wpt.load_response(st);
		wpt.connect();
		
		assertTrue(wpt.checkConnection());
		assertEquals(wpt.version, "V-B6-0403");
	}
}
