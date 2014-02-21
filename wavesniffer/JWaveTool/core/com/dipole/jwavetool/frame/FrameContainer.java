/**
 * @author Bertrand antoine
 *
 */
package com.dipole.jwavetool.frame;

import java.util.ArrayList;
import javax.swing.event.EventListenerList;

import com.dipole.jwavetool.events.FrameContainerListener;

public class FrameContainer {
	private static FrameContainer instance;
	
	private final EventListenerList listeners = new EventListenerList();
	private ArrayList<SnifferFrameInterface> frameList;
	private boolean saved = true;	//no need to save an empty container

	/**
	 * Create the container with the default capacity
	 */
	private FrameContainer() {
		this.frameList = new ArrayList<SnifferFrameInterface>();
	}
	
	/**
	 * Get the instance of the container
	 * @return An instance of the container
	 */
	public static synchronized FrameContainer getInstance(){
		if(instance == null){
			instance = new FrameContainer();
		}
		
		return instance;
	}
	
	/**
	 * Create the container with an initial capacity
	 * @param defaultLenght The initial capacity
	 */
	public FrameContainer(final int defaultLenght) {
		this.frameList = new ArrayList<SnifferFrameInterface>(defaultLenght);
	}

	/**
	 * Add a frame in the container
	 * @param newFrame
	 */
	public synchronized void addFrame(final SnifferFrameInterface newFrame) {
		if(newFrame != null){
			this.frameList.add(newFrame);
			this.setSaved(false);
			this.fireFrameContainerAdded();
		}
	}
	
	/**
	 * Get a frame at the selected position in the container
	 * @param index	The index in the container
	 * @return The selected frame
	 */
	public SnifferFrameInterface getFrameAt(final int index) {
		return this.frameList.get(index);
	}
	
	/**
	 * Get the latest frame in the container
	 * @return The latest frame
	 */
	public SnifferFrameInterface getLastFrame(){
		SnifferFrameInterface frame = null;
		
		if(this.frameList.size() > 0) {
			frame =  this.getFrameAt(this.frameList.size() -1);
		}
		
		return frame;
	}
	
	/**
	 * Get the number of frames in the container
	 * @return The number of frames
	 */
	public int getTotalFrames(){
		return this.frameList.size();
	}
	
	/**
	 * Get all frame from the container
	 * @return An ArrayList of all frames in the container
	 */
	public ArrayList<SnifferFrameInterface> getFrameList(){
		return this.frameList;
	}
	
	/**
	 * Clear the container
	 */
	public void resetContainer(){
		this.frameList.clear();
		this.fireContainerCleared();
	}

	/**
	 * Set the saved flag
	 * @param modified
	 */
	public void setSaved(final boolean modified) {
		this.saved = modified;
	}

	/**
	 * Check if the container has been saved/modified
	 * @return true if container has been saved.
	 */
	public boolean isSaved() {
		return this.saved;
	}

	/**
	 * Add a frame listener
	 * @param listener
	 */
	public void addFrameContainerListener(final FrameContainerListener listener){
		this.listeners.add(FrameContainerListener.class, listener);
	}
	
	/**
	 * Remove the frame listener
	 * @param listener
	 */
	public void removeFrameContainerListener(final FrameContainerListener listener){
		this.listeners.remove(FrameContainerListener.class, listener);
	}
	
	/**
	 * Get all listeners
	 * @return An Array of all listeners
	 */
	public FrameContainerListener[] getFrameContainerListeners(){
		return this.listeners.getListeners(FrameContainerListener.class);
		
	}
	
	/**
	 * Throw an event when a frame has been added to the container
	 */
	protected void fireFrameContainerAdded(){
		for(FrameContainerListener listener : this.getFrameContainerListeners()){
			listener.frameAdded();
		}
	}
	
	/**
	 * Throw an event when frames have been filtered
	 * @param frameInd indexes of all frames to display
	 */
	public void fireFrameFiltered(final int[] frameInd){
		for(FrameContainerListener listener : this.getFrameContainerListeners()){
			listener.framesFiltered(frameInd);
		}
	}
	
	/**
	 * Throw an event when the container has been cleared
	 */
	private void fireContainerCleared() {
		for(FrameContainerListener listener : this.getFrameContainerListeners()){
			listener.containerCleared();
		}
	}
	
	/**
	 * throw an event when frames need to be Highlighted
	 * @param frameInd indexes off all frame to be Highlighted
	 */
	public void fireFramesHighlighted(final int[] frameInd){
		for(FrameContainerListener listener : this.getFrameContainerListeners()){
			listener.framesHighlighted(frameInd);
		}
	}
}
