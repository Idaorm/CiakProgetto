package controller;

import controller.service.Facade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import controller.service.TmdbMovie;
import controller.service.TmdbService;
import jakarta.servlet.http.HttpSession;
import model.UtenteRegistrato;

@WebServlet("/CatalogoServlet")
public class CatalogoServlet extends HttpServlet {

    private TmdbService tmdbService= new TmdbService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");
        Set<Integer> idsInWatchlist = new HashSet<>();

        if (utente != null) {
            Facade facade = new Facade();
            List<TmdbMovie> watchlistCompleta = facade.getWatchlistCompleta(utente.getIdUtente());
            if (watchlistCompleta != null) {
                for (TmdbMovie m : watchlistCompleta) {
                    idsInWatchlist.add(m.id);
                }
            }
        }
        request.setAttribute("idsInWatchlist", idsInWatchlist);

        String query = request.getParameter("q");

        List<TmdbMovie> movies;

        if (query != null && !query.trim().isEmpty()) {
            movies = tmdbService.searchMovies(query);
        } else {
            movies = tmdbService.getPopularMovies();
        }

        request.setAttribute("filmPopolari", movies);
        request.getRequestDispatcher("/jsp/CatalogoFilm.jsp").forward(request, response);
    }
}
