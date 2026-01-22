package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import service.Facade;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/RegistrazioneServlet")
public class RegistrazioneServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/Registrazione.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Facade facade;
        try {
            facade = new Facade();
        } catch (Exception e) {
            request.setAttribute("errore", "Il servizio non è disponibile. Riprova più tardi.");
            request.getRequestDispatcher("/jsp/Registrazione.jsp").forward(request, response);
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password").trim();
        String confermaPassword = request.getParameter("confermaPassword").trim();

        List<String> errori = new ArrayList<>();

        // validazioni
        if (email == null || email.isBlank()) {
            errori.add("Il campo 'Email' è obbligatorio");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errori.add("Email non valida");
        }

        if (password.isBlank() || password.length() < 8) {
            errori.add("La password deve contenere almeno 8 caratteri.");
        }

        if (confermaPassword.isBlank() || !password.equals(confermaPassword)) {
            errori.add("Le password non corrispondono.");
        }

        request.setAttribute("email", email);

        if (!errori.isEmpty()) {
            request.setAttribute("errori", errori);
            request.getRequestDispatcher("/jsp/Registrazione.jsp").forward(request, response);
            return;
        }

        try {
            boolean registrato = facade.registraUtente(email, password);

            if (registrato) {
                request.setAttribute("successo", true);
                request.getRequestDispatcher("/jsp/Login.jsp").forward(request, response);
            } else {
                request.setAttribute("errore", "L'email inserita risulta già registrata.");
                request.getRequestDispatcher("/jsp/Registrazione.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore interno. Riprova più tardi.");
            request.getRequestDispatcher("/jsp/Registrazione.jsp").forward(request, response);
        }
    }
}
