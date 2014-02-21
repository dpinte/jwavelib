/**
 * 
 */
package com.coronis.test.modules;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.coronis.exception.BadlyFormattedFrameException;
import com.coronis.exception.CoronisException;
import com.coronis.logging.Logger;
import com.coronis.modules.WaveThermPT100;
import com.coronis.test.CommonTest;
import com.dipole.libs.DataSet;
import com.dipole.libs.Measure;

/**
 * @author antoine
 *
 */
public class WaveThermPT100Test extends WaveThermPT100 {
	private static final int[] modID = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};

	private static final String currValMsg = "";
	private static final String datalLogMsg = "";
	private static final String advDataLogMsg = "";
	private static final String[] advDataLogMultiMsg = {""};
	private static final String[] advDataLogMultiRepMsg = {""};
	
	private DataSet testDataSet;
	
	public WaveThermPT100Test() {
		super(modID, null, null);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Logger.DEBUG = true;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveThermPT100#parseTemperature(int[])}.
	 */
	@Test
	public final void testParseTemperature() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveTherm#readDatalog(int[])}.
	 * @throws CoronisException 
	 */
	@Ignore
	@Test
	public final void testReadDatalog() throws CoronisException {
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wth_pt100_dataLog.csv"));
		this.readDatalog(CommonTest.msgFromHexString(datalLogMsg));
		
		assertEquals(this.testDataSet.getLength(), this.dataSet.getLength());
		
		Measure expected;
		Measure actual;
		for(int i = 0; i< this.dataSet.getLength(); i++) {
			expected = this.testDataSet.getMeasure(i);
			actual = this.dataSet.getMeasure(i);
			
			assertEquals(expected.getValue(), actual.getValue(), 0);
			
			/* timestamp with delta = 1 second */
			assertEquals(expected.getTimeStamp(), actual.getTimeStamp(), 1000);
		}
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveTherm#readAdvancedDataLog(int[], boolean)}.
	 * @throws CoronisException 
	 */
	@Test
	public final void testReadAdvancedDataLogSingle() throws CoronisException {
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wth_pt100_advDataLog_single.csv"));
		this.readAdvancedDataLog(CommonTest.msgFromHexString(advDataLogMsg), false);
		
		assertEquals(this.testDataSet.getLength(), this.dataSet.getLength());
		
		Measure expected;
		Measure actual;
		for(int i = 0; i< this.dataSet.getLength(); i++) {
			expected = this.testDataSet.getMeasure(i);
			actual = this.dataSet.getMeasure(i);
			
			assertEquals(expected.getValue(), actual.getValue(), 0);
			
			/* timestamp with delta = 1 second */
			assertEquals(expected.getTimeStamp(), actual.getTimeStamp(), 1000);
		}
	}

	@Test
	public final void testReadAdvancedDataLogMulti() throws CoronisException {
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wth_pt100_advDataLog_multi.csv"));
		
		for(int i = 0; i < advDataLogMultiMsg.length; i++) {
			this.readAdvancedDataLog(CommonTest.msgFromHexString(advDataLogMultiMsg[i]), true);
		}
		
		assertEquals(this.testDataSet.getLength(), this.dataSet.getLength());
		
		Measure expected;
		Measure actual;
		for(int i = 0; i< this.dataSet.getLength(); i++) {
			expected = this.testDataSet.getMeasure(i);
			actual = this.dataSet.getMeasure(i);
			
			assertEquals(expected.getValue(), actual.getValue(), 0);
			
			/* timestamp with delta = 1 second */
			assertEquals(expected.getTimeStamp(), actual.getTimeStamp(), 1000);
		}
	}
	
	@Test
	public final void testReadAdvancedDataLogMultiReapeated() throws CoronisException {
		this.testDataSet =  CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wth_pt_100_advDataLog_multi.csv"));
		
		for(int i = 0; i < advDataLogMultiRepMsg.length; i++) {
			this.readAdvancedDataLog(CommonTest.msgFromHexString(advDataLogMultiRepMsg[i]), false);
		}
		
		assertEquals(this.testDataSet.getLength(), this.dataSet.getLength());
		
		Measure expected;
		Measure actual;
		for(int i = 0; i< this.dataSet.getLength(); i++) {
			expected = this.testDataSet.getMeasure(i);
			actual = this.dataSet.getMeasure(i);
			
			assertEquals(expected.getValue(), actual.getValue(), 0);
			
			/* timestamp with delta = 1 second */
			assertEquals(expected.getTimeStamp(), actual.getTimeStamp(), 1000);
		}
	}
	
	/**
	 * Test method for {@link com.coronis.modules.WaveTherm#readCurrentValues(int[])}.
	 * @throws BadlyFormattedFrameException 
	 */
	@Test
	public final void testReadCurrentValues() throws BadlyFormattedFrameException {		
		double[] temp = this.readCurrentValues(CommonTest.msgFromHexString(currValMsg));
		
		assertEquals(24.25, temp[0], 0);
		assertEquals(Double.NaN, temp[1], 0);
	}
	
	@Test
	public final void testReadType() {
		String typeMsg = "0519063028B5A0192D0119";	
		try {
			this.readType(CommonTest.msgFromHexString(typeMsg));
			fail("Module type is not the right one");
		} catch (CoronisException e) {			
			System.out.println(this._moduleType);
			System.out.println(this.getModuleType());
		} 		
	}			

}
