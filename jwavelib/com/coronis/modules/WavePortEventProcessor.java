/*
 * WavePortEventProcessor.java
 *
 * Created on 4 juillet 2008, 16:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.coronis.modules;

import com.coronis.frames.CoronisFrame;

/**
 *
 * @author dpinte
 */
public interface WavePortEventProcessor {    
    public void event(CoronisFrame crf);
    public boolean isTimeOut();
}
