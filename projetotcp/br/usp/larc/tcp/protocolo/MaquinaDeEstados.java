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
     * Estado atual da máquina de transmissao
     */
    private String estadoMETX;
    
    /**
     * Estado atual da máquina de recepcao
     */
    private String estadoMERX;
    
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
     * Tamanho da janela destino. O usuário pode mudar manualmente o valor dessa janela, mas por default vamos 
     * utilizar o valor de 100 bytes
     */
    private int tamJanela = 100;
    
    /**
     * Maximum Segment Size
     */
    private int MSS = 34;
    
    /**
     * Buffer de transmissão de dados
     */
    private byte[] bufferTX = new byte[2 * this.tamJanela]; 
    
    /**
     * Buffer de recepção de dados
     */
    private byte[] bufferRX = new byte[this.tamJanela];
    
    /**
     * Ponteiro para o ultimo byte do buffer de transissao que foi enviado
     */
    private int numSeqTX = -1;
    
    /**
     * Ponteiro para o último byte do buffer de trasmissao que recebeu um ACK (que foi reconhecido)
     */
    private int numSeqTXReconhecida = -1;
    
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
    			this.trataTX();
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
    	
    	// se recebeu um SYN
    	if(pacoteRecebido.getControle() == Short.parseShort(Decoder.byteToString(TCPIF.S_SYN)))
    	{
    		// Atualiza o frame
    		// TODO testando aqui
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
    			// pe ACK
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
    		mef.atualizaDadosEstado(estadoMEConAtual, "." , "<-", segmentoRecebido);
    		if(estadoMEConAtual.equals(TCPIF.SYNRCVD))
    		{
    			// muda para o estado ESTABLISHED
    			estadoMEConAtual = TCPIF.ESTABLISHED;
    			// envia primitiva Open Sucess
    			enviaPrimitiva(TCPIF.P_OPENSUCCESS, null);
    			
    			mef.atualizaDadosEstado(estadoMEConAtual, "O. Sucess" , ".", ".");
    			
    			// TODO: celso
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
    	
    	mef.atualizaDadosRecebidos(pacoteRecebido.getDados());
    	
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
    	pacoteDeEnvio.setJanela(this.tamJanela);
    	// pe o checksum
    	pacoteDeEnvio.setChecksum(0);
    	// pe o ponteiro de dados urgentes
    	pacoteDeEnvio.setPonteiroUrgente(0);
    	// TODO - opes do TCP
    	pacoteDeEnvio.setOpcoes(0);
    	pacoteDeEnvio.geraOpcoes();
    	// dados do tcp - vem da chamada dessa funo
    	pacoteDeEnvio.setDados(mef.getDados());
    	
    	// poe o numero de sequencia
    	pacoteDeEnvio.setNumSequencia(this.proxNumSeq);
    	//atualiza o numero de sequencia
    	if(mef.getDados().getBytes().length == 0)
    		this.proxNumSeq++;
    	else
    		this.proxNumSeq += mef.getDados().getBytes().length;
    	
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
    		this.ativaTimeOut();
    	}
    	
    	// manda o pacote para a camada IP
		ip.transmite(pacoteDeEnvio.getIpSimuladoRemoto(), pacoteDeEnvio.toString(), 1024 );
    }
    
    /**
     * Mtodo que trata um timeout, escalonando uma tarefa que cuidar do evento de timeout caso o tempo de time out
     * seja excedido
     *
     */
    private void ativaTimeOut(){
//    	if(this.oTimer != null)
//    	{
//    		this.oTimer = null;
//    	}
    	this.oTimer = new Timer();
    	this.oTimeOutTask = new TimeOutTask(this);
    	oTimer.schedule(this.oTimeOutTask, tempoTimeout);
    }
    
    /**
     * Classe que tem como responsabilidade tratar o estouro de timeout
     * @author Hugo Barauna
     *
     */
    public class TimeOutTask extends TimerTask {	
    	
    	private MaquinaDeEstados me;
    	
    	public TimeOutTask(MaquinaDeEstados me){
    		this.me = me;
    	}
    	
    	/**
    	 * Este  o mtodo ativado quando a task  executada, de acordo com o schedule que foi feito
    	 */
    	public void run(){
    		try {
    			//this.me.oTimer = null;
    			this.me.recebePrimitiva(TCPIF.P_TIMEOUT, null);
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
	 * Este metodo e usado no controle de sequencializao, no contexto de envio de segmentos, para duas coisas:<br/>
	 * 1. Criar a String que deve ser mostrada no campo segmento no diagrama de tempos da MaquinaDeEstadosFrame<br/>
	 * 2. Atualizar os seguintes atributos de controle de sequncia: proxNumSeq e numAck
	 * @param tipoSegmento O tipo do segmento, ex.: SYN, ACK, SYN+ACK. Este tipo deve ser representado usando as
	 * constantes da interface TCPIF
	 * @param pacote O pacote TCP que  passado para o mtodo. Usado para tirar algumas informaes do pacote, 
	 * como tamanho
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
			segmento += "," + "0" + "," + this.tamJanela + ")";
			break;
		case TCPIF.S_FIN:
			segmento = "FIN" + "(" +  this.proxNumSeq + "," +  pacote.getTamanhoSegementoBytes();
			segmento += "," + "0" + "," + this.tamJanela + ")";
			break;
		case TCPIF.S_RST:
			segmento = "RST" + "(" +  this.proxNumSeq + "," +  pacote.getTamanhoSegementoBytes();
			segmento += "," + "0" + "," + this.tamJanela + ")";
			break;
		case TCPIF.S_ACK:
			segmento = "ACK" + "(" +  this.proxNumSeq + "," +  pacote.getTamanhoSegementoBytes();
			segmento += "," + this.numAck + "," + this.tamJanela + ")";
			break;
		case TCPIF.S_SYN_ACK:
			segmento = "SYNACK" + "(" +  this.proxNumSeq + "," +  pacote.getTamanhoSegementoBytes();
			segmento += "," + this.numAck + "," + this.tamJanela + ")";
			break;
		default:
			break;
		}
		

//		if(pacote.getDados().getBytes().length == 0) // vai enviar um segmento de controle, sem nada no campo dados
//			this.proxNumSeq++;
//		else 
//			this.proxNumSeq += pacote.getDados().getBytes().length;
		return segmento;
	}
	
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
		default:
			break;
		}
		
		if(pacoteRecebido.getDados().getBytes().length == 0) // recebeu um segmento de controle, sem nada no campo dados
			this.numAck++;
		else
			this.numAck += pacoteRecebido.getDados().getBytes().length;
		
		return segmento;
	}
	
	private void trataTX(){
		
	}
	
	private void trataRX(){
		
	}
}//fim da classe MaquinaDeEstados 2006