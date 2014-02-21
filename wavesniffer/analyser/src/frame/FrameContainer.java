/**
 * @author Bertrand antoine
 *
 */
package frame;

import java.util.ArrayList;
import javax.swing.event.EventListenerList;


import events.*;

public class FrameContainer {
	private final EventListenerList listeners = new EventListenerList();
	private ArrayList<SnifferFrameInterface> frameList;
	private boolean saved = true;	//no need to save an empty container

	/**
	 * 
	 */
	public FrameContainer() {
		this.frameList = new ArrayList<SnifferFrameInterface>();
	}

	/**
	 * 
	 * @param defaultLenght
	 */
	public FrameContainer(int defaultLenght) {
		this.frameList = new ArrayList<SnifferFrameInterface>(defaultLenght);
	}

	/**
	 * 
	 * @param newFrame
	 */
	public synchronized void addFrame(SnifferFrameInterface newFrame) {
		if(newFrame != null){
			this.frameList.add(newFrame);
			this.setSaved(false);
			this.fireFrameContainerAdded();
		}
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public SnifferFrameInterface getFrameAt(int index) {
		return this.frameList.get(index);
	}
	
	/**
	 * 
	 * @return
	 */
	public SnifferFrameInterface getLastFrame(){
		if(this.frameList.size() > 0)
			return this.getFrameAt(this.frameList.size() -1);
		else
			return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTotalFrames(){
		return this.frameList.size();
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<SnifferFrameInterface> getFrameList(){
		return this.frameList;
	}
	
	public void resetContainer(){
		this.frameList.clear();
		this.fireContainerCleared();
	}

	public void setSaved(boolean modified) {
		this.saved = modified;
	}

	public boolean isSaved() {
		return this.saved;
	}

	/**
	 * 
	 * @param listener
	 */
	public void addFrameContainerListener(FrameContainerListener listener){
		this.listeners.add(FrameContainerListener.class, listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeFrameContainerListener(FrameContainerListener listener){
		this.listeners.remove(FrameContainerListener.class, listener);
	}
	
	/**
	 * 
	 * @return
	 */
	public FrameContainerListener[] getFrameContainerListeners(){
		return this.listeners.getListeners(FrameContainerListener.class);
		
	}
	
	/**
	 * 
	 */
	protected void fireFrameContainerAdded(){
		for(FrameContainerListener listener : this.getFrameContainerListeners()){
			listener.frameAdded();
		}
	}
	
	public void fireFrameFiltered(int[] frameInd){
		for(FrameContainerListener listener : this.getFrameContainerListeners()){
			listener.framesFiltered(frameInd);
		}
	}
	
	private void fireContainerCleared() {
		for(FrameContainerListener listener : this.getFrameContainerListeners()){
			listener.containerCleared();
		}
	}
	
	public void fireFramesHighlighted(int[] frameInd){
		for(FrameContainerListener listener : this.getFrameContainerListeners()){
			listener.framesHighlighted(frameInd);
		}
	}
}
