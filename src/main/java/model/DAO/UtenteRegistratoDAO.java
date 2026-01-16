package model.DAO;

import model.UtenteRegistrato;

import java.sql.*;

/**
 * DAO per l'entità Utente.
 * Incapsula tutte le operazioni di accesso al database relative agli utenti.
 */
public class UtenteRegistratoDAO {

    private Connection connection;

    /**
     * Costruttore DAO.
     * @param connection Connessione JDBC al database
     */
    public UtenteRegistratoDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserisce un nuovo utente nel database.
     * @param u Utente da inserire
     * @throws SQLException
     */
    public void insertUtente(UtenteRegistrato u) throws SQLException {
        String sql = """
            INSERT INTO UtenteRegistrato (username, email, password, photo, bio, watchlistVisibility)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPassword());
        ps.setString(4, u.getPhoto());
        ps.setString(5, u.getBio());
        ps.setBoolean(6, u.isWatchlistVisibility());

        ps.executeUpdate();
    }

    /**
     * Recupera un utente dal database dato il suo ID.
     * @param idUtente ID dell'utente
     * @return Oggetto Utente se presente, null altrimenti
     * @throws SQLException
     */
    public UtenteRegistrato getUtenteById(int idUtente) throws SQLException {
        String sql = "SELECT * FROM Utente WHERE Id_utente = ?";
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
        String sql = "SELECT * FROM Utente WHERE email = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return mapResultSetToUtente(rs);
        }
        return null;
    }

    /**
     * Aggiorna i dati di un utente già presente nel database.
     * @param u Utente con dati aggiornati
     * @throws SQLException
     */
    public void updateUtente(UtenteRegistrato u) throws SQLException {
        String sql = """
            UPDATE Utente
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
     * Elimina un utente dal database dato il suo ID.
     * Con ON DELETE CASCADE i record associati saranno eliminati automaticamente.
     * @param idUtente ID dell'utente da eliminare
     * @throws SQLException
     */
    public void deleteUtente(int idUtente) throws SQLException {
        String sql = "DELETE FROM Utente WHERE Id_utente = ?";
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

