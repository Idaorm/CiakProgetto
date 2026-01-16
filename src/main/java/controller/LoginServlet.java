package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.UtenteRegistrato;
import model.DAO.UtenteRegistratoDAO;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UtenteRegistratoDAO utenteDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        utenteDAO = new UtenteRegistratoDAO(); // inizializzazione DAO
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usernameOrEmail = request.getParameter("username");
        String password = request.getParameter("password");

        if (usernameOrEmail == null || password == null) {
            request.setAttribute("error", "Parametri mancanti");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            UtenteRegistrato utente = utenteDAO.login(usernameOrEmail, password);

            if (utente != null) {
                request.getSession().setAttribute("utente", utente);
                response.sendRedirect(request.getContextPath() + "/AccountUtente.jsp");
            } else {
                request.setAttribute("error", "Username/email o password non corretti");
                request.getRequestDispatcher("/Login.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Errore del server. Riprova più tardi.");
            request.getRequestDispatcher("/Login.jsp").forward(request, response);
        }
    }
}

