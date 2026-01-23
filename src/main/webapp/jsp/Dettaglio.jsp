<%@ page import="controller.service.TmdbMovie" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="model.Recensione" %>
<%@ page import="model.UtenteRegistrato" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    TmdbMovie f = (TmdbMovie) request.getAttribute("filmDettaglio");
    Boolean giaInWatchlist = (Boolean) request.getAttribute("giaInWatchlist");
    if (giaInWatchlist == null) giaInWatchlist = false;

    // NOTA: Ora recuperiamo una Mappa <Recensione, UtenteRegistrato>
    LinkedHashMap<Recensione, UtenteRegistrato> recensioniMap =
            (LinkedHashMap<Recensione, UtenteRegistrato>) request.getAttribute("recensioniMap");

    boolean hasPoster = (f != null && f.poster_path != null && !f.poster_path.isEmpty());
    String posterUrl = hasPoster
            ? "https://image.tmdb.org/t/p/w500" + f.poster_path
            : "https://via.placeholder.com/300x450?text=No+Poster";

    String year = (f != null && f.release_date != null && f.release_date.length() >= 4)
            ? f.release_date.substring(0, 4)
            : "N/A";
    String encodedTitle = (f != null) ? URLEncoder.encode(f.title, "UTF-8") : "";
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/ciak.svg">
    <meta charset="UTF-8">
    <title>Film <%= (f != null) ? f.title : "Dettaglio" %></title>
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #0b0e11;
            color: #ffffff;
            margin: 0;
            padding: 20px;
        }

        .container {
            max-width: 1100px;
            margin: 0 auto;
        }

        .top-section {
            display: flex;
            gap: 50px;
            margin-bottom: 40px;
            align-items: flex-start;
        }

        .poster-box {
            flex-shrink: 0;
            width: 320px;
        }

        .poster-img {
            width: 100%;
            border-radius: 16px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.6);
            border: 1px solid rgba(255, 255, 255, 0.1);
        }

        .info-box {
            flex-grow: 1;
            padding-top: 10px;
        }

        h1 {
            font-size: 3.5rem;
            margin: 0 0 15px 0;
            line-height: 1.1;
            background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .meta-tags {
            display: flex;
            gap: 15px;
            margin-bottom: 25px;
            font-size: 1rem;
            color: #aeb4be;
            align-items: center;
        }

        .tag {
            background-color: #1f232b;
            padding: 5px 12px;
            border-radius: 20px;
            font-weight: 600;
            border: 1px solid #2a3241;
            font-size: 0.85rem;
        }

        .stars-rating {
            color: #f5c518;
            font-size: 1.2rem;
            margin-bottom: 30px;
            font-weight: bold;
        }

        .action-buttons {
            display: flex;
            gap: 20px;
            margin-bottom: 35px;
            flex-wrap: wrap;
        }

        .btn-gradient {
            background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
            border: none;
            padding: 14px 30px;
            color: white;
            font-weight: bold;
            border-radius: 12px;
            cursor: pointer;
            text-decoration: none;
            box-shadow: 0 5px 20px rgba(245, 87, 108, 0.3);
            transition: transform 0.2s;
            display: inline-block;
        }

        .btn-review {
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid #f093fb;
            padding: 14px 30px;
            color: #f093fb;
            font-weight: bold;
            border-radius: 12px;
            cursor: pointer;
            text-decoration: none;
            transition: 0.3s;
            display: inline-block;
        }

        .btn-outline {
            background: transparent;
            border: 1px solid #5a6b8c;
            padding: 14px 30px;
            color: #aeb4be;
            font-weight: bold;
            border-radius: 12px;
            cursor: pointer;
            text-decoration: none;
            transition: 0.2s;
            display: inline-block;
        }

        .btn-gradient:hover,
        .btn-review:hover,
        .btn-outline:hover {
            transform: translateY(-3px);
            color: white;
        }

        .btn-review:hover {
            background-color: #f093fb;
            color: white;
            box-shadow: 0 0 15px rgba(240, 147, 251, 0.4);
        }

        .btn-added-state {
            display: block;
            background-color: #1f2533;
            border: 1px solid #28a745;
            color: #28a745;
            padding: 10px;
            border-radius: 10px;
            cursor: default;
            font-weight: 600;
            text-align: center;
            text-decoration: none;
        }

        .plot-box {
            background-color: #151a23;
            padding: 30px;
            border-radius: 16px;
            border: 1px solid #2a3241;
            margin-bottom: 40px;
        }

        .plot-box h3 {
            margin-top: 0;
            margin-bottom: 15px;
            color: #fff;
            font-size: 1.5rem;
        }

        .plot-text {
            line-height: 1.7;
            color: #bdc1c6;
            font-size: 1.05rem;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 60px;
        }

        .stat-card {
            background-color: #101216;
            border: 1px solid #2a3241;
            padding: 25px;
            border-radius: 12px;
        }

        .stat-label {
            color: #6b7280;
            font-size: 0.8rem;
            font-weight: 700;
            text-transform: uppercase;
            margin-bottom: 10px;
            letter-spacing: 1px;
        }

        .stat-value {
            color: #fff;
            font-size: 1.3rem;
            font-weight: 600;
        }

        .reviews-section {
            border-top: 1px solid #2a3241;
            padding-top: 40px;
            min-height: 150px;
        }

        .reviews-title {
            font-size: 2rem;
            margin-bottom: 20px;
            color: #fff;
        }

        .review-card {
            background: rgba(255, 255, 255, 0.05);
            padding: 20px;
            border-radius: 12px;
            border: 1px solid rgba(255, 255, 255, 0.1);
            display: flex;
            gap: 20px;
            align-items: flex-start;
        }

        .review-header-link {
            text-decoration: none;
            display: block;
            flex-shrink: 0;
        }

        .review-header-link:hover {
            opacity: 0.8;
        }

        .username-text {
            color: #fff;
            font-weight: bold;
            font-size: 1.1rem;
        }

        .review-content {
            flex: 1;
        }

        .avatar-container {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            overflow: hidden;
            border: 2px solid rgba(255,255,255,0.2);
        }

        .review-avatar-img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .review-avatar-purple {
            width: 100%;
            height: 100%;
            background-color: #8A2BE2;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .no-reviews-msg {
            color: #5a6b8c;
            font-style: italic;
            font-size: 1rem;
        }

        @media (max-width: 768px) {
            .top-section { flex-direction: column; align-items: center; text-align: center; }
            .poster-box { width: 240px; }
            .action-buttons { justify-content: center; flex-direction: column; width: 100%; }
            .btn-gradient, .btn-review, .btn-outline { width: 100%; box-sizing: border-box; text-align: center; }
            h1 { font-size: 2.5rem; }
        }
    </style>
</head>
<body>
<jsp:include page="/jsp/Header.jsp" />

<% if (f != null) { %>

<div class="container">

    <div class="top-section">
        <div class="poster-box">
            <img src="<%= posterUrl %>" class="poster-img" alt="Poster di <%= f.title %>">
        </div>

        <div class="info-box">
            <h1><%= f.title %></h1>

            <div class="meta-tags">
                <span>🗓 <%= year %></span>
                <span class="tag">Film</span>
                <span class="tag">HD</span>
            </div>

            <div class="stars-rating">
                ★ <%= f.vote_average %> / 10
            </div>

            <div class="action-buttons">
                <% if (giaInWatchlist) { %>
                <div class="btn-added-state">
                    <span style="margin-right: 8px;">✓</span> In Watchlist
                </div>
                <% } else { %>
                    <a href="WatchlistServlet?action=add&provenienza=dettaglio&idTmdb=<%= f.id %>&titolo=<%= encodedTitle %>" class="btn-gradient">
                        + Aggiungi alla lista
                    </a>
                    <% } %>

                     <a href="${pageContext.request.contextPath}/jsp/Recensione.jsp?idTmdb=<%= f.id %>&titolo=<%= encodedTitle %>" class="btn-review">
                    Aggiungi recensione
                    </a>

                    <a href="${pageContext.request.contextPath}/CatalogoServlet" class="btn-outline">
                    ← Indietro
                     </a>
            </div>

            <div class="plot-box">
                <h3>Trama</h3>
                <p class="plot-text">
                    <%= (f.overview != null && !f.overview.isEmpty())
                            ? f.overview
                            : "Trama non disponibile in italiano per questo contenuto." %>
                </p>
            </div>
        </div>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Data di Uscita</div>
            <div class="stat-value"><%= f.release_date %></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Valutazione Utenti</div>
            <div class="stat-value"><%= f.vote_average %></div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Tipo</div>
            <div class="stat-value">Film</div>
        </div>

        <div class="stat-card">
            <div class="stat-label">Stato</div>
            <div class="stat-value">Rilasciato</div>
        </div>
    </div>

    <div class="reviews-section">
        <h2 class="reviews-title">
            Cosa dicono gli utenti
            <% if (recensioniMap != null) { %> (<%= recensioniMap.size() %>) <% } %>
        </h2>

        <% if (recensioniMap != null && !recensioniMap.isEmpty()) { %>

        <div style="display: grid; gap: 20px;">
            <%
                // Iteriamo sulla Mappa <Recensione, UtenteRegistrato>
                for (Map.Entry<Recensione, UtenteRegistrato> entry : recensioniMap.entrySet()) {
                    Recensione r = entry.getKey();
                    UtenteRegistrato autore = entry.getValue();
            %>

            <div class="review-card">

                <a href="${pageContext.request.contextPath}/AccountUtenteServlet?id=<%= autore.getIdUtente() %>" class="review-header-link">
                    <div class="avatar-container">
                        <% if (autore.getPhoto() != null && !autore.getPhoto().isEmpty()) { %>
                        <img src="${pageContext.request.contextPath}/images/profilo/<%= autore.getPhoto() %>"
                             alt="<%= autore.getUsername() %>" class="review-avatar-img">
                        <% } else { %>
                        <div class="review-avatar-purple">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="60%" height="60%">
                                <path fill-rule="evenodd" d="M7.5 6a4.5 4.5 0 119 0 4.5 4.5 0 01-9 0zM3.751 20.105a8.25 8.25 0 0116.498 0 .75.75 0 01-.437.695A18.683 18.683 0 0112 22.5c-2.786 0-5.433-.608-7.812-1.7a.75.75 0 01-.437-.695z" clip-rule="evenodd" />
                            </svg>
                        </div>
                        <% } %>
                    </div>
                </a>

                <div class="review-content">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px;">

                        <a href="${pageContext.request.contextPath}/AccountUtenteServlet?id=<%= autore.getIdUtente() %>" style="text-decoration: none;">
                            <span class="username-text"><%= autore.getUsername() %></span>
                        </a>

                        <span style="color: #666; font-size: 12px;">
                            <%= r.getDate() %>
                        </span>
                    </div>

                    <div style="color: #ffd700; font-size: 14px; margin-bottom: 8px;">
                        <% for(int i=0; i<r.getRating(); i++) { %>★<% } %>
                        <% for(int i=r.getRating(); i<5; i++) { %>☆<% } %>
                    </div>

                    <p style="color: #e4e6eb; line-height: 1.5; margin: 0; font-size: 15px;">
                        "<%= r.getText() %>"
                    </p>
                </div>
            </div>

            <% } %>
        </div>

        <% } else { %>
        <p class="no-reviews-msg">Nessuna recensione presente per questo film. Sii il primo a scriverne una!</p>
        <% } %>
    </div>

</div>

<% } else { %>
<div class="container">
    <h1>Film non trovato</h1>
    <a href="index.jsp" class="btn-outline">Torna alla Home</a>
</div>
<% } %>

</body>
</html>