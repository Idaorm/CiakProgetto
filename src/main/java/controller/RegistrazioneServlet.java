package controller;

import model.DAO.UtenteRegistratoDAO;
import model.UtenteRegistrato;
import org.mindrot.jbcrypt.BCrypt;
import util.DBConnection;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/RegistrazioneServlet")
public class RegistrazioneServlet extends HttpServlet {

    private UtenteRegistratoDAO utenteDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        utenteDAO = new UtenteRegistratoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/Registrazione.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password").trim();
        String confermaPassword = request.getParameter("confermaPassword").trim();

        // validazioni lato server
        List<String> errori = new ArrayList<>();
        if (email == null || email.isBlank() ) {
            errori.add("Il campo 'Email' è obbligatorio");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") ) {
            errori.add("Email non valida");
        }

        if (password.isBlank() || password.length() < 8) {
            errori.add("La password deve contenere almeno 8 caratteri.");
        }

        if (confermaPassword.isBlank() || !password.equals(confermaPassword)) {
            errori.add("Le password non corrispondono.");
        }

        // rimetto i valori inseriti
        request.setAttribute("email", email);

        if (!errori.isEmpty()) {
            request.setAttribute("errori", errori);
            request.getRequestDispatcher("/jsp/Registrazione.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            if (utenteDAO.emailGiaRegistrata(email)) {
                request.setAttribute("errore", "L'email inserita risulta già registrata.");
                request.getRequestDispatcher("/jsp/Registrazione.jsp").forward(request, response);
                return;
            }

            // genera salt casuale
            String salt = BCrypt.gensalt();
            // calcola l’hash della password fornita dall’utente, combinandola con il salt
            String passwordHash = BCrypt.hashpw(password, salt);

            UtenteRegistrato nuovoUtente = new UtenteRegistrato();
            nuovoUtente.setUsername(email);
            nuovoUtente.setEmail(email);
            nuovoUtente.setPassword(passwordHash);

            if (utenteDAO.insertUtente(nuovoUtente)) {
                request.setAttribute("successo",true);
                request.getRequestDispatcher("/jsp/Login.jsp").forward(request, response);
            } else {
                request.setAttribute("errore", "Errore nella registrazione. Riprova.");
                request.getRequestDispatcher("/jsp/Registrazione.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
