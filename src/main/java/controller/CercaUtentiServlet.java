package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.DAO.UtenteRegistratoDAO;
import model.UtenteRegistrato;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CercaUtentiServlet", value = "/CercaUtentiServlet")
public class CercaUtentiServlet extends HttpServlet {

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

        UtenteRegistratoDAO utenteDAO = new UtenteRegistratoDAO();
        List<UtenteRegistrato> utenti = utenteDAO.cercaUtenti(query.trim());

        request.setAttribute("utenti", utenti);

        if (utenti.isEmpty()) {
            request.setAttribute("messaggio", "Nessun utente trovato");
        }

        request.getRequestDispatcher("/jsp/RisultatiCercaUtenti.jsp")
                .forward(request, response);
    }
}
