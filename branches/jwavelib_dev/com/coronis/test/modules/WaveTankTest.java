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
import com.coronis.modules.WaveTank;
import com.coronis.test.CommonTest;
import com.dipole.libs.DataSet;
import com.dipole.libs.Measure;

/**
 * @author antoine
 *
 */
public class WaveTankTest extends WaveTank {
	private static final int[] modID = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
	
	private static final String currValMsg = "05220830067C810B800002";
	private static final String datalLogMsg = "05220830067C830B800001000100010001000100010001000100010001000100020001000100010001000100010001000100010001000100010001FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF0D070901080013";	
	private static final String advDataLogMsg = "";
	private static final String[] advDataLogMultiMsg = {"",	""};	
	private static final String[] advDataLogMultiRepMsg = {	"", ""};
	
	private DataSet testDataSet;
	
	public WaveTankTest() {
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

	@Test
	public final void testParseValue() {
		int[] values = {0x00, 0x00, 0x04, 0x00, 0x08, 0x00, 0x0C, 0x00, 0x0F, 0xFF, 0xF0, 0x02};

		assertEquals(0, this.parseValue(values[0], values[1]), 0.1);
		assertEquals(25, this.parseValue(values[2], values[3]), 0.1);
		assertEquals(50, this.parseValue(values[4], values[5]), 0.1);
		assertEquals(75, this.parseValue(values[6], values[7]), 0.1);
		assertEquals(100, this.parseValue(values[8], values[9]), 0.1);
		assertEquals(Double.NaN, this.parseValue(values[10], values[11]), 0);
	}
	
	/**
	 * Test method for {@link com.coronis.modules.WaveTank#readDatalog(int[])}.
	 * @throws CoronisException 
	 */
	@Test
	public final void testReadDatalog() throws CoronisException {
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wtk_dataLog.csv"));
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
	 * Test method for {@link com.coronis.modules.WaveTank#readAdvancedDataLog(int[], boolean)}.
	 * @throws CoronisException 
	 */
	@Test
	@Ignore
	public final void testReadAdvancedDataLogSingle() throws CoronisException {
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wtk_advDataLog_single.csv"));
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

	@Ignore
	@Test
	public final void testReadAdvancedDataLogMulti() throws CoronisException {
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wtk_advDataLog_multi.csv"));
		
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
	
	@Ignore
	@Test
	public final void testReadAdvancedDataLogMultiRepeated() throws CoronisException {
		this.testDataSet =  CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wtk_advDataLog_multi.csv"));
		
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
	 * Test method for {@link com.coronis.modules.WaveTank#readCurrentValues(int[])}.
	 * @throws BadlyFormattedFrameException 
	 */
	@Test
	public final void testReadCurrentValues() throws BadlyFormattedFrameException {
		double[] values = this.readCurrentValues(CommonTest.msgFromHexString(currValMsg));
		
		assertEquals(0, values[0], 0.1);
	}
	
	@Test
	public final void testReadType() {
		String typeMsg = "0519063028B5A0192D0119";	
		try {
			this.readType(CommonTest.msgFromHexString(typeMsg));
			fail("Module type is not the right one");
		} catch (CoronisException e) {			
			
		} 		
	}			

}
