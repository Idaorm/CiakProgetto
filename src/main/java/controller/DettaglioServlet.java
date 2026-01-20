package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

import service.TmdbService;
import model.DAO.RecensioneDAO;
import model.Recensione;

@WebServlet("/DettaglioServlet")
public class DettaglioServlet extends HttpServlet {

    private TmdbService service = new TmdbService();
    private RecensioneDAO recensioneDAO = new RecensioneDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id"); // ID del film (es. "550")

        if (id != null && !id.isEmpty()) {
            try {

                request.setAttribute("filmDettaglio", service.getMovieById(id));

                int idTmdb = Integer.parseInt(id);

                LinkedHashMap<Recensione, String> recensioniMap = recensioneDAO.doRetrieveByTmdbId(idTmdb);

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