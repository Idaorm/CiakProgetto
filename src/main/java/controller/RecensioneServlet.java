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
        String titolo = request.getParameter("titolo");

        try {

            int idTmdb = Integer.parseInt(idTmdbStr);
            int rating = Integer.parseInt(request.getParameter("rating"));
            String text = request.getParameter("text");
            String titoloEncoded = (titolo != null) ? URLEncoder.encode(titolo, StandardCharsets.UTF_8) : "";

            Facade facade;
            try {
                facade = new Facade();
            } catch (Exception e) {
                String msg = "Il servizio non è disponibile. Riprova più tardi.";
                response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdb
                        + "&titolo=" + titoloEncoded
                        + "&esito=errore&msg=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
                return;
            }

            Film film = facade.findOrCreateFilm(idTmdb, titolo);
            Recensione recensione = new Recensione(
                    rating,
                    text,
                    new java.sql.Date(System.currentTimeMillis()),
                    utente.getIdUtente(),
                    film.getIdFilm()
            );

            boolean successo = facade.salvaRecensione(recensione);

            if (!successo) {
                // recensione già presente
                String msg = "Hai già scritto una recensione per questo film!";
                response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdb
                        + "&titolo=" + titoloEncoded
                        + "&esito=errore&msg=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
                return;
            }

            // segna il film come visto
            facade.marcaComeVisto(utente.getIdUtente(), film.getIdFilm());

            // tutto OK
            response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdb
                    + "&titolo=" + titoloEncoded
                    + "&esito=success");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            String msg = "Errore durante il salvataggio della recensione. Parametri non validi.";
            response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr
                    + "&titolo=" + ((titolo != null) ? URLEncoder.encode(titolo, StandardCharsets.UTF_8) : "")
                    + "&esito=errore&msg=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
        } catch (SQLException e) {
            e.printStackTrace();
            String msg = "Errore interno durante il salvataggio della recensione.";
            response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr
                    + "&titolo=" + ((titolo != null) ? URLEncoder.encode(titolo, StandardCharsets.UTF_8) : "")
                    + "&esito=errore&msg=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            String msg = "Errore imprevisto durante il salvataggio della recensione.";
            response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr
                    + "&titolo=" + ((titolo != null) ? URLEncoder.encode(titolo, StandardCharsets.UTF_8) : "")
                    + "&esito=errore&msg=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
        }
    }
}
