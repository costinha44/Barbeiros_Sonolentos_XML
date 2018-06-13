package serverSide;

import genclass.GenericIO;
import genclass.TextFile;
import comInf.MemFIFO;

/**
 *   Este tipo de dados define a barbearia que constitui o serviço prestado numa solução do Problema dos Barbeiros
 *   Sonolentos que implementa o modelo cliente-servidor de tipo 2 (replicação do servidor) com lançamento estático dos
 *   threads barbeiro.
 */

public class BarberShop
{
  /* Estado das entidades intervenientes */

  /**
   *  O barbeiro está a dormir (o thread correspondente não foi ainda instanciado)
   *
   *    @serialField SLEEPING
   */

   private final static int SLEEPING = 0;

   /* Estado das entidades intervenientes */

  /**
 /**
   *  O barbeiro está a trabalhar
   *
   *    @serialField WORKING
   */

   private final static int WORKING  = 1;

  /**
   *  O cliente realiza as tarefas do dia a dia
   *
   *    @serialField LIVNORML
   */

   private final static int LIVNORML = 0;

  /**
   *  O cliente quer cortar o cabelo
   *
   *    @serialField WANTCUTH
   */

   private final static int WANTCUTH = 1;

  /**
   *  O cliente senta-se aguardando a sua vez
   *
   *    @serialField WAITTURN
   */

   private final static int WAITTURN = 2;

  /**
   *  O cliente está a cortar o cabelo
   *
   *    @serialField CUTHAIR
   */

   private final static int CUTHAIR  = 3;

  /* Campos internos */

  /**
   *  Número de barbeiros que trabalham na barbearia (por defeito, 2)
   *
   *    @serialField nBarber
   */

   private final int nBarber = 2;

  /**
   *  Número de clientes que frequentam a barbearia (por defeito, 5)
   *
   *    @serialField nCustomer
   */

   private final int nCustomer = 5;

  /**
   *  Número de cortes de cabelo solicitados e ainda não servidos
   *
   *    @serialField nReqCut
   */

   private int nReqCut;

  /**
   *  Número de cadeiras de espera
   *
   *    @serialField nWaitChair
   */

   private final int nWaitChair = 2;

  /**
   *  Estado presente dos barbeiros
   *
   *    @serialField stateBarber
   */

   private int [] stateBarber;

  /**
   *  Estado presente dos clientes
   *
   *    @serialField stateCustomer
   */

   private int [] stateCustomer;

  /**
   *  Fila de espera (clientes) aguardando o corte do cabelo
   *
   *    @serialField sitCustomer
   */

   private MemFIFO sitCustomer;

  /**
   *  Nome do ficheiro de logging
   *
   *    @serialField fileName
   */

   private String fileName = "log.txt";

  /**
   *  Número de iterações do ciclo de vida dos clientes
   *
   *    @serialField nIter
   */

   private int nIter = 0;

  /**
   *  Registo dos barbeiros a dormir
   *
   *    @serialField registry
   */

   private Thread [] registry;

  /**
   *  Instanciação da barbearia.
   */

   public BarberShop ()
   {
     /* inicializar o estado interno */

      stateBarber = new int [nBarber];
      registry = new Thread [nBarber];
      stateCustomer = new int [nCustomer];
      for (int i = 0; i < nBarber; i++)
      { stateBarber[i] = SLEEPING;
        registry[i] = null;
      }
      for (int i = 0; i < nCustomer; i++)
        stateCustomer[i] = LIVNORML;
      nReqCut = 0;
      sitCustomer = new MemFIFO (nWaitChair);

     /* inicializar o ficheiro de logging */

      reportInitialStatus ();
   }

  /*
   *  Definição das operações efectuadas sobre o monitor:
   *     - inicializar o ficheiro de logging
   *     - ir cortar o cabelo
   *     - dormir
   *     - chamar o cliente
   *     - receber o pagamento pelo serviço prestado
   *     - acordar intempestivamente um barbeiro para sinalizar fim de operações.
   */

  /**
   *  Inicialização do ficheiro de logging.
   *  Significa:
   *    <li>actualizar o nome do ficheiro e o número de iterações
   *    <li>escrever o cabeçalho e o estado inicial.
   *
   *    @param fileName nome do ficheiro de logging
   *    @param nIter número de iterações do ciclo de vida dos clientes
   */

   public synchronized void setFileName (String fileName, int nIter)
   {
      if ((fileName != null) && !("".equals (fileName))) this.fileName = fileName;
      if (nIter > 0) this.nIter = nIter;
      reportInitialStatus ();
   }

  /**
   *  Operação de ir cortar o cabelo (originada pelo cliente).
   *
   *    @param customerId identificação do cliente
   *
   *    @return <li> true, se conseguiu cortar o cabelo
   *            <li> false, em caso contrário
   */

   public synchronized boolean goCutHair (int customerId)
   {
      stateCustomer[customerId] = WANTCUTH;                // sinaliza que quer cortar o cabelo
      reportStatus ();

      if (sitCustomer.full ())                             // verifica se a barbearia está cheia
         return (false);                                   // em caso afirmativo, não entra

      stateCustomer[customerId] = WAITTURN;                // sinaliza que vai esperar pelo corte de cabelo
      reportStatus ();
      sitCustomer.write (new Integer (customerId));        // senta-se numa cadeira de espera
      nReqCut += 1;                                        // pede um corte de cabelo
      notifyAll ();                                        // tenta acordar um barbeiro

      while (stateCustomer[customerId] != LIVNORML)        // aguarda a continuação das operações
      { try
        { wait ();
        }
        catch (InterruptedException e) {}
      }

      return (true);                                       // abandona a barbearia com o cabelo cortado
   }

  /**
   *  Operação de ir dormir (originada pelo barbeiro).
   *
   *    @param barberId identificação do barbeiro
   *
   *    @return <li> true, se o seu ciclo de vida chegou ao fim
   *            <li> false, em caso contrário
   */

   public synchronized boolean goToSleep (int barberId)
   {
      while (nReqCut == 0)                                 // aguarda a solicitação de um corte de cabelo
      { registry[barberId] = Thread.currentThread ();      // registar o thread barbeiro
        try
        { wait ();
        }
        catch (InterruptedException e)
        { registry[barberId] = null;                       // elimina o registo
          return true;                                     // sinaliza o fim do ciclo de vida
        }
      }

      if (nReqCut > 0) nReqCut -= 1;                       // toma nota do pedido de corte de cabelo
      registry[barberId] = null;                           // elimina o registo

      return false;
   }

  /**
   *  Operação de chamar o cliente (originada pelo barbeiro).
   *
   *    @param barberId identificação do barbeiro
   *
   *    @return identificação do cliente
   */

   public synchronized int callCustomer (int barberId)
   {
      int customerId;                                                // identificação do cliente

      stateBarber[barberId] = WORKING;                               // sinaliza que está em actividade
      customerId = ((Integer) sitCustomer.read ()).intValue ();      // chama o cliente
      stateCustomer[customerId] = CUTHAIR;                           // sinaliza que o cliente está a cortar o cabelo
      reportStatus ();

      return (customerId);
   }

  /**
   *  Operação de receber o pagamento pelo serviço prestado (originada pelo barbeiro).
   *
   *    @param barberId identificação do barbeiro
   *    @param customerId identificação do cliente
   */

   public synchronized void getPayment (int barberId, int customerId)
   {
      stateBarber[barberId] = SLEEPING;                    // sinaliza que vai dormir
      stateCustomer[customerId] = LIVNORML;                // sinaliza que o cliente vai prosseguir com a sua vida
      reportStatus ();
      notifyAll ();                                        // acorda o cliente
   }

  /**
   *  Obtenção do número de barbeiros que trabalham na barbearia.
   *
   *    @return número de barbeiros
   */

   public int getNBarb ()
   {
      return nBarber;
   }

  /**
   *  Obtenção do número de clientes que frequentam a barbearia.
   *
   *    @return número de clientes
   */

   public int getNCust ()
   {
      return nCustomer;
   }

  /**
   *  Acordar intempestivamente um barbeiro para sinalizar fim de operações.
   *
   *    @param barberId identificação do barbeiro
   */

   public synchronized void endOperation (int barberId)
   {
      if (registry[barberId] != null)
         registry[barberId].interrupt ();
   }

  /**
   *  Escrever o estado inicial (operação interna).
   *  <p>Os barbeiros estão a dormir e os clientes a realizar as tarefas do dia a dia.
   */

   private void reportInitialStatus ()
   {
      TextFile log = new TextFile ();                      // instanciação de uma variável de tipo ficheiro de texto

      if (!log.openForWriting (".", fileName))
         { GenericIO.writelnString ("A operação de criação do ficheiro " + fileName + " falhou!");
           System.exit (1);
         }
      log.writelnString ("                Problema dos barbeiros sonolentos");
      log.writelnString ("\nNúmero de iterações = " + nIter + "\n");
      if (!log.close ())
         { GenericIO.writelnString ("A operação de fecho do ficheiro " + fileName + " falhou!");
           System.exit (1);
         }
      reportStatus ();
   }

  /**
   *  Escrever o estado actual (operação interna).
   *  <p>Uma linha de texto com o estado de actividade dos barbeiros e dos clientes é escrito no ficheiro.
   */

   private void reportStatus ()
   {
      TextFile log = new TextFile ();                      // instanciação de uma variável de tipo ficheiro de texto
      String lineStatus = "";                              // linha a imprimir

      if (!log.openForAppending (".", fileName))
         { GenericIO.writelnString ("A operação de criação do ficheiro " + fileName + " falhou!");
           System.exit (1);
         }
      for (int i = 0; i < nBarber; i++)
        switch (stateBarber[i])
        { case SLEEPING: lineStatus += " DORMINDO ";
                         break;
          case WORKING:  lineStatus += " ACTIVIDA ";
                         break;
        }
      for (int i = 0; i < nCustomer; i++)
        switch (stateCustomer[i])
        { case LIVNORML: lineStatus += " VIVVNRML ";
                         break;
          case WANTCUTH: lineStatus += " QUERCORT ";
                         break;
          case WAITTURN: lineStatus += " ESPERAVZ ";
                         break;
          case CUTHAIR:  lineStatus += " CORTACBL ";
                         break;
        }
      log.writelnString (lineStatus);
      if (!log.close ())
         { GenericIO.writelnString ("A operação de fecho do ficheiro " + fileName + " falhou!");
           System.exit (1);
         }
   }
}
