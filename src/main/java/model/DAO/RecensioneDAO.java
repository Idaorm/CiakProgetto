package model.DAO;

import model.Recensione;
import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;




public class RecensioneDAO {

    public void doSave(Recensione recensione) {
        String sql = "INSERT INTO Recensione (rating, text, date, Id_utente, Id_film) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recensione.getRating());
            ps.setString(2, recensione.getText());
            ps.setDate(3, recensione.getDate()); // Inseriamo la data
            ps.setInt(4, recensione.getIdUtente());
            ps.setInt(5, recensione.getIdFilm());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public LinkedHashMap<Recensione, String> doRetrieveByTmdbId(int idTmdb) {

        LinkedHashMap<Recensione, String> recensioniMap = new LinkedHashMap<>();

        String sql = "SELECT r.rating, r.text, r.date, r.Id_utente, r.Id_film, u.Email " +
                "FROM Recensione r " +
                "JOIN Film f ON r.Id_film = f.Id_film " +
                "JOIN UtenteRegistrato u ON r.Id_utente = u.Id_utente " +
                "WHERE f.Id_tmdb = ? " +
                "ORDER BY r.date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTmdb);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recensione r = new Recensione();
                    r.setRating(rs.getInt("rating"));
                    r.setText(rs.getString("text"));
                    r.setDate(rs.getDate("date"));

                    String fullEmail = rs.getString("Email");
                    String iniziale = "?";

                    if (fullEmail != null && !fullEmail.isEmpty()) {
                        iniziale = fullEmail.substring(0, 1).toUpperCase();
                    }

                    recensioniMap.put(r, iniziale);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recensioniMap;
    }

    public LinkedHashMap<Recensione, String> doRetrieveByUtente(int idUtente) {
        LinkedHashMap<Recensione, String> recensioniMap = new LinkedHashMap<>();

        // Uniamo Recensione e Film per avere il titolo
        String sql = "SELECT r.rating, r.text, r.date, r.Id_utente, r.Id_film, f.titolo " +
                "FROM Recensione r " +
                "JOIN Film f ON r.Id_film = f.Id_film " +
                "WHERE r.Id_utente = ? " +
                "ORDER BY r.date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUtente);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recensione r = new Recensione();
                    r.setRating(rs.getInt("rating"));
                    r.setText(rs.getString("text"));
                    r.setDate(rs.getDate("date"));
                    r.setIdUtente(rs.getInt("Id_utente"));
                    r.setIdFilm(rs.getInt("Id_film"));

                    String titoloFilm = rs.getString("titolo");
                    recensioniMap.put(r, titoloFilm);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recensioniMap;
    }
}
