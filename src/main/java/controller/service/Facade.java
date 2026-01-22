package controller.service;

import model.DAO.FilmDAO;
import model.DAO.RecensioneDAO;
import model.DAO.UtenteRegistratoDAO;
import model.DAO.WatchlistItemDAO;
import model.Film;
import model.Recensione;
import model.UtenteRegistrato;
import model.WatchlistItem;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.*;

public class Facade {

    private UtenteRegistratoDAO utenteDAO;
    private FilmDAO filmDAO;
    private TmdbService tmdbService;
    private RecensioneDAO recensioneDAO;
    private WatchlistItemDAO watchlistDAO;

    public Facade() {
        this.utenteDAO = new UtenteRegistratoDAO();
        this.filmDAO = new FilmDAO();
        this.tmdbService = new TmdbService();
        this.recensioneDAO = new RecensioneDAO();
        this.watchlistDAO = new WatchlistItemDAO();
    }

    // REGISTRAZIONE
    public boolean registraUtente(String email, String password) throws SQLException {
        if (utenteDAO.emailGiaRegistrata(email)) {
            return false;
        }
        String salt = BCrypt.gensalt();
        String passwordHash = BCrypt.hashpw(password, salt);
        UtenteRegistrato nuovoUtente = new UtenteRegistrato();
        nuovoUtente.setUsername(email);
        nuovoUtente.setEmail(email);
        nuovoUtente.setPassword(passwordHash);
        return utenteDAO.insertUtente(nuovoUtente);
    }

    // LOGIN
    public UtenteRegistrato login(String email, String password) throws SQLException {
        UtenteRegistrato utente = utenteDAO.getUtenteByEmail(email);
        if (utente != null && BCrypt.checkpw(password, utente.getPassword())) {
            return utente;
        }
        return null;
    }

    // PROFILO UTENTE
    public Map<String, Object> getDatiProfiloCompleti(int idUtente) {
        Map<String, Object> dati = new HashMap<>();
        try {
            // recupera utente
            UtenteRegistrato utente = utenteDAO.getUtenteById(idUtente);
            if (utente == null) {
                dati.put("errore", "Utente non trovato");
                return dati;
            }
            dati.put("utente", utente);
            // conteggi
            int watchlistCount = utenteDAO.getWatchlistCount(idUtente);
            int recensioniCount = utenteDAO.getRecensioniCount(idUtente);
            dati.put("watchlistCount", watchlistCount);
            dati.put("recensioniCount", recensioniCount);
            // watchlist dettagliata
            List<WatchlistItem> watchlist = watchlistDAO.findByUtente(idUtente);
            dati.put("watchlist", watchlist);
            // dettagli TMDB dei film
            List<TmdbMovie> moviesApi = new ArrayList<>();
            if (watchlist != null) {
                for (WatchlistItem item : watchlist) {
                    int idTmdb = filmDAO.recuperaIdTmdbDaIdInterno(item.getIdFilm());
                    TmdbMovie movie = null;
                    if (idTmdb > 0) {
                        movie = tmdbService.getMovieDetails(idTmdb);
                    }
                    if (movie == null) {
                        movie = new TmdbMovie();
                        movie.title = "Titolo non disponibile";
                    }
                    moviesApi.add(movie);
                }
            }
            dati.put("moviesApi", moviesApi);
            // recensioni dell'utente
            LinkedHashMap<Recensione, String> recensioniMap = recensioneDAO.doRetrieveByUtente(idUtente);
            dati.put("recensioniMap", recensioniMap);
        } catch (SQLException e) {
            e.printStackTrace();
            dati.put("errore", "Errore interno. Riprova più tardi.");
        }
        return dati;
    }

    // MODIFICA ACCOUNT
    public void aggiornaUtente(UtenteRegistrato utente) throws SQLException {
        utenteDAO.updateUtente(utente);
    }

    // ELIMINA ACCOUNT
    public void eliminaUtente(int idUtente) throws SQLException {
        utenteDAO.deleteUtente(idUtente);
    }

    // RICERCA UTENTI
    public List<UtenteRegistrato> cercaUtenti(String query) {
        return utenteDAO.cercaUtenti(query);
    }

    // AGGIUNGI FILM ALLA WATCHLIST
    public void aggiungiFilmAllaWatchlist(int idUtente, int idTmdb, String titolo) {
        Film film = filmDAO.findOrCreate(idTmdb, titolo);
        watchlistDAO.add(idUtente, film.getIdFilm());
    }

    // RIMUOVI FILM DALLA WATCHLIST
    public void rimuoviFilmDallaWatchlist(int idItem) {
        watchlistDAO.remove(idItem);
    }

    // CAMBIA STATO DEL FILM NELLA WATCHLIST
    public void toggleStatoWatchlistItem(int idItem, boolean statoAttuale) {
        watchlistDAO.toggleStatus(idItem, statoAttuale);
    }

    // SEGNA FILM COME "VISTO"
    public void marcaComeVisto(int idUtente, int idFilm) {
        watchlistDAO.markAsWatched(idUtente, idFilm);
    }

    // RECUPERA WATCHLIST DI UN UTENTE
    public List<WatchlistItem> getWatchlistItems(int idUtente) {
        return watchlistDAO.findByUtente(idUtente);
    }

    // RECUPERA WATCHLIST DI UN UTENTE (DETTAGLIATA)
    public List<TmdbMovie> getWatchlistCompleta(int idUtente) {
        List<WatchlistItem> items = watchlistDAO.findByUtente(idUtente);
        List<TmdbMovie> moviesApi = new ArrayList<>();
        for (WatchlistItem item : items) {
            int tmdbId = filmDAO.recuperaIdTmdbDaIdInterno(item.getIdFilm());
            TmdbMovie movie = (tmdbId > 0) ? tmdbService.getMovieDetails(tmdbId) : null;
            if (movie == null || movie.title == null) {
                movie = new TmdbMovie();
                movie.title = "Titolo non disponibile";
                movie.poster_path = null;
                movie.release_date = "";
                movie.genre_ids = new ArrayList<>();
            }
            moviesApi.add(movie);
        }
        return moviesApi;
    }

    // RECUPERA RECENSIONI DI UN UTENTE
    public Map<Recensione, String> getRecensioniUtente(int idUtente) {
        return recensioneDAO.doRetrieveByUtente(idUtente);
    }

    // TROVA FILM NEL DB
    public Film findOrCreateFilm(int tmdbId, String titolo) {
        return filmDAO.findOrCreate(tmdbId, titolo);
    }

    // SALVA NUOVA RECENSIONE
    public boolean salvaRecensione(Recensione r) throws SQLException {
        Recensione esistente = recensioneDAO.doRetrieveByUtenteAndFilm(r.getIdUtente(), r.getIdFilm());
        if (esistente != null) {
            return false;
        }
        recensioneDAO.doSave(r);
        return true;
    }

    // RECUPERA RECENSIONI DI UN FILM
    public LinkedHashMap<Recensione, UtenteRegistrato> getRecensioniPerFilm(int idTmdb) {
        return recensioneDAO.doRetrieveByTmdbId(idTmdb);
    }

}
