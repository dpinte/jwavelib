package modules.analyser;

import java.util.ArrayList;
import java.util.HashMap;

import com.coronis.frames.CoronisFrame;

import common.ParameterList;
import frame.FrameContainer;
import frame.SnifferFrameInterface;
import frame.SnifferReceivedFrame;
import frame.SnifferReceivedMultiFrame;
import frame.SnifferReqWriteParameterFrame;

enum State {
	lvl1, ack, lvl2
}

public class FrameAnalyser {
	
	/**
	 * Filter frames
	 * @param container The frame container
	 * @param cmd The frame command. -1 for all
	 * @param dir The frame direction. -1 for all
	 */
	public static void filterFrames(FrameContainer container, int cmd, int dir){
		ArrayList <SnifferFrameInterface> frameList = container.getFrameList();
		ArrayList <Integer> indList = new ArrayList <Integer> ();
		int[] indexes = null;
		
		if(cmd == -1 && dir == -1){
			indexes = new int[1];
			indexes[0] = -1;
		} else {
			int i = 0;
			for(SnifferFrameInterface frame : frameList){
				if(	(frame.getCmd() == cmd || cmd == -1) &&
					(frame.getDirection() == dir || dir == -1)) {
					indList.add(i);
				}
				i++;
			}
			
			i = 0;
			indexes = new int[indList.size()];
			for(Integer value : indList){
				indexes[i++] = value;
			}
		}
		container.fireFrameFiltered(indexes);
	}
	
	/**
	 * Filter frame with all related frames then fire a FrameFiltred event
	 * @param container The frame container
	 * @param frameInd The frame index in container
	 * @param withACK Display ACK
	 */
	public static void filterRelatedFrame(FrameContainer container, int frameInd, boolean withACK){
		int[] frames;
		
		frames = getRelatedFrames(container, frameInd, withACK);
		
		if(frames == null || frames.length == 0)
			return;
		else
			container.fireFrameFiltered(frames);
	}
	
	/**
	 * Highlight related frames
	 * @param container
	 * @param frameInd
	 * @param withACK
	 */
	static public void highlightRelatedFrames(FrameContainer container, int frameInd, boolean withACK){
		int[] frames = getRelatedFrames(container, frameInd, withACK);
		
		if(frames == null || frames.length == 0){
			return;
		} else {
			container.fireFramesHighlighted(frames);
		}
	}
		
	/**
	 * Scan container and search all frames.
	 * Works only if the given frame is a request
	 * @param container The frame container
	 * @param frameInd The index of the frame in the container
	 * @param withACK Take the related ACK
	 * @return array with related frames indexes
	 */
	public static int[] getRelatedFrames(FrameContainer container, int frameInd, boolean withACK){
		ArrayList <SnifferFrameInterface> frameList = container.getFrameList();
		ArrayList <Integer> list = new ArrayList <Integer> ();
		ArrayList <Integer> relCmd = null;
		State state;
		boolean end = false;
		int lvl = 1;
		int cmd, rootCmd;
		int multiNbr = 0;
		
		list.add(Integer.valueOf(frameInd));
		rootCmd = frameList.get(frameInd).getCmd();
		
		if(!frame.FrameAnalyser.relation.containsKey(rootCmd)){
			return new int[0];
		}
		
		relCmd = frame.FrameAnalyser.relation.get(Integer.valueOf(rootCmd)).getRelation(0, lvl);
		if(relCmd.size() == 0){
			return new int[0];
		}
		
		if(withACK)
			state = State.ack;
		else
			state = State.lvl1;
		
		for(int i = frameInd; i < frameList.size() && !end; i++){
			cmd = frameList.get(i).getCmd();
			switch(state){
				case lvl1:
					if(relCmd.contains(Integer.valueOf(cmd))){
						list.add(Integer.valueOf(i));
						
						lvl = 2;
						relCmd = frame.FrameAnalyser.relation.get(Integer.valueOf(rootCmd)).getRelation(0, lvl);
						
						if(relCmd.size() == 0 && withACK){
							state = State.ack;
							lvl = 3;
						} else if (withACK)
							state = State.ack;
						else 
							state = State.lvl2;
					}
					break;
					
				case ack:
					if(cmd == CoronisFrame.CMD_ACK){
						list.add(Integer.valueOf(i));
						
						if(lvl == 2)
							state = State.lvl2;
						else if (lvl == 1)
							state = State.lvl1;
						else 
							end = true;
					}
					break;
					
				case lvl2:
					if(relCmd.contains(Integer.valueOf(cmd))){
						list.add(Integer.valueOf(i));
						
						if(cmd == CoronisFrame.RECEIVED_MULTIFRAME){
							multiNbr = ((SnifferReceivedMultiFrame)frameList.get(i)).getFrameIndex();
						}
						
						if(withACK){
							state = State.ack;
							if(multiNbr <= 1)
								lvl = 3;
						} else {
							if(multiNbr <= 1)
								end = true;
						}
					}
					break;
			}
		}
		
		int[] res = new int[list.size()];
		int i = 0;
		for(Integer e : list){
			res[i++] = e.intValue();
		}
		
		return res;
	}

	/**
	 * Search all modules ID
	 * @param container The frame Container
	 * @return A ArrayList with all modules ID
	 */
	public static ArrayList <String> getAllModulesId(FrameContainer container){
		ArrayList <String> list = new ArrayList <String> ();
		int cmd;
		
		for(SnifferFrameInterface frame : container.getFrameList()){
			cmd = frame.getCmd();
			
			switch(cmd){
				case CoronisFrame.RES_SEND_SERVICE:
				case CoronisFrame.SERVICE_RESPONSE:
				case CoronisFrame.RECEIVED_FRAME:
					if(!list.contains(((SnifferReceivedFrame)frame).getModuleId()))
						list.add(((SnifferReceivedFrame)frame).getModuleId());
					break;
				
				case CoronisFrame.RECEIVED_MULTIFRAME:
					if(!list.contains(((SnifferReceivedMultiFrame)frame).getModuleId()))
						list.add(((SnifferReceivedMultiFrame)frame).getModuleId());
					break;
			}			
		}
		return list;
	}
	
	/**
	 * Scan all the container to find radio parametres
	 * @param container frame container
	 * @return HashMap with the parameters
	 */
	public static HashMap <Integer, Integer[]> scanRadioParameters(FrameContainer container){
		HashMap <Integer, Integer[]> map = new HashMap <Integer, Integer[]> ();
		int cmd;
		
		
		for(SnifferFrameInterface frame : container.getFrameList()){
			cmd = frame.getCmd();
			switch(cmd){
				case CoronisFrame.REQ_WRIT_PARAM:
					
					int[] paramData = ((SnifferReqWriteParameterFrame)frame).getParameterData();
					int paramNum = ((SnifferReqWriteParameterFrame)frame).getParameterNumber();
					
					Integer[] tmp = new Integer[paramData.length];
					for(int i = 0; i < tmp.length; i++){
						tmp[i] = paramData[i];
					}
					
					map.put(paramNum, tmp);
					break;
					
				default:
					break;
			}
		}
		return map;
	}
	
	/**
	 * Search the relay route of a frame
	 * @param container The frame container
	 * @param frameIndex The frame index in the container
	 * @return Array with modulesID of the repeaters
	 */
	public static String[] getFrameRelayRoute(FrameContainer container, int frameIndex){
		ArrayList <SnifferFrameInterface> frameList= container.getFrameList();
		SnifferFrameInterface frame;
		String[] route = null;
		boolean end = false;
		boolean check = false;
		
		int i = frameIndex;
		while(!end && i > 0){
			frame = frameList.get(i);
			
			switch(frame.getCmd()){
				case CoronisFrame.REQ_WRIT_PARAM:
				case CoronisFrame.REQ_READ_PARAM:
					int paramNum = ((SnifferReqWriteParameterFrame)frame).getParameterNumber();
					
					if( paramNum == ParameterList.RELAY_ROUTE){
						int[] paramVal = ((SnifferReqWriteParameterFrame)frame).getParameterData();
						
						String tmp = ParameterList.parseParameterValue(paramNum, paramVal);
						route = tmp.split(",");
						
						end = true;
					}
					break;
					
				case CoronisFrame.REQ_SEND_FRAME:
					if(check)
						end = true;
					else
						check = true;
					break;
					
				default:
					break;
			}
			i--;
		}
				
		return route;
	}
	
	/**
	 * Count all frames by Command
	 * @param container The frameCOntainer
	 * @return A HashMap with Each frameType with the number of occurrence
	 */
	public static HashMap <Integer, Integer> countFrameByType(FrameContainer container){
		HashMap <Integer, Integer> count = new HashMap <Integer, Integer> ();
		ArrayList <SnifferFrameInterface> list = container.getFrameList();
		int cmd;
		
		for(SnifferFrameInterface frame : list){
			cmd = frame.getCmd();
			
			if(count.containsKey(cmd)){
				int nbr = count.get(cmd);
				nbr++;
				count.put(cmd, nbr);
			} else {
				count.put(cmd, 1);
			}
		}
		
		return count;
	}
}
