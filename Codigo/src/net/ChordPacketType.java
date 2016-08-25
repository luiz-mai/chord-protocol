package net;

// TODO: Prestar atenção por que enums são ints e os códigos das mensagens são 1 byte
public enum ChordPacketType {
	Join((byte)0), 
	JoinResponse((byte)128), 
	Leave((byte)1), 
	LeaveResponse((byte)129),
	Lookup((byte)2),
	LookupResponse((byte)130),
	Update((byte)3),
	UpdateResponse((byte)131);
	
	private byte codigo;
	
	ChordPacketType (byte cod){
		this.codigo = cod;
	}
}
