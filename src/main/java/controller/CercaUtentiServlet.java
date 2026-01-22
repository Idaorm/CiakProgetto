package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import model.UtenteRegistrato;
import service.Facade;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CercaUtentiServlet", value = "/CercaUtentiServlet")
public class CercaUtentiServlet extends HttpServlet {

    private Facade facade;

    @Override
    public void init() throws ServletException {
        super.init();
        facade = new Facade();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

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
