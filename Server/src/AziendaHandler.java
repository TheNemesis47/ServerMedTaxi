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
             PrintWriter output = new PrintWriter(aziendaSocket.getOutputStream(), true)) {

            // Invia i dettagli della prenotazione all'azienda
            String prenoTesto = String.format(
                    "Nuova prenotazione:%n%s - %s%n%s - %s%n%s - %s%n%s - %s%nCodice: %s",
                    nome, cognome, email, telefono, partenza, arrivo, data, orario, codice
            );
            output.println(prenoTesto);
            BufferedReader input = new BufferedReader(new InputStreamReader(aziendaSocket.getInputStream()));
            System.out.println("In attesa di risposta...");
            String response = input.readLine();
            System.out.println("Risposta eleboro: " + response);
            if (response.equals("OK")) {
                response = input.readLine();
                try {
                    System.out.println(piva + ' ' + codice + ' ' + response);
                    prenotazione.immissionePrenotazione(piva, codice, response, orario);

                    System.out.println("Prenotazione effettuata con successo!");
                }catch (Exception e) {
                    System.out.println("Errore durante la prenotazione");
                }


            }
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}