package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.UtenteRegistrato;
import service.Facade;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private Facade facade;

    @Override
    public void init() throws ServletException {
        super.init();
        facade = new Facade();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/Login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("errore", "Email e password sono obbligatorie.");
            request.getRequestDispatcher("/jsp/Login.jsp").forward(request, response);
            return;
        }

        try {
            UtenteRegistrato utente = facade.login(email, password);

            if (utente != null) {
                HttpSession session = request.getSession();
                session.setAttribute("utente", utente);
                response.sendRedirect(request.getContextPath() + "/CatalogoServlet");
            } else {
                request.setAttribute("errore", "Email o password non valide.");
                request.getRequestDispatcher("/jsp/Login.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore interno. Riprova più tardi.");
            request.getRequestDispatcher("/jsp/Login.jsp").forward(request, response);
        }
    }
}
