import java.sql.*;

public class AziendaHnadler {
    private String piva;
    private Date giorno;
    private int disponibilita_ambulanze;

    public void immissione(String piva, Date giorno, int disponibilita_ambulanze) {
        this.piva = piva;
        this.giorno = giorno;
        this.disponibilita_ambulanze = disponibilita_ambulanze;
    }
}
