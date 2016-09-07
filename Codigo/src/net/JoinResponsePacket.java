package net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

public class JoinResponsePacket extends ChordPacket{
		
	private DatagramSocket socket = null;
	private Inet4Address incomingIp;
	
	private int sucessorID;
	private int sucessorIp;
	private int predecessorID;
	private int predecessorIp;
	
	public JoinResponsePacket (byte[] buffer, int offset){
		
	}
	
	public JoinResponsePacket (DatagramSocket socket, Inet4Address incomingIp,
			int sucessorID, int sucessorIp, int predecessorID, int predecessorIp){
		
		this.socket = socket;
		this.incomingIp = incomingIp;
		
		this.code = ChordPacket.JOIN_RESP_CODE;	
		this.sucessorID = sucessorID;
		this.sucessorIp = sucessorIp;
		this.predecessorID = predecessorID;
		this.predecessorIp = predecessorIp;

	}
	
	void handle(){
		try {
			byte[] joinBuffer = ByteBuffer.allocate(18)
					  .put(this.code)
					  .put((byte)1)
					  .putInt(this.sucessorID)
					  .putInt(this.sucessorIp)
					  .putInt(this.predecessorID)
					  .putInt(this.predecessorIp)
					  .array();

			DatagramPacket joinResponsePacket = new DatagramPacket(joinBuffer, joinBuffer.length, incomingIp, ChordNode.UDP_PORT);

			socket.send(joinResponsePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String toString(){
		//TODO toString();
		return "";
	}
	
}
