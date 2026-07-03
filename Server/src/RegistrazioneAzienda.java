import java.sql.SQLException;
import java.util.Scanner;

import Singleton.Database;

public class RegistrazioneAzienda {
    private String nome;
    private String partitaIva;
    private double telefono;
    private String indirizzo;
    private String comune;
    private String provincia;
    private int cap;
    private String email;
    private String psw;
    private float prezzo_per_km;
    
    protected void immissione() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Inserire nome azienda");
        this.nome = scanner.nextLine().trim();

        System.out.println("Inserire partita iva");
        this.partitaIva = scanner.nextLine().trim();

        System.out.println("Inserire telefono");
        while (true) {
            try {
                this.telefono = Double.parseDouble(scanner.nextLine());
                break; // Esci dal ciclo se l'input è valido
            } catch (NumberFormatException e) {
                System.out.println("Per favore, inserire un numero valido per il telefono:");
            }
        }

        System.out.println("Inserire indirizzo");
        this.indirizzo = scanner.nextLine().trim();

        System.out.println("Inserire comune");
        this.comune = scanner.nextLine().trim();

        System.out.println("Inserire provincia");
        this.provincia = scanner.nextLine().trim();

        System.out.println("Inserire CAP");
        while (true) {
            try {
                this.cap = Integer.parseInt(scanner.nextLine());
                break; // Esci dal ciclo se l'input è valido
            } catch (NumberFormatException e) {
                System.out.println("Per favore, inserire un numero intero per il CAP:");
            }
        }

        System.out.println("Inserire email");
        this.email = scanner.nextLine().trim();

        System.out.println("Inserire password");
        this.psw = scanner.nextLine().trim();

        System.out.println("Inserire prezzo per km");
        while (true) {
            try {
                this.prezzo_per_km = Float.parseFloat(scanner.nextLine());
                break; // Esci dal ciclo se l'input è valido
            } catch (NumberFormatException e) {
                System.out.println("Per favore, inserire un numero valido per il prezzo per km:");
            }
        }

        Database database = Database.getInstance();

        try {
            database.RegistrazioneAzienda(this.nome, this.partitaIva, this.telefono, this.indirizzo, this.comune, this.provincia, this.cap, this.email, this.psw, this.prezzo_per_km);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
