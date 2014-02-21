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
 * $Date: 2009-07-31 12:27:19 +0200 (Fri, 31 Jul 2009) $
 * $Revision: 154 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/test/divers/CoronisTest.java $
 */

package com.coronis.test.divers;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.coronis.modules.platform.RxTxSerialWavePort;
import com.coronis.modules.WaveFlow;
import com.coronis.modules.WavePort;
import com.coronis.modules.WaveTalk;
import com.coronis.modules.WaveTank;
import com.coronis.modules.WaveTherm;
import com.coronis.modules.WaveThermDalas;
import com.coronis.modules.WaveThermPT100;
import com.coronis.logging.FileLogger;
import com.coronis.logging.Logger;

/**
 * Sample code to queries modules.
 * <p>
 * 
 * @author antoine
 *
 */
public class CoronisTest {
	/* test config */
	private static String portName = "/dev/ttyUSB0";
	
	private static int[] wtlModID = {0x02, 0x16, 0x06, 0x30, 0x4F, 0x7A};
	private static int[] wthDalasModID = {0x05, 0x19, 0x06, 0x30, 0x28, 0xB5};
	//private static int[] wthPT100ModID = {0x05, 0x29, 0x07, 0x30, 0x01, 0x41};
	private static int[] wflModID = {0x05, 0x16, 0x06, 0x30, 0x4A, 0x44};
	private static int[] wtkModID = {0x05, 0x22, 0x08, 0x30, 0x06, 0x7C};
	
	public static void main(String[] args) {
		/* Initialize Logger:
		 * 
		 * output to: 	- stdout
		 * 				- ${HOME}/CoronisTest.log
		 */
		try {
			Logger.setLogger(new FileLogger(System.getProperty("user.home") 
											+ File.separatorChar 
											+"CoronisTest.log", true));
		} catch (IOException e) {
			Logger.error(e.getMessage());
		}

		/*
		 * Display debug and frame messages
		 */
		Logger.DEBUG = true;
		Logger.DEBUG_CORONIS_FRAMES = true;

		/* WavePort */ 
		WavePort wpt = new RxTxSerialWavePort("name", portName, 1);
		
		/* WaveTalk */
		WaveTalk wtl1 = new WaveTalk(wtlModID);			
		WaveTalk[] wtlArray = new WaveTalk[1];
		wtlArray[0] = wtl1;
		
		/* WaveTherm without and with repeater */
		WaveTherm wth1 = new WaveThermDalas(wthDalasModID, wpt, null);
		//WaveTherm wth1 = new WaveThermPT100(wthModID, wpt, null, WaveTherm.PT100_PT1000);
		WaveTherm wth2 = new WaveThermDalas(wthDalasModID, wpt, wtlArray);
		
		/* WaveFlow without and with repeater */
		WaveFlow wfl1 = new WaveFlow(wflModID , wpt, null);
		WaveFlow wfl2 = new WaveFlow(wflModID, wpt, wtlArray);
		
		/* WaveTank without and with repeater */
		WaveTank wtk1 = new WaveTank(wtkModID, wpt, null);
		WaveTank wtk2 = new WaveTank(wtkModID, wpt, wtlArray);
		
		Logger.log("Base test\n");
		try{			
			wpt.connect();
			
			if (wpt.checkConnection() == true) {
				Logger.log("Connection to waveport is ok");
			}

			/*
			Logger.mainLog("\n\n ==== ask datalogging Parameter ====\n\n");
			wpt.query_ptp_command(	Message.askDataLoggingParameters(wthModID),
									null,
									wth1.getModuleId());
			
			Logger.mainLog("\n\n ==== check parameter ====\n\n");
			wth1.checkParameters();
			
			Logger.mainLog("\n\n ==== ask stop datalogging ====\n\n");
			Logger.log(Boolean.toString(wth1.stopDataLogging()));
			
			Logger.mainLog("\n\n ==== check parameter ====\n\n");
			wth1.checkParameters();
			
			Logger.mainLog("\n\n ==== restart datalogging ====\n\n");
			Logger.log(Boolean.toString(wth1.startDataLoggingNow()));
			
			Logger.mainLog("\n\n ==== check parameter ====\n\n");
			wth1.checkParameters();
			*/
			
			Logger.mainLog("\n    === WaveTherm without repeaters test ===\n");
			Thread.sleep(20);
			Logger.log(Arrays.toString(wth1.getCurrentValues()) +"\n");
			Logger.log(wth1.getDataLog().toString(true));
			Logger.log(wth1.getAdvancedDataLog(20, 0).toString(true));
			Logger.log(wth1.getAdvancedDataLog(10, 10).toString(true));
			Logger.log(wth1.getAdvancedDataLog(100, 0).toString(true));
			
			Logger.mainLog("\n    === WaveTherm with repeaters test ===\n");
			Thread.sleep(20);
			Logger.log(wth2.getAdvancedDataLog(100, 0).toString(true));
			
			Logger.mainLog("\n    === WaveFlow without repeater test ===\n");
			Thread.sleep(20);
			Logger.log(Arrays.toString(wfl1.getCurrentValues())+"\n");
			Logger.log(wfl1.getDataLog().toString(true));
			Logger.log(wfl1.getAdvancedDataLog(20, 0).toString(true));
			Logger.log(wfl1.getAdvancedDataLog(10, 10).toString(true));
			Logger.log(wfl1.getAdvancedDataLog(100, 0).toString(true));
			
			Logger.mainLog("\n    === WaveFlow with repeaters test ===\n");
			Thread.sleep(20);
			Logger.log(wfl2.getAdvancedDataLog(100, 0).toString(true));
			
			Logger.mainLog("\n    === WaveTank without repeater test ===\n");
			Thread.sleep(20);
			Logger.log(Arrays.toString(wtk1.getCurrentValues())+"\n");
			Logger.log(wtk1.getDataLog().toString(true));
			Logger.log(wtk1.getAdvancedDataLog(20, 0).toString(true));
			Logger.log(wtk1.getAdvancedDataLog(10, 10).toString(true));
			Logger.log(wtk1.getAdvancedDataLog(100, 0).toString(true));
			
			Logger.mainLog("\n    === WaveTank with repeaters test ===\n");
			Thread.sleep(20);
			Logger.log(wtk2.getAdvancedDataLog(100, 0).toString(true));
			
			wpt.disconnect();
			wpt = null;
		

		} catch (Exception e) {
			Logger.error("Error during test: "+ e.getClass().getCanonicalName());
			Logger.error(e.getMessage());
			wpt.disconnect();
			wpt = null;	
			e.printStackTrace();
		}
	
		Logger.log("Exitting");
		System.exit(0);
	}
}
