package frame;

import java.util.ArrayList;

public class FrameRelation {
	private int cmd;
	private ArrayList <Integer> level1 = new ArrayList <Integer> ();
	private ArrayList <Integer> level2 = new ArrayList <Integer> ();
	
	public FrameRelation(){}
	
	public FrameRelation(int cmd) {
		this.cmd = cmd;
	}

	public void addRelation(int cmd, int parent, int level){
		if(level == 1){
			this.level1.add(cmd);
		} else if(level == 2){
			this.level2.add(cmd);
		}
	}
	
	public ArrayList <Integer> getRelation(int parent, int level){
		if(level == 1){
			return this.level1;
		} else if(level == 2){
			return this.level2;
		} else {
			return null;
		}
	}
	
	public void setCmd(int cmd){
		this.cmd = cmd;
	}
	
	public int getCmd(){
		return this.cmd;
	}
}
