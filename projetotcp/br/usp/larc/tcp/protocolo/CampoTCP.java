package br.usp.larc.tcp.protocolo;

/*
 * @(#)PacoteTCP.java	1.0 18/08/2006
 *
 * Copyleft (L) 2006 Laborat�rio de Arquitetura e Redes de Computadores
 * Escola Polit�cnica da Universidade de S�o Paulo.
 *
 */

/**  
 *
 * Essa classe � respons�vel pela decodifi��o e codifica��o de Campos do pacote
 * TCP que contenham String que ocupem mais de um byte. Ele codifica e 
 * decodifica String em array de bytes e vice-versa.
 *
 * @author Laborat�rio de Arquitetura e Redes de Computadores.
 * @version	1.0 Agosto 2003.
 */
public class CampoTCP {
	
    /**
     * Array auxliar para ajuda na manipula��o.
     */
    char letras[]       = {'a', 'a', 'a', 'a'};

    /**
     * Tamanho do campo em bytes.
     */
    int tamanhoCampo    = 0;

    /**
     * Construtor da classe CampoTCP.
     *
     * @param  _n  Tamanho do campo em bytes.
     */
    public CampoTCP(int _n) {
        this.tamanhoCampo = _n;
    }

    /**
     * Construtor da classe CampoTCP.
     *
     * @param  _n  Tamanho do campo em bytes.
     * @param  _l  Array de bytes que cont�m os dados para preencher o campo.
     */
    public CampoTCP(int _n, char[] _l) {
        this(_n);
        int i;
        for (i = 0; i < _n; i++) {
            this.letras[i] = _l[i];
        }
    }

    /**
     * Construtor da classe CampoTCP.
     *
     * @param  n  Tamanho do campo em bytes.
     * @param  s  String que cont�m os dados para preencher o campo.
     */
    public CampoTCP(int _n, String _s) {
        this(_n);
        int i;
        for (i = 0; i < _n; i++) {
            this.letras[i] = _s.charAt(i);
        }
    }

    /**
     * Construtor da classe CampoTCP.
     *
     * @param  _n              Tamanho do campo em bytes.
     * @param  _value          Long que cont�m os dados para preencher o campo.
     * @exception  Exception  Gera Exception, se ocorrer algum erro.
     */
    public CampoTCP(int _n, long _value) throws Exception {
        this(_n);
        this.setValue(_value);

    }

    /**
     * Construtor da classe CampoTCP.
     *
     * @param  _n              Tamanho do campo em bytes.
     * @param  _value          Int que cont�m os dados para preencher o campo.
     * @exception  Exception  Gera Exception, se ocorrer algum erro.
     */
    public CampoTCP(int _n, int _value) throws Exception {
        this(_n);
        this.setValue(_value);
    }

    /**
     * Construtor da classe CampoTCP.
     *
     * @param  n              Tamanho do campo em bytes.
     * @param  value          Short que cont�m os dados para preencher o campo.
     * @exception  Exception  Gera Exception, se ocorrer algum erro.
     */
    public CampoTCP(int _n, short _value) throws Exception {
        this(_n);
        this.setValue(_value);
    }

    /**
     * Retorna o char do campo que est� no indice como par�metro.
     *
     * @param  i              �ndice do char que quer ser retornado.
     * @return                O char com o valor daquele byte.
     * @exception  Exception  Gera Exception, se o �ndice estourar o array do
     * campo.
     */
    public char getLetraAt(int _indice) throws Exception {
        if (_indice < this.tamanhoCampo) {
            return this.letras[_indice];
        } else {
            throw new Exception("Out of range!");
        }
    }

    /**
     *  Retorna um array de char que representa o campo.
     *
     *@return    O array de char.
     */
    public char[] getLetras() {
        return this.letras;
    }

    /**
     * M�todo acessador para o atributo tamanhoCampo.
     *
     * @return    O valor do atributo tamanhoCampo.
     */
    public int getTamanhoCampo() {
        return this.tamanhoCampo;
    }

    /**
     * M�todo modificador para o atributo tamanhoCampo.
     *
     * @param  n  O novo valor para o atributo tamanhoCampo.
     */
    public void setCampoBytes(int n) {
        this.tamanhoCampo = n;
    }

    /**
     * M�todo que retorna o valor do Campo TCP com sua representa��o n�merica
     * decimal.
     *
     * @return    O valor do campo com sua representa��o num�rica.
     */
    public long getValue() {
        long n  = 0;
        for (int i = 0; i < this.tamanhoCampo; i++) {
            n = n + (long) Math.pow((double) 256, (double) i) *
                (long) this.letras[i];
        }
        return n;
    }

    /**
     * M�todo usado para campos com 4 (quatro) bytes. Converte um Campo TCP que 
     * pode ter valor entre 0 e 4294967295 (2 (dois) elevado a 32 menos 1(um)) 
     * em um array com 4 (quatro) bytes e encapsula esse valor no objeto CampoTCP.
     *
     * @param  value          Valor do campo.
     * @exception  Exception  Caso o valor n�o esteja entre 0 e 4294967295, gera
     * exce��o.
     */
    public void setValue(long _value) throws Exception {
        if (this.tamanhoCampo == 4) {
            if ((_value >= 0) && (_value < (long) Math.pow(
                (double) 2, (double) 32))) {

                this.letras[0] = (char) (_value % 256);
                this.letras[1] = (char) ((_value % 65536) / 256);
                this.letras[2] = (char) ((_value / 65536) % 256);
                this.letras[3] = (char) ((_value / 65536) / 256);
            } else {
                throw new Exception("Too big or too small!");
            }
        } else {
            throw new Exception("Value out of range!");
        }
    }
    
    /**
     * M�todo usado para campos com 2 (dois) bytes. Converte um Campo TCP que
     * pode ter valor entre 0 e 65535 (2 (dois) elevado a 16 menos 1(um)) em um 
     * array com 2 (dois) bytes e encapsula esse valor no objeto CampoTCP. 
     *
     * @param  value          Valor do campo.
     * @exception  Exception  Caso o valor n�o esteja entre 0 e 65535, gera
     * exce��o.
     */
    public void setValue(int value) throws Exception {
        if (this.tamanhoCampo >= 2) {
            if ((value >= 0) && (value < 65536)) {
                this.letras[0] = (char) (value % 256);
                this.letras[1] = (char) (value / 256);
            } else {
                throw new Exception("Too big or too small!");
                }
        } else {
            throw new Exception("Value out of range!");
        }
    }

    /**
     * M�todo usado para campos com 1 (um) bytes. Converte um Campo TCP que
     * pode ter valor entre 0 e 255 (2 (dois) elevado a 8 menos 1(um)) em um 
     * array com 1 (um) byte e encapsula esse valor no objeto CampoTCP. 
     *
     * @param  value          Valor do campo.
     * @exception  Exception  Caso o valor n�o esteja entre 0 e 255, gera
     * exce��o.
     */
    public void setValue(short value) throws Exception {
        if ((value >= 0) && (value < 256)) {
            this.letras[0] = (char) value;
        } else {
            throw new Exception("Too big or too small!");
        }
    }

    /**
     * Retorna o valor do Campo em String (no caso, codificado em bytes).
     *
     * @return    O String com o valor do Campo codificado em bytes.
     */
    public String toString() {
        String s  = new String(this.letras);
        return s.substring(0, this.tamanhoCampo);
    }

    /**
     * M�todo que ilustra funcionamento da classe CampoTCP.
     *
     */
    public static void main(String[] args) {
        try {
            
            /*faz testes dos limites
            System.out.println("Teste Limite long:");
            long limiteLong= (long)(Math.pow((double) 2, (double) 32)-1);
            CampoTCP teste1 = new CampoTCP(4,limiteLong);
            System.out.println("Campo codificado: " + teste1.toString());
            System.out.println("Campo decodificado: " + teste1.getValue());
            
            System.out.println("Teste Limite int:");
            int limiteInt= (int)(Math.pow((double) 2, (double) 16)-1);
            CampoTCP teste2 = new CampoTCP(2,limiteInt);
            System.out.println("Campo codificado: " + teste2.toString());
            System.out.println("Campo decodificado: " + teste2.getValue());
            
            System.out.println("Teste Limite short:");
            short limiteShort= (short)(Math.pow((double) 2, (double) 8)-1);
            CampoTCP teste3 = new CampoTCP(1,limiteShort);
            System.out.println("Campo codificado: " + teste3.toString());
            System.out.println("Campo decodificado: " + teste3.getValue());
            */
             
            for (int n = 0; n < 65536; n++) {
                CampoTCP ns  = null;
                CampoTCP r   = null;
                if (n < 256) {
                    ns = new CampoTCP(1, (short) n);
                    r = new CampoTCP(1, ns.toString());
                } else {
                    ns = new CampoTCP(2, n);
                    r = new CampoTCP(2, ns.toString());
                }
                    System.out.println(n + " " + r.getValue());
                }
                for (long m = (long) Math.pow((double) 2, (double) 31);
                    m < (long) Math.pow((double) 2, (double) 32); m++) {

                    CampoTCP ns  = new CampoTCP(4, m);
                    CampoTCP r   = new CampoTCP(4, ns.toString());
                    System.out.println(m + " " + r.getValue());
                }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
// fim da classe CampoTCP 2006