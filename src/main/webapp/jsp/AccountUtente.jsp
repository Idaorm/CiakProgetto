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
  <title>Profilo - ${requestScope.utente.username}</title>

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AccountUtente.css">
</head>

<body>
<jsp:include page="/jsp/Header.jsp" />

<%

  // 1. Chi sono io?
  UtenteRegistrato loggato = (UtenteRegistrato) session.getAttribute("utente");

  // 2. Chi voglio visitare
  UtenteRegistrato profilo = (UtenteRegistrato) request.getAttribute("profiloEsterno");

  if (profilo == null) {
    profilo = loggato;
  }

  boolean isOwner = (loggato != null && profilo != null && loggato.getIdUtente() == profilo.getIdUtente());

  List<WatchlistItem> items = (List<WatchlistItem>) request.getAttribute("watchlist");
  List<TmdbMovie> movies = (List<TmdbMovie>) request.getAttribute("moviesApi");
  // ... (il resto delle statistiche rimangono uguali) ...
  int totali = (items != null) ? items.size() : 0;
  long visti = 0; if (items != null) visti = items.stream().filter(WatchlistItem::isStatus).count();
  long daVedere = totali - visti;

  Map<Integer, String> genreMap = new HashMap<>();
  genreMap.put(28, "Azione"); genreMap.put(12, "Avventura"); genreMap.put(16, "Animazione");
  genreMap.put(35, "Commedia"); genreMap.put(80, "Crime"); genreMap.put(18, "Dramma");
  genreMap.put(14, "Fantasy"); genreMap.put(27, "Horror"); genreMap.put(878, "Sci-Fi");
  genreMap.put(53, "Thriller");
%>
<div class="container">
  <section class="profile-header">
    <div class="profile-info">
      <div class="avatar">
        <%-- Immagine Profilo --%>
        <c:choose>
          <c:when test="${not empty requestScope.utente.photo}">
            <img src="${pageContext.request.contextPath}/images/profilo/${requestScope.utente.photo}" alt="Avatar" class="avatar-img">
          </c:when>
          <c:otherwise>
            <div style="width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; background: #333; color: #fff; font-size: 3rem; font-weight: bold;">
                ${requestScope.utente.username.charAt(0)}
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <div class="user-details">
        <h1><%= profilo.getUsername() %></h1>

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

          <%-- Mostra il tasto Modifica SOLO se sono il proprietario --%>
          <% if (isOwner) { %>
          <form action="${pageContext.request.contextPath}/jsp/ModificaAccount.jsp" method="get">
            <button type="submit" class="btn-header btn-primary">
              Modifica Account
            </button>
          </form>
          <% } %>
        </div>
      </div>
    </div>

    <div class="bio">
      <h3>Biografia</h3>
      <p>
        <%= (profilo.getBio() != null && !profilo.getBio().isEmpty()) ? profilo.getBio() : "Nessuna biografia inserita." %>
      </p>
    </div>
  </section>

  <div class="tabs">
    <button class="tab active" onclick="switchTab('watchlist')">WatchList</button>
    <button class="tab" onclick="switchTab('recensioni')">Recensioni</button>
  </div>

  <div id="watchlist-content" class="tab-content active">

    <%-- Logica Visibilità Watchlist --%>
    <% if (!isOwner && !profilo.isWatchlistVisibility()) { %>
    <div class="empty-state">
      <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" width="50" height="50">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
      </svg>
      <h3>Watchlist Privata</h3>
      <p>L'utente ha deciso di non rendere pubblica la sua lista film.</p>
    </div>

    <% } else if (items == null || items.isEmpty()) { %>
    <div class="empty-state">
      <h3>Nessun film nella lista</h3>
      <p><%= isOwner ? "Inizia ad aggiungere film dal catalogo!" : "L'utente non ha ancora aggiunto film." %></p>
    </div>

    <% } else { %>
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
        for (int i = 0; i < items.size() && i < movies.size(); i++) {
          WatchlistItem it = items.get(i);
          TmdbMovie m = movies.get(i);

          if (m != null && m.title != null) {
            String statusClass = it.isStatus() ? "visto" : "da-vedere";
            String year = (m.release_date != null && m.release_date.length() >= 4) ? m.release_date.substring(0, 4) : "N/D";
            String genreName = (m.genre_ids != null && !m.genre_ids.isEmpty()) ? genreMap.getOrDefault(m.genre_ids.get(0), "Cinema") : "Cinema";
            String posterUrl = (m.poster_path != null && !m.poster_path.isEmpty())
                    ? "https://image.tmdb.org/t/p/w500" + m.poster_path
                    : "https://via.placeholder.com/500x750?text=No+Poster";
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
            <a href="${pageContext.request.contextPath}/WatchlistServlet?action=toggle&idItem=<%= it.getIdItem() %>&status=<%= it.isStatus() %>" class="btn-status-wl">
              <%= it.isStatus() ? "✓ Visto" : "Da vedere" %>
            </a>
            <a href="${pageContext.request.contextPath}/WatchlistServlet?action=remove&idItem=<%= it.getIdItem() %>" class="btn-remove-wl" onclick="return confirm('Rimuovere?')">✕</a>
            <% } else { %>
            <span class="btn-status-wl" style="cursor: default; flex-grow: 1; opacity: 0.7;">
                                    <%= it.isStatus() ? "Visto" : "Da vedere" %>
                                </span>
            <% } %>
          </div>
        </div>
      </div>
      <%      }
      }
      %>
    </div>
    <% } %>
  </div>

  <div id="recensioni-content" class="tab-content">
    <c:choose>
      <c:when test="${empty recensioniMap}">
        <div class="empty-state">
          <h3>Nessuna recensione</h3>
          <p><%= isOwner ? "Non hai ancora scritto recensioni." : "L'utente non ha scritto recensioni." %></p>
        </div>
      </c:when>
      <c:otherwise>
        <div class="reviews-grid">
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
                  <span style="color: #ffd700;">★</span> ${entry.key.rating}/5
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
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));


    const buttons = document.querySelectorAll('.tab');
    if(tabName === 'watchlist') buttons[0].classList.add('active');
    else buttons[1].classList.add('active');

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