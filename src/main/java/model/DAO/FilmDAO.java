package model.DAO;

import model.Film;
import util.DBConnection;

import java.sql.*;

public class FilmDAO {

    public Film findOrCreate(int tmdbId, String titolo) {
        Film film = findByTmdbId(tmdbId);

        if (film != null) {
            return film;
        } else {
            return createFilm(tmdbId, titolo);
        }
    }

    public Film findByTmdbId(int tmdbId) {
        String sql = "SELECT * FROM Film WHERE Id_tmdb = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tmdbId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Film(
                        rs.getInt("Id_film"),
                        rs.getInt("Id_tmdb"),
                        rs.getString("titolo")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Film createFilm(int tmdbId, String titolo) {
        String sql = "INSERT INTO Film (Id_tmdb, titolo) VALUES (?, ?)";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, tmdbId);
            ps.setString(2, titolo);
            ps.executeUpdate();

            // Recuperiamo l'ID generato dal database
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int newId = rs.getInt(1);
                return new Film(newId, tmdbId, titolo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int recuperaIdTmdbDaIdInterno(int idFilmInterno) {
        String sql = "SELECT Id_tmdb FROM Film WHERE Id_film = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idFilmInterno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Id_tmdb");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // film non trovato
    }

}