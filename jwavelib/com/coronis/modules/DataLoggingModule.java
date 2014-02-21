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
 * $Date: 2009-07-17 11:38:34 +0200 (Fri, 17 Jul 2009) $
 * $Revision: 114 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/modules/DataLoggingModule.java $
 */

package com.coronis.modules;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Calendar;
import java.util.Date;

import com.coronis.exception.BadlyFormattedFrameException;
import com.coronis.exception.ConfigException;
import com.coronis.exception.CoronisException;
import com.coronis.exception.MissingDataException;
import com.coronis.exception.TransmissionException;
import com.coronis.exception.UnsupportedFrameException;
import com.dipole.libs.*;

public abstract class DataLoggingModule extends Module {

	protected int _frequency; // units are milliseconds
    protected long saved_datalog_date;
    protected double saved_datalog_value;
	protected double LAST_DATALOG_VALUE = Double.NaN;
	protected long LAST_DATALOG_DATE = 0;
	protected DataSet dst;

	public static final int MAX_DAYS_TO_RETRIEVE = 4;
	public static final int MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;
	public static final int MILLISECONDS_PER_MINUTE = 1000 * 60;
	// FIXME : default frequency should not be used. If not provided by the
	// end-user, it should be read from the module
	public static final int DEFAULT_FREQUENCY = 15; // 15 minutes

	public int MAX_DATA_PER_SINGLEFRAME;
	public int MAX_EXTENDED_DATALOG_COUNT;
	public int MAX_DATA_PER_FRAME;
	
	
	public static int DATALOG_PEMARNENT_LOOP = 0x00;
	public static int DATALOG_STOP_MEMORY_FULL = 0x02;
	
	public static int DATALOG_DEACTIVATED = 0x00;
	public static int DATALOG_TIME_STEPS = 0x04;
	public static int DATALOG_ONCE_WEEK = 0x08;
	public static int DATALOG_ONCE_MONTH = 0x0C;

	protected boolean _dataloggingParameterLoaded = false;
	protected int _operatingMode = 0;
	protected int _measurementPeriod = 0;
	
	protected long _lastCallDate = 0;
	protected boolean _isLastCallOk = true;
	protected int dataCounter;    
	protected int latestIdx;
	protected int correction;	

	protected Calendar extdlg_tstamp = null;

	/**
	 * Creates a new DataLogging module
	 * 
	 * @param moduleId The radio address of the module
	 * @param wpt The WavePort
	 * @param modRepeaters An array with the repeaters
	 */
	public DataLoggingModule(int[] moduleId, WavePort wpt,
			WaveTalk[] modRepeaters) {
		this(Functions.printHumanHex(moduleId, false), DEFAULT_FREQUENCY,
				moduleId, modRepeaters, wpt);
	}

	/**
	 * Creates a new DataLogging module
	 * 
	 * @param modName 
	 * @param freqMinute
	 * @param moduleId The radio address of the module
	 * @param modRepeaters An array with the repeaters
	 * @param wpt The WavePort
	 */
	public DataLoggingModule(String modName, int freqMinute, int[] moduleId,
			WaveTalk[] modRepeaters, WavePort wpt) {
		super(modName, moduleId, wpt, modRepeaters);
		
		_frequency = freqMinute * 60 * 1000;
		LAST_DATALOG_VALUE = Double.NaN;
		LAST_DATALOG_DATE = 0; // intialized to 1/1/1970 00h00
		_isDataloggingModule = true;
		this.setMaxValues();
	}

	/**
	 * setMaxValues() define two constants for the classes : -
	 * MAX_EXTENDED_DATALOG_COUNT - MAX_DATA_PER_SINGLEFRAME
	 */
	protected abstract void setMaxValues();

	/**
	 * 
	 * @return
	 */
	public int getMaxDatalogCount() {
		return this.MAX_EXTENDED_DATALOG_COUNT;
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxDataPerSingleFrame() {
		return this.MAX_DATA_PER_SINGLEFRAME;
	}

	/**
	 * 
	 * @param measureToRead
	 * @param fromIdx
	 * @return
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public boolean retrieveFromDatalogging(int measureToRead, int fromIdx)
			throws CoronisException, IOException, InterruptedIOException {
		/*
		 * Dallas can read maximum DALLAS_MAX_SINGLEFRAME data before going to
		 * multiframe
		 */

		this._lastCallDate = System.currentTimeMillis();
		this._isLastCallOk = true;

		latestIdx = fromIdx;
		if (latestIdx != 0) {
			// this is a correction count parameter that must be set when
			// datacollection does not start from the latest datalogged measure.
			// correction value will be used in the readExtendedDatalog method !
			correction = _frequency
					* (this.MAX_EXTENDED_DATALOG_COUNT - latestIdx);
			_logger.debug("Correction is :" + correction);
		} else
			correction = 0;
		dataCounter = 0;

		int toRead;

		// reinitialize the dataset
		dst = new DataSet(_modid);
		_logger.info("Must retrieve " + measureToRead
				+ " data from probe starting at " + fromIdx);
		if (this.isRepeated()) {
			do {
				toRead = (measureToRead > this.MAX_DATA_PER_SINGLEFRAME) ? this.MAX_DATA_PER_SINGLEFRAME
						: measureToRead;
				try {
					fromIdx = getExtendedDatalog(toRead, fromIdx);
				} catch (MissingDataException e) {
					throw e;
				} catch (CoronisException e) {
					this._isLastCallOk = false;
					fromIdx = fromIdx - MAX_DATA_PER_SINGLEFRAME;
				}
				// update lastIdx variable --> next call will be done using the
				// lastIndex minus 1 (indexes are decreasing in the datalog
				// table)
				fromIdx = (fromIdx - 1 < 0) ? MAX_EXTENDED_DATALOG_COUNT
						: fromIdx - 1;
				measureToRead -= toRead;
				_logger.debug("To be retrieved " + measureToRead);
				// Wait a bit to let the Waveport retake its breath
				try {
					Thread.currentThread().sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (measureToRead > 0);
		} else {
			getExtendedDatalog(measureToRead, fromIdx);
		}

		Date ldate = new Date(LAST_DATALOG_DATE);
		_logger.info("Last datalog date :" + ldate.toString());
		return this._isLastCallOk;
	}

	/**
	 * 
	 * @param msg
	 * @return
	 * @throws CoronisException
	 */
	public abstract String readDatalog(int[] msg) throws CoronisException;

	/**
	 * 
	 * @param msg
	 * @return
	 * @throws CoronisException
	 */
	public abstract String readCurrentValue(int[] msg) throws CoronisException;

	/**
	 * 
	 * @return
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public abstract boolean resetDatalogging() throws CoronisException,
			IOException, InterruptedIOException;

	/**
	 * 
	 * @return
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public boolean getDailyData() throws CoronisException, IOException,
			InterruptedIOException {
		/*
		 * Store 96 data per day
		 */

		// because we can have sliding measures, we add one more measure to the
		// count !
		int mustRead = (daysToRetrieve() * measurePerDay()) + 1;

		// lastIdx is the index of the data to read in the logs. When unknown
		// and want to have the latest one, should be initialized to 0x00
		int lastIdx = 0x00;

		return retrieveFromDatalogging(mustRead, lastIdx);
	}

	/**
	 * 
	 * @param dataToRead
	 * @param startingAT
	 * @return
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public boolean getData(int dataToRead, int startingAT)
			throws CoronisException, IOException, InterruptedIOException {

		if (dataToRead > this.getMaxDatalogCount())
			throw new ConfigException(
					"Maximum number of datalogging for sensor is "
							+ this.getMaxDatalogCount());

		// lastIdx is the index of the data to read in the logs. When unknown
		// and want to have the latest one, should be initialized to 0x00
		int lastIdx = 0x00;

		return retrieveFromDatalogging(dataToRead, lastIdx);
	}

	/**
	 * 
	 * @return
	 */
	public int measurePerDay() {
		/*
		 * Return the number of measure per day for the given module. Using the
		 * frequency that is in milliseconds !
		 */

		return (24 * 60 * 60 * 1000) / _frequency;
	}

	/**
	 * 
	 * @return
	 */
	public int daysToRetrieve() {
		// check that a single day is enough using LAST_DATALOG_DATE : if >
		// 24H00, then ask for the number of missing days with a maximum of 4
		// days
		// if LAST_DATALOG_DATE has not been updated, returns 1;
		Date ldate = new Date(LAST_DATALOG_DATE);
		_logger.info("Last datalog date is :" + ldate.toString());
		if (LAST_DATALOG_DATE == 0) {
			_logger
					.debug("Retrieve the last day of data (it's the first call)");
			return 1;
		}
		Calendar cld = Calendar.getInstance();
		// difference from now - last_datalog_date - _frequency (because we have
		//  tolerance equal to the frequency)
		long timeDiff = cld.getTime().getTime() - LAST_DATALOG_DATE - _frequency;
		// Compute the module of the timeDiff by 24H00
		int dayCount = (int) Math.ceil(timeDiff / MILLISECONDS_PER_DAY);
		if (dayCount < 0) {
			_logger
					.warning("It seems that there is a problem with the dates in the application. Current date is loware than the last datalog date ... Returning 1 day to retrive !");
			return 1;
		}
		_logger.debug("Days to retrieve :" + LAST_DATALOG_DATE + " / diff "
				+ timeDiff + " / count " + dayCount);
		_logger.info("Days to retrieve :" + dayCount);
		if (dayCount > MAX_DAYS_TO_RETRIEVE) {
			_logger.warning("Retrieving " + MAX_DAYS_TO_RETRIEVE
					+ " days from module");
			return MAX_DAYS_TO_RETRIEVE;
		} else
			return (dayCount == 0) ? 1 : dayCount;
	}

	/**
	 * 
	 * @param lastdldate
	 */
	public void setLastDatalogDate(long lastdldate) {
		LAST_DATALOG_DATE = lastdldate;
	}

	/**
	 * 
	 * @return
	 */
	public long getLastDatalogDate() {
		return LAST_DATALOG_DATE;
	}
        
    /**
     * 
     */
    public void saveLastDatalogInfo() {
        this.saved_datalog_date = this.LAST_DATALOG_DATE;
        this.saved_datalog_value = this.LAST_DATALOG_VALUE;
    }
    
    /**
     * 
     */
    public void resetLastDatalogInfo() {
        /* This method resets the LAST_DATALOG_DATE to the latest saved on*/
        this.LAST_DATALOG_DATE = this.saved_datalog_date;
        this.LAST_DATALOG_VALUE = this.saved_datalog_value;
    }

    /**
     * 
     * @return
     */
	public long getLastCallDate() {
		return this._lastCallDate;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isLastCallOk() {
		return this._isLastCallOk;
	}

	/**
	 * 
	 * @return
	 */
	// FIXME : this is really dirty ... the getData method should :
	// - either return void and an exception if an exception if needed, the data
	// should only be accessed by this getter
	// - or return the dst as filled even if an exception occured ...
	public DataSet getPartialDataSet() {
		return dst;
	}

	/**
	 * 
	 * @return
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public boolean checkParameters() throws CoronisException, IOException,
			InterruptedIOException {
		// request reading functional mode and datalogging frequency
		int[] answer = waveport.query_ptp_command(Message
				.askDataLoggingParameters(_modid), _repeaters, this.getModuleId());

		boolean succeeded = true;
		if (answer[Message.ID_LENGTH + 0] != Message.ACK_GET_SENSOR_PARAM) {
			_logger
					.error("Invalid answer to read module parameters while checking datalogging parameters");
			succeeded = false;
		}
		// data length, number of param, size of param, value of param, ...
		
		// load datalogging parameters from answer
		_operatingMode = answer[Message.ID_LENGTH + 4];
		_measurementPeriod = answer[Message.ID_LENGTH + 7];
		_dataloggingParameterLoaded = true;
		
		// check functional mode
		if ((_operatingMode & 0x0C) != DataLoggingModule.DATALOG_TIME_STEPS) {
			_logger
					.error("Step datalogging is not enabled ! Parameter has value :"
							+ (Functions.printHumanHex(
									_operatingMode, true)));
			succeeded = false;
		}
		
		// check the stop mode of datalogging
		if ((_operatingMode & 0x02) != DataLoggingModule.DATALOG_PEMARNENT_LOOP) {
			_logger.error("Datalogging is not in permanent loop mode !");
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
                _logger.debug("Units are : " + unit + " minutes");
                _logger.debug("Frequency is : " + freq_per_unit);
		if ((freq_per_unit * unit * DataLoggingModule.MILLISECONDS_PER_MINUTE) != this._frequency) { 
			_logger.error("Frequency is not setted correctly. Value is "
					+ (freq_per_unit * unit) + " minutes");
			succeeded = false;
		}

		return succeeded;
	}

	/**
	 * 
	 * @return
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public String getDatalog() throws CoronisException, IOException,
			InterruptedIOException {
		int[] answer = waveport.query_ptp_command(Message.askDataLog(_modid), _repeaters,
				this.getModuleId());
		return readDatalog(answer);
	}

	/**
	 * 
	 * @return
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public String getCurrentValue() throws CoronisException, IOException,
			InterruptedIOException {
		int[] answer = waveport.query_ptp_command(Message.askCurrentValue(_modid),
				_repeaters, this.getModuleId());
		return readCurrentValue(answer);
	}

	/**
	 * 
	 * @return
	 */
	public boolean initialise() {
		_logger.log("Setting time for  :" + this.getModuleId());
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
						_logger.info(wtk.getType(this.waveport, replist));
					} catch (CoronisException e) {
						_logger.warning("could not contact wavetalk "
								+ wtk.getName() + ". Skipped");
					}
				}
			}
			_logger.info(this.getType());

			if (this.setTime(cld) == false) {
				_logger
						.error("Error while setting the date and time for module :"
								+ this.getModuleId());
			} else {
				_logger.debug("Set date and time succeeded for  :"
						+ this.getModuleId());
			}
			if (this.checkParameters()) {
				_logger.info("Module datalogging parameters ok");
			} else {
				_logger
						.info("Module datalogging parameters aren't set properly. Restarting datalogging for module "
								+ this.getModuleId());
				this.resetDatalogging();
			}
			return true;
		} catch (TransmissionException ex) {
			this.deActivate();
			_logger.error("Transmission exception: " + ex.getMessage());
			_logger.warning("Module " + this.getName()
					+ "  has been deactivated");
		} catch (UnsupportedFrameException ex) {
			_logger.error("Error while running data acquisition: "
					+ ex.getMessage() + " type is " + ex.toString());
		} catch (BadlyFormattedFrameException e) {
			_logger.error("Bad frame format exception : " + e.toString());
		} catch (InterruptedIOException e) {
			_logger
					.error("Thread has been interrupted - probably due to a timeout :"
							+ e.toString());
		} catch (IOException e) {
			_logger.error("Problem with IO :" + e.toString());
		} catch (CoronisException e) {
			// FIXME : this is realy crappy
			_logger.error("Unsupported error while setting the module time : "
					+ e.toString());
		}
		return false;
	}

	/**
	 * 
	 * @param toRead
	 * @param from
	 * @return
	 * @throws CoronisException
	 * @throws IOException
	 * @throws InterruptedIOException
	 */
	public int getExtendedDatalog(int toRead, int from)
			throws CoronisException, IOException, InterruptedIOException {
		/*
		 * FIXME : Reads only the index table for counter A !
		 */

		_logger.debug("Asking " + toRead + " values starting at index " + from);

		// reset last value timestemp for extended datalogging calls
		extdlg_tstamp = null;
		int[] bytes = {};

		bytes = Message.askExtendedDataLog(this, toRead, from);

		if (toRead > this.MAX_DATA_PER_SINGLEFRAME) {
			_logger.debug("Multiframe query - " + toRead + " vs "
					+ this.MAX_DATA_PER_SINGLEFRAME);
			int[][] answer = waveport.query_ptp_multiframe(bytes, this
					.getModuleId());
                        if (answer.length == 1) {
                            readExtendedDatalog(answer[0], false);                            
                        } else {
                            for (int i = 0; i < answer.length; i++)
                                    readExtendedDatalog(answer[i], true);                            
                        }
                        return 0;
		} else {
			_logger.debug("Single frame query");
			int[] answer = waveport.query_ptp_command(bytes, _repeaters, this
					.getModuleId());
			return readExtendedDatalog(answer, false);
		}
	}

	/**
	 * 
	 * @param msg
	 * @param isMultiFrame
	 * @return
	 * @throws CoronisException
	 */
	public abstract int readExtendedDatalog(int[] msg, boolean isMultiFrame)
			throws CoronisException;

}
