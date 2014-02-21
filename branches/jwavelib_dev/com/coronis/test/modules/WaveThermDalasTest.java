/**
 * 
 */
package com.coronis.test.modules;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.coronis.exception.BadlyFormattedFrameException;
import com.coronis.exception.CoronisException;
import com.coronis.exception.MissingDataException;
import com.coronis.logging.Logger;
import com.coronis.modules.WaveThermDalas;
import com.coronis.test.CommonTest;
import com.dipole.libs.DataSet;
import com.dipole.libs.Measure;

/**
 * @author antoine
 *
 */
public class WaveThermDalasTest extends WaveThermDalas {
	private static final int[] modID = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
	
	private static final String currValMsg = "0519063028B581048001844FFF";
	private static final String datalLogMsg = "0519063028B58304800183018301820182018101800180017F0180017F017F017E017E017F017E017F017F017E017E017E017E017E017E017E017E017E017D017D017D017D017C017C017B017B017B017A01790179017901790179017A017A017A017A017A017A017A0F060901040D0D";	
	private static final String advDataLogMsg = "0519063028B58601010F060901040D119411810183018301820182018101800180017F0180017F017F017E017E017F017E017F017F017E017E017E";
	private static final String[] advDataLogMultiMsg = {"0002020519063028B586010213060905172B119411550178017801770177017701770176017601750175017501740173017301720171017101700170016F016E016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F01700170017001700170017001700170017001700170017001700170017001710171017101710171",
														"0002010519063028B5860202115411310171017101710171017101710170017001700170016F016F016E016F016E016E016E016D016D016C016C016B016A0169016901680167016701660166016601650165016401640163"};	
	private static final String[] advDataLogMultiRepMsg = {	"0519063028B586010113060905172B1194115A0178017801770177017701770176017601750175017501740173017301720171017101700170016F016E016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F016F017001700170017001700170017001700170017001700170017001700170",
															"0519063028B586010113060905172B11591131017101710171017101710171017101710171017101710170017001700170016F016F016E016F016E016E016E016D016D016C016C016B016A0169016901680167016701660166016601650165016401640163"};
	
	private DataSet testDataSet;
	
	public WaveThermDalasTest() {
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
		this.testDataSet= null;
		this.dataSet.clear();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=MissingDataException.class)
	public final void testParseTemperature() throws MissingDataException {
		// Test with value +125°C bits : 0000 0111 1101 0000  hexa : 0x07D0
		int[] val = {0x07, 0xD0};
		assertEquals(125.0, this.parseTemperature(val), 0.1);
		
		// Test with value +85°C 0000 0101 0101 0000 0x0550
		int[] val1 = {0x05, 0x50};
		assertEquals(85, this.parseTemperature(val1), 0.1);
		
		//Test with value -10.125°C 1111 1111 0101 1110 0xFF5E
		int[] val2 = {0xFF, 0x5E};
		assertEquals(-10.125, this.parseTemperature(val2), 0.1);
		
		//Test with value -55°C 1111 1100 1001 0000 0xFC90
		int[] val3 = {0xFC, 0x90};
		assertEquals(-55, this.parseTemperature(val3), 0.1);
		
		// Test with -25.0625   +125°C 1111 1110 0110 1111 0xFE6F
		int[] val4 = {0xFE, 0x6F};
		assertEquals(-25.0625, this.parseTemperature(val4), 0.1);
		
		int[] val5 = {0x01, 0x41};
		assertEquals(20.0625, this.parseTemperature(val5), 0.1);
		
		// test missing data exception
		int[] val6 = {0xFF, 0xFF};
		this.parseTemperature(val6);
	}
	
	/**
	 * Test method for {@link com.coronis.modules.WaveTherm#readDatalog(int[])}.
	 * @throws CoronisException 
	 */
	@Test
	public final void testReadDatalog() throws CoronisException {
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wth_Dalas_dataLog.csv"));
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
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wth_Dalas_advDataLog_single.csv"));
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
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wth_Dalas_advDataLog_multi.csv"));
		
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
		this.testDataSet =  CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wth_Dalas_advDataLog_multi.csv"));
		
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
		} catch (CoronisException e) {			
			fail("Module type is not the right one" + e.toString());
		} 		
	}			
}
