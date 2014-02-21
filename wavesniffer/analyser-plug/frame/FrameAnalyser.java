package frame;

import java.util.HashMap;

public class FrameAnalyser {

	public static HashMap <Integer, FrameRelation> relation;
	
	/**
	 * Check timeStamp
	 * @param ts timeStamp of a frame
	 * @return true is time stamp are OK (< 10ms)
	 */
	public static boolean checkTimestamp(int[] ts) {
		if(ts == null || ts.length == 0){
			return false;
		}
		
		/* don't check first byte of frame */
		for (int i = 1; i < ts.length; i++) {
			if (ts[i] > 10) {
				return false;
			}
		}
		return true;
	}
}
