/**
 * 
 */
package com.coronis.test.frames;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.coronis.exception.CoronisException;
import com.coronis.frames.CoronisFrame;
import com.coronis.frames.CoronisFrameReader;
import com.coronis.logging.Logger;
import com.coronis.test.virtualComp.VirtualCoronisFrame;

/**
 * @author antoine
 *
 */
public class CoronisFrameReaderTest {
	private static final int[] msg = {0x00}; //{0x07, 0x01, 0x02, 0x16, 0x06, 0x30, 0x4F, 0x7A};
	private static final int cmd = 0x21; //0x40;
	private static final int[] msg1 = {0x05, 0x19, 0x06, 0x30, 0x28, 0xB5, 0x06, 0x00, 0x3B, 0x00, 0x00};
	private static final int cmd1 = 0x20;

	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	 * Test method for {@link com.coronis.frames.CoronisFrameReader#readFrame()}.
	 * @throws IOException 
	 * @throws CoronisException 
	 */
	@Test
	public final void testReadFrameOK() throws CoronisException, IOException {
		CoronisFrame frameOk = new CoronisFrame(cmd, msg);
		CoronisFrameReader reader = new CoronisFrameReader(new ByteArrayInputStream(frameOk.getByteArray()));
		CoronisFrame frame = reader.readFrame();
		
		CoronisFrame frameOk1 = new CoronisFrame(cmd1, msg1);
		CoronisFrameReader reader1 = new CoronisFrameReader(new ByteArrayInputStream(frameOk1.getByteArray()));
		CoronisFrame frame1 = reader1.readFrame();
	}
	
	/**
	 * Test method for {@link com.coronis.frames.CoronisFrameReader#readFrame()} with a bad STX.
	 * @throws IOException 
	 * @throws CoronisException 
	 */
	@Test(expected=CoronisException.class)
	public final void testReadFrameBadSTX() throws CoronisException, IOException {
		VirtualCoronisFrame frameStx = new VirtualCoronisFrame(cmd, msg);
		frameStx.setBadSTX();
		
		CoronisFrameReader reader = new CoronisFrameReader(new ByteArrayInputStream(frameStx.getByteArray()));
		CoronisFrame frame = reader.readFrame();
	}
	
	/**
	 * Test method for {@link com.coronis.frames.CoronisFrameReader#readFrame()} with a bad LEN.
	 * @throws IOException 
	 * @throws CoronisException 
	 */
	@Test(expected=CoronisException.class)
	public final void testReadFrameBadLEN() throws CoronisException, IOException {
		VirtualCoronisFrame frameLen = new VirtualCoronisFrame(cmd, msg);
		frameLen.setMessageLength(0);
		
		CoronisFrameReader reader = new CoronisFrameReader(new ByteArrayInputStream(frameLen.getByteArray()));
		CoronisFrame frame = reader.readFrame();
	}
	
	/**
	 * Test method for {@link com.coronis.frames.CoronisFrameReader#readFrame()} with a bad CRC.
	 * @throws IOException 
	 * @throws CoronisException 
	 */
	@Test(expected=CoronisException.class)
	public final void testReadFrameBadCRC() throws CoronisException, IOException {
		VirtualCoronisFrame frameCrc = new VirtualCoronisFrame(cmd, msg);
		frameCrc.setCRC(4242);
		
		CoronisFrameReader reader = new CoronisFrameReader(new ByteArrayInputStream(frameCrc.getByteArray()));
		CoronisFrame frame = reader.readFrame();
	}
	
	/**
	 * Test method for {@link com.coronis.frames.CoronisFrameReader#readFrame()} with a bad ETX.
	 * @throws IOException 
	 * @throws CoronisException 
	 */
	@Test(expected=CoronisException.class)
	public final void testReadFrameBadETX() throws CoronisException, IOException {
		VirtualCoronisFrame frameEtx= new VirtualCoronisFrame(cmd, msg);
		frameEtx.setBadETX();
		
		CoronisFrameReader reader = new CoronisFrameReader(new ByteArrayInputStream(frameEtx.getByteArray()));
		CoronisFrame frame = reader.readFrame();
	}

}
