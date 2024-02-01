import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AziendaHandler {
    private String piva;
    private Prenotazione prenotazione;
    private String codice;


    public AziendaHandler(Prenotazione prenotazione) {
        this.prenotazione = prenotazione;
    }

    public void sendBookingDetails(String json, String piva) {
        JSONHandler jsonHandler = new JSONHandler(json);
        this.piva = piva;
        this.codice = jsonHandler.getCodice();

        try (Socket aziendaSocket = new Socket("localhost", 54321);
             PrintWriter output = new PrintWriter(aziendaSocket.getOutputStream(), true);
             BufferedReader input = new BufferedReader(new InputStreamReader(aziendaSocket.getInputStream()))) {

            // Invia i dettagli della prenotazione all'azienda
            String prenoTesto = String.format(
                    "%s - %s%n%s - %s%n%s - %s%n%s - %s%nCodice: %s%n---FINE---",
                    jsonHandler.getNome(), jsonHandler.getCognome(), jsonHandler.getEmail(), jsonHandler.getCellulare(), jsonHandler.getPartenza(), jsonHandler.getArrivo(), jsonHandler.getGiorno(), jsonHandler.getOrario(), this.codice
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
                        prenotazione.immissionePrenotazione(piva, codice, response, jsonHandler.getOrario());

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