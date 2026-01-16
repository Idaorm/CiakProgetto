<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.WatchlistItem, service.TmdbMovie" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <link rel="icon" type="image/png" href="images/ciak (1).svg">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta charset="UTF-8">
    <title>Ciak! - La mia Watchlist</title>
    <style>
        :root {
            --bg-color: #0b0e11;
            --card-bg: #151a23;
            --text-color: #ffffff;
            --text-muted: #8b92a8;
            --accent-gradient: linear-gradient(90deg, #f093fb, #f5576c);
            --border-color: #2a3241;
        }

        body {
            background-color: var(--bg-color);
            color: var(--text-color);
            font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            margin: 0;
            padding: 40px;
        }

        h1 { font-size: 2.5rem; margin-bottom: 10px; }
        .subtitle { color: var(--text-muted); margin-bottom: 30px; }

        /* STATISTICHE */
        .stats-container {
            display: flex;
            gap: 20px;
            margin-bottom: 40px;
        }
        .stat-card {
            background: var(--card-bg);
            padding: 20px;
            border-radius: 12px;
            flex: 1;
            border: 1px solid var(--border-color);
            text-align: center;
        }
        .stat-card h2 {
            margin: 0;
            font-size: 2rem;
            background: var(--accent-gradient);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .stat-card p { margin: 5px 0 0; color: var(--text-muted); text-transform: uppercase; font-size: 0.8rem; }

        /* FILTRI */
        .filter-bar { margin-bottom: 30px; }
        .filter-btn {
            background: var(--card-bg);
            color: var(--text-muted);
            border: 1px solid var(--border-color);
            padding: 10px 25px;
            border-radius: 25px;
            cursor: pointer;
            margin-right: 10px;
            font-weight: 600;
            transition: 0.3s;
        }
        .filter-btn.active {
            background: var(--accent-gradient);
            color: white;
            border: none;
        }

        /* GRID DEI FILM */
        .grid-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 25px;
        }
        .card {
            background: var(--card-bg);
            border-radius: 16px;
            overflow: hidden;
            border: 1px solid var(--border-color);
            transition: transform 0.3s;
            display: flex; flex-direction: column;
        }
        .card:hover { transform: translateY(-5px); }
        .card img { width: 100%; height: 300px; object-fit: cover; background: #2a3241; }

        .card-body { padding: 15px; flex-grow: 1; display: flex; flex-direction: column; }
        .card-title { font-weight: bold; font-size: 1rem; margin-bottom: 15px; height: 40px; overflow: hidden; }

        .actions { display: flex; gap: 8px; margin-top: auto; }
        .btn-status {
            background: #0b0e11;
            border: 1px solid var(--border-color);
            color: white;
            padding: 8px;
            border-radius: 8px;
            flex-grow: 1;
            text-decoration: none;
            text-align: center;
            font-size: 0.85rem;
        }
        .btn-remove {
            background: rgba(245, 87, 108, 0.1);
            border: 1px solid #f5576c;
            color: #f5576c;
            padding: 8px 12px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: bold;
        }
    </style>
</head>
<body>

<h1>La mia watchlist</h1>
<p class="subtitle">I tuoi film salvati dal catalogo</p>

<%
    List<WatchlistItem> items = (List<WatchlistItem>) request.getAttribute("items");
    List<TmdbMovie> movies = (List<TmdbMovie>) request.getAttribute("moviesApi");

    int totali = (items != null) ? items.size() : 0;
    long visti = (items != null) ? items.stream().filter(WatchlistItem::isStatus).count() : 0;
    long daVedere = totali - visti;
%>

<div class="stats-container">
    <div class="stat-card"><h2><%= totali %></h2><p>Salvati</p></div>
    <div class="stat-card"><h2><%= visti %></h2><p>Visti</p></div>
    <div class="stat-card"><h2><%= daVedere %></h2><p>Da vedere</p></div>
</div>

<div class="filter-bar">
    <button class="filter-btn active" onclick="applyFilter('all', this)">Tutti</button>
    <button class="filter-btn" onclick="applyFilter('da-vedere', this)">Da vedere</button>
    <button class="filter-btn" onclick="applyFilter('visto', this)">Visto</button>
</div>

<div class="grid-container" id="watchlistGrid">
    <%
        // MODIFICA: Controllo che entrambe le liste esistano e non siano vuote
        if (items != null && movies != null && !items.isEmpty()) {
            // Usiamo Math.min per evitare IndexOutOfBoundsException se le liste hanno lunghezze diverse
            int displaySize = Math.min(items.size(), movies.size());

            for (int i = 0; i < displaySize; i++) {
                WatchlistItem it = items.get(i);
                TmdbMovie m = movies.get(i);
                String statusClass = it.isStatus() ? "visto" : "da-vedere";

                // Gestione immagine mancante
                String posterUrl = (m.poster_path != null && !m.poster_path.isEmpty())
                        ? "https://image.tmdb.org/t/p/w500" + m.poster_path
                        : "images/placeholder.jpg"; // Assicurati di avere un'immagine di default
    %>
    <div class="card movie-card <%= statusClass %>">
        <img src="<%= posterUrl %>" alt="<%= m.title %>">
        <div class="card-body">
            <div class="card-title"><%= (m.title != null) ? m.title : "Titolo non disponibile" %></div>
            <div class="actions">
                <a href="WatchlistServlet?action=toggle&idItem=<%= it.getIdItem() %>&status=<%= it.isStatus() %>" class="btn-status">
                    <%= it.isStatus() ? "✓ Visto" : "○ Da vedere" %>
                </a>
                <a href="WatchlistServlet?action=remove&idItem=<%= it.getIdItem() %>" class="btn-remove" title="Rimuovi">✕</a>
            </div>
        </div>
    </div>
    <%
        }
    } else {
    %>
    <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: var(--text-muted);">
        <p>La tua watchlist è vuota. Aggiungi qualche film dal catalogo!</p>
    </div>
    <% } %>
</div>

<script>
    function applyFilter(filterType, btn) {
        document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');

        const cards = document.querySelectorAll('.movie-card');
        cards.forEach(card => {
            if (filterType === 'all') {
                card.style.display = 'flex';
            } else {
                card.style.display = card.classList.contains(filterType) ? 'flex' : 'none';
            }
        });
    }
</script>
</body>
</html>