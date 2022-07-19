package schule.fdslimburg.quiz.server.comm;

public class NetData {
	public long timestamp;
	public NetStatus status;
	
	public static NetData parse(String data) {
		String[] arrData = data.split(";");
		System.out.println (data);
		if(arrData.length < 2)
			return null;
		NetData n = new NetData();
		n.timestamp = Long.parseLong (arrData[0]);
		n.status = NetStatus.getValue (Integer.parseInt(arrData[1]));
		return n;
	}
}
