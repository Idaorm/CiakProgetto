package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.DAO.FilmDAO;
import model.DAO.WatchlistItemDAO;
import model.Film;
import model.UtenteRegistrato;
import model.WatchlistItem;
import service.TmdbMovie;
import service.TmdbService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/WatchlistServlet")
public class WatchlistServlet extends HttpServlet {
    private WatchlistItemDAO watchlistDao = new WatchlistItemDAO();
    private FilmDAO filmDao = new FilmDAO();
    private TmdbService tmdbService = new TmdbService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utenteLoggato");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            try {
                int idTmdb = Integer.parseInt(request.getParameter("idTmdb"));
                String titolo = request.getParameter("titolo");
                Film filmSalvato = filmDao.findOrCreate(idTmdb, titolo);
                watchlistDao.add(utente.getIdUtente(), filmSalvato.getIdFilm());
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect("WatchlistServlet");
            return;
        } else if ("toggle".equals(action)) {
            int idItem = Integer.parseInt(request.getParameter("idItem"));
            boolean status = Boolean.parseBoolean(request.getParameter("status"));
            watchlistDao.toggleStatus(idItem, status);
        } else if ("remove".equals(action)) {
            int idItem = Integer.parseInt(request.getParameter("idItem"));
            watchlistDao.remove(idItem);
        }

        // CARICAMENTO DATI
        List<WatchlistItem> items = watchlistDao.findByUtente(utente.getIdUtente());
        List<TmdbMovie> moviesApi = new ArrayList<>();

        for (WatchlistItem item : items) {
            int tmdbId = recuperaIdTmdbDaIdInterno(item.getIdFilm());
            TmdbMovie m = tmdbService.getMovieDetails(tmdbId);

            if (m != null) {
                moviesApi.add(m);
            } else {

                TmdbMovie placeholder = new TmdbMovie();
                placeholder.title = "Titolo non disponibile";
                placeholder.poster_path = ""; // O un'immagine di default
                moviesApi.add(placeholder);
            }
        }

        request.setAttribute("items", items);
        request.setAttribute("moviesApi", moviesApi);
        request.getRequestDispatcher("Watchlist.jsp").forward(request, response);
    }

    private int recuperaIdTmdbDaIdInterno(int idFilmInterno) {
        try (java.sql.Connection conn = util.DBConnection.getConnection()) {
            String sql = "SELECT Id_tmdb FROM Film WHERE Id_film = ?";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idFilmInterno);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("Id_tmdb");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
