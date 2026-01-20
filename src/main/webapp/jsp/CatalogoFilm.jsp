<%@ page import="java.util.List" %>
<%@ page import="service.TmdbMovie" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/jsp/Header.jsp" />
<!DOCTYPE html>
<html lang="it">
<head>
    <link rel="icon" type="image/png" href="images/ciak (1).svg">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Catalogo</title>
    <style>
        *, *::before, *::after {
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background-color: #0b0e11;
            color: #e4e6eb;
            margin: 0;
            padding: 20px ;
        }

        .header-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 40px;
            padding-bottom: 20px;
            border-bottom: 1px solid #2a3241;
        }

        .title-group h1 {
            margin: 0;
            font-size: 2.5rem;
            text-transform: uppercase;
            letter-spacing: 2px;
            background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .search-form {
            display: block;
        }

        .search-input {
            background-color: #151a23;
            border: 1px solid #2a3241;
            border-radius: 50px;
            padding: 12px 24px;
            color: white;
            font-size: 1rem;
            width: 350px;
            outline: none;
            transition: all 0.3s ease;
            text-align: center;
        }

        .search-input::placeholder {
            color: #5a6b8c;
            font-style: italic;
        }

        .search-input:focus {
            border-color: #f093fb;
            box-shadow: 0 0 20px rgba(240, 147, 251, 0.2);
            transform: scale(1.02);
        }

        .grid-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 30px;
        }

        .card {
            background-color: #151a23;
            border: 1px solid #2a3241;
            border-radius: 16px;
            overflow: hidden;
            transition: all 0.3s ease;
            display: flex;
            flex-direction: column;
            position: relative;
        }

        .card:hover {
            transform: translateY(-10px);
            border-color: #f5576c;
            box-shadow: 0 10px 30px rgba(245, 87, 108, 0.15);
        }

        .card-poster {
            width: 100%;
            height: 330px;
            overflow: hidden;
            position: relative;
            background-color: #000;
        }

        .card-poster img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.5s ease;
            display: block;
        }

        .card:hover .card-poster img {
            transform: scale(1.05);
        }

        .no-poster {
            width: 100%;
            height: 100%;
            background-color: #1f2533;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
            color: #8b92a8;
            padding: 20px;
        }

        .card-body {
            padding: 20px;
            flex-grow: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }

        .card-title {
            font-size: 1.1rem;
            font-weight: 700;
            margin-bottom: 8px;
            color: #fff;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .card-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            font-size: 0.9rem;
            color: #8b92a8;
        }

        .rating-badge {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            padding: 4px 10px;
            border-radius: 12px;
            font-weight: 700;
            font-size: 0.8rem;
        }

        .btn-add {
            display: block;
            background-color: transparent;
            border: 1px solid #f093fb;
            color: #f093fb;
            padding: 10px;
            border-radius: 10px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s;
            text-align: center;
            text-decoration: none;
        }

        .btn-add:hover {
            background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
            border-color: transparent;
            color: white;
            box-shadow: 0 0 15px rgba(240, 147, 251, 0.4);
        }

        @media (max-width: 768px) {
            .header-row {
                flex-direction: column;
                gap: 20px;
                text-align: center;
            }
            .search-input { width: 100%; }
        }
    </style>
</head>
<body>

<div class="header-row">
    <div class="title-group">
        <h1>I titoli del momento</h1>
    </div>

    <form action="CatalogoServlet" method="GET" class="search-form">
        <input type="text" name="q" class="search-input" placeholder="Cerca film...">
    </form>

    <form action="CercaUtentiServlet" method="GET" class="search-form">
        <input type="text" name="q" class="search-input" placeholder="Cerca utenti...">
    </form>
</div>

<div class="grid-container">
    <%
        List<TmdbMovie> films = (List<TmdbMovie>) request.getAttribute("filmPopolari");

        if (films != null && !films.isEmpty()) {
            for (TmdbMovie f : films) {
                // Calcolo Anno
                String year = (f.release_date != null && f.release_date.length() >= 4)
                        ? f.release_date.substring(0, 4)
                        : "N/A";

                // Controllo Immagine
                boolean hasImage = (f.poster_path != null && !f.poster_path.isEmpty());
                String imgUrl = hasImage ? "https://image.tmdb.org/t/p/w500" + f.poster_path : "";
    %>
    <div class="card">

        <a href="DettaglioServlet?id=<%= f.id %>" class="card-poster" style="display: block; text-decoration: none;">
            <% if (hasImage) { %>
            <img src="<%= imgUrl %>" alt="<%= f.title %>">
            <% } else { %>
            <div class="no-poster">
                Immagine non<br>disponibile
            </div>
            <% } %>
        </a>

        <div class="card-body">
            <div class="card-title" title="<%= f.title %>"><%= f.title %></div>

            <div class="card-meta">
                <span><%= year %></span>
                <span class="rating-badge">★ <%= f.vote_average %></span>
            </div>

            <a href="WatchlistServlet?action=add&idTmdb=<%= f.id %>&titolo=<%= java.net.URLEncoder.encode(f.title, "UTF-8") %>" class="btn-add">+ Aggiungi</a>
        </div>
    </div>
    <%
        }
    } else {
    %>
    <div style="grid-column: 1/-1; text-align: center; color: #8b92a8; padding: 50px;">
        <h2>Nessun film trovato.</h2>
        <p>Prova a cercare qualcos'altro.</p>
    </div>
    <%
        }
    %>
</div>

</body>
</html>