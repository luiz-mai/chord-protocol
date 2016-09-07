package net;

public class LookupResponsePacket extends ChordPacket {

	
	public LookupResponsePacket (byte[] buffer, int offset){
		
	}
	
	@Override
	public byte[] toByteArray(){
		return new byte[1];
	}

}
