package controller;

import controller.service.Facade;
import model.Film;
import model.Recensione;
import model.UtenteRegistrato;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;

@WebServlet("/RecensioneServlet")
public class RecensioneServlet extends HttpServlet {

    private Facade facade = new Facade();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/Login.jsp");
            return;
        }

        String idTmdbStr = request.getParameter("idTmdb");
        String ratingStr = request.getParameter("rating");
        String text = request.getParameter("text");


        String titoloInput = request.getParameter("titolo");

        if (idTmdbStr == null || ratingStr == null || text == null || text.trim().isEmpty()) {
            redirectError(response, request, idTmdbStr, titoloInput, "Dati mancanti. Compila tutti i campi.");
            return;
        }

        if (text.length() > 200) {
            redirectError(response, request, idTmdbStr, titoloInput, "Recensione troppo lunga (max 200 caratteri).");
            return;
        }

        try {
            int idTmdb = Integer.parseInt(idTmdbStr);
            int rating = Integer.parseInt(ratingStr);

            Film film = facade.findOrCreateFilm(idTmdb);

            if (film == null) {
                redirectError(response, request, idTmdbStr, titoloInput, "Film non trovato o ID non valido.");
                return;
            }

            Recensione recensione = new Recensione();
            recensione.setIdUtente(utente.getIdUtente());
            recensione.setIdFilm(film.getIdFilm());
            recensione.setRating(rating);
            recensione.setText(text);
            recensione.setDate(Date.valueOf(LocalDate.now()));


            boolean successo = facade.salvaRecensione(recensione);

            if (successo) {
                String encodedTitle = URLEncoder.encode(film.getTitolo(), StandardCharsets.UTF_8.name());
                response.sendRedirect(request.getContextPath() + "/jsp/Recensione.jsp?idTmdb=" + idTmdb + "&titolo=" + encodedTitle + "&esito=success");
            } else {
                // Se restituisce false, l'utente ha già recensito questo film
                redirectError(response, request, idTmdbStr, titoloInput, "Hai già recensito questo film!");
            }

        } catch (NumberFormatException e) {
            redirectError(response, request, idTmdbStr, titoloInput, "Errore nel formato dei dati.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectError(response, request, idTmdbStr, titoloInput, "Errore di sistema: " + e.getMessage());
        }
    }


    private void redirectError(HttpServletResponse response, HttpServletRequest request, String idTmdb, String titolo, String msg) throws IOException {
        String encodedTitle = (titolo != null) ? URLEncoder.encode(titolo, StandardCharsets.UTF_8.name()) : "Sconosciuto";
        String encodedMsg = URLEncoder.encode(msg, StandardCharsets.UTF_8.name());

        response.sendRedirect(request.getContextPath() +
                "/jsp/Recensione.jsp?idTmdb=" + idTmdb +
                "&titolo=" + encodedTitle +
                "&esito=errore" +
                "&msg=" + encodedMsg);
    }
}