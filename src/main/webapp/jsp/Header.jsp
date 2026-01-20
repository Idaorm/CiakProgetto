<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.UtenteRegistrato" %>
<%
    UtenteRegistrato utente = (UtenteRegistrato) session.getAttribute("utente");
    boolean isLogged = (utente != null);
%>

<style>

    .main-header {
        background: rgba(10, 14, 20, 0.95);
        backdrop-filter: blur(10px);
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        height: 80px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        position: sticky;
        top: 0;
        z-index: 1000;
        font-family: 'Inter', sans-serif;
        margin-bottom: 40px;
    }

    .header-left {
        display: flex;
        align-items: center;
    }

    .logo-wrapper {
        display: flex;
        align-items: center;
        gap: 15px;
        user-select: none;
    }

    .logo-img {
        height: 55px;
        width: auto;
        filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
    }

    .header-logo-text {
        font-size: 24px;
        font-weight: 900;
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        cursor: default;
    }

    .header-right {
        display: flex;
        align-items: center;
        gap: 15px;
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
        border: 1px solid rgba(255, 255, 255, 0.2) !important;
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

</style>

<header class="main-header">
    <div class="header-left">
        <div class="logo-wrapper">
            <img src="${pageContext.request.contextPath}/images/ciak.svg" alt="Logo" class="logo-img">
            <span class="header-logo-text">CIAK!</span>
        </div>
    </div>

    <div class="header-right">
        <a href="${pageContext.request.contextPath}/CatalogoServlet" class="btn-header btn-primary">Catalogo Film</a>
        <% if (!isLogged) { %>
        <a href="${pageContext.request.contextPath}/LoginServlet" class="btn-header btn-primary">Accedi</a>
        <a href="${pageContext.request.contextPath}/RegistrazioneServlet" class="btn-header btn-ghost">Registrati</a>
        <% } else { %>
        <a href="${pageContext.request.contextPath}/AccountUtenteServlet" class="btn-header btn-primary">Il mio Account</a>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn-header btn-ghost">Logout</a>
        <% } %>
    </div>
</header>