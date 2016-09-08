package net;

public class UpdateResponsePacket extends ChordPacket {

	
	public UpdateResponsePacket (byte[] buffer, int offset){
		
	}
	
	@Override
	public byte[] toByteArray(){
		return new byte[1];
	}


}
