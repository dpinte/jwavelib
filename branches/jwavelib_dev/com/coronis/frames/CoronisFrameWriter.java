/*
 * CoronisFrameWriter.java
 *
 * Created on 31 octobre 2007, 17:36
 *
 * 
 * Author : Didrik Pinte <dpinte@itae.be>
 * Copyright : Dipole Consulting SPRL 2008
 * 
 * $Date: 2009-07-24 17:11:53 +0200 (Fri, 24 Jul 2009) $
 * $Revision: 123 $
 * $Author: abertrand $
 * $HeadURL: https://secure2.svnrepository.com/s_dpinte/jwavelib/branches/jwavelib_dev/com/coronis/frames/CoronisFrameWriter.java $
 */
package com.coronis.frames;

import java.io.*;

import com.coronis.logging.Logger;

/**
 * Coronis frame writer manages the writing of CoronisFrame to a given 
 * outputstream.
 * 
 * @author dpinte
 */
public class CoronisFrameWriter {

    protected static OutputStream _ostream;
    private static int[] emptyMsg = {};

    public CoronisFrameWriter(OutputStream os) {
        _ostream = os;     
    }

    public boolean close() {
        try {
            _ostream.close();
            _ostream = null;
        } catch (IOException ex) {
            Logger.error("Error while closing inputstream");
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
            Logger.error("CoronisFrameWriter :: Error while writing to the serial port");
            throw e;
        }
        
        Logger.frame(wframe.getFrameAsString(), false);
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
