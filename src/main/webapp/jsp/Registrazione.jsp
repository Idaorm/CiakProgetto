<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/jsp/Header.jsp" />

<!DOCTYPE html>
<html lang="it">
<head>
  <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/images/ciak (1).svg">
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/png" href="images/ciak (1).svg">
  <title>Registrazione | Ciak!</title>

  <style>
    *, *::before, *::after {
      box-sizing: border-box;
    }

    body {
      font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
      background-color: #0b0e11;
      color: #e4e6eb;
      margin: 0;
      padding: 20px 40px;
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

    .register-container {
      max-width: 480px;
      margin: 0 auto;
      background-color: #151a23;
      border: 1px solid #2a3241;
      border-radius: 20px;
      padding: 40px;
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    }

    .logo {
      display: flex;
      justify-content: center;
      align-items: center;
      margin-bottom: 25px;
    }

    .logo img {
      max-width: 100%;
      height: auto;
    }

    .error-box {
      background-color: rgba(245, 87, 108, 0.15);
      border: 1px solid #f5576c;
      color: #f5576c;
      padding: 15px 20px;
      border-radius: 12px;
      margin-bottom: 25px;
      font-weight: 600;
    }

    .error-box ul {
      margin: 0;
      padding-left: 20px;
    }


    .register-container h2 {
      text-align: center;
      margin-bottom: 30px;
      font-size: 1.8rem;
      color: #fff;
    }

    .form-group {
      margin-bottom: 20px;
    }

    .form-group label {
      display: block;
      margin-bottom: 8px;
      font-weight: 600;
      color: #8b92a8;
    }

    .form-input {
      width: 100%;
      background-color: #0b0e11;
      border: 1px solid #2a3241;
      border-radius: 50px;
      padding: 12px 20px;
      color: white;
      font-size: 1rem;
      outline: none;
      transition: all 0.3s ease;
    }

    .form-input::placeholder {
      color: #5a6b8c;
      font-style: italic;
    }

    .form-input:focus {
      border-color: #f093fb;
      box-shadow: 0 0 20px rgba(240, 147, 251, 0.2);
      transform: scale(1.02);
    }

    .password-group {
      position: relative;
    }

    .toggle-password {
      position: absolute;
      right: 18px;
      top: 50%;
      transform: translateY(-50%);
      cursor: pointer;
      font-size: 1.1rem;
      color: #8b92a8;
      user-select: none;
      transition: color 0.3s ease;
    }

    .toggle-password:hover {
      color: #f093fb;
    }

    .btn-submit {
      width: 100%;
      background-color: transparent;
      border: 1px solid #f093fb;
      color: #f093fb;
      padding: 14px;
      border-radius: 50px;
      cursor: pointer;
      font-weight: 700;
      font-size: 1rem;
      transition: all 0.3s;
      text-transform: uppercase;
      letter-spacing: 1px;
    }

    .btn-submit:hover {
      background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
      border-color: transparent;
      color: white;
      box-shadow: 0 0 15px rgba(240, 147, 251, 0.4);
    }

    .login-link {
      text-align: center;
      margin-top: 25px;
      color: #8b92a8;
    }

    .login-link a {
      color: #f093fb;
      text-decoration: none;
      font-weight: 600;
    }

    .login-link a:hover {
      text-decoration: underline;
    }

    @media (max-width: 768px) {
      body {
        padding: 20px;
      }
    }
  </style>
</head>
<body>

<div class="register-container">

  <div class="logo">
    <img src="${pageContext.request.contextPath}/images/ciak (1).svg" alt="Logo Ciak!" width="300" height="300">
  </div>

  <h2>Crea il tuo account</h2>

  <%-- Messaggi di errore multipli --%>
  <%
    List<String> errori = (List<String>) request.getAttribute("errori");
    if (errori != null && !errori.isEmpty()) {
  %>
  <div class="error-box">
    <ul>
      <% for (String err : errori) { %>
      <li><%= err %></li>
      <% } %>
    </ul>
  </div>
  <%
    }
  %>

  <%-- Errore singolo --%>
  <%
    String errore = (String) request.getAttribute("errore");
    if (errore != null) {
  %>
  <div class="error-box">
    <%= errore %>
  </div>
  <%
    }
  %>

  <form action="<%=request.getContextPath()%>/RegistrazioneServlet" method="POST">

    <div class="form-group">
      <label for="email">Email</label>
      <input type="email"
             id="email"
             name="email"
             value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"
             class="form-input"
             placeholder="nome@email.com"
             required>
    </div>

    <div class="form-group">
      <label for="password">Password</label>
      <input type="password"
             id="password"
             name="password"
             class="form-input"
             placeholder="••••••••"
             required>
      <label style="display: block; margin-top: 5px; font-weight: normal; color: #8b92a8;">
        <input type="checkbox" id="showPassword" onclick="togglePassword('password', this)"> Mostra password
      </label>
    </div>

    <div class="form-group">
      <label for="confermaPassword">Conferma Password</label>
      <input type="password"
             id="confermaPassword"
             name="confermaPassword"
             class="form-input"
             placeholder="••••••••"
             required>
      <label style="display: block; margin-top: 5px; font-weight: normal; color: #8b92a8;">
        <input type="checkbox" id="showConfermaPassword" onclick="togglePassword('confermaPassword', this)"> Mostra password
      </label>
    </div>

    <button type="submit" class="btn-submit">
      Registrati
    </button>
  </form>

  <div class="login-link">
    Hai già un account?
    <a href="<%=request.getContextPath()%>/LoginServlet">Accedi</a>
  </div>
</div>

<script>
  function togglePassword(inputId, checkbox) {
    const input = document.getElementById(inputId);
    input.type = checkbox.checked ? "text" : "password";
  }
</script>

</body>
</html>
