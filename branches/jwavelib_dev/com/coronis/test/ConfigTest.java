package com.coronis.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.coronis.Config;
import com.coronis.exception.ConfigException;
import com.coronis.logging.Logger;

public class ConfigTest {
	private static Config config;
	private static InputStream is;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String content = ""
			+ "dailycall.hour=0\n"
			+ "dailycall.min=5\n"
			+ "ftp.host=192.168.1.1\n"
			+ "ftp.login=xxx\n"
			+ "ftp.pzd=xxx\n"
			+ "ftp.port=21\n"
			+ "#wfl575.type=waveflow\n"
			+ "wfl575.frequency=15\n"
			+ "wfl575.id=021607314575\n"
			+ "wfl559.type=waveflow\n"
			+ "#wfl559.frequency=15\n"
			+ "wfl559.id=021607314559\n"
			+ "wth36e.type=wavetherm\n"
			+ "wth36e.frequency=15\n"
			+ "#wth36e.id=03190730036e\n"
			+ "wth331.type=wavetherm\n"
			+ "wth331.frequency=15\n"
			+ "wth331.id=031907300331\n"
			+ "wth331.repeaters=wta1,wta2,wta3\n"
			+ "wth34e.type=wavetherm\n"
			+ "wth34e.frequency=15\n"
			+ "wth34e.id=03190730034e\n"
			+ "wth34e.repeaters=wta1,wta2,wta3\n"
			+ "wth34b.type=wavetherm\n"
			+ "wth34b.frequency=15\n"
			+ "wth34b.id=03190730034b\n"
			+ "wth34b.repeaters=wta1,wta2,wta3\n"
			+ "wth373.type=wavetherm\n"
			+ "wth373.frequency=15\n"
			+ "wth373.id=031907300373\n"
			+ "wth373.repeaters=wta1,wta2,wta3\n"
			+ "wth337.type=wavetherm\n"
			+ "wth337.frequency=15\n"
			+ "wth337.id=031907300337\n"
			+ "wth337.repeaters=wta1,wta2,wta3\n"
			+ "wth33c.type=wavetherm\n"
			+ "wth33c.frequency=15\n"
			+ "wth33c.id=03190730033c\n"
			+ "wth385.type=wavetherm\n"
			+ "wth385.frequency=15\n"
			+ "wth385.id=031907300385\n"
			+ "wth385.repeaters=wta1,wta2,wta3\n"
			+ "wth382.type=wavetherm\n"
			+ "wth382.frequency=15\n"
			+ "wth382.id=031907300382\n"
			+ "wth382.repeaters=wta1,wta2,wta3\n"
			+ "modules=wfl575,wfl559,wth36e,wth331,wth34e,wth34b,wth373,wth337,wth33c,wth385,wth382\n"
			+ "wta1.type=wavetalk\n" + "wta1.id=071507C00295\n"
			+ "wta2.type=wavetalk\n" + "wta2.id=071507C00298\n"
			+ "wta3.type=wavetalk\n" + "wta3.id=071507C002f4\n"
			+ "wta4.type=wavetalk\n" + "#wta4.id=010101010101\n"
			+ "#wta5.type=wavetalk\n" + "wta5.id=020202020202\n"
			+ "repeaters=wta1,wta2,wta3\n" + "debug.coronis=1\n"
			+ "debug.all=1\n"
			+ "debug.coronis=1\n";
		is = new ByteArrayInputStream(content.getBytes());
		config = new Config(true);
		config.load(is);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Logger.DEBUG = false;
		Logger.DEBUG_CORONIS_FRAMES = false;
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testCheckDebug() {
		assertTrue(Logger.DEBUG);
		assertTrue(Logger.DEBUG_CORONIS_FRAMES);
	}

	@Test
	public final void testGetModule() throws ConfigException {
		assertEquals("wth331", config.getModule("wth331", null).getName());
		assertEquals("031907300331", config.getModule("wth331", null).getModuleId());
		assertEquals(3, config.getModule("wth331", null).getRepeaterCount());
	}

	@Test(expected=ConfigException.class)
	public final void testGetModuleMissingModId() throws ConfigException {
		try {
			assertEquals("wth36e", config.getModule("wth36e", null).getName());
		} catch (NullPointerException e) {
			throw new ConfigException(e.getMessage());
		}
	}
	
	@Test(expected=ConfigException.class)
	public final void testGetModuleMissingType() throws ConfigException {
		try {
			assertEquals("wfl559", config.getModule("wfl559", null).getName());
		} catch (NullPointerException e) {
			throw new ConfigException(e.getMessage());
		}
	}
	
	@Test(expected=ConfigException.class)
	public final void testGetModuleMissingFreq() throws ConfigException {
		try {
			assertEquals("wth36e", config.getModule("wth36e", null).getName());
		} catch (NullPointerException e) {
			throw new ConfigException(e.getMessage());
		}
	}
	
	@Test
	public final void testGetRepeater() throws ConfigException {
		assertEquals("wta1", config.getRepeater("wta1").getName());
		assertEquals("071507C00295", config.getRepeater("wta1").getModuleId());
		
		//assertEquals(null, config.getRepeater("wta5"));
	}

	@Test(expected=ConfigException.class)
	public final void testGetRepeaterMissingModID() throws ConfigException {
		try {
			assertEquals("wta4", config.getRepeater("wta4").getName());
		} catch (NullPointerException e) {
			throw new ConfigException(e.getMessage());
		}
	}
	
	@Test(expected=ConfigException.class)
	public final void testGetRepeaterMissingType() throws ConfigException {
		try {
			assertEquals("wta5", config.getRepeater("wta5").getName());
		} catch (NullPointerException e) {
			throw new ConfigException(e.getMessage());
		}
	}
	
	@Test
	public final void testHasKey() {
		assertTrue(config.hasKey("ftp.host"));
		assertFalse(config.hasKey("fail"));
	}

	@Test(expected=ConfigException.class)
	public final void testGetStringValue() throws ConfigException {
		assertEquals("192.168.1.1", config.getStringValue("ftp.host"));
		config.getStringValue("fail");
	}

	@Test
	public final void testGetStringArrayValue() throws ConfigException {
		assertEquals(11, config.getStringArrayValue(Config.MODULES, Config.SEPARATOR_CHAR).length);	
		assertEquals(1, config.getStringArrayValue(Config.FTPHOST, Config.SEPARATOR_CHAR).length);
	}

	@Test
	public final void testSetValueStringString() throws ConfigException {
		config.setValue("test", "OK");
		
		assertTrue(config.hasKey("test"));
		assertEquals("OK", config.getStringValue("test"));
	}

	@Test
	public final void testSetValueStringStringArray() throws ConfigException {
		String[] str = {"1", "2", "3"};
		config.setValue("test", str);
		
		assertEquals("1,2,3", config.getStringValue("test"));
		assertEquals(3, config.getStringArrayValue("test", Config.SEPARATOR_CHAR).length);
	}
}
