package com.dipole.jwavetool.frame;

import java.util.ArrayList;

public class FrameRelation {
	private int cmd;
	private ArrayList <Integer> level1 = new ArrayList <Integer> ();
	private ArrayList <Integer> level2 = new ArrayList <Integer> ();
	
	public FrameRelation(){}
	
	public FrameRelation(final int cmd) {
		this.cmd = cmd;
	}

	public void addRelation(final int cmd, final int parent, final int level) {
		if(level == 1) {
			this.level1.add(cmd);
		} else if(level == 2) {
			this.level2.add(cmd);
		}
	}
	
	public ArrayList <Integer> getRelation(final int parent, final int level) {
		if(level == 1) {
			return this.level1;
		} else if(level == 2) {
			return this.level2;
		} else {
			return null;
		}
	}
	
	public void setCmd(final int cmd) {
		this.cmd = cmd;
	}
	
	public int getCmd() {
		return this.cmd;
	}
}
