package misc;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Tools {

	// Converte um endereço IP para um inteiro
	public static int ipToInt(InetAddress ip) {
		return ByteBuffer.wrap(ip.getAddress()).getInt();
	}

	/* Essa funcao converte um inteiro em um endereco IP. Caso esse inteiro nao
	 * represente um IP válido, o valor null será retornado
	 */
	public static Inet4Address intToIp(int i) {
		try {
			return (Inet4Address) Inet4Address.getByName((new Integer(i)).toString());
		} catch (UnknownHostException e) {
			return null;
		}
	}
}
