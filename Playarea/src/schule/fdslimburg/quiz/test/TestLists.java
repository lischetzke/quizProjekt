package schule.fdslimburg.quiz.test;

import java.util.ArrayList;
import java.util.List;

public class TestLists {
	public static void main (String[] args) {
		List<A> list = new ArrayList<> ();
		List<A> toRemove = new ArrayList<> ();
		
		list.add(new A());
		list.add(new A());
		list.add(new A());
		list.add(new A());
		list.add(new A());
		list.add(new A());
		list.add(new A());
		list.add(new A());
		list.add(new A());
		list.add(new A());
		list.add(new A());
		list.add(new A());
		
		for(A a : list) {
			System.out.println ("A id: " + a.id);
		}
		System.out.println ();
		
		for(A a : list) {
			if(a.id % 3 == 0) {
				toRemove.add (a);
			}
		}
		list.removeAll (toRemove);
		
		for(A a : list) {
			System.out.println ("A id: " + a.id);
		}
	}
	
	static class A {
		private static int _counter = 0;
		public int id = _counter++;
	}
}
