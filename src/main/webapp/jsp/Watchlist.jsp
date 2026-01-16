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
            --accent-pink: #f5576c; /* Aggiunto per i badge */
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
            padding: 30px 20px; /* Aumentato padding per matchare mockup */
            border-radius: 16px; /* Bordi più tondi */
            flex: 1;
            border: 1px solid var(--border-color);
            text-align: left; /* Allineato a sinistra come nel mockup */
        }
        .stat-card h2 {
            margin: 0;
            font-size: 2.5rem;
            background: var(--accent-gradient);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .stat-card p { margin: 5px 0 0; color: var(--text-muted); font-size: 0.9rem; }

        /* FILTRI */
        .filter-bar { margin-bottom: 30px; }
        .filter-btn {
            background: #1a202c; /* Colore più scuro per i bottoni inattivi */
            color: var(--text-muted);
            border: none;
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
        }

        /* GRID DEI FILM */
        .grid-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 25px;
        }
        .card {
            background: var(--card-bg);
            border-radius: 20px; /* Bordi molto più tondi */
            overflow: hidden;
            border: 1px solid var(--border-color);
            transition: transform 0.3s;
            display: flex;
            flex-direction: column;
        }
        .card:hover { transform: translateY(-5px); }
        .card img { width: 100%; height: 320px; object-fit: cover; }

        .card-body { padding: 20px; flex-grow: 1; display: flex; flex-direction: column; }
        .card-title { font-weight: bold; font-size: 1.1rem; margin-bottom: 5px; height: auto; }

        /* MODIFICA: Nuovi stili per Anno e Badge */
        .card-meta {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 20px;
            font-size: 0.85rem;
            color: var(--text-muted);
        }
        .badge-genre {
            background: var(--accent-pink);
            color: white;
            padding: 2px 10px;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: bold;
        }

        /* BOTTONI AZIONE */
        .actions { display: flex; gap: 10px; margin-top: auto; }

        /* Bottone Da vedere / Visto */
        .btn-status {
            background: #0b0e11;
            border: 1px solid var(--border-color);
            color: var(--text-muted);
            padding: 10px;
            border-radius: 10px;
            flex-grow: 1;
            text-decoration: none;
            text-align: center;
            font-size: 0.85rem;
            transition: 0.3s;
        }
        .btn-status:hover { border-color: var(--accent-pink); color: white; }

        /* Bottone X Rossa */
        .btn-remove {
            background: transparent;
            border: 1px solid var(--accent-pink);
            color: var(--accent-pink);
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 10px;
            text-decoration: none;
            font-size: 1.2rem;
            transition: 0.3s;
        }
        .btn-remove:hover { background: var(--accent-pink); color: white; }
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
    <div class="stat-card"><h2><%= totali %></h2><p>Film salvati</p></div>
    <div class="stat-card"><h2><%= visti %></h2><p>Film visti</p></div>
    <div class="stat-card"><h2><%= daVedere %></h2><p>Da vedere</p></div>
</div>

<div class="filter-bar">
    <button class="filter-btn active" onclick="applyFilter('all', this)">Tutti</button>
    <button class="filter-btn" onclick="applyFilter('da-vedere', this)">Da vedere</button>
    <button class="filter-btn" onclick="applyFilter('visto', this)">Visto</button>
</div>

<div class="grid-container" id="watchlistGrid">
    <%
        if (items != null && movies != null) {
            for (int i = 0; i < items.size(); i++) {
                WatchlistItem it = items.get(i);
                TmdbMovie m = movies.get(i);
                String statusClass = it.isStatus() ? "visto" : "da-vedere";

                // MODIFICA: Recupero anno
                String year = (m.release_date != null && m.release_date.length() >= 4)
                        ? m.release_date.substring(0, 4) : "N/D";
    %>
    <div class="card movie-card <%= statusClass %>">
        <img src="https://image.tmdb.org/t/p/w500<%= m.poster_path %>" alt="<%= m.title %>">
        <div class="card-body">
            <div class="card-title"><%= m.title %></div>

            <div class="card-meta">
                <span><%= year %></span>
                <span class="badge-genre">Fantascienza</span>
            </div>

            <div class="actions">
                <a href="WatchlistServlet?action=toggle&idItem=<%= it.getIdItem() %>&status=<%= it.isStatus() %>" class="btn-status">
                    <%= it.isStatus() ? "✓ Visto" : "Da vedere" %>
                </a>
                <a href="WatchlistServlet?action=remove&idItem=<%= it.getIdItem() %>" class="btn-remove" title="Rimuovi">✕</a>
            </div>
        </div>
    </div>
    <%
        }
    } else {
    %>
    <p>La tua watchlist è vuota. Aggiungi qualche film dal catalogo!</p>
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
                if (card.classList.contains(filterType)) {
                    card.style.display = 'flex';
                } else {
                    card.style.display = 'none';
                }
            }
        });
    }
</script>
</body>
</html>