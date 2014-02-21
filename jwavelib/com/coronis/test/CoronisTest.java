/*
 * CoronisTest.java
 *
 * Created on 31 octobre 2007, 17:56
 * 
 * CoronisTest class is used to test the coronis environment using a 
 * J2SE environment directly connected to a WavePort by a serial driver.
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 *
 * $Date: 2009-06-09 23:46:42 +0200 (Tue, 09 Jun 2009) $
 * $Revision: 82 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/test/CoronisTest.java $
 */

package com.coronis.test;
import com.coronis.modules.platform.RxTxSerialWavePort;
import com.coronis.modules.WaveFlow;
import com.coronis.modules.WavePort;
import com.coronis.modules.WaveTherm;
import com.coronis.CoronisLib;
import com.coronis.logging.*;

public class CoronisTest {
	
	public static void main(String[] args) {
		//threadingTest();
		baseTest();
	}

	
	public static void baseTest(){
		SimpleLogger logger = new BasicLogger();
		/* Main test method for CoronisTest */
		logger.log("Base test\n");

		WavePort wpt = new RxTxSerialWavePort("name", "/dev/ttyUSB3");
		try{			
			wpt.connect();
			
			// MODULE 1
			int[] module1 = {0x05, 0x19, 0x06, 0x30, 0x2D, 0x69};
			WaveTherm wth2 = new WaveTherm(module1, wpt, null);
			// MODULE 2
			int[] module2 = {0x05, 0x19, 0x06, 0x30, 0x28, 0xB5};
			WaveTherm wth1 = new WaveTherm(module2, wpt, null);
			
			//WaveFlow wfl3 = new WaveFlow(CoronisLib.moduleIdFromString("011606328C78") , wpt);
			WaveFlow wfl3 = new WaveFlow(CoronisLib.moduleIdFromString("051606304A44") , wpt, null);
			
			if (wpt.checkConnection() == true) {
				logger.log("Connection to waveport is ok");
			}
			
			//SimpleLogger.log(wth1.getType());
			//SimpleLogger.log(wth2.getType());	
			//SimpleLogger.log(wth1.getType());
			//SimpleLogger.log(wth2.getDateTime());
			//SimpleLogger.log(wth1.getDateTime());
			//SimpleLogger.log(wfl3.getDateTime());
			Thread.sleep(20);
			logger.log(wfl3.getFirmware());
			//SimpleLogger.log(wth2.getFirmware());
			//SimpleLogger.log(wth1.getCurrentValue());
			//SimpleLogger.log(wth1.getCurrentValue());
			//SimpleLogger.log(wth2.getCurrentValue());
			//SimpleLogger.log(wfl3.getCurrentValue());
			//SimpleLogger.log(wth1.getDatalog());
			//SimpleLogger.log(wth2.getDailyData().toString());
			//SimpleLogger.log(wfl3.getDateTime());
			//SimpleLogger.log(wth1.getDatalog().toString());
			//SimpleLogger.log(wth2.getDatalog().toString());
			//SimpleLogger.log(wth1.getDailyData().toString());
			//Thread.sleep(20);
			//SimpleLogger.log(wfl3.getDailyData().toString());
			Thread.sleep(20);
			logger.log(""+wth1.getDailyData());
			logger.log(wth1.getPartialDataSet().toString());
			//SimpleLogger.log(wth2.getDatalog().toString());
			//SimpleLogger.log(wfl3.getDatalog().toString());
					
			wpt.disconnect();
			wpt = null;
			
		} catch (Exception e) {
			logger.error("Error during test");
			wpt.disconnect();
			wpt = null;			
			e.printStackTrace();
		}
		
		
		logger.log("Exitting");
	}
	
}
	
	
