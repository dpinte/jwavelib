/*
 * WavePort.java
 *
 * Created on 31 octobre 2007, 17:56
 *
 * This is the WavePort class implementation. This class is abstract and must
 * implement the connect() and disconnect() methods that will link the WavePort
 * with a given serial driver (see Win32SerialWavePort.java,
 * StandardSerialWavePort.java and MeSerialWavePort.java).
 *
 * At the moment, the only communication type that is supported is the point to
 * point procotol. This version does not support multi-frames or broadcasted
 * protocol.
 *
 * Publish/subscriber design pattern : http://en.wikipedia.org/wiki/Publish/subscribe
 *
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 *
 * $Date: 2009-09-17 20:35:39 +0200 (Thu, 17 Sep 2009) $
 * $Revision: 160 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/modules/WavePort.java $
 */
package com.coronis.modules;

import java.io.*;
import java.util.*;

import com.coronis.exception.*;
import com.coronis.frames.*;
import com.coronis.modules.requests.*;
import com.dipole.libs.Functions;
import com.coronis.Config;
import com.coronis.logging.Logger;

public abstract class WavePort implements Runnable {
    
    
    /* Waveport internal parameters */
	protected static final short AWAKENING_PERIOD			= 0x00;
	protected static final short WAKEUP_TYPE 				= 0x01;
	protected static final short WAKEUP_LENGTH				= 0x02;
	protected static final short WAVECARD_POLLING_GROUP 	= 0x03;
	protected static final short RADIO_ACK					= 0x04;
	protected static final short RELAY_ROUTE_STATUS			= 0x06;
	protected static final short RELAY_ROUTE				= 0x07;
	protected static final short POLLING_ROUTE				= 0x08;
	protected static final short GROUP_NUMBER				= 0x09;
	protected static final short POLLING_TIME				= 0x0A;
	protected static final short RADIO_USER_TIMEOUT			= 0x0C;
    protected static final short EXCHANGE_STATUS       		= 0x0E;
    protected static final short SWITCH_MODE_STATUS			= 0x10;
    protected static final short WAVECARD_MULTICAST_GROUP	= 0x16;
    protected static final short BCST_RECEPTION_TIMEOUT		= 0x17;
    
    public static final short ACK_RECEIVED_TIMEOUT      = 500;  // milliseconds before an ACK timeout
    public static final short ACK_RETRY_COUNT           = 3;    // there is maximum 3 retry to get an ACK
    
    protected final int queryRetries;
    protected final String wpt_name;
    protected final String serialPort;
    protected OutputStream outputStream;
    protected InputStream inputStream;
    protected CoronisFrameWriter fwriter;
    protected CoronisFrameReader freader;
    protected boolean isConnected = false;
    
    /*
     * ACK_RETURN_TIME is the minimum time needed before sending an ACK to the WavePort.
     * Specifications says it's 1 milliseconds.
     */
    protected static final int ACK_RETURN_TIME = 1;
    
    /*
     * Point to point query needs 2100 milliseconds to be executed.
     * Set to 3000 milliseconds to be sure.
     * Mean time for ptp with ACK and two retry after a bad reception of ACK)
     * see Excel Coronis documentation provided by Mr Rabaud on the 13/06
     * + 20 milliseconds (two ACK_RETUNR_TIME)
     */
    public static final int PTP_QUERY_TIME = 4324 + ( 2 * ACK_RETURN_TIME) + 500;
    
    /*
     * Repeated ptp query with ACK 
     * by number of relais and for two retries
     */
    public static final int RPT_1_QUERY_TIME = 5899 + ( 2 * ACK_RETURN_TIME) + 500;
    public static final int RPT_2_QUERY_TIME = 7410 + ( 2 * ACK_RETURN_TIME) + 500;
    public static final int RPT_3_QUERY_TIME = 8921 + ( 2 * ACK_RETURN_TIME) + 500;
    
    public String version = "not loaded";
    
    protected boolean _isListening;

    protected Thread    inputListener;
    protected Hashtable eventSubscribers;
    
    /**
     * Creates a new WavePort
     * 
     * @param name The internal name of the WavePort
     * @param sport The serial port connected to the WavePort
     * @param retries The number of query retries
     */
    public WavePort(String name, String sport, int retries) {
        this.wpt_name = name;
        this.serialPort = sport;
        this.eventSubscribers = new Hashtable();
        this.queryRetries = retries;
    }
    
    /**
     * Creates a new WavePort with a default of 5 retries if queries fail
     * 
     * @param name The internal name of the WavePort
     * @param sport The serial port connected to the WavePort
     */
    public WavePort(String name, String sport) {
    	this(name, sport, Config.DEFAULT_RETRIES);
    }
    
    // ABSTRACT METHODS
    /**
     * Connect to the WavePort
     * @return true if connection OK
     */
    public abstract boolean connect();
    
    /**
     * Disconnect from the WavePort
     * @return true if disconnected
     */
    public abstract boolean disconnect();   
    
    /**
     * Implements the run() method of the Runnable interface. 
     * The run method must start the listening process
     */
    public abstract void run();    
    
    public abstract void stopListener();    
    
    /**
     * Get the name of the WavePort
     * @return The WavePort name
     */
    public String getName() {
        return wpt_name;
    }
    
    public void startListener() {
        if (inputListener == null || inputListener.isAlive() == false ) {
            inputListener = new Thread(this);
            inputListener.setPriority(Thread.MAX_PRIORITY);
            inputListener.start();            
        }        
    }    
    
    public void subscribe(int eventType, WavePortEventProcessor pcs) {
        Integer evt = new Integer(eventType);
        if (eventSubscribers.containsKey(evt) == false) {
            Vector vt = new Vector();
             vt.addElement(pcs);
            synchronized (eventSubscribers)  {             
                eventSubscribers.put(evt, vt);
            }            
        }  else {
            synchronized (eventSubscribers){
                    ((Vector)eventSubscribers.get(evt)).addElement(pcs);
            }                                  
        }    
    }
    
    public void unsubscribe(int eventType, WavePortEventProcessor pcs ) {
        Integer evt = new Integer(eventType);
        if (eventSubscribers.containsKey(evt) == true) {  
            synchronized (eventSubscribers){
                ((Vector)eventSubscribers.get(evt)).removeElement(pcs);
            }
        }
    }    
   
    /**
     * Reset the WavePort to it's default configuration
     * @return true if no error during setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setDefaultParameters() throws IOException, CoronisException {
        
        // FIXME : this is a workaround to sleep a bit after the firmware asked to the WavePort.
        // it should be managed at the class level --> prevent successive calls with less than 500 ms
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
        if (setAwakeningPeriod(0x0A) == false) {
            Logger.error("Error while setting the awakening period on waveport.");
            return false;
        }
        
        if (setWakeUpType(false) == false) {
            Logger.error("Error while setting wake up type on waveport.");
            return false;
        }
        
        int[] wkplgt = {0x4C, 0X04};
        if (setWakeUpLength(wkplgt) == false) {
            Logger.error("Error while setting wake up length on waveport.");
            return false;
        }
        
        if (setRadioACK(true) == false) {
            Logger.error("Error while setting ardio ACK on waveport.");
            return false;
        }
        
        if (setRadioUserTimeOut(0x14) == false) {
            Logger.error("Error while setting radio user timeout on waveport.");
            return false;
        }
        
        if (setExchangeStatus(false, false) == false) {
            Logger.error("Error while activating error frames on waveport.");
            return false;
        }
        
        if (setSwitchModeStatus(true) == false) {
            Logger.error("Error while activating switch mode status on waveport.");
            return false;
        }
        return true;
    }
    
    /**
     * * Set the SWITCH_MODE_STATUS parameter
     * <p>
     * Default:  automatic selection is activated
     * 
     * @param activate 	<ul>
     * 					<li> true to activate automatic selection
     * 					<li> false to desactivate automatic selection
     * 					</ul>
     * @return true if no error during setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setSwitchModeStatus(boolean activate) throws IOException, CoronisException {
    	int[] pData = {(activate) ?  0x01 : 0x00};
        return setInternalParameter(SWITCH_MODE_STATUS, pData);
    }               
    
    /**
     * Set the RADIO_USER_TIMEOUT parameter
     * <p>
     * Default: 2 seconds (0x14)
     * 
     * @param value Value in multiples of 100ms
     * @return true if no error during setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setRadioUserTimeOut(int value) throws IOException, CoronisException  {
        int[] pData = {value};
        return setInternalParameter(RADIO_USER_TIMEOUT, pData);
    }     
   
    /**
     * Set the RADIO_ACKNOWLEDGE parameter
     * <p>
     * Default: with acknowledgement
     * 
     * @param withAck 	<ul>
     * 					<li> true: with acknowledgement
     * 					<li> false: no acknowledgement
     * 					</ul>
     * @return true if no error during setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setRadioACK(boolean withAck) throws IOException, CoronisException  {
        int[] pData = {(withAck) ?  0x01 : 0x00};
        return setInternalParameter(RADIO_ACK, pData);
    }  

    /**
     * Set the WAKEUP_LENGTH parameter
     * <p>
     * Default: 1100 ms
     * 
     * @param value value in multiples of 1ms, LSB first<br>
     * 				<ul>
     * 				<li> min. value = 20 ms (0x1400)
     * 				<li> max. value = 10 sec (0x1027)
     * @return true if no error during setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setWakeUpLength(int[] value) throws IOException, CoronisException  {
    	//TODO: check values
        return setInternalParameter(WAKEUP_LENGTH, value);
    }   
    
    /**
     * Set WAKEUP_TYPE parameter
     * <p>
     * Default: long wake-up
     * 
     * @param shortWakeUp	<ul>
     * 						<li> true: use short wake-up (50ms)
     * 						<li> false: use long wake-up
     * 						</ul>
     * @return true if no error durring setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setWakeUpType(boolean shortWakeUp) throws IOException, CoronisException  {
        int[] pData = {(shortWakeUp) ?  0x01 : 0x00};
        return setInternalParameter(WAKEUP_TYPE, pData);
    }    
    
    /**
     * Set AWAKENING_PERIOD parameter
     * <p>
     * Default: 1 second (0x0A))
     * 
     * @param value Period in multiples of 100 ms<br>
     * 				0 for nearly constant reception (20 ms)
     * @return true if no error during setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setAwakeningPeriod(int value) throws IOException, CoronisException  {
        int[] pData = {value};
        return setInternalParameter(AWAKENING_PERIOD, pData);
    }
     
    /**
     * Set EXCHANGE_STATUS parameter
     * <p>
     * Default: status and error frames desactivated (0x00)
     * 
     * @param errorFrame true to active error frame
     * @param statusFrame true to active status frame
     * 
     * @return true if no error during setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setExchangeStatus(boolean errorFrame, boolean statusFrame) throws IOException, CoronisException  {
        int[] pData = new int[1];
        
        if(errorFrame) {
        	pData[0] = (statusFrame) ? 0x03 : 0x01;
        } else {
        	pData[0] = (statusFrame) ? 0x02 : 0x00;
        }
        
        return setInternalParameter(EXCHANGE_STATUS, pData);
    }  
    
    /**
     * Set the RELAY_ROUTE parameter
     * <p>
     * 3 repeaters maximum
     * 
     * @param wtk An array with the all the repeaters
     * @return true if no error during setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setRelayRoute(WaveTalk[] wtk) throws IOException, CoronisException  {
        /*
         *  BYTE 1: number of repeaters in route
         *  Maximum number of repeaters = 3
         * If BYTE 1 != 0
         * BYTES 2 to 7: First repeater's radio
         * address..., etc.
         */
        int[] pData = null;
        
        if (wtk != null && wtk.length > 0) {
            pData = new int[1 + 6 * wtk.length];
            pData[0] = wtk.length;  // set the number of repeaters
            // add the repeaters radio id to the messag
            
            for (int repc = 0; repc < wtk.length; repc++) {
                for (int idj=0; idj < wtk[repc].getRadioId().length; idj++) {
                    //int val = (1+(repc * 6)+idj);
                    //logger.debug("Indexes :" + val + " - " + wtk[repc].modid[idj]);
                    pData[1+ (repc * 6)+idj] = wtk[repc].getRadioId()[idj];
                }
            }
            return setInternalParameter(RELAY_ROUTE, pData);
        } else {
        	/*
             * 19 june 2008 : Mr Rabaud from Coronis Systems told us not to do anything when there is no repeaters ! 
             * it seems that the WavePort does use this parameter only one time ! Thus, it is not mandatory to set it.
             */
            //pData = new int[1];
            //pData[0] = 0x00;
            //return setInternalParameter(0x07, pData);
            return true;
        }
        
    }
    
    public String getFirmware() throws IOException, InterruptedException, CoronisException {
    	/*
         * Query the waveport and read it's firmware version
         */
        if (isConnected == false) {
            connect();
        }
        
        // example of message
        // int[] msg = {0x05, 0x19, 0x06, 0x30, 0x2D, 0x69, 0x01 };
        Logger.debug("Check connection request sent");
                
        int[] msg = {};
        
        int timeout = getTimeOut(null);
        
        WavePortFirmwareRequest ptp = new WavePortFirmwareRequest(this, msg, timeout * 100);
        
        
        int resent = 0;
        int tranmissionError = 0;
        int timeoutError = 0;
        // request is sent maximum three times if TransmissionException or TimeOutException are catched.
        do {
            try {
                try {
                    Thread.sleep(ACK_RETRY_COUNT * WavePort.ACK_RECEIVED_TIMEOUT);
                } catch (InterruptedException e) {
                    
                }
                if (ptp.process()) {
                    this.version = ((WavePortFirmwareFrame)ptp.getAnswer()).getFirmware();                 
                    return this.version;
                } else if (ptp.isTimeOut()) {throw new TimeOutException("Waveport firmware request has timed out ...");}
                       else {throw new CoronisException("Strange process did not finish correctly but it's not a timeout ..."); }
            } catch (TransmissionException e) {
                tranmissionError++;
                ptp.unsubscribe(); // remove old event subscription
            } catch (TimeOutException e) {
                timeoutError++;
                ptp.unsubscribe(); // remove old event subscription
            } catch (NoAckException e) {
                tranmissionError++;
                ptp.unsubscribe();
                Logger.warning("No ack exception. WavePort is in a bad mood. Waiting 2 seconds");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (CommandException e) {
                tranmissionError++;
                ptp.unsubscribe();
                Logger.warning("Command exception while talking to the WavePort. WavePort is in a bad mood. Waiting 2 seconds");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        } while (++resent < this.queryRetries);
        return null;    	
    }
    
    /**
     * Check the connection with the WavePort
     * 
     * @return  <ul>
     * 			<li> true if connection is OK
     * 			<li> false if check failed
     * 			</ul>
     * @throws IOException
     * @throws InterruptedIOException
     * @throws CoronisException
     */
    public boolean checkConnection() throws
            IOException,
            InterruptedIOException,
            CoronisException {
    	try {
			String version = this.getFirmware();
			if (version == null) return false;
			else return true;
		} catch (InterruptedException e) {
			return false;
		}    	
    }
    
    /**
     * Request to set WavePort internal parameter
     * 
     * @param parameter Parameter number
     * @param pData Parameter value
     * @return true if no error during setup
     * @throws IOException
     * @throws InterruptedIOException
     * @throws CoronisException
     */  
    protected boolean setInternalParameter(int parameter, int[] pData) throws
            IOException,
            InterruptedIOException,
            CoronisException {
        /*
         * Query the waveport and read it's firmware version in order to check
         * that the RS-232 connection works fine
         */
        if (isConnected == false) {
            connect();
        }
         
        Logger.debug("Set property request sent");
        
        int timeout = getTimeOut(null);
        
        // message is parameter id + parameter data
        int[] msg = new int[1+ pData.length];
        msg[0] = parameter;
        for (int i =1; i < pData.length+1; i++ ) {
            msg[i] = pData[i-1];
        }
        
        WavePortSetParameterRequest ptp = new WavePortSetParameterRequest(this, msg, (int)(timeout * 1.1* 100));   
        int resent = 0;
        int tranmissionError = 0;
        int timeoutError = 0;
        // request is sent maximum three times if TransmissionException or TimeOutException are catched.
        do {
            try {
                try {
                    Thread.sleep(ACK_RETRY_COUNT * WavePort.ACK_RECEIVED_TIMEOUT);
                } catch (InterruptedException e) {
                    
                }
                if (ptp.process()) {
                    Logger.debug("Set write parameter returned");
                    if (((ResWriteParameterFrame)ptp.getAnswer()).getStatus() == true) {
                        Logger.debug("Property setted ok");   
                        return true;
                    } else {
                         Logger.error("Write parameter error while trying to set parameter " + Functions.printHumanHex(parameter, true) );
                        return false;                
                    }
                } else if (ptp.isTimeOut()) throw new TimeOutException("Waveport set paremeter request has timed out ...");
                else {
                    Logger.debug("Strange process did not finish correctly but it's not a timeout ...");
                    throw new CoronisException("Strange process did not finish correctly but it's not a timeout ...");
                }
            } catch (TransmissionException e) {
                tranmissionError++;
                ptp.unsubscribe(); // remove old event subscription
            } catch (TimeOutException e) {
                timeoutError++;
                ptp.unsubscribe(); // remove old event subscription
            } catch (NoAckException e) {
                tranmissionError++;
                ptp.unsubscribe();
                Logger.warning("No ack exception. WavePort is in a bad mood. Waiting 10 seconds");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (CommandException e) {
                tranmissionError++;
                ptp.unsubscribe();
                Logger.warning("Command exception while talking to the WavePort. WavePort is in a bad mood. Waiting 10 seconds");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        } while (++resent < this.queryRetries);
        throw new TransmissionException(this.queryRetries 
        								+" repetition of the setInternalParameter request did not succed ( "
        								+ tranmissionError + " transmission errors and "
        								+ timeoutError + " timeout errors)");
    }
    
    /**
     * Send a frame
     * 
     * @param cmd the command of the frame
     * @param msg the message to send
     * @throws IOException
     */
    public void send(int cmd, int[] msg) throws IOException {
        fwriter.sendFrame(cmd , msg);
    }
   
    /**
     * Send a service request in Point-to-Point mode
     * 
     * @param msg the message to send
     * @param repeaters the repeaters list
     * @param moduleid the radio address of the module to query
     * @return The data part of the answer
     * @throws IOException
     * @throws InterruptedIOException
     * @throws CoronisException
     */
    public int[] query_ptp_service(int[] msg, WaveTalk[] repeaters, String moduleid) throws IOException,
            InterruptedIOException,
            CoronisException {
                PointToPointServiceRequest ptp = new PointToPointServiceRequest(this, msg, 60 * 1000, moduleid);
                return query_ptp(msg, repeaters, moduleid, ptp);        
    }
    
    /**
     * Send a request in Point-to-Point mode
     * @param msg the message to send
     * @param repeaters the repeaters list
     * @param moduleid the radio address of the module to query
     * @return The data part of the answer
     * @throws IOException
     * @throws InterruptedIOException
     * @throws CoronisException
     */
    public int[] query_ptp_command(int[] msg, WaveTalk[] repeaters, String moduleid) throws IOException,
            InterruptedIOException,
            CoronisException {  
                // Ask suggested by the Coronis support, for PTP requests we always just wait for a 0x30 or 0X31. 
                // Thus, the timeout is setted to 60 seconds
                PointToPointRequest ptp = new PointToPointRequest(this, msg, 60 * 1000, moduleid);
                return query_ptp(msg, repeaters, moduleid, ptp);
    }
    
    /**
     * Send a Multiframe request in Point-to-Point mode
     * 
     * @param msg the message to send
     * @return The data part of the answer
     * @throws IOException
     * @throws InterruptedIOException
     * @throws CoronisException
     */ 
    public int[][] query_ptp_multiframe(int[] msg, String moduleid) throws IOException,
            InterruptedIOException,
            CoronisException {  
        // Ask suggested by the Coronis support, for PTP requests we always just wait for a 0x36 or 0X31. 
        // Thus, the timeout is setted to 5* 60 seconds
        PointToPointMultiFrameRequest ptp = new PointToPointMultiFrameRequest(this, msg, 5 * 60 * 1000, moduleid);

        if (isConnected == false) {
            connect();
        }        
        
        int resent = 0;
        int tranmissionError = 0;
        int timeoutError = 0;
        // request is sent maximum three times if TransmissionException or TimeOutException are catched.
        do {
            try {
                Thread.sleep(ACK_RETRY_COUNT * WavePort.ACK_RECEIVED_TIMEOUT);
            } catch (InterruptedException e) {
                
            }           

                try {
                    CoronisFrame[] frames;
                    if (ptp.process()) {
                        frames = ptp.getAnswers();
                        int[][] output = new int[frames.length][];
                        for (int i=0; i < frames.length; i++) {
                            output[i] = frames[i].getData();                            
                        }
                        return output;
                    }
                    
                    else if (ptp.isTimeOut()) throw new TimeOutException("PTP request has timed out ...");
                    else{
                        Logger.debug("Strange process did not finish correctly but it's not a timeout ...");
                        throw new CoronisException("Strange process did not finish correctly but it's not a timeout ...");
                    }
                } catch (TransmissionException e) {
                    Logger.warning("Transmission problem. Waiting 10 seconds then retrying");
                    tranmissionError++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (TimeOutException e) {
                    Logger.warning("Timeout exception. Waiting 10 seconds then retrying");
                    timeoutError++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (NoAckException e) {
                    tranmissionError++;
                    Logger.warning("No ack exception. WavePort is in a bad mood. Waiting 10 seconds");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (CommandException e) {
                    tranmissionError++;
                    
                    Logger.warning("Command exception while talking to the WavePort. WavePort is in a bad mood. Waiting 10 seconds");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } finally {
                    ptp.unsubscribe(); // remove old event subscription   
                }
            
        } while (++resent < this.queryRetries);
        throw new TransmissionException(this.queryRetries 
        								+" repetition of the ptp request did not succed ( "
        								+ tranmissionError + " transmission errors and "
        								+ timeoutError + " timeout errors)");                
    }
    
    /**
     * Request in Point-to-Point mode
     * 
     * @param msg the message to send
     * @param repeaters the repeaters list
     * @param moduleid the radio address of the module to query
     * @param req the request to send
     * @return The data part of the answer
     * @throws IOException
     * @throws InterruptedIOException
     * @throws CoronisException
     */
    public int[] query_ptp(int[] msg, WaveTalk[] repeaters, String moduleid, Request req) throws IOException,
            InterruptedIOException,
            CoronisException {      
        
        if (isConnected == false) {
            connect();
        }
        
        
        // Radio user timeout does not have to be setted. See e-mail from Coronis support Christophe MAUGENEST on the 7-oct-2008
        // int timeout = getTimeOut(repeaters);
        //if (setRadioUserTimeOut(timeout) == false)  throw new CoronisException("Error while setting radio user timeout");
        
        int resent = 0;
        int tranmissionError = 0;
        int timeoutError = 0;
        // request is sent maximum three times if TransmissionException or TimeOutException are catched.
        do {
            try {
                Thread.sleep(ACK_RETRY_COUNT * WavePort.ACK_RECEIVED_TIMEOUT);
            } catch (InterruptedException e) {
                
            }
            if (setRelayRoute(repeaters) == false) throw new CoronisException("Error while setting relay route ...");
            else {

                try {
                    if (req.process()) return req.getAnswer().getData();
                    else if (req.isTimeOut()) throw new TimeOutException("PTP request has timed out ...");
                    else{
                        Logger.debug("Strange process did not finish correctly but it's not a timeout ...");
                        throw new CoronisException("Strange process did not finish correctly but it's not a timeout ...");
                    }
                } catch (TransmissionException e) {
                    Logger.warning("Transmission problem. Waiting 10 seconds then retrying");
                    tranmissionError++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (TimeOutException e) {
                    Logger.warning("Timeout exception. Waiting 10 seconds then retrying");
                    timeoutError++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (NoAckException e) {
                    tranmissionError++;
                    Logger.warning("No ack exception. WavePort is in a bad mood. Waiting 10 seconds");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (CommandException e) {
                    tranmissionError++;
                    
                    Logger.warning("Command exception while talking to the WavePort. WavePort is in a bad mood. Waiting 10 seconds");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } finally {
                    req.unsubscribe(); // remove old event subscription   
                }
            }
            
        } while (++resent < this.queryRetries);
        throw new TransmissionException(this.queryRetries 
        								+" repetition of the ptp request did not succed ( "
        								+ tranmissionError + " transmission errors and "
        								+ timeoutError + " timeout errors)");
    }    
    
    /**
     * 
     * @param repeaters
     * @return
     */
    private int getTimeOut(WaveTalk[] repeaters) {
        // set timeout - units are 100 ms
        int timeout = PTP_QUERY_TIME / 100; // 4 seconds
        if (repeaters != null) {
            switch (repeaters.length) {
                case 1 : timeout = RPT_1_QUERY_TIME / 100;
                break;
                case 2 : timeout = RPT_2_QUERY_TIME / 100;
                break;
                case 3 : timeout = RPT_3_QUERY_TIME / 100;
                break;
            }
        }
        return timeout;
    }
    
    /**
     * Send a frame (Only for debugging purpose)
     * 
     * @param cmd
     * @param data
     * @return true if frame has been sent without error
     */
    public boolean sendFrame(int cmd, int[] data){
        try {
            /*
             * Receive a CMD and data and sent it.
             *
             * This method is primarly used for debugging purpose
             */
            this.fwriter.sendFrame(cmd, data);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    /**
     * Get the Frame reader (only for debugging)
     * 
     * @return A CoronisFrameReader object
     */
    public CoronisFrameReader getFrameReader() {
        return freader;
    }
}
