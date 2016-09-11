package net;

import java.io.ByteArrayInputStream;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

import misc.Tools;

public class LookupResponsePacket extends ChordPacket {

	private int wantedID;
	private int sucessorID;
	private Inet4Address sucessorIp;
	
	public LookupResponsePacket (byte[] buffer, int offset){
		
		byte lido[] = new byte[13];
		
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		
		// Le um byte do buffer (codigo) 
		bais.read(lido,offset,1);
		this.code = lido[0];
		
		// Le 4 bytes do buffer (wantedID)
		bais.read(lido,offset,4);
		this.wantedID = ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (sucessorID)
		bais.read(lido,offset,4);
		this.sucessorID = ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (sucessorIp)
		bais.read(lido,offset,4);
		this.sucessorIp = Tools.intToIp(ByteBuffer.wrap(lido).getInt());
	}
	
	public LookupResponsePacket(int wantedID, int sucessorID, Inet4Address sucessorIp){
		super();
		this.code = ChordPacket.LOOKUP_RESP_CODE;
		this.wantedID = wantedID;
		this.sucessorID = sucessorID;
		this.sucessorIp = sucessorIp;
	}

	public int getWantedID() {
		return wantedID;
	}

	public int getSucessorID() {
		return sucessorID;
	}

	public Inet4Address getSucessorIp() {
		return sucessorIp;
	}

	@Override
	public byte[] toByteArray() {
		return ByteBuffer.allocate(13)
				.put(this.code)
				.putInt(wantedID)
				.putInt(sucessorID)
				.putInt(Tools.ipToInt(sucessorIp))
				.array();
	}
	
	@Override 
	public String toString(){
		return String.format("[LookupResponse]\n"
				+ "code: %Xh\n"
				+ "wantedID: %Xh\n"
				+ "sucessorID: %Xh\n",this.code, this.wantedID, this.sucessorID)
				+ "sucessorIP: " + this.sucessorIp.getHostAddress() + "\n"
				+ "\n";
	}


}
