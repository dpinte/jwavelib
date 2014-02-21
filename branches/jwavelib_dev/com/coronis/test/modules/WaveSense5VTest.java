/**
 * 
 */
package com.coronis.test.modules;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.coronis.exception.CoronisException;
import com.coronis.modules.WaveSense5V;
import com.coronis.test.CommonTest;

/**
 * @author antoine
 *
 */
public class WaveSense5VTest extends WaveSense5V {
	private static final int[] modID = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};

	public WaveSense5VTest() {
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
	 * Test method for {@link com.coronis.modules.WaveSense5V#parseValue(int, int)}.
	 */
	@Test
	public final void testParseValue() {
		int[] values = {0x00, 0x00, 0x04, 0x00, 0x08, 0x00,
						0x0C, 0x00, 0x0F, 0xFF, 0xFF, 0xFF};

		assertEquals(0, this.parseValue(values[0], values[1]), 0.1);
		assertEquals(1.25, this.parseValue(values[2], values[3]), 0.1);
		assertEquals(2.5, this.parseValue(values[4], values[5]), 0.1);
		assertEquals(3.75, this.parseValue(values[6], values[7]), 0.1);
		assertEquals(5, this.parseValue(values[8], values[9]), 0.1);
		assertEquals(Double.NaN, this.parseValue(values[10], values[11]), 0);
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveSense#readDatalog(int[])}.
	 */
	@Test
	public final void testReadDatalog() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveSense#readAdvancedDataLog(int[], boolean)}.
	 */
	@Test
	public final void testReadAdvancedDataLog() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveSense#readCurrentValues(int[])}.
	 */
	@Test
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
