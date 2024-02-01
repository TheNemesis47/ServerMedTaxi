import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader input;
    private BufferedWriter output;
    private String fileJSON = "Prenotazione.json"; // Assicurati che il percorso esista

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));


            // Ricevi il JSON dal cliente
            StringBuilder jsonInput = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                if (line.equals("END")) {
                    break;
                }
                jsonInput.append(line);
            }
            JSONObject jsonReceived = new JSONObject(jsonInput.toString());

            // Scrivi il JSON ricevuto in un file
            //JSONHandler.scriviJsonInFile(jsonReceived.toString(), this.fileJSON);

            // Aggiungi aziende disponibili al JSON
            jsonReceived.put("codice", generateRandomString(5));
            Prenotazione prenotazione = new Prenotazione(jsonReceived.toString());
            List<String> aziendeDisponibili = prenotazione.ricercaAziendeDisponibili();
            List<String> aziendeInfo = prenotazione.calcolaDistanzaECosto();
            jsonReceived.put("aziendeDisponibili", new JSONArray(aziendeInfo));


            // Invia il JSON modificato al cliente
            output.write(jsonReceived.toString());
            output.newLine();
            output.flush();

            // Aspetta la risposta del cliente con l'azienda scelta
            jsonInput = new StringBuilder();
            while ((line = input.readLine()) != null && !line.isEmpty()) {
                if (line.equals("END")) {
                    break;
                }
                jsonInput.append(line);
            }
            JSONObject jsonUpdated = new JSONObject(jsonInput.toString());

            //Invio prenotazione all azienda
            //estrapolo la piva dall azienda scelta
            String pivaAzScelta =  prenotazione.estrattorePIVA(jsonUpdated.getString("aziendaScelta"));
            jsonUpdated.put("pivaAZScelta", pivaAzScelta);
            prenotazione.setPiva(pivaAzScelta);

            // aggiorno file json
            JSONHandler.scriviJsonInFile(jsonUpdated.toString(), this.fileJSON);

            AziendaHandler aziendaHandler = new AziendaHandler(prenotazione);
            aziendaHandler.sendBookingDetails(jsonUpdated.toString(), pivaAzScelta);


            output.close();
            input.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!?";
        StringBuilder randomString = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            randomString.append(characters.charAt(randomIndex));
        }

        return randomString.toString();
    }
}








/*
import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ClientHandler implements Runnable {
    private static int fileCount = 0;
    private Socket clientSocket;
    private BufferedReader input;
    private BufferedWriter output;
    private String fileJSON;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        synchronized (ClientHandler.class) {
            fileCount++;
            this.fileJSON = "Prenotazione.json";
        }
    }



    @Override
    public void run() {
        try {

            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

            StringBuilder jsonInput = new StringBuilder();
            String line;
            System.out.println("ciao" + fileCount);
            while ((line = input.readLine()) != null && !line.isEmpty()) {
                jsonInput.append(line);
            }



            JSONHandler jsonHandler = new JSONHandler(jsonInput.toString());
            System.out.println("ciao");
            jsonHandler.scriviJsonInFile(this.fileJSON);

            // Gestisci la richiesta in base al tipo di utente
            String isUser = "cliente";
            if (jsonHandler.getTipoUtente().equals(isUser)){
                //é un cliente

                // Utilizza JSONHandler per estrarre i dati necessari
                String nome = jsonHandler.getNome();
                String cognome = jsonHandler.getCognome();
                String email = jsonHandler.getEmail();
                String partenza = jsonHandler.getPartenza();
                String arrivo = jsonHandler.getArrivo();
                String giorno = jsonHandler.getGiorno();
                String ora_precisa = jsonHandler.getOra_precisa();
                String orario = jsonHandler.getOrario();
                String cellulare = jsonHandler.getCellulare();

                //DA ELIMINAREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE SOLO PER DEBUG
                System.out.println(nome + ' ' + cognome + ' ' + email + ' ' + partenza + ' ' + arrivo + ' ' + giorno + ' ' + ora_precisa + ' ' + orario + ' ' + cellulare + ' ');

                Prenotazione prenotazione = new Prenotazione(nome, cognome, email, partenza, arrivo, giorno, orario, cellulare);
                jsonHandler.setCodice(prenotazione.generateRandomString(5));

                // Genera una lista di aziende disponibili per la prenotazione
                List<String> aziende = prenotazione.ricercaAziendeDisponibili();

                // Calcola la distanza tra due punti
                prenotazione.calcolaDistanzaECosto(aziende);

                JSONHandler.aggiuntaAziende(aziende);

                // Invia il JSON al client
                String jsonContent = new String(Files.readAllBytes(Paths.get(fileJSON)));
                output.write(jsonContent);
                output.flush();








                String aziendaSelezionata = input.readLine(); // Leggi la risposta dal client per vedere qual azienda ha selezioato
                String pivaAzScelta =  prenotazione.estrattorePIVA(aziendaSelezionata);
                System.out.println("Azienda selezionata: " + aziendaSelezionata);

                // Invia la risposta al client
                String result = "Risposta elaborata con successo";
                output.write(result);


                //avviso pop-up di prenotazione all azienda con quella piva

                AziendaHandler aziendaHandler = new AziendaHandler(prenotazione, pivaAzScelta);
                aziendaHandler.sendBookingDetails(nome, cognome, email, partenza, arrivo, giorno, ora_precisa, cellulare, jsonHandler.getCodice());
            }

            output.flush(); // Assicurati che tutti i dati siano inviati al client
            clientSocket.close(); // Chiudi la connessione dopo aver gestito la richiesta
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
 */