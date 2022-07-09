package schule.fdslimburg.quiz.server.events;

import java.util.EventListener;

public interface ClientDataEventListener extends EventListener {
	public void triggerEvent(ClientDataEventArgs ea);
}
