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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("q");

        UtenteRegistratoDAO utenteDAO = new UtenteRegistratoDAO();
        List<UtenteRegistrato> utenti = utenteDAO.cercaUtenti(query);

        request.setAttribute("utenti", utenti);
        request.getRequestDispatcher("/jsp/RisultatiCercaUtenti.jsp").forward(request, response);
    }

}