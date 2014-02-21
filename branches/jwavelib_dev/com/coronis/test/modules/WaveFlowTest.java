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
import com.coronis.modules.WaveFlow;
import com.coronis.test.CommonTest;
import com.dipole.libs.DataSet;
import com.dipole.libs.Measure;

/**
 * @author antoine
 *
 */
public class WaveFlowTest extends WaveFlow {
	private static final int[] modID = {0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF};
	
	private static final String typeMsg = "0519063028B5A0192D0119";	
	private static final String currValMsg = "051606304A448164000000002A00000000";
	private static final String datalLogMsg = "051606304A448364000000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A1406090602040D";	
	private static final String advDataLogMsg = "051606304A448901011406090602040501083408210000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A";
	private static final String[] advDataLogMultiMsg = {"000404051606304A448901041406090602040501083408150000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A",
														"000403051606304A4489020401081407F50000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A",
														"000402051606304A448903040107F407D50000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A",
														"000401051606304A448904040107D407D10000002A0000002A0000002A0000002A"};	
	private static final String[] advDataLogMultiRepMsg = {	"051606304A448901011406090602040501083408180000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A",
															"051606304A448901011406090602040501081707FB0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A", 
															"051606304A44890101140609060204050107FA07DE0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A", 
															"051606304A44890101140609060204050107DD07D10000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A0000002A"};
	
	private DataSet testDataSet;
	
	public WaveFlowTest() {
		super(modID, null, null);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
		this.testDataSet = null;
		this.dataSet.clear();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveFlow#readDatalog(int[])}.
	 * @throws CoronisException 
	 */
	@Test
	public final void testReadDatalog() throws CoronisException {
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wfl_dataLog.csv"));
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
	 * Test method for {@link com.coronis.modules.WaveFlow#readAdvancedDataLog(int[], boolean)}.
	 * @throws CoronisException 
	 */
	@Test
	public final void testReadAdvancedDataLogSingle() throws CoronisException {
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wfl_advDataLog_single.csv"));
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
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wfl_advDataLog_multi.csv"));
		
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
	public final void testReadAdvancedDataLogMultiRepeated() throws CoronisException {
		this.testDataSet = CommonTest.buildDataSetFromCsv(this.getClass().getResourceAsStream("./dataSet/wfl_advDataLog_multi.csv"));
		
		for(int i = 0; i < advDataLogMultiMsg.length; i++) {
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
	 * Test method for {@link com.coronis.modules.WaveFlow#readCurrentValues(int[])}.
	 * @throws BadlyFormattedFrameException 
	 */
	@Test
	public final void testReadCurrentValues() throws BadlyFormattedFrameException {
		double[] values = this.readCurrentValues(CommonTest.msgFromHexString(currValMsg));
		
		assertEquals(42.0, values[0], 0);
		assertEquals(0.0, values[1], 0);
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveFlow#addDifferentialConsumption(com.dipole.libs.DataSet, double)}.
	 */
	@Test
	@Ignore
	public final void testAddDifferentialConsumption() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveFlow#parseValue(int[], int)}.
	 */
	@Test
	public final void testParseValueIntArrayInt() {
		// Test The minimum value
		assertEquals(0, this.parseValue(0x00, 0x00, 0x00, 0x00));

		// Test a value
		assertEquals(638018, this.parseValue(0x00, 0x09, 0xBC, 0x42));
		
		// Test the maximum value for 32 bits
		Long max = new Long("4294967295");
		assertEquals(max.longValue(), this.parseValue(0xFF, 0xFF, 0xFF, 0xFF));

		// Test the maximum integer value for the JVM
		assertEquals(Integer.MAX_VALUE,
				this.parseValue(0x80, 0x00, 0x00, 0x00) - 1);
	}

	/**
	 * Test method for {@link com.coronis.modules.WaveFlow#parseValue(int, int, int, int)}.
	 */
	@Test
	public final void testParseValueIntIntIntInt() {
		int test = 841710;
		for (int j = 0; j < test; j+=1) {
			int v1 = test+j >> 3*8;
			int v2 = (test+j >> 2*8) & 0x000000FF;
			int v3 = (test+j >> 8) & 0x000000FF;
			int v4 = (test+j) & 0x000000FF;
			assertEquals(test+j,  this.parseValue(v1,v2,v3,v4));
		}
	}


	@Test
	public final void testReadType() {
		try {
			this.readType(CommonTest.msgFromHexString(typeMsg));
			fail("Module type is not the right one");
		} catch (CoronisException e) {			
			
		} 		
	}	
	
	/**
	 * Test method for {@link com.coronis.modules.WaveFlow#readGlobalCurentValues(int[])}.
	 */
	@Test
	@Ignore
	public final void testReadGlobalCurentValues() {
		fail("Not yet implemented"); // TODO
	}

}
