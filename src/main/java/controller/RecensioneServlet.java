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
import java.sql.SQLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/RecensioneServlet")
public class RecensioneServlet extends HttpServlet {

    private FilmDAO filmDAO = new FilmDAO();
    private RecensioneDAO recensioneDAO = new RecensioneDAO();
    private WatchlistItemDAO watchlistDAO = new WatchlistItemDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");

        // 1. Controllo Login
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


            String titoloEncoded = URLEncoder.encode(titolo, StandardCharsets.UTF_8);


            Film film = filmDAO.findOrCreate(idTmdb, titolo);


            Recensione esistente = recensioneDAO.doRetrieveByUtenteAndFilm(utente.getIdUtente(), film.getIdFilm());

            if (esistente != null) {
                // ERRORE: Recensione già presente -> NotificaErroreResponse
                String msg = "Hai già scritto una recensione per questo film!";
                response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdb + "&titolo=" + titoloEncoded + "&esito=errore&msg=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
                return;
            }

            // Creazione Recensione
            Date dataOdierna = new Date(System.currentTimeMillis());
            Recensione recensione = new Recensione(rating, text, dataOdierna, utente.getIdUtente(), film.getIdFilm());

            // Salvataggio
            recensioneDAO.doSave(recensione);

            // Aggiornamento Watchlist (segna come "Visto")
            watchlistDAO.markAsWatched(utente.getIdUtente(), film.getIdFilm());


            response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdb + "&titolo=" + titoloEncoded + "&esito=success");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect("index.jsp?error=invalid_params");
        } catch (Exception e) {
            e.printStackTrace();
            // Gestione errore generico
            String titoloEncoded = (titolo != null) ? URLEncoder.encode(titolo, StandardCharsets.UTF_8) : "";
            String msg = "Errore durante il salvataggio della recensione.";
            response.sendRedirect("jsp/Recensione.jsp?idTmdb=" + idTmdbStr + "&titolo=" + titoloEncoded + "&esito=errore&msg=" + URLEncoder.encode(msg, StandardCharsets.UTF_8));
        }
    }
}