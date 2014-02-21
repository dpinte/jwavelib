package com.coronis.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.coronis.Config;
import com.coronis.logging.BasicLogger;
import com.coronis.modules.Module;
import com.coronis.modules.WaveTalk;
import com.dipole.libs.Constants;

import junit.framework.TestCase;

public class ConfigTest extends TestCase {

	protected void setUp() throws Exception {
		Config.setLogger(new BasicLogger());
		Constants.DEBUG = true;
	}	
	
	public void testConfig() {
		String content = ""
				+ "dailycall.hour=0\n"
				+ "dailycall.min=5\n"
				+ "ftp.host=192.168.1.1\n"
				+ "ftp.login=xxx\n"
				+ "ftp.pzd=xxxx\n"
				+ "ftp.port=21\n"
				+ "wfl575.frequency=15\n"
				+ "wfl575.id=021607314575\n"
				+ "wfl575.type=waveflow\n"				
				+ "wfl559.type=waveflow\n"
				+ "wfl559.frequency=15\n"
				+ "wfl559.id=021607314559\n"
				+ "wth36e.type=wavetherm\n"
				+ "wth36e.frequency=15\n"
				+ "wth36e.id=03190730036e\n"
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
				+ "wta1.type=wavetalk\n" + "wta1.id=071507c00295\n"
				+ "wta2.type=wavetalk\n" + "wta2.id=071507c00298\n"
				+ "wta3.type=wavetalk\n" + "wta3.id=071507c002f4\n"
				+ "repeaters=wta1,wta2,wta3\n" + "debug.coronis=1\n"
				+ "debug.all=1";
		try {
			InputStream is = new ByteArrayInputStream(content.getBytes());
			Config cfg = new Config();
			cfg.load(is);
			System.out.println(cfg);
			WaveTalk[] wtks = cfg.getRepeaterArray();
			assertEquals(cfg.getModuleArray(null, wtks).length, 11);
			Module[] mods = cfg.getModuleArray(null, wtks);
			assertEquals(mods[0].getName(), "wfl575");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
