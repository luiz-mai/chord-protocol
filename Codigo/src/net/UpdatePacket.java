package net;

import java.io.ByteArrayInputStream;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

import misc.Tools;

public class UpdatePacket extends ChordPacket {

	private int originID;
	private int newSucessorID;
	private Inet4Address newSucessorIP;

	public static final int packetSize = 13;

	// Construtor para a criação de um pacote Update recebido
	public UpdatePacket(byte[] buffer, int offset) {

		byte lido[] = new byte[4];

		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		// Le um byte do buffer (codigo)
		bais.read(lido, offset, 1);
		this.code = lido[0];

		// Le 4 bytes do buffer (originID)
		bais.read(lido, offset, 4);
		this.originID = ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (newSucessorID)
		bais.read(lido, offset, 4);
		this.newSucessorID = ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (newSucessorIP)
		bais.read(lido, offset, 4);
		this.newSucessorIP = Tools.intToIp(ByteBuffer.wrap(lido).getInt());

	}

	// Construtor para a criação de um pacote UpdatenResponse a ser enviado
	public UpdatePacket(int originID, int newSucessorID, Inet4Address newSucessorIP) {
		super();
		this.code = ChordPacket.UPDATE_CODE;
		this.originID = originID;
		this.newSucessorID = newSucessorID;
		this.newSucessorIP = newSucessorIP;
	}

	public int getOriginID() {
		return originID;
	}

	public int getNewSucessorID() {
		return newSucessorID;
	}

	public Inet4Address getNewSucessorIP() {
		return newSucessorIP;
	}

	@Override
	public byte[] toByteArray() {
		int size = UpdatePacket.packetSize;

		return ByteBuffer.allocate(size).put(this.code).putInt(this.originID).putInt(newSucessorID)
				.putInt(Tools.ipToInt(this.newSucessorIP)).array();
	}

	public String toString() {
		return String.format("[Update - %s]\n"
				+ "code: %Xh\n"
				+ "originID: %Xh\n"
				+ "newSucessorID: %Xh", getCurrentTime(), this.code, this.originID, this.newSucessorID)
				+ "\nsucessorIP: " + this.newSucessorIP.getHostAddress() 
				+ "\n\n";
	}

}
