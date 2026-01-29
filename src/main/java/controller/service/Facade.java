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

    public Facade(UtenteRegistratoDAO utenteDAO,
                  FilmDAO filmDAO,
                  TmdbService tmdbService,
                  RecensioneDAO recensioneDAO,
                  WatchlistItemDAO watchlistDAO) {

        this.utenteDAO = utenteDAO;
        this.filmDAO = filmDAO;
        this.tmdbService = tmdbService;
        this.recensioneDAO = recensioneDAO;
        this.watchlistDAO = watchlistDAO;
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
            UtenteRegistrato utente = utenteDAO.getUtenteById(idUtente);
            if (utente == null) {
                dati.put("errore", "Utente non trovato");
                return dati;
            }
            dati.put("utente", utente);

            int watchlistCount = utenteDAO.getWatchlistCount(idUtente);
            int recensioniCount = utenteDAO.getRecensioniCount(idUtente);
            dati.put("watchlistCount", watchlistCount);
            dati.put("recensioniCount", recensioniCount);

            List<WatchlistItem> watchlist = watchlistDAO.findByUtente(idUtente);
            dati.put("watchlist", watchlist);

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

            LinkedHashMap<Recensione, String> recensioniMap = recensioneDAO.doRetrieveByUtente(idUtente);
            dati.put("recensioniMap", recensioniMap);
        } catch (SQLException e) {
            e.printStackTrace();
            dati.put("errore", "Errore interno. Riprova più tardi.");
        }
        return dati;
    }

    public void aggiornaUtente(UtenteRegistrato utente) throws SQLException {
        utenteDAO.updateUtente(utente);
    }

    public void eliminaUtente(int idUtente) throws SQLException {
        utenteDAO.deleteUtente(idUtente);
    }

    public List<UtenteRegistrato> cercaUtenti(String query) {
        return utenteDAO.cercaUtenti(query);
    }


    public Film findOrCreateFilm(int tmdbId) {
        // 1. Scarica info da TMDB
        TmdbMovie movieApi = tmdbService.getMovieDetails(tmdbId);
        String titoloReale = "Titolo Sconosciuto";

        if (movieApi != null && movieApi.title != null) {
            titoloReale = movieApi.title;
        } else {
            // Se l'API fallisce, l'ID potrebbe essere non valido
            return null;
        }

        // 2. Salva o trova nel DB usando il titolo vero
        return filmDAO.findOrCreate(tmdbId, titoloReale);
    }

    // Sovraccarico per compatibilità (ma usa la logica sicura)
    public Film findOrCreateFilm(int tmdbId, String titoloFallback) {
        Film f = findOrCreateFilm(tmdbId);
        if (f == null && titoloFallback != null) {
            // Solo se l'API fallisce usiamo il titolo passato
            return filmDAO.findOrCreate(tmdbId, titoloFallback);
        }
        return f;
    }

    public void aggiungiFilmAllaWatchlist(int idUtente, int idTmdb, String titolo) {
        // Usa il metodo sicuro
        Film film = findOrCreateFilm(idTmdb);
        if (film != null) {
            watchlistDAO.add(idUtente, film.getIdFilm());
        }
    }

    public double getMediaVotiCommunity(int idTmdb) {
        // Usa il metodo sicuro (Basta TitoloPlaceholder!)
        Film film = findOrCreateFilm(idTmdb);
        if (film != null) {
            return recensioneDAO.getMediaVotiPerFilm(film.getIdFilm());
        }
        return 0.0;
    }

    //--

    public void rimuoviFilmDallaWatchlist(int idItem) {
        watchlistDAO.remove(idItem);
    }

    public void toggleStatoWatchlistItem(int idItem, boolean statoAttuale) {
        watchlistDAO.toggleStatus(idItem, statoAttuale);
    }

    public void marcaComeVisto(int idUtente, int idFilm) {
        watchlistDAO.markAsWatched(idUtente, idFilm);
    }

    public List<WatchlistItem> getWatchlistItems(int idUtente) {
        return watchlistDAO.findByUtente(idUtente);
    }

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

    public Map<Recensione, String> getRecensioniUtente(int idUtente) {
        return recensioneDAO.doRetrieveByUtente(idUtente);
    }

    public boolean salvaRecensione(Recensione r) throws SQLException {
        Recensione esistente = recensioneDAO.doRetrieveByUtenteAndFilm(r.getIdUtente(), r.getIdFilm());
        if (esistente != null) {
            return false;
        }
        recensioneDAO.doSave(r);
        return true;
    }

    public LinkedHashMap<Recensione, UtenteRegistrato> getRecensioniPerFilm(int idTmdb) {
        return recensioneDAO.doRetrieveByTmdbId(idTmdb);
    }
}