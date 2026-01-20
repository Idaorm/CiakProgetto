<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<!DOCTYPE html>
<html lang="it">
<head>
  <meta charset="UTF-8">
  <title>Modifica Account</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/png" href="images/ciak (1).svg">

  <style>

    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background: #0a0e14;
      color: #e4e6eb;
    }

    .container {
      max-width: 900px;
      margin: 40px auto;
      padding: 20px;
    }

    .edit-card {
      background: #151b26;
      border-radius: 16px;
      padding: 40px;
      border: 1px solid #1f2937;
    }

    .edit-card h1 {
      font-size: 32px;
      margin-bottom: 30px;
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }

    /* Avatar */
    .avatar-section {
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
      overflow: hidden;
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 60px;
    }

    .avatar img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .form-group {
      margin-bottom: 25px;
    }

    label {
      display: block;
      margin-bottom: 8px;
      font-weight: 600;
      color: #8b92a8;
    }

    input[type="text"],
    input[type="email"],
    textarea,
    select {
      width: 100%;
      padding: 12px 15px;
      border-radius: 8px;
      border: 1px solid #1f2937;
      background: #0a0e14;
      color: #e4e6eb;
      font-size: 16px;
    }

    textarea {
      resize: vertical;
      min-height: 100px;
    }

    input[readonly] {
      opacity: 0.6;
      cursor: not-allowed;
    }

    /* Toggle */
    .toggle {
      display: flex;
      align-items: center;
      gap: 15px;
    }

    .toggle input {
      width: 20px;
      height: 20px;
      accent-color: #f093fb;
    }

    /* Bottoni */
    .actions {
      display: flex;
      justify-content: space-between;
      margin-top: 40px;
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

    .btn-ghost {
      color: #e4e6eb;
      background: transparent;
      border: 1px solid rgba(255, 255, 255, 0.2);
    }

    .btn-ghost:hover {
      background: rgba(255,255,255,0.1);
      border-color: #f093fb;
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

    /* Card opzioni */
    .option-card {
      background: #0a0e14;
      border: 1px solid #1f2937;
      border-radius: 12px;
      padding: 18px;
      margin-top: 10px;
    }

    .option-row {
      display: flex;
      align-items: flex-start;
      gap: 14px;
    }

    .option-row input[type="checkbox"] {
      margin-top: 4px;
      width: 20px;
      height: 20px;
      accent-color: #f093fb;
    }

    .option-text {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .option-text strong {
      font-size: 15px;
      color: #e4e6eb;
    }

    .option-text small {
      font-size: 13px;
      color: #8b92a8;
      line-height: 1.4;
    }

    /* Upload migliorato */
    .file-input {
      margin-top: 10px;
    }

    .file-input input[type="file"] {
      background: #0a0e14;
      border: 1px dashed #1f2937;
      padding: 10px;
      border-radius: 8px;
      width: 100%;
      cursor: pointer;
    }

    .file-input input[type="file"]:hover {
      border-color: #f093fb;
    }

    .error-box {
      background: rgba(245, 87, 108, 0.12);
      border: 1px solid rgba(245, 87, 108, 0.4);
      color: #f5576c;
      padding: 14px 18px;
      border-radius: 10px;
      margin-bottom: 25px;
      font-weight: 600;
    }

    @media (max-width: 768px) {
      .avatar-section {
        flex-direction: column;
        text-align: center;
      }
    }
  </style>
</head>

<body>
<jsp:include page="/jsp/Header.jsp" />
<div class="container">
  <div class="edit-card">
    <h1>Modifica account</h1>

    <c:if test="${not empty errore}">
      <div class="error-box">
          ${errore}
      </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/ModificaAccountServlet"
          method="post"
          enctype="multipart/form-data">

      <!-- Avatar -->
      <div class="avatar-section">
        <div class="avatar">
          <c:choose>
            <c:when test="${not empty utente.photo}">
              <img src="${pageContext.request.contextPath}/images/profilo/${utente.photo}" alt="Avatar">
            </c:when>
            <c:otherwise>
              👤
            </c:otherwise>
          </c:choose>
        </div>

        <div class="form-group">
          <label>Immagine profilo</label>

          <div class="option-card">

            <div class="file-input">
              <input type="file" id="photo" name="photo" accept="image/*">
              <small style="color:#8b92a8">
                JPG, PNG o WEBP – max 5MB
              </small>
            </div>

            <c:if test="${not empty utente.photo}">
              <div class="option-row option-danger" style="margin-top:16px">
                <input type="checkbox" id="removePhoto" name="removePhoto">
                <div class="option-text">
                  <strong>Rimuovi immagine attuale</strong>
                  <small>
                    Verrà ripristinata l’icona predefinita del profilo.
                  </small>
                </div>
              </div>
            </c:if>

          </div>
        </div>

      </div>

      <!-- Username -->
      <div class="form-group">
        <label for="username">Username</label>
        <input type="text" id="username" name="username"
               value="${utente.username}" required>
      </div>

      <!-- Email -->
      <div class="form-group">
        <label>Email</label>
        <input type="email" value="${utente.email}" readonly>
      </div>

      <!-- Bio -->
      <div class="form-group">
        <label for="bio">Biografia</label>
        <textarea id="bio" name="bio"
                  placeholder="Scrivi qualcosa su di te...">${utente.bio}</textarea>
      </div>

      <!-- Watchlist visibility -->
      <div class="form-group">
        <label>Visibilità Watchlist</label>

        <div class="option-card">
          <div class="option-row">
            <input type="checkbox"
                   id="watchlistVisibility"
                   name="watchlistVisibility"
                   <c:if test="${utente.watchlistVisibility}">checked</c:if>>

            <div class="option-text">
              <strong>Rendi la watchlist pubblica</strong>
              <small>
                Se attiva, consentirai agli altri utenti Ciak! che visitano il tuo profilo
                di vedere la tua Watchlist. In caso contrario, la tua lista di film sarà visibile solo a te.
              </small>
            </div>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="actions">

        <a href="${pageContext.request.contextPath}/AccountUtenteServlet"
           class="btn-header btn-ghost">
          Annulla
        </a>

        <button type="submit" class="btn-header btn-primary">
          Salva modifiche
        </button>

      </div>

    </form>
  </div>
</div>
</body>
</html>
