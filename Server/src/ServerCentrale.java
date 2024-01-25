import java.util.Scanner;

public class ServerCentrale {
    private static final int PORT = 12346;
    private static final int NUM_THREADS = 10;
    protected static boolean statoServer = false;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Inserire: \n[1] Per avviare il server \n" +
                    "[2] Per fermare il server\n[3] Per inserire azienda");
            int scelta = scanner.nextInt();

            switch (scelta) {
                case 1:
                    if (!statoServer) {
                        statoServer = true;
                        System.out.println("Server avviato");
                        Thread serverThread = new Thread(new AvviaServer());
                        serverThread.start();
                        //thread main aspettare 2 secondi prima di procedere
                        try {
                            Thread.sleep(2000);
                            System.out.println("Server avviato con successo");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Il server è già avviato.");
                    }
                    break;

                case 2:
                    statoServer = false;
                    System.out.println("Server fermato");
                    break;

                case 3:
                    // Logica per l'inserimento dell'azienda
                    RegistrazioneAzienda registrazioneAzienda = new RegistrazioneAzienda();
                    registrazioneAzienda.immissione();
                    break;

                default:
                    break;
            }
        }
    }
}






/*
 * 1- prenotazione:
 *      prende input email,indirizzo di partenza, arrivo, giorno, orario, cellulare
 *      fa una query al database per vedere se ci sono aziende che hano ambulanze disp in quella fascia oraria
 *      se ci sono, mostra le aziende e prende input dell'azienda scelta
 *      crea la query per aggiungere nella tabella prenotazione
 *      se la query va a buon fine, mostra messaggio di conferma
 *      
 *  AZIENDA
 *  1- prenotazioni : query che controlla la tabella prenotazioni e mostra i viaggi da fare
 *      query che annulla la prenotazione
 *      query che assegna l ambulanza dal parco auto al trasferimento
 *  3- parco auto: 
 *      aggiunta o rimozione di ambulanze dal parco auto
 *  2- storico prenotazioni: query che mostra i viaggi effettuati
 */