<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Ciack!</title>
    <style>
        body {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            background: #0a0e14;
            color: #e4e6eb;
            min-height: 100vh;
        }

        * {
            box-sizing: border-box;
        }

        /* Login Page Styles */
        .login-page {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #0a0e14 0%, #151b26 100%);
            padding: 24px;
            position: relative;
            overflow: hidden;
        }

        .login-page::before {
            content: '';
            position: absolute;
            width: 500px;
            height: 500px;
            background: radial-gradient(circle, rgba(240, 147, 251, 0.1) 0%, transparent 70%);
            top: -200px;
            right: -200px;
            animation: float 8s ease-in-out infinite;
        }

        .login-page::after {
            content: '';
            position: absolute;
            width: 400px;
            height: 400px;
            background: radial-gradient(circle, rgba(245, 87, 108, 0.1) 0%, transparent 70%);
            bottom: -150px;
            left: -150px;
            animation: float 6s ease-in-out infinite reverse;
        }

        @keyframes float {
            0%, 100% {
                transform: translate(0, 0) scale(1);
            }
            50% {
                transform: translate(30px, 30px) scale(1.1);
            }
        }

        .login-container {
            width: 100%;
            max-width: 450px;
            position: relative;
            z-index: 1;
        }

        .login-card {
            background: rgba(21, 27, 38, 0.95);
            backdrop-filter: blur(20px);
            border-radius: 32px;
            padding: 48px;
            border: 1px solid rgba(255, 255, 255, 0.05);
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
        }

        .login-header {
            text-align: center;
            margin-bottom: 40px;
        }

        .logo {
            margin-bottom: 16px;
        }

        @keyframes pulse {
            0%, 100% {
                transform: scale(1);
            }
            50% {
                transform: scale(1.05);
            }
        }

        .login-title {
            font-size: 36px;
            font-weight: 900;
            margin: 0 0 8px 0;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .login-subtitle {
            font-size: 16px;
            color: #8b92a8;
            margin: 0;
            font-weight: 500;
        }

        .login-form {
            display: flex;
            flex-direction: column;
            gap: 24px;
            margin-bottom: 32px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .form-group label {
            font-size: 14px;
            font-weight: 600;
            color: #8b92a8;
        }

        .form-group input {
            padding: 12px 16px;
            border-radius: 12px;
            border: 1px solid #1f2937;
            background: #151b26;
            color: #e4e6eb;
            font-size: 15px;
            font-family: inherit;
            transition: all 0.2s;
        }

        .form-group input:focus {
            outline: none;
            border-color: #f093fb;
            box-shadow: 0 0 0 3px rgba(240, 147, 251, 0.1);
        }

        .btn-login {
            width: 100%;
            padding: 16px;
            border-radius: 16px;
            border: none;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            font-size: 16px;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.3s;
            font-family: inherit;
            box-shadow: 0 8px 24px rgba(240, 147, 251, 0.3);
        }

        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 32px rgba(240, 147, 251, 0.4);
        }

        .btn-login:active {
            transform: translateY(0);
        }

        .login-footer {
            text-align: center;
            padding-top: 24px;
            border-top: 1px solid #1f2937;
        }

        .login-footer p {
            margin: 0;
            font-size: 15px;
            color: #8b92a8;
        }

        .signup-link {
            color: #f093fb;
            text-decoration: none;
            font-weight: 700;
            transition: all 0.2s;
        }

        .signup-link:hover {
            color: #f5576c;
        }

        .error-message {
            background: rgba(239, 68, 68, 0.1);
            border: 1px solid rgba(239, 68, 68, 0.3);
            color: #ef4444;
            padding: 12px 16px;
            border-radius: 12px;
            font-size: 14px;
            font-weight: 600;
            margin-bottom: 20px;
            display: none;
        }

        .error-message.show {
            display: block;
        }

        @media (max-width: 640px) {
            .login-card {
                padding: 32px 24px;
            }

            .login-title {
                font-size: 28px;
            }

            .logo {
                font-size: 48px;
            }
        }
    </style>
</head>
<body>
<div class="login-page">
    <div class="login-container">
        <div class="login-card">

            <div class="login-header">
                <div class="logo">
                    <img src="${pageContext.request.contextPath}/images/ciak (1).svg" alt="Logo Ciak!" width="300" height="300">
                </div>
                <p class="login-subtitle">Accedi al tuo account</p>
            </div>


            <!-- Error message (se presente) -->
            <%
                String error = request.getParameter("error");
                if (error != null && !error.isEmpty()) {
            %>
            <div class="error-message show">
                <%
                    if ("invalid".equals(error)) {
                        out.print("⚠️ Credenziali non valide. Riprova.");
                    } else if ("required".equals(error)) {
                        out.print("⚠️ Compila tutti i campi.");
                    } else {
                        out.print("⚠️ Si è verificato un errore. Riprova.");
                    }
                %>
            </div>
            <% } %>

            <form class="login-form" method="post" action="LoginServlet">
                <div class="form-group">
                    <label for="login-email">Email</label>
                    <input type="email" id="login-email" name="email" placeholder="tuaemail@esempio.com" required>
                </div>
                <div class="form-group">
                    <label for="login-password">Password</label>
                    <input type="password" id="login-password" name="password" placeholder="••••••••" required>
                </div>
                <button type="submit" class="btn-login">Accedi</button>
            </form>

            <div class="login-footer">
                <p>Non hai un account? <a href="register.jsp" class="signup-link">Registrati</a></p>
            </div>

        </div>
    </div>
</div>

<script>
    // Client-side validation
    document.querySelector('.login-form').addEventListener('submit', function(e) {
        const email = document.getElementById('login-email').value.trim();
        const password = document.getElementById('login-password').value;

        if (!email || !password) {
            e.preventDefault();
            alert('Compila tutti i campi!');
            return false;
        }

        if (!email.includes('@')) {
            e.preventDefault();
            alert('Inserisci un indirizzo email valido!');
            return false;
        }
    });
</script>
</body>
</html>