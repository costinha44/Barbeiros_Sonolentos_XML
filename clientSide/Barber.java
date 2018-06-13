package clientSide;

/**
 *   Este tipo de dados define o thread barbeiro para uma solução do Problema dos Barbeiros Sonolentos que implementa
 *   o modelo cliente-servidor de tipo 2 (replicação do servidor) com lançamento estático dos threads barbeiro.
 *   A comunicação baseia-se em passagem de mensagens sobre sockets usando o protocolo TCP.
 */

public class Barber extends Thread
{
  /**
   *  Identificação do barbeiro
   *
   *    @serialField barberId
   */

   private int barberId;

  /**
   *  Stub à barbearia
   *    @serialField bShopStub;
   */

   private BarberShopStub bShopStub;

  /**
   *  Número do port de escuta do servidor
   *    @serialField serverPortNumb
   */

   private int serverPortNumb;

  /**
   *  Instanciação de um barbeiro.
   *
   *    @param barberId identificação do barbeiro
   *    @param bShopStub stub à barbearia
   */

   public Barber (int barberId, BarberShopStub bShopStub)
   {
      super ("Barber_" + barberId);

      this.barberId = barberId;
      this.bShopStub = bShopStub;
   }

  /**
   *  Ciclo de vida do thread barbeiro.
   */

   @Override
   public void run ()
   {
      int customerId;                                      // identificação do cliente
      boolean test;                                        // teste de fim de operações

      while (true)
      { test = bShopStub.goToSleep (barberId);             // o barbeiro adormece enquanto aguarda um cliente
        if (test) break;                                   // fim do ciclo de vida do thread barbeiro
        customerId = bShopStub.callCustomer (barberId);    // o barbeiro é acordado e chama o cliente
        cuttingHair ();                                    // o barbeiro corta-lhe o cabelo
        bShopStub.getPayment (barberId, customerId);       // o barbeiro termina o serviço e é pago por ele
      }
   }

  /**
   *  Corte do cabelo (operação interna).
   */

   private void cuttingHair ()
   {
      try
      { sleep ((long) (1 + 100 * Math.random ()));
      }
      catch (InterruptedException e) {}
   }
}
