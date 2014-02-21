package events;

import java.util.EventListener;

public interface FrameContainerListener extends EventListener {
	void frameAdded();
	void framesFiltered(int[] frameInd);
	void framesHighlighted(int[] frameInd);
	void containerCleared();
}
