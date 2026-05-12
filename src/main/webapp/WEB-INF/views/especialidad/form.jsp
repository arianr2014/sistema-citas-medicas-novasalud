<%@page import="com.mycompany.miprimeraweb.model.Especialidad"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Especialidad especialidad = (Especialidad) request.getAttribute("especialidad");
    if (especialidad == null) {
        especialidad = new Especialidad();
    }
    boolean editando = especialidad.getIdEspecialidad() > 0;
    request.setAttribute("pageTitle", (editando ? "Editar" : "Registrar") + " Especialidad");
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
            <span class="text-white small">Modulo: Especialidades</span>
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
                            <i class="bi bi-tags"></i>
                            <%= editando ? "Editar Especialidad" : "Registrar Especialidad" %>
                        </h5>
                        <a href="${pageContext.request.contextPath}/especialidades" class="btn btn-light btn-sm">
                            <i class="bi bi-arrow-left"></i> Volver
                        </a>
                    </div>
                    <div class="card-body">
                        <% if (request.getAttribute("error") != null) { %>
                            <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                        <% } %>

                        <form method="post" action="${pageContext.request.contextPath}/especialidades" class="row g-3">
                            <input type="hidden" name="idEspecialidad" value="<%= especialidad.getIdEspecialidad() %>">

                            <div class="col-md-6">
                                <label class="form-label">Nombre *</label>
                                <input type="text" class="form-control" name="nombre" maxlength="100" required value="<%= especialidad.getNombre() == null ? "" : especialidad.getNombre() %>">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Descripcion</label>
                                <textarea class="form-control" name="descripcion" maxlength="255" rows="2"><%= especialidad.getDescripcion() == null ? "" : especialidad.getDescripcion() %></textarea>
                            </div>

                            <div class="col-12 d-flex justify-content-end gap-2">
                                <a href="${pageContext.request.contextPath}/especialidades" class="btn btn-outline-secondary">Cancelar</a>
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
