package reader;

//import javax.swing.event.EventListenerList;

import events.ReaderErrorListener;
import events.ReaderStatusListener;

public interface ReaderInterface {
	/*private final EventListenerList statuslisteners = new EventListenerList();
	private final EventListenerList srrorlisteners = new EventListenerList();*/
	
	public ReaderErrorListener[] getReaderErrorListeners();
	public ReaderStatusListener[] getReaderStatusListeners();
	public void addReaderStatusListener(ReaderStatusListener listener);
	public void addReaderErrorListener(ReaderErrorListener listener);
	public void removeReaderStatusListener(ReaderStatusListener listener);
	public void removeReaderErrorListener(ReaderErrorListener listener);
	public void fireReaderStatus(String message);
	public void fireReaderError(String message);
}
