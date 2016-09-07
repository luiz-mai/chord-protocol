package net;

public abstract class ChordPacket {

	public static final byte JOIN_CODE = 0b0000_0000;
	public static final byte JOIN_RESP_CODE = (byte) 0b1000_0000;
	public static final byte LEAVE_CODE = 0b0000_0001;
	public static final byte LEAVE_RESP_CODE = (byte) 0b1000_0001;
	public static final byte LOOKUP_CODE = 0b0000_0010;
	public static final byte LOOKUP_RESP_CODE = (byte) 0b1000_0010;
	public static final byte UPDATE_CODE = 0b0000_0011;
	public static final byte UPDATE_RESP_CODE = (byte) 0b1000_0011;
	
	protected byte code;
	
	abstract public byte[] toByteArray();
	
	public byte getCode() {
		return code;
	}
}
