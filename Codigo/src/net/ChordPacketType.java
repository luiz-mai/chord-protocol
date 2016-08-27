package net;

// TODO: Prestar atenção por que enums são ints e os códigos das mensagens são 1 byte
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
