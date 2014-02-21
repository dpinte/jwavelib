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
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 *
 * $Date: 2009-07-08 12:40:34 +0200 (Wed, 08 Jul 2009) $
 * $Revision: 98 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/modules/WavePort.java $
 */
package com.coronis.modules;

import com.coronis.exception.*;
import com.coronis.frames.*;
import com.coronis.modules.requests.*;
import com.dipole.libs.Functions;

import java.io.*;
import java.util.*;
import com.coronis.Config;
import com.coronis.logging.SimpleLogger;

public abstract class WavePort implements Runnable {
    
    
    // Internal parameters
    public static final short EXCHANGE_STATUS           = 0x0E;
    public static final short RELAY_ROUTE               = 0x07;
    public static final short ACK_RECEIVED_TIMEOUT      = 500;  // milliseconds before an ACK timeout
    public static final short ACK_RETRY_COUNT           = 3;    // there is maximum 3 retry to get an ACK
    
    public static final short QUERY_RETRIES             = 5;
        
    protected final String wpt_name;
    protected final String serialPort;
    protected OutputStream outputStream;
    protected InputStream inputStream;
    protected CoronisFrameWriter fwriter;
    protected CoronisFrameReader freader;
    protected boolean isConnected = false;
    
    // ACK_RETURN_TIME is the minimum time needed before sending an ACK to the WavePort.
    // Specifications says it's 1 milliseconds.
    protected static final int ACK_RETURN_TIME = 1;
    
    // Point to point query needs 2100 milliseconds to be executed.
    // Set to 3000 milliseconds to be sure.
    // Mean time for ptp with ACK and two retry after a bad reception of ACK)
    // see Excel Coronis documentation provided by Mr Rabaud on the 13/06
    // + 20 milliseconds (two ACK_RETUNR_TIME)
    public static final int PTP_QUERY_TIME = 4324 + ( 2 * ACK_RETURN_TIME) + 500;
    // Repeated ptp query with ACK 
    // by number of relais and for two retries
    public static final int RPT_1_QUERY_TIME = 5899 + ( 2 * ACK_RETURN_TIME) + 500;
    public static final int RPT_2_QUERY_TIME = 7410 + ( 2 * ACK_RETURN_TIME) + 500;
    public static final int RPT_3_QUERY_TIME = 8921 + ( 2 * ACK_RETURN_TIME) + 500;
    
    public String version = "not loaded";
    
    protected boolean _isListening;

    protected Thread    inputListener;
    protected Hashtable eventSubscribers;
    
    protected SimpleLogger _logger;
    
    /**
     * Creates a new WavePort
     * @param name The internal name of the WavePort
     * @param sport The serial port connected to the WavePort
     */
    public WavePort(String name, String sport) {
        wpt_name = name;
        serialPort = sport;
        eventSubscribers = new Hashtable();
        try {
            _logger = Config.getLogger();
        } catch (Exception e ) {
            System.err.println(e.toString());
            System.exit(1);
        }
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
     * @return true if no error durring setup
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
            _logger.error("Error while setting the awakening period.");
            return false;
        }
        if (setWakeUpType(0x00) == false) {
            _logger.error("Error while setting wake up type on waveport.");
            return false;
        }
        int[] wkplgt = {0x4C, 0X04};
        if (setWakeUpLength(wkplgt) == false) {
            _logger.error("Error while setting wake up length on waveport.");
            return false;
        }
        if (setRadioACK(0x01) == false) {
            _logger.error("Error while setting wake up length on waveport.");
            return false;
        }
        if (setRadioUserTimeOut(0x14) == false) {
            _logger.error("Error while setting radio user timeout on waveport.");
            return false;
        }
        if (setExchangeStatus(0x01) == false) {
            _logger.error("Error while activating error frames on waveport.");
            return false;
        }
        if (setSwitchModeStatus(0x01) == false) {
            _logger.error("Error while activating switch mode status on waveport.");
            return false;
        }
        return true;
    }
    
    /**
     * Set the SWITCH_MODE_STATUS parameter
     * <p>
     * Default value = 1 (automatic selection activated)
     * 
     * @param value <ul>
     * 				<li> 0: automatic selection deactivated
     * 				<li> 1: automatic selection activated
     * 				</ul>
     * @return true if no error durring setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setSwitchModeStatus(int value) throws IOException, CoronisException {
        int[] pData = {value};
        return setInternalParameter(0X10, pData);
    }               
    
    /**
     * Set the RADIO_USER_TIMEOUT parameter
     * <p>
     * Default value = 0x14 (2 seconds)
     * 
     * @param value Value in multiples of 100ms
     * @return true if no error durring setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setRadioUserTimeOut(int value) throws IOException, CoronisException  {
        int[] pData = {value};
        return setInternalParameter(0X0C, pData);
    }     
   
    /**
     * Set the RADIO_ACKNOWLEDGE parameter
     * <p>
     * Default value = 1 (with acknowledgement)
     * 
     * @param value <ul>
     * 				<li> 0: no acknowledgement
     * 				<li> 1: with acknowledgement
     * 				</ul>
     * @return true if no error durring setup
     * @throws IOException
     * @throws CoronisException
     */
    public boolean setRadioACK(int value) throws IOException, CoronisException  {
        int[] pData = {value};
        return setInternalParameter(0X04, pData);
    }  

    /**
     * Set the WAKEUP_LENGTH parameter
     * <p>
     * Default value = 1100 ms
     * 
     * @param value value in multiples of 1ms, LSB first<br>
     * 				<ul>
     * 				<li> min. value = 20 ms (0x1400)
     * 				<li> max. value = 10 sec (0x1027)
     * @return true if no error durring setup
     * @throws IOException
     * @throws CoronisException
     */
    // max. value = 10 sec. (0x1027)
    public boolean setWakeUpLength(int[] value) throws IOException, CoronisException  {       
        return setInternalParameter(0X02, value);
    }   
    
    /**
     * Set WAKEUP_TYPE parameter
     * <p>
     * Default value = 0: long wake-up
     * 
     * @param value <ul>
     * 				<li> 0: long wake-up
     * 				<li> 1: short wake-up (50 ms)
     * 				</ul>
     * @return true if no error durring setup
     * @throws IOException
     * @throws CoronisException
     */
    // 1: short wake-up = 50 ms
    public boolean setWakeUpType(int value) throws IOException, CoronisException  {
        int[] pData = {value};
        return setInternalParameter(0X01, pData);
    }    
    
    /**
     * Set AWAKENING_PERIOD parameter
     * <p>
     * Default value = 0x0A (1 sec)
     * 
     * @param value Period in multiples of 100 ms<br>
     * 				0 for nearly constant reception (20 ms)
     * @return true if no error durring setup
     * @throws IOException
     * @throws CoronisException
     */
    // Period in multiples of 100ms (by default,
    // 0x0A for one second; max. = 10 sec.)
    // 0 = nearly constant reception (every 20ms)
    public boolean setAwakeningPeriod(int value) throws IOException, CoronisException  {
        int[] pData = {value};
        return setInternalParameter(0x00, pData);
    }
    
    
    /**
     * Set EXCHANGE_STATUS parameter
     * <p>
     * Default value = 0x00 (status and error frames desactivated)
     *  
     * @param value <ul>
     * 				<li> 0: status and error frames desactivated
     * 				<li> 1: error frame activated
     * 				<li> 2: status frame activated
     * 				<li> 3 both status and error frames activated
     * 				</ul>
     * @return true if no error durring setup
     * @throws IOException
     * @throws CoronisException
     */
    // 0: status and error frames deactivated
    // 1: error frame activated
    // 2: status frame activated
    // 3: both status and error frames activated
    // Default EXCHANGE_STATUS = 0x00.
    public boolean setExchangeStatus(int value) throws IOException, CoronisException  {
        int[] pData = {value};
        return setInternalParameter(0x0E, pData);
    }  
    
    /**
     * Set the RELAY_ROUTE parameter
     * <p>
     * 3 repeaters maximum
     * 
     * @param wtk An array with the all the repeaters
     * @return true if no error durring setup
     * @throws IOException
     * @throws CoronisException
     */
    // BYTE 1: number of repeaters in route
    // Maximum number of repeaters = 3
    // If BYTE 1 != 0
    // BYTES 2 to 7: First repeater's radio
    // address..., etc.
    public boolean setRelayRoute(WaveTalk[] wtk) throws IOException, CoronisException  {
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
            return setInternalParameter(0x07, pData);
        } else {
            // 19 june 2008 : Mr Rabaud from Coronis Systems told us not to do anything when there is no repeaters ! 
            // it seems that the WavePort does use this parameter only one time ! Thus, it is not mandatory to set it.
            //pData = new int[1];
            //pData[0] = 0x00;
            //return setInternalParameter(0x07, pData);
            return true;
        }
        
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
        /*
         * Query the waveport and read it's firmware version in order to check
         * that the RS-232 connection works fine
         */
        if (isConnected == false) {
            connect();
        }
        
        // example of message
        // int[] msg = {0x05, 0x19, 0x06, 0x30, 0x2D, 0x69, 0x01 };
        _logger.debug("Check connection request sent");
                
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
                    _logger.log("Waveport firmware :" + version);
                    return true;
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
                _logger.warning("No ack exception. WavePort is in a bad mood. Waiting 2 seconds");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (CommandException e) {
                tranmissionError++;
                ptp.unsubscribe();
                _logger.warning("Command exception while talking to the WavePort. WavePort is in a bad mood. Waiting 2 seconds");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        } while (++resent < QUERY_RETRIES);
        return false;
    }
    
    /**
     * Request to set WavePort internal parameter
     * @param parameter
     * @param pData
     * @return true if no error durring setup
     * @throws IOException
     * @throws InterruptedIOException
     * @throws CoronisException
     */
    
    public boolean setInternalParameter(int parameter, int[] pData) throws
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
         
        _logger.debug("Set property request sent");
        
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
                    _logger.debug("Set write parameter returned");
                    if (((ResWriteParameterFrame)ptp.getAnswer()).getStatus() == true) {
                        _logger.debug("Property setted ok");   
                        return true;
                    } else {
                         _logger.error("Write parameter error while trying to set parameter " + Functions.printHumanHex(parameter, true) );
                        return false;                
                    }
                } else if (ptp.isTimeOut()) throw new TimeOutException("Waveport set paremeter request has timed out ...");
                else {
                    _logger.debug("Strange process did not finish correctly but it's not a timeout ...");
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
                _logger.warning("No ack exception. WavePort is in a bad mood. Waiting 10 seconds");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (CommandException e) {
                tranmissionError++;
                ptp.unsubscribe();
                _logger.warning("Command exception while talking to the WavePort. WavePort is in a bad mood. Waiting 10 seconds");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        } while (++resent < QUERY_RETRIES);
        throw new TransmissionException(QUERY_RETRIES + " repetition of the setInternalParameter request did not succed ( " + tranmissionError + " transmission errors and " + timeoutError + " timeout errors)");
    }
    
    
    /**
     * Send a frame
     * @param cmd
     * @param msg
     * @throws IOException
     */
    public void send(int cmd, int[] msg) throws IOException {
        fwriter.sendFrame(cmd , msg);
    }
   
    
    /**
     * Send a service request in Point-to-Point mode
     * @param msg
     * @param repeaters
     * @param moduleid
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
     * @param msg
     * @param repeaters
     * @param moduleid
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
     * @param msg
     * @param moduleid
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
                            output[i] = frames[i].getBinaryMessage();                            
                        }
                        return output;
                    }
                    
                    else if (ptp.isTimeOut()) throw new TimeOutException("PTP request has timed out ...");
                    else{
                        _logger.debug("Strange process did not finish correctly but it's not a timeout ...");
                        throw new CoronisException("Strange process did not finish correctly but it's not a timeout ...");
                    }
                } catch (TransmissionException e) {
                    _logger.warning("Transmission problem. Waiting 10 seconds then retrying");
                    tranmissionError++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (TimeOutException e) {
                    _logger.warning("Timeout exception. Waiting 10 seconds then retrying");
                    timeoutError++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (NoAckException e) {
                    tranmissionError++;
                    _logger.warning("No ack exception. WavePort is in a bad mood. Waiting 10 seconds");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (CommandException e) {
                    tranmissionError++;
                    
                    _logger.warning("Command exception while talking to the WavePort. WavePort is in a bad mood. Waiting 10 seconds");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } finally {
                    ptp.unsubscribe(); // remove old event subscription   
                }
            
        } while (++resent < QUERY_RETRIES);
        throw new TransmissionException(QUERY_RETRIES + " repetition of the ptp request did not succed ( " + tranmissionError + " transmission errors and " + timeoutError + " timeout errors)");                
    }
    
    
    /**
     * Request in Point-to-Point mode
     * @param msg
     * @param repeaters
     * @param moduleid
     * @param req
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
                    if (req.process()) return req.getAnswer().getBinaryMessage();
                    else if (req.isTimeOut()) throw new TimeOutException("PTP request has timed out ...");
                    else{
                        _logger.debug("Strange process did not finish correctly but it's not a timeout ...");
                        throw new CoronisException("Strange process did not finish correctly but it's not a timeout ...");
                    }
                } catch (TransmissionException e) {
                    _logger.warning("Transmission problem. Waiting 10 seconds then retrying");
                    tranmissionError++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (TimeOutException e) {
                    _logger.warning("Timeout exception. Waiting 10 seconds then retrying");
                    timeoutError++;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (NoAckException e) {
                    tranmissionError++;
                    _logger.warning("No ack exception. WavePort is in a bad mood. Waiting 10 seconds");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } catch (CommandException e) {
                    tranmissionError++;
                    
                    _logger.warning("Command exception while talking to the WavePort. WavePort is in a bad mood. Waiting 10 seconds");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } finally {
                    req.unsubscribe(); // remove old event subscription   
                }
            }
            
        } while (++resent < QUERY_RETRIES);
        throw new TransmissionException(QUERY_RETRIES + " repetition of the ptp request did not succed ( " + tranmissionError + " transmission errors and " + timeoutError + " timeout errors)");
    }    
    
    
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
    
    
    public CoronisFrameReader getFrameReader() {
        return freader;
    }
}


