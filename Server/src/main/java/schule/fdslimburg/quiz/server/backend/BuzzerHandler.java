package schule.fdslimburg.quiz.server.backend;

import schule.fdslimburg.quiz.server.IControl;
import schule.fdslimburg.quiz.server.comm.Communication;
import schule.fdslimburg.quiz.server.events.ClientDataEventArgs;
import schule.fdslimburg.quiz.server.events.ClientDataEventListener;

import static schule.fdslimburg.quiz.server.Util.millis;

public class BuzzerHandler implements ClientDataEventListener, Runnable, IControl {
	private final Object _lock = new Object();
	public boolean locked = false;
	public boolean pressed = false;
	public long firstPress = 0L;
	public long clientTimestamp = 0L;
	public int clientId;
	
	private boolean running = false;
	private boolean stopThread = false;
	private Communication comm;
	private Thread runner;
	
	public BuzzerHandler(Communication comm) {
		this.comm = comm;
		this.runner = new Thread(this);
	}
	
	@Override
	public void triggerEvent (ClientDataEventArgs ea) {
		synchronized (_lock) {
			if (!locked && firstPress == 0) {
				pressed = true;
				firstPress = millis ();
				clientId = ea.clientId;
				clientTimestamp = ea.data.timestamp;
			} else if (!locked && millis () < (firstPress + 500)) {
				if (ea.data.timestamp < clientTimestamp) {
					// Client was "faster"
					clientTimestamp = ea.data.timestamp;
					clientId = ea.clientId;
				}
			}
		}
	}
	
	@Override
	public void run () {
		running = true;
		
		while(!stopThread) {
			if(firstPress != 0 && millis() > (firstPress + 500)) {
				synchronized (_lock) {
					locked = true;
					// Soft-Reset
					firstPress = 0L;
					pressed = false;
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException (e);
			}
		}
		
		running = false;
	}
	
	// Unlock/Reset
	public void unlock() {
		synchronized (_lock) {
			locked = false;
			firstPress = 0L;
			pressed = false;
			clientId = 0;
			clientTimestamp = 0L;
		}
	}
	
	@Override
	public void startModule () {
		if(running)
			return;
		this.comm.addClientDataEventListener (this);
		this.runner.start ();
	}
	
	@Override
	public void stopModule () {
		stopThread = true;
	}
}
