package events;

import java.util.EventListener;

public interface ReaderErrorListener extends EventListener {
	public void readerError(String message);
}
