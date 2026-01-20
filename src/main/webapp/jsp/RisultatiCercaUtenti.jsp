<h2>Risultati utenti</h2>

<c:if test="${empty utenti}">
    <p>Nessun utente trovato</p>
</c:if>

<ul>
    <c:forEach var="u" items="${utenti}">
        <li>
            <a href="ProfiloServlet?id=${u.id}">
                    ${u.username} (${u.nome})
            </a>
        </li>
    </c:forEach>
</ul>
