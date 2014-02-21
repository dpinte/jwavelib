package reader;

import events.ReaderErrorListener;
import events.ReaderStatusListener;

public interface ReaderInterface {

	
	public ReaderErrorListener[] getReaderErrorListeners();
	public ReaderStatusListener[] getReaderStatusListeners();
	public void addReaderStatusListener(ReaderStatusListener listener);
	public void addReaderErrorListener(ReaderErrorListener listener);
	public void removeReaderStatusListener(ReaderStatusListener listener);
	public void removeReaderErrorListener(ReaderErrorListener listener);
	public void fireReaderStatus(String message);
	public void fireReaderError(String message);
}
