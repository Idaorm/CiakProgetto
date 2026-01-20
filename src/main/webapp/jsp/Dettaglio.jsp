<%@ page import="service.TmdbMovie" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="model.Recensione" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="/jsp/Header.jsp" />

<%

    TmdbMovie f = (TmdbMovie) request.getAttribute("filmDettaglio");

    LinkedHashMap<Recensione, String> recensioniMap = (LinkedHashMap<Recensione, String>) request.getAttribute("recensioniMap");

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
    <link rel="icon" type="image/png" href="images/ciak (1).svg">
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

        /* --- STILE RECENSIONI --- */
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

        .avatar-circle {
            width: 45px;
            height: 45px;
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            color: #000;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 900;
            font-size: 20px;
            text-transform: uppercase;
            border: 2px solid rgba(255, 255, 255, 0.2);
            flex-shrink: 0;
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
                <a href="WatchlistServlet?action=add&idTmdb=<%= f.id %>&titolo=<%= encodedTitle %>" class="btn-gradient">
                    + Aggiungi alla lista
                </a>

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
                for (Map.Entry<Recensione, String> entry : recensioniMap.entrySet()) {
                    Recensione r = entry.getKey();
                    // String inizialeEmail = entry.getValue(); // Non ci serve più
            %>

            <div class="review-card">
                <div style="flex: 1;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">

                        <div style="color: #ffd700; font-size: 14px;">
                            <% for(int i=0; i<r.getRating(); i++) { %>★<% } %>
                            <% for(int i=r.getRating(); i<5; i++) { %>☆<% } %>
                        </div>

                        <span style="color: #666; font-size: 12px;">
                            <%= r.getDate() %>
                        </span>
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