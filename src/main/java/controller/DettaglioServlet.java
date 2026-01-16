package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import service.TmdbService;

@WebServlet("/DettaglioServlet")
public class DettaglioServlet extends HttpServlet {
    private TmdbService service = new TmdbService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id != null) {
            request.setAttribute("filmDettaglio", service.getMovieById(id));
            request.getRequestDispatcher("/jsp/Dettaglio.jsp").forward(request, response);
        } else {
            response.sendRedirect("CatalogoServlet");
        }
    }
}