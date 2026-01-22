<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.UtenteRegistrato" %>
<%
    // Controllo Sessione
    UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");
    if (utente == null) {
        response.sendRedirect(request.getContextPath() + "/jsp/Login.jsp");
        return;
    }

    String idTmdb = request.getParameter("idTmdb");
    String titolo = request.getParameter("titolo");

    // Recuperiamo i parametri di esito dalla Servlet
    String esito = request.getParameter("esito");
    String msgErrore = request.getParameter("msg");

    if (idTmdb == null || titolo == null) {
        response.sendRedirect(request.getContextPath() + "/CatalogoServlet" +
                "");
        return;
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/ciak.svg">
    <meta charset="UTF-8">
    <title>Recensisci <%= titolo %></title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;900&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            padding: 0;
            background-color: #0a0e14;
            font-family: 'Inter', sans-serif;
            color: #e4e6eb;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            padding: 20px;
        }

        .review-container {
            max-width: 600px;
            margin: 60px auto;
            padding: 40px;
            background: rgba(22, 27, 34, 0.8);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 16px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
            backdrop-filter: blur(10px);
        }

        h2 {
            margin-top: 0;
            font-size: 28px;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            text-align: center;
        }

        .subtitle {
            text-align: center;
            color: #a0a0a0;
            margin-bottom: 30px;
            font-size: 16px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #fff;
        }

        .custom-input, .custom-select {
            width: 100%;
            padding: 12px 15px;
            border-radius: 8px;
            background: #0d1117;
            border: 1px solid #30363d;
            color: white;
            font-family: inherit;
            font-size: 15px;
            box-sizing: border-box;
            transition: border-color 0.2s;
        }

        .custom-input:focus, .custom-select:focus {
            outline: none;
            border-color: #f5576c;
        }

        .btn-submit {
            width: 100%;
            padding: 14px;
            border-radius: 8px;
            border: none;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            font-size: 16px;
            font-weight: 700;
            cursor: pointer;
            transition: transform 0.2s, opacity 0.2s;
            margin-top: 10px;
        }

        .btn-submit:hover {
            opacity: 0.9;
            transform: translateY(-2px);
        }

        .btn-cancel {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #a0a0a0;
            text-decoration: none;
            font-size: 14px;
        }
        .btn-cancel:hover {
            color: white;
        }


        .msg-box {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
            font-weight: 600;
        }

        .msg-success {
            background-color: rgba(46, 160, 67, 0.2);
            border: 1px solid #2ea043;
            color: #7ee787;
        }

        .msg-error {
            background-color: rgba(218, 54, 51, 0.2);
            border: 1px solid #da3633;
            color: #f85149;
        }

        .link-return {
            color: white;
            text-decoration: underline;
            margin-left: 10px;
        }
    </style>
</head>
<body>

<jsp:include page="/jsp/Header.jsp" />

<div class="review-container">
    <h2>La tua opinione conta</h2>
    <p class="subtitle">Stai recensendo: <strong><%= titolo %></strong></p>

    <% if ("success".equals(esito)) { %>
    <div class="msg-box msg-success">
        Recensione pubblicata con successo! ✅
        <br>
        <a href="${pageContext.request.contextPath}/DettaglioServlet?id=movie&idTmdb=<%= idTmdb %>" class="link-return">Torna alla scheda del film</a>
    </div>
    <% } else if ("errore".equals(esito)) { %>
    <div class="msg-box msg-error">
        ⚠️ <%= (msgErrore != null) ? msgErrore : "Errore durante il salvataggio." %>
    </div>
    <% } %>
    <form action="${pageContext.request.contextPath}/RecensioneServlet" method="post">

        <input type="hidden" name="idTmdb" value="<%= idTmdb %>">
        <input type="hidden" name="titolo" value="<%= titolo %>">

        <div class="form-group">
            <label for="rating">Voto (da 1 a 5)</label>
            <select name="rating" id="rating" class="custom-select" required>
                <option value="" disabled selected>Seleziona un voto...</option>
                <option value="5">⭐⭐⭐⭐⭐ - Capolavoro</option>
                <option value="4">⭐⭐⭐⭐ - Ottimo</option>
                <option value="3">⭐⭐⭐ - Piacevole</option>
                <option value="2">⭐⭐ - Non mi ha convinto</option>
                <option value="1">⭐ - Da evitare</option>
            </select>
        </div>

        <div class="form-group">
            <label for="text">La tua recensione</label>
            <textarea name="text" id="text" rows="5" class="custom-input"
                      placeholder="Cosa ti è piaciuto? Cosa cambieresti? Scrivi qui..."
                      maxlength="200" required></textarea>
            <div style="text-align: right; font-size: 12px; color: #666; margin-top: 5px;">Max 200 caratteri</div>
        </div>

        <button type="submit" class="btn-submit">Pubblica Recensione</button>

        <a href="javascript:history.back()" class="btn-cancel">Annulla e torna indietro</a>
    </form>
</div>

</body>
</html>