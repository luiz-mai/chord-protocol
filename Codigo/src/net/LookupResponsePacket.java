package net;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class LookupResponsePacket extends ChordPacket {

	private int wantedID;
	private int sucessorID;
	private int sucessorIp;
	
	public LookupResponsePacket (byte[] buffer, int offset){
		
		byte lido[] = new byte[13];
		
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		
		// Le um byte do buffer (codigo) 
		bais.read(lido,offset,1);
		this.code = lido[0];
		
		// Le 4 bytes do buffer (wantedID)
		bais.read(lido,offset,4);
		this.wantedID = java.nio.ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (sucessorID)
		bais.read(lido,offset,4);
		this.sucessorID = java.nio.ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (sucessorIp)
		bais.read(lido,offset,4);
		this.sucessorIp = java.nio.ByteBuffer.wrap(lido).getInt();
	}
	
	public LookupResponsePacket(int wantedID, int sucessorID, int sucessorIp){
		super();
		this.code = ChordPacket.LOOKUP_RESP_CODE;
		this.wantedID = wantedID;
		this.sucessorID = sucessorID;
		this.sucessorIp = sucessorIp;
	}
	

	@Override
	public byte[] toByteArray() {
		return ByteBuffer.allocate(13)
				.put(this.code)
				.putInt(wantedID)
				.putInt(sucessorID)
				.putInt(sucessorIp)
				.array();
	}
	
	@Override
	void handle() {
		// TODO Auto-generated method stub

	}

}
