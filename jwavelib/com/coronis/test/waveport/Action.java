package com.coronis.test.waveport;

import com.coronis.frames.CoronisFrame;

public class Action {
	/*
	 * Classe used in the WavePortTest and VirtualWavePort as actions to be executed in response
	 * First, the thread sleeps during waittime, and then it simulates the reception of a CoronisFrame, cfr.
	 */
	public int waittime;
	public CoronisFrame cfr;
	
	public Action(int waittime, CoronisFrame cfr){
		this.waittime = waittime;
		this.cfr = cfr;
	}
}
