package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.DAO.UtenteRegistratoDAO;
import model.DAO.RecensioneDAO;
import model.Recensione;
import model.UtenteRegistrato;
import model.WatchlistItem;
import service.TmdbMovie;
import model.DAO.WatchlistItemDAO;
import model.DAO.FilmDAO;
import model.Film;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AccountUtenteServlet", value = "/AccountUtenteServlet")
public class AccountUtenteServlet extends HttpServlet {

    private UtenteRegistratoDAO utenteDAO;
    private WatchlistItemDAO watchlistDAO;
    private FilmDAO filmDAO;
    private RecensioneDAO recensioneDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.utenteDAO = new UtenteRegistratoDAO();
        this.watchlistDAO = new WatchlistItemDAO();
        this.filmDAO = new FilmDAO();
        this.recensioneDAO = new RecensioneDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UtenteRegistrato utenteDaVisualizzare = null;

        // Se esiste parametro id, cerchiamo l'utente dal DB
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int idUtente = Integer.parseInt(idParam);
                utenteDaVisualizzare = utenteDAO.getUtenteById(idUtente);
                if (utenteDaVisualizzare == null) {
                    request.setAttribute("errore", "Utente non trovato.");
                    request.getRequestDispatcher("/jsp/RisultatiCercaUtenti.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errore", "ID utente non valido.");
                request.getRequestDispatcher("/jsp/RisultatiCercaUtenti.jsp").forward(request, response);
                return;
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("errore", "Errore interno. Riprova più tardi.");
                request.getRequestDispatcher("/jsp/RisultatiCercaUtenti.jsp").forward(request, response);
                return;
            }
        } else {
            // Altrimenti fallback al profilo dell'utente loggato
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("utente") == null) {
                response.sendRedirect(request.getContextPath() + "/jsp/Login.jsp");
                return;
            }
            utenteDaVisualizzare = (UtenteRegistrato) session.getAttribute("utente");
        }

        try {
            // Conteggi
            int watchlistCount = utenteDAO.getWatchlistCount(utenteDaVisualizzare.getIdUtente());
            int recensioniCount = utenteDAO.getRecensioniCount(utenteDaVisualizzare.getIdUtente());

            // Watchlist dettagliata
            List<WatchlistItem> watchlist = watchlistDAO.findByUtente(utenteDaVisualizzare.getIdUtente());
            service.TmdbService tmdbService = new service.TmdbService();
            List<TmdbMovie> moviesApi = new ArrayList<>();

            if (watchlist != null) {
                for (WatchlistItem item : watchlist) {
                    int idTmdbVero = filmDAO.recuperaIdTmdbDaIdInterno(item.getIdFilm());
                    if (idTmdbVero != 0) {
                        TmdbMovie movie = tmdbService.getMovieDetails(idTmdbVero);
                        if (movie != null) {
                            moviesApi.add(movie);
                        } else {
                            TmdbMovie errorMovie = new TmdbMovie();
                            errorMovie.title="Dati TMDB non disponibili";
                            moviesApi.add(errorMovie);
                        }
                    } else {
                        TmdbMovie errorMovie = new TmdbMovie();
                        errorMovie.title="Film non trovato nel catalogo";
                        moviesApi.add(errorMovie);
                    }
                }
            }

            // Recensioni
            LinkedHashMap<Recensione, String> recensioniMap = recensioneDAO.doRetrieveByUtente(utenteDaVisualizzare.getIdUtente());

            // Set attributi
            request.setAttribute("watchlistCount", watchlistCount);
            request.setAttribute("recensioniCount", recensioniCount);
            request.setAttribute("watchlist", watchlist);
            request.setAttribute("moviesApi", moviesApi);
            request.setAttribute("recensioniMap", recensioniMap);
            request.setAttribute("utente", utenteDaVisualizzare);

            request.getRequestDispatcher("/jsp/AccountUtente.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore interno. Riprova più tardi.");
            request.getRequestDispatcher("/jsp/Login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
