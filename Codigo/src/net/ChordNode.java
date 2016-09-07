package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;

import misc.Tools;


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
				Inet4Address incomingIp = (Inet4Address)Inet4Address.getByName("0.0.0.0");

				// Esperar o recebimento de um pacote
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, incomingIp, UDP_PORT);
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
					case ChordPacket.JOIN_CODE:
						JoinPacket jp = new JoinPacket(buffer,offset);
						handleJoin(jp);
						break;
					//Join Response
					case ChordPacket.JOIN_RESP_CODE:
						JoinResponsePacket jrp = new JoinResponsePacket(buffer,offset);
						handleJoinResponse(jrp);
						break;
					//Leave
					case ChordPacket.LEAVE_CODE:
						LeavePacket lp = new LeavePacket(buffer,offset);
						handleLeave(lp);
						break;
					//LeaveResponse
					case ChordPacket.LEAVE_RESP_CODE:
						LeaveResponsePacket lrp = new LeaveResponsePacket(buffer,offset);
						handleLeaveResponse(lrp);
						break;
					//Lookup
					case ChordPacket.LOOKUP_CODE:
						LookupPacket lkp = new LookupPacket(buffer,offset);
						handleLookup(lkp);
						break;
					//LookupResponse
					case ChordPacket.LOOKUP_RESP_CODE:
						LookupResponsePacket lkrp = new LookupResponsePacket(buffer,offset);
						handleLookupResponse(lkrp);
						break;
					//Update
					case ChordPacket.UPDATE_CODE:
						UpdatePacket up = new UpdatePacket(buffer,offset);
						handleUpdate(up);
						break;
					//UpdateResponse
					case ChordPacket.UPDATE_RESP_CODE:
						UpdateResponsePacket urp = new UpdateResponsePacket(buffer,offset);
						handleUpdateResponse(urp);
						break;
				}	
				
				
			} catch (IOException e) {
				e.printStackTrace();
				this.listen = false;
			}

		}
	}
	
	
	public void handleJoin(JoinPacket jp) {
		if(this.sucessor == null && this.predecessor == null){
			//Único nó da rede
			/*new JoinResponsePacket(socket,
								   incomingIp,
								   this.getID(),
								   Tools.ipToInt(this.getIp()),
								   this.getID(),
								   Tools.ipToInt(this.getIp()));*/
		} else {
			//BUSCAR NA REDE
		}
	}
	
	public void handleJoinResponse(JoinResponsePacket jrp) {
		
	}


	public void handleLeave(LeavePacket lp) {

	}
	
	public void handleLeaveResponse(LeaveResponsePacket lrp) {

	}

	
	public void handleLookup(LookupPacket lp) {
		if(this.ID == lp.getWantedID()){
			//O ID procurado é igual ao ID do nó
			
			LookupResponsePacket lrp = new LookupResponsePacket(lp.getWantedID(), this.getID(), Tools.ipToInt(this.getIp()));
			byte[] lrpArray = lrp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lrpArray, 13, Tools.intToIp(lp.getOriginIp()), UDP_PORT );
			socket.send(dp);
			
		} else if(this.ID > lp.getWantedID() && this.getPredecessor().getID() < lp.getWantedID()){
			//O ID procurado fica entre o nó e seu antecessor. Logo, retorna o ID do nó.
			
			LookupResponsePacket lrp = new LookupResponsePacket(lp.getWantedID(), this.getID(), Tools.ipToInt(this.getIp()));
			byte[] lrpArray = lrp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lrpArray, 13, Tools.intToIp(lp.getOriginIp()), UDP_PORT );
			socket.send(dp);
			
		} else if(this.ID < lp.getWantedID() && this.getSucessor().getID() > lp.getWantedID()){
			//O ID procurado fica entre o nó e seu sucessor. Logo, retorna o ID do sucessor.
			
			LookupResponsePacket lrp = new LookupResponsePacket(lp.getWantedID(), this.getSucessor().getID(), Tools.ipToInt(this.getSucessor().getIp()));
			byte[] lrpArray = lrp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lrpArray, 13, Tools.intToIp(lp.getOriginIp()), UDP_PORT );
			DatagramPacket dp = new 
			socket.send(dp);

		} else{
			//O nó atual não é capaz de definir o sucessor, então repassa o Lookup para o próximo nó.
			
			byte[] lpArray = lp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lpArray, 13, this.getSucessor().getIp(), UDP_PORT );
			socket.send(dp);
			
		}
	}
	
	public void handleLookupResponse(LookupResponsePacket lrp) {
		//Ao receber um LookupResponse, o nó deve atualizar seu sucessor.
		
		ChordNode sucessor = this.getSucessor();
		this.setSucessor(sucessor);
		
	}
	
	public void handleUpdate(UpdatePacket up) {

	}
	
	public void handleUpdateResponse(UpdateResponsePacket urp) {

	}

}
