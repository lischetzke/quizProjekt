package schule.fdslimburg.quiz.server.events;

import java.util.EventListener;

public interface RemoveClientEventListener extends EventListener {
	public void triggerEvent(RemoveClientEventArgs ea);
}
