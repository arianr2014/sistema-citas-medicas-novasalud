<%@page import="com.mycompany.miprimeraweb.model.Medico"%>
<%@page import="java.util.List"%>
<%@page import="com.mycompany.miprimeraweb.model.Horario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Horario horario = (Horario) request.getAttribute("horario");
    if (horario == null) {
        horario = new Horario();
    }
    boolean editando = horario.getIdHorario() > 0;
    List<Medico> medicos = (List<Medico>) request.getAttribute("medicos");
    request.setAttribute("pageTitle", (editando ? "Editar" : "Registrar") + " Horario");
%>
<!DOCTYPE html>
<html lang="es">
<%@ include file="/WEB-INF/views/layout/head.jspf" %>
<body class="bg-light">
    <nav class="navbar navbar-expand-lg navbar-dark bg-success shadow-sm">
        <div class="container-fluid px-4">
            <button class="btn btn-outline-light d-lg-none me-2 mobile-menu-toggle" type="button" data-bs-toggle="offcanvas" data-bs-target="#sidebarMenu" aria-controls="sidebarMenu" aria-label="Abrir menu">
                <i class="bi bi-list fs-4"></i>
            </button>
            <span class="navbar-brand fw-semibold">Sistema de Citas Medicas</span>
            <span class="text-white small">Modulo: Horarios</span>
        </div>
    </nav>

    <%@ include file="/WEB-INF/views/layout/menu-mobile.jspf" %>

    <main class="container-fluid py-4 px-4">
        <div class="row g-4">
            <div class="col-12 col-lg-3 order-1 order-lg-1">
                <%@ include file="/WEB-INF/views/layout/menu-right.jspf" %>
            </div>
            <div class="col-12 col-lg-9 order-2 order-lg-2">
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">
                            <i class="bi bi-clock"></i>
                            <%= editando ? "Editar Horario" : "Registrar Horario" %>
                        </h5>
                        <a href="${pageContext.request.contextPath}/horarios" class="btn btn-light btn-sm">
                            <i class="bi bi-arrow-left"></i> Volver
                        </a>
                    </div>
                    <div class="card-body">
                        <% if (request.getAttribute("error") != null) { %>
                            <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                        <% } %>

                        <form method="post" action="${pageContext.request.contextPath}/horarios" class="row g-3">
                            <input type="hidden" name="idHorario" value="<%= horario.getIdHorario() %>">

                            <div class="col-md-6">
                                <label class="form-label">Medico *</label>
                                <select class="form-select" name="idMedico" required>
                                    <option value="">Seleccione un medico</option>
                                    <% if (medicos != null) { %>
                                        <% for (Medico m : medicos) { %>
                                            <option value="<%= m.getIdMedico() %>" <%= horario.getIdMedico() == m.getIdMedico() ? "selected" : "" %>>
                                                <%= m.getNombreCompleto() %>
                                            </option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Dia *</label>
                                <select class="form-select" name="dia" required>
                                    <option value="">Seleccione un dia</option>
                                    <option value="lunes" <%= "lunes".equalsIgnoreCase(horario.getDia()) ? "selected" : "" %>>Lunes</option>
                                    <option value="martes" <%= "martes".equalsIgnoreCase(horario.getDia()) ? "selected" : "" %>>Martes</option>
                                    <option value="miercoles" <%= "miercoles".equalsIgnoreCase(horario.getDia()) ? "selected" : "" %>>Miercoles</option>
                                    <option value="jueves" <%= "jueves".equalsIgnoreCase(horario.getDia()) ? "selected" : "" %>>Jueves</option>
                                    <option value="viernes" <%= "viernes".equalsIgnoreCase(horario.getDia()) ? "selected" : "" %>>Viernes</option>
                                    <option value="sabado" <%= "sabado".equalsIgnoreCase(horario.getDia()) ? "selected" : "" %>>Sabado</option>
                                    <option value="domingo" <%= "domingo".equalsIgnoreCase(horario.getDia()) ? "selected" : "" %>>Domingo</option>
                                </select>
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Hora inicio *</label>
                                <input type="time" class="form-control" name="horaInicio" required value="<%= horario.getHoraInicio() == null ? "" : horario.getHoraInicio() %>">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Hora fin *</label>
                                <input type="time" class="form-control" name="horaFin" required value="<%= horario.getHoraFin() == null ? "" : horario.getHoraFin() %>">
                            </div>

                            <div class="col-12 d-flex justify-content-end gap-2">
                                <a href="${pageContext.request.contextPath}/horarios" class="btn btn-outline-secondary">Cancelar</a>
                                <button type="submit" class="btn btn-success"><i class="bi bi-save"></i> Guardar</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
