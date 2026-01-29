package controller;

import controller.service.TmdbMovie;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import controller.service.TmdbService;
import jakarta.servlet.http.HttpSession;
import model.Recensione;
import model.UtenteRegistrato;
import controller.service.Facade;


@WebServlet("/DettaglioServlet")
public class DettaglioServlet extends HttpServlet {

    private TmdbService service = new TmdbService();
    private Facade facade = new Facade();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");
        boolean giaInWatchlist = false;

        String id = request.getParameter("id");
        if (id == null || id.isEmpty()) {
            id = request.getParameter("idTmdb");
        }


        if (id != null && !id.isEmpty()) {
            int idFilmDettaglio = Integer.parseInt(id);
            if (utente != null) {
                List<TmdbMovie> watchlist = facade.getWatchlistCompleta(utente.getIdUtente());
                if (watchlist != null) {
                    for (TmdbMovie m : watchlist) {
                        if (m.id == idFilmDettaglio) {
                            giaInWatchlist = true;
                            break;
                        }
                    }
                }
            }
        }
        request.setAttribute("giaInWatchlist", giaInWatchlist);


        if (id != null && !id.isEmpty()) {
            try {
                // Dettagli del film dall'API TMDB
                request.setAttribute("filmDettaglio", service.getMovieById(id));
                int idTmdb = Integer.parseInt(id);
                // Recupero delle recensioni
                LinkedHashMap<Recensione, UtenteRegistrato> recensioniMap = facade.getRecensioniPerFilm(idTmdb);
                request.setAttribute("recensioniMap", recensioniMap);
                double votoCommunity = facade.getMediaVotiCommunity(idTmdb);
                request.setAttribute("votoCommunity", votoCommunity);
                request.getRequestDispatcher("/jsp/Dettaglio.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                response.sendRedirect("CatalogoServlet");
            }
        } else {
            response.sendRedirect("CatalogoServlet");
        }
    }
}
