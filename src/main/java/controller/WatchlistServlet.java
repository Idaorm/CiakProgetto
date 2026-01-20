package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.DAO.FilmDAO;
import model.DAO.RecensioneDAO;
import model.DAO.WatchlistItemDAO;
import model.Film;
import model.UtenteRegistrato;
import model.WatchlistItem;
import service.TmdbMovie;
import service.TmdbService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/WatchlistServlet")
public class WatchlistServlet extends HttpServlet {

    private WatchlistItemDAO watchlistDao = new WatchlistItemDAO();
    private FilmDAO filmDao = new FilmDAO();
    private TmdbService tmdbService = new TmdbService();


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/Login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if (action != null) {
            try {
                if ("add".equals(action)) {
                    String idTmdbStr = request.getParameter("idTmdb");
                    String titolo = request.getParameter("titolo");

                    if (idTmdbStr != null && titolo != null) {
                        int idTmdb = Integer.parseInt(idTmdbStr);

                        Film filmSalvato = filmDao.findOrCreate(idTmdb, titolo);
                        watchlistDao.add(utente.getIdUtente(), filmSalvato.getIdFilm());
                    }
                    response.sendRedirect(request.getContextPath() + "/CatalogoServlet");
                    return;

                } else if ("toggle".equals(action)) {
                    int idItem = Integer.parseInt(request.getParameter("idItem"));
                    boolean status = Boolean.parseBoolean(request.getParameter("status"));
                    watchlistDao.toggleStatus(idItem, status);

                    response.sendRedirect(request.getContextPath() + "/WatchlistServlet");
                    return;

                } else if ("remove".equals(action)) {
                    int idItem = Integer.parseInt(request.getParameter("idItem"));
                    watchlistDao.remove(idItem);
                    response.sendRedirect(request.getContextPath() + "/WatchlistServlet");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            List<WatchlistItem> items = watchlistDao.findByUtente(utente.getIdUtente());
            List<TmdbMovie> moviesApi = new ArrayList<>();

            for (WatchlistItem item : items) {
                int tmdbId = filmDao.recuperaIdTmdbDaIdInterno(item.getIdFilm());
                if (tmdbId > 0) {
                    TmdbMovie m = tmdbService.getMovieDetails(tmdbId);
                    if (m != null) {
                        moviesApi.add(m);
                    } else {
                        TmdbMovie placeholder = new TmdbMovie();
                        placeholder.title = "Dettagli non disponibili";
                        placeholder.poster_path = "";
                        placeholder.genre_ids = new ArrayList<>();
                        moviesApi.add(placeholder);
                    }
                }
            }
            RecensioneDAO recensioneDao = new RecensioneDAO();
            Map<model.Recensione, String> recensioniUtente = recensioneDao.doRetrieveByUtente(utente.getIdUtente());

            request.setAttribute("utente", utente);
            request.setAttribute("watchlist", items);
            request.setAttribute("moviesApi", moviesApi);
            request.setAttribute("recensioni", recensioniUtente);


            request.setAttribute("watchlistCount", items.size());
            request.setAttribute("recensioniCount", recensioniUtente.size());

            //  Forward finale
            request.getRequestDispatcher("/jsp/AccountUtente.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}