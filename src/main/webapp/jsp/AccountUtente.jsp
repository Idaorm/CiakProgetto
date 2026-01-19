<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Profilo - ${utente.username}</title>
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

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
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 60px;
      border: 3px solid #1f2937;
    }

    .user-details h1 {
      font-size: 36px;
      margin-bottom: 10px;
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
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

    .btn-modifica {
      margin-top: 20px;
      padding: 10px 20px;
      background: #f093fb;
      border: none;
      border-radius: 8px;
      color: #0a0e14;
      font-weight: bold;
      cursor: pointer;
      transition: background 0.3s;
    }

    .btn-modifica:hover {
      background: #f5576c;
      color: white;
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

    }
  </style>
</head>

<body>
<div class="container">
  <!-- Header Profilo -->
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

        <!-- Bottone Modifica account se l'utente è loggato e proprietario del profilo -->
        <c:if test="${not empty sessionScope.utente and sessionScope.utente.idUtente == utente.idUtente}">
          <form action="${pageContext.request.contextPath}/ModificaAccountServlet" method="get">
            <button type="submit" class="btn-modifica">Modifica account</button>
          </form>
        </c:if>

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

  <!-- Tabs -->
  <div class="tabs">
    <button class="tab active" onclick="switchTab('watchlist')">WatchList</button>
    <button class="tab" onclick="switchTab('recensioni')">Recensioni</button>
  </div>

  <!-- Contenuto Watchlist -->
  <div id="watchlist-content" class="tab-content active">
    <c:choose>
      <c:when test="${empty watchlist}">
        <div class="empty-state">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M7 4v16M17 4v16M3 8h4m10 0h4M3 12h18M3 16h4m10 0h4M4 20h16a1 1 0 001-1V5a1 1 0 00-1-1H4a1 1 0 00-1 1v14a1 1 0 001 1z"></path>
          </svg>
          <h3>Nessun film nella lista</h3>
        </div>
      </c:when>
      <c:otherwise>

        <!-- CODICE WATCHLIST -->

      </c:otherwise>
    </c:choose>
  </div>

  <!-- Contenuto Recensioni -->
  <div id="recensioni-content" class="tab-content">
    <c:choose>
      <c:when test="${empty recensioni}">
        <div class="empty-state">
          <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z"></path>
          </svg>
          <h3>Nessuna recensione</h3>
          <p>Non hai ancora scritto recensioni.</p>
        </div>
      </c:when>
      <c:otherwise>

        <!-- CODICE RECENSIONI -->

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
    event.target.classList.add('active');
    document.getElementById(tabName + '-content').classList.add('active');
  }
</script>
</body>

</html>