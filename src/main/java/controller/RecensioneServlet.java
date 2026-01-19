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
import model.Recensione;
import model.UtenteRegistrato;

import java.io.IOException;
import java.sql.Date;

@WebServlet("/RecensioneServlet")
public class RecensioneServlet extends HttpServlet {

    private FilmDAO filmDAO = new FilmDAO();
    private RecensioneDAO recensioneDAO = new RecensioneDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utenteLoggato");

        if (utente == null) {
            response.sendRedirect("jsp/Login.jsp");
            return;
        }

        try {
            int idTmdb = Integer.parseInt(request.getParameter("idTmdb"));
            String titolo = request.getParameter("titolo");
            int rating = Integer.parseInt(request.getParameter("rating"));
            String text = request.getParameter("text");
            Film film = filmDAO.findOrCreate(idTmdb, titolo);
            Date dataOdierna = new Date(System.currentTimeMillis());
            Recensione recensione = new Recensione(rating, text, dataOdierna, utente.getIdUtente(), film.getIdFilm());
            recensioneDAO.doSave(recensione);

            WatchlistItemDAO watchlistDAO = new WatchlistItemDAO();
            watchlistDAO.markAsWatched(utente.getIdUtente(), film.getIdFilm());

            response.sendRedirect(request.getContextPath() + "/DettaglioServlet?id=movie&idTmdb=" + idTmdb);


        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=recensione_failed");
        }
    }
}