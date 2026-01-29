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
import controller.service.Facade;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

            Film filmReale = facade.findOrCreateFilm(idTmdb);

            if (filmReale == null) {
                response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr
                        + "&titolo=" + URLEncoder.encode(titoloInput, StandardCharsets.UTF_8)
                        + "&esito=errore&msg=" + URLEncoder.encode("ID Film inesistente.", StandardCharsets.UTF_8));
                return;
            }

            String titoloVero = filmReale.getTitolo();


            if (titoloInput == null || !titoloInput.trim().equalsIgnoreCase(titoloVero.trim())) {

                String msg = "Errore : Il titolo  non corrisponde all'ID ";

                response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr
                        + "&titolo=" + URLEncoder.encode(titoloInput, StandardCharsets.UTF_8)
                        + "&esito=errore&msg=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
                return;
            }

            Recensione recensione = new Recensione(
                    rating,
                    text,
                    new java.sql.Date(System.currentTimeMillis()),
                    utente.getIdUtente(),
                    filmReale.getIdFilm()
            );

            boolean successo = facade.salvaRecensione(recensione);

            if (!successo) {
                String msg = "Hai già scritto una recensione per questo film!";
                response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr
                        + "&titolo=" + URLEncoder.encode(titoloVero, StandardCharsets.UTF_8)
                        + "&esito=errore&msg=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
                return;
            }

            facade.marcaComeVisto(utente.getIdUtente(), filmReale.getIdFilm());

            response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr
                    + "&titolo=" + URLEncoder.encode(titoloVero, StandardCharsets.UTF_8)
                    + "&esito=success");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr
                    + "&titolo=" + (titoloInput != null ? URLEncoder.encode(titoloInput, StandardCharsets.UTF_8) : "")
                    + "&esito=errore&msg=" + URLEncoder.encode("Errore generico nel salvataggio.", StandardCharsets.UTF_8));
        }
    }
}