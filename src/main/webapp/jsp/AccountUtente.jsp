<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="java.util.*, model.WatchlistItem, controller.service.TmdbMovie, model.UtenteRegistrato" %>


<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/ciak.svg">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AccountUtente.css">
  <title>Profilo - ${utente.username}</title>
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

      <c:if test="${not empty errore}">
        <div style="grid-column: 1 / -1; text-align: center; color: #f5576c; padding: 20px;">
          <strong>${errore}</strong>
        </div>
      </c:if>

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
                controller.service.TmdbMovie m = movies.get(i);
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