package net;

import java.io.ByteArrayInputStream;

public class JoinPacket extends ChordPacket{
		
	private int newNodeID;
	
	public JoinPacket (byte[] buffer, int offset){
		
		byte lido[] = new byte[4];
		
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		
		// Le um byte do buffer (codigo) 
		bais.read(lido,offset,1);
		this.code = lido[0];
		
		// Le 4 bytes do buffer (newNodeID)
		bais.read(lido,offset,1);
		this.newNodeID = java.nio.ByteBuffer.wrap(lido).getInt();
	}
	
	void handle(){

	}

	public String toString(){
		StringBuilder sb = new StringBuilder().append(String.format("%02x",this.code)).append(String.format("%08x",this.newNodeID));
		return sb.toString();
	}
	
}
