<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/ciak.svg">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ricerca utenti</title>

    <style>
        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background-color: #0b0e11;
            color: #e4e6eb;
            margin: 0;
            padding: 20px;
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

        .search-input {
            background-color: #151a23;
            border: 1px solid #2a3241;
            border-radius: 50px;
            padding: 12px 24px;
            color: white;
            font-size: 1rem;
            width: 350px;
            text-align: center;
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
            padding: 30px;
            text-align: center;
            transition: all 0.3s ease;
        }

        .card:hover {
            transform: translateY(-8px);
            border-color: #8f074f;
            box-shadow: 0 10px 30px rgba(245, 87, 108, 0.15);
        }

        .avatar {
            width: 96px;
            height: 96px;
            border-radius: 50%;
            background-color: #1f2533;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2.5rem;
            margin: 0 auto 15px;
            overflow: hidden;
            border: 2px solid #2a3241;
        }

        .avatar-img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .username {
            font-size: 1.3rem;
            font-weight: 700;
            color: white;
            margin-bottom: 15px;
        }

        .btn-profile {
            display: inline-block;
            border: 1px solid #f093fb;
            color: #f093fb;
            padding: 10px 18px;
            border-radius: 12px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
        }

        .btn-profile:hover {
            background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
            color: white;
            border-color: transparent;
        }
    </style>
</head>

<body>

<jsp:include page="/jsp/Header.jsp" />

<div class="header-row">
    <div class="title-group">
        <h1>Utenti trovati</h1>
    </div>

    <form action="${pageContext.request.contextPath}/CercaUtentiServlet" method="GET">
        <input type="text"
               name="utenteDaCercare"
               class="search-input"
               placeholder="Cerca utenti..."
               value="${param.utenteDaCercare}">
    </form>
</div>

<div class="grid-container">

    <!-- Mostra eventuale errore -->
    <c:if test="${not empty errore}">
        <div style="grid-column: 1 / -1; text-align: center; color: #f5576c; padding: 20px;">
            <strong>${errore}</strong>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty utenti}">
            <c:forEach var="u" items="${utenti}">

                <div class="card"
                     onclick="window.location='${pageContext.request.contextPath}/AccountUtenteServlet?id=${u.idUtente}'"
                     style="cursor:pointer;">
                    <div class="avatar">
                        <c:choose>
                            <c:when test="${not empty u.photo}">
                                <img src="${pageContext.request.contextPath}/images/profilo/${u.photo}" class="avatar-img" alt="Avatar">
                            </c:when>
                            <c:otherwise>👤</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="username">@${u.username}</div>
                </div>

            </c:forEach>
        </c:when>

        <c:otherwise>
            <div style="grid-column: 1 / -1; text-align: center; color: #8b92a8; padding: 60px;">
                <h2>Nessun utente trovato</h2>
                <p>Prova con un nome o username diverso.</p>
            </div>
        </c:otherwise>
    </c:choose>

</div>

</body>
</html>
