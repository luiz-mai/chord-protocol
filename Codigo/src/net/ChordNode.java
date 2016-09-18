package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ThreadLocalRandom;

import main.Main;

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
	public static final int TIMEOUT = 1000; // ms

	// Variável que define se a thread do servidor deve continuar rodando
	public boolean listen = true;

	// Construtor para a criacao do no local com ID aleatorio
	public ChordNode(Inet4Address ip, ChordNode sucessor, ChordNode predecessor) {
		super();
		this.ip = ip;
		this.sucessor = sucessor;
		this.predecessor = predecessor;
		this.ID = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);

		try {
			this.socket = new DatagramSocket(UDP_PORT, ip);

			// Aqui vamos setar o timeout para o recebimento de pacotes pelo
			// socket
			// para que ele pare a cada TIMEOUT milisegundos
			this.getSocket().setSoTimeout(ChordNode.TIMEOUT);
		} catch (SocketException se) {
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

	public void generateNewRandomID() {
		this.ID = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public void run() {

		try {
			/*
			 * Setando o timeout para o socket. Ele só ficará bloqueado
			 * esperando um pacote por no máximo 500ms (0.5s).
			 */
			this.socket.setSoTimeout(500);
		} catch (SocketException se) {
			System.out.println("Erro ao setar o timeout para o socket em ChordNode.run()");
			se.printStackTrace();
		}

		while (listen) {

			// O tamanho do buffer para os pacotes é de 21 bytes pois esse é
			// tamanho do maior pacote previsto pela aplicação: um envio da
			// funcionalidade Leave.
			byte[] buffer = new byte[21];

			DatagramPacket packet = receivePacket(buffer);

			if (packet == null)
				continue;

			// Pegando o codigo do pacote (primeiro byte)
			int offset = packet.getOffset();

			byte code = buffer[offset];

			Inet4Address incomingIp = (Inet4Address) packet.getAddress();

			switch (code) {
			// Join
			case ChordPacket.JOIN_CODE:
				JoinPacket jp = new JoinPacket(buffer, offset);
				Main.showReceivedMessage(jp, incomingIp);
				handleJoin(jp, incomingIp);
				break;
			// Join Response
			case ChordPacket.JOIN_RESP_CODE:
				JoinResponsePacket jrp = new JoinResponsePacket(buffer, offset);
				Main.showReceivedMessage(jrp, incomingIp);
				handleJoinResponse(jrp);
				break;
			// Leave
			case ChordPacket.LEAVE_CODE:
				LeavePacket lp = new LeavePacket(buffer, offset);
				Main.showReceivedMessage(lp, incomingIp);
				handleLeave(lp);
				break;
			// LeaveResponse
			case ChordPacket.LEAVE_RESP_CODE:
				LeaveResponsePacket lrp = new LeaveResponsePacket(buffer, offset);
				Main.showReceivedMessage(lrp, incomingIp);
				handleLeaveResponse(lrp);
				break;
			// Lookup
			case ChordPacket.LOOKUP_CODE:
				LookupPacket lkp = new LookupPacket(buffer, offset);
				Main.showReceivedMessage(lkp, incomingIp);
				handleLookup(lkp);
				break;
			// LookupResponse
			case ChordPacket.LOOKUP_RESP_CODE:
				LookupResponsePacket lkrp = new LookupResponsePacket(buffer, offset);
				Main.showReceivedMessage(lkrp, incomingIp);
				handleLookupResponse(lkrp);
				break;
			// Update
			case ChordPacket.UPDATE_CODE:
				UpdatePacket up = new UpdatePacket(buffer, offset);
				Main.showReceivedMessage(up, incomingIp);
				handleUpdate(up, incomingIp);
				break;
			// UpdateResponse
			case ChordPacket.UPDATE_RESP_CODE:
				UpdateResponsePacket urp = new UpdateResponsePacket(buffer, offset);
				Main.showReceivedMessage(urp, incomingIp);
				handleUpdateResponse(urp);
				break;
			default:
				// Ignore
			}

		}
	}

	public void handleJoin(JoinPacket jp, InetAddress incomingIp) {

		byte confirmacaoErro;

		// Precisamos salvar uma referëncia para o antigo predecessor para não
		// perdê-la na atualização
		ChordNode oldPredecessor = this.predecessor;

		if (this.ID == jp.getNewNodeID()) {
			// O nó que enviou o pacote tem o mesmo ID que o nó local. Responder
			// com código de erro.
			confirmacaoErro = 0;
		} else {
			// O novo nó pode ser inserido com segurança.
			confirmacaoErro = 0x01;

			// Vamos atualizar o predecessor do nó local
			ChordNode newNode = new ChordNode(jp.getNewNodeID(), incomingIp);
			this.setPredecessor(newNode);
			Main.setPredecessorUI(newNode);
		}

		/*
		 * Decisão de projeto: Numa situação de erro, não foi especificado o que
		 * deveria ser retornado nos demais campos de JoinResponse. Por
		 * simplicidade, escolhemos retornar os mesmos valores que a situação
		 * sem erro. Cabe ao destinatário verificar o campos de confirmação/erro
		 * para determinar a validade das informações contidas no pacote.
		 */

		JoinResponsePacket jrp = new JoinResponsePacket(confirmacaoErro, this.ID, this.ip, oldPredecessor.getID(),
				oldPredecessor.getIp());

		sendPacket(jrp, incomingIp);
	}

	public void handleJoinResponse(JoinResponsePacket jrp) {
		/*
		 * A única situação na qual recebemos um JoinResponse é quando queremos
		 * entrar na rede e enviamos um Join para um nó que já pertença a ela.
		 * Esse nó então responde a solicitação com um JoinResponse, contendo as
		 * informações sobre antecessor e sucessor.
		 */

		if (jrp.getStatus() == (byte) 0x00) {
			/*
			 * Tratamos esse caso dentro da função JoinRing
			 */
		} else {
			// Join sem erro.

			/*
			 * Só atualizar os ponteiros de sucessor e predecessor se eles já
			 * não estiverem setados. Temo que fazer isso pq essa JoinResponse
			 * pode ter sido enviada por engano e caso executemos o procedimento
			 * padrão para tratá-la podemos bagunçar a rede toda.
			 */

			if (this.getSucessor() == null && this.getPredecessor() == null) {
				ChordNode newSucessor = new ChordNode(jrp.getSucessorID(), jrp.getSucessorIP());
				ChordNode newPredecessor = new ChordNode(jrp.getPredecessorID(), jrp.getPredecessorIP());

				this.setSucessor(newSucessor);
				this.setPredecessor(newPredecessor);

				// Agora precisamos avisar o nosso antecessor que entramos na
				// rede
				// Para isso mandamos um Update

				// Os dois primeiros campos vão ser iguais mesmo?
				UpdatePacket up = new UpdatePacket(this.getID(), this.getID(), this.getIp());
				sendPacket(up, this.getPredecessor().getIp());
			}
			// E o caso de apenas um deles ser nulo? Precisamos tratar?
		}
	}

	public void handleLeave(LeavePacket lp) {
		/*
		 * Recebemos um Leave quando ou o nosso antecessor ou sucessor desejam
		 * sair da rede. Nesse caso, precisamos checar qual dos dois é que está
		 * mandando essa mensagem e atualizar o ponteiro correspondentemente.
		 */

		// ID do nó que está saindo da rede.
		int senderID = lp.getExitID();

		if (senderID == this.getSucessor().getID()) {
			// É o sucessor quem está saindo da rede.

			// Precisamos salvar o IP do nó que está saindo da rede para
			// podermos enviar a resposta
			Inet4Address oldSucessorIP = this.getSucessor().getIp();

			ChordNode newSucessor = new ChordNode(lp.getExitSucID(), lp.getExitSucIP());
			this.setSucessor(newSucessor);
			Main.setSucessorUI(this.getSucessor());

			/*
			 * Tendo atualizado o sucessor, precisamos enviar uma mensagem de
			 * confirmação para o nó que está saindo da rede.
			 */

			sendPacket(new LeaveResponsePacket(this.getID()), oldSucessorIP);

		} else if (senderID == this.getPredecessor().getID()) {
			// É o predecessor quem está saindo da rede.

			// Precisamos salvar o IP do nó que está saindo da rede para
			// podermos enviar a resposta
			Inet4Address oldPredecIP = this.getPredecessor().getIp();

			ChordNode newPredecessor = new ChordNode(lp.getExitPredecID(), lp.getExitPredecIP());
			this.setPredecessor(newPredecessor);
			Main.setPredecessorUI(this.getPredecessor());
			/*
			 * Tendo atualizado o predecessor, precisamos enviar uma mensagem de
			 * confirmação para o nó que está saindo da rede.
			 */

			sendPacket(new LeaveResponsePacket(this.getID()), oldPredecIP);

		} else {
			// O ID do pacote que enviou a mensagem não corresponde nem ao
			// sucessor nem ao
			// predecessor. Deve ter sido enviado por engano.

			// Ignorar
		}
	}

	public void handleLeaveResponse(LeaveResponsePacket lrp) {
		/*
		 * Não fazemos nada nesse caso pois lidamos com ele separadamente dentro
		 * da função leaveRing
		 */

		int senderID = lrp.getOriginID();

		if (senderID == this.getSucessor().getID()) {
			// Sucessor OK
		} else if (senderID == this.getPredecessor().getID()) {
			// Predecessor OK
		} else {
			// Ignorar
		}
	}

	public void handleLookup(LookupPacket lp) {

		int localID = this.getID();
		int sucID = this.getSucessor().getID();
		int predecID = this.getPredecessor().getID();
		int wantedID = lp.getWantedID();

		ChordNode wantedSuc = null;

		if (Integer.compareUnsigned(localID, wantedID) == 0) {
			// O ID procurado é igual ao ID do nó local
			wantedSuc = this;

		} else if (Integer.compareUnsigned(sucID, wantedID) == 0) {
			// O ID procurado é igual ao ID do nó sucessor
			wantedSuc = this.getSucessor();

		} else if (Integer.compareUnsigned(predecID, wantedID) == 0) {
			// O ID procurado é igual ao ID do nó predecessor
			wantedSuc = this.getPredecessor();

		} else if (Integer.compareUnsigned(localID, sucID) == 0 && Integer.compareUnsigned(sucID, predecID) == 0) {
			// Nesse caso o nó atual está sozinho na rede, entao o sucessor de
			// qualquer ID procurado eh o ID local
			wantedSuc = this;

		} else if (Integer.compareUnsigned(localID, sucID) != 0 && Integer.compareUnsigned(sucID, predecID) == 0) {
			// Aqui, a rede tem dois nós.
			// Na situação de 2 nós, há 6 possibilidades, divididas em dois
			// subcasos. Sejam L o noh local e M o outro noh da rede e P o ID
			// procurado.
			//
			// Subcaso 1: L < M
			// a) P eh o menor de todos (P < L): P -> L -> M
			// b) P esta entre os dois (L < P < M): L -> P -> M
			// c) P eh o maior de todos (P > M): L -> M -> P
			//
			// Subcaso 2: L > M
			// a) P eh o menor de todos (P < M): P -> M -> L
			// b) P esta entre os dois (M < P < L): M -> P -> L
			// c) P eh o maior de todos (P > L) : M -> L -> P

			if (Integer.compareUnsigned(localID, sucID) < 0) {
				// Subcaso 1: L < M

				if (Integer.compareUnsigned(wantedID, localID) > 0 && Integer.compareUnsigned(wantedID, sucID) < 0) {
					// Caso 1b
					wantedSuc = this.getSucessor();
				} else {
					// Casos 1a e 1c (nos dois o sucessor eh L)
					wantedSuc = this;
				}
			} else {
				// Subcaso 2: L > M
				if (Integer.compareUnsigned(wantedID, sucID) > 0 && Integer.compareUnsigned(wantedID, localID) < 0) {
					// Caso 2b
					wantedSuc = this;
				} else {
					// Casos 2a e 2c (nos dois o sucessor eh M)
					wantedSuc = this.getSucessor();
				}
			}
		} else {
			/*
			 * Esse eh o caso mais geral, para uma rede com 3 ou mais nós. Nessa
			 * situação existem 3 subcasos com 3 ramificações cada: A =
			 * antecessor P = procurado L = local S = sucessor
			 * 
			 * OBS: casos subsequentes são mutuamente exclusivos, i.e. no caso b
			 * subentende-se que o caso a não foi atendido (estrutura if-else)
			 * 
			 * Subcaso 1: O noh local eh o maior da rede (L > S) a) P > L ou P <
			 * S : P deve ser adicionado depois de L A -> L -> P -> S b) P > A :
			 * P deve ser adicionado antes de L A -> P -> L -> S c) Nao temos
			 * informacao suficiente, encaminhar a consulta para S.
			 * 
			 * Subcaso 2: O noh local eh o menor da rede (A > L) a) P > A ou P <
			 * L : P deve ser adicionado antes de L A -> P -> L -> S b) P < S: P
			 * deve ser adicionado depois de L A -> L -> P -> S c) Nao temos
			 * informacao suficiente, encaminhar a consulta para S.
			 * 
			 * Subcaso 3: L eh um noh intermediario a) P > S ou P < A Nao temos
			 * informacao suficiente, encaminhar a consulta para S. b) P < L: P
			 * deve ser adicionado antes de L A -> P -> L -> S c) P deve ser
			 * adicionado depois de L (pois nesse caso P > L) A -> L -> P -> S
			 */

			if (Integer.compareUnsigned(localID, sucID) > 0) {
				// Subcaso 1: L eh o maior da rede (L > S)

				if (Integer.compareUnsigned(wantedID, localID) > 0 || Integer.compareUnsigned(wantedID, sucID) < 0) {
					// Caso 1a
					wantedSuc = this.getSucessor();

				} else if (Integer.compareUnsigned(wantedID, predecID) > 0) {
					// Caso 1b
					wantedSuc = this;

				} else {
					// Caso 1c
					// Temos que encaminhar o pedido, entao simplesmente
					// deixamos o sucessor nulo
				}
			} else if (Integer.compareUnsigned(predecID, localID) > 0) {
				// Subcaso 2: L eh o menor da rede (L < A)

				if (Integer.compareUnsigned(wantedID, predecID) > 0 || Integer.compareUnsigned(wantedID, localID) < 0) {
					// Caso 2a
					wantedSuc = this;
				} else if (Integer.compareUnsigned(wantedID, sucID) < 0) {
					// Caso 2b
					wantedSuc = this.getSucessor();
				} else {
					// Caso 2c
					// Temos que encaminhar o pedido, entao simplesmente
					// deixamos o sucessor nulo
				}
			} else {
				// Subcaso 3: L eh um noh intermediario (A < L < S)

				if (Integer.compareUnsigned(wantedID, sucID) > 0 || Integer.compareUnsigned(wantedID, predecID) < 0) {
					// Caso 3a
					// Temos que encaminhar o pedido, entao simplesmente
					// deixamos o sucessor nulo
				} else if (Integer.compareUnsigned(wantedID, localID) < 0) {
					// Caso 3b
					wantedSuc = this;
				} else {
					// Caso 3c
					wantedSuc = this.getSucessor();
				}
			}
		}

		if (wantedSuc != null) {
			// Conseguimos definir o sucessor
			LookupResponsePacket lrp = new LookupResponsePacket(wantedID, wantedSuc.getID(), wantedSuc.getIp());
			sendPacket(lrp, lp.getOriginIp());
		} else {
			// Nao foi possivel definir o sucessor, temos que encaminhar a
			// consulta para o sucessor
			sendPacket(lp, this.getSucessor().getIp());
		}
	}

	public void handleLookupResponse(LookupResponsePacket lrp) {

		if (this.getSucessor() == null || this.getPredecessor() == null) {
			// Nó acabou de ser criado e ainda não tem sucessor e predecessor.
			// Ao receber um LookupResponse, o nó deve atualizar seu sucessor.

			ChordNode sucessor = new ChordNode(lrp.getSucessorID(), lrp.getSucessorIp());
			this.setSucessor(sucessor);

			Main.setSucessorUI(sucessor);

		} else {
			// No caso de recebermos a resposta apenas imprimimos na tela o pacote
		}

	}

	public void handleUpdate(UpdatePacket up, Inet4Address incomingIP) {
		/*
		 * Quando recebemos um update, quer dizer que um novo nó entrou na rede
		 * e ele é o novo sucessor do nó local. Portanto, devemos atualizar o
		 * ponteiro para o sucessor.
		 */

		ChordNode newSucessor = new ChordNode(up.getNewSucessorID(), up.getNewSucessorIP());
		this.setSucessor(newSucessor);
		Main.setSucessorUI(newSucessor);

		UpdateResponsePacket urp = new UpdateResponsePacket((byte) 1, this.getID());
		sendPacket(urp, incomingIP);
	}

	public void handleUpdateResponse(UpdateResponsePacket urp) {
		/*
		 * Tratamos esse caso dentro de JoinRing
		 */
		
		if (urp.getStatus() == (byte) 0x00) {
		} else {
		}

	}

	public void sendPacket(ChordPacket cp, InetAddress destIP) {

		byte[] cpArray = cp.toByteArray();
		DatagramPacket dp = new DatagramPacket(cpArray, cpArray.length, destIP, UDP_PORT);

		// Adicionar pacote enviado à GUI
		Main.showSentMessage(cp, (Inet4Address) destIP);

		try {
			socket.send(dp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DatagramPacket receivePacket(byte[] buffer) {

		DatagramPacket packet = null;

		try {

			Inet4Address incomingIp = (Inet4Address) Inet4Address.getByName("0.0.0.0");

			// Esperar o recebimento de um pacote
			packet = new DatagramPacket(buffer, buffer.length, incomingIp, UDP_PORT);
			socket.receive(packet);

		} catch (SocketTimeoutException se) {
			// System.out.println("ChordNode.receivePacket(): Fim do timeout do
			// socket. Parando e reiniciando.");
			packet = null;
		} catch (IOException e) {
			System.out.println("ChordNode.receivePacket(): Erro na hora de receber pacotes.");
			System.exit(1);
		}

		return packet;
	}

	public static ChordNode createRing(Inet4Address ipLocal) {

		ChordNode local = new ChordNode(ipLocal, null, null);

		// Como o nó ainda está sozinho na rede, sucessor e antecessor devem
		// apontar pro próprio objeto
		local.setSucessor(local);
		local.setPredecessor(local);

		// Atualizar infos na UI
		Main.setSucessorUI(local);
		Main.setPredecessorUI(local);
		Main.setMyselfUI(local);

		local.start();

		return local;
	}

	public static void joinRing(Inet4Address ipLocal, Inet4Address knownHost) {

		// Gerando id aleatoriamente
		int joinAttempts = 0;
		int updateAttempts = 0;
		ChordNode local = new ChordNode(ipLocal, null, null);

		// Setar o timeout do socket para 0 nesse inicio pois tudo eh sequencial
		try {
			local.getSocket().setSoTimeout(0);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		// Passo 1: Fazer um lookup pelo ID que acabamos de criar

		LookupResponsePacket lrp = null;

		while (lrp == null) {
			byte buffer[] = new byte[21];

			LookupPacket lp = new LookupPacket(local.getID(), local.getIp(), local.getID());
			local.sendPacket(lp, knownHost);

			DatagramPacket packet = local.receivePacket(buffer);

			InetAddress incomingIp = packet.getAddress();

			// Pegar o código do pacote recebido
			byte code = ChordPacket.getPacketCode(packet);

			if (code == ChordPacket.LOOKUP_RESP_CODE) {

				lrp = new LookupResponsePacket(buffer, packet.getOffset());
				Main.showReceivedMessage(lrp, incomingIp);

				if (lrp.getSucessorID() == local.getID()) {
					// O ID gerado é repetido. Vamos gerar um novo.
					local.generateNewRandomID();
					lrp = null;
					continue;
				}

				// Setaremos o sucessor e antecessor na resposta ao Join

			}

		}

		// Atualizar info do no local na UI
		Main.setMyselfUI(local);

		// Passo 2: Enviar uma mensagem de Join para o nosso sucessor
		JoinResponsePacket jrp = null;

		while (jrp == null) {

			byte buffer[] = new byte[21];

			JoinPacket jp = new JoinPacket(local.getID());

			Inet4Address sucessorIP = lrp.getSucessorIp();

			local.sendPacket(jp, sucessorIP);

			DatagramPacket packet = local.receivePacket(buffer);

			// Pegar o código do pacote recebido
			byte code = ChordPacket.getPacketCode(packet);

			if (code == ChordPacket.JOIN_RESP_CODE) {

				joinAttempts++;

				jrp = new JoinResponsePacket(buffer, packet.getOffset());
				Main.showReceivedMessage(jrp, packet.getAddress());

				if (jrp.getStatus() != 0) {
					// Não houve erro no Join

					// Agora precisamos atualizar o sucessor e antecessor do nó
					// local
					ChordNode suc = new ChordNode(jrp.getSucessorID(), jrp.getSucessorIP());
					ChordNode ant = new ChordNode(jrp.getPredecessorID(), jrp.getPredecessorIP());
					local.setSucessor(suc);
					local.setPredecessor(ant);

					// Atualizar as infos de sucessor e antecessor na UI
					Main.setSucessorUI(suc);
					Main.setPredecessorUI(ant);

				} else {
					// Houve erro no Join. Vamos tentar novamente.
					if (joinAttempts >= 10) {
						// Evita que entre em um loop infinito
						local.closeSocket();
						System.exit(0);
					}
					jrp = null;
					continue;
				}

			}

		}

		// Passo 3: O último passo para entrarmos na rede é mandar um Update
		// para o nosso antecessor

		UpdateResponsePacket urp = null;

		while (urp == null) {

			byte buffer[] = new byte[21];

			UpdatePacket up = new UpdatePacket(local.getID(), local.getID(), local.getIp());

			Inet4Address predecIP = local.getPredecessor().getIp();

			local.sendPacket(up, predecIP);

			DatagramPacket packet = local.receivePacket(buffer);

			// Pegar o código do pacote recebido
			byte code = ChordPacket.getPacketCode(packet);

			if (code == ChordPacket.UPDATE_RESP_CODE) {

				updateAttempts++;

				urp = new UpdateResponsePacket(buffer, packet.getOffset());
				Main.showReceivedMessage(urp, packet.getAddress());

				if (urp.getStatus() == 0) {
					// Houve erro no Update. Vamos tentar de novo.
					if (updateAttempts >= 10) {
						// Evita que entre em um loop infinito
						local.closeSocket();
						System.exit(0);
					}
					urp = null;
					continue;
				} else {
					// Ocorreu tudo bem.

					try {
						// Vamos estabelecer um timeout para o loop principal
						// nao
						// ficar travado enquanto nao recebe pacotes
						local.getSocket().setSoTimeout(ChordNode.TIMEOUT);
					} catch (SocketException e) {
						e.printStackTrace();
					}

					Main.localNode = local;
					local.start();
				}

			}

		}

	}

	public static void leaveRing(ChordNode localNode) {

		LeaveResponsePacket sucLeaveResponsePacket = null;
		LeaveResponsePacket predLeaveResponsePacket = null;
		LeavePacket lp = new LeavePacket(localNode.getID(), localNode.getSucessor().getID(),
				localNode.getSucessor().getIp(), localNode.getPredecessor().getID(),
				localNode.getPredecessor().getIp());

		// flag para sinalizar se o pacote ja foi enviado, para que possamos
		// botar o envio dentro do while e nao enviar o mesmo pacote multiplas
		// vezes
		boolean alreadySent = false;

		// Vamos setar o timeout do receive para infinito para evitar perder o
		// pacote de resposta
		try {
			localNode.socket.setSoTimeout(0);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		while (sucLeaveResponsePacket == null) {

			byte buffer[] = new byte[21];

			if (!alreadySent) {
				localNode.sendPacket(lp, localNode.getSucessor().getIp());
				alreadySent = true;
			}

			DatagramPacket packet = localNode.receivePacket(buffer);

			// Pegar o código do pacote recebido
			byte code = ChordPacket.getPacketCode(packet);

			if (code == ChordPacket.LEAVE_RESP_CODE) {

				sucLeaveResponsePacket = new LeaveResponsePacket(buffer, packet.getOffset());

				// Checar se foi o remetente que esparavamos.
				if (sucLeaveResponsePacket.getOriginID() == localNode.getSucessor().getID())
					break;
				else
					sucLeaveResponsePacket = null;

			}
		}

		alreadySent = false;

		while (predLeaveResponsePacket == null) {

			byte buffer[] = new byte[21];

			if (!alreadySent) {
				alreadySent = true;
				localNode.sendPacket(lp, localNode.getPredecessor().getIp());
			}

			DatagramPacket packet = localNode.receivePacket(buffer);

			// Pegar o código do pacote recebido
			byte code = ChordPacket.getPacketCode(packet);

			if (code == ChordPacket.LEAVE_RESP_CODE) {

				predLeaveResponsePacket = new LeaveResponsePacket(buffer, packet.getOffset());

				// Checar se foi o remetente que esparavamos.
				if (predLeaveResponsePacket.getOriginID() == localNode.getPredecessor().getID())
					break;
				else
					predLeaveResponsePacket = null;

			}
		}

		localNode.closeSocket();
	}

	public void closeSocket() {
		socket.close();
	}

}
