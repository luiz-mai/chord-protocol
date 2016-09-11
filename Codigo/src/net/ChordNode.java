package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

import main.Main;

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
	public static final int TIMEOUT = 1000; //ms

	// Vari�vel que define se a thread do servidor deve continuar rodando
	public boolean listen = true;

	// Construtor para criar o n� local
	public ChordNode(int id, Inet4Address ip, ChordNode sucessor, ChordNode predecessor) {
		super();
		this.ID = id;
		this.ip = ip;
		this.sucessor = sucessor;
		this.predecessor = predecessor;
		
		try{
			this.socket = new DatagramSocket(UDP_PORT,ip);
			
			// Aqui vamos setar o timeout para o recebimento de pacotes pelo socket
			// para que ele pare a cada TIMEOUT milisegundos
			this.getSocket().setSoTimeout(ChordNode.TIMEOUT);
		}catch(SocketException se){
				System.out.println("Erro ao setar o timeout para o socket em ChordNode.joinRing()");
				se.printStackTrace();
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
		
		try{
			/* Setando o timeout para o socket. Ele s� ficar� bloqueado esperando
			 * um pacote por no m�ximo 500ms (0.5s).
			 */
			this.socket.setSoTimeout(500);
		}catch(SocketException se){
			System.out.println("Erro ao setar o timeout para o socket em ChordNode.run()");
			se.printStackTrace();
		}

		while (listen) {

				// O tamanho do buffer para os pacotes � de 21 bytes pois esse �
				// tamanho do maior pacote previsto pela aplica��o: um envio da
				// funcionalidade Leave.
				byte[] buffer = new byte[21];
				
				
			
				DatagramPacket packet = receivePacket(buffer);
				
				if( packet == null )
					continue;

				// Pegando o codigo do pacote (primeiro byte)
				// TODO: Estou admitindo que o buffer � sempre realocado quando eu chamo packetReceive
				// e que por isso o pacote come�a no come�o do buffer.
				int offset = packet.getOffset();
				
				byte code = buffer[offset];
				
				Inet4Address incomingIp = (Inet4Address) packet.getAddress();
				
				switch (code) {
				// Join
				case ChordPacket.JOIN_CODE:
					JoinPacket jp = new JoinPacket(buffer, offset);
					showReceivedMessage(jp);
					handleJoin(jp, incomingIp);
					break;
				// Join Response
				case ChordPacket.JOIN_RESP_CODE:
					JoinResponsePacket jrp = new JoinResponsePacket(buffer, offset);
					showReceivedMessage(jrp);
					handleJoinResponse(jrp);
					break;
				// Leave
				case ChordPacket.LEAVE_CODE:
					LeavePacket lp = new LeavePacket(buffer, offset);
					showReceivedMessage(lp);
					handleLeave(lp);
					break;
				// LeaveResponse
				case ChordPacket.LEAVE_RESP_CODE:
					LeaveResponsePacket lrp = new LeaveResponsePacket(buffer, offset);
					showReceivedMessage(lrp);
					handleLeaveResponse(lrp);
					break;
				// Lookup
				case ChordPacket.LOOKUP_CODE:
					LookupPacket lkp = new LookupPacket(buffer, offset);
					showReceivedMessage(lkp);
					handleLookup(lkp);
					break;
				// LookupResponse
				case ChordPacket.LOOKUP_RESP_CODE:
					LookupResponsePacket lkrp = new LookupResponsePacket(buffer, offset);
					showReceivedMessage(lkrp);
					handleLookupResponse(lkrp);
					break;
				// Update
				case ChordPacket.UPDATE_CODE:
					UpdatePacket up = new UpdatePacket(buffer, offset);
					showReceivedMessage(up);
					handleUpdate(up);
					break;
				// UpdateResponse
				case ChordPacket.UPDATE_RESP_CODE:
					UpdateResponsePacket urp = new UpdateResponsePacket(buffer, offset);
					showReceivedMessage(urp);
					handleUpdateResponse(urp);
					break;
				default:
					// Ignore
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
	
		sendPacket(jrp,incomingIp);
	}

	public void handleJoinResponse(JoinResponsePacket jrp) {
		/* A �nica situa��o na qual recebemos um JoinResponse � quando queremos entrar
		 * na rede e enviamos um Join para um n� que j� perten�a a ela. Esse n� ent�o
		 * responde a solicita��o com um JoinResponse, contendo as informa��es sobre
		 * antecessor e sucessor.
		 */
		
		if(jrp.getStatus() == (byte) 0x00){
			/* TODO: em que situa��o haveria um erro no Join? O caso de ID repetido n�o
			 * � tratado quando fazemos o lookup e recebemos o mesmo ID como sucessor?
			 * Al�m disso, o que fazer quando recebermos um erro assim? Reenviar o Join?
			 */
		}else{
			// Join sem erro.
			
			/* S� atualizar os ponteiros de sucessor e predecessor se eles j� n�o estiverem
			 * setados. Temo que fazer isso pq essa JoinResponse pode ter sido enviada por engano
			 * e caso executemos o procedimento padr�o para trat�-la podemos bagun�ar a rede
			 * toda.
			 */
			
			if(this.getSucessor() == null && this.getPredecessor() == null){
				ChordNode newSucessor = new ChordNode(jrp.getSucessorID(),jrp.getSucessorIP());
				ChordNode newPredecessor = new ChordNode(jrp.getPredecessorID(),jrp.getPredecessorIP());
				
				this.setSucessor(newSucessor);
				this.setPredecessor(newPredecessor);
				
				// Agora precisamos avisar o nosso antecessor que entramos na rede
				// Para isso mandamos um Update
				
				// TODO: Os dois primeiros campos v�o ser iguais mesmo?
				UpdatePacket up = new UpdatePacket(this.getID(),this.getID(),this.getIp());
				sendPacket(up,this.getPredecessor().getIp());
			}
			// E o caso de apenas um deles ser nulo? Precisamos tratar?
		}
	}

	public void handleLeave(LeavePacket lp) {
		/*
		 * Recebemos um Leave quando ou o nosso antecessor ou sucessor desejam sair da rede.
		 * Nesse caso, precisamos checar qual dos dois � que est� mandando essa mensagem e
		 * atualizar o ponteiro correspondentemente.
		 */
		
		// ID do n� que est� saindo da rede.
		int senderID = lp.getExitID();
		
		if( senderID == this.getSucessor().getID() ){
			// � o sucessor quem est� saindo da rede.
			
			// Precisamos salvar o IP do n� que est� saindo da rede para podermos enviar a resposta
			Inet4Address oldSucessorIP = this.getSucessor().getIp();
			
			ChordNode newSucessor = new ChordNode(lp.getExitSucID(),lp.getExitSucIP());
			this.setSucessor(newSucessor);
			
			/* Tendo atualizado o sucessor, precisamos enviar uma mensagem de confirma��o
			 * para o n� que est� saindo da rede.
			 */
			
			sendPacket(new LeaveResponsePacket(this.getID()),oldSucessorIP);
			
		}else if (senderID == this.getPredecessor().getID()){
			// � o predecessor quem est� saindo da rede.
			
			// Precisamos salvar o IP do n� que est� saindo da rede para podermos enviar a resposta
			Inet4Address oldPredecIP = this.getPredecessor().getIp();
		
			ChordNode newPredecessor = new ChordNode(lp.getExitPredecID(),lp.getExitPredecIP());
			this.setPredecessor(newPredecessor);
			
			/* Tendo atualizado o sucessor, precisamos enviar uma mensagem de confirma��o
			 * para o n� que est� saindo da rede.
			 */
			
			sendPacket(new LeaveResponsePacket(this.getID()),oldPredecIP);
			
		}else{
			// O ID do pacote que enviou a mensagem n�o corresponde nem ao sucessor nem ao
			// predecessor. Deve ter sido enviado por engano.
			
			// TODO: Ignorar?
		}
	}

	public void handleLeaveResponse(LeaveResponsePacket lrp) {
		/* TODO: Esse caso � um pouco mais complexo. Por que receberemos
		 * pacotes desse tipo quando estivermos saindo da rede. Ent�o precisamos
		 * enviar um pacote de Leave para os vizinhos e esperar 2 pacotes
		 * LeaveResponse, um de cada um, para ent�o sabermos que j� podemos sair
		 * efetivamente da rede. Ent�o precisamos setar esse "OK, pode sair" em alguma 
		 * vari�vel da classe ou algo assim.
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
			// O ID procurado � igual ao ID do n�

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

		} else if(this.getSucessor() == this.getPredecessor()){
			// Nesse caso o n� atual est� sozinho na rede, por isso os ponteiros para o sucessor e 
			// antecessor s�o iguais. Podemos comparar o objetos diretamente pois nesse caso o objeto
			// apontado � o mesmo, visto a forma como o n� � criado na fun��o createRing()
			
			System.out.println("Cai no caso do kra sozinho!");
			LookupResponsePacket lrp = new LookupResponsePacket(lp.getWantedID(), this.getID(),
					this.getIp());
			
			sendPacket(lrp,lp.getOriginIp());
			
		} else if (this.ID > lp.getWantedID() && this.getPredecessor().getID() < lp.getWantedID()) {
		
			// O ID procurado fica entre o n� e seu antecessor. Logo, retorna o
			// ID do n�.

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
			// O ID procurado fica entre o n� e seu sucessor. Logo, retorna o ID
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
			// O n� atual n�o � capaz de definir o sucessor, ent�o repassa o
			// Lookup para o pr�ximo n�.

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
			// N� acabou de ser criado e ainda n�o tem sucessor e predecessor.
			// Ao receber um LookupResponse, o n� deve atualizar seu sucessor.
			
			ChordNode sucessor = new ChordNode(lrp.getSucessorID(),lrp.getSucessorIp());
			this.setSucessor(sucessor);
			
		}else{
			System.out.printf("O sucessor do ID procurado tem o ID %d e o seu IP � ",lrp.getSucessorID(),lrp.getSucessorIp().toString());
		}

	}

	public void handleUpdate(UpdatePacket up) {
		/* Quando recebemos um update, quer dizer que um novo n� entrou
		 * na rede e ele � o novo sucessor do n� local. Portanto, devemos
		 * atualizar o ponteiro para o sucessor.
		 */
		
		ChordNode newSucessor = new ChordNode(up.getNewSucessorID(),up.getNewSucessorIP());
		this.setSucessor(newSucessor);
		Main.setSucessorUI(newSucessor);
	}

	public void handleUpdateResponse(UpdateResponsePacket urp) {

		if(urp.getStatus() == (byte) 0x00){
			/* TODO: em que situa��o haveria um erro no Update?
			 * Reenviar a mensagem em caso de erro?
			 */
		}else{
			// TODO: Acho que nao precisa fazer nada haha.
		}
		
	}
	
	public void sendPacket(ChordPacket cp, InetAddress destIP){
		
		byte[] cpArray = cp.toByteArray();
		DatagramPacket dp = new DatagramPacket(cpArray, cpArray.length, destIP, UDP_PORT);
		
		// Adicionar pacote enviado � GUI
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
			
		}catch(SocketTimeoutException se){
			//System.out.println("ChordNode.receivePacket(): Fim do timeout do socket. Parando e reiniciando.");
			packet = null;
		}catch(IOException e){
			System.out.println("ChordNode.receivePacket(): Erro na hora de receber pacotes.");
			System.exit(1);
		}
		
		return packet;
	}
	
	public static ChordNode createRing(Inet4Address ipLocal){
		
		// Gerando id aleatoriamente
		int id = (new Random()).nextInt();
		
		ChordNode local = new ChordNode(id,ipLocal,null,null);
		
		// Como o n� ainda est� sozinho na rede, sucessor e antecessor devem apontar pro pr�prio objeto
		local.setSucessor(local);
		local.setPredecessor(local);
		
		Main.setSucessorUI(local);
		Main.setPredecessorUI(local);

		/*main.Main.sucessorID.setText(Integer.toHexString(local.getID()).toUpperCase()); 
		main.Main.sucessorIp.setText(local.getIp().getHostAddress()); 
		main.Main.predecessorID.setText(Integer.toHexString(local.getID()).toUpperCase()); 
		main.Main.predecessorIp.setText(local.getIp().getHostAddress()); 
		*/
		local.start();
		
		return local;
	}
	
	public static void joinRing(Inet4Address ipLocal, Inet4Address knownHost){
		
		// Gerando id aleatoriamente
		int id = (new Random(1000)).nextInt();
		int joinAttempts = 0;
		int updateAttempts = 0;
		
		ChordNode local = new ChordNode(id,ipLocal,null,null);
		
		// Passo 1: Fazer um lookup pelo ID que acabamos de criar
		
		LookupResponsePacket lrp = null;
		
		while(lrp == null){
			byte buffer[] = new byte[21];
			
			//TODO: Eh correto colocar o ID de origem como sendo desse kra que ainda ta fora da rede?
			LookupPacket lp = new LookupPacket(local.getID(),local.getIp(),local.getID());
			local.sendPacket(lp, knownHost);
			
			DatagramPacket packet = local.receivePacket(buffer);
			
			// Se o pacote recebido for nulo significa que houve um timeout e n�o recebemos nada
			// Tentar novamente
			if(packet == null)
				continue;
			
			// Pegar o c�digo do pacote recebido
			byte code = ChordPacket.getPacketCode(packet);
			
			if(code == ChordPacket.LOOKUP_RESP_CODE){
				
				lrp = new LookupResponsePacket(buffer,packet.getOffset());
				
				if(lrp.getSucessorID() == local.getID()){
					// O ID gerado � repetido. Vamos gerar um novo.
					local.setID((new Random(10000)).nextInt());
					lrp = null;
					continue;	
				}
				
				// Setaremos o sucessor e antecessor na resposta ao Join
				
			}
			
		}
		
		// Passo 2: Enviar uma mensagem de Join para o nosso sucessor
		JoinResponsePacket jrp = null;
		
		while(jrp == null){
			
			byte buffer[] = new byte[21];
			
			JoinPacket jp = new JoinPacket(local.getID());
			
			Inet4Address sucessorIP = lrp.getSucessorIp();
			
			local.sendPacket(jp,sucessorIP);
			
			DatagramPacket packet = local.receivePacket(buffer);
			
			// Se o pacote recebido for nulo significa que houve um timeout e n�o recebemos nada
			// Tentar novamente
			if(packet == null)
				continue;
			
			// Pegar o c�digo do pacote recebido
			byte code = ChordPacket.getPacketCode(packet);
			
			if(code == ChordPacket.JOIN_RESP_CODE){
				
				joinAttempts++;
				
				jrp = new JoinResponsePacket(buffer,packet.getOffset());
				
				if(jrp.getStatus() != 0){
					// N�o houve erro no Join
					
					// Agora precisamos atualizar o sucessor e antecessor do n� local
					ChordNode suc = new ChordNode(jrp.getSucessorID(),jrp.getSucessorIP());
					ChordNode ant = new ChordNode(jrp.getPredecessorID(),jrp.getPredecessorIP());
					local.setSucessor(suc);
					local.setPredecessor(ant);
					
					// Atualizar as infos de sucessor e antecessor na UI
					main.Main.sucessorID.setText(Integer.toHexString(local.getID()).toUpperCase()); 
					main.Main.sucessorIp.setText(local.getIp().getHostAddress()); 
					main.Main.predecessorID.setText(Integer.toHexString(local.getID()).toUpperCase()); 
					main.Main.predecessorIp.setText(local.getIp().getHostAddress()); 
					
				}else{
					// Houve erro no Join. Vamos tentar novamente.
					if(joinAttempts >= 10){
						//Evita que entre em um loop infinito
						local.closeSocket();
						System.exit(0);
					}
					jrp = null;
					continue;
				}
				
			}
			
		}
		
		// Passo 3: O �ltimo passo para entrarmos na rede � mandar um Update para o nosso antecessor
		
		UpdateResponsePacket urp = null;
		
		while(urp == null){
			
			byte buffer[] = new byte[21];
			
			UpdatePacket up = new UpdatePacket(local.getID(),local.getID(),local.getIp());
			
			Inet4Address predecIP = local.getPredecessor().getIp();
			
			local.sendPacket(up,predecIP);
			
			DatagramPacket packet = local.receivePacket(buffer);
			
			// Se o pacote recebido for nulo significa que houve um timeout e n�o recebemos nada
			// Tentar novamente
			if(packet == null)
				continue;
			
			// Pegar o c�digo do pacote recebido
			byte code = ChordPacket.getPacketCode(packet);
			
			if(code == ChordPacket.UPDATE_RESP_CODE){
				
				updateAttempts++;
				
				urp = new UpdateResponsePacket(buffer,packet.getOffset());
				
				if(urp.getStatus() == 0){
					// Houve erro no Update. Vamos tentar de novo.
					if(updateAttempts >= 10){
						//Evita que entre em um loop infinito
						local.closeSocket();
						System.exit(0);
					}
					urp = null;
					continue;
				}else{
					// Estamos devidamente inseridos na rede. N�o h� mais nada a ser feito.
				}
				
			}
			
		}
		
		local.start();
	}
	
	
	public void showSentMessage(ChordPacket packet){
		Main.sentMessages.add(packet.toString());
	}
	
	public void showReceivedMessage(ChordPacket packet){
		Main.receivedMessages.add(packet.toString());
	}
	
	public void closeSocket(){
		socket.close();
	}
	

}
