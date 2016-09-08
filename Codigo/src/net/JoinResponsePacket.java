package net;

import java.io.ByteArrayInputStream;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

import misc.Tools;

public class JoinResponsePacket extends ChordPacket {

	private byte status; // 0 = falha e 1 = sucesso
	private int sucessorID;
	private Inet4Address sucessorIP;
	private int predecessorID;
	private Inet4Address predecessorIP;

	public static final int packetSize = 18;

	// Construtor para a criação de um pacote JoinResponse recebido
	public JoinResponsePacket(byte[] buffer, int offset) {

		// Buffer inicializao com 4 pois eh o tamanho maximo dos campos a serem
		// lidos
		byte lido[] = new byte[4];

		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		// Le um byte do buffer (codigo)
		bais.read(lido, offset, 1);
		this.code = lido[0];

		// Le 1 byte do buffer (status)
		bais.read(lido, offset, 1);
		this.status = lido[0];

		// Le 4 bytes do buffer (sucessorID)
		bais.read(lido, offset, 4);
		this.sucessorID = ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (sucessorIP)
		bais.read(lido, offset, 4);
		this.sucessorIP = Tools.intToIp(ByteBuffer.wrap(lido).getInt());

		// Le 4 bytes do buffer (predecessorID)
		bais.read(lido, offset, 4);
		this.predecessorID = ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (sucessorID)
		bais.read(lido, offset, 4);
		this.predecessorIP = Tools.intToIp(ByteBuffer.wrap(lido).getInt());

	}

	// Construtor para a criação de um pacote JoinResponse a ser enviado
	public JoinResponsePacket(byte status, int sucessorID, Inet4Address sucessorIP, int predecessorID,
			Inet4Address predecessorIP) {
		super();
		this.code = ChordPacket.JOIN_RESP_CODE;
		this.status = status;
		this.sucessorID = sucessorID;
		this.sucessorIP = sucessorIP;
		this.predecessorID = predecessorID;
		this.predecessorIP = predecessorIP;
	}
	
	public byte getStatus() {
		return status;
	}

	public int getSucessorID() {
		return sucessorID;
	}

	public Inet4Address getSucessorIP() {
		return sucessorIP;
	}

	public int getPredecessorID() {
		return predecessorID;
	}

	public Inet4Address getPredecessorIP() {
		return predecessorIP;
	}

	@Override
	public byte[] toByteArray() {
		
		int size = JoinResponsePacket.packetSize;
		
		return ByteBuffer.allocate(size)
				.put(this.code)
				.put(this.status)
				.putInt(this.sucessorID)
				.putInt(Tools.ipToInt(this.sucessorIP))
				.putInt(this.predecessorID)
				.putInt(Tools.ipToInt(this.predecessorIP))
				.array();
	}

	public String toString() {
		return String.format( "code: %X \nstatus: %X\nsucessorID: %d",this.code,this.status,this.sucessorID) +  
				"\nsucessorIP: " + this.sucessorIP.toString() + 
				String.format( "\npredecessorID: %d",this.predecessorID) +  
				"\npredecessorIP:" + this.predecessorIP.toString() + "\n\n";
	}

}
