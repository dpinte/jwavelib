package frame.test;

import java.util.ArrayList;

import frame.*;
import junit.framework.TestCase;

public class FrameFactoryTest extends TestCase {

	public FrameFactoryTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testBuildFrameFromString() {
		SnifferFrameInterface frame = null;
		
		/* test a correct frame */
		frame =  FrameFactory.buildFrameFromString("FF020C400701021606304F7AC46F03",
													"{145/1/1/1/1/1/1/1/1/1/1/1/1/1/1}",
													"4d");
		assertNotNull(frame);
		assertTrue(frame.isStxOk());
		assertTrue(frame.isEtxOk());
		assertTrue(frame.isCrcOk());
		assertEquals("FF020C400701021606304F7AC46F03", ((SnifferReqWriteParameterFrame)frame).getMessage());
		
		/* test frame with a bad STX so no data, no footer */
/*		frame =  FrameFactory.buildFrameFromString("FF03",
													"{145/1}",
													"4d");
		assertNotNull(frame);
		assertFalse(frame.isStxOk());
		assertFalse(frame.isEtxOk());
		assertNotSame("0701021606304F7A", ((SnifferFrame)frame).getMessage());

		 test frame with a bad ETX 
		frame = FrameFactory.buildFrameFromString("FF020C400701021606304F7AC46F04",
													"{145/1/1/1/1/1/1/1/1/1/1/1/1/1/1}",
													"4d");
		assertNotNull(frame);
		assertTrue(frame.isStxOk());
		assertFalse(frame.isEtxOk());
		assertEquals("FF020C400701021606304F7AC46F03", ((SnifferFrame)frame).getMessage());*/
	}

	public final void testBuildFrameFromArray() {
		SnifferFrameInterface frame = null;
		int[] header, footer;
		int[] bodyA = {0x40, 0x07, 0x01, 0x02, 0x16, 0x06, 0x30, 0x4F, 0x7A};
		int[] tsA = {145, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		ArrayList <Integer> body = new ArrayList <Integer> ();
		ArrayList <Integer> ts = new ArrayList <Integer> ();
		int dir = 0x4d;
		int i;
		
		/* test a correct frame */
		header = new int[]{0xFF, 0x02, 0x0c};
		footer = new int[]{0xC4, 0x6F, 0x03};
		
		for(i = 0; i < bodyA.length; i++){
			body.add(bodyA[i]);
			ts.add(tsA[i]);
		}
		
		frame = FrameFactory.buildFrameFromArray(header, bodyA, footer, tsA, dir);
		assertNotNull(frame);
		assertTrue(frame.isStxOk());
		assertTrue(frame.isEtxOk());
		assertTrue(frame.isCrcOk());
		assertEquals("FF020C400701021606304F7AC46F03", ((SnifferReqWriteParameterFrame)frame).getMessage());
		
		/* test a frame with bad STX */
		header = new int[]{0xFF, 0x04};
/*		footer = null;
		
		frame = FrameFactory.buildFrameFromArray(header,
													new ArrayList <Integer> (),
													footer,
													new ArrayList <Integer> (),
													dir);
		assertNotNull(frame);
		assertFalse(frame.hasSTX());
		assertFalse(frame.hasETX());
		assertNotSame("0701021606304F7A", frame.getData());
		
		 test a frame with a bad ETX 
		header = new int[]{0xFF, 0x02, 0x0C};
		footer = new int[]{0xC4, 0x6F, 0x04};
		
		body.clear();
		for(i = 0; i < bodyA.length; i++){
			body.add(bodyA[i]);
		}
		
		frame = FrameFactory.buildFrameFromArray(header,
													body,
													footer,
													ts,
													dir);
		assertNotNull(frame);
		assertTrue(frame.hasSTX());
		assertFalse(frame.hasETX());
		assertEquals("FF020C400701021606304F7AC46F03", frame.getMessage());
	*/
	}

}
