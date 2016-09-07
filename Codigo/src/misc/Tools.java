package misc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Tools {
	
	public static int ipToInt (InetAddress ip){
		return ByteBuffer.wrap(ip.getAddress()).getInt();
	}
	
	public static InetAddress intToIp (int i) throws UnknownHostException {
		return InetAddress.getByName((new Integer(i)).toString());
	}
}
