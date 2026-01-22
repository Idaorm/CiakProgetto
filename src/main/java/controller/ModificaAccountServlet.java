package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import model.UtenteRegistrato;
import controller.service.Facade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

@WebServlet(name = "ModificaAccountServlet", value = "/ModificaAccountServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class ModificaAccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Facade facade;
        try {
            facade = new Facade();
        } catch (Exception e) {
            request.setAttribute("errore", "Il servizio non è disponibile. Riprova più tardi.");
            request.getRequestDispatcher("/jsp/ModificaAccount.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");

        // controllo se l'utente vuole eliminare l'account
        if (request.getParameter("deleteAccount") != null) {
            try {
                facade.eliminaUtente(utente.getIdUtente());
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/CatalogoServlet");
                return;
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("errore", "Errore durante l'eliminazione dell'account. Riprova più tardi");
                request.setAttribute("utente", utente);
                request.getRequestDispatcher("/jsp/ModificaAccount.jsp").forward(request, response);
                return;
            }
        }

        // aggiornamento account
        String username = request.getParameter("username");
        String bio = request.getParameter("bio");
        boolean watchlistVisibility = request.getParameter("watchlistVisibility") != null;
        boolean removePhoto = request.getParameter("removePhoto") != null;

        if (username == null || username.isBlank()) {
            request.setAttribute("errore", "Username non valido");
            request.setAttribute("utente", utente);
            request.getRequestDispatcher("/jsp/ModificaAccount.jsp").forward(request, response);
            return;
        }

        // upload immagine
        Part photoPart = request.getPart("photo");
        String photoFileName = utente.getPhoto();
        String uploadPath = getServletContext().getRealPath("/images/profilo");

        if (removePhoto) {
            if (photoFileName != null) {
                File oldFile = new File(uploadPath, photoFileName);
                if (oldFile.exists()) oldFile.delete();
            }
            photoFileName = null;
        }

        if (photoPart != null && photoPart.getSize() > 0) {
            String submittedFileName = Paths.get(photoPart.getSubmittedFileName()).getFileName().toString();
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String newFileName = "user_" + utente.getIdUtente() + "_" + System.currentTimeMillis()
                    + submittedFileName.substring(submittedFileName.lastIndexOf("."));
            photoPart.write(uploadPath + File.separator + newFileName);
            photoFileName = newFileName;
        }

        utente.setUsername(username);
        utente.setBio(bio);
        utente.setWatchlistVisibility(watchlistVisibility);
        utente.setPhoto(photoFileName);

        try {
            facade.aggiornaUtente(utente);
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
