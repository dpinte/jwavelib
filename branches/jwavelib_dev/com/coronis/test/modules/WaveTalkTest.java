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
import com.coronis.modules.WaveTalk;
import com.coronis.test.CommonTest;

/**
 * @author antoine
 *
 */
public class WaveTalkTest extends WaveTalk{

	private static final int[] modID = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};

	public WaveTalkTest() {
		super(modID);
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
	 * Test method for {@link com.coronis.modules.WaveTalk#getMinRSSI()}.
	 */
	@Test
	public final void testGetMinRSSI() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveTalk#getMaxRSSI()}.
	 */
	@Test
	public final void testGetMaxRSSI() {
		fail("Not yet implemented"); // TODO
	}
	
	@Test
	public final void testReadType() {
		// this one seems broken, type should be 0x15 and not 0x19
		String typeMsg = "0519063028B5A0192D0119";	
		try {
			this.readType(CommonTest.msgFromHexString(typeMsg));
			fail("Module type is not the right one");
		} catch (CoronisException e) {			
			
		} 		
	}		

}
