package schule.fdslimburg.quiz.server;

import java.util.Calendar;

public interface IControl {
	public void startModule();
	public void stopModule();
	
	@Deprecated // Very resource heavy but more accurate
	public default void sleepThread (long millis) {
		long startTime = System.nanoTime ();
		long nanos = millis * 1000 * 1000;
		while(System.nanoTime () - startTime < nanos) {
			Thread.yield ();
		}
	}
}
