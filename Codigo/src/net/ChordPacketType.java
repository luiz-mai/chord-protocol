package net;

// TODO: Prestar aten��o por que enums s�o ints e os c�digos das mensagens s�o 1 byte
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
