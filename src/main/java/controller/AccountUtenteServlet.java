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
        UtenteRegistrato utenteLoggato = (session != null) ? (UtenteRegistrato) session.getAttribute("utente") : null;


        String idParam = request.getParameter("id");
        UtenteRegistrato utenteDaVisualizzare = null;

        try {
            if (idParam != null && !idParam.isEmpty()) {
                // Sto visitando il profilo di qualcun altro
                int idRichiesto = Integer.parseInt(idParam);
                utenteDaVisualizzare = utenteDAO.getUtenteById(idRichiesto);

            }


            if (utenteDaVisualizzare == null) {
                utenteDaVisualizzare = utenteLoggato;
            }


            if (utenteDaVisualizzare == null) {
                response.sendRedirect(request.getContextPath() + "/jsp/Login.jsp");
                return;
            }

            boolean isMyProfile = (utenteLoggato != null && utenteLoggato.getIdUtente() == utenteDaVisualizzare.getIdUtente());

            int targetId = utenteDaVisualizzare.getIdUtente();

            int watchlistCount = utenteDAO.getWatchlistCount(targetId);
            int recensioniCount = utenteDAO.getRecensioniCount(targetId);
            List<WatchlistItem> watchlist = watchlistDAO.findByUtente(targetId);

            LinkedHashMap<Recensione, String> recensioniMap = recensioneDAO.doRetrieveByUtente(targetId);

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
                            errorMovie.title = "Dati TMDB non disponibili";
                            moviesApi.add(errorMovie);
                        }
                    } else {
                        TmdbMovie errorMovie = new TmdbMovie();
                        errorMovie.title = "Film non trovato nel catalogo";
                        moviesApi.add(errorMovie);
                    }
                }
            }


            request.setAttribute("watchlistCount", watchlistCount);
            request.setAttribute("recensioniCount", recensioniCount);
            request.setAttribute("watchlist", watchlist);
            request.setAttribute("moviesApi", moviesApi);
            request.setAttribute("recensioniMap", recensioniMap);

            request.setAttribute("profiloEsterno", utenteDaVisualizzare);

            request.setAttribute("isMyProfile", isMyProfile);

            request.getRequestDispatcher("/jsp/AccountUtente.jsp").forward(request, response);

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore nel recupero del profilo.");
            request.getRequestDispatcher("/jsp/Error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}