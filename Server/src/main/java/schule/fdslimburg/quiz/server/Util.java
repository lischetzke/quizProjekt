package schule.fdslimburg.quiz.server;

import java.util.Calendar;

public class Util {
	public static long millis() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}
}
