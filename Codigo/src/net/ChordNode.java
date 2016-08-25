package net;

import java.io.IOException;
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
	
	// Variável que define se a thread do servidor deve continuar rodando
	public static final int UDP_PORT = 12233;
	public static boolean listen = true;
	
	// Construtor para criar o nó local 
	public ChordNode(int id, Inet4Address ip, ChordNode sucessor, ChordNode predecessor) throws IOException {
		super();
		this.ID = id;
		this.ip = ip;
		this.sucessor = sucessor;
		this.predecessor = predecessor;
		socket = new DatagramSocket(UDP_PORT);
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
		
		while(listen){
			
		}
	}

}
