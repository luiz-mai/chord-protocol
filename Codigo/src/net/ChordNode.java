package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Random;

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
	public ChordNode(int id, Inet4Address ip, ChordNode sucessor, ChordNode predecessor) {
		super();
		this.ID = id;
		this.ip = ip;
		this.sucessor = sucessor;
		this.predecessor = predecessor;
		
		try{
			this.socket = new DatagramSocket(UDP_PORT,ip);
		}catch(IOException e){
			System.out.println("Erro ao criar o nó. Fechando programa.");
			System.exit(1);
		}
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

	public DatagramSocket getSocket() {
		return socket;
	}
	
	public void run() {

		while (listen) {

				// O tamanho do buffer para os pacotes é de 21 bytes pois esse é
				// tamanho do maior pacote previsto pela aplicação: um envio da
				// funcionalidade Leave.
				byte[] buffer = new byte[21];
				
				DatagramPacket packet = receivePacket(buffer);
				
				if( packet == null )
					continue;

				// Pegando o codigo do pacote (primeiro byte)
				// TODO: Estou admitindo que o buffer é sempre realocado quando eu chamo packetReceive
				// e que por isso o pacote começa no começo do buffer.
				int offset = packet.getOffset();
				
				byte code = buffer[offset];
				
				Inet4Address incomingIp = (Inet4Address) packet.getAddress();
				
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
				default:
					// Ignore
				}

			

		}
	}

	public void handleJoin(JoinPacket jp, InetAddress incomingIp) {

		byte confirmacaoErro;
		
		//Precisamos salvar uma referëncia para o antigo predecessor para não perdê-la na atualização
		ChordNode oldPredecessor = this.predecessor;
				
		if (this.ID == jp.getNewNodeID()) {
			// O nó que enviou o pacote tem o mesmo ID que o nó local. Responder
			// com código de erro.
			confirmacaoErro = 0;			
		} else {
			// O novo nó pode ser inserido com segurança.
			confirmacaoErro = 0x01;
			
			// Vamos atualizar o predecessor do nó local
			ChordNode newNode = new ChordNode(jp.getNewNodeID(),incomingIp);
			this.setPredecessor(newNode);
		}
		
		/*
		 * Decisão de projeto: Numa situação de erro, não foi especificado o que deveria ser 
		 * retornado nos demais campos de JoinResponse. Por simplicidade, escolhemos retornar 
		 * os mesmos valores que a situação sem erro. Cabe ao destinatário verificar o campos 
		 * de confirmação/erro para determinar a validade das informações contidas no pacote.
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
		/* A única situação na qual recebemos um JoinResponse é quando queremos entrar
		 * na rede e enviamos um Join para um nó que já pertença a ela. Esse nó então
		 * responde a solicitação com um JoinResponse, contendo as informações sobre
		 * antecessor e sucessor.
		 */
		
		if(jrp.getStatus() == (byte) 0x00){
			/* TODO: em que situação haveria um erro no Join? O caso de ID repetido não
			 * é tratado quando fazemos o lookup e recebemos o mesmo ID como sucessor?
			 * Além disso, o que fazer quando recebermos um erro assim? Reenviar o Join?
			 */
		}else{
			// Join sem erro.
			
			/* Só atualizar os ponteiros de sucessor e predecessor se eles já não estiverem
			 * setados. Temo que fazer isso pq essa JoinResponse pode ter sido enviada por engano
			 * e caso executemos o procedimento padrão para tratá-la podemos bagunçar a rede
			 * toda.
			 */
			
			if(this.getSucessor() == null && this.getPredecessor() == null){
				ChordNode newSucessor = new ChordNode(jrp.getSucessorID(),jrp.getSucessorIP());
				ChordNode newPredecessor = new ChordNode(jrp.getPredecessorID(),jrp.getPredecessorIP());
				
				this.setSucessor(newSucessor);
				this.setPredecessor(newPredecessor);
				
				// Agora precisamos avisar o nosso antecessor que entramos na rede
				// Para isso mandamos um Update
				
				// TODO: Os dois primeiros campos vão ser iguais mesmo?
				UpdatePacket up = new UpdatePacket(this.getID(),this.getID(),this.getIp());
				sendPacket(up,this.getPredecessor().getIp());
			}
			// E o caso de apenas um deles ser nulo? Precisamos tratar?
		}
	}

	public void handleLeave(LeavePacket lp) {
		/*
		 * Recebemos um Leave quando ou o nosso antecessor ou sucessor desejam sair da rede.
		 * Nesse caso, precisamos checar qual dos dois é que está mandando essa mensagem e
		 * atualizar o ponteiro correspondentemente.
		 */
		
		// ID do nó que está saindo da rede.
		int senderID = lp.getExitID();
		
		if( senderID == this.getSucessor().getID() ){
			// É o sucessor quem está saindo da rede.
			
			// Precisamos salvar o IP do nó que está saindo da rede para podermos enviar a resposta
			Inet4Address oldSucessorIP = this.getSucessor().getIp();
			
			ChordNode newSucessor = new ChordNode(lp.getExitSucID(),lp.getExitSucIP());
			this.setSucessor(newSucessor);
			
			/* Tendo atualizado o sucessor, precisamos enviar uma mensagem de confirmação
			 * para o nó que está saindo da rede.
			 */
			
			sendPacket(new LeaveResponsePacket(this.getID()),oldSucessorIP);
			
		}else if (senderID == this.getPredecessor().getID()){
			// É o predecessor quem está saindo da rede.
			
			// Precisamos salvar o IP do nó que está saindo da rede para podermos enviar a resposta
			Inet4Address oldPredecIP = this.getPredecessor().getIp();
		
			ChordNode newPredecessor = new ChordNode(lp.getExitPredecID(),lp.getExitPredecIP());
			this.setPredecessor(newPredecessor);
			
			/* Tendo atualizado o sucessor, precisamos enviar uma mensagem de confirmação
			 * para o nó que está saindo da rede.
			 */
			
			sendPacket(new LeaveResponsePacket(this.getID()),oldPredecIP);
			
		}else{
			// O ID do pacote que enviou a mensagem não corresponde nem ao sucessor nem ao
			// predecessor. Deve ter sido enviado por engano.
			
			// TODO: Ignorar?
		}
	}

	public void handleLeaveResponse(LeaveResponsePacket lrp) {
		/* TODO: Esse caso é um pouco mais complexo. Por que receberemos
		 * pacotes desse tipo quando estivermos saindo da rede. Então precisamos
		 * enviar um pacote de Leave para os vizinhos e esperar 2 pacotes
		 * LeaveResponse, um de cada um, para então sabermos que já podemos sair
		 * efetivamente da rede. Então precisamos setar esse "OK, pode sair" em alguma 
		 * variável da classe ou algo assim.
		 */
		
		int senderID = lrp.getOriginID();
		
		if(senderID == this.getSucessor().getID()){
			// Sucessor OK
		}else if (senderID == this.getPredecessor().getID()){
			// Predecessor OK
		}else{
			// Nenhum dos dois. Engano?
			// Ignorar
		}
	}

	public void handleLookup(LookupPacket lp) {
		if (this.ID == lp.getWantedID()) {
			// O ID procurado é igual ao ID do nó

			LookupResponsePacket lrp = new LookupResponsePacket(lp.getWantedID(), this.getID(),
					this.getIp());
			
			/*byte[] lrpArray = lrp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lrpArray, 13, Tools.intToIp(lp.getOriginIp()), UDP_PORT);
			try {
				socket.send(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			sendPacket(lrp,lp.getOriginIp());

		} else if (this.ID > lp.getWantedID() && this.getPredecessor().getID() < lp.getWantedID()) {
			// O ID procurado fica entre o nó e seu antecessor. Logo, retorna o
			// ID do nó.

			LookupResponsePacket lrp = new LookupResponsePacket(lp.getWantedID(), this.getID(),
					this.getIp());
			/*byte[] lrpArray = lrp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lrpArray, 13, Tools.intToIp(lp.getOriginIp()), UDP_PORT);

			try {
				socket.send(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			sendPacket(lrp,lp.getOriginIp());

		} else if (this.ID < lp.getWantedID() && this.getSucessor().getID() > lp.getWantedID()) {
			// O ID procurado fica entre o nó e seu sucessor. Logo, retorna o ID
			// do sucessor.

			LookupResponsePacket lrp = new LookupResponsePacket(lp.getWantedID(), this.getSucessor().getID(),
					this.getSucessor().getIp());
			/*byte[] lrpArray = lrp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lrpArray, 13, Tools.intToIp(lp.getOriginIp()), UDP_PORT);
			try {
				socket.send(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			sendPacket(lrp,lp.getOriginIp());

		} else {
			// O nó atual não é capaz de definir o sucessor, então repassa o
			// Lookup para o próximo nó.

			/*byte[] lpArray = lp.toByteArray();
			DatagramPacket dp = new DatagramPacket(lpArray, 13, this.getSucessor().getIp(), UDP_PORT);
			try {
				socket.send(dp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			sendPacket(lp,this.getSucessor().getIp());

		}
	}

	public void handleLookupResponse(LookupResponsePacket lrp) {
		
		if(this.getSucessor() == null || this.getPredecessor() == null){
			// Nó acabou de ser criado e ainda não tem sucessor e predecessor.
			// Ao receber um LookupResponse, o nó deve atualizar seu sucessor.
			
			ChordNode sucessor = new ChordNode(lrp.getSucessorID(),lrp.getSucessorIp());
			this.setSucessor(sucessor);
			
		}else{
			System.out.printf("O sucessor do ID procurado tem o ID %d e o seu IP é ",lrp.getSucessorID(),lrp.getSucessorIp().toString());
		}

	}

	public void handleUpdate(UpdatePacket up) {
		/* Quando recebemos um update, quer dizer que um novo nó entrou
		 * na rede e ele é o novo sucessor do nó local. Portanto, devemos
		 * atualizar o ponteiro para o sucessor.
		 */
		
		ChordNode newSucessor = new ChordNode(up.getNewSucessorID(),up.getNewSucessorIP());
		this.setSucessor(newSucessor);
	}

	public void handleUpdateResponse(UpdateResponsePacket urp) {

		if(urp.getStatus() == (byte) 0x00){
			/* TODO: em que situação haveria um erro no Update?
			 * Reenviar a mensagem em caso de erro?
			 */
		}else{
			// TODO: Acho que nao precisa fazer nada haha.
		}
		
	}
	
	public void sendPacket(ChordPacket cp, InetAddress destIP){
		
		byte[] cpArray = cp.toByteArray();
		DatagramPacket dp = new DatagramPacket(cpArray, cpArray.length, destIP, UDP_PORT);
		
		// Adicionar pacote enviado à GUI
		main.Main.sentMessages.add(cp.toString());
		
		try {
			socket.send(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DatagramPacket receivePacket(byte[] buffer){
		
		DatagramPacket packet = null;
		
		try{
			
			Inet4Address incomingIp = (Inet4Address) Inet4Address.getByName("0.0.0.0");

			// Esperar o recebimento de um pacote
			packet = new DatagramPacket(buffer, buffer.length, incomingIp, UDP_PORT);
			socket.receive(packet);
			
		}catch(IOException e){
			System.out.println("Erro na hora de receber pacotes. Morri na função ChordNode.receivePacket()");
			System.exit(1);
		}
		
		return packet;
	}
	
	public static void createRing(Inet4Address ipLocal){
		
		// Gerando id aleatoriamente
		int id = (new Random(1000)).nextInt();
		
		ChordNode local = new ChordNode(id,ipLocal,null,null);
		
		// Como o nó ainda está sozinho na rede, sucessor e antecessor devem apontar pro próprio objeto
		local.setSucessor(local);
		local.setPredecessor(local);
		
		local.run();
	}
	
	public static void joinRing(Inet4Address ipLocal, Inet4Address knownHost){
		
		// Gerando id aleatoriamente
		int id = (new Random(1000)).nextInt();
		
		ChordNode local = new ChordNode(id,ipLocal,null,null);
		local.run();
		
		// Precisamos fazer um lookup no ID recém criado.
		
		//TODO: Eh correto colocar o ID de origem como sendo desse kra que ainda ta fora da rede?
		LookupPacket lp = new LookupPacket(local.getID(),local.getIp(),local.getID());
		local.sendPacket(lp, knownHost);
		
		byte buffer[] = new byte[21];
		
		DatagramPacket packet = local.receivePacket(buffer);
		
		int offset = packet.getOffset();
		
		byte code = buffer[offset];
		
		if(code == ChordPacket.LOOKUP_RESP_CODE){
			
		}
		
		
		
		
	}
	
	

}
