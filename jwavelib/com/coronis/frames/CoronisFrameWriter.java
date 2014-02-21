/*
 * CoronisFrameWriter.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-06-09 23:50:13 +0200 (Tue, 09 Jun 2009) $
 * $Revision: 84 $
 * $Author: dpinte $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/jwavelib/com/coronis/frames/CoronisFrameWriter.java $
 */
package com.coronis.frames;

import java.io.*;
import com.coronis.Config;
import com.coronis.logging.SimpleLogger;

/**
 * Coronis frame writer manages the writing of CoronisFrame to a given 
 * outputstream.
 * 
 * @author dpinte
 */
public class CoronisFrameWriter {

    protected static OutputStream _ostream;
    private static int[] emptyMsg = {};
    protected SimpleLogger _logger;

    public CoronisFrameWriter(OutputStream os) {
        _ostream = os;
        try {
            _logger = Config.getLogger();
        } catch (Exception e ) {
        	// FIXME : this is just ugly
        	// No logger should not crash the system
            System.err.println(e.toString());
            System.exit(1);
        }        
    }

    public boolean close() {
        try {
            _ostream.close();
            _ostream = null;
        } catch (IOException ex) {
            _logger.error("Error while closing inputstream");
            return false;
        }
        return true;
    }

    // this method must be synchronized !
    public synchronized CoronisFrame sendFrame(CoronisFrame wframe) throws IOException {    
        try {
            _ostream.write(wframe.getByteArray());
            _ostream.flush();           
        } catch (IOException e) {
            _logger.error("CoronisFrameWriter :: Error while writing to the serial port");
            throw e;
        }
        _logger.frame(wframe.getMessage(), false);
        return wframe;
    }

    public CoronisFrame sendFrame(int cmd, int[] message) throws IOException {
        CoronisFrame myframe = new CoronisFrame(cmd, message);
        return sendFrame(myframe);
    }

    public CoronisFrame sendACK() throws IOException {
        CoronisFrame myframe = new CoronisFrame(CoronisFrame.ACK, emptyMsg);
        return sendFrame(myframe);
    }

    public CoronisFrame sendNAK() throws IOException {
        CoronisFrame myframe = new CoronisFrame(CoronisFrame.NAK, emptyMsg);
        return sendFrame(myframe);
    }

    public CoronisFrame sendERROR() throws IOException {
        CoronisFrame myframe = new CoronisFrame(CoronisFrame.ERROR, emptyMsg);
        return sendFrame(myframe);
    }
}
