package net;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class UpdateResponsePacket extends ChordPacket {

	private byte status;
	private int originID;
	
	public static final int packetSize = 6;
	
	public UpdateResponsePacket (byte[] buffer, int offset){
		
		byte lido[] = new byte[4];

		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		// Le um byte do buffer (codigo)
		bais.read(lido, offset, 1);
		this.code = lido[0];
		
		// Le um byte do buffer (status)
		bais.read(lido, offset, 1);
		this.status = lido[0];

		// Le 4 bytes do buffer (originID)
		bais.read(lido, offset, 4);
		this.originID = ByteBuffer.wrap(lido).getInt();
	}
	
	public UpdateResponsePacket(byte status, int originID) {
		super();
		this.code = ChordPacket.UPDATE_RESP_CODE;
		this.status = status;
		this.originID = originID;
	}

	public byte getStatus() {
		return status;
	}

	public int getOriginID() {
		return originID;
	}

	@Override
	public byte[] toByteArray(){
		
		int size = UpdateResponsePacket.packetSize;
		
		return ByteBuffer.allocate(size)
				.put(this.code)
				.put(this.status)
				.putInt(this.originID)
				.array();
	}
	
	@Override
	public String toString(){
		return String.format("[UpdateResponse - %s]\n"
				+ "code: %Xh\n"
				+ "status: %Xh\n"
				+ "originID: %Xh",getCurrentTime(), this.code,this.status,this.originID)
				+ "\n\n";
	}


}
