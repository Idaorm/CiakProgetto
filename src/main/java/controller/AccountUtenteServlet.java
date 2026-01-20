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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/Login.jsp");
            return;
        }

        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");

        try {

            int watchlistCount = utenteDAO.getWatchlistCount(utente.getIdUtente());
            int recensioniCount = utenteDAO.getRecensioniCount(utente.getIdUtente());


            List<WatchlistItem> watchlist = watchlistDAO.findByUtente(utente.getIdUtente());
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



            LinkedHashMap<Recensione, String> recensioniMap = recensioneDAO.doRetrieveByUtente(utente.getIdUtente());



            request.setAttribute("watchlistCount", watchlistCount);
            request.setAttribute("recensioniCount", recensioniCount);
            request.setAttribute("watchlist", watchlist);
            request.setAttribute("moviesApi", moviesApi);
            request.setAttribute("recensioniMap", recensioniMap);
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
        doGet(request, response);
    }
}