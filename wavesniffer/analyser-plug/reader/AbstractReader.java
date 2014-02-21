package reader;

import javax.swing.event.EventListenerList;

import events.ReaderErrorListener;
import events.ReaderStatusListener;
import frame.FrameContainer;

public abstract class AbstractReader implements ReaderInterface, Runnable{
	protected FrameContainer container;
	private final EventListenerList statuslisteners = new EventListenerList();
	private final EventListenerList errorlisteners = new EventListenerList();
	
	public AbstractReader(){}
	
	public void addReaderErrorListener(ReaderErrorListener listener) {
		this.errorlisteners.add(ReaderErrorListener.class, listener);
	}

	public void addReaderStatusListener(ReaderStatusListener listener) {
		this.statuslisteners.add(ReaderStatusListener.class, listener);
	}

	public void fireReaderError(String message) {
		for(ReaderErrorListener listener : this.getReaderErrorListeners()){
			listener.readerError(message);
		}
	}

	public void fireReaderStatus(String message) {
		for(ReaderStatusListener listener: this.getReaderStatusListeners()){
			listener.readerStatus(message);
		}
	}

	public ReaderErrorListener[] getReaderErrorListeners(){
		return this.errorlisteners.getListeners(ReaderErrorListener.class);
	}
	
	public ReaderStatusListener[] getReaderStatusListeners(){
		return this.statuslisteners.getListeners(ReaderStatusListener.class);
	}
	
	public void removeReaderErrorListener(ReaderErrorListener listener) {
		this.errorlisteners.remove(ReaderErrorListener.class, listener);
	}

	public void removeReaderStatusListener(ReaderStatusListener listener) {
		this.statuslisteners.remove(ReaderStatusListener.class, listener);
	}
}
