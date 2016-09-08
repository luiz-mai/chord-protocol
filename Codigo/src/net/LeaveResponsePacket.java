package net;

public class LeaveResponsePacket extends ChordPacket {

	
	public LeaveResponsePacket (byte[] buffer, int offset){
		
	}
	
	@Override
	public byte[] toByteArray(){
		return new byte[1];
	}

}
