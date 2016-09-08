package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;

import misc.Tools;

public class ChordNode extends Thread {

	// Na especifica��o o ID deve ser de 32 bits, que � o tamanho exato do tipo
	// int em Java
	private int ID;
	private Inet4Address ip;
	private ChordNode sucessor;
	private ChordNode predecessor;
	private DatagramSocket socket = null;

	// Constante com a porta UDP a ser usada no protocolo
	public static final int UDP_PORT = 12233;

	// Vari�vel que define se a thread do servidor deve continuar rodando
	public boolean listen = true;

	// Construtor para criar o n� local
	public ChordNode(int id, Inet4Address ip, ChordNode sucessor, ChordNode predecessor) throws IOException {
		super();
		this.ID = id;
		this.ip = ip;
		this.sucessor = sucessor;
		this.predecessor = predecessor;
		this.socket = new DatagramSocket(UDP_PORT);
	}

	// Construtor utilizado para criar os objetos sucessor e predecessor
	public ChordNode(int iD, InetAddress ip) {
		super();
		this.ID = iD;
		this.ip = (Inet4Address) ip;
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

				// O tamanho do buffer para os pacotes � de 21 bytes pois esse �
				// tamanho do maior pacote previsto pela aplica��o: um envio da
				// funcionalidade Leave.
				byte[] buffer = new byte[21];
				Inet4Address incomingIp = (Inet4Address) Inet4Address.getByName("0.0.0.0");

				// Esperar o recebimento de um pacote
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, incomingIp, UDP_PORT);
				socket.receive(packet);

				// Parsing do pacote recebido para descobrir qual o seu tipo

				// TODO Remover: Imagino que a info do pacote nesse momento
				// esteja salva no array buffer. Espero que isso seja correto xD

				// Capturar o offset do pacote no buffer pra saber onde come�ar
				// a ler
				int offset = packet.getOffset();

				// Pegando o codigo do pacote (primeiro byte)
				byte code = buffer[offset];

				switch (code) {
				// Join
				case ChordPacket.JOIN_CODE:
					JoinPacket jp = new JoinPacket(buffer, offset);
					handleJoin(jp, incomingIp);
					break;
				// Join Response
				case ChordPacket.JOIN_RESP_CODE:
					JoinResponsePacket jrp = new JoinResponsePacket(buffer, offset);
					handleJoinResponse(jrp);
					break;
				// Leave
				case ChordPacket.LEAVE_CODE:
					LeavePacket lp = new LeavePacket(buffer, offset);
					handleLeave(lp);
					break;
				// LeaveResponse
				case ChordPacket.LEAVE_RESP_CODE:
					LeaveResponsePacket lrp = new LeaveResponsePacket(buffer, offset);
					handleLeaveResponse(lrp);
					break;
				// Lookup
				case ChordPacket.LOOKUP_CODE:
					LookupPacket lkp = new LookupPacket(buffer, offset);
					handleLookup(lkp);
					break;
				// LookupResponse
				case ChordPacket.LOOKUP_RESP_CODE:
					LookupResponsePacket lkrp = new LookupResponsePacket(buffer, offset);
					handleLookupResponse(lkrp);
					break;
				// Update
				case ChordPacket.UPDATE_CODE:
					UpdatePacket up = new UpdatePacket(buffer, offset);
					handleUpdate(up);
					break;
				// UpdateResponse
				case ChordPacket.UPDATE_RESP_CODE:
					UpdateResponsePacket urp = new UpdateResponsePacket(buffer, offset);
					handleUpdateResponse(urp);
					break;
				}

			} catch (IOException e) {
				e.printStackTrace();
				this.listen = false;
			}

		}
	}

	public void handleJoin(JoinPacket jp, InetAddress incomingIp) {

		byte confirmacaoErro;
		
		//Precisamos salvar uma refer�ncia para o antigo predecessor para n�o perd�-la na atualiza��o
		ChordNode oldPredecessor = this.predecessor;
				
		if (this.ID == jp.getNewNodeID()) {
			// O n� que enviou o pacote tem o mesmo ID que o n� local. Responder
			// com c�digo de erro.
			confirmacaoErro = 0;			
		} else {
			// O novo n� pode ser inserido com seguran�a.
			confirmacaoErro = 0x01;
			
			// Vamos atualizar o predecessor do n� local
			ChordNode newNode = new ChordNode(jp.getNewNodeID(),incomingIp);
			this.setPredecessor(newNode);
		}
		
		/*
		 * Decis�o de projeto: Numa situa��o de erro, n�o foi especificado o que deveria ser 
		 * retornado nos demais campos de JoinResponse. Por simplicidade, escolhemos retornar 
		 * os mesmos valores que a situa��o sem erro. Cabe ao destinat�rio verificar o campos 
		 * de confirma��o/erro para determinar a validade das informa��es contidas no pacote.
		 */
		
		JoinResponsePacket jrp = new JoinResponsePacket(confirmacaoErro, this.ID, this.ip, oldPredecessor.getID(),
				oldPredecessor.getIp());
		
		byte[] jrpArray = jrp.toByteArray();
		DatagramPacket dp = new DatagramPacket(jrpArray, JoinResponsePacket.packetSize, incomingIp, UDP_PORT);
		
		try {
			socket.send(dp);
		} catch (IOException ioe) {
			// TODO: O que fazer nesse caso? Imprimir mensagem de erro na
			// tela? Esperar um tempo e mandar de novo?
		}
	}

	public void handleJoinResponse(JoinResponsePacket jrp) {
		
	}

	public void handleLeave(LeavePacket lp) {

	}

	public void handleLeaveResponse(LeaveResponsePacket lrp) {

	}

	public void handleLookup(LookupPacket lp) {
		if (this.ID == lp.getWantedID()) {
			// O ID procurado � igual ao ID do n�

			LookupResponsePacket lrp = new LookupResponsePacket(lp.getWantedID(), this.getID(),
					Tools.ipToInt(this.getIp()));
			byte[] lrpArray = lrp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lrpArray, 13, Tools.intToIp(lp.getOriginIp()), UDP_PORT);
			try {
				socket.send(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (this.ID > lp.getWantedID() && this.getPredecessor().getID() < lp.getWantedID()) {
			// O ID procurado fica entre o n� e seu antecessor. Logo, retorna o
			// ID do n�.

			LookupResponsePacket lrp = new LookupResponsePacket(lp.getWantedID(), this.getID(),
					Tools.ipToInt(this.getIp()));
			byte[] lrpArray = lrp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lrpArray, 13, Tools.intToIp(lp.getOriginIp()), UDP_PORT);

			try {
				socket.send(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (this.ID < lp.getWantedID() && this.getSucessor().getID() > lp.getWantedID()) {
			// O ID procurado fica entre o n� e seu sucessor. Logo, retorna o ID
			// do sucessor.

			LookupResponsePacket lrp = new LookupResponsePacket(lp.getWantedID(), this.getSucessor().getID(),
					Tools.ipToInt(this.getSucessor().getIp()));
			byte[] lrpArray = lrp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lrpArray, 13, Tools.intToIp(lp.getOriginIp()), UDP_PORT);
			try {
				socket.send(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// O n� atual n�o � capaz de definir o sucessor, ent�o repassa o
			// Lookup para o pr�ximo n�.

			byte[] lpArray = lp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lpArray, 13, this.getSucessor().getIp(), UDP_PORT);
			try {
				socket.send(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void handleLookupResponse(LookupResponsePacket lrp) {
		// Ao receber um LookupResponse, o n� deve atualizar seu sucessor.

		ChordNode sucessor = this.getSucessor();
		this.setSucessor(sucessor);

	}

	public void handleUpdate(UpdatePacket up) {

	}

	public void handleUpdateResponse(UpdateResponsePacket urp) {

	}

}
