package controller;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.DAO.UtenteRegistratoDAO;
import model.UtenteRegistrato;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AccountUtenteServlet", value = "/AccountUtenteServlet")
public class AccountUtenteServlet extends HttpServlet {

    private UtenteRegistratoDAO utenteDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        utenteDAO = new UtenteRegistratoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recupera l'utente dalla sessione
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            // Se non loggato, rimanda al login
            response.sendRedirect(request.getContextPath() + "/jsp/Login.jsp");
            return;
        }

        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");

        try {
            int watchlistCount = utenteDAO.getWatchlistCount(utente.getIdUtente());
            int recensioniCount = utenteDAO.getRecensioniCount(utente.getIdUtente());

            // Imposta attributi per la JSP
            request.setAttribute("watchlistCount", watchlistCount);
            request.setAttribute("recensioniCount", recensioniCount);

            // Passa l'utente alla JSP (per usare i dati in JSP)
            request.setAttribute("utente", utente);

            request.getRequestDispatcher("/jsp/AccountUtente.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore interno. Riprova più tardi.");
            request.getRequestDispatcher("/jsp/Login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Elabora la richiesta
        doGet(request, response);
    }
    
}