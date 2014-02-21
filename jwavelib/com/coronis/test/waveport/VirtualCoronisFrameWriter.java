package com.coronis.test.waveport;

import java.io.IOException;
import java.io.OutputStream;

import com.coronis.frames.CoronisFrame;
import com.coronis.frames.CoronisFrameWriter;

public class VirtualCoronisFrameWriter extends CoronisFrameWriter {

	private VirtualWavePort _wpt; 
	
	public VirtualCoronisFrameWriter(OutputStream os) {
		super(os);
	}
	
	public VirtualCoronisFrameWriter(OutputStream os, VirtualWavePort wpt) {
		super(os);	
		_wpt = wpt;		
	}
	
    public synchronized CoronisFrame sendFrame(CoronisFrame wframe) throws IOException {      	
        try {
            _ostream.write(wframe.getByteArray());
            _ostream.flush();              
            _wpt.startListener();
        } catch (IOException e) {
            _logger.error("CoronisFrameWriter :: Error while writing to the serial port");
            throw e;
        }
        _logger.frame(wframe.getMessage(), false);
        return wframe;
    }
	
	
}
