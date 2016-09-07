package net;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class JoinPacket extends ChordPacket {

	private int newNodeID;

	public static final int packetSize = 5;

	// Construtor para a criação de um pacote Join recebido
	public JoinPacket(byte[] buffer, int offset) {

		byte lido[] = new byte[4];

		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		// Le um byte do buffer (codigo)
		bais.read(lido, offset, 1);
		this.code = lido[0];

		// Le 4 bytes do buffer (newNodeID)
		bais.read(lido, offset, 4);
		this.newNodeID = ByteBuffer.wrap(lido).getInt();
	}
	
	
	// Construtor para a criação de um pacote Join para envio
	public JoinPacket(int newNodeID) {
		super();
		this.code = ChordPacket.JOIN_CODE;
		this.newNodeID = newNodeID;
	}

	@Override
	public byte[] toByteArray() {
		return ByteBuffer.allocate(5)
				.put(this.code)
				.putInt(newNodeID)
				.array();
	}

	public String toString() {
		return String.format("code: %x \nnewNodeID: %x\n\n", this.code, this.newNodeID);
	}

}
