package net;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class LookupPacket extends ChordPacket{
		
	private int originID;
	private int originIp;
	private int wantedID;
	
	public LookupPacket (byte[] buffer, int offset){
		
		byte lido[] = new byte[13];
		
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		
		// Le um byte do buffer (codigo) 
		bais.read(lido,offset,1);
		this.code = lido[0];
		
		// Le 4 bytes do buffer (newNodeID)
		bais.read(lido,offset,4);
		this.originID = java.nio.ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (newNodeID)
		bais.read(lido,offset,4);
		this.originIp = java.nio.ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (newNodeID)
		bais.read(lido,offset,4);
		this.wantedID = java.nio.ByteBuffer.wrap(lido).getInt();
	}
	
	public LookupPacket(int originID, int originIp, int wantedID){
		super();
		this.code = ChordPacket.LOOKUP_RESP_CODE;
		this.originID = originID;
		this.originIp = originIp;
		this.wantedID = wantedID;
	}
	
	@Override
	public byte[] toByteArray(){
		return ByteBuffer.allocate(13)
				.put(this.code)
				.putInt(originID)
				.putInt(originIp)
				.putInt(wantedID)
				.array();
	}


	public String toString() {
		return String.format("code: %b \noriginID: %d\noriginIP: %d\nwnatedID: %d", this.code, this.originID, this.originIp, this.wantedID);
	}

	public int getOriginID() {
		return originID;
	}

	public int getOriginIp() {
		return originIp;
	}

	public int getWantedID() {
		return wantedID;
	}
	
}
