package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import controller.service.TmdbMovie;
import controller.service.TmdbService;

@WebServlet("/CatalogoServlet")
public class CatalogoServlet extends HttpServlet {

    private TmdbService tmdbService= new TmdbService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
