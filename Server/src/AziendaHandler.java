import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AziendaHandler {
    private Prenotazione prenotazione;
    String piva;

    public AziendaHandler(Prenotazione prenotazione, String piva) {
        this.prenotazione = prenotazione;
        this.piva = piva;
    }

    public void sendBookingDetails(String nome, String cognome, String email,
                                   String partenza, String arrivo, String data,
                                   String orario, String telefono, String codice) {
        try (Socket aziendaSocket = new Socket("localhost", 54321);
             PrintWriter output = new PrintWriter(aziendaSocket.getOutputStream(), true);
             BufferedReader input = new BufferedReader(new InputStreamReader(aziendaSocket.getInputStream()))) {

            // Invia i dettagli della prenotazione all'azienda
            String prenoTesto = String.format(
                    "%s - %s%n%s - %s%n%s - %s%n%s - %s%nCodice: %s%n---FINE---",
                    nome, cognome, email, telefono, partenza, arrivo, data, orario, codice
            );
            System.out.println(prenoTesto);
            output.println(prenoTesto);

            System.out.println("In attesa di risposta...");
            String response;
            while ((response = input.readLine()) != null) {
                System.out.println("Risposta ricevuta: " + response);
                if (response.equals("OK")) {
                    response = input.readLine();  // Assume that the next line contains the actual response
                    try {
                        System.out.println(piva + ' ' + codice + ' ' + response);
                        prenotazione.immissionePrenotazione(piva, codice, response, orario);

                        System.out.println("Prenotazione effettuata con successo!");
                        break;  // Interrompe il ciclo dopo aver ricevuto la risposta
                    } catch (Exception e) {
                        System.out.println("Errore durante la prenotazione");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}