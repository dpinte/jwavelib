/**
 * 
 */
package com.coronis.test.modules;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.coronis.exception.CoronisException;
import com.coronis.modules.WaveSense4_20;
import com.coronis.test.CommonTest;

/**
 * @author antoine
 *
 */
public class WaveSense4_20Test extends WaveSense4_20 {
	private static final int[] modID = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
	
	public WaveSense4_20Test() {
		super(modID, null, null);
	}
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
	 * Test method for {@link com.coronis.modules.WaveSense4_20#parseValue(int, int)}.
	 */
	@Test
	public final void testParseValue() {
		int[] values = {0xEE, 0xEE, 0x00, 0x00, 0x04, 0x00, 0x08, 0x00, 
						0x0C, 0x00, 0x0F, 0xFF, 0xFF, 0xFF};

		assertEquals(0, this.parseValue(values[0], values[1]), 0.1);
		assertEquals(4, this.parseValue(values[2], values[3]), 0.1);
		assertEquals(8, this.parseValue(values[4], values[5]), 0.1);
		assertEquals(12, this.parseValue(values[6], values[7]), 0.1);
		assertEquals(16, this.parseValue(values[8], values[9]), 0.1);
		assertEquals(20, this.parseValue(values[10], values[11]), 0.1);
		assertEquals(Double.NaN, this.parseValue(values[12], values[13]), 0);
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveSense#readDatalog(int[])}.
	 */
	@Test
	@Ignore
	public final void testReadDatalog() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveSense#readAdvancedDataLog(int[], boolean)}.
	 */
	@Test
	@Ignore
	public final void testReadAdvancedDataLog() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveSense#readCurrentValues(int[])}.
	 */
	@Test
	@Ignore
	public final void testReadCurrentValues() {
		fail("Not yet implemented"); // TODO
	}
	
	@Test
	public final void testReadType() {
		String typeMsg = "0519063028B5A0192D0119";	
		try {
			this.readType(CommonTest.msgFromHexString(typeMsg));
			fail("Module type is not the right one");
		} catch (CoronisException e) {			
			
		} 		
	}	

}
