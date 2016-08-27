package net;

// TODO: Prestar aten��o por que enums s�o ints e os c�digos das mensagens s�o 1 byte
public enum ChordPacketType {
	Join((byte)0b0000_0000), 
	JoinResponse((byte)0b1000_0000), 
	Leave((byte)0b0000_0001), 
	LeaveResponse((byte)0b1000_0001),
	Lookup((byte)0b0000_0010),
	LookupResponse((byte)0b1000_0010),
	Update((byte)0b0000_0011),
	UpdateResponse((byte)0b1000_0011);
	
	private byte code;
	
	public byte getCode(){
		return this.code;
	}
	
	ChordPacketType (byte cod){
		this.code = cod;
	}
}
