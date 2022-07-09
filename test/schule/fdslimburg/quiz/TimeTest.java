package schule.fdslimburg.quiz;

import static org.junit.Assert.*;
import org.junit.*;
import schule.fdslimburg.quiz.server.IControl;

import java.util.ArrayList;
import java.util.List;

public class TimeTest implements IControl {
	@Test
	public void testExec() throws InterruptedException {
		List<Thread> tests = new ArrayList<> ();
		tests.add(new Thread(new TestA(10000, 1)));
		//tests.add(new Thread(new TestA(10000, 5)));
		tests.add(new Thread(new TestA(10000, 10)));
		//tests.add(new Thread(new TestA(10000, 15)));
		tests.add(new Thread(new TestA(10000, 25)));
		//tests.add(new Thread(new TestA(10000, 50)));
		
		//tests.add(new Thread(new TestB(10000, 1)));
		////tests.add(new Thread(new TestB(10000, 5)));
		//tests.add(new Thread(new TestB(10000, 10)));
		////tests.add(new Thread(new TestB(10000, 15)));
		//tests.add(new Thread(new TestB(10000, 25)));
		////tests.add(new Thread(new TestB(10000, 50)));
		
		Thread.sleep (100);
		
		for (Thread test : tests) {
			test.start ();
		}
		
		boolean running = true;
		while(running) {
			running = false;
			for (Thread test : tests) {
				if(test.isAlive ()) {
					running = true;
					break;
				}
			}
			Thread.sleep(1000);
		}
		
		assertTrue (true);
	}
	
	class TestA implements Runnable {
		private int runs = 1;
		private long millis = 1;
		
		public TestA(int runs, long millis) {
			this.runs = runs;
			this.millis = millis;
		}
		
		@Override
		public void run () {
			try {
				long total = 0;
				long min = Long.MAX_VALUE;
				long max = Long.MIN_VALUE;
				for(int i = 0; i < runs; i++) {
					long start = System.nanoTime ();
					Thread.sleep(millis);
					long stop = System.nanoTime ();
					long time = stop - start;
					total += time;
					min = Math.min(min, time);
					max = Math.max(max, time);
				}
				long avg = Math.round(1.0 * total / runs);
				
				System.out.println ("TestA\t" + runs + "\t" + millis + "\t-\t" + avg + "\t" + min + "\t" + max + "\t" + (max-min));
			} catch (InterruptedException e) {
				throw new RuntimeException (e);
			}
		}
	}
	
	class TestB implements Runnable, IControl {
		private int runs = 1;
		private long millis = 1;
		
		public TestB(int runs, long millis) {
			this.runs = runs;
			this.millis = millis;
		}
		
		@Override
		public void run () {
			long total = 0;
			long min = Long.MAX_VALUE;
			long max = Long.MIN_VALUE;
			for(int i = 0; i < runs; i++) {
				long start = System.nanoTime ();
				sleepThread(millis);
				long stop = System.nanoTime ();
				long time = stop - start;
				total += time;
				min = Math.min(min, time);
				max = Math.max(max, time);
			}
			long avg = Math.round(1.0 * total / runs);
			
			System.out.println ("TestB\t" + runs + "\t" + millis + "\t-\t" + avg + "\t" + min + "\t" + max + "\t" + (max-min));
		}
		
		@Override
		public void startModule () {
		
		}
		
		@Override
		public void stopModule () {
		
		}
	}
	
	@Override
	public void startModule () {
	
	}
	
	@Override
	public void stopModule () {
	
	}
}
