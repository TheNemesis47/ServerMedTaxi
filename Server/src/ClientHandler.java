import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
            

            // Leggi e gestisci le richieste del client
            String richiesta = input.readLine();
            String whois = "azienda";

            if (richiesta.equals(whois)) {
                output.println("Benvenuto azienda");

                // Leggi i tre parametri 
                String param1 = input.readLine();
                String param2 = input.readLine();
                String param3 = input.readLine();

                // Stampa i parametri ricevuti dal client
                System.out.println("Parametri ricevuti dal client:");
                System.out.println("Parametro 1: " + param1);
                System.out.println("Parametro 2: " + param2);
                System.out.println("Parametro 3: " + param3);

                // Elabora la richiesta (aggiungi la logica necessaria)
                String result = "Risposta elaborata con successo";

                // Invia la risposta al client
                output.println(result);
            } else {
                output.println("Benvenuto client");

                // Leggere i parametri per la prenotazione
                String nome = input.readLine();
                String cognome = input.readLine();
                String email = input.readLine();
                String partenza = input.readLine();
                String arrivo = input.readLine();
                String data = input.readLine(); // La data deve essere in formato yyyy-mm-dd
                String orario = input.readLine();
                String telefono = input.readLine();
                String codice = input.readLine(); // Il codice deve essere di 6 cifre

                // Conversione della data in oggetto Date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date giorno = new Date(sdf.parse(data).getTime());

                System.out.println(nome);
                System.out.println(cognome);
                System.out.println(email);
                System.out.println(partenza);
                System.out.println(arrivo);
                System.out.println(giorno);
                System.out.println(orario);
                System.out.println(telefono);

                // Creazione oggetto Prenotazione
                Prenotazione prenotazione = new Prenotazione(nome, cognome, email, partenza, arrivo, giorno, orario, Double.parseDouble(telefono));
                List<String> aziende = prenotazione.ricercaAziendeDisponibili();
                prenotazione.calcolaDistanzaECosto(aziende);

                System.out.println("Azienda disponibili: " + aziende);

                for (String azienda : aziende) {
                    output.println(azienda);
                }
                output.println("END_OF_LIST"); // Segnale di fine dell'elenco

                String aziendaSelezionata = input.readLine(); // Leggi la risposta dal client per vedere qual azienda ha selezioato
                System.out.println("Azienda selezionata: " + aziendaSelezionata);


                String result = "Risposta elaborata con successo";

                // Invia la risposta al client
                output.println(result);
            }

            // Chiudi la connessione
            clientSocket.close();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}