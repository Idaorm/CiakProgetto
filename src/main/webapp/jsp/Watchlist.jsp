<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.WatchlistItem, service.TmdbMovie" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/ciak.svg">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ciak! - La mia Watchlist</title>
    <style>
        :root {
            --bg-color: #0b0e11;
            --card-bg: #151a23;
            --text-color: #ffffff;
            --text-muted: #8b92a8;
            --accent-gradient: linear-gradient(90deg, #f093fb, #f5576c);
            --border-color: #2a3241;
            --accent-pink: #f5576c;
        }
        body {
            background-color: var(--bg-color);
            color: var(--text-color);
            font-family: 'Segoe UI', sans-serif;
            padding: 40px;
        }
        .stats-container {
            display: flex;
            gap: 20px;
            margin-bottom: 40px;
        }
        .stat-card {
            background: var(--card-bg);
            padding: 30px 20px;
            border-radius: 16px;
            flex: 1;
            border: 1px solid var(--border-color);
        }
        .stat-card h2 {
            margin: 0;
            font-size: 2.5rem;
            background: var(--accent-gradient);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .filter-bar {
            margin-bottom: 30px;
        }
        .filter-btn {
            background: #1a202c;
            color: var(--text-muted);
            border: none;
            padding: 10px 25px;
            border-radius: 25px;
            cursor: pointer;
            margin-right: 10px;
            font-weight: 600;
        }
        .filter-btn.active {
            background: var(--accent-gradient);
            color: white;
        }
        .grid-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 25px;
        }
        .card {
            background: var(--card-bg);
            border-radius: 20px;
            overflow: hidden;
            border: 1px solid var(--border-color);
            transition: transform 0.3s;
            display: flex;
            flex-direction: column;
        }
        .card:hover {
            transform: translateY(-5px);
        }
        .card img {
            width: 100%;
            height: 320px;
            object-fit: cover;
        }
        .card-body {
            padding: 20px;
            flex-grow: 1;
            display: flex;
            flex-direction: column;
        }
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
        .actions {
            display: flex;
            gap: 10px;
            margin-top: auto;
        }
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
        }
        .btn-remove {
            border: 1px solid var(--accent-pink);
            color: var(--accent-pink);
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 10px;
            text-decoration: none;
        }
    </style>
</head>
<body>

<h1>La mia watchlist</h1>

<%
    List<WatchlistItem> items = (List<WatchlistItem>) request.getAttribute("items");
    List<TmdbMovie> movies = (List<TmdbMovie>) request.getAttribute("moviesApi");

    int totali = (items != null) ? items.size() : 0;
    long visti = (items != null) ? items.stream().filter(WatchlistItem::isStatus).count() : 0;
    long daVedere= totali -visti;

    Map<Integer, String> genreMap = new HashMap<>();
    genreMap.put(28, "Azione"); genreMap.put(12, "Avventura"); genreMap.put(16, "Animazione");
    genreMap.put(35, "Commedia"); genreMap.put(80, "Crime"); genreMap.put(18, "Dramma");
    genreMap.put(14, "Fantasy"); genreMap.put(27, "Horror"); genreMap.put(878, "Fantascienza");
    genreMap.put(53, "Thriller");
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

<div class="grid-container">
    <%
        if (items != null && movies != null) {
            for (int i = 0; i < items.size() && i < movies.size(); i++) {
                WatchlistItem it = items.get(i);
                TmdbMovie m = movies.get(i);
                String statusClass = it.isStatus() ? "visto" : "da-vedere";
                String year = (m.release_date != null && m.release_date.length() >= 4) ? m.release_date.substring(0, 4) : "N/D";

                String genreName = "Cinema";
                if (m.genre_ids != null && !m.genre_ids.isEmpty()) {
                    genreName = genreMap.getOrDefault(m.genre_ids.get(0), "Cinema");
                }
    %>
    <div class="card movie-card <%= statusClass %>">
        <img src="https://image.tmdb.org/t/p/w500<%= m.poster_path %>" alt="<%= m.title %>">
        <div class="card-body">
            <div class="card-title"><strong><%= m.title %></strong></div>
            <div class="card-meta">
                <span><%= year %></span>
                <span class="badge-genre"><%= genreName %></span>
            </div>
            <div class="actions">
                <a href="WatchlistServlet?action=toggle&idItem=<%= it.getIdItem() %>&status=<%= it.isStatus() %>" class="btn-status">
                    <%= it.isStatus() ? "✓ Visto" : "Da vedere" %>
                </a>
                <a href="WatchlistServlet?action=remove&idItem=<%= it.getIdItem() %>" class="btn-remove" onclick="return confirm('Rimuovere?')">✕</a>
            </div>
        </div>
    </div>
    <% } } %>
</div>

<script>
    function applyFilter(filterType, btn) {
        document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        document.querySelectorAll('.movie-card').forEach(card => {
            card.style.display = (filterType === 'all' || card.classList.contains(filterType)) ? 'flex' : 'none';
        });
    }
</script>
</body>
</html>