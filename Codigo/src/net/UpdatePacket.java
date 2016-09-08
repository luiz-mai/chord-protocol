package net;

public class UpdatePacket extends ChordPacket {

	public UpdatePacket (byte[] buffer, int offset){
		
	}
	
	@Override
	public byte[] toByteArray(){
		return new byte[1];
	}


}
