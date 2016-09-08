package net;

import java.io.ByteArrayInputStream;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

import misc.Tools;

public class LeavePacket extends ChordPacket {

	private int exitID;
	private int exitSucID;
	private Inet4Address exitSucIP;
	private int exitPredecID;
	private Inet4Address exitPredecIP;
	
	public static final int packetSize = 21;
	
	// Construtor para a criação de um pacote Leave recebido
	public LeavePacket (byte[] buffer, int offset){
		
		// Buffer inicializao com 4 pois eh o tamanho maximo dos campos a serem
		// lidos
		byte lido[] = new byte[4];

		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);

		// Le um byte do buffer (codigo)
		bais.read(lido, offset, 1);
		this.code = lido[0];

		// Le 4 bytes do buffer (exitID)
		bais.read(lido, offset, 4);
		this.exitID = ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (exitSucID)
		bais.read(lido, offset, 4);
		this.exitSucID = ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (exitSucIP)
		bais.read(lido, offset, 4);
		this.exitSucIP = Tools.intToIp(ByteBuffer.wrap(lido).getInt());

		// Le 4 bytes do buffer (exitPredecID)
		bais.read(lido, offset, 4);
		this.exitPredecID = ByteBuffer.wrap(lido).getInt();

		// Le 4 bytes do buffer (exitPredecIP)
		bais.read(lido, offset, 4);
		this.exitPredecIP = Tools.intToIp(ByteBuffer.wrap(lido).getInt());
	}
	
	
	// Construtor para a criação de um pacote Leave a ser enviado
	public LeavePacket(int exitID, int exitSucID, Inet4Address exitSucIP, int exitPredecID, Inet4Address exitPredecIP) {
		super();
		this.code = ChordPacket.LEAVE_CODE;
		this.exitID = exitID;
		this.exitSucID = exitSucID;
		this.exitSucIP = exitSucIP;
		this.exitPredecID = exitPredecID;
		this.exitPredecIP = exitPredecIP;
	}

	@Override
	public byte[] toByteArray(){
		int size = LeavePacket.packetSize;
		
		return ByteBuffer.allocate(size)
				.put(this.code)
				.putInt(this.exitID)
				.putInt(this.exitSucID)
				.putInt(Tools.ipToInt(this.exitSucIP))
				.putInt(this.exitPredecID)
				.putInt(Tools.ipToInt(this.exitPredecIP))
				.array();
	}
	
	@Override
	public String toString(){
		return String.format( "code: %X \nexitID: %d \nexitSucID",this.code,this.exitID,this.exitSucID) +  
				"\nexitSucIP: " + this.exitSucIP.toString() + 
				String.format( "\nexitPredecID: %d",this.exitPredecID) +  
				"\nexitPredecIP:" + this.exitPredecIP.toString() + "\n\n";
	}
}
