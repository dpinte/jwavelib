package com.coronis.test.frames;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.coronis.exception.CoronisException;
import com.coronis.frames.*;

public class CoronisFrameBuilderTest {
	private static int[] msg = {};
	
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * test build ACK frame
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_ACK() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.ACK, msg)
					instanceof ACKFrame);
	}
	
	/**
	 * test build NAK frame
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_NAK() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.NAK, msg)
				instanceof NAKFrame);
	}
	
	/**
	 * test build ERROR frame
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_ERROR() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.ERROR, msg)
				instanceof ErrorFrame);
	}
	
	/**
	 * test build TARNSMISSION_ERROR frame
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_TR_ERROR() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.TR_ERROR_FRAME, msg)
				instanceof TransmissionErrorFrame);
	}
	
	/**
	 * test build RES_SEND_FRAME and RES_SEND_SERVICE frames
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_RES_SEND() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_SEND_FRAME, msg)
													instanceof ResSendFrame);
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_SEND_SERVICE, msg)
													instanceof ResSendFrame);
	}
	
	/**
	 * test build RECEIVED_FRAME and SERVICE_RESPONSE frames
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_RECEIVED_FRAME() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RECEIVED_FRAME, msg)
													instanceof ReceivedFrame);
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.SERVICE_RESPONSE, msg)
													instanceof ReceivedFrame);
	}
	
	/**
	 * test build RECEIVED_MULTIFRAME frame
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_RECEIVED_MULTIFRAME() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RECEIVED_MULTIFRAME, msg)
													instanceof ReceivedMultiFrame);
	}

	/**
	 * test build RECEIVED_BROADCAST_RESPONSE frame
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_RECIEVED_BROADCAST () throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RECEIVED_BROADCAST_RESPONSE, msg)
													instanceof ReceivedBroadcastResponseFrame);
	}

	/**
	 * test build RES_FIMRWARE_VERSION frame
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_FIRMWARE() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_FIRMWARE_VERSION, msg)
					instanceof WavePortFirmwareFrame);
	}
	
	/**
	 * test build RES_CHANGE_RX_POWER, RES_CHANGE_UART_BAUDRATE, RES_SELECT_CHANNEL,
	 * RES_SELECT_PHYCONFIG, RES_WRIT_AUTOCORR_STATE and RES_WRIT_PARAM frames
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_RES_WRITE_PARAM() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_CHANGE_TX_POWER, msg)
					instanceof ResWriteParameterFrame);
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_CHANGE_UART_BAUDRATE, msg)
					instanceof ResWriteParameterFrame);
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_SELECT_CHANNEL, msg)
					instanceof ResWriteParameterFrame);
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_SELECT_PHYCONFIG, msg)
					instanceof ResWriteParameterFrame);
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_WRIT_AUTOCORR_STATE, msg)
					instanceof ResWriteParameterFrame);
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_WRIT_PARAM, msg)
					instanceof ResWriteParameterFrame);
	}
	
	/**
	 * test build RES_READ_PARAM, RES_READ_CHANNEL, RES_READ_PHYCONFIG and
	 * RES_READ_TX_POWER frames
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_RES_READ_PARAM() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_READ_PARAM, msg)
					instanceof ResReadParameterFrame);
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_READ_CHANNEL, msg)
					instanceof ResReadParameterFrame);
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_READ_PHYCONFIG, msg)
					instanceof ResReadParameterFrame);
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_READ_TX_POWER, msg)
					instanceof ResReadParameterFrame);
	}
	
	/**
	 * test build REQ_WRIT_PARAM frame
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_REQ_WRITE_PARAM() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.REQ_WRIT_PARAM, msg)
					instanceof ReqWriteParameterFrame);
	}
	
	/**
	 * test build RES_READ_REMOTE_RSSI and RES_READ_LOCAL_RSSI frames
	 * @throws CoronisException
	 */
	@Test
	public final void testBuildFrame_RES_REMPTE_RSSI() throws CoronisException {
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_READ_REMOTE_RSSI, msg)
					instanceof ResRemoteRSSIFrame);
		assertTrue(CoronisFrameBuilder.buildFrame(CoronisFrame.RES_READ_LOCAL_RSSI, msg)
					instanceof ResRemoteRSSIFrame);
	}

	/**
	 * test build unsupported frame
	 * @throws CoronisException
	 */
	@Test(expected=CoronisException.class)
	public final void testBuildFrame_UNSUPPORTED() throws CoronisException {
		CoronisFrameBuilder.buildFrame(0xFF, msg);
	}
	
	/**
	 * Test method for {@link com.coronis.frames.CoronisFrameBuilder#getACK()}.
	 */
	@Test
	public final void testGetACK() {	
		assertTrue(CoronisFrameBuilder.getACK() instanceof ACKFrame);
	}
}
