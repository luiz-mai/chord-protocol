package misc;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Tools {

	// Converte um endere�o IP para um inteiro
	public static int ipToInt(InetAddress ip) {
		return ByteBuffer.wrap(ip.getAddress()).getInt();
	}

	/* Essa funcao converte um inteiro em um endereco IP. Caso esse inteiro nao
	 * represente um IP v�lido, o valor null ser� retornado
	 */
	public static Inet4Address intToIp(int i) {
		
		try {
			
			byte[] intEmBytes = ByteBuffer.allocate(4).putInt(i).array();
			
			return (Inet4Address) Inet4Address.getByAddress(intEmBytes);
		} catch (UnknownHostException e) {
			return null;
		}
	}
}
