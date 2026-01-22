package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import model.UtenteRegistrato;

import service.Facade;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "AccountUtenteServlet", value = "/AccountUtenteServlet")
public class AccountUtenteServlet extends HttpServlet {

    private Facade facade;

    @Override
    public void init() throws ServletException {
        super.init();
        this.facade = new Facade();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UtenteRegistrato utenteDaVisualizzare = null;

        // se esiste parametro id, cerchiamo l'utente nel DB
        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int idUtente = Integer.parseInt(idParam);
                Map<String, Object> datiProfilo = facade.getDatiProfiloCompleti(idUtente);
                if (datiProfilo.containsKey("errore")) {
                    request.setAttribute("errore", datiProfilo.get("errore"));
                    request.getRequestDispatcher("/jsp/RisultatiCercaUtenti.jsp").forward(request, response);
                    return;
                }
                utenteDaVisualizzare = (UtenteRegistrato) datiProfilo.get("utente");
                request.setAttribute("watchlistCount", datiProfilo.get("watchlistCount"));
                request.setAttribute("recensioniCount", datiProfilo.get("recensioniCount"));
                request.setAttribute("watchlist", datiProfilo.get("watchlist"));
                request.setAttribute("moviesApi", datiProfilo.get("moviesApi"));
                request.setAttribute("recensioniMap", datiProfilo.get("recensioniMap"));
            } catch (NumberFormatException e) {
                request.setAttribute("errore", "ID utente non valido.");
                request.getRequestDispatcher("/jsp/RisultatiCercaUtenti.jsp").forward(request, response);
                return;
            }
        } else {
            // fallback al profilo dell'utente loggato
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("utente") == null) {
                response.sendRedirect(request.getContextPath() + "/jsp/Login.jsp");
                return;
            }
            utenteDaVisualizzare = (UtenteRegistrato) session.getAttribute("utente");

            Map<String, Object> datiProfilo = facade.getDatiProfiloCompleti(utenteDaVisualizzare.getIdUtente());
            if (datiProfilo.containsKey("errore")) {
                request.setAttribute("errore", datiProfilo.get("errore"));
            } else {
                request.setAttribute("watchlistCount", datiProfilo.get("watchlistCount"));
                request.setAttribute("recensioniCount", datiProfilo.get("recensioniCount"));
                request.setAttribute("watchlist", datiProfilo.get("watchlist"));
                request.setAttribute("moviesApi", datiProfilo.get("moviesApi"));
                request.setAttribute("recensioniMap", datiProfilo.get("recensioniMap"));
            }
        }

        request.setAttribute("utente", utenteDaVisualizzare);
        request.getRequestDispatcher("/jsp/AccountUtente.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}


