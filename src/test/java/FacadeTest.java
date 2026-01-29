import controller.service.Facade;
import controller.service.TmdbMovie;
import controller.service.TmdbService;

import model.DAO.FilmDAO;
import model.DAO.RecensioneDAO;
import model.DAO.UtenteRegistratoDAO;
import model.DAO.WatchlistItemDAO;

import model.Film;
import model.Recensione;
import model.UtenteRegistrato;
import model.WatchlistItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FacadeTest {

    private Facade facade;
    private UtenteRegistratoDAO utenteDAO;
    private FilmDAO filmDAO;
    private TmdbService tmdbService;
    private RecensioneDAO recensioneDAO;
    private WatchlistItemDAO watchlistDAO;

    @BeforeEach
    void setUp() {
        utenteDAO = mock(UtenteRegistratoDAO.class);
        filmDAO = mock(FilmDAO.class);
        tmdbService = mock(TmdbService.class);
        recensioneDAO = mock(RecensioneDAO.class);
        watchlistDAO = mock(WatchlistItemDAO.class);

        facade = new Facade(utenteDAO, filmDAO, tmdbService, recensioneDAO, watchlistDAO);
    }

    // -------- REGISTRAZIONE --------
    @Test
    void registraUtente_shouldReturnFalseIfEmailExists() throws SQLException {
        when(utenteDAO.emailGiaRegistrata("test@mail.it")).thenReturn(true);

        boolean result = facade.registraUtente("test@mail.it", "password");

        assertFalse(result);
        verify(utenteDAO, never()).insertUtente(any());
    }

    @Test
    void registraUtente_shouldInsertUserIfEmailNotExists() throws SQLException {
        when(utenteDAO.emailGiaRegistrata("new@mail.it")).thenReturn(false);
        when(utenteDAO.insertUtente(any(UtenteRegistrato.class))).thenReturn(true);

        boolean result = facade.registraUtente("new@mail.it", "password123");

        assertTrue(result);
        verify(utenteDAO).insertUtente(any(UtenteRegistrato.class));
    }

    // -------- LOGIN --------
    @Test
    void login_shouldReturnUserIfPasswordIsCorrect() throws SQLException {
        UtenteRegistrato u = new UtenteRegistrato();
        u.setPassword(BCrypt.hashpw("password123", BCrypt.gensalt()));
        when(utenteDAO.getUtenteByEmail("test@mail.it")).thenReturn(u);

        UtenteRegistrato result = facade.login("test@mail.it", "password123");

        assertNotNull(result);
    }

    @Test
    void login_shouldReturnNullIfPasswordIncorrect() throws SQLException {
        UtenteRegistrato u = new UtenteRegistrato();
        u.setPassword(BCrypt.hashpw("password123", BCrypt.gensalt()));
        when(utenteDAO.getUtenteByEmail("test@mail.it")).thenReturn(u);

        UtenteRegistrato result = facade.login("test@mail.it", "wrongpass");

        assertNull(result);
    }

    // -------- PROFILO UTENTE --------
    @Test
    void getDatiProfiloCompleti_shouldReturnData() throws SQLException {
        UtenteRegistrato u = new UtenteRegistrato();
        u.setIdUtente(1);
        when(utenteDAO.getUtenteById(1)).thenReturn(u);
        when(utenteDAO.getWatchlistCount(1)).thenReturn(5);
        when(utenteDAO.getRecensioniCount(1)).thenReturn(3);

        WatchlistItem item = new WatchlistItem();
        item.setIdFilm(10);
        when(watchlistDAO.findByUtente(1)).thenReturn(Collections.singletonList(item));
        when(filmDAO.recuperaIdTmdbDaIdInterno(10)).thenReturn(100);
        TmdbMovie movie = new TmdbMovie();
        movie.title = "FilmTest";
        when(tmdbService.getMovieDetails(100)).thenReturn(movie);

        LinkedHashMap<Recensione, String> recensioni = new LinkedHashMap<>();
        when(recensioneDAO.doRetrieveByUtente(1)).thenReturn(recensioni);

        Map<String, Object> dati = facade.getDatiProfiloCompleti(1);

        assertEquals(u, dati.get("utente"));
        assertEquals(5, dati.get("watchlistCount"));
        assertEquals(3, dati.get("recensioniCount"));
        assertEquals(1, ((List<?>) dati.get("watchlist")).size());
        assertEquals("FilmTest", ((List<TmdbMovie>) dati.get("moviesApi")).get(0).title);
        assertEquals(recensioni, dati.get("recensioniMap"));
    }

    // -------- AGGIORNA / ELIMINA / CERCA UTENTE --------
    @Test
    void aggiornaUtente_shouldCallDao() throws SQLException {
        UtenteRegistrato u = new UtenteRegistrato();
        facade.aggiornaUtente(u);
        verify(utenteDAO).updateUtente(u);
    }

    @Test
    void eliminaUtente_shouldCallDao() throws SQLException {
        facade.eliminaUtente(1);
        verify(utenteDAO).deleteUtente(1);
    }

    @Test
    void cercaUtenti_shouldReturnList() {
        List<UtenteRegistrato> list = Collections.singletonList(new UtenteRegistrato());
        when(utenteDAO.cercaUtenti("query")).thenReturn(list);

        List<UtenteRegistrato> result = facade.cercaUtenti("query");

        assertEquals(list, result);
    }

    // -------- WATCHLIST --------
    @Test
    void aggiungiFilmAllaWatchlist_shouldAddFilmForUser() {
        Film film = new Film();
        film.setIdFilm(10);
        when(filmDAO.findOrCreate(100, "Matrix")).thenReturn(film);

        facade.aggiungiFilmAllaWatchlist(1, 100, "Matrix");

        verify(filmDAO).findOrCreate(100, "Matrix");
        verify(watchlistDAO).add(1, 10);
    }

    @Test
    void rimuoviFilmDallaWatchlist_shouldCallDao() {
        facade.rimuoviFilmDallaWatchlist(5);
        verify(watchlistDAO).remove(5);
    }

    @Test
    void toggleStatoWatchlistItem_shouldCallDao() {
        facade.toggleStatoWatchlistItem(5, true);
        verify(watchlistDAO).toggleStatus(5, true);
    }

    @Test
    void marcaComeVisto_shouldCallDao() {
        facade.marcaComeVisto(1, 10);
        verify(watchlistDAO).markAsWatched(1, 10);
    }

    @Test
    void getWatchlistItems_shouldReturnList() {
        List<WatchlistItem> list = Collections.singletonList(new WatchlistItem());
        when(watchlistDAO.findByUtente(1)).thenReturn(list);

        List<WatchlistItem> result = facade.getWatchlistItems(1);

        assertEquals(list, result);
    }

    @Test
    void getWatchlistCompleta_shouldReturnMovies() {
        WatchlistItem item = new WatchlistItem();
        item.setIdFilm(10);
        when(watchlistDAO.findByUtente(1)).thenReturn(Collections.singletonList(item));
        when(filmDAO.recuperaIdTmdbDaIdInterno(10)).thenReturn(100);
        TmdbMovie movie = new TmdbMovie();
        movie.title = "Matrix";
        when(tmdbService.getMovieDetails(100)).thenReturn(movie);

        List<TmdbMovie> result = facade.getWatchlistCompleta(1);

        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).title);
    }

    // -------- RECENSIONI --------


    @Test
    void findOrCreateFilm_shouldReturnFilm() {

        TmdbMovie movieMock = new TmdbMovie();
        movieMock.title = "Matrix Real";
        when(tmdbService.getMovieDetails(100)).thenReturn(movieMock);


        Film filmAtteso = new Film();
        filmAtteso.setTitolo("Matrix Real");
        when(filmDAO.findOrCreate(100, "Matrix Real")).thenReturn(filmAtteso);

        Film result = facade.findOrCreateFilm(100, "Matrix");

        assertNotNull(result);
        assertEquals("Matrix Real", result.getTitolo());
        verify(tmdbService).getMovieDetails(100);
    }

    @Test
    void salvaRecensione_shouldReturnFalseIfExists() throws SQLException {
        Recensione r = new Recensione();
        when(recensioneDAO.doRetrieveByUtenteAndFilm(anyInt(), anyInt())).thenReturn(new Recensione());

        assertFalse(facade.salvaRecensione(r));
    }

    @Test
    void salvaRecensione_shouldReturnTrueIfNotExists() throws SQLException {
        Recensione r = new Recensione();
        when(recensioneDAO.doRetrieveByUtenteAndFilm(anyInt(), anyInt())).thenReturn(null);

        assertTrue(facade.salvaRecensione(r));
        verify(recensioneDAO).doSave(r);
    }

    @Test
    void getMediaVotiCommunity_shouldReturnAverage() {
        Film filmMock = new Film();
        filmMock.setIdFilm(50);
        when(filmDAO.findOrCreate(eq(999), anyString())).thenReturn(filmMock);
        when(recensioneDAO.getMediaVotiPerFilm(50)).thenReturn(4.5);
        double media = facade.getMediaVotiCommunity(999);
        assertEquals(4.5, media);
        verify(recensioneDAO).getMediaVotiPerFilm(50);
    }

}
