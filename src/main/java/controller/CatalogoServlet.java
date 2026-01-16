package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import service.TmdbMovie;
import service.TmdbService;

@WebServlet("/CatalogoServlet")
public class CatalogoServlet extends HttpServlet {

    private TmdbService service = new TmdbService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String query = request.getParameter("q");

        List<TmdbMovie> movies;

        if (query != null && !query.trim().isEmpty()) {
            System.out.println("Ricerca utente: " + query);
            movies = service.searchMovies(query);
        } else {
            movies = service.getPopularMovies();
        }

        request.setAttribute("filmPopolari", movies);

        request.getRequestDispatcher("/jsp/CatalogoFilm.jsp").forward(request, response);
    }
}