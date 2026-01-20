package model.DAO;

import model.UtenteRegistrato;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per l'entità Utente.
 * Incapsula tutte le operazioni di accesso al database relative agli utenti.
 */
public class UtenteRegistratoDAO {

    private Connection connection;

    /**
     * Costruttore DAO.
     */
    public UtenteRegistratoDAO() {
        try {
            // Ottieni la connessione dal DBConnection
            this.connection = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nella connessione al DB", e);
        }
    }

    /**
     * Inserisce un nuovo utente nel database.
     * @param u Utente da inserire
     * @return true se l'operazione è avvenuta con successo
     *         false altrimenti
     * @throws SQLException
     */
    public boolean insertUtente(UtenteRegistrato u) throws SQLException {
        String sql = """
        INSERT INTO UtenteRegistrato (username, email, password, photo, bio, watchlistVisibility)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getPhoto() != null ? u.getPhoto() : "");
            ps.setString(5, u.getBio() != null ? u.getBio() : "");
            ps.setBoolean(6, u.isWatchlistVisibility());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Verifica se esiste già un utente registrato con l'email specificata.
     * @param email l'email da controllare
     * @return true se l'email è già associata a un utente registrato,
     *         false altrimenti
     * @throws SQLException se si verifica un errore durante l'accesso al database
     */
    public boolean emailGiaRegistrata(String email) throws SQLException {
        String sql = "SELECT 1 FROM UtenteRegistrato WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Recupera un utente dal database dato il suo ID.
     * @param idUtente ID dell'utente
     * @return Oggetto Utente se presente, null altrimenti
     * @throws SQLException
     */
    public UtenteRegistrato getUtenteById(int idUtente) throws SQLException {
        String sql = "SELECT * FROM UtenteRegistrato WHERE Id_utente = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idUtente);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return mapResultSetToUtente(rs);
        }
        return null;
    }

    /**
     * Recupera un utente dal database data la sua email.
     * @param email Email dell'utente
     * @return Oggetto Utente se presente, null altrimenti
     * @throws SQLException
     */
    public UtenteRegistrato getUtenteByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM UtenteRegistrato WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return mapResultSetToUtente(rs);
        }
        return null;
    }

    /**
     * Recupera il numero di film presenti nella watchlist di un utente.
     * @param idUtente L'ID dell'utente di cui calcolare il numero di film nella watchlist
     * @return Il numero di film presenti nella watchlist dell'utente
     * @throws SQLException Se si verifica un errore durante l'accesso al database
     */
    public int getWatchlistCount(int idUtente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM WatchListItem WHERE Id_utente = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Recupera il numero di recensioni scritte da un utente.
     * @param idUtente L'ID dell'utente di cui calcolare il numero di recensioni
     * @return Il numero di recensioni scritte dall'utente
     * @throws SQLException Se si verifica un errore durante l'accesso al database
     */
    public int getRecensioniCount(int idUtente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Recensione WHERE Id_utente = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }


    /**
     * Aggiorna i dati di un utente già presente nel database.
     * @param u Utente con dati aggiornati
     * @throws SQLException
     */
    public void updateUtente(UtenteRegistrato u) throws SQLException {
        String sql = """
            UPDATE UtenteRegistrato
            SET username = ?, email = ?, password = ?, photo = ?, bio = ?, watchlistVisibility = ?
            WHERE Id_utente = ?
        """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPassword());
        ps.setString(4, u.getPhoto());
        ps.setString(5, u.getBio());
        ps.setBoolean(6, u.isWatchlistVisibility());
        ps.setInt(7, u.getIdUtente());

        ps.executeUpdate();
    }

    /**
     * Cerca gli utenti registrati il cui username contiene la stringa di ricerca fornita.
     * @param query stringa di ricerca inserita dall'utente
     * @return una lista di UtenteRegistrato che corrispondono ai criteri di ricerca;
     *         la lista è vuota se non viene trovato alcun utente.
     */
    public List<UtenteRegistrato> cercaUtenti(String query) {

        List<UtenteRegistrato> risultati = new ArrayList<>();

        String sql = "SELECT * FROM utenti WHERE username LIKE ? OR nome LIKE ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UtenteRegistrato u = new UtenteRegistrato();
                u.setIdUtente(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                risultati.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return risultati;
    }

    /**
     * Elimina un utente dal database dato il suo ID.
     * Con ON DELETE CASCADE i record associati saranno eliminati automaticamente.
     * @param idUtente ID dell'utente da eliminare
     * @throws SQLException
     */
    public void deleteUtente(int idUtente) throws SQLException {
        String sql = "DELETE FROM UtenteRegistrato WHERE Id_utente = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idUtente);
        ps.executeUpdate();
    }

    /**
     * Mappa una riga del ResultSet in un oggetto Utente.
     * @param rs ResultSet corrente
     * @return Oggetto Utente
     * @throws SQLException
     */
    private UtenteRegistrato mapResultSetToUtente(ResultSet rs) throws SQLException {
        UtenteRegistrato u = new UtenteRegistrato();
        u.setIdUtente(rs.getInt("Id_utente"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setPhoto(rs.getString("photo"));
        u.setBio(rs.getString("bio"));
        u.setWatchlistVisibility(rs.getBoolean("watchlistVisibility"));
        return u;
    }

}

