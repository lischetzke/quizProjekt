package schule.fdslimburg.quiz.test;

public enum NetStatus {
	PING(0x01),
	PRESS(0x02),
	LON(0x04),
	LOFF(0x08),
	ACORRECT(0x10),
	AWRONG(0x20);
	
	public final int value;
	NetStatus(final int i) {
		this.value = i;
	}
	
	public static NetStatus getValue(int value) {
		for(NetStatus e: NetStatus.values()) {
			if(e.value == value) {
				return e;
			}
		}
		return null;
	}
}
