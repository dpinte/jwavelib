package modules.sniffer.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import modules.sniffer.SerialReader;

import junit.framework.TestCase;

public class snifferTest extends TestCase {

	private SerialReader fr;
	/* sync frame */
	private byte[] syncBuf = {(byte)0xFF, (byte)0xFF, (byte)0xFF};
	
	/* frame OK */
	/*
	private byte[] dataBufOK = {(byte)0x4D, (byte)0xFF, (byte)0x01, (byte)0x4D, (byte)0x02, (byte)0x01,
								(byte)0x4D, (byte)0x0B, (byte)0x01, (byte)0x4D, (byte)0x20, (byte)0x01,
								(byte)0x4D, (byte)0x43, (byte)0x01, (byte)0x4D, (byte)0x06, (byte)0x01,
								(byte)0x4D, (byte)0x01, (byte)0x01, (byte)0x4D, (byte)0x00, (byte)0x01,
								(byte)0x4D, (byte)0x00, (byte)0x01, (byte)0x4D, (byte)0x02, (byte)0x01,
								(byte)0x4D, (byte)0x01, (byte)0x01, (byte)0x4D, (byte)0xD2, (byte)0x01,
								(byte)0x4D, (byte)0x41, (byte)0x01, (byte)0x4D, (byte)0x03, (byte)0x01 };
	*/
	
	/* frame with bad stx */
	/*
	private byte[] dataBufST = {(byte)0x4D, (byte)0xFF, (byte)0x01, (byte)0x4D, (byte)0x05, (byte)0x01,
								(byte)0x4D, (byte)0x0B, (byte)0x01, (byte)0x4D, (byte)0x20, (byte)0x01,
								(byte)0x4D, (byte)0x43, (byte)0x01, (byte)0x4D, (byte)0x06, (byte)0x01,
								(byte)0x4D, (byte)0x01, (byte)0x01, (byte)0x4D, (byte)0x00, (byte)0x01,
								(byte)0x4D, (byte)0x00, (byte)0x01, (byte)0x4D, (byte)0x02, (byte)0x01,
								(byte)0x4D, (byte)0x01, (byte)0x01, (byte)0x4D, (byte)0xD2, (byte)0x01,
								(byte)0x4D, (byte)0x41, (byte)0x01, (byte)0x4D, (byte)0x03, (byte)0x01 };
	*/
	
	/* frame with bad etx */
	/*
	private byte[] dataBufET = {(byte)0x4D, (byte)0xFF, (byte)0x01, (byte)0x4D, (byte)0x02, (byte)0x01,
								(byte)0x4D, (byte)0x0B, (byte)0x01, (byte)0x4D, (byte)0x20, (byte)0x01,
								(byte)0x4D, (byte)0x43, (byte)0x01, (byte)0x4D, (byte)0x06, (byte)0x01,
								(byte)0x4D, (byte)0x01, (byte)0x01, (byte)0x4D, (byte)0x00, (byte)0x01,
								(byte)0x4D, (byte)0x00, (byte)0x01, (byte)0x4D, (byte)0x02, (byte)0x01,
								(byte)0x4D, (byte)0x01, (byte)0x01, (byte)0x4D, (byte)0xD2, (byte)0x01,
								(byte)0x4D, (byte)0x41, (byte)0x01, (byte)0x4D, (byte)0xFF, (byte)0x01 };
	*/
	
	public snifferTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testWaitForSync() {
		this.fr = new SerialReader();
		
		this.fr.setStreamIn((InputStream) new ByteArrayInputStream(this.syncBuf));
		try{
			assertEquals(true, this.fr.waitForSync());
		} catch (IOException e){
			System.err.println(e.toString());
		}
	}
	
	public final void testReadFrame() {
		/*try{
			this.fr.setStreamIn((InputStream) new ByteArrayInputStream(this.dataBufOK));
			SnifferFrameInterface sf = this.fr.readFrame();
			assertNotNull(sf);
			assertEquals("FF020B2043060100000201D24103", ((SnifferFrame)sf).getMessage());
			assertTrue(sf.isCrcOk());
			
			this.fr.setStreamIn((InputStream) new ByteArrayInputStream(this.dataBufST));
			System.out.println("\nbad ST");
			SnifferFrame sf2 = this.fr.readFrame();
			assertNotNull(sf2);
			assertNotSame("FF020B2043060100000201D24103", sf2.getMessage());
			
			this.fr.setStreamIn((InputStream) new ByteArrayInputStream(this.dataBufET));
			System.out.println("\nbad ET");
			SnifferFrameInterface sf3 = this.fr.readFrame();
			assertNotNull(sf3);
			assertNotSame("FF020B2043060100000201D24103",  ((SnifferFrame)sf3).getMessage());
		} catch (IOException e){
			System.err.println(e.toString());
		}*/
	}
}
