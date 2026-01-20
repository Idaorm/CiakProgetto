package controller;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.DAO.UtenteRegistratoDAO;
import model.UtenteRegistrato;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

@WebServlet(name = "ModificaAccountServlet", value = "/ModificaAccountServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1MB
        maxFileSize = 1024 * 1024 * 5,         // 5MB
        maxRequestSize = 1024 * 1024 * 10      // 10MB
)
public class ModificaAccountServlet extends HttpServlet {

    private UtenteRegistratoDAO utenteDAO;

    @Override
    public void init() throws ServletException {
        utenteDAO = new UtenteRegistratoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }
        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");
        request.setAttribute("utente", utente);
        request.getRequestDispatcher("/jsp/ModificaAccount.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");

        // Parametri
        String username = request.getParameter("username");
        String bio = request.getParameter("bio");
        boolean watchlistVisibility = request.getParameter("watchlistVisibility") != null;
        boolean removePhoto = request.getParameter("removePhoto") != null;

        // Validazione base
        if (username == null || username.isBlank()) {
            request.setAttribute("errore", "Username non valido");
            request.setAttribute("utente", utente);
            request.getRequestDispatcher("/jsp/ModificaAccount.jsp").forward(request, response);
            return;
        }

        // Upload immagine
        Part photoPart = request.getPart("photo");
        String photoFileName = utente.getPhoto(); // default = foto attuale

        String uploadPath = getServletContext().getRealPath("/images/profilo");

        // Se l'utente ha chiesto di rimuovere la foto
        if (removePhoto) {

            if (photoFileName != null) {
                File oldFile = new File(uploadPath, photoFileName);
                if (oldFile.exists()) {
                    oldFile.delete(); // elimina file fisico
                }
            }

            photoFileName = null; // rimuove dal DB
        }

        // Se carica una nuova immagine (HA PRIORITÀ)
        if (photoPart != null && photoPart.getSize() > 0) {

            String submittedFileName =
                    Paths.get(photoPart.getSubmittedFileName()).getFileName().toString();

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String newFileName =
                    "user_" + utente.getIdUtente() + "_" + System.currentTimeMillis()
                            + submittedFileName.substring(submittedFileName.lastIndexOf("."));

            photoPart.write(uploadPath + File.separator + newFileName);
            photoFileName = newFileName;
        }

        // aggiorna oggetto
        utente.setUsername(username);
        utente.setBio(bio);
        utente.setWatchlistVisibility(watchlistVisibility);
        utente.setPhoto(photoFileName);

        try {
            utenteDAO.updateUtente(utente);

            // aggiorna sessione
            session.setAttribute("utente", utente);

            response.sendRedirect(request.getContextPath() + "/AccountUtenteServlet");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore durante il salvataggio. Riprova più tardi");
            request.setAttribute("utente", utente);
            request.getRequestDispatcher("/jsp/ModificaAccount.jsp").forward(request, response);
        }
    }
    
}