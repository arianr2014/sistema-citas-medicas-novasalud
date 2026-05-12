<%@page import="com.mycompany.miprimeraweb.model.Paciente"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Paciente paciente = (Paciente) request.getAttribute("paciente");
    if (paciente == null) {
        paciente = new Paciente();
    }
    boolean editando = paciente.getIdPaciente() > 0;
    request.setAttribute("pageTitle", (editando ? "Editar" : "Registrar") + " Paciente");
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
            <span class="text-white small">Modulo: Pacientes</span>
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
                            <i class="bi bi-person-plus"></i>
                            <%= editando ? "Editar Paciente" : "Registrar Paciente" %>
                        </h5>
                        <a href="${pageContext.request.contextPath}/pacientes" class="btn btn-light btn-sm">
                            <i class="bi bi-arrow-left"></i> Volver
                        </a>
                    </div>
                    <div class="card-body">
                        <% if (request.getAttribute("error") != null) { %>
                            <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                        <% } %>

                        <form method="post" action="${pageContext.request.contextPath}/pacientes" class="row g-3">
                            <input type="hidden" name="idPaciente" value="<%= paciente.getIdPaciente() %>">

                            <div class="col-md-4">
                                <label class="form-label">DNI *</label>
                                <input type="text" class="form-control" name="dni" maxlength="15" required value="<%= paciente.getDni() == null ? "" : paciente.getDni() %>">
                            </div>

                            <div class="col-md-4">
                                <label class="form-label">Nombres *</label>
                                <input type="text" class="form-control" name="nombres" maxlength="100" required value="<%= paciente.getNombres() == null ? "" : paciente.getNombres() %>">
                            </div>

                            <div class="col-md-4">
                                <label class="form-label">Apellidos *</label>
                                <input type="text" class="form-control" name="apellidos" maxlength="100" required value="<%= paciente.getApellidos() == null ? "" : paciente.getApellidos() %>">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Telefono</label>
                                <input type="text" class="form-control" name="telefono" maxlength="20" value="<%= paciente.getTelefono() == null ? "" : paciente.getTelefono() %>">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Direccion</label>
                                <input type="text" class="form-control" name="direccion" maxlength="255" value="<%= paciente.getDireccion() == null ? "" : paciente.getDireccion() %>">
                            </div>

                            <div class="col-12 d-flex justify-content-end gap-2">
                                <a href="${pageContext.request.contextPath}/pacientes" class="btn btn-outline-secondary">Cancelar</a>
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
