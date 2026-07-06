<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="pageTitle" value="Login - Citas Medicas" scope="request" />

<!DOCTYPE html>
<html lang="es">
<%@ include file="/WEB-INF/views/layout/head.jspf" %>
<body class="login-page">
    <div class="container-fluid min-vh-100 d-flex align-items-center justify-content-center py-4">
        <div class="login-shell shadow-lg">
            <div class="row g-0 h-100">

                <aside class="col-12 col-lg-5 login-panel-left">
                    <div class="login-panel-inner">
                        <div class="login-brand mb-4">
                            <div class="login-brand-icon">
                                <i class="bi bi-heart-pulse-fill"></i>
                            </div>
                            <div>
                                <h2 class="login-brand-title mb-0">NOVASALUD</h2>
                                <p class="login-brand-subtitle mb-0">
                                    Sistema de Gestion de Citas Medicas
                                </p>
                            </div>
                        </div>

                        <h3 class="login-welcome-title">Bienvenido</h3>

                        <p class="login-welcome-text">
                            Accede al sistema para gestionar pacientes, medicos y citas medicas de manera eficiente y segura.
                        </p>

                        <ul class="login-feature-list list-unstyled mb-0">
                            <li>
                                <i class="bi bi-calendar2-check"></i>
                                <div>
                                    <strong>Gestion de Citas</strong>
                                    <span>Agenda y administra citas medicas.</span>
                                </div>
                            </li>

                            <li>
                                <i class="bi bi-people"></i>
                                <div>
                                    <strong>Pacientes y Medicos</strong>
                                    <span>Registra y consulta informacion.</span>
                                </div>
                            </li>

                            <li>
                                <i class="bi bi-shield-lock"></i>
                                <div>
                                    <strong>Acceso Seguro</strong>
                                    <span>Protegemos tus datos.</span>
                                </div>
                            </li>
                        </ul>
                    </div>
                </aside>

                <section class="col-12 col-lg-7 login-panel-right">
                    <div class="login-form-wrap">
                        <h1 class="login-form-title">Iniciar Sesion</h1>
                        <p class="login-form-subtitle">Ingresa tus credenciales para continuar</p>

                        <!--
                            Fase 5.1:
                            Mensajes del login usando JSTL.
                            Se evita imprimir datos dinámicos con salidas directas JSP.
                        -->
                        <c:if test="${not empty param.logout}">
                            <div class="alert alert-success py-2">
                                Sesion cerrada correctamente.
                            </div>
                        </c:if>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger py-2">
                                <c:out value="${error}" />
                            </div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/login" class="login-form">
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Usuario</label>
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="bi bi-person"></i>
                                    </span>
                                    <input type="text"
                                           name="username"
                                           class="form-control"
                                           placeholder="Ingrese su usuario"
                                           required>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-semibold">Contrasena</label>
                                <div class="input-group">
                                    <span class="input-group-text">
                                        <i class="bi bi-lock"></i>
                                    </span>
                                    <input type="password"
                                           name="password"
                                           class="form-control"
                                           placeholder="Ingrese su contrasena"
                                           required>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-success w-100 login-submit-btn">
                                Ingresar
                            </button>
                        </form>

                        <p class="login-footer-note mb-0">NOVASALUD 2026</p>
                    </div>
                </section>

            </div>
        </div>
    </div>
</body>
</html>