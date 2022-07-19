package schule.fdslimburg.quiz.test;

public class testsBinaryString {
	public static void main (String[] args) {
		int i = 0x20;
		System.out.println (pad(i));
	}
	
	private static String pad(int v) {
		String a = Integer.toBinaryString(v);;
		for(int i = a.length (); i < 7; i++) {
			a = "0" + a;
		}
		return a;
	}
}
