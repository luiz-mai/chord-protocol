package net;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class LeaveResponsePacket extends ChordPacket {

	private int originID;
	
	public static final int packetSize = 5;
	
	// Construtor para a criação de um pacote LeaveResponse recebido
	public LeaveResponsePacket (byte[] buffer, int offset){
		byte lido[] = new byte[4];

		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		// Le um byte do buffer (codigo)
		bais.read(lido, offset, 1);
		this.code = lido[0];

		// Le 4 bytes do buffer (originID)
		bais.read(lido, offset, 4);
		this.originID = ByteBuffer.wrap(lido).getInt();
	}
	
	
	// Construtor para a criação de um pacote LeaveResponse a ser enviado
	public LeaveResponsePacket(int originID) {
		super();
		this.code = ChordPacket.LEAVE_RESP_CODE;
		this.originID = originID;
	}


	@Override
	public byte[] toByteArray(){

		int size = LeaveResponsePacket.packetSize;
		
		return ByteBuffer.allocate(size)
				.put(this.code)
				.putInt(this.originID)
				.array();
	}
	
	@Override
	public String toString(){
		return String.format("code: %X \noriginID: %d \n\n",this.code,this.originID);
	}

}
