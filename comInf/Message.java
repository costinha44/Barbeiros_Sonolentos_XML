package comInf;

import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import genclass.GenericIO;

/**
 *   Este tipo de dados define as mensagens que são trocadas entre os clientes e o servidor numa solução do Problema
 *   dos Barbeiros Sonolentos que implementa o modelo cliente-servidor de tipo 2 (replicação do servidor) com lançamento
 *   estático dos threads barbeiro.
 *   A comunicação propriamente dita baseia-se na troca de strings XML num canal TCP.
 */

public class Message
{
  /* Tipos das mensagens */

  /**
   *  Inicialização do ficheiro de logging (operação pedida pelo cliente)
   *    @serialField SETNFIC
   */

   public static final int SETNFIC  =  1;

  /**
   *  Ficheiro de logging foi inicializado (resposta enviada pelo servidor)
   *    @serialField NFICDONE
   */

   public static final int NFICDONE =  2;

  /**
   *  Corte de cabelo (operação pedida pelo cliente)
   *    @serialField REQCUTH
   */

   public static final int REQCUTH  =  3;

  /**
   *  Cabelo cortado (resposta enviada pelo servidor)
   *    @serialField CUTHDONE
   */

   public static final int CUTHDONE =  4;

  /**
   *  Barbearia cheia (resposta enviada pelo servidor)
   *    @serialField BSHOPF
   */

   public static final int BSHOPF   =  5;

  /**
   *  Alertar o thread barbeiro do fim de operações (operação pedida pelo cliente)
   *    @serialField ENDOP
   */

   public static final int ENDOP    =  6;

  /**
   *  Operação realizada com sucesso (resposta enviada pelo servidor)
   *    @serialField ACK
   */

   public static final int ACK      =  7;

  /**
   *  Mandar o barbeiro dormir (operação pedida pelo cliente)
   *    @serialField GOTOSLP
   */

   public static final int GOTOSLP  =  8;

  /**
   *  Continuação do ciclo de vida do barbeiro (resposta enviada pelo servidor)
   *    @serialField CONT
   */

   public static final int CONT     =  9;

  /**
   *  Terminação do ciclo de vida do barbeiro (resposta enviada pelo servidor)
   *    @serialField END
   */

   public static final int END      = 10;

  /**
   *  Chamar um cliente pelo barbeiro (operação pedida pelo cliente)
   *    @serialField CALLCUST
   */

   public static final int CALLCUST = 11;

  /**
   *  Enviar a identificação do cliente (resposta enviada pelo servidor)
   *    @serialField CUSTID
   */

   public static final int CUSTID   = 12;

  /**
   *  Receber pagamento pelo barbeiro (operação pedida pelo cliente)
   *    @serialField GETPAY
   */

   public static final int GETPAY   = 13;

  /**
   *  Shutdown do servidor (operação pedida pelo cliente)
   *    @serialField SHUT
   */

   public static final int SHUT   = 14;


  /* Campos das mensagens */

  /**
   *  Tipo da mensagem
   *    @serialField msgType
   */

   private int msgType;

  /**
   *  Identificação do cliente
   *    @serialField custId
   */

   private int custId;

  /**
   *  Identificação do barbeiro
   *    @serialField custId
   */

   private int barbId;

  /**
   *  Nome do ficheiro de logging
   *    @serialField fName
   */

   private String fName;

  /**
   *  Número de iterações do ciclo de vida dos clientes
   *    @serialField nIter
   */

   private int nIter;

  /**
   *  Instanciação de uma mensagem (forma 1).
   *
   *    @param type tipo da mensagem
   */

   public Message (int type)
   {
      msgType = type;
      custId = -1;
      barbId = -1;
      fName = null;
      nIter = -1;
   }

  /**
   *  Instanciação de uma mensagem (forma 2).
   *
   *    @param type tipo da mensagem
   *    @param id identificação do cliente/barbeiro
   */

   public Message (int type, int id)
   {
      msgType = type;
      if ((msgType == REQCUTH) || (msgType == CUSTID))
         { custId= id;
           barbId = -1;
         }
         else { custId = -1;
                barbId = id;
              }
      fName = null;
      nIter = -1;
   }

  /**
   *  Instanciação de uma mensagem (forma 3).
   *
   *    @param type tipo da mensagem
   *    @param barbId identificação do barbeiro
   *    @param custId identificação do cliente
   */

   public Message (int type, int barbId, int custId)
   {
      msgType = type;
      this.barbId= barbId;
      this.custId= custId;
      fName = null;
      nIter = -1;
   }

  /**
   *  Instanciação de uma mensagem (forma 4).
   *
   *    @param type tipo da mensagem
   *    @param name nome do ficheiro de logging
   *    @param nIter número de iterações do ciclo de vida dos clientes
   */

   public Message (int type, String name, int nIter)
   {
      msgType = type;
      custId = -1;
      barbId = -1;
      fName= name;
      this.nIter = nIter;
   }

  /**
   *  Instanciação de uma mensagem (forma 5).
   *
   *    @param stringXML mensagem em formato XML
   */

   public Message (String stringXML)
   {
      InputSource in = new InputSource (new StringReader (stringXML));
      SAXParserFactory spf;
      SAXParser saxParser = null;

      spf = SAXParserFactory.newInstance ();
      spf.setNamespaceAware (false);
      spf.setValidating (false);
      try
      { saxParser = spf.newSAXParser ();
        saxParser.parse (in, new HandlerXML ());
      }
      catch (ParserConfigurationException e)
      { GenericIO.writelnString ("Erro na instanciação do parser (configuração): " + e.getMessage () + "!");
        System.exit (1);
      }
      catch (SAXException e)
      { GenericIO.writelnString ("Erro na instanciação do parser (SAX): " + e.getMessage () + "!");
        System.exit (1);
      }
      catch (IOException e)
      { GenericIO.writelnString ("Erro na execução do parser (SAX): " + e.getMessage () + "!");
        System.exit (1);
      }
   }

  /**
   *  Obtenção do valor do campo tipo da mensagem.
   *
   *    @return tipo da mensagem
   */

   public int getType ()
   {
      return (msgType);
   }

  /**
   *  Obtenção do valor do campo identificador do cliente.
   *
   *    @return identificação do cliente
   */

   public int getCustId ()
   {
      return (custId);
   }

  /**
   *  Obtenção do valor do campo identificador do barbeiro.
   *
   *    @return identificação do barbeiro
   */

   public int getBarbId ()
   {
      return (barbId);
   }

  /**
   *  Obtenção do valor do campo nome do ficheiro de logging.
   *
   *    @return nome do ficheiro
   */

   public String getFName ()
   {
      return (fName);
   }

  /**
   *  Obtenção do valor do campo número de iterações do ciclo de vida dos clientes.
   *
   *    @return número de iterações do ciclo de vida dos clientes
   */

   public int getNIter ()
   {
      return (nIter);
   }

  /**
   *  Impressão dos campos internos.
   *  Usada para fins de debugging.
   *
   *    @return string contendo, em linhas separadas, a concatenação da identificação de cada campo e valor respectivo
   */

   @Override
   public String toString ()
   {
      return ("Tipo = " + msgType +
              "\nId Cliente = " + custId +
              "\nId Barbeiro = " + barbId +
              "\nNome Fic. Logging = " + fName +
              "\nN. de Iteracoes = " + nIter);
   }

  /**
   *  Conversão para um string XML.
   *
   *    @return string contendo a descrição XML da mensagem
   */

   public String toXMLString ()
   {
      return ("<Mensagem>" +
                "<Tipo>" + msgType + "</Tipo>" +
                "<IdCliente>" + custId + "</IdCliente>" +
                "<IdBarbeiro>" + barbId + "</IdBarbeiro>" +
                "<NomeFicLogging>" + fName + "</NomeFicLogging>" +
                "<NdeIteracoes>" + nIter + "</NdeIteracoes>" +
              "</Mensagem>");
   }

  /**
   *  Este tipo de dados define o handler que gere os acontecimentos que ocorrem durante o parsing de um string XML.
   *
   */

   private class HandlerXML extends DefaultHandler
   {

     /**
      *  Parsing do string XML em curso
      *    @serialField parsing
      */

      private boolean parsing;

     /**
      *  Parsing de um elemento em curso
      *    @serialField element
      */

      private boolean element;

     /**
      *  Nome do elemento em processamento
      *    @serialField elemName
      */

      private String elemName;

     /**
      *  Início do processamento do string XML.
      *
      */

      @Override
      public void startDocument () throws SAXException
      {
         msgType = -1;
         custId = -1;
         barbId = -1;
         fName = null;
         nIter = -1;
         parsing = true;
      }


     /**
      *  Fim do processamento do string XML.
      *
      */

      @Override
      public void endDocument () throws SAXException
      {
         parsing = false;
      }

     /**
      *  Início do processamento de um elemento do string XML.
      *
      */

      @Override
      public void startElement(String namespaceURI, String localName,
                               String qName, Attributes atts) throws SAXException
      {
         element = parsing;
         if (parsing) elemName = qName;
      }

     /**
      *  Fim do processamento de um elemento do string XML.
      *
      */

      @Override
      public void endElement(String namespaceURI, String localName, String qName) throws SAXException
      {
         element = false;
         elemName = null;
      }

     /**
      *  Processamento do conteúdo de um elemento do string XML.
      *
      */

      @Override
      public void characters(char[] ch, int start, int length) throws SAXException
      {
         String elem;

         elem = new String (ch, start, length);
         if (parsing && element)
            { if (elemName.equals ("Tipo")) {msgType = new Integer (elem); return;}
              if (elemName.equals ("IdCliente")) {custId = new Integer (elem); return;}
              if (elemName.equals ("IdBarbeiro")) {barbId = new Integer (elem); return;}
              if (elemName.equals ("NomeFicLogging")) {fName = elem; return;}
              if (elemName.equals ("NdeIteracoes")) nIter = new Integer (elem);
            }
      }
   }

  /**
   *  Este tipo de dados define o handler que gere os acontecimentos que ocorrem durante o parsing de um string XML.
   *
   */

   public static void main (String [] args)
   {
      Message msg1 = new Message (3, "logging.txt", 10);
      String str = msg1.toXMLString ();
      Message msg2 = new Message (str);
      System.out.println ("Mensagem" + msg2.toString ());
   }
}