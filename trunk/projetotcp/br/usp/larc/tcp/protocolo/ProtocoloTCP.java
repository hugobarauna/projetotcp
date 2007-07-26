package br.usp.larc.tcp.protocolo;

/*
 * @(#)ProtocoloTCP.java	1.0 18/08/2006
 *
 * Copyleft (L) 2006 Laboratrio de Arquitetura e Redes de Computadores.
 * Escola Politcnica da Universidade de So Paulo.
 *
 */

import br.usp.larc.tcp.ipsimulada.*;

/** 
 * Classe que encapasula de modo global todos as classes do Protocolo TCP 
 * Simulado.  nessa classe que voc voc vai implementar as aes que os
 * eventos que a Interfaces Monitor gera, fazendo com que as classes se 
 * comuniquem entre si de acordo com cada ao/evento. 
 *
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a 
 * criatividade na implementao do seu projeto.
 *  
 *
 * @author	Laboratrio de Arquitetura e Redes de Computadores.
 * @version	1.0 Agosto 2003.
 */

public class ProtocoloTCP implements TCPIF {
    
    /** 
     * Atributo que representa a camada IpSimulada.
     */        
    private IpSimulada camadaIpSimulada;
   
    /**
     * Atributo que representa se o canal IP esta aberto.
     */        
    private boolean camadaIPSimuladaAberta;
    
    /**
     * Objeto Monitor.
     */
    private Monitor monitor;
   
    //adicione aqui outros atributos importantes que voc julgar necessrio
        
    /** 
     * Construtor da classe ProtocoloTCP.
     */
    public ProtocoloTCP() {
    	this.init();
    }
    
    /** 
     * Mtodo que inicializa os atributos do Protocolo TCP.
     */
    public void init() {
        this.camadaIpSimulada = new IpSimulada();
	this.camadaIPSimuladaAberta = false;
        this.monitor = new Monitor(this);
    }
    
    /** 
     * Mtodo que recebe primitivas da camada de aplicao e executa as operaes
     * para atender a ao. As primitivas esto definidas na interface TCPIF.
     *
     * @param _primitiva A primitiva que a aplicao enviou.
     * @param args[] Um array de argumentos que a aplicao pode enviar.
     * @exception Exception  Caso ocorra algum erro ou exceo, lana (throw) 
     * para quem chamou o mtodo.
     */
    public void recebePrimitivaAplicacao(int _primitiva, String args[])
        throws Exception {
        
        switch (_primitiva) {
            case ProtocoloTCP.P_TCP_OPEN:
                try{
                    this.inicializaTcp();
                    break;
                } catch(Exception e) {
                    System.out.println("ProtocoloTCP: Erro " +
                        "recebePrimitivaAplicacao(P_TCP_OPEN)");
                    throw new Exception("Erro no recebimento de primitiva: " + 
                        ProtocoloTCP.P_TCP_OPEN + "\n" + e.getMessage());
                }
            case ProtocoloTCP.P_TCP_CLOSE:
                try{
                    this.finalizaTCP();
                    break;
                } catch(Exception e) {
                    System.out.println("ProtocoloTCP: Erro " +
                        "recebePrimitivaAplicacao(P_TCP_CLOSE_ME)");
                    throw new Exception("Erro no recebimento de primitiva: " + 
                        _primitiva + "\n" + e.getMessage());
                }
            case ProtocoloTCP.P_TCP_OPEN_ME:
                try{
                    int portaME = Integer.parseInt(args[0]);
                    this.criaMaquinaEstado(portaME);
                    break;
                } catch(Exception e) {
                    System.out.println("ProtocoloTCP: Erro " +
                        "recebePrimitivaAplicacao(P_TCP_OPEN_ME)");
                    throw new Exception("Erro no recebimento de primitiva: " + 
                        _primitiva + "\n" + e.getMessage());
                }
            case ProtocoloTCP.P_TCP_CLOSE_ME:
                try{
                    int idConexao = Integer.parseInt(args[0]);
                    this.fechaMaquinaEstado(idConexao);
                    break;
                } catch(Exception e) {
                    System.out.println("ProtocoloTCP: Erro " +
                        "recebePrimitivaAplicacao(P_TCP_OPEN_ME)");
                    throw new Exception("Erro no recebimento de primitiva: " + 
                        _primitiva + "\n" + e.getMessage());
                }
            case ProtocoloTCP.P_TCP_RESET:
                //adicione aqui o cdigo que trata a primitiva que reseta o
                //o protocolo TCP.
        }
    }
    
    /**
     * Método que inicializa o protocolo TCP.
     *
     * @exception Exception  Caso ocorra algum erro ou exceo, lana (throw) 
     * para quem chamou o mtodo.
     */
    public void inicializaTcp() throws Exception {
        if (!camadaIPSimuladaAberta) {
            try {
                System.out.println("Iniciando Protocolo TCP...");
                this.inicializaIpSimulada(
                    ProtocoloTCP.BUFFER_DEFAULT_IP_SIMULADA);
                String ipBytePonto =
				this.camadaIpSimulada.descobreCanalIPSimulado();
                this.monitor.setIpSimuladoLocal(
                    Decoder.bytePontoToIpSimulado(ipBytePonto));
                this.camadaIPSimuladaAberta = true;
                System.out.println("Iniciado:");
		System.out.println("\tIP Simulado do MONITOR  : " + ipBytePonto);
                this.monitor.monitoraCamadaIP();
            } catch (Exception e) {
                System.out.println("ProtocoloTCP.inicializaTcp(): " +
                    e.getMessage());
                throw e;
            }
        } else {
            System.out.println("Protocolo TCP j inicializado.");
        }
    }

    /**
     * Mtodo que finaliza o protocoloTCP e consequentemente o projeto.
     *
     * @exception Exception  Caso ocorra algum erro ou exceo, joga (throw) 
     * para quem chamou o mtodo.
     */
    public void finalizaTCP() throws Exception{
        if (camadaIPSimuladaAberta) {
            try {
               if (this.camadaIpSimulada != null) {
                    this.monitor.terminaMonitoramentoCamadaIP();
                    this.monitor.fechar();
                    this.monitor = null;
                    this.finalizaIpSimulada();
                    this.camadaIpSimulada = null;
                    System.out.println("Protocolo TCP finalizado.");
                    System.exit(0);
                } else {
                    System.out.println("Protocolo TCP no foi inicializado.");
                }
            } catch (Exception e) {
                System.out.println("ProtocoloTCP.finalizaTCP()" + e.getMessage());
                throw e;
            }
        } else {
                System.out.println("Protocolo TCP no foi inicializado.");
        }
    }
    
    /**
     * Mtodo que abre uma nova Mquina de Estados associada a uma porta TCP
     * recebida  como parmetro.
     *
     * @param _portaME A porta TCP que ser associada a mquina de estados.
     * @exception Exception  Caso ocorra algum erro ou exceo, lana (throw) 
     * para quem chamou o mtodo.
     */
    public void criaMaquinaEstado(int _portaME) throws Exception {
        if (camadaIPSimuladaAberta) {
            try {
                if (!monitor.criaMaquinaDeEstados(_portaME)) {
                    throw new Exception("Porta: " + _portaME + " j usada.");
                }
            } catch (Exception e) {
                System.out.println("ProtocoloTCP.inicializaTcp(): " + e.getMessage());
                throw e;
            }
        } else {
            throw new Exception("Protocolo TCP no inicializado.");
        }
    }
    
    /**
     * Mtodo que fecha mquina de estados com id de Conexo passada como 
     * parmetro.
     *
     * @param _idConexao O id da Conexo da mquina que voc quer fechar.
     * @exception Exception  Caso ocorra algum erro ou exceo, lana (throw) 
     * para quem chamou o mtodo.
     */
    public void fechaMaquinaEstado(int _idConexao) throws Exception {
        if (camadaIPSimuladaAberta) {
            try {
                if (!monitor.fechaMaquinaDeEstados(_idConexao)) {
                    throw new Exception("Id: " + _idConexao + " no existe.");
                }
            } catch (Exception e) {
                System.out.println("ProtocoloTCP.fechaMaquinaEstado(): " + 
                    e.getMessage());
                throw e;
            }
        } else {
            throw new Exception("Protocolo TCP no inicializado.");
        }
    }
    
    /**
     * Mtodo que reinicializa o protocolo TCP fazendo com que o protocolo volte
     * ao seu estado inicial .
     *
     * @exception Exception  Caso ocorra algum erro ou exceo, lana (throw) 
     * para quem chamou o mtodo.
     */
    public void reinicializaTcp() throws Exception {
        System.out.println("Reiniciando Protocolo TCP...");
        //implemente aqui o mtodo que reinicializa o Protocolo TCP.
    }
    
    /**
     * Mtodo que inicializa a camada IpSimulada.
     * 
     * @param buffer tamanho do buffer.
     * @exception Exception excecao jogada quando inicializa a camada IpSimulada.
     */
    public void inicializaIpSimulada(int buffer) throws Exception {
        try {
                this.camadaIpSimulada.inicializaCanal(buffer);
                this.camadaIPSimuladaAberta = true;
        } catch (Exception e) {
             System.out.println("ProtocoloTCP.inicializaIpSimulada(): "  +
                e.getMessage());
             throw e;
        }
    }
    
    /** 
     * Mtodo que finaliza a camada ipSimulada.
     * @exception Exception excecao joagada quando a ipSimulada  fechada.
     */
    public void finalizaIpSimulada() throws Exception {
        try {
            this.camadaIpSimulada.finalizaCanal();
            this.camadaIPSimuladaAberta = false;
        } catch (Exception e) {
            System.out.println("ProtocoloTCP.finalizaIpSimulada(): " + 
                e.getMessage());
            throw e;
        }
    }
    
    /** Mtodo acessador para o atributo camadaIpSimulada.
     * @return A referncia para o atributo camadaIpSimulada.
     *
     */
    public IpSimulada getCamadaIpSimulada() {
        return camadaIpSimulada;
    }
    
    /** Mtodo modificador para o atributo camadaIpSimulada.
     * @param camadaIpSimulada Novo valor para o atributo camadaIpSimulada.
     *
     */
    public void setCamadaIpSimulada(IpSimulada _camadaIpSimulada) {
        this.camadaIpSimulada = _camadaIpSimulada;
    }

    /** 
     * Mtodo acessador para o atributo camadaIPSimuladaAberta que verifica
     * se a camada IP Simulada j est aberta para esse protocolo.
     * 
     * @return Valor do atributo camadaIPSimuladaAberta.
     */
    public boolean getCamadaIPSimuladaAberta() {
        return camadaIPSimuladaAberta;
    }    
    
    /** 
     * Mtodo modificador para o atributo camadaIPSimuladaAberta.
     * 
     * @param isCanalIpAberto Novo valor para o atributo camadaIPSimuladaAberta.
     */
    public void setCamadaIPSimuladaAberta(boolean _camadaIPSimuladaAberta) {
        this.camadaIPSimuladaAberta = _camadaIPSimuladaAberta;
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
 
    /*
     * Mtodo que executa o projeto.
     */
    public static void main(String args[]) {
    	// Cria uma instncia do simulador TCP
        ProtocoloTCP protocoloTCP = new ProtocoloTCP();
        System.out.println("Iniciando Projeto...");
    }

}//fim da classe ProtocoloTCP 2006