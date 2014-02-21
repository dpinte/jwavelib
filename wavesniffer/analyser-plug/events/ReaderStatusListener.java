package events;

import java.util.EventListener;

public interface ReaderStatusListener extends EventListener {
	public void readerStatus(String message);
}
