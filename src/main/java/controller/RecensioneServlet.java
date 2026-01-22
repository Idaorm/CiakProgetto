package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Film;
import model.Recensione;
import model.UtenteRegistrato;
import service.Facade;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet("/RecensioneServlet")
public class RecensioneServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("jsp/Login.jsp");
            return;
        }

        String idTmdbStr = request.getParameter("idTmdb");
        String titoloInput = request.getParameter("titolo");
        String ratingStr = request.getParameter("rating");
        String text = request.getParameter("text");

        try {
            int idTmdb = Integer.parseInt(idTmdbStr);
            int rating = Integer.parseInt(ratingStr);

            Facade facade = new Facade();
            Film film = facade.findOrCreateFilm(idTmdb, null);

            String titoloReale = (film != null) ? film.getTitolo() : "Dettaglio";
            String titoloEncoded = URLEncoder.encode(titoloReale, StandardCharsets.UTF_8);

            Recensione recensione = new Recensione(
                    rating,
                    text,
                    new java.sql.Date(System.currentTimeMillis()),
                    utente.getIdUtente(),
                    film.getIdFilm()
            );

            boolean successo = facade.salvaRecensione(recensione);

            if (!successo) {
                String msg = "Hai già scritto una recensione per questo film!";
                response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr
                        + "&titolo=" + titoloEncoded
                        + "&esito=errore&msg=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
                return;
            }

            facade.marcaComeVisto(utente.getIdUtente(), film.getIdFilm());

            response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr
                    + "&titolo=" + titoloEncoded
                    + "&esito=success");

        } catch (Exception e) {
            e.printStackTrace();

            response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr
                    + "&titolo=" + URLEncoder.encode("ErroreID", StandardCharsets.UTF_8)
                    + "&esito=errore&msg=" + URLEncoder.encode("Impossibile trovare il film. ID non valido.", StandardCharsets.UTF_8));
        }
    }
}