package net;

import java.io.ByteArrayInputStream;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

import misc.Tools;

public class LookupPacket extends ChordPacket{
		
	private int originID;
	private Inet4Address originIP;
	private int wantedID;
	
	public LookupPacket (byte[] buffer, int offset){
		
		byte lido[] = new byte[13];
		
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		
		// Le um byte do buffer (codigo) 
		bais.read(lido,offset,1);
		this.code = lido[0];
		
		// Le 4 bytes do buffer (originID)
		bais.read(lido,offset,4);
		this.originID = ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (oroginIp)
		bais.read(lido,offset,4);
		this.originIP = Tools.intToIp(ByteBuffer.wrap(lido).getInt());

		// Le 4 bytes do buffer (newNodeID)
		bais.read(lido,offset,4);
		this.wantedID = ByteBuffer.wrap(lido).getInt();
	}
	
	public LookupPacket(int originID, Inet4Address originIp, int wantedID){
		super();
		this.code = ChordPacket.LOOKUP_CODE;
		this.originID = originID;
		this.originIP = originIp;
		this.wantedID = wantedID;
	}
	
	@Override
	public byte[] toByteArray(){
		return ByteBuffer.allocate(13)
				.put(this.code)
				.putInt(originID)
				.putInt(Tools.ipToInt(originIP))
				.putInt(wantedID)
				.array();
	}


	public String toString() {
		
		return String.format("[Lookup]\n"
				+ "code: %Xh\n"
				+ "originID: %Xh\n",this.code, this.originID)
				+ "originIP: " + this.originIP.getHostAddress() + "\n"
				+ String.format("wantedID: %Xh", this.wantedID)
				+ "\n";
	}

	public int getOriginID() {
		return originID;
	}

	public Inet4Address getOriginIp() {
		return originIP;
	}

	public int getWantedID() {
		return wantedID;
	}
	
}
