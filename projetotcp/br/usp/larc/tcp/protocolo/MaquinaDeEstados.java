package br.usp.larc.tcp.protocolo;

/*
 * @(#)MaquinaDeEstados.java	1.0 18/08/2006
 *
 * Copyleft (L) 2006 Laboratrio de Arquitetura e Redes de Computadores
 * Escola Politcnica da Universidade de So Paulo
 *
 */

import java.util.Timer;

import br.usp.larc.tcp.aplicacao.MaquinaDeEstadosFrame;
import br.usp.larc.tcp.aplicacao.MonitorFrame;
import br.usp.larc.tcp.ipsimulada.IpSimulada;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

/** 
 * Classe que representa a Mquina de Estado do seu Protocolo (que pode ter n).
 * Detalhes e dicas de implementao podem ser consultadas nas Apostilas.
 *
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a 
 * criatividade na implementao do seu projeto.
 *  
 *
 * @author	Laboratrio de Arquitetura e Redes de Computadores
 * @version	1.0 Agosto 2003
 */
public class MaquinaDeEstados {
    
    /** 
     * Atributo que representa o frame associado a Mquina de Estados
     */
    private MaquinaDeEstadosFrame meFrame;

    /** 
     * Atributo que representa o monitor associado a Mquina de Estados
     */
    private Monitor monitor;

    /** 
     * Atributo que representa o id da Conexo associado a Mquina de Estados
     */
    private int idConexao;

    /** 
     * IP Simulado Local associado a Mquina de Estados
     */
    private String ipSimuladoLocal;

    /** 
     * IP Simulado Destino associado a Maquina de Estados
     */
    private String ipSimuladoDestino;

    /** 
     * Nome da estao destino associado a Mquina de Estados
     */
    private String nomeEstacaoDestino;
    
    /** 
     * Porta TCP local associada a Mquina de Estados
     */
    private int portaLocal;

    /** 
     * Porta TCP remota associada a Mquina de Estados
     */
    private int portaDestino;

    /** 
     * O estado atual da mquina de conexo e desconexo
     */
    private String estadoMEConAtual;
    
    /**
     * Estado atual da maquina de transmissao
     */
    private String estadoMETX = TCPIF.IDLE;
    
    /**
     * Estado atual da maquina de recepcao
     */
    private String estadoMERX = TCPIF.RECEIVING;
     
    /** 
     * Constante que guarda o nmero de retransmisses de um segmeto TCP com
     * timestamp expirado.
     */
    // private static final int numMaxRetransmissoes = ProtocoloTCP.MAX_RETRANSMISSOES;
    // nessa fase do projeto vamos considerar o nmero mximo de retransmisses igual a 3
    private static final int numMaxRetransmissoes = 3;
    
    /**
     * Atributo que guarda o nmero de retransmisses de pacote que foram realizadas
     */
    private int numRetransmissoes = 0; 
    
    /** 
     * Constante que guarda o tempo (em milisegundos) para expirar o timestamp
     * de um segmento TCP enviado.
     */
    //private static int tempoTimeout = ProtocoloTCP.T_ESTOURO_RETRANSMISSOES;
    // estamos usando esse valor de 5000 milisegundos para demonstrar o funcionamento do timeout, pois a contante
    // ProtocoloTCP.T_ESTOURO_RETRANSMISSOES  um valor muito baixo 
    private static int tempoTimeout = 5000;
    
    
    /**
     * Objeto Timer que faz a contagem do tempo para ser verificado o tempo de timeout
     */
    private Timer oTimer = null;
    
    /**
     * Objeto que  a tarefa a ser executado quando o tempo de timeout for excedido
     */
    private TimeOutTask oTimeOutTask = null;
    
    /** 
     * Segmento TCP para ser enviado
     */
    private PacoteTCP pacoteDeEnvio;
    
    /** 
     * Segmento TCP recebido
     */
    private PacoteTCP pacoteRecebido;
    
    /**
     * Próximo numero de sequência a ser enviado. Usado no controle de sequência.
     */
    private long proxNumSeq = 0;
    
    /**
     * Numero de sequência esperado (número do ACK esperado). Usado no controle de sequência.
     */
    private int numAck = 0;
    
    /**
     * Ultimo número de sequência não confirmado. Usado pelo transmissor no controle de sequência para guardar o 
     * último número de sequência transmitido e não confirmado pelo receptor.
     */
    private int numSeqNaoConfirmado = 0;
    
    /**
     * Tamanho da janela de recepcao. O usuário pode mudar manualmente o valor dessa janela, mas por default vamos 
     * utilizar o valor de 100 bytes
     */
    private int tamJanelaRecepcao = 12;
    
    /**
     * Tamanho da janela de recepcao do receptor remoto. Por default, coloquei 100 bytes, mas na verdade o receptor
     * avisa o valor dessa varivavel atraves do campo janela de recepcao do cabecalho TCP
     */
    private int tamJanelaRemota = this.tamJanelaRecepcao;
    
    /**
     * Maximum Segment Size. Coloquei esse valor apenar para haver segmentação na hora de transmitir dados. Mas
     * poderia ser outro valors
     */
    private int MSS = 34;
    
    /**
     * Buffer de transmissão de dados
     */
    private byte[] bufferTX = new byte[2 * this.tamJanelaRemota]; 
    
    /**
     * Buffer de recepção de dados
     */
//    private byte[] bufferRX = new byte[2 * this.tamJanelaRecepcao];
    private byte[] bufferRX = new byte[2 * this.tamJanelaRecepcao];
    
    /**
     * Ponteiro para o ultimo byte do buffer de transmissao que foi enviado
     */
    private int numSeqTX = -1;
    
    /**
     * Ponteiro para o último byte do buffer de trasmissao que recebeu um ACK (que foi reconhecido)
     */
    private int numSeqTXReconhecida = -1;
    
    /**
     * Guarda o valor do ultimo byte transmitido na ultima transmissao. Nao tem aplicacao direta no
     * protocolo, e apenas uma variavel auxiliar no processo de transmissao de dados
     */
    private int numSeqTXAuxiliar = 0;
    
    private int numSeqRXAuxiliarAck = 0;
    
    /**
     * Número de bytes que foram transmitidos do buffer de transmissao
     */
    private int numBytesTransmitidos = 0;
    
    /**
     * Numero do total de bytes da mensagem a ser transmitida
     */
    private int tamanhoTotalMensagem;
    /**
     * Numero de bytes da mensagem de transmissao que ja foram transmitidos para o buffer.
     * Serve como ponteiro para a cadeia de bytes da mensagem
     */
    private int numBytesBufferrizadosTX = 0;    
    
    
    private int numBytesBufferrizadosRX = 0;
    
//    /**
//     * Dados a serem transmitidos em um segmento
//     */
//    private byte[] dadosSegmentoTX;
    
    /**
     * Numero do ultimo byte da cadeia de dados lido do buffer para o a camada de aplicacao.
     * Usado pela maquina de recepcao de dados
     */
    private int ultimoByteLido = -1;
    
    /**
     * Numero do ultimo bytena cadeia de dados que chegou da rede e foi colocado no buffer de recepcao.
     * Usado pela maquina de recepcao de dados
     */
    private int ultimoByteRecebido = -1;
    
    /**
     * Guarda o valor do ultimo byte recebido na ultima recepcao. Nao tem aplicacao direta no
     * protocolo, e apenas uma variavel auxiliar no processo de recepcao de dados
     */
    private int numSeqRXAuxiliar = 0;
    
    private int janelaTransmissao = 0;
    
    private int inicioBufferRX = -1;
    private int fimBufferRX = 0;

    /**
     * Indica se o timeout que ocorreu é de conexao ou de transmissao
     * Se true, o timeout é de transmissao
     * Se false, o timeout é de conexão
     */
	private boolean timeOutTX;
    
    /** Construtor da classe MaquinaDeEstados */
    public MaquinaDeEstados() {
    }
    
    /** Construtor da classe MaquinaDeEstados */
    public MaquinaDeEstados(Monitor _monitor, int _porta, int _idConexao) {
        this.monitor = _monitor;
        this.estadoMEConAtual = ProtocoloTCP.CLOSED;
        this.ipSimuladoLocal = _monitor.getIpSimuladoLocal();
        this.portaLocal = _porta;
        this.idConexao = _idConexao;
        this.meFrame = new MaquinaDeEstadosFrame(this);
        this.meFrame.atualizaInfoConexao(this.estadoMEConAtual, 
            this.getIpSimuladoLocalBytePonto(),
            Integer.toString(this.portaLocal),  "null",  "null");
        meFrame.setEstadoTX(TCPIF.IDLE);
        meFrame.setJanela(this.tamJanelaRecepcao);
        meFrame.setSegmento(this.MSS);
    }

    /** 
     * Método que recebe primitivas  e executa as operaes para atender a ao.
     * As primitivas esto definidas na interface TCPIF. 
     *
     * @param _primitiva A primitiva que enviada.
     * @param args[] Um array de argumentos que voc pode receber adicionalmente
     * @exception Exception  Caso ocorra algum erro ou exceo, lana (throw) 
     * para quem chamou o mtodo.
     */
    public void recebePrimitiva(byte _primitiva, String args[])
        throws Exception {
       
    	// guarda a primitiva 
    	byte primitiva = _primitiva;
    	// cria um pacote TCP
    	PacoteTCP pacoteTCP = new PacoteTCP();
    	
    	MaquinaDeEstadosFrame mef = getMeFrame();
    	// se for Active Open
    	if(primitiva == TCPIF.P_ACTIVEOPEN)
    	{
    		// se estiver no estado CLOSED
    		if(this.estadoMEConAtual.equals(TCPIF.CLOSED))
    		{
    			// poe um SYN no byte de controle
    			pacoteTCP.setControle(TCPIF.S_SYN);
    			
    			// Atualiza o frame
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_SYN, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "Act. Open" , "->", segmento);
    			
    			// muda para o estado SYNSENT
    			estadoMEConAtual = TCPIF.SYNSENT;
    			
    			// Atualiza o frame
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , ".", ".");
    			
    			// envia o pacote
    			enviaSegmentoTCP(pacoteTCP);
    		}
    	}
    	// se for Passive Open
    	else if(primitiva == TCPIF.P_PASSIVEOPEN)
    	{
    		// se estiver no estado CLOSED
    		if(estadoMEConAtual.equals(TCPIF.CLOSED))
    		{
	    		// Atualiza o frame
	    		mef.atualizaDadosEstado(estadoMEConAtual, "Pass. Open" , ".", ".");
				// muda para o estado LISTEN
				estadoMEConAtual = TCPIF.LISTEN;
				//Atualiza o frame
	    		mef.atualizaDadosEstado(estadoMEConAtual, "Open Id" , ".", ".");
				// envia a primitiva OPENID
				this.enviaPrimitiva(TCPIF.P_OPENID, null);
    		}
    	}
    	// se for Close
    	else if(primitiva == TCPIF.P_CLOSE)
    	{
    		mef.atualizaDadosEstado(estadoMEConAtual, "Close" , ".", ".");
//    		// se estiver no estado CLOSED
//    		if(estadoMEConAtual.equals(TCPIF.CLOSED))
//    		{
//    			// muda para o estado CLOSED
//    			estadoMEConAtual = TCPIF.CLOSED;
//    			mef.atualizaDadosEstado(estadoMEConAtual, "." , ".", ".");
//    		}
    		// se estiver no estado SYNRCVD
    		if(estadoMEConAtual.equals(TCPIF.SYNRCVD))
    		{
    			// muda para o estado FINWAIT_1
    			estadoMEConAtual = TCPIF.FINWAIT_1;
    			// envia um FIN
    			pacoteTCP.setControle(TCPIF.S_FIN);
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_FIN, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
    			enviaSegmentoTCP(pacoteTCP);
    		}
    		// se estiver no estado SYNSENT
    		else if(estadoMEConAtual.equals(TCPIF.SYNSENT))
    		{
    			// muda para o estado CLOSED
    			estadoMEConAtual = TCPIF.CLOSED;
    			// envia um RST
    			pacoteTCP.setControle(TCPIF.S_RST);
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_RST, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
    			enviaSegmentoTCP(pacoteTCP);
    		}
    		// se estiver no estado ESTABLISHED
    		else if(estadoMEConAtual.equals(TCPIF.ESTABLISHED))
    		{
    			// muda para o estado FINWAIT_1
    			estadoMEConAtual = TCPIF.FINWAIT_1;
    			// envia um FIN
    			pacoteTCP.setControle(TCPIF.S_FIN);
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_FIN, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
    			enviaSegmentoTCP(pacoteTCP);
    		}
    		// se estiver no estado CLOSEWAIT
    		else if(estadoMEConAtual.equals(TCPIF.CLOSEWAIT))
    		{
    			// muda para o estado LAST_ACK
    			estadoMEConAtual = TCPIF.LAST_ACK;
    			// envia um FIN
    			pacoteTCP.setControle(TCPIF.S_FIN);
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_FIN, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
    			enviaSegmentoTCP(pacoteTCP);
    		}
    		// se estiver no estado LISTEN
    		else if(estadoMEConAtual.equals(TCPIF.LISTEN))
    		{
    			// muda para o estado CLOSED
    			estadoMEConAtual = TCPIF.CLOSED;
   			
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , ".", ".");
    		}
    	}
    	//se for Send
    	else if(primitiva == TCPIF.P_SEND)
    	{
    		// Atualiza o frame
    		mef.atualizaDadosEstado(estadoMEConAtual, "Send" , ".", ".");
    		
    		// se estiver no estado LISTEN
    		if(estadoMEConAtual.equals(TCPIF.LISTEN))
    		{
    			// muda para o estado SYNSENT
    			estadoMEConAtual = TCPIF.SYNSENT;
    			// pe um SYN no byte de controle
    			pacoteTCP.setControle(TCPIF.S_SYN);
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_SYN, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
    			// envia o pacote
    			enviaSegmentoTCP(pacoteTCP);
    		}
    		else if(estadoMEConAtual.equals(TCPIF.ESTABLISHED))
    		{
    			// seta aqui o tamanho da mensagem que esta sendo enviada
    			if(this.estadoMETX.equals(TCPIF.IDLE))
    			{
    				this.estadoMETX = TCPIF.TRASMITTING;
    				this.tamanhoTotalMensagem = this.getMeFrame().getDados().getBytes().length;
    				this.trataTX();
    			}
    		}
    	}
    	// se for Timeout
    	else if(primitiva == TCPIF.P_TIMEOUT)
    	{    		
	    	if(this.estadoMEConAtual.equals(TCPIF.TIMEWAIT))
   	    	{
	    		// envia primitiva TERMINATE
	    		enviaPrimitiva(TCPIF.P_TERMINATE, null);
	    		// vai para o estado CLOSED
	    		estadoMEConAtual = TCPIF.CLOSED;
	    		mef.atualizaDadosEstado(estadoMEConAtual, "Terminate" , ".", ".");
   	    	}
	    	else if(this.numRetransmissoes < numMaxRetransmissoes)
			{
   	    		if(this.estadoMEConAtual.equals(TCPIF.SYNSENT))
   	    		{
					this.numRetransmissoes++;
	    			// mantm o estado SYNSENT
	    			// pe um SYN no byte de controle
	    			pacoteTCP.setControle(TCPIF.S_SYN);
	    			// atualiza o frame
	    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_SYN, pacoteTCP);
	    			mef.atualizaDadosEstado(estadoMEConAtual, "Time Out" , "->", segmento);
	    			// diminui em um o proximo numero de sequencia pois é uma retransmissão, e o numero de 
	    			// sequência da retransmissão deve ser o mesmo do segmento original
	    			this.proxNumSeq--;
	    			// envia o pacote
	    			enviaSegmentoTCP(pacoteTCP);
   	    		}
   	    		else if(this.estadoMEConAtual.equals(TCPIF.SYNRCVD))
   	    		{
					this.numRetransmissoes++;
	    			// mantm o estado SYNRCVD
	    			// pe um SYN+ACK no byte de controle
	    			pacoteTCP.setControle(TCPIF.S_SYN_ACK);
	    			// atualiza o frame
	    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_SYN_ACK, pacoteTCP);
	    			mef.atualizaDadosEstado(estadoMEConAtual, "Time Out" , "->", segmento);
	    			// diminui em um o proximo numero de sequencia pois é uma retransmissão, e o numero de 
	    			// sequência da retransmissão deve ser o mesmo do segmento original
	    			this.proxNumSeq--;
	    			// envia o pacote
	    			enviaSegmentoTCP(pacoteTCP);
   	    		}
   	    		else if(this.estadoMEConAtual.equals(TCPIF.FINWAIT_1))
   	    		{
					this.numRetransmissoes++;
	    			// mantm o estado FINWAIT_1
	    			// pe um SYN+ACK no byte de controle
	    			pacoteTCP.setControle(TCPIF.S_FIN);
	    			// atualiza o frame
	    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_FIN, pacoteTCP);
	    			mef.atualizaDadosEstado(estadoMEConAtual, "Time Out" , "->", segmento);
	    			// diminui em um o proximo numero de sequencia pois é uma retransmissão, e o numero de 
	    			// sequência da retransmissão deve ser o mesmo do segmento original
	    			this.proxNumSeq--;
	    			// envia o pacote
	    			enviaSegmentoTCP(pacoteTCP);
   	    		}
   	    		else if(this.estadoMEConAtual.equals(TCPIF.CLOSING))
   	    		{
					this.numRetransmissoes++;	
	    			// mantm o estado CLOSING
	    			// pe um SYN+ACK no byte de controle
	    			pacoteTCP.setControle(TCPIF.S_FIN);
	    			// atualiza o frame
	    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_FIN, pacoteTCP);
	    			mef.atualizaDadosEstado(estadoMEConAtual, "Time Out" , "->", segmento);
	    			// diminui em um o proximo numero de sequencia pois é uma retransmissão, e o numero de 
	    			// sequência da retransmissão deve ser o mesmo do segmento original
	    			this.proxNumSeq--;
	    			// envia o pacote
	    			enviaSegmentoTCP(pacoteTCP);
   	    		}
			}
   			else
   			{
				// atualiza o frame
				mef.atualizaDadosEstado(estadoMEConAtual, "Time Out" , ".", ".");
				// reinicia o contador de retransmisses
				this.numRetransmissoes = 0;
				// muda para o estado CLOSED
    			this.estadoMEConAtual = TCPIF.CLOSED;
    			// envia primitiva Error para a camada de aplicao
    			this.enviaPrimitiva(TCPIF.P_ERROR, null);
    			// atualiza frame
    			this.proxNumSeq++;
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_RST, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "Error" , "->", segmento);
    			// pe um RST no byte de controle
    			pacoteTCP.setControle(TCPIF.S_RST);
    			// envia o pacote
    			enviaSegmentoTCP(pacoteTCP);
   			}

    	}
    	
    	// muda o status de conexo
    	if( ipSimuladoDestino == null || ipSimuladoDestino.isEmpty() )
    	{
    		mef.atualizaInfoConexao(estadoMEConAtual, Decoder.ipSimuladoToBytePonto(ipSimuladoLocal) , portaLocal + "", 

"", "");
    	}
    	else
    	{
    		mef.atualizaInfoConexao(estadoMEConAtual, Decoder.ipSimuladoToBytePonto(ipSimuladoLocal) , portaLocal + "", 

Decoder.ipSimuladoToBytePonto(ipSimuladoDestino), portaDestino + "");
    	}
    }
    
    /** 
     * Mtodo que envia primitivas
     *
     * @param _primitiva A primitiva que est sendo enviada.
     * @param args[] Um array de argumentos que a aplicao pode enviar.
     * @exception Exception  Caso ocorra algum erro ou exceo, lana (throw) 
     * para quem chamou o mtodo.
     */
    public void enviaPrimitiva(int _primitiva, String args[])
        throws Exception {
        
    	
   	
      //implemente aqui o envio de primitivas para sua MquinaDeEstadosFrame
    }
    
    /** 
     * Mtodo que recebe segmentos TCP e faz o tratamento desse pacote
     *
     * @param _pacoteTCP O segmento TCP recebido
     * @exception Exception  Caso ocorra algum erro ou exceo, lana (throw) 
     * para quem chamou o mtodo.
     */
    public void recebeSegmentoTCP(PacoteTCP _pacoteTCP)
        throws Exception {
    	
    	// cria um Pacote TCP
    	PacoteTCP pacoteTCP = new PacoteTCP();
    	// pega a MaquinaDeEstadosFrame
    	MaquinaDeEstadosFrame mef = getMeFrame();
    	
    	this.pacoteRecebido = _pacoteTCP;
    	
    	this.ipSimuladoDestino = Decoder.bytePontoToIpSimulado(pacoteRecebido.getIpSimuladoLocal());
    	this.portaDestino = Integer.parseInt(pacoteRecebido.getPortaLocal());
    	
    	System.out.println("Ip destino: " +this.ipSimuladoDestino);
    	System.out.println("Porta destino: " +this.portaDestino);
    	
    	
    	System.out.println("Dados: " + _pacoteTCP.getDados());
    	
    	// se recebeu um SYN
    	if(pacoteRecebido.getControle() == Short.parseShort(Decoder.byteToString(TCPIF.S_SYN)))
    	{
    		// Atualiza o frame
    		String segmentoRecebido = this.atualizaSegmentoRecepcao(TCPIF.S_SYN, _pacoteTCP); 
    		mef.atualizaDadosEstado(estadoMEConAtual, "." , "<-", segmentoRecebido);
    		if(estadoMEConAtual.equals(TCPIF.LISTEN) || estadoMEConAtual.equals(TCPIF.SYNRCVD))
    		{
    			// muda para o estado SYNRCVD
    			estadoMEConAtual = TCPIF.SYNRCVD;
    			// pe SYN+ACK
    			pacoteTCP.setControle(TCPIF.S_SYN_ACK);
    			// atualiza o frame
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_SYN_ACK, pacoteTCP);
        		mef.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
        		// envia o pacote
    			enviaSegmentoTCP(pacoteTCP);
    		}
    		else if(estadoMEConAtual.equals(TCPIF.SYNSENT))
    		{
    			// muda para o estado SYNRCVD
    			estadoMEConAtual = TCPIF.SYNRCVD;
    			// poe ACK
    			pacoteTCP.setControle(TCPIF.S_ACK);
    			// atualiza frame
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
    			// envia o pacote
    			enviaSegmentoTCP(pacoteTCP);
    		}
    	}
    	// se recebeu um ACK
    	else if(pacoteRecebido.getControle() == Short.parseShort(Decoder.byteToString(TCPIF.S_ACK)))
    	{
   		
    		// Atualiza o frame
    		String segmentoRecebido = this.atualizaSegmentoRecepcao(TCPIF.S_ACK, _pacoteTCP);
    		// se ele estiver ainda no modo de transmitir dados, imprimir o estado da maquina TX e não
    		// o estado da maquina de conexao desconexao
    		if(this.estadoMETX != TCPIF.IDLE)
    			mef.atualizaDadosEstado(this.estadoMETX, "." , "<-", segmentoRecebido);
    		else
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , "<-", segmentoRecebido);
    		
    		if(estadoMEConAtual.equals(TCPIF.SYNRCVD))
    		{
    			// muda para o estado ESTABLISHED
    			estadoMEConAtual = TCPIF.ESTABLISHED;
    			// envia primitiva Open Sucess
    			enviaPrimitiva(TCPIF.P_OPENSUCCESS, null);
    			
    			mef.atualizaDadosEstado(estadoMEConAtual, "O. Sucess" , ".", ".");
    			
    			TabelaDeConexoes tab = monitor.getTabelaDeConexoes();
    			ConexaoTCP ctcp = (ConexaoTCP)tab.get(this.getIdConexao());
    			ctcp.setIpSimuladoRemoto(this.ipSimuladoDestino);
    			ctcp.setPortaRemota( String.valueOf(this.portaDestino));
    			tab.put(ctcp);
    		}
    		else if(estadoMEConAtual.equals(TCPIF.FINWAIT_1))
    		{
    			// muda para o estado FINWAIT_2
    			estadoMEConAtual = TCPIF.FINWAIT_2;
    			
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , ".", ".");
    		}
    		else if(estadoMEConAtual.equals(TCPIF.CLOSING))
    		{
    			// muda para o estado TIMEWAIT
    			estadoMEConAtual = TCPIF.TIMEWAIT;
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , ".", ".");
    			
    			// espera o tempo 2MSL, que  igual a 2 vezes tempo de timeout
    			this.wait(10000);
    			// muda para o estado CLOSED	
    			estadoMEConAtual = TCPIF.CLOSED;
    			// envia primitiva de Terminate
    			enviaPrimitiva(TCPIF.P_TERMINATE, null);
    			// atualiza frame
    			mef.atualizaDadosEstado(estadoMEConAtual, "Terminate", ".", ".");
    		}
    		else if(estadoMEConAtual.equals(TCPIF.LAST_ACK))
    		{
    			// muda para o estado FINWAIT_2
    			estadoMEConAtual = TCPIF.CLOSED;
    			// envia primitiva Terminate
    			enviaPrimitiva(TCPIF.P_TERMINATE, null);
    			
    			mef.atualizaDadosEstado(estadoMEConAtual, "Terminate" , ".", ".");
    		}
    		else if(estadoMEConAtual.equals(TCPIF.ESTABLISHED))
    		{
    			// cancela timeout de retransmissao
    			//this.cancelaTimeOut();
    			
    			// Tratamento de Recepcao
    			// TODO depois consertar isso aqui para um caso mais geral que pode ser necessrio para a 
    			// fase 5
    			// Esse idle abaixo quer dizer que a maquina esta recebendo, e nao transmitindo
    			if(this.numSeqTX == -1)
    				this.trataRX();
    			
    			// Tratamento de transmissao
    			if(this.estadoMETX.equals(TCPIF.TRASMITTING))
    			{
    				if(this.pacoteRecebido.getJanela() == 0)
    				{
    					this.estadoMETX = TCPIF.TX_BLOCKED;
    					meFrame.setEstadoTX(TCPIF.TX_BLOCKED);
    					
    					// ativa timeout de Keep Alive
    					this.ativaTimeOut(true,0);

    				}
    				if(this.pacoteRecebido.getJanela() > 0)
    				{
    					this.estadoMETX = TCPIF.TRASMITTING;
						meFrame.setEstadoTX(TCPIF.TRASMITTING);
					}
    			}
    			if(this.estadoMETX.equals(TCPIF.WAITING_ACK))
    			{	

    				this.cancelaTimeOut();
    				
    				if(this.pacoteRecebido.getJanela() == 0)
    				{
    					//TODO checar se isso esta certo
	    				// quer dizer que ele nao tem mais dados para transmitir
    					// zera o numero de sequencia auxiliar e zera o buufer de transmissao
	    				// modificado celso if((this.numSeqTX + 1) == this.tamanhoTotalMensagem)
    					if((this.numSeqTXAuxiliar) == this.tamanhoTotalMensagem)
	    				{
	    					this.estadoMETX = TCPIF.IDLE;
	    					meFrame.setEstadoTX(TCPIF.IDLE);
	    					// celso
	    					this.numSeqTXAuxiliar = 0;
	    					this.numBytesBufferrizadosTX = 0;
	    					// renderiza na tela
	    					this.meFrame.atualizaDadosEstado(estadoMETX, "." , ".", ".");
	    					this.meFrame.atualizaDadosEstado(estadoMEConAtual, "." , ".", ".");
	       					// seta o ltimo Numero de Sequencia no Confirmado
	    					meFrame.setNumSeqNConf(this.numSeqTX);
	    				}
	    				// faltam dados a serem transmitidos
	    				else
	    				{
	    					System.out.println("numSeqAux" + this.numSeqTXAuxiliar + " msg" + this.tamanhoTotalMensagem);
	    					this.estadoMETX = TCPIF.TX_BLOCKED;
	    					meFrame.setEstadoTX(TCPIF.TX_BLOCKED);
	    					// timeout de KeepAlive
	    					this.ativaTimeOut(true,0);
	    				}
    				}
    				if(this.pacoteRecebido.getJanela() > 0)
    				{	
    					this.janelaTransmissao = 0;
    					// se nao faltam dados a serem transmitidos
	    				// modificado celso if((this.numSeqTX + 1) == this.tamanhoTotalMensagem)
    					if((this.numBytesTransmitidos) == this.tamanhoTotalMensagem)
    					{
	    					this.estadoMETX = TCPIF.IDLE;
	    					meFrame.setEstadoTX(TCPIF.IDLE);
	    					// celso
	    					this.numSeqTXAuxiliar = 0;
	    					this.numBytesBufferrizadosTX = 0;	 
	    					this.meFrame.setDados("");
    					}
    					// faltam dados a serem transmitidos
    					else
    					{
    						this.estadoMETX = TCPIF.TRASMITTING;
	    					meFrame.setEstadoTX(TCPIF.TRASMITTING);
	    				}
    					
    					// seta o ltimo Numero de Sequencia no Confirmado
    					meFrame.setNumSeqNConf(this.numSeqTX);
    				}
    			}
    			if(this.estadoMETX.equals(TCPIF.TX_BLOCKED))
    			{
    				if(this.pacoteRecebido.getJanela() == 0)
    				{
    					this.estadoMETX = TCPIF.TX_BLOCKED;
    					meFrame.setEstadoTX(TCPIF.TX_BLOCKED);
    				}
    				else
    				{
    					this.estadoMETX = TCPIF.TRASMITTING;
						meFrame.setEstadoTX(TCPIF.TRASMITTING);
						
						//desativa timeout de keepAlive
						this.cancelaTimeOut();
					}
    			}	
    			
    			this.trataTX();
    		}
    	}
        // se recebeu um SYN+ACK
        else if(pacoteRecebido.getControle() == Short.parseShort(Decoder.byteToString(TCPIF.S_SYN_ACK)))
        {
        	// Desabilita o timer que estava fazendo a contagem de timeout
        	this.cancelaTimeOut();
    		// Atualiza o frame
        	String segmentoRecebido = this.atualizaSegmentoRecepcao(TCPIF.S_SYN_ACK, _pacoteTCP);
    		mef.atualizaDadosEstado(estadoMEConAtual, "." , "<-", segmentoRecebido);
    		if(estadoMEConAtual.equals(TCPIF.SYNSENT))
    		{
    			// muda para o estado ESTABLISHED
    			estadoMEConAtual = TCPIF.ESTABLISHED;
    			// envia primitiva Open Sucess
    			enviaPrimitiva(TCPIF.P_OPENSUCCESS, null);
    			// pe ACK
    			pacoteTCP.setControle(TCPIF.S_ACK);
    			// atualiza frame
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "O. Sucess" , "->", segmento);
    			// envia o pacote
    			enviaSegmentoTCP(pacoteTCP);
    			
    			TabelaDeConexoes tab = monitor.getTabelaDeConexoes();
    			ConexaoTCP ctcp = (ConexaoTCP)tab.get(this.getIdConexao());
    			ctcp.setIpSimuladoRemoto(this.ipSimuladoDestino);
    			ctcp.setPortaRemota( String.valueOf(this.portaDestino));
    			tab.put(ctcp);
    		}
    		else if(estadoMEConAtual.equals(TCPIF.ESTABLISHED))
    		{
    			// mantm o estado
    			// pe ACK
    			pacoteTCP.setControle(TCPIF.S_ACK);
    			// atualiza frame
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
    			// envia o pacote
    			enviaSegmentoTCP(pacoteTCP);
    		}
        }
        // se recebeu um FIN
        else if(pacoteRecebido.getControle() == Short.parseShort(Decoder.byteToString(TCPIF.S_FIN)))
        {
    		// Atualiza o frame
        	String segmentoRecebido = this.atualizaSegmentoRecepcao(TCPIF.S_FIN, _pacoteTCP);
    		mef.atualizaDadosEstado(estadoMEConAtual, "." , "<-", segmentoRecebido);
        	if(estadoMEConAtual.equals(TCPIF.ESTABLISHED) || estadoMEConAtual.equals(TCPIF.CLOSEWAIT))
        	{
    			// muda para o estado CLOSEWAIT
    			estadoMEConAtual = TCPIF.CLOSEWAIT;
    			// pe ACK
    			pacoteTCP.setControle(TCPIF.S_ACK);
    			// atualiza frame
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
    			// envia o pacote
    			enviaSegmentoTCP(pacoteTCP);
        	}
        	else if(estadoMEConAtual.equals(TCPIF.FINWAIT_1) || estadoMEConAtual.equals(TCPIF.CLOSING))
        	{
    			// muda para o estado CLOSING
    			estadoMEConAtual = TCPIF.CLOSING;
    			// pe ACK
    			pacoteTCP.setControle(TCPIF.S_ACK);
    			// atualiza frame
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
    			// envia o pacote
    			enviaSegmentoTCP(pacoteTCP);
        	}
        	else if(estadoMEConAtual.equals(TCPIF.FINWAIT_2) || estadoMEConAtual.equals(TCPIF.TIMEWAIT))
        	{
    			// muda para o estado TIMEWAIT
    			estadoMEConAtual = TCPIF.TIMEWAIT;
    			// pe ACK
    			pacoteTCP.setControle(TCPIF.S_ACK);
    			// atualiza frame
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacoteTCP);
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
    			// envia o pacote
    			enviaSegmentoTCP(pacoteTCP);
    			
    			// espera o tempo 2MSL, que  igual a 2 vezes tempo de timeout
    			this.wait(10000);
    			// muda para o estado CLOSED	
    			estadoMEConAtual = TCPIF.CLOSED;
    			// envia primitiva de Terminate
    			enviaPrimitiva(TCPIF.P_TERMINATE, null);
    			// atualiza frame
    			mef.atualizaDadosEstado(estadoMEConAtual, "Terminate", ".", ".");
        	}
        }
        // se recebeu um RST
        else if(pacoteRecebido.getControle() == Short.parseShort(Decoder.byteToString(TCPIF.S_RST)))
        {
    		// Atualiza o frame
        	String segmentoRecebido = this.atualizaSegmentoRecepcao(TCPIF.S_RST, _pacoteTCP);
    		mef.atualizaDadosEstado(estadoMEConAtual, "." , "<-", segmentoRecebido);
        	if(estadoMEConAtual.equals(TCPIF.SYNRCVD))
        	{
    			// muda para o estado LISTEN
    			estadoMEConAtual = TCPIF.LISTEN;
    			
    			mef.atualizaDadosEstado(estadoMEConAtual, "." , ".", ".");
        	}
        	// se for outro estado, menos CLOSED, LISTEN, SYNRCVD e TIMEWAIT
        	else if(!estadoMEConAtual.equals(TCPIF.CLOSED) || !estadoMEConAtual.equals(TCPIF.LISTEN)
        			|| !estadoMEConAtual.equals(TCPIF.TIMEWAIT) )
        	{
    			// muda para o estado CLOSED
    			estadoMEConAtual = TCPIF.CLOSED;
    			// envia primitiva Error
    			enviaPrimitiva(TCPIF.P_ERROR, null);
    			// atuliza frame
    			mef.atualizaDadosEstado(estadoMEConAtual, "Error" , ".", ".");
        	}
        }
    	// recebeu dados
        else
        {
        	this.trataRX();
        }
    	
    	if( ipSimuladoDestino == null || ipSimuladoDestino.isEmpty() )
    	{
    		mef.atualizaInfoConexao(estadoMEConAtual, Decoder.ipSimuladoToBytePonto(ipSimuladoLocal) , portaLocal + "", 

"", "");
    	}
    	else
    	{
    		mef.atualizaInfoConexao(estadoMEConAtual, Decoder.ipSimuladoToBytePonto(ipSimuladoLocal) , portaLocal + "", 

    		Decoder.ipSimuladoToBytePonto(ipSimuladoDestino), portaDestino + "");
    	}
    	
//    	mef.atualizaDadosRecebidos(pacoteRecebido.getDados());
    	
       //implemente aqui o tratamento do recebimento do pacote
    }
    
    /** 
     * Mtodo que envia segmento TCP
     *
     * @param _pacoteTCP O segmento TCP recebido
     * @exception Exception  Caso ocorra algum erro ou exceo, lana (throw) 
     * para quem chamou o mtodo.
     */
    public void enviaSegmentoTCP(PacoteTCP _pacoteTCP)
        throws Exception {
    	
        // implementando o envio de pacote
    	
    	pacoteDeEnvio = _pacoteTCP;
    	
    	// pega o frame
    	MaquinaDeEstadosFrame mef = getMeFrame();
    	
    	// cdigo para enviar o pacote
    	ProtocoloTCP tcp = monitor.getProtocoloTCP();
    	IpSimulada ip= tcp.getCamadaIpSimulada();

    	
    	System.out.println("Enviou para: " + getIpSimuladoDestino());
    	System.out.println("Enviou em: " + getPortaDestino());
    	
    	// pe o IP local
    	pacoteDeEnvio.setIpSimuladoLocal(Decoder.ipSimuladoToBytePonto(getIpSimuladoLocal()));
    	// pe a porta local
    	pacoteDeEnvio.setPortaLocal(getPortaLocal() + "");
    	// pe o IP de destino
    	pacoteDeEnvio.setIpSimuladoRemoto(Decoder.ipSimuladoToBytePonto(getIpSimuladoDestino()));
    	// pe a porta de destino
    	pacoteDeEnvio.setPortaRemota( getPortaDestino() + "" );
    	// pe o nmero de sequncia
//    	pacoteDeEnvio.setNumSequencia(0);
//    	pacoteDeEnvio.ajustaNumSeq((long)(Math.pow((double) 2, (double) 32)-1));
    	// pe o nmero de ACK
    	
   
//    	pacoteDeEnvio.setNumAck(0);
    	// pe o OffSet
    	pacoteDeEnvio.setOffset((short)0);
    	// TODO - pe o Controle
    	//pacoteDeEnvio.setControle((short)0) ;
    	// TODO - pe a janela
    	pacoteDeEnvio.setJanela(this.tamJanelaRecepcao);
    	// pe o checksum
    	pacoteDeEnvio.setChecksum(0);
    	// pe o ponteiro de dados urgentes
    	pacoteDeEnvio.setPonteiroUrgente(0);
    	// TODO - opes do TCP
    	pacoteDeEnvio.setOpcoes(0);
    	pacoteDeEnvio.geraOpcoes();
    	// dados do tcp ja foram setado na maquina de transmissao, metodo trataTX() 
    	//pacoteDeEnvio.setDados(mef.getDados());
    	
    	// poe o numero de sequencia
    	pacoteDeEnvio.setNumSequencia(this.proxNumSeq);
    	//atualiza o numero de sequencia
    	
    	if(_pacoteTCP.getDados().getBytes().length == 0)
    		this.proxNumSeq++;
    	else
    		this.proxNumSeq += _pacoteTCP.getDados().getBytes().length;
    	
    	meFrame.setNumSeq(this.proxNumSeq);
    	
    	// poe o numero de ack. Caso não esteja mandando um segmento do tipo ACK, devemos renderizar na tela 
    	// ACK igual a 0, naquela diagrama de tempos da maquina de estado frame
    	if(pacoteDeEnvio.getControle() == Short.parseShort(Decoder.byteToString(TCPIF.S_ACK)) || 
    			pacoteDeEnvio.getControle() == Short.parseShort(Decoder.byteToString(TCPIF.S_SYN_ACK)))
    		pacoteDeEnvio.setNumAck(this.numAck);
    	else
    		pacoteDeEnvio.setNumAck(0);
    	// TODO Aqui tem que mudar o comportamento de quando ele estiver no estado timewait, porque  diferente de 
    	// todos os outros estados. 
    	// caso o atual estado seja passvel de timeout (segundo o anexo II), o tratamento do timeout  ativado
    	if(this.estadoMEConAtual.equals(TCPIF.SYNSENT) || this.estadoMEConAtual.equals(TCPIF.SYNRCVD)
    		|| this.estadoMEConAtual.equals(TCPIF.FINWAIT_1) || this.estadoMEConAtual.equals(TCPIF.CLOSING)
    		|| this.estadoMEConAtual.equals(TCPIF.TIMEWAIT))
    	{
    		this.ativaTimeOut(false,0);
    	}
    	
    	// manda o pacote para a camada IP
		ip.transmite(pacoteDeEnvio.getIpSimuladoRemoto(), pacoteDeEnvio.toString(), 1024 );
    }
    
    /**
     * Mtodo que trata um timeout, escalonando uma tarefa que cuidar do evento de timeout caso o tempo de time out
     * seja excedido
     *
     */
    private void ativaTimeOut(boolean timeOutTX, int tempo){
//    	if(this.oTimer != null)
//    	{
//    		this.oTimer = null;
//    	}
    	this.oTimer = new Timer();
    	this.oTimeOutTask = new TimeOutTask(this,timeOutTX);
    	if(tempo==0)
    	{
    		oTimer.schedule(this.oTimeOutTask, tempoTimeout);
    	}
    	else
    	{
    		oTimer.schedule(this.oTimeOutTask, tempo);
    	}
    	
    }
    
    /**
     * Classe que tem como responsabilidade tratar o estouro de timeout
     * @author Hugo Barauna
     *
     */
    public class TimeOutTask extends TimerTask {	
    	
    	private MaquinaDeEstados me;
		private boolean timeOutTX;
    	
    	
    	public TimeOutTask(MaquinaDeEstados me, boolean timeOutTX){
    		this.me = me;
    		this.timeOutTX = timeOutTX;
    	}
    	
    	/**
    	 * Este e o metodo ativado quando a task e executada, de acordo com o schedule que foi feito
    	 */
    	public void run(){
    		try {
    			//this.me.oTimer = null;
    			this.me.recebePrimitiva(TCPIF.P_TIMEOUT, null);
    			
    			// se for um timeout de transmissão, envia um ACK
    			if(this.timeOutTX)
    			{
    				// timeout de Keep Alive
    				if(me.estadoMETX.equals(TCPIF.TX_BLOCKED))
    				{
    					System.out.println("Timeout");
    					me.tamJanelaRecepcao = 0;
    					PacoteTCP pacote = new PacoteTCP();
    					pacote.setControle(TCPIF.S_ACK);
    					me.enviaSegmentoTCP(pacote);
    	    			String segmento = me.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacote);
    	    			me.meFrame.atualizaDadosEstado(estadoMEConAtual, "." , "->", segmento);
    	    			
    	    			// reativa o timeout
    	    			me.ativaTimeOut(true,0);
    				}
    				// timeout de retransmissão de dados
    				else if(me.estadoMETX.equals(TCPIF.WAITING_ACK))
    				{
    					System.out.println("timeout retransmissao");
    					int bytesTransmitidos;
    					if( (me.numBytesTransmitidos % 12) == 0)
    						bytesTransmitidos = 12;
    					else
    						bytesTransmitidos = me.numBytesTransmitidos % 12; 
    					//me.numBytesTransmitidos -= me.meFrame.getDados().length();
    					me.numBytesTransmitidos -= bytesTransmitidos;
    					me.numSeqTX -= bytesTransmitidos;
    					me.proxNumSeq -= bytesTransmitidos;
    					me.numBytesBufferrizadosTX -= bytesTransmitidos;
    					me.estadoMETX = TCPIF.TRASMITTING;
    					me.janelaTransmissao = 0;
    					me.trataTX();
    				}
    				// timeout de estado bloqueado
    				else if(me.estadoMERX.equals(TCPIF.RX_BLOCKED))
    				{
    					PacoteTCP pacote = new PacoteTCP();
    					pacote.setControle(TCPIF.S_ACK);
    					// pega a Janela que o usuário disponibilizou
    					me.tamJanelaRecepcao = 0;
     					String textoSegmento = me.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacote);
    					me.meFrame.atualizaDadosEstado(estadoMERX, "." , "->", textoSegmento);
    					me.enviaSegmentoTCP(pacote);
    				}
    				
    				// timeout de entrega de aplicação
    				else
    				{
    					PacoteTCP pacote = new PacoteTCP();
    					pacote.setControle(TCPIF.S_ACK);
    					// pega a Janela que o usuário disponibilizou
    					me.tamJanelaRecepcao = me.meFrame.getJanela();
     					String textoSegmento = me.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacote);
    					me.meFrame.atualizaDadosEstado(estadoMERX, "." , "->", textoSegmento);
    					me.enviaSegmentoTCP(pacote);
    					
    					// esvazia o buffer
    					String mensagemNoBuffer = new String(me.bufferRX);
    					mensagemNoBuffer = mensagemNoBuffer.substring(0, me.numBytesBufferrizadosRX);
    					String dados = me.meFrame.getDadosRecebidos();
    					dados = dados.concat(mensagemNoBuffer);
    					me.numBytesBufferrizadosRX = 0;
    					me.fimBufferRX = 0;
    					me.numSeqRXAuxiliar = 0;
    					me.meFrame.setDadosRecebidos(dados);
    					
    					// atualiza ultimo byte lido pela camada de aplicação
    					me.ultimoByteLido += mensagemNoBuffer.length();
    					
    					// reinicia a janela de recepcao
    					me.tamJanelaRecepcao = me.bufferRX.length;
    				}
    			}
    			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	public void kill(){
    		try {
				//this.finalize();
				this.cancel();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    private void cancelaTimeOut()
    {
    	this.oTimeOutTask.kill();
    	this.oTimeOutTask = null;
    	//this.oTimer.cancel();
    	//this.oTimer = null;
    	this.numRetransmissoes = 0;
    }
      
    /** Mtodo acessador para o atributo monitor.
     * @return A referncia para o atributo monitor.
     *
     */
    public Monitor getMonitor() {
        return monitor;
    }
    
    /** Mtodo modificador para o atributo monitor.
     * @param monitor Novo valor para o atributo monitor.
     *
     */
    public void setMonitor(Monitor _monitor) {
        this.monitor = _monitor;
    }
    
    /** Mtodo modificador para o atributo meFrame.
     * @param meFrame Novo valor para o atributo meFrame.
     *
     */
    public void setMeFrame(MaquinaDeEstadosFrame _meFrame) {
        this.meFrame = _meFrame;
    }
    
    /** Mtodo acessador para o atributo meFrame.
     * @return A referncia para o atributo meFrame.
     *
     */
    public MaquinaDeEstadosFrame getMeFrame() {
        return meFrame;
    }    

    /** Mtodo acessador para o atributo idConexao.
     * @return A referncia para o atributo idConexao.
     *
     */
    public int getIdConexao() {
        return idConexao;
    }
    
    /** Mtodo modificador para o atributo idConexao.
     * @param idConexao Novo valor para o atributo idConexao.
     *
     */
    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }
    
    /** Mtodo acessador para o atributo ipSimuladoLocal.
     * @return A referncia para o atributo ipSimuladoLocal.
     *
     */
    public String getIpSimuladoLocal() {
        return ipSimuladoLocal;
    }
    
    /** Mtodo modificador para o atributo ipSimuladoLocal.
     * @param ipSimuladoLocal Novo valor para o atributo ipSimuladoLocal.
     *
     */
    public void setIpSimuladoLocal(String _ipSimuladoLocal) {
        this.ipSimuladoLocal = _ipSimuladoLocal;
    }
    
    /** Mtodo acessador para o atributo ipSimuladoLocal.
     * @return A referncia para o atributo ipSimuladoLocal.
     *
     */
    public String getIpSimuladoLocalBytePonto() {
        return Decoder.ipSimuladoToBytePonto(this.ipSimuladoLocal);
    }
    
    /** Mtodo modificador para o atributo ipSimuladoLocal.
     * @param ipSimuladoLocal Novo valor para o atributo ipSimuladoLocal.
     *
     */
    public void setIpSimuladoLocalBytePonto(String _ipSimuladoLocal) {
        this.ipSimuladoLocal = Decoder.bytePontoToIpSimulado(_ipSimuladoLocal);
    }
    
        /** Mtodo acessador para o atributo portaLocal.
     * @return A referncia para o atributo portaLocal.
     *
     */
    public int getPortaLocal() {
        return portaLocal;
    }
    
    /** Mtodo modificador para o atributo portaLocal.
     * @param portaLocal Novo valor para o atributo portaLocal.
     *
     */
    public void setPortaLocal(int portaLocal) {
        this.portaLocal = portaLocal;
    }
    
    /** Mtodo acessador para o atributo ipSimuladoDestino.
     * @return A referncia para o atributo ipSimuladoDestino.
     *
     */
    public String getIpSimuladoDestino() {
        return ipSimuladoDestino;
    }
    
    /** Mtodo modificador para o atributo ipSimuladoDestino.
     * @param ipSimuladoDestino Novo valor para o atributo ipSimuladoDestino.
     *
     */
    public void setIpSimuladoDestino(String _ipSimuladoDestino) {
        this.ipSimuladoDestino = _ipSimuladoDestino;
    }
    /** Mtodo acessador para o atributo nomeEstacaoDestino.
     * @return A referncia para o atributo nomeEstacaoDestino.
     *
     */
    public String getNomeEstacaoDestino() {
        return nomeEstacaoDestino;
    }
    
    /** Mtodo modificador para o atributo nomeEstacaoDestino.
     * @param nomeEstacaoDestino Novo valor para o atributo nomeEstacaoDestino.
     *
     */
    public void setNomeEstacaoDestino(String _nomeEstacaoDestino) {
        this.nomeEstacaoDestino = _nomeEstacaoDestino;
    }
    
    /** Mtodo acessador para o atributo portaDestino.
     * @return A referncia para o atributo portaDestino.
     *
     */
    public int getPortaDestino() {
        return portaDestino;
    }
    
    /** Mtodo modificador para o atributo portaDestino.
     * @param portaDestino Novo valor para o atributo portaDestino.
     *
     */
    public void setPortaDestino(int _portaDestino) {
        this.portaDestino = _portaDestino;
    }

	public int getTempoTimeout() {
		return tempoTimeout;
	}
	
	public void setTempoTimeOut(int timeOut)
	{
		tempoTimeout = timeOut; 
	}
	
	
	/**
	 * Este metodo e usado no controle de sequencializao, no contexto de envio de segmentos, para uma coisa:<br/>
	 * 1. Criar a String que deve ser mostrada no campo segmento no diagrama de tempos da MaquinaDeEstadosFrame
	 * @param tipoSegmento O tipo do segmento, ex.: SYN, ACK, SYN+ACK. Este tipo deve ser representado usando as
	 * constantes da interface TCPIF
	 * @param pacote O pacote TCP que e passado para o metodo. Usado para tirar algumas informacoes do pacote, 
	 * como por exemplo, tamanho
	 * @return Retorna uma String formatada para ser apresentada no campo Segmento do diagrama de tempos da MaquinaDeEstadosFrame
	 */
	private String atualizaSequencializacaoEnvio(byte tipoSegmento, PacoteTCP pacote)
	{
		String segmento = "";
		
		// Parece que nao da para usar este método PacoteTCP.getTamanho() para pegar o tamanho do segmento TCP diretamente
		// Talvez seja necessrio fazer umas continhas
		
		switch (tipoSegmento) {
		case TCPIF.S_SYN:
			segmento = "SYN" + "(" +  this.proxNumSeq + "," +  pacote.getTamanhoSegementoBytes();
			segmento += "," + "0" + "," + this.tamJanelaRecepcao + ")";
			break;
		case TCPIF.S_FIN:
			segmento = "FIN" + "(" +  this.proxNumSeq + "," +  pacote.getTamanhoSegementoBytes();
			segmento += "," + "0" + "," + this.tamJanelaRecepcao + ")";
			break;
		case TCPIF.S_RST:
			segmento = "RST" + "(" +  this.proxNumSeq + "," +  pacote.getTamanhoSegementoBytes();
			segmento += "," + "0" + "," + this.tamJanelaRecepcao + ")";
			break;
		case TCPIF.S_ACK:
			segmento = "ACK" + "(" +  this.proxNumSeq + "," +  pacote.getTamanhoSegementoBytes();
			segmento += "," + this.numAck + "," + this.tamJanelaRecepcao + ")";
			break;
		case TCPIF.S_SYN_ACK:
			segmento = "SYNACK" + "(" +  this.proxNumSeq + "," +  pacote.getTamanhoSegementoBytes();
			segmento += "," + this.numAck + "," + this.tamJanelaRecepcao + ")";
			break;
		case TCPIF.S_TX:
			segmento = "TX" + "(" +  this.proxNumSeq + "," +  pacote.getTamanhoSegementoBytes();
			segmento += "," + "0" + "," + this.tamJanelaRecepcao + ")";
			break;
		default:
			break;
		}
		
		return segmento;
	}
	
	/**
	 * Este metodo e usado no controle de sequencializao, no contexto de recepcao de segmentos, para uma coisa:<br/>
	 * 1. Criar a String que deve ser mostrada no campo segmento no diagrama de tempos da MaquinaDeEstadosFrame<br/>
	 * 2. Atualizar a variável que guarda o número de sequencia esperado (ACK)
	 * @param tipoSegmento O tipo do segmento, ex.: SYN, ACK, SYN+ACK. Este tipo deve ser representado usando as
	 * constantes da interface TCPIF
	 * @param pacoteRecebido Pacote recebido. Usado para extrair informacoes que serao redenrizados no diagrama
	 * de tempo
	 * @return
	 */
	private String atualizaSegmentoRecepcao(byte tipoSegmento, PacoteTCP pacoteRecebido){
		String segmento = "";
		
		// Parece que nao da para usar este método PacoteTCP.getTamanho() para pegar o tamanho do segmento TCP diretamente
		// Talvez seja necessrio fazer umas continhas
		
		long tamanhoPacote = pacoteRecebido.getTamanhoSegementoBytes();
		
		switch (tipoSegmento) {
		case TCPIF.S_SYN:
			segmento = "SYN" + "(" +  pacoteRecebido.getNumSequencia() + "," + tamanhoPacote;
			segmento += "," + pacoteRecebido.getNumAck() + "," + pacoteRecebido.getJanela() + ")";
			break;
		case TCPIF.S_FIN:
			segmento = "FIN" + "(" +  pacoteRecebido.getNumSequencia() + "," +  tamanhoPacote;
			segmento += "," + pacoteRecebido.getNumAck() + "," + pacoteRecebido.getJanela() + ")";
			break;
		case TCPIF.S_RST:
			segmento = "RST" + "(" +  pacoteRecebido.getNumSequencia() + "," +  tamanhoPacote;
			segmento += "," + pacoteRecebido.getNumAck() + "," + pacoteRecebido.getJanela() + ")";
			break;
		case TCPIF.S_ACK:
			segmento = "ACK" + "(" +  pacoteRecebido.getNumSequencia() + "," +  tamanhoPacote;
			segmento += "," + pacoteRecebido.getNumAck() + "," + pacoteRecebido.getJanela() + ")";
			break;
		case TCPIF.S_SYN_ACK:
			segmento = "SYNACK" + "(" +  pacoteRecebido.getNumSequencia() + "," +  tamanhoPacote;
			segmento += "," + pacoteRecebido.getNumAck() + "," + pacoteRecebido.getJanela() + ")";
			break;
		case TCPIF.S_RX:
			segmento = "RX" + "(" +  pacoteRecebido.getNumSequencia() + "," +  tamanhoPacote;
			segmento += "," + "0" + "," + pacoteRecebido.getJanela() + ")";
			break;
		default:
			break;
		}
		
		if(pacoteRecebido.getDados().getBytes().length == 0) // recebeu um segmento de controle, sem nada no campo dados
			this.numAck++;
		else
			this.numAck += pacoteRecebido.getDados().getBytes().length;
		
		meFrame.setNumACK(this.numAck);
		
		return segmento;
	}
	
	/**
	 * Método que implementa a maquina de estados do protocolo de transmissao de dados sobre segmentos TCP
	 *
	 */
	private void trataTX() throws Exception{
		
		if(this.estadoMETX.equals(TCPIF.TRASMITTING))
		{
			// seta algumas variaveis necessarias a transmissao
			int tamanhoMensagem = this.meFrame.getDados().getBytes().length;
			byte[] mensagemBytes = this.meFrame.getDados().getBytes();
			// estou considerando aqui que o cabeçalho TCP sempre terá 24 bytes, baseado no contrutor da 
			// classe PacoteTCP
			// esta variavel guarda o numero maximo de bytes que o semento TCP pode carregar como dados 
			int tamDados = this.MSS - 24;
			
			
			// copia a mensagem inteira para o buffer, acreditando que a mensagem ira caber inteiramente no buffer
			while(this.numBytesBufferrizadosTX < tamanhoMensagem)
			{
				this.bufferTX[this.numBytesBufferrizadosTX] = mensagemBytes[this.numBytesBufferrizadosTX];
				this.numBytesBufferrizadosTX++;
			}
			
			byte[] dadosSegmentoTX = new byte[tamDados];
			
			// controle do tamanho da mensagem e da janela de transmissão
			while(this.numBytesTransmitidos<tamanhoMensagem && this.janelaTransmissao<this.tamJanelaRemota)
			{
				// controle de tamanho da mensagem e tamanho do segmento
				int i;
				for(i = 0; i < tamDados && this.numBytesTransmitidos < tamanhoMensagem && 
						this.numSeqTXAuxiliar < tamDados && this.janelaTransmissao<this.tamJanelaRemota; i++)
				{	
					dadosSegmentoTX[i] = this.bufferTX[this.numBytesTransmitidos];
					this.numBytesTransmitidos++;
					// controle se sequência
					this.numSeqTXAuxiliar++;
					this.janelaTransmissao++;
				}
				
				// caso ocorreu segmentação ou acabou de enviar todos os bytes, envia o segmento
				PacoteTCP pacote = new PacoteTCP();
				String dados = new String(dadosSegmentoTX);
				dados = dados.substring(0, i);
				pacote.setDados(dados);
				String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_TX, pacote);
				this.meFrame.atualizaDadosEstado(estadoMETX, "." , "->", segmento);
				this.enviaSegmentoTCP(pacote);
				// controle se sequencia
				this.numSeqTX += this.numSeqTXAuxiliar + 1;
				this.numSeqTXAuxiliar = 0;
			}
			
			
			//trata evento de quando todos os dados foram transmitidos
			if(this.numBytesTransmitidos == this.tamanhoTotalMensagem)
			{
				this.estadoMETX = TCPIF.WAITING_ACK;
				meFrame.setEstadoTX(TCPIF.WAITING_ACK);
				
				// ative timeout de retransmissao
				this.ativaTimeOut(true,10000);
			}
			// trata evento de estouro da janela de recepcao do receptor remoto
			else if(this.numBytesTransmitidos == this.tamJanelaRemota)
			{
				this.estadoMETX = TCPIF.WAITING_ACK;
				meFrame.setEstadoTX(TCPIF.WAITING_ACK);
//				this.janelaTransmissao = 0;
				
				// ative timeout de retransmissao
				this.ativaTimeOut(true,10000);
			}
		
		}
	}
	
	/**
	 * Trata a recepo de dados
	 * 
	 * @throws Exception
	 */
	private void trataRX() throws Exception{
		/*	
		if(this.estadoMERX.equals(TCPIF.RECEIVING))
		{
			// inicializa algumas variaveis necessarias para controlar a recepcao de dados
			int tamanhoDados = this.pacoteRecebido.getDados().getBytes().length;
			byte[] mensagemBytes = this.pacoteRecebido.getDados().getBytes();

			// cancela o timeOut de entrega de aplicação
			this.cancelaTimeOut();
			
			// copia cada byte do segmento recebido para o buffer de recepcao
			for(int i=0; i<tamanhoDados; i++)
			{
				this.bufferRX[this.fimBufferRX] = mensagemBytes[i];
				this.fimBufferRX++;
				this.numBytesBufferrizadosRX++;
				this.ultimoByteRecebido++;
			}

	
			String mensagem = new String(this.bufferRX);
			mensagem = mensagem.substring(this.numSeqRXAuxiliar, this.fimBufferRX);
			String textoSegmento = this.atualizaSegmentoRecepcao(TCPIF.S_RX, this.pacoteRecebido);
			this.meFrame.atualizaDadosEstado(estadoMERX, "." , "<-", textoSegmento);
			this.numSeqRXAuxiliar = this.fimBufferRX;
			
			// tratamento do estouro de janela de recepção (janela cheia)
			// se isso aconter, o receptor tem que enviar um ACK para o transmissor para indicar que reconheceu
			// os dados e que o transmissor ja pode mandar outra rajada de dados
			if( (this.ultimoByteRecebido - this.ultimoByteLido) == this.tamJanelaRecepcao)
			{
				PacoteTCP pacote = new PacoteTCP();
				pacote.setControle(TCPIF.S_ACK);
    			String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacote);

    			meFrame.atualizaDadosEstado(estadoMERX, "." , "->", segmento);
				this.enviaSegmentoTCP(pacote);
			}
			
			// tratamento do evento buffer 100% ocupado:
			// ve se o buffer esta cheio, se sim,  
			// vai para estado bloqueado, envia um ACK com janela 0
			// esvazia o buffer, lendo o buffer para camada de aplicacao
			if(this.numBytesBufferrizadosRX == this.bufferRX.length)
			{
				PacoteTCP pacote = new PacoteTCP();
				pacote.setControle(TCPIF.S_ACK);
				this.tamJanelaRecepcao = 0;
				// atualiza estado e renderiza na tela
				this.estadoMERX = TCPIF.RX_BLOCKED;
				meFrame.setEstadoRX(TCPIF.RX_BLOCKED);
				textoSegmento = this.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacote);
				this.meFrame.atualizaDadosEstado(estadoMERX, "." , "->", textoSegmento);
				this.enviaSegmentoTCP(pacote);
				
				// esvazia o buffer
				String mensagemNoBuffer = new String(this.bufferRX);
				String dados = this.meFrame.getDadosRecebidos();
				dados = dados.concat(mensagemNoBuffer);
				this.numBytesBufferrizadosRX = 0;
				this.fimBufferRX = 0;
				this.numSeqRXAuxiliar = 0;
				this.meFrame.setDadosRecebidos(dados);
				
				// atualiza ultimo byte lido pela camada de aplicação
				this.ultimoByteLido += mensagemNoBuffer.length();
				
				// reinicia a janela de recepcao
				this.tamJanelaRecepcao = this.bufferRX.length;
			}
			// tratamento do evento buffer vazio
		    // se estado igual a bloqueado e o buffer esta vazio, envia um ack e muda o estado para RECEIVING
			if(this.numBytesBufferrizadosRX == 0 && this.estadoMERX.equals(TCPIF.RX_BLOCKED))
			{
				// monta segmento ACK com janela maio que zero
				PacoteTCP pacote = new PacoteTCP();
				//this.tamJanelaRecepcao = meFrame.getJanela();
				this.tamJanelaRecepcao = meFrame.getJanela();
				pacote.setControle(TCPIF.S_ACK);
				System.out.println("entrou em limpa");
				
				// atualiza estado e renderiza na tela
				this.estadoMERX = TCPIF.RECEIVING;
				meFrame.setEstadoRX(TCPIF.RECEIVING);
				String segmento = this.atualizaSequencializacaoEnvio(TCPIF.S_ACK, pacote);
				meFrame.atualizaDadosEstado(estadoMERX, "." , "->", segmento);
				
				this.enviaSegmentoTCP(pacote);
			}
			
			// ativa timeout de aplicação
			this.ativaTimeOut(true,0);
			
		}*/
	}
}//fim da classe MaquinaDeEstados 2006