package model.DAO;

import model.Recensione;
import model.UtenteRegistrato; // Importante: ora usiamo questo modello
import controller.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class RecensioneDAO {

    public void doSave(Recensione recensione) {
        String sql = "INSERT INTO Recensione (rating, text, date, Id_utente, Id_film) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recensione.getRating());
            ps.setString(2, recensione.getText());
            ps.setDate(3, recensione.getDate());
            ps.setInt(4, recensione.getIdUtente());
            ps.setInt(5, recensione.getIdFilm());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LinkedHashMap<Recensione, UtenteRegistrato> doRetrieveByTmdbId(int idTmdb) {
        LinkedHashMap<Recensione, UtenteRegistrato> recensioniMap = new LinkedHashMap<>();
        String sql = "SELECT r.rating, r.text, r.date, r.Id_utente, r.Id_film, u.Username, u.Photo " +
                "FROM Recensione r " +
                "JOIN Film f ON r.Id_film = f.Id_film " +
                "JOIN UtenteRegistrato u ON r.Id_utente = u.Id_utente " +
                "WHERE f.Id_tmdb = ? " +
                "ORDER BY r.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTmdb);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recensione r = new Recensione();
                    r.setRating(rs.getInt("rating"));
                    r.setText(rs.getString("text"));
                    r.setDate(rs.getDate("date"));
                    r.setIdUtente(rs.getInt("Id_utente"));
                    r.setIdFilm(rs.getInt("Id_film"));
                    UtenteRegistrato u = new UtenteRegistrato();
                    u.setIdUtente(rs.getInt("Id_utente"));
                    u.setUsername(rs.getString("Username"));
                    u.setPhoto(rs.getString("Photo"));
                    recensioniMap.put(r, u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recensioniMap;
    }

    public LinkedHashMap<Recensione, String> doRetrieveByUtente(int idUtente) {
        LinkedHashMap<Recensione, String> recensioniMap = new LinkedHashMap<>();
        String sql = "SELECT r.rating, r.text, r.date, r.Id_utente, r.Id_film, f.titolo " +
                "FROM Recensione r " +
                "JOIN Film f ON r.Id_film = f.Id_film " +
                "WHERE r.Id_utente = ? " +
                "ORDER BY r.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
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

    public Recensione doRetrieveByUtenteAndFilm(int idUtente, int idFilm) {
        String sql = "SELECT rating, text, date, Id_utente, Id_film FROM Recensione WHERE Id_utente = ? AND Id_film = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idFilm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Recensione r = new Recensione();
                    r.setRating(rs.getInt("rating"));
                    r.setText(rs.getString("text"));
                    r.setDate(rs.getDate("date"));
                    r.setIdUtente(rs.getInt("Id_utente"));
                    r.setIdFilm(rs.getInt("Id_film"));
                    return r;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getMediaVotiPerFilm(int idFilm) {
        String sql = "SELECT AVG(rating) as media FROM Recensione WHERE Id_film = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idFilm);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("media");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}