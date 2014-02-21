package com.coronis.test.modules;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.coronis.exception.CoronisException;
import com.coronis.modules.Module;
import com.coronis.test.CommonTest;

public class ModuleTest extends Module {
	private static final int[] id = {0x05, 0x19, 0x06, 0x30, 0x28, 0xB5};
	
	private static final String firmMsg = "0519063028B5A85600A30202";
	private static final String dateMsg = "0519063028B592180609031429";
	private static final String typeMsg = "0519063028B5A0192D0119";
	
	public ModuleTest() {
		super(id, null, null);
	}
	
	public int getModuleType(){
		return 0x19;
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testIsRepeated() {
		assertFalse(this.isRepeated());
	}

	@Test
	public final void testGetRepeaterCount() {
		assertEquals(0, this.getRepeaterCount());
	}

	@Test
	public final void testGetRepeaters() {
		assertNull(this.getRepeaters());
	}

	@Test
	public final void testGetName() {
		assertEquals("0519063028B5", this.getName());
	}

	@Test
	public final void testGetModuleId() {
		assertEquals("0519063028B5", this.getModuleId());
	}

	@Test
	public final void testGetRadioId() {
		assertArrayEquals(id, this.getRadioId());
	}

	@Test
	public final void testReadDatetime() {
		assertEquals("Wed Jun 24 20:41:00 CEST 2009",
					this.readDatetime(CommonTest.msgFromHexString(dateMsg)));
	}

	@Test
	public final void testReadFirmware() {
		assertEquals("V-A3-0202",
					this.readFirmware(CommonTest.msgFromHexString(firmMsg)));
	}

	@Test
	public final void testReadType() {
		try {
			this.readType(CommonTest.msgFromHexString(typeMsg));			
			assertEquals(0x2D, this._rssi);
			assertEquals(0x19, this._moduleType);
			assertEquals(0x01, this._wakeUpFrequency);
		} catch (CoronisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public final void testIsDataloggingModule() {
		assertFalse(this.isDataloggingModule());
	}

}
