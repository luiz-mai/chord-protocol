package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;

public class ChordNode extends Thread {

	// Na especificação o ID deve ser de 32 bits, que é o tamanho exato do tipo
	// int em Java
	private int ID;
	private Inet4Address ip;
	private ChordNode sucessor;
	private ChordNode predecessor;
	private DatagramSocket socket = null;

	// Constante com a porta UDP a ser usada no protocolo
	public static final int UDP_PORT = 12233;

	// Variável que define se a thread do servidor deve continuar rodando
	public boolean listen = true;

	// Construtor para criar o nó local
	public ChordNode(int id, Inet4Address ip, ChordNode sucessor, ChordNode predecessor) throws IOException {
		super();
		this.ID = id;
		this.ip = ip;
		this.sucessor = sucessor;
		this.predecessor = predecessor;
		this.socket = new DatagramSocket(UDP_PORT);
	}

	// Construtor utilizado para criar os objetos sucessor e predecessor
	public ChordNode(int iD, Inet4Address ip) {
		super();
		this.ID = iD;
		this.ip = ip;
		this.sucessor = null;
		this.predecessor = null;
		this.socket = null;

	}

	public int getID() {
		return ID;
	}

	public void setID(int id) {
		this.ID = id;
	}

	public Inet4Address getIp() {
		return ip;
	}

	public void setIp(Inet4Address ip) {
		this.ip = ip;
	}

	public ChordNode getSucessor() {
		return sucessor;
	}

	public void setSucessor(ChordNode sucessor) {
		this.sucessor = sucessor;
	}

	public ChordNode getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(ChordNode predecessor) {
		this.predecessor = predecessor;
	}

	public void run() {

		while (listen) {
			try {

				// O tamanho do buffer para os pacotes é de 21 bytes pois esse é
				// tamanho do maior pacote previsto pela aplicação: um envio da
				// funcionalidade Leave.
				byte[] buffer = new byte[21];

				// Esperar o recebimento de um pacote
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				// Parsing do pacote recebido para descobrir qual o seu tipo

				// TODO Remover: Imagino que a info do pacote nesse momento
				// esteja salva no array buffer. Espero que isso seja correto xD
				
				// Capturar o offset do pacote no buffer pra saber onde começar a ler
				int offset = packet.getOffset();
				
				// Pegando o codigo do pacote (primeiro byte)
				byte code = buffer[offset];

				switch(code){
					//Join
					case (byte)0b0000_0000:
						break;
					//Join Response
					case (byte)0b1000_0000:
						break;
					//Leave
					case (byte)0b0000_0001:
						break;
					//LeaveResponse
					case (byte)0b1000_0001:
						break;
					//Lookup
					case (byte)0b0000_0010:
						break;
					//LookupResponse
					case (byte)0b1000_0010:
						break;
					//Update
					case (byte)0b0000_0011:
						break;
					//UpdateResponse
					case (byte)0b1000_0011:
						break;
				}	
				
				
			} catch (IOException e) {
				e.printStackTrace();
				this.listen = false;
			}

		}
	}
	
	public void parseJoin() {
		
	}
	
	public void handleJoin() {

	}

	public void parseLeave() {
		
	}

	public void handleLeave() {

	}

	public void parseLookup() {
		
	}
	
	public void handleLookup() {

	}

	public void parseUpdate() {
		
	}
	
	public void handleUpdate() {

	}

}
