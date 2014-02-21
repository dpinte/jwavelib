/**
 * 
 */
package com.coronis.test.frames;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.coronis.frames.CoronisFrame;

/**
 * @author antoine
 *
 */
public class CoronisFrameTest {
	private static final int[] message = { 0x43, 0x06, 0x01, 0x00, 0x00, 0x02, 0x01};
	private static final int command = 0x20;
	private static final int[] outputFrame = {0xFF, 0x02, 0x0B, 0x20, 0x43, 0x06, 0x01, 0x00, 0x00, 0x02, 0x01, 0xD2, 0x41, 0x03};
	private static final int crc = 16850;
	
	private static CoronisFrame frame;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		frame = new CoronisFrame(command, message);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.coronis.frames.CoronisFrame#checkCrc(int)}.
	 */
	@Test
	public final void testCheckCrc() {
		assertTrue(frame.checkCrc(crc));
	}

	/**
	 * Test method for {@link com.coronis.frames.CoronisFrame#getCrc()}.
	 */
	@Test
	public final void testGetCrc() {
		assertEquals(crc, frame.getCrc());
	}

	/**
	 * Test method for {@link com.coronis.frames.CoronisFrame#getFrame()}.
	 */
	@Test
	public final void testGetFrame() {
		assertArrayEquals(outputFrame, frame.getFrame());
	}

	/**
	 *  Test method for {@link com.coronis.frames.CoronisFrame#getFrameAsString()}.
	 */
	@Test
	public final void testGetFrameAsString() {
		assertEquals("FF020B2043060100000201D24103", frame.getFrameAsString());
	}
	
	/**
	 * Test method for {@link com.coronis.frames.CoronisFrame#getData()}.
	 */
	@Test
	public final void testData() {
		assertArrayEquals(message, frame.getData());
	}

	/**
	 * Test method for {@link com.coronis.frames.CoronisFrame#getByteArray()}.
	 */
	@Test
	@Ignore
	public final void testGetByteArray() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.coronis.frames.CoronisFrame#getMessageLength()}.
	 */
	@Test
	public final void testGetFrameLength() {
		assertEquals(outputFrame.length, frame.getFrameLength());
	}

	/**
	 * Test method for {@link com.coronis.frames.CoronisFrame#isACK()}.
	 */
	@Test
	public final void testIsACK() {
		assertFalse(frame.isACK());
	}

	/**
	 * Test method for {@link com.coronis.frames.CoronisFrame#mustACK()}.
	 */
	@Test
	public final void testMustACK() {
		assertFalse(frame.mustACK());
	}

	/**
	 * Test method for {@link com.coronis.frames.CoronisFrame#getCmd()}.
	 */
	@Test
	public final void testGetCmd() {
		assertEquals(command, frame.getCmd());
	}

}
