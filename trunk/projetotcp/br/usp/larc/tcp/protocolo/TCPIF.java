package br.usp.larc.tcp.protocolo;

/*
 * @(#)TCPIF.java	1.0 18/08/2006
 *
 * Copyleft (L) 2006 Laboratrio de Arquitetura e Redes de Computadores
 * Escola Politcnica da Universidade de So Paulo.
 *
 */

/** 
 * Interface que contm os principais eventos/estados do Protocolo TCP
 * Voc deve adicionar/remover atributos nessa interface, se necessrio.
 * 
 *
 * @author	Laboratrio de Arquitetura e Redes de Computadores.
 * @version	1.0 Agosto 2003.
 */
public interface TCPIF {
    	
        // Estados da conexo TCP de 200 a 209
        public static final String CLOSED                   = "Closed";
        public static final String LISTEN                   = "Listen";
        public static final String SYNRCVD                  = "SynRcvd";
        public static final String SYNSENT                  = "SynSent";
        public static final String ESTABLISHED              = "Estab.";
        public static final String CLOSEWAIT                = "CloseWait";
        public static final String FINWAIT_1                = "FinWait_1";
        public static final String FINWAIT_2                = "FinWait_2";
        public static final String CLOSING                  = "Closing";
        public static final String TIMEWAIT                 = "TimeWait";
        public static final String LAST_ACK                 = "LastAck";

        // Estados da maquina de transmissao
        public static final String IDLE						= "Idle";
        public static final String TRASMITTING				= "Transmitting";
        public static final String WAITING_ACK				= "Waiting_ACK";
        public static final String BLOCKED					= "Blocked";
        
        //Primitivas de 001 at 004 (relacionadas com o frame Monitor)
        public static final int P_TCP_OPEN                  = 000;
        public static final int P_TCP_CLOSE                 = 001;
        public static final int P_TCP_RESET                 = 002;
        public static final int P_TCP_OPEN_ME               = 003;
        public static final int P_TCP_CLOSE_ME              = 004;

        //Primitivas de 100 ate 109 (relacionadas com o frame Mquina de Estado)
        public static final byte P_NENHUM                   = 100;
        public static final byte P_PASSIVEOPEN              = 101;
        public static final byte P_CLOSE                    = 102;
        public static final byte P_ACTIVEOPEN               = 103;
        public static final byte P_SEND                     = 104;
        public static final byte P_OPENSUCCESS              = 105;
        public static final byte P_OPENID                   = 106;
        public static final byte P_ERROR                    = 107;
        public static final byte P_CLOSING                  = 108;
        public static final byte P_TERMINATE                = 109;
        public final static byte P_TIMEOUT                  = 110;

        // TimeOuts
        public final static int MAX_RETRANSMISSOES          = 10;
        public static final int T_TIMEOUT                   = 150;
        public static final int T_ESTOURO_RETRANSMISSOES    = 151;
        // Maximum Segment Lifetime
        public static final int T_TIMEOUT_TX                = 2000;
        
        // Tamanho do segmento (no caso, um datagrama UDP) default que camada 
        // IP Simulada recebe e envia por vez

        public static final int BUFFER_DEFAULT_IP_SIMULADA  = 1024;
        
        // Campos de controle dos Segmentos (ver tabela da apostila 1) (PDU)
        public static final byte S_NENHUM                   = 0x00;
        public static final byte S_SYN                      = 0x2;
        public static final byte S_ACK                      = 0x10;
        public static final byte S_SYN_ACK                  = 0x12;
        public static final byte S_FIN                      = 0x01;
        public static final byte S_FIN_ACK                  = 0x11;
        public static final byte S_URG                      = 0x20;
        public static final byte S_PSH                      = 0x8;
        public static final byte S_RST                      = 0x4;

}//fim da interface TCPIF 2006