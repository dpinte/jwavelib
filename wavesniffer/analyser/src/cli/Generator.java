package cli;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;

public class Generator {

	public Generator(String portName) {
		CommPortIdentifier portID;
		CommPort commPort = null;
		SerialPort serialPort;
		OutputStream streamOut;
		byte[] sync = { (byte)0xFF, (byte)0xFF, (byte)0xFF};
		byte[] buff = {	(byte)0x4D, (byte)0xFF, (byte)0x01, (byte)0x4D, (byte)0x02, (byte)0x01,
						(byte)0x4D, (byte)0x0B, (byte)0x01, (byte)0x4D, (byte)0x20, (byte)0x01,
						(byte)0x4D, (byte)0x43, (byte)0x01, (byte)0x4D, (byte)0x06, (byte)0x01,
						(byte)0x4D, (byte)0x01, (byte)0x01, (byte)0x4D, (byte)0x00, (byte)0x01,
						(byte)0x4D, (byte)0x00, (byte)0x01, (byte)0x4D, (byte)0x02, (byte)0x01,
						(byte)0x4D, (byte)0x01, (byte)0x01, (byte)0x4D, (byte)0xD2, (byte)0x01,
						(byte)0x4D, (byte)0x41, (byte)0x01, (byte)0x4D, (byte)0x03, (byte)0x01 };
		
		try{
			System.out.println("    attempting to open " + portName);
			
			portID = CommPortIdentifier.getPortIdentifier(portName);
		    commPort = portID.open("WaveSniffer Analyser", 1000);
		    
		    serialPort = (SerialPort) commPort;
		    serialPort.setSerialPortParams(	115200,
		    								SerialPort.DATABITS_8,
		    								SerialPort.STOPBITS_1,
		    								SerialPort.PARITY_NONE);
		    streamOut = serialPort.getOutputStream();
			
			System.out.println("    serial port open, now generate frame\n\n");
			streamOut.write(sync);
			
			int i = 0, j = 0;
			while(i < 5){
				for(j = 0; j < buff.length; j++){
					System.out.print(" "+ buff[j]);
					streamOut.write(buff[j]);
				}
				
				System.out.println("\nframe "+ i +" sent");
				i++;
				streamOut.flush();
				Thread.sleep(1000);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (PortInUseException e) {
			System.err.println(e.getMessage());
		} catch (NoSuchPortException e){
			System.err.println(e.getMessage());
		} catch (UnsupportedCommOperationException e){
			System.err.println(e.getMessage());
		} catch (InterruptedException e){
			return;
		}
		finally{
			if(commPort != null)
				commPort.close();
		}
	}

}
