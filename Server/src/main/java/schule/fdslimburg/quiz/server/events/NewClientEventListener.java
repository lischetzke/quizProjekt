package schule.fdslimburg.quiz.server.events;

import java.util.EventListener;

public interface NewClientEventListener extends EventListener {
	public void triggerEvent(NewClientEventArgs ea);
}
