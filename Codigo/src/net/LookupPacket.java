package net;

import java.io.ByteArrayInputStream;

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
	
	@Override
	public byte[] toByteArray(){
		
	}


	public String toString(){
		StringBuilder sb = new StringBuilder().append(String.format("%02x",this.code)).append(String.format("%08x",this.originID)).append(String.format("%08x",this.originIp)).append(String.format("%08x",this.wantedID));
		return sb.toString();
	}
	
}
