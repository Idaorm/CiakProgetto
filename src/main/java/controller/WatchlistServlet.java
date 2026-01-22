package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Recensione;
import model.UtenteRegistrato;
import model.WatchlistItem;
import service.Facade;
import service.TmdbMovie;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/WatchlistServlet")
public class WatchlistServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Facade facade;
        try {
            facade = new Facade();
        } catch (Exception e) {
            request.setAttribute("errore", "Il servizio non è disponibile. Riprova più tardi.");
            request.getRequestDispatcher("/jsp/Login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/Login.jsp");
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                int idTmdb = Integer.parseInt(request.getParameter("idTmdb"));
                String titolo = request.getParameter("titolo");
                facade.aggiungiFilmAllaWatchlist(utente.getIdUtente(), idTmdb, titolo);
                response.sendRedirect(request.getContextPath() + "/WatchlistServlet");
                return;
            } else if ("toggle".equals(action)) {
                int idItem = Integer.parseInt(request.getParameter("idItem"));
                boolean status = Boolean.parseBoolean(request.getParameter("status"));
                facade.toggleStatoWatchlistItem(idItem, status);
                response.sendRedirect(request.getContextPath() + "/WatchlistServlet");
                return;
            } else if ("remove".equals(action)) {
                int idItem = Integer.parseInt(request.getParameter("idItem"));
                facade.rimuoviFilmDallaWatchlist(idItem);
                response.sendRedirect(request.getContextPath() + "/WatchlistServlet");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/jsp/AccountUtente.jsp");
            return;
        }

        // Recupera dati da mostrare nella JSP
        List<WatchlistItem> items = facade.getWatchlistItems(utente.getIdUtente());
        List<TmdbMovie> moviesApi = facade.getWatchlistCompleta(utente.getIdUtente());
        Map<Recensione, String> recensioniUtente = facade.getRecensioniUtente(utente.getIdUtente());

        request.setAttribute("utente", utente);
        request.setAttribute("watchlist", items);
        request.setAttribute("moviesApi", moviesApi);
        request.setAttribute("recensioniMap", recensioniUtente);
        request.setAttribute("watchlistCount", items.size());
        request.setAttribute("recensioniCount", recensioniUtente.size());

        request.getRequestDispatcher("/jsp/AccountUtente.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
