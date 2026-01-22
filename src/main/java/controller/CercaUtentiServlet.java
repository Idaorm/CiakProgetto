package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import model.UtenteRegistrato;
import controller.service.Facade;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CercaUtentiServlet", value = "/CercaUtentiServlet")
public class CercaUtentiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Facade facade;
        try {
            facade = new Facade();
        } catch (Exception e) {
            request.setAttribute("errore", "Il servizio non è disponibile. Riprova più tardi.");
            request.getRequestDispatcher("/jsp/RisultatiCercaUtenti.jsp").forward(request, response);
            return;
        }

        String query = request.getParameter("utenteDaCercare");

        System.out.println("QUERY UTENTE = [" + query + "]");

        if (query == null || query.trim().isEmpty()) {
            request.setAttribute("utenti", List.of());
            request.setAttribute("messaggio", "Inserisci un testo per la ricerca");
            request.getRequestDispatcher("/jsp/RisultatiCercaUtenti.jsp")
                    .forward(request, response);
            return;
        }

        List<UtenteRegistrato> utenti = facade.cercaUtenti(query.trim());

        request.setAttribute("utenti", utenti);

        if (utenti.isEmpty()) {
            request.setAttribute("messaggio", "Nessun utente trovato");
        }

        request.getRequestDispatcher("/jsp/RisultatiCercaUtenti.jsp")
                .forward(request, response);
    }
}
