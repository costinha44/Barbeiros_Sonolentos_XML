package clientSide;

/**
 *   Este tipo de dados define o thread cliente para uma solução do Problema dos Barbeiros Sonolentos que implementa
 *   o modelo cliente-servidor de tipo 2 (replicação do servidor) com lançamento estático dos threads barbeiro.
 *   A comunicação baseia-se em passagem de mensagens sobre sockets usando o protocolo TCP.
 */

public class Customer extends Thread
{
  /**
   *  Identificação do cliente
   *
   *    @serialField customerId
   */

   private int customerId;

  /**
   *  Número de iterações do seu ciclo de vida
   *    @serialField nIter
   */

   private int nIter;

  /**
   *  Stub à barbearia
   *    @serialField bShopStub;
   */

   private BarberShopStub bShopStub;

  /**
   *  Instanciação de um cliente.
   *
   *    @param customerId identificação do cliente
   *    @param nIter número de iterações do seu ciclo de vida
   *    @param bShopStub stub à barbearia
   */

   public Customer (int customerId, int nIter, BarberShopStub bShopStub)
   {
      super ("Customer_" + customerId);

      this.customerId = customerId;
      this.nIter = nIter;
      this.bShopStub = bShopStub;
   }

  /**
   *  Ciclo de vida do thread cliente.
   */

   @Override
   public void run ()
   {
      for (int i = 0; i < nIter; i++)
      { livingNormalLife ();                               // o cliente realiza as tarefas do dia a dia
        while (!bShopStub.goCutHair (customerId))          // o cliente tenta cortar o cabelo
          haveCupCoffee ();                                // se não o pode fazer de momento, vai tomar café
      }
   }

  /**
   *  Vivendo a vida normal (operação interna).
   */

   private void livingNormalLife ()
   {
      try
      { sleep ((long) (1 + 40 * Math.random ()));
      }
      catch (InterruptedException e) {}
   }

  /**
   *  Ir tomar café (operação interna).
   */

   private void haveCupCoffee ()
   {
      try
      { sleep ((long) (1 + 10 * Math.random ()));
      }
      catch (InterruptedException e) {}
   }
}
