/*
 * 1- prenotazione:
 *      prende input email,indirizzo di partenza, arrivo, giorno, orario, cellulare
 *      fa una query al database per vedere se ci sono aziende che hano ambulanze disp in quella fascia oraria
 *      se ci sono, mostra le aziende e prende input dell'azienda scelta
 *      crea la query per aggiungere nella tabella prenotazione
 *      se la query va a buon fine, mostra messaggio di conferma
 */
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Singleton.Database;
import org.json.JSONObject;

import static java.sql.DriverManager.getConnection;

public class Prenotazione {
    private String nome;
    private String cognome;
    private String email;
    private String partenza;
    private String arrivo;
    private Date giorno;
    private String orario;
    private String cellulare;

    public Prenotazione(String nome, String cognome, String email, String partenza, String arrivo, String giorno1, String orario, String cellulare) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.partenza = partenza;
        this.arrivo = arrivo;
        this.giorno = convertStringToSqlDate(giorno1);
        this.orario = orario;
        this.cellulare = cellulare;
    }



    public static Date convertStringToSqlDate(String dateString) {
        //stabilisco come deve essere la data
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // parsing della stringa in java.util.Date
            java.util.Date parsed = format.parse(dateString);
            // parsing da java.util.Date in java.sql.Date
            return new Date(parsed.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }



    public List<String> ricercaAziendeDisponibili() {
        List<String> aziendeDisponibili = new ArrayList<>();
        try (Connection connection = Database.getInstance().getConnection()) {
            String campoDisponibilita = orario.equals("mattina") ? "disp_mattina" : "disp_sera";

            String query = "SELECT nome, piva, prezzo_per_km FROM azienda WHERE EXISTS "
                    + "(SELECT * FROM disponibilita WHERE azienda.piva = disponibilita.piva "
                    + "AND " + campoDisponibilita + " > 0)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String nomeAzienda = resultSet.getString("nome");
                        String pivaAzienda = resultSet.getString("piva");
                        float prezzoPerKm = resultSet.getFloat("prezzo_per_km");
                        aziendeDisponibili.add(nomeAzienda + " (" + pivaAzienda + ") - Prezzo per km: " + prezzoPerKm);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return aziendeDisponibili;
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



    public List<String> calcolaDistanzaECosto(List<String> aziendeDisponibili) {
        List<String> risultatoRicerca = new ArrayList<>();
        double distanza = calcolaDistanza(partenza, arrivo);

        for (String azienda : aziendeDisponibili) {
            String[] parti = azienda.split(" - Prezzo per km: ");
            String nomeAzienda = parti[0];
            double prezzoPerKm = Double.parseDouble(parti[1]);
            double costo = distanza * prezzoPerKm;
            risultatoRicerca.add(nomeAzienda + " - Distanza: " + distanza + " km - Costo: " + costo + " €");
        }

        return risultatoRicerca;
    }



    private double calcolaDistanza(String partenza, String arrivo) {
        String apiKey = "AIzaSyB-7VoL5g7xLox1cZA9KVYEAu6l34FZ-tQ"; 
        try {
            // Formattazione degli indirizzi per l'URL
            partenza = partenza.replace(" ", "+");
            arrivo = arrivo.replace(" ", "+");

            // Costruzione dell'URL per la chiamata API
            String urlString = String.format("https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&key=%s", partenza, arrivo, apiKey);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // Controllo del codice di risposta
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                StringBuilder response = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                // Lettura della risposta
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }

                // Chiusura dello scanner
                scanner.close();

                // Parsing della risposta JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject elements = jsonResponse.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                JSONObject distance = elements.getJSONObject("distance");

                // Ottenimento della distanza in metri e conversione in chilometri
                double distanzaInMetri = distance.getDouble("value");
                System.out.println("Distanza in metri: " + distanzaInMetri);
                System.out.println(partenza + " - " + arrivo);
                return distanzaInMetri / 1000.0; // Conversione in chilometri
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // Ritorna -1 in caso di errore
    }



    public static String estrattorePIVA(String testo){
        // Espressione regolare per una sequenza di 11 cifre
        String regex = "\\b\\d{11}\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(testo);

        // Trova la prima corrispondenza
        if (matcher.find()) {
            return matcher.group();
        }

        // Restituisce null se nessuna corrispondenza è trovata
        return null;
    }



    public void immissionePrenotazione(String piva, String codice, String targa, String ora_precisa) throws SQLException {
        System.out.println(nome + ' ' + cognome + ' ' + partenza + ' ' + arrivo + ' ' + this.giorno + ' ' + cellulare + ' ' + orario + ' ' + piva + ' ' + codice + ' ' + targa);
        try (Connection connection = Database.getInstance().getConnection()) {
            String sql = "INSERT INTO prenotazione (nome_trasportato, cognome_trasportato, indirizzo_partenza, indirizzo_arrivo, giorno_trasporto, numero_cellulare, mattina_sera, code_track, p_iva, targa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, this.nome);
                statement.setString(2, this.cognome);
                statement.setString(3, this.partenza);
                statement.setString(4, this.arrivo);
                java.sql.Date sqlDate = new java.sql.Date(this.giorno.getTime());
                statement.setDate(5, sqlDate);
                statement.setString(6, this.cellulare);
                statement.setString(7, ora_precisa);
                statement.setString(8, codice);
                statement.setString(9, piva);
                statement.setString(10, targa);
                statement.executeUpdate();
            }
        }
    }
}
