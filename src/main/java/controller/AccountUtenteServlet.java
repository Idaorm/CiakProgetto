package controller;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.DAO.UtenteRegistratoDAO;
import model.UtenteRegistrato;
import model.WatchlistItem;
import service.TmdbMovie;
import model.DAO.WatchlistItemDAO;
import model.DAO.FilmDAO;
import model.Film;
import java.util.List;
import java.util.ArrayList;


import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AccountUtenteServlet", value = "/AccountUtenteServlet")
public class AccountUtenteServlet extends HttpServlet {

    private UtenteRegistratoDAO utenteDAO;
    private WatchlistItemDAO watchlistDAO;
    private FilmDAO filmDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        utenteDAO = new UtenteRegistratoDAO();
        this.watchlistDAO = new WatchlistItemDAO();
        this.filmDAO = new FilmDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recupera l'utente dalla sessione
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            // Se non loggato, rimanda al login
            response.sendRedirect(request.getContextPath() + "/jsp/Login.jsp");
            return;
        }

        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");

        try {
            int watchlistCount = utenteDAO.getWatchlistCount(utente.getIdUtente());
            int recensioniCount = utenteDAO.getRecensioniCount(utente.getIdUtente());

            WatchlistItemDAO watchlistDAO = new WatchlistItemDAO();
            List<WatchlistItem> watchlist = watchlistDAO.findByUtente(utente.getIdUtente());

            // Recupera i dettagli estetici (titoli, poster) dalle API di TMDB
            service.TmdbService tmdbService = new service.TmdbService();
            List<TmdbMovie> moviesApi = new ArrayList<>();

            if (watchlist != null) {
                for (WatchlistItem item : watchlist) {

                    // Traduciamo l'ID interno del DB nell'ID TMDB ufficiale
                    int idTmdbVero = filmDAO.recuperaIdTmdbDaIdInterno(item.getIdFilm());

                    if (idTmdbVero != 0) {
                        // Se l'ID esiste, chiediamo i dati (poster, titolo) a TMDB
                        TmdbMovie movie = tmdbService.getMovieDetails(idTmdbVero);

                        if (movie != null) {
                            moviesApi.add(movie);
                        } else {
                            TmdbMovie errorMovie = new TmdbMovie();
                            errorMovie.title="Dati TMDB non disponibili";
                            moviesApi.add(errorMovie);
                        }
                    } else {
                        // Se l'ID non è stato trovato nel catalogo interno
                        TmdbMovie errorMovie = new TmdbMovie();
                        errorMovie.title="Film non trovato nel catalogo";
                        moviesApi.add(errorMovie);
                    }
                }
            }

            // Imposta attributi per la JSP
            request.setAttribute("watchlistCount", watchlistCount);
            request.setAttribute("recensioniCount", recensioniCount);
            request.setAttribute("watchlist", watchlist);
            request.setAttribute("moviesApi", moviesApi);
            request.setAttribute("utente", utente);

            // Passa l'utente alla JSP (per usare i dati in JSP)
            request.setAttribute("utente", utente);

            request.getRequestDispatcher("/jsp/AccountUtente.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore interno. Riprova più tardi.");
            request.getRequestDispatcher("/jsp/Login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Elabora la richiesta
        doGet(request, response);
    }
    
}