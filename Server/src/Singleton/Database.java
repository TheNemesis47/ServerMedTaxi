package Singleton;

import java.sql.*;

public class Database {
    private String host = "127.0.0.1";
    private int port = 3306;
    private String database = "test";
    private String username = "root";
    private String password = "";

    private static Database instance;

    private Database() {
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        return DriverManager.getConnection(url, username, password);
    }

    public void RegistrazioneUtente(String nome, String cognome, int telefono, String data, String via, String comune, String citta, String email, String psw) throws SQLException {
        Connection connection = getConnection();

        String sql = "INSERT INTO utente (nome, cognome, telefono, data, via, comune, citta, email, psw) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setString(2, cognome);
            statement.setInt(3, telefono);
            java.sql.Date sqlDate = java.sql.Date.valueOf(data);
            statement.setDate(4, sqlDate);
            statement.setString(5, via);
            statement.setString(6, comune);
            statement.setString(7, citta);
            statement.setString(8, email);
            statement.setString(9, psw);
            statement.executeUpdate();
        } finally {
            connection.close();
        }
    }

    public void RegistrazioneAzienda(String nome, String piva, double telefono, String indirizzo, String comune, String provincia, int cap, String email, String psw, float prezzo_per_km) throws SQLException {
        Connection connection = getConnection();
    
        String sql = "INSERT INTO azienda (nome, piva, telefono, indirizzo, comune, provincia, cap, email, psw, prezzo_per_km) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setString(2, piva);
            statement.setDouble(3, telefono);
            statement.setString(4, indirizzo);
            statement.setString(5, comune);
            statement.setString(6, provincia);
            statement.setInt(7, cap);
            statement.setString(8, email);
            statement.setString(9, psw);
            statement.setFloat(10, prezzo_per_km);
            statement.executeUpdate();
        } finally {
            connection.close();
        }
    }
    




    
}