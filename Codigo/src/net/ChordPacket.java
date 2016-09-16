package net;

import java.net.DatagramPacket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

	// Essa função retorna o primeiro byte do pacote, que é onde o código está
	// armazenado no protocolo Chord
	public static byte getPacketCode(DatagramPacket packet) {
		int offset = packet.getOffset();
		return packet.getData()[offset];
		
		
	}

	public byte getCode() {
		return code;
	}
	
	public static String getCurrentTime(){
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
	    return sdf.format(cal.getTime());
	}
}
