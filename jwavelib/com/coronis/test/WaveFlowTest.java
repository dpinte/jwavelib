package com.coronis.test;

import junit.framework.TestCase;
import java.util.Date;

import com.coronis.Config;
import com.coronis.CoronisLib;
import com.coronis.exception.ConfigException;
import com.coronis.logging.BasicLogger;
import com.coronis.modules.WaveFlow;
import com.dipole.libs.*;

public class WaveFlowTest extends TestCase {

	protected void setUp() throws Exception {
		Config.setLogger(new BasicLogger());
		Constants.DEBUG = true;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testModuleType() {
		/*
		 * Test the CoronisLib.getModuleTypeString
		 */
		assertEquals("Waveflow", CoronisLib.getModuleTypeString(0x16));		
	}	

	public void testDifferentialConsumptionComputation() {
		try {
			int[] wlfid = CoronisLib.moduleIdFromString("051606304A44");
			WaveFlow wfl = new WaveFlow(wlfid, null, null);
			DataSet dst = new DataSet(wlfid);
			dst.addMeasure(10.0, new Date(45));
			dst.addMeasure(30.0, new Date(45));
			dst.addMeasure(20.0, new Date(35));
			dst.addMeasure(10.0, new Date(10));
			wfl.addDifferentialConsumption(dst, 5);
			
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testReadValue(){
		try {
			int[] wlfid = CoronisLib.moduleIdFromString("051606304A44");
			WaveFlow wfl = new WaveFlow(wlfid, null, null);
			assertEquals(0, wfl.parseValue(0x00, 0x00, 0x00, 0x00));
			// Test the maximum value for 32 bits
			Long max = new Long("4294967295");
			assertEquals(max.longValue(), wfl.parseValue(0xFF, 0xFF, 0xFF, 0xFF));
			// Test the maximum integer value for the JVM
			assertEquals(Integer.MAX_VALUE,  wfl.parseValue(0x80,0x00,0x00, 0x00)-1);
			// Test the maximum integer value for the JVM
			int test = 841710;
			for (int j = 0; j < test; j+=1) {
				int v1 = test+j >> 3*8;
				int v2 = (test+j >> 2*8) & 0x000000FF;
				int v3 = (test+j >> 8) & 0x000000FF;
				int v4 = (test+j) & 0x000000FF;
				//System.out.println("Total :" + test + " values : (" + v1 + "-" + v2 + "-"+v3 + "-"+v4 + ")");
				assertEquals(test+j,  wfl.parseValue(v1,v2,v3,v4));
			}
			
			assertEquals(638018, wfl.parseValue(0x00, 0x09, 0xBC, 0x42));
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
