package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

import controller.service.TmdbService;
import model.Recensione;
import model.UtenteRegistrato;
import controller.service.Facade;

@WebServlet("/DettaglioServlet")
public class DettaglioServlet extends HttpServlet {

    private TmdbService service = new TmdbService();
    private Facade facade = new Facade();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        if (id != null && !id.isEmpty()) {
            try {
                // Dettagli del film dall'API TMDB
                request.setAttribute("filmDettaglio", service.getMovieById(id));
                int idTmdb = Integer.parseInt(id);
                // Recupero delle recensioni
                LinkedHashMap<Recensione, UtenteRegistrato> recensioniMap = facade.getRecensioniPerFilm(idTmdb);
                request.setAttribute("recensioniMap", recensioniMap);
                request.getRequestDispatcher("/jsp/Dettaglio.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                response.sendRedirect("CatalogoServlet");
            }
        } else {
            response.sendRedirect("CatalogoServlet");
        }
    }
}
