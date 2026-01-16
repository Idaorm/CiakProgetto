package model.DAO;

import model.WatchlistItem;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WatchlistItemDAO {

    // Aggiunta di un film
    public void add(int idUtente, int idFilm) {
        String sql = "INSERT IGNORE INTO WatchlistItem (Id_utente, Id_film, status) VALUES (?, ?, false)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idFilm);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    //Modifica stato
    public void toggleStatus(int idItem, boolean currentStatus) {
        String sql = "UPDATE WatchlistItem SET status = ? WHERE Id_item = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, !currentStatus);
            ps.setInt(2, idItem);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    //Rimozione di un film
    public void remove(int idItem) {
        String sql = "DELETE FROM WatchlistItem WHERE Id_item = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idItem);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    //Visualizzazione della watchlist dell'utente
    public List<WatchlistItem> findByUtente(int idUtente) {
        List<WatchlistItem> list = new ArrayList<>();
        String sql = "SELECT * FROM WatchlistItem WHERE Id_utente = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                WatchlistItem item = new WatchlistItem();
                item.setIdItem(rs.getInt("Id_item"));
                item.setStatus(rs.getBoolean("status"));
                item.setIdFilm(rs.getInt("Id_film"));
                item.setIdUtente(idUtente);
                list.add(item);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}