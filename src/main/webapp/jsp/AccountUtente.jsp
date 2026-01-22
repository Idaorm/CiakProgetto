<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="java.util.*, model.WatchlistItem, service.TmdbMovie, model.UtenteRegistrato" %>


<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/ciak.svg">
  <title>Profilo - ${utente.username}</title>
  <style>

    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background: #0a0e14;
      color: #e4e6eb;
      line-height: 1.6;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 20px;
    }

    /* Header Profilo */
    .profile-header {
      background: #151b26;
      border-radius: 16px;
      padding: 40px;
      margin-bottom: 40px;
      border: 1px solid #1f2937;
    }

    .profile-info {
      display: flex;
      align-items: center;
      gap: 30px;
      margin-bottom: 30px;
    }

    .avatar {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      border: 3px solid #1f2937;
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      flex-shrink: 0;
      position: relative;
    }

    .avatar img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 50%;
    }

    .user-details h1 {
      font-size: 36px;
      margin-bottom: 10px;
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }

    .user-actions-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 20px;
      gap: 40px;
    }

    .user-actions-row form {
      margin-left: auto;
    }

    .user-stats {
      display: flex;
      gap: 30px;
      margin-top: 15px;
    }

    .stat {
      text-align: center;
    }

    .stat-value {
      font-size: 24px;
      font-weight: bold;
      color: #f093fb;
    }

    .stat-label {
      font-size: 14px;
      color: #8b92a8;
      text-transform: uppercase;
    }

    .bio {
      margin-top: 20px;
      padding: 20px;
      background: #0a0e14;
      border-radius: 12px;
      border: 1px solid #1f2937;
    }

    .bio h3 {
      font-size: 14px;
      color: #8b92a8;
      text-transform: uppercase;
      margin-bottom: 10px;
    }

    /* Tabs */
    .tabs {
      display: flex;
      gap: 20px;
      border-bottom: 2px solid #1f2937;
      margin-bottom: 30px;
    }

    .tab {
      padding: 15px 30px;
      font-size: 18px;
      font-weight: 600;
      color: #8b92a8;
      cursor: pointer;
      border: none;
      background: none;
      position: relative;
      transition: color 0.3s;
    }

    .tab:hover {
      color: #e4e6eb;
    }

    .tab.active {
      color: #f093fb;
    }

    .tab.active::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      right: 0;
      height: 2px;
      background: #f093fb;
    }

    .tab-content {
      display: none;
    }

    .tab-content.active {
      display: block;
    }

    /* Stato Vuoto */
    .empty-state {
      text-align: center;
      padding: 60px 20px;
      color: #8b92a8;
    }

    .empty-state svg {
      width: 80px;
      height: 80px;
      margin-bottom: 20px;
      opacity: 0.4;
    }

    .btn-header {
      padding: 10px 24px;
      border-radius: 8px;
      font-size: 15px;
      font-weight: 600;
      text-decoration: none;
      transition: all 0.2s;
      border: none;
      cursor: pointer;
    }

    .btn-primary {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      color: white;
      box-shadow: 0 4px 15px rgba(245, 87, 108, 0.3);
    }

    .btn-primary:hover {
      opacity: 0.9;
      transform: translateY(-1px);
      box-shadow: 0 6px 20px rgba(245, 87, 108, 0.4);
    }

    /* --- STILI WATCHLIST --- */
    :root {
      --accent-gradient: linear-gradient(90deg, #f093fb, #f5576c);
      --card-bg: #151b26;
      --border-color: #1f2937;
      --accent-pink: #f5576c;
      --text-muted: #8b92a8;
    }
    .actions {
      display: flex;
      gap: 10px;
      margin-top: auto;
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
      text-align: center;
    }

    .stat-card h2 {
      margin: 0;
      font-size: 2.5rem;
      background: var(--accent-gradient);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      font-weight: 800;
    }

    .grid-container {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
      gap: 25px;
    }

    .movie-card {
      background: var(--card-bg);
      border-radius: 20px;
      overflow: hidden;
      border: 1px solid var(--border-color);
      transition: transform 0.3s;
      display: flex;
      flex-direction: column;
      position: relative;
    }

    .movie-card:hover {
      transform: translateY(-5px);
      border-color: #f5576c;
      box-shadow: 0 10px 20px rgba(0, 0, 0, 0.4);
    }
    .movie-card-img {
      width: 100%; height: 320px;
      object-fit: cover; }

    .card-body-wl {
      padding: 20px;
      flex-grow: 1;
      display: flex;
      flex-direction: column; }

    .card-meta-wl {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 20px;
      font-size: 0.85rem;
      color: var(--text-muted);
    }

    .badge-genre-wl {
      background: var(--accent-pink);
      color: white;
      padding: 2px 10px;
      border-radius: 12px;
      font-size: 0.75rem;
      font-weight: bold;
    }

    .btn-status-wl {
      background: #0a0e14;
      border: 1px solid var(--border-color);
      color: var(--text-muted);
      padding: 10px;
      border-radius: 10px;
      flex-grow: 1;
      text-decoration: none;
      text-align: center;
      font-size: 0.85rem;
    }

    .btn-remove-wl {
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
    .btn-remove-wl:hover {
      background: rgba(245, 87, 108, 0.1);
      transform: scale(1.05);
    }

    .movie-title-wl {
      font-weight: bold;
      margin-bottom: 8px;
      color: #ffffff;
      font-size: 1.1rem;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      min-height: 2.8em;
    }
    .filter-bar-wl { margin-bottom: 30px; }
    .filter-btn-wl {
      background: #1a202c;
      color: var(--text-muted);
      border: none;
      padding: 10px 25px;
      border-radius: 25px;
      cursor: pointer;
      margin-right: 10px;
      font-weight: 600;
    }
    .filter-btn-wl.active { background: var(--accent-gradient); color: white; }

    /* --- STILI RECENSIONI --- */
    .reviews-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 25px;
    }

    .review-card {
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 16px;
      padding: 25px;
      transition: transform 0.2s, box-shadow 0.2s;
      display: flex;
      flex-direction: column;
    }

    .review-card:hover {
      transform: translateY(-3px);
      border-color: var(--accent-pink);
      box-shadow: 0 8px 20px rgba(0,0,0,0.3);
    }

    .review-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 15px;
      border-bottom: 1px solid #1f2937;
      padding-bottom: 10px;
    }

    .review-movie-title {
      font-size: 1.1rem;
      font-weight: 700;
      color: #fff;
      margin: 0;
    }

    .review-date {
      font-size: 0.8rem;
      color: var(--text-muted);
      margin-top: 5px;
    }

    .review-rating {
      background: rgba(240, 147, 251, 0.1);
      color: #f093fb;
      padding: 5px 12px;
      border-radius: 20px;
      font-weight: bold;
      font-size: 0.9rem;
      white-space: nowrap;
    }

    .review-text {
      color: #d1d5db;
      font-size: 0.95rem;
      line-height: 1.6;
      font-style: italic;
      flex-grow: 1;
    }

    .quote-icon {
      font-size: 1.5rem;
      color: #8b92a8;
      opacity: 0.3;
      margin-right: 5px;
    }


    /* Responsive */
    @media (max-width: 768px) {
      .profile-info {
        flex-direction: column;
        text-align: center;
      }

      .user-details h1 {
        font-size: 28px;
      }

      .user-actions-row {
        flex-direction: column;
        align-items: center;
        gap: 20px;
      }

      .user-actions-row form {
        margin-left: 0;
      }

      .stats-container {
        flex-direction: column;
        gap: 10px;
      }

      .stat-card {
        padding: 15px;
      }

      .stat-card h2 {
        font-size: 1.8rem;
      }

      .filter-bar-wl {
        display: flex;
        overflow-x: auto;
        padding-bottom: 10px;
        white-space: nowrap;
        gap: 10px;
      }

      .filter-btn-wl {
        padding: 8px 15px;
        font-size: 14px;
      }

      .grid-container, .reviews-grid {
        grid-template-columns: repeat(auto-fill, minmax(100%, 1fr));
        gap: 15px;
      }

      .movie-card img {
        height: 230px;
      }

      .card-body-wl {
        padding: 12px;
      }
    }

  </style>
</head>

<body>
<jsp:include page="/jsp/Header.jsp" />
<%
  // Recupero dati per Watchlist (Scriptlet legacy)
  List<WatchlistItem> items = (List<WatchlistItem>) request.getAttribute("watchlist");
  List<TmdbMovie> movies = (List<TmdbMovie>) request.getAttribute("moviesApi");

  // Calcolo statistiche Watchlist
  int totali = (items != null) ? items.size() : 0;
  long visti = 0;
  if (items != null) {
    visti = items.stream().filter(WatchlistItem::isStatus).count();
  }
  long daVedere = totali - visti;

  // Mappa generi
  Map<Integer, String> genreMap = new HashMap<>();
  genreMap.put(28, "Azione"); genreMap.put(12, "Avventura"); genreMap.put(16, "Animazione");
  genreMap.put(35, "Commedia"); genreMap.put(80, "Crime"); genreMap.put(18, "Dramma");
  genreMap.put(14, "Fantasy"); genreMap.put(27, "Horror"); genreMap.put(878, "Sci-Fi");
  genreMap.put(53, "Thriller");

  // Controllo se l'utente loggato è anche il proprietario del profiilo
  UtenteRegistrato loggato = (UtenteRegistrato) session.getAttribute("utente");
  UtenteRegistrato profilo = (UtenteRegistrato) request.getAttribute("utente");
  boolean isOwner = (loggato != null && profilo != null && loggato.getIdUtente() == profilo.getIdUtente());
%>

<div class="container">
  <section class="profile-header">
    <div class="profile-info">
      <div class="avatar">
        <c:choose>
          <c:when test="${not empty utente.photo}">
            <img src="${pageContext.request.contextPath}/images/profilo/${utente.photo}" alt="Avatar" class="avatar-img">
          </c:when>
          <c:otherwise>
            👤
          </c:otherwise>
        </c:choose>
      </div>
      <div class="user-details">
        <h1>${utente.username}</h1>
        <div class="user-actions-row">

          <div class="user-stats">
            <div class="stat">
              <div class="stat-value">${watchlistCount}</div>
              <div class="stat-label">Film</div>
            </div>
            <div class="stat">
              <div class="stat-value">${recensioniCount}</div>
              <div class="stat-label">Recensioni</div>
            </div>
          </div>

          <c:if test="${not empty sessionScope.utente and sessionScope.utente.idUtente == utente.idUtente}">
            <form action="${pageContext.request.contextPath}/ModificaAccountServlet" method="get">
              <button type="submit" class="btn-header btn-primary">
                Modifica Account
              </button>
            </form>
          </c:if>

        </div>

      </div>
    </div>

    <div class="bio">
      <h3>Biografia</h3>
      <p>
        <c:choose>
          <c:when test="${not empty utente.bio}">
            ${utente.bio}
          </c:when>
          <c:otherwise>
            Nessuna bio
          </c:otherwise>
        </c:choose>
      </p>
    </div>
  </section>

  <div class="tabs">
    <button class="tab active" onclick="switchTab('watchlist')">WatchList</button>
    <button class="tab" onclick="switchTab('recensioni')">Recensioni</button>
  </div>

  <div id="watchlist-content" class="tab-content active">
    <c:choose>

      <%-- Lista privata non visibile a visitatori o altri utenti --%>
      <c:when test="${empty sessionScope.utente or (sessionScope.utente.idUtente != utente.idUtente and not utente.watchlistVisibility)}">
        <div class="empty-state">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" width="50" height="50">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 4v16m8-8H4"></path>
          </svg>
          <h3>La watchlist è privata</h3>
          <p>Solo il proprietario può vedere i film salvati.</p>
        </div>
      </c:when>

      <%-- Lista vuota visibile solo al proprietario o se lista pubblica --%>
      <c:when test="${not empty sessionScope.utente and (sessionScope.utente.idUtente == utente.idUtente or utente.watchlistVisibility) and empty watchlist}">
        <div class="empty-state">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" width="50" height="50">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M7 4v16M17 4v16M3 8h4m10 0h4M3 12h18M3 16h4m10 0h4M4 20h16a1 1 0 001-1V5a1 1 0 00-1-1H4a1 1 0 00-1 1v14a1 1 0 001 1z"></path>
          </svg>
          <h3>Nessun film nella lista</h3>
        </div>
      </c:when>

      <%-- Lista visibile con elementi --%>
      <c:otherwise>
        <%-- CODICE WATCHLIST --%>
        <div class="stats-container">
          <div class="stat-card"><h2><%= totali %></h2><p>Film salvati</p></div>
          <div class="stat-card"><h2><%= visti %></h2><p>Film visti</p></div>
          <div class="stat-card"><h2><%= daVedere %></h2><p>Da vedere</p></div>
        </div>

        <div class="filter-bar-wl">
          <button class="filter-btn-wl active" onclick="applyFilter('all', this)">Tutti</button>
          <button class="filter-btn-wl" onclick="applyFilter('da-vedere', this)">Da vedere</button>
          <button class="filter-btn-wl" onclick="applyFilter('visto', this)">Visti</button>
        </div>

        <div class="grid-container">
          <%
            if (items != null && movies != null) {
              for (int i = 0; i < items.size() && i < movies.size(); i++) {
                model.WatchlistItem it = items.get(i);
                service.TmdbMovie m = movies.get(i);
                if (m != null && m.title != null) {
                  String statusClass = it.isStatus() ? "visto" : "da-vedere";
                  String year = (m.release_date != null && m.release_date.length() >= 4) ? m.release_date.substring(0, 4) : "N/D";
                  String genreName = (m.genre_ids != null && !m.genre_ids.isEmpty()) ? genreMap.getOrDefault(m.genre_ids.get(0), "Cinema") : "Cinema";
                  String posterUrl = (m.poster_path != null && !m.poster_path.isEmpty())
                          ? "https://image.tmdb.org/t/p/w500" + m.poster_path
                          : "https://via.placeholder.com/500x750?text=No+Poster+Available";
          %>
          <div class="movie-card <%= statusClass %>">
            <img src="<%= posterUrl %>" alt="<%= m.title %>" class="movie-card-img">
            <div class="card-body-wl">
              <div class="movie-title-wl" style="font-weight: bold; margin-bottom: 5px;"><%= m.title %></div>
              <div class="card-meta-wl">
                <span><%= year %></span>
                <span class="badge-genre-wl"><%= genreName %></span>
              </div>
              <div class="actions">
                <% if(isOwner) { %>
                <a href="WatchlistServlet?action=toggle&idItem=<%= it.getIdItem() %>&status=<%= it.isStatus() %>" class="btn-status-wl">
                  <%= it.isStatus() ? "✓ Visto" : "Da vedere" %>
                </a>
                <a href="WatchlistServlet?action=remove&idItem=<%= it.getIdItem() %>" class="btn-remove-wl" onclick="return confirm('Rimuovere?')">✕</a>
                <% } else { %>
                <span class="btn-status-wl" style="cursor: default; flex-grow: 1;">
                                  <%= it.isStatus() ? "Visto" : "Da vedere" %>
                              </span>
                <% } %>
              </div>
            </div>
          </div>
          <%
                }
              }
            }
          %>
        </div>
      </c:otherwise>
    </c:choose>
  </div>

  <div id="recensioni-content" class="tab-content">
    <c:choose>
      <c:when test="${empty recensioniMap}">
        <div class="empty-state">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"></path>
          </svg>
          <h3>Nessuna recensione</h3>
        </div>
      </c:when>
      <c:otherwise>
        <div class="reviews-grid">
            <%-- Ciclo sulla Mappa. entry.key = Recensione, entry.value = Titolo Film --%>
          <c:forEach var="entry" items="${recensioniMap}">

            <div class="review-card">

              <div class="review-header">
                <div>
                  <h4 class="review-movie-title">${entry.value}</h4>
                  <div class="review-date">
                    <fmt:formatDate value="${entry.key.date}" pattern="d MMMM yyyy" />
                  </div>
                </div>
                <div class="review-rating">
                  ★ ${entry.key.rating}/5
                </div>
              </div>

              <div class="review-text">
                <span class="quote-icon">❝</span>
                <c:out value="${entry.key.text}" />
              </div>

            </div>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<script>
  function switchTab(tabName) {
    // Rimuovi classe active da tutti i tab
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    // Nascondi tutti i contenuti
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    // Attiva il tab selezionato
    const clickedTab = event.target;
    clickedTab.classList.add('active');
    document.getElementById(tabName + '-content').classList.add('active');
  }

  function applyFilter(filterType, btn) {
    document.querySelectorAll('.filter-btn-wl').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    document.querySelectorAll('.movie-card').forEach(card => {
      card.style.display = (filterType === 'all' || card.classList.contains(filterType)) ? 'flex' : 'none';
    });
  }

</script>
</body>
</html>