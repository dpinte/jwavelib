package com.coronis.test.virtualComp;

import java.io.IOException;
import java.io.OutputStream;

import com.coronis.frames.CoronisFrame;
import com.coronis.frames.CoronisFrameWriter;
import com.coronis.logging.Logger;

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
            Logger.error("CoronisFrameWriter :: Error while writing to the serial port");
            throw e;
        }
        Logger.frame(wframe.getFrameAsString(), false);
        return wframe;
    }
	
	
}
