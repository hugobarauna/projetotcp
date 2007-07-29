/*
 * @(#)MaquinaDeEstadosFrame.java	1.0 18/08/2006
 *
 * Copyleft (L) 2006 Laboratrio de Arquitetura e Redes de Computadores
 * Escola Politcnica da Universidade de So Paulo.
 *
 */
package br.usp.larc.tcp.aplicacao;


import br.usp.larc.tcp.protocolo.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** 
 * Classe que representa a Interface HM Maquina de Estados.
 * Detalhes e dicas de implementao podem ser consultadas nas Apostilas.
 *
 * Procure sempre usar o paradigma Orientado a Objeto, a simplicidade e a 
 * criatividade na implementao do seu projeto.
 *  
 *
 * @author	Laboratrio de Arquitetura e Redes de Computadores.
 * @version	1.0 Agosto 2003.
 */
public class MaquinaDeEstadosFrame extends javax.swing.JFrame {
    
	public String estTX = "Idle";
	public String estRX = "Recv";
	public String numSeq = "0";
	public String numSeqNConf = "0";
	
    /** Creates new form MaquinaDeEstadosFrame */
    public MaquinaDeEstadosFrame() {
        initComponents();
    }
    
    /** Creates new form Monitor */
    public MaquinaDeEstadosFrame(MaquinaDeEstados _maquinaDeEstados) {
        this.maquinaDeEstados = _maquinaDeEstados;
        this.initComponents(); 
        this.setSize(590,550);
        this.idConexao = _maquinaDeEstados.getIdConexao();
        this.setTitle("M\u00e1quina de Estados - ID: " + 
             this.idConexao  + " => Canal: " +
            _maquinaDeEstados.getIpSimuladoLocalBytePonto() + " => Porta: " +
            _maquinaDeEstados.getPortaLocal());
        this.jTextFieldTimeout.setText(String.valueOf(this.maquinaDeEstados.getTempoTimeout()));
        this.show();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        jLabelnformacoesConexao = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButtonActiveOpen = new javax.swing.JButton();
        jButtonPassiveOpen = new javax.swing.JButton();
        jButtonEnviar = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jLabelHost = new javax.swing.JLabel();
        jLabelPorta = new javax.swing.JLabel();
        jTextFieldHost = new javax.swing.JTextField();
        jTextFieldPorta = new javax.swing.JTextField();
        jLabelDadosEnviados = new javax.swing.JLabel();
        jLabelNumBytesEnviados = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDadosEnviados = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jLabelEstado = new javax.swing.JLabel();
        jLabelPrimitiva = new javax.swing.JLabel();
        jLabelSegmento = new javax.swing.JLabel();
        jLabelDescricaoSegmento = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaEstado = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaPrimitiva = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaSegmento = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextAreaDescricaoSegmento = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jButtonAjuda = new javax.swing.JButton();
        jButtonReset = new javax.swing.JButton();
        jLabelTimeout = new javax.swing.JLabel();
        jLabelTamJan = new javax.swing.JLabel();
        jLabelTamSeg = new javax.swing.JLabel();
        jTextFieldTimeout = new javax.swing.JTextField();
        jTextFieldTamJan = new javax.swing.JTextField();
        jTextFieldTamSeg = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabelEstAtual = new javax.swing.JLabel();
        jLabelNumSeq = new javax.swing.JLabel();
        jLabelNumACK = new javax.swing.JLabel();
        jTextFieldNumSeq = new javax.swing.JTextField();
        jTextFieldEstAtual = new javax.swing.JTextField();
        jTextFieldNumACK = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextAreaDadosRecebidos = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        getContentPane().setLayout(null);
        
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel1.setLayout(null);

        jPanel1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Conex\u00e3o", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12), java.awt.Color.red));
        jLabelnformacoesConexao.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 10));
        jPanel1.add(jLabelnformacoesConexao);
        jLabelnformacoesConexao.setBounds(10, 20, 350, 30);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(5, 0, 370, 60);

        jPanel2.setLayout(null);

        jPanel2.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Primitivas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12), java.awt.Color.red));
        jButtonActiveOpen.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jButtonActiveOpen.setText("ActiveOpen");
        jButtonActiveOpen.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonActiveOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonActiveOpen.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonActiveOpen.setEnabled(true);
        jPanel2.add(jButtonActiveOpen);
        jButtonActiveOpen.setBounds(10, 20, 80, 30);
        jButtonActiveOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //jButtonEnviarActionPerformed(evt);
            	maquinaDeEstados.setIpSimuladoDestino(Decoder.bytePontoToIpSimulado(jTextFieldHost.getText()));
            	maquinaDeEstados.setPortaDestino(Integer.parseInt(jTextFieldPorta.getText()));
            	try {
            		// manda uma primitiva Active Open
            		maquinaDeEstados.recebePrimitiva(TCPIF.P_ACTIVEOPEN, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });

        jButtonPassiveOpen.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jButtonPassiveOpen.setText("PassiveOpen");
        jButtonPassiveOpen.setMaximumSize(new java.awt.Dimension(500, 500));
        jButtonPassiveOpen.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonPassiveOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPassiveOpen.setEnabled(true);
        jPanel2.add(jButtonPassiveOpen);
        jButtonPassiveOpen.setBounds(10, 60, 100, 30);
        jButtonPassiveOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //jButtonEnviarActionPerformed(evt);
            	//maquinaDeEstados.setIpSimuladoDestino(jTextFieldHost.getText());
            	//maquinaDeEstados.setPortaDestino(Integer.parseInt(jTextFieldPorta.getText()));
            	try {
            		// manda uma primitiva Active Open
            		maquinaDeEstados.recebePrimitiva(TCPIF.P_PASSIVEOPEN, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });

        jButtonEnviar.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jButtonEnviar.setText("Enviar");
        jButtonEnviar.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEnviarActionPerformed(evt);
            }
        });

        jPanel2.add(jButtonEnviar);
        jButtonEnviar.setBounds(140, 60, 100, 30);

        jButtonClose.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jButtonClose.setText("Close");
        jButtonClose.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.add(jButtonClose);
        jButtonClose.setBounds(260, 60, 100, 30);
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
					maquinaDeEstados.recebePrimitiva(TCPIF.P_CLOSE, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });

        jLabelHost.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelHost.setText("Host");
        jPanel2.add(jLabelHost);
        jLabelHost.setBounds(100, 20, 30, 15);

        jLabelPorta.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelPorta.setText("Porta");
        jPanel2.add(jLabelPorta);
        jLabelPorta.setBounds(290, 20, 30, 16);

        jTextFieldHost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldHostActionPerformed(evt);
            }
        });

        jPanel2.add(jTextFieldHost);
        jTextFieldHost.setBounds(130, 20, 160, 20);

        jTextFieldPorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPortaActionPerformed(evt);
            }
        });

        jPanel2.add(jTextFieldPorta);
        jTextFieldPorta.setBounds(320, 20, 40, 20);

        jLabelDadosEnviados.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelDadosEnviados.setText("Dados Enviados");
        jPanel2.add(jLabelDadosEnviados);
        jLabelDadosEnviados.setBounds(10, 90, 100, 15);

        jLabelNumBytesEnviados.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelNumBytesEnviados.setText("N\u00famero de byres enviados: 0");
        jPanel2.add(jLabelNumBytesEnviados);
        jLabelNumBytesEnviados.setBounds(140, 90, 170, 15);

        jScrollPane1.setViewportView(jTextAreaDadosEnviados);

        jPanel2.add(jScrollPane1);
        jScrollPane1.setBounds(10, 104, 350, 80);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(5, 60, 370, 190);

        jPanel3.setLayout(null);

        jPanel3.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Diagrama de Tempo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12), java.awt.Color.red));
        jLabelEstado.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelEstado.setText("Estado");
        jPanel3.add(jLabelEstado);
        jLabelEstado.setBounds(10, 20, 38, 15);

        jLabelPrimitiva.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelPrimitiva.setText("Primitiva");
        jPanel3.add(jLabelPrimitiva);
        jLabelPrimitiva.setBounds(100, 20, 60, 15);

        jLabelSegmento.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelSegmento.setText("Segmento");
        jPanel3.add(jLabelSegmento);
        jLabelSegmento.setBounds(170, 20, 60, 15);

        jLabelDescricaoSegmento.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelDescricaoSegmento.setText("(NS,Tam, NACK, T. Jan)");
        jPanel3.add(jLabelDescricaoSegmento);
        jLabelDescricaoSegmento.setBounds(230, 20, 130, 16);

        jScrollPane2.setViewportView(jTextAreaEstado);

        jPanel3.add(jScrollPane2);
        jScrollPane2.setBounds(10, 40, 90, 200);

        jScrollPane3.setViewportView(jTextAreaPrimitiva);

        jPanel3.add(jScrollPane3);
        jScrollPane3.setBounds(100, 40, 80, 200);

//        jTextAreaSegmento.setFont(new java.awt.Font("Times New Roman", 0, 14));
        jScrollPane4.setViewportView(jTextAreaSegmento);

        jPanel3.add(jScrollPane4);
        jScrollPane4.setBounds(180, 40, 40, 200);

        jScrollPane5.setViewportView(jTextAreaDescricaoSegmento);

        jPanel3.add(jScrollPane5);
        jScrollPane5.setBounds(220, 40, 140, 200);

        getContentPane().add(jPanel3);
        jPanel3.setBounds(5, 250, 370, 250);

        jPanel4.setLayout(null);

        jPanel4.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Configura\u00e7\u00f5es", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12), java.awt.Color.red));
        jButtonAjuda.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jButtonAjuda.setText("Ajuda");
        jButtonAjuda.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.add(jButtonAjuda);
        jButtonAjuda.setBounds(10, 110, 80, 20);

        jButtonReset.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jButtonReset.setText("Reset");
        jButtonReset.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResetActionPerformed(evt);
            }
        });

        jPanel4.add(jButtonReset);
        jButtonReset.setBounds(120, 110, 70, 20);

        jLabelTimeout.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelTimeout.setText("Timeout (ms):");
        jPanel4.add(jLabelTimeout);
        jLabelTimeout.setBounds(10, 20, 76, 15);

        jLabelTamJan.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelTamJan.setText("Tam. Jan. (bytes):");
        jPanel4.add(jLabelTamJan);
        jLabelTamJan.setBounds(10, 50, 96, 15);

        jLabelTamSeg.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelTamSeg.setText("Tam. Seg (bytes):");
        jPanel4.add(jLabelTamSeg);
        jLabelTamSeg.setBounds(10, 80, 95, 15);

        jTextFieldTimeout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTimeoutActionPerformed(evt);
            }
        });

        jPanel4.add(jTextFieldTimeout);
        jTextFieldTimeout.setBounds(110, 20, 80, 20);

        jPanel4.add(jTextFieldTamJan);
        jTextFieldTamJan.setBounds(110, 50, 80, 20);

        jPanel4.add(jTextFieldTamSeg);
        jTextFieldTamSeg.setBounds(110, 80, 80, 20);

        getContentPane().add(jPanel4);
        jPanel4.setBounds(380, 0, 200, 140);

        jPanel5.setLayout(null);

        jPanel5.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Informa\u00e7\u00f5es", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12), java.awt.Color.red));
        jLabelEstAtual.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelEstAtual.setText("Est Atual (Tx/Rx)");
        jPanel5.add(jLabelEstAtual);
        jLabelEstAtual.setBounds(10, 20, 89, 15);

        jLabelNumSeq.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelNumSeq.setText("N\u00ba Seq/Ult Conf");
        jPanel5.add(jLabelNumSeq);
        jLabelNumSeq.setBounds(10, 50, 90, 15);

        jLabelNumACK.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12));
        jLabelNumACK.setText("N\u00ba ACK");
        jPanel5.add(jLabelNumACK);
        jLabelNumACK.setBounds(10, 80, 39, 15);

        jPanel5.add(jTextFieldNumSeq);
        jTextFieldNumSeq.setBounds(100, 50, 90, 20);

        jTextFieldEstAtual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldEstAtualActionPerformed(evt);
            }
        });

        jPanel5.add(jTextFieldEstAtual);
        jTextFieldEstAtual.setBounds(100, 20, 90, 20);

        jPanel5.add(jTextFieldNumACK);
        jTextFieldNumACK.setBounds(100, 80, 90, 20);

        getContentPane().add(jPanel5);
        jPanel5.setBounds(380, 140, 200, 110);

        jPanel6.setLayout(null);

        jPanel6.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EtchedBorder(), "Dados Recebidos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Microsoft Sans Serif", 0, 12), java.awt.Color.red));
        jScrollPane6.setViewportView(jTextAreaDadosRecebidos);

        jPanel6.add(jScrollPane6);
        jScrollPane6.setBounds(10, 20, 180, 220);

        getContentPane().add(jPanel6);
        jPanel6.setBounds(380, 250, 200, 250);

        jLabel1.setForeground(new java.awt.Color(51, 51, 255));
        jLabel1.setText("MEFrame - Powered by Java Technologies - Copyleft (L)2003 LARC/USP");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel7.add(jLabel1);

        getContentPane().add(jPanel7);
        jPanel7.setBounds(10, 500, 570, 20);

        pack();
        

        
    }//GEN-END:initComponents

    private void jButtonEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnviarActionPerformed
        // Add your handling code here:
    	maquinaDeEstados.setIpSimuladoDestino(Decoder.bytePontoToIpSimulado(jTextFieldHost.getText()));
    	maquinaDeEstados.setPortaDestino(Integer.parseInt(jTextFieldPorta.getText()));
    	try {
			maquinaDeEstados.recebePrimitiva(TCPIF.P_SEND, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }//GEN-LAST:event_jButtonEnviarActionPerformed

    private void jTextFieldPortaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPortaActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_jTextFieldPortaActionPerformed

    private void jTextFieldEstAtualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEstAtualActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_jTextFieldEstAtualActionPerformed

    private void jTextFieldTimeoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTimeoutActionPerformed
        // Add your handling code here:
    	int timeout = Integer.parseInt(this.jTextFieldTimeout.getText());
    	this.maquinaDeEstados.setTempoTimeOut(timeout);
    }//GEN-LAST:event_jTextFieldTimeoutActionPerformed

    private void jTextFieldHostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldHostActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_jTextFieldHostActionPerformed

    private void jButtonResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetActionPerformed
         JOptionPane.showMessageDialog(null,"TimeoutTX: O host no enviou resposta.");
    }//GEN-LAST:event_jButtonResetActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        this.dispose();
        this.maquinaDeEstados.getMonitor().fechaMaquinaDeEstados(this.idConexao);
    }//GEN-LAST:event_exitForm
    
    public void setNumACK(int numACK)
    {
    	jTextFieldNumACK.setText(String.valueOf(numACK));
    }
    
    
    public void setEstadoRX(String estado)
    {
    	if(estado.equals(TCPIF.RECEIVING))
    	{
    		estRX = "Recv";
    	}
    	else if(estado.equals(TCPIF.RX_BLOCKED))
    	{
    		estRX = "Bloq";
    	}
    	jTextFieldEstAtual.setText(estTX + "/" + estRX);
    }
    
    public void setEstadoTX(String estado)
    {
    	if(estado.equals(TCPIF.WAITING_ACK))
    	{
    		estTX = "WAck";
    	}
    	else if(estado.equals(TCPIF.TX_BLOCKED))
    	{
    		estTX = "Bloq";
    	}
    	else if(estado.equals(TCPIF.IDLE))
    	{
    		estTX = "Idle";
    	}
    	else if(estado.equals(TCPIF.TRASMITTING))
    	{
    		estTX = "Trans";
    	}
    	jTextFieldEstAtual.setText(estTX + "/" + estRX);
    }
    
    public int getJanela()
    {
    	return Integer.parseInt(jTextFieldTamJan.getText());
    }
    
    public int getSegmento()
    {
    	return Integer.parseInt(jTextFieldTamSeg.getText());
    }
    
    public void setJanela(int janela)
    {
    	jTextFieldTamJan.setText(String.valueOf(janela));
    }
    
    public void setSegmento(int segmento)
    {
    	jTextFieldTamSeg.setText(String.valueOf(segmento));
    }
    
    public void setNumSeq(long numSeq)
    {
    	this.numSeq = String.valueOf(numSeq);
    	
    	jTextFieldNumSeq.setText(this.numSeq + "/"+ this.numSeqNConf);
    }
    
    public void setNumSeqNConf(long numSeqNConf)
    {
    	this.numSeqNConf = String.valueOf(numSeqNConf);
    	
    	jTextFieldNumSeq.setText(this.numSeq + "/"+ this.numSeqNConf);
    }
    
    /*
     * Atualiza os dados da area de texto da tabela do diagrama de tempo
     *
     * Exemplo:
     * MaquinaDeEstadosFrame mef = new MaquinaDeEstadosFrame();\
     * mef.atualizaDadosEstado("Closed", "Act.Open", "->", "(0,0,0,300)");   
     *
     *
     * @param _estado O novo estado da mquina de estados da conexo
     * @param _primitiva O tipo de primitiva que est sendo utilizada
     * @param _direcao A direo do segmento (enviado: -> ou recebido: <-)
     * @param _segmento O segmento (NS, Tam, NACK, T.Jan)
     */
    public void atualizaDadosEstado(String _estado, String _primitiva, 
        String _direcao, String _segmento) {

        StringBuffer campo1 = new StringBuffer();
        StringBuffer campo2 = new StringBuffer();
        StringBuffer campo3 = new StringBuffer();
        StringBuffer campo4 = new StringBuffer();

        campo1.append(jTextAreaEstado.getText());
        campo1.append(_estado + "\n");
        jTextAreaEstado.setText(campo1.toString());

        campo2.append(jTextAreaPrimitiva.getText());
        campo2.append(_primitiva + "\n");
        jTextAreaPrimitiva.setText(campo2.toString());

        campo3.append(jTextAreaSegmento.getText());
        campo3.append("  " + _direcao + "\n");
        jTextAreaSegmento.setText(campo3.toString());

        campo4.append(jTextAreaDescricaoSegmento.getText());
        campo4.append(_segmento + "\n");
        jTextAreaDescricaoSegmento.setText(campo4.toString());
    }
    
    /*
     * Atualiza na interface as informaes sobre a conexo
     * 
     * Exemplo:
     * MaquinaDeEstadosFrame mef = new MaquinaDeEstadosFrame();
     * mef.atualizaInfoConexao("Established", "10.0.0.1:3123", "1211", "", "");
     *
     *
     * @param _estado O estado atual da mquina de estados da conexo
     * @param _IPSimuladoLocal O IP Simulado Local da Conexo
     * @param _portaLocal A porta local da conexo
     * @param _IPSimuladoRemoto O IP Simulado Remoto da Conexo
     * @param _portaRemota A porta remota da conexo
     */
    public void atualizaInfoConexao(String _estado, String _IPSimuladoLocal,
        String _portaLocal, String _IPSimuladoRemoto, String _portaRemota) {

            StringBuffer buffer = new StringBuffer();
        
            if(!_IPSimuladoLocal.equals("")) {
                buffer.append( _estado + ". ( " + _IPSimuladoLocal + ", " +
                    _portaLocal);
            }

            if(!_IPSimuladoRemoto.equals("")) {
                buffer.append(", " + _IPSimuladoRemoto + ", " +
                    _portaRemota +  " )");
            } else {
                buffer.append(", -, - )");
                
            }
            
            jTextFieldHost.setText(_IPSimuladoLocal);
            jLabelnformacoesConexao.setText(buffer.toString());
          
    }
    
    
    
    
    // TODO
//    public void atualizaDadosRecebidos(String _dados)
//    {
//    	jTextAreaDadosRecebidos.setText(_dados);
//    }
    
    // TODO
    public String getDados()
    {
    	return jTextAreaDadosEnviados.getText();
    }
    
    public void setDadosRecebidos(String dadosRecebidos)
    {
    	this.jTextAreaDadosRecebidos.setText(dadosRecebidos);
    }
    
    public String getDadosRecebidos()
    {
    	return this.jTextAreaDadosRecebidos.getText();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        MaquinaDeEstadosFrame mef = new MaquinaDeEstadosFrame();
        mef.initComponents();
        mef.setSize(590,530);
        mef.show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonActiveOpen;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonEnviar;
    private javax.swing.JButton jButtonPassiveOpen;
    private javax.swing.JButton jButtonReset;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelDadosEnviados;
    private javax.swing.JLabel jLabelDescricaoSegmento;
    private javax.swing.JLabel jLabelEstAtual;
    private javax.swing.JLabel jLabelEstado;
    private javax.swing.JLabel jLabelHost;
    private javax.swing.JLabel jLabelNumACK;
    private javax.swing.JLabel jLabelNumBytesEnviados;
    private javax.swing.JLabel jLabelNumSeq;
    private javax.swing.JLabel jLabelPorta;
    private javax.swing.JLabel jLabelPrimitiva;
    private javax.swing.JLabel jLabelSegmento;
    private javax.swing.JLabel jLabelTamJan;
    private javax.swing.JLabel jLabelTamSeg;
    private javax.swing.JLabel jLabelTimeout;
    private javax.swing.JLabel jLabelnformacoesConexao;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextArea jTextAreaDadosEnviados;
    private javax.swing.JTextArea jTextAreaDadosRecebidos;
    private javax.swing.JTextArea jTextAreaDescricaoSegmento;
    private javax.swing.JTextArea jTextAreaEstado;
    private javax.swing.JTextArea jTextAreaPrimitiva;
    private javax.swing.JTextArea jTextAreaSegmento;
    private javax.swing.JTextField jTextFieldEstAtual;
    public javax.swing.JTextField jTextFieldHost;
    private javax.swing.JTextField jTextFieldNumACK;
    private javax.swing.JTextField jTextFieldNumSeq;
    public javax.swing.JTextField jTextFieldPorta;
    private javax.swing.JTextField jTextFieldTamJan;
    private javax.swing.JTextField jTextFieldTamSeg;
    private javax.swing.JTextField jTextFieldTimeout;
    // End of variables declaration//GEN-END:variables
    private MaquinaDeEstados maquinaDeEstados;
    private int idConexao;
}

// fim da classe MaquinaDeEstados 2006