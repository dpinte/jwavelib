/**
 * DataLoggingModule.java
 * 
 * Created on 23 octobre 2008, 09:00
 *
 * Author : Didrik Pinte <dpinte@dipole-consulting.com>
 * Copyright : Dipole Consulting SPRL 2007-2008
 * 
 * This is a generic datalogging module from Coronis. It is an abstract class !
 *
 * $Date: 2009-09-17 20:35:39 +0200 (Thu, 17 Sep 2009) $
 * $Revision: 160 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/modules/DataLoggingModule.java $
 */

package com.coronis.modules;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Calendar;

import com.coronis.CoronisLib;
import com.coronis.exception.BadlyFormattedFrameException;
import com.coronis.exception.ConfigException;
import com.coronis.exception.CoronisException;
import com.coronis.exception.MissingDataException;
import com.coronis.exception.TransmissionException;
import com.coronis.exception.UnsupportedFrameException;
import com.coronis.logging.Logger;
import com.dipole.libs.*;

/**
 * Class to represent a datalogging module
 * <p>
 * Known limitations:<br>
 * <ul>
 * <li> Only time step datalogging is supported
 * </ul>
 */
public abstract class DataLoggingModule extends Module {

    protected int _frequency; // units are milliseconds
    protected long saved_datalog_date;
    //protected double saved_datalog_value;
    protected DataSet dataSet;

    private static final int MILLISECONDS_PER_MINUTE = 1000 * 60;
    // FIXME : default frequency should not be used. If not provided by the
    // end-user, it should be read from the module
    public static final int DEFAULT_FREQUENCY = 15; // 15 minutes

    protected int MAX_DATA_PER_SINGLEFRAME;
    protected int MAX_EXTENDED_DATALOG_COUNT;
    protected int MAX_DATALOG_COUNT;

    public static int DATALOG_PEMARNENT_LOOP = 0x00;
    public static int DATALOG_STOP_MEMORY_FULL = 0x02;

    public static int DATALOG_DEACTIVATED = 0x00;
    public static int DATALOG_TIME_STEPS = 0x04;
    public static int DATALOG_ONCE_WEEK = 0x08;
    public static int DATALOG_ONCE_MONTH = 0x0C;

    protected boolean _dataloggingParameterLoaded = false;
    protected int _operatingMode = 0;
    protected int _measurementPeriod = 0;
    protected int appStatus = 0;
    protected boolean _isLastCallOk = true;
    protected int correction;	
    protected int module_type = 0x00;

    /**
     * Creates a new DataLogging module
     * 
     * @param moduleId The radio address of the module
	 * @param wpt The WavePort use with this module
	 * @param modRepeaters The repeaters to reach the module
     */
    public DataLoggingModule(int[] moduleId, WavePort wpt, WaveTalk[] modRepeaters) {
            this(Functions.printHumanHex(moduleId, false), DEFAULT_FREQUENCY, moduleId, modRepeaters, wpt);
    }

    /**
     * Creates a new DataLogging module
     * 
     * @param modname The module name
     * @param freqMinute The datalogging frequency of the module (in minutes)
     * @param moduleId The radio address of the module
     * @param modRepeaters The repeaters to reach the module
     * @param wpt The WavePort use with this module
     */
    public DataLoggingModule(String modName, int freqMinute, int[] moduleId, WaveTalk[] modRepeaters, WavePort wpt) {
        super(modName, moduleId, wpt, modRepeaters);

        this.dataSet = new DataSet(moduleId);

        _frequency = freqMinute * 60 * 1000;
        _isDataloggingModule = true;
        this.setMaxValues();

        Logger.debug(   "Creating "+ this._moduleName 
                        +" : ID = "+ this.getModuleId()
                        +" / freq = "+ this._frequency);
    }

    /**
     * setMaxValues() define two constants for the classes : -
     * MAX_EXTENDED_DATALOG_COUNT - MAX_DATA_PER_SINGLEFRAME
     */
    protected abstract void setMaxValues();

    /**
     * Get the maximum values in Datalogging table
     * 
     * @return An Integer
     */
    public int getMaxDatalogCount() {
            return this.MAX_EXTENDED_DATALOG_COUNT;
    }

    /**
     * Get the maximum value received per frame
     * 
     * @return An Integer
     */
    public int getMaxDataPerSingleFrame() {
            return this.MAX_DATA_PER_SINGLEFRAME;
    }

    /**
     * Restart the dataLogging
     * 
     * @return true if restart OK
     * @throws CoronisException
     * @throws IOException
     * @throws InterruptedIOException
     */
    public abstract boolean resetDatalogging() throws CoronisException, IOException, InterruptedIOException;
    
    /**
     * Get the frequency of measurement 
     *
     * @return The frequency in mili-second
     */
    public int getFrequency() {
        return this._frequency;
    }
    
    /**
     * Save Some DataSet MetaData
     */
    public void saveLastDatalogInfo() {
        this.saved_datalog_date = this.dataSet.getLastDataLogDate();
        //this.saved_datalog_value = this.LAST_DATALOG_VALUE;
    }
	
    /**
     * Resets the LAST_DATALOG_DATE to the latest saved on
     */
    public void resetLastDatalogInfo() {
        this.dataSet.setLastDataLogDate(this.saved_datalog_date);
        //this.LAST_DATALOG_VALUE = this.saved_datalog_value;
    }

    /**
     * Check if las call is OK
     * 
     * @return A boolean
     */
    public boolean isLastCallOk() {
            return this._isLastCallOk;
    }
	
    /**
    * Initialize the module by :
    * 	- get the RSSI to all the repeaters if used.
    * 	- get the type of the module
    *   - sets the time of the module using the system time
    *   - check the datalogging parameters
    * 
    * @return true if everything is OK
    */
    public boolean initialise() {
        boolean state = false;
        Logger.log("Initialisation of : " + this.getModuleId());
        Calendar cld = Calendar.getInstance();
        try {
            if (this.isRepeated()) {
                for (int wtkcnt = 0; wtkcnt < this.getRepeaterCount(); wtkcnt++) {
                    WaveTalk wtk = this.getRepeaters()[wtkcnt];
                    try {
                        // get repeaters to access him
                        WaveTalk[] replist = null;
                        // get the previous repeaters in list
                        if (wtkcnt >= 1) {
                            replist = new WaveTalk[wtkcnt];
                            for (int replistcnt = 0; replistcnt < replist.length; replistcnt++) {
                                replist[replistcnt] = this.getRepeaters()[replistcnt];
                            }
                        }
                        // logger.info("RSSI for " + wtk.getName() + " is " +
                        // module.waveport.readRSSI(wtk.getRadioId(), replist));
                        Logger.info(wtk.getType(this.waveport, replist));
                    } catch (CoronisException e) {
                        Logger.warning("could not contact wavetalk "
                                        + wtk.getName() + ". Skipped");
                    }
                }
            }

            Logger.info(this.getType());

            if (this.setTime(cld) == false) {
                Logger.error("Error while setting the date and time for module :"
                            + this.getModuleId());
            } else {
                Logger.debug("Set date and time succeeded for : "
                                + this.getModuleId());
            }
            
            if (this.checkParameters()) {
                Logger.info("Module datalogging parameters ok");
            } else {
                Logger.info("Module datalogging parameters aren't set properly. Restarting datalogging for module: "
                            + this.getModuleId());
                this.resetDatalogging();
            }

            state = true;
        } catch (TransmissionException e) {
            this.deActivate();
            Logger.error("Transmission exception: "+ e.getMessage());
            Logger.warning("Module "+ this.getName()
                            +"  has been deactivated");
        } catch (UnsupportedFrameException e) {
            Logger.error("Error while running data acquisition: "
                        + e.getMessage() +" type is "+ e.getMessage());
        } catch (BadlyFormattedFrameException e) {
            Logger.error("Bad frame format exception : " + e.getMessage());
        } catch (InterruptedIOException e) {
            Logger.error("Thread has been interrupted - probably due to a timeout :"
                        + e.toString());
        } catch (IOException e) {
            Logger.error("Problem with IO :"+ e.getMessage());
        } catch (CoronisException e) {
            // FIXME : this is realy crappy
            Logger.error("Unsupported error while setting the module time : "
                        + e.getMessage());
        }

        return state;
    }

    /**
     * Retrieve the operation Mode and the frequency
     * <p>
     * If the frequency is different than
     * @throws InterruptedIOException
     * @throws IOException
     * @throws CoronisException
     */
    protected void getInternalParameter() throws InterruptedIOException, IOException, CoronisException {
        Logger.debug("Ask Internal Parameters for: "+ this.getModuleId());

        // request reading functional mode and datalogging frequency
        int[] answer = waveport.query_ptp_command(Message.askDataLoggingParameters(this._modid),
                                                                                                this._repeaters,
                                                                                                this.getModuleId());
        Logger.debug("param: "+ Functions.printHumanHex(answer, false));
        if (answer[Message.ID_LENGTH + 0] != Message.ACK_GET_SENSOR_PARAM) {
            Logger.error("Invalid answer to read module parameters while checking datalogging parameters");
        }

        /*
         * paralmeters order:
         * 1) OPERATING_MODE
         * 2) MEASUREMENT_PERIOD
         * 3) APPLICATIVE STATUS 
         */

        // load datalogging parameters from answer
        this._operatingMode = answer[Message.ID_LENGTH + 4];
        this._measurementPeriod = answer[Message.ID_LENGTH + 7];
        this.appStatus = answer[Message.ID_LENGTH + 9];
        this._dataloggingParameterLoaded = true;
    }

    /**
     * Stop the dataLogging
     * 
     * @return true if the dataLogging has been stoped
     * @throws CoronisException 
     * @throws IOException 
     * @throws InterruptedIOException 
     */
    public boolean stopDataLogging() throws InterruptedIOException, IOException, CoronisException {
        this._operatingMode &= 0xF3;

        int[] answer = this.waveport.query_ptp_command(Message.resetDatalogging(this._modid,
                                                                                this._operatingMode,
                                                                                CoronisLib.getDataloggingFrequency(this._frequency)),
                                                                                this._repeaters,
                                                                                this.getModuleId());

        boolean ret = (answer[Message.ID_LENGTH + 1] == 0x01) ? true : false;

        return ret;
    }

    /**
     * Start or restart the Datalogging NOW
     * <p>
     * To start the dataLogging, the RESTART_DATALOGGING command (0x0A)
     * is used. So the first is when the dataliogging has been started
     * 
     * @return true if the datalogging has been started
     * @throws InterruptedIOException
     * @throws IOException
     * @throws CoronisException
     */
    public abstract boolean startDataLoggingNow() throws InterruptedIOException, IOException, CoronisException;

    public boolean startDataLoggingAt(int hour) throws InterruptedIOException, ConfigException, IOException, CoronisException {	
        int[] answer = this.waveport.query_ptp_command(Message.setDataLoggingParemeters(this._modid,
                                                                                        this._operatingMode,
                                                                                        CoronisLib.getDataloggingFrequency(this._frequency),
                                                                                        hour),
                                                                                        this._repeaters,
                                                                                        this.getModuleId());

        boolean ret = (answer[Message.ID_LENGTH + 1] == 0x00) ? true : false;

        return ret;
    }

    /**
     * Check parameters
     * 
     * @return true if parameters are OK
     * @throws CoronisException
     * @throws IOException
     * @throws InterruptedIOException
     */
    // FIXME: Add option to set datalog_time_step and datalog_loop
    protected boolean checkParameters() throws CoronisException, IOException, InterruptedIOException {
        Logger.debug("Checking Parameters");
            if(!this._dataloggingParameterLoaded) {
                    this.getInternalParameter();
            }

            boolean succeeded = true;


            Logger.debug("operating mode: "+ Functions.printHumanHex(this._operatingMode, true));
            Logger.debug("frequency: "+ Functions.printHumanHex(this._measurementPeriod, true));

            // check functional mode
            if ((_operatingMode & 0x0C) != DataLoggingModule.DATALOG_TIME_STEPS) {
                    Logger.error(	"Step datalogging is not enabled ! Parameter has value :"
                                                    + (Functions.printHumanHex(_operatingMode, true)));
                    succeeded = false;
            }

            // check the stop mode of datalogging
            if ((_operatingMode & 0x02) != DataLoggingModule.DATALOG_PEMARNENT_LOOP) {
                    Logger.error("Datalogging is not in permanent loop mode !");
                    succeeded = false;
            }

            // check the frequency
            int freq_per_unit = _measurementPeriod >> 2;

            int unit = 0;
            switch (_measurementPeriod & 0x03) {
                    case 0x00: unit = 1;
                                       break;
                    case 0x01: unit = 5;
                                      break;
                    case 0x02: unit = 15;
                               break;
                    case 0x03: unit = 30;
                               break;
                    default : throw new CoronisException("Unit cannot be > than 0x04");
            }

            Logger.debug("Units are : " + unit + " minutes");
            Logger.debug("Frequency is : " + freq_per_unit);

            if ((freq_per_unit * unit * DataLoggingModule.MILLISECONDS_PER_MINUTE) != this._frequency) { 
                    Logger.error(	"Frequency is not setted correctly. Value is "
                                                    + (freq_per_unit * unit) + " minutes");
                    succeeded = false;
            }

            return succeeded;
    }

    /**
     * Get the current DataSet
     * 
     * @return a DataSet
     */
    public DataSet getDataSet() {
            return this.dataSet;

    }

    /**
     * Request to read the DataLogging table of the module
     * <p>
     * Clear the dataSet then send a request to the module;
     * 
     * @return A dataSet of all received values
     * @throws CoronisException
     * @throws IOException
     * @throws InterruptedIOException
     */
    public DataSet getDataLog() throws CoronisException, IOException, InterruptedIOException {
        Logger.debug(this.getModuleId() +": request DataLog");

        this.dataSet.clear();
        //this.dataSet.setType(DataSet.DATALOG_TYPE);

        this.readDatalog(this.waveport.query_ptp_command(Message.askDataLog(_modid),
                                                            this._repeaters,
                                                            this.getModuleId()));

        return this.dataSet;
    }

    /**
     * Read the response of the request, parse and 
     * record all value in the DataSet
     * 
     * @param msg
     * @throws CoronisException
     */
    protected abstract void readDatalog(int[] msg) throws CoronisException;

    /**
     * Request to read the Advanced Datalog table of the module
     * <p>
     * Clear the dataSet then send a request to the module;
     * 
     * @param measureToRead	Number of value to read
     * @param fromIdx 	The index to start reading Advanced datalog
     * 					<p>
     * 					0x00 to read from the last index
     * @return A DataSet holding all measure received
     * @throws CoronisException
     * @throws IOException
     * @throws InterruptedIOException
     */
    public DataSet getAdvancedDataLog(int measureToRead, int fromIdx) throws CoronisException, IOException, InterruptedIOException {
            Logger.debug(	this.getModuleId() +": Request to read "
                                            + measureToRead +" values in in Advanced DataLog "
                                            + "from index "+fromIdx);

            /* check if we don't ask more value than Advanced Dalalogging can record */ 
            if(measureToRead > this.MAX_EXTENDED_DATALOG_COUNT) {
                    throw new CoronisException("Maximum number of datalogging for sensor is "
                                                                            + this.MAX_EXTENDED_DATALOG_COUNT +" but, "
                                                                            + measureToRead +" asked");
            }

            this.dataSet.clear();
            //this.dataSet.setType(DataSet.ADVANCED_DATALOG_TYPE);

            this._retrieveAdvancedDataLog(measureToRead, fromIdx);

            return this.dataSet;
    }

    /**
     * 
     * @param measureToRead
     * @param fromIdx
     * @param MultiFrame
     * @throws CoronisException
     * @throws IOException
     * @throws InterruptedIOException
     */
    // FIXME: Send request to stop DataLogging if MultiFrame is not used
    protected void _retrieveAdvancedDataLog(int measureToRead, int fromIdx) throws CoronisException, IOException, InterruptedIOException {

        /*
         * this is a correction count parameter that must be set when
         * datacollection does not start from the latest datalogged measure.
         * correction value will be used in the readAdvanceddDatalog method !
         */
        if (fromIdx != 0) {
            correction = _frequency	* (this.MAX_EXTENDED_DATALOG_COUNT - fromIdx);
        } else {
            correction = 0;
        }

        /* Check if the number of value to read can be sent with a single frame */
        if(measureToRead > this.MAX_DATA_PER_SINGLEFRAME) {
            Logger.debug("measureToRead > MAX_DATA_PER_SINGLEFRAME");
            if(this.isRepeated()) {
                /* Repeaters are used. we can't use MultiFrames
                 * instead send single frame queries until we have all needed values
                 */

                Logger.log(	"The module use reapeaters. "
                            + ((measureToRead / this.MAX_DATA_PER_SINGLEFRAME)+ 1)
                            +" frames will be sent to read "
                            + measureToRead +" values");
                int toRead;
                do {
                    toRead = (measureToRead > this.MAX_DATA_PER_SINGLEFRAME) ? this.MAX_DATA_PER_SINGLEFRAME : measureToRead;
                    try {
                        Logger.debug("trying to read "+ toRead 
                        			+" values from index "+ fromIdx);

                        this.readAdvancedDataLog(this.waveport.query_ptp_command(Message.askExtendedDataLog(this, toRead, fromIdx),
                                                                                                            this._repeaters,
                                                                                                            this.getModuleId()),
                                                false);	
                    } catch (MissingDataException e) {
                        throw e;
                    } catch (CoronisException e) {
                        this._isLastCallOk = false;
                        fromIdx = fromIdx - MAX_DATA_PER_SINGLEFRAME;
                    }

                    /*
                     * update lastIdx variable --> next call will be done using the
                     * lastIndex minus 1 (indexes are decreasing in the datalog
                     * table)
                     */
                    fromIdx = this.dataSet.getLastReceivedIndex() - 1;
                    //fromIdx = (fromIdx - 1 < 0) ? MAX_EXTENDED_DATALOG_COUNT
                    //		: fromIdx - 1;
                    measureToRead -= toRead;

                    Logger.debug("To be retrieved " + measureToRead);

                    // Wait a bit to let the Waveport retake its breath
                    try {
                        Thread.currentThread().sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } while (measureToRead > 0);
            } else {
                Logger.debug("Use Multiframes");
                /* send using Multi-Frames */
                int answers[][];
                answers = waveport.query_ptp_multiframe(Message.askExtendedDataLog(this, measureToRead, fromIdx),
                                                        this.getModuleId());

                if(answers.length > 1){
                    for(int i = 0; i < answers.length; i++) {
                        this.readAdvancedDataLog(answers[i], true);
                    }
                } else {
                   /*
                    * If we request more values than present in the datalog table
                    * and module can send all values in a single frame
                    */
                    this.readAdvancedDataLog(answers[0], false);
                }
            }
        } else {
            this.readAdvancedDataLog(this.waveport.query_ptp_command(Message.askExtendedDataLog(this, measureToRead, fromIdx),
                                                                    this._repeaters,
                                                                    this.getModuleId()),
                                    false);
        }
    }

    /**
     * 
     */
    protected abstract void readAdvancedDataLog(int[] msg, boolean isMultiFrame) throws CoronisException;

    /**
     * Request the current value for all sensors as described 
     * in the user guides.
     * <p>
     * The order of each item follow the name of the sensors:<br>
     * for example with a WaveTherm:
     * 	<ul>
     * 	<li>the item 0 is sensor A
     * 	<li>the item 1 is sensor B
     * 	</ul>
     * 	<p>
     * If a sensor is not activated or it's measure is missing, 
     * the value = -1
     *  
     * @return An Array of measure
     * @throws IOException 
     * @throws CoronisException 
     * @throws InterruptedIOException 
     */
    public double[] getCurrentValues () throws	InterruptedIOException, CoronisException, IOException {
            Logger.debug(this.getModuleId() +": request Current values");
            return readCurrentValues(this.waveport.query_ptp_command(Message.askCurrentValue(_modid),
                                                                      _repeaters,
                                                                      this.getModuleId()));
    }

    /**
     * Read each sensors value from message
     * <p>
     * If a sensors is not set, its value = -1;
     * 
     * @param msg
     * @return 
     * @throws CoronisException
     */
    protected abstract double[] readCurrentValues(int[] msg) throws BadlyFormattedFrameException;

    /**
     * Check if the battery reach its end of life
     * 
     * @return true if battery reach its end of life
     */
    public boolean batteryEndOfLife() {
            return ((this.appStatus & 0x02) == 0x2) ? true : false;
    }
    
    /**
     * Check the current operating mode byte and report datalogging status
     * 
     * @return true if datalogging is enabled
     */
    public boolean isDataLoggingEnabled() {
    	return ((this._operatingMode & 0x0C) == 0x00) ? false : true;
    }
    
    /**
     * Creates dataSet with measure difference
     * 
     * @return A DataSet
     */
    // FIXME : need test
    public DataSet createMeasureDiff() {
    	Logger.debug("create measures diff");
    	
    	DataSet diffSet = new DataSet(this._modid);
    	Measure prevMeas, actMeas;
    	double[] tempVal;
    	
    	if(this.dataSet.getLength() > 0) {
	    	for(int i = 0 ; i < this.dataSet.getLength() - 1; i++) {
	    		tempVal = new double[2];
	    		
	    		actMeas = this.dataSet.getMeasure(i);
	    		prevMeas = this.dataSet.getMeasure(i + 1);
	    		
	    		tempVal[0] = actMeas.getValue();
	    		
	    		if(	actMeas.getValue() == Double.NaN ||
	    			prevMeas.getValue() == Double.NaN) {
	    			Logger.debug("catMeas and presMeas == NaN");
	    			tempVal[1] = Double.NaN;
	    		} else {
	    			tempVal[1] = actMeas.getValue() - prevMeas.getValue();
	    		}
	    		
	    		diffSet.addMeasure(tempVal, actMeas.getTimeStamp());
	    	}
	    	
	    	tempVal = new double[2];
	    	actMeas = this.dataSet.getMeasure(this.dataSet.getLength() - 1);
	    	prevMeas = this.dataSet.getLastMeasure();
	    	
	    	tempVal[0] = actMeas.getValue();
	    	if(	actMeas.getValue() == Double.NaN ||
	    			prevMeas.getValue() == Double.NaN) {
	    			tempVal[1] = Double.NaN;
	    	} else {
	    		tempVal[1] = actMeas.getValue() - prevMeas.getValue();
	    	}
	    	
	    	diffSet.addMeasure(tempVal, actMeas.getTimeStamp());
    	}
    	
    	return diffSet;
    }
    
    /**
     * Check if operation mode and application status byte have changed.<br>
     * If changed, update it.
     * 
     * @param mode The operation mode byte
     * @param status the application status byte
     */
    protected void checkStatusAndMode(int mode, int status) {
    	if(this.appStatus != status) {
    		Logger.warning("application status has changed: "
    						+"from "+ Functions.printHumanHex(this.appStatus, true)
    						+" to "+ Functions.printHumanHex(status, true)
    						+" => update it");
    		this.appStatus = status;
    	}
    	
    	if(this._operatingMode != mode) {
    		Logger.warning("operating mode has changed: "
						+"from "+ Functions.printHumanHex(this._operatingMode, true)
						+" to "+ Functions.printHumanHex(mode, true)
						+" => update it");
    		this._operatingMode = mode;
    	}
    }
}
