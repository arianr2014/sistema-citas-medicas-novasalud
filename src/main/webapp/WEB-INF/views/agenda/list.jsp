<%@page import="com.mycompany.miprimeraweb.model.Cita"%>
<%@page import="com.mycompany.miprimeraweb.model.Medico"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% request.setAttribute("pageTitle", "Agenda Medica"); %>
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
            <div class="d-flex flex-column flex-md-row align-items-md-center gap-2 ms-auto">
                <span class="text-white small">Modulo: Agenda Medica</span>
                <%@ include file="/WEB-INF/views/layout/usuario-sesion.jspf" %>
            </div>
            
        </div>
    </nav>

    <%@ include file="/WEB-INF/views/layout/menu-mobile.jspf" %>

    <main class="container-fluid py-4 px-4">
        <div class="row g-4">
            <div class="col-12 col-lg-3 order-1 order-lg-1">
                <%@ include file="/WEB-INF/views/layout/menu-right.jspf" %>
            </div>
            <div class="col-12 col-lg-9 order-2 order-lg-2">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4 class="text-green fw-bold mb-0"><i class="bi bi-journal-medical"></i> Agenda de Citas Programadas</h4>
                </div>

                <div class="card mb-3 border-0 shadow-sm">
                    <div class="card-body">
                        <form method="get" action="${pageContext.request.contextPath}/agenda-medico" class="row g-2 align-items-end">
                            <input type="hidden" name="buscar" value="1">

                            <div class="col-12 col-md-6">
                                <label class="form-label mb-1">Medico</label>
                                <select name="idMedico" class="form-select" required>
                                    <option value="0">Seleccione un medico</option>
                                    <% List<Medico> medicos = (List<Medico>) request.getAttribute("medicos"); %>
                                    <% int idMedicoSel = request.getAttribute("idMedico") == null ? 0 : (Integer) request.getAttribute("idMedico"); %>
                                    <% if (medicos != null) { %>
                                        <% for (Medico m : medicos) { %>
                                            <option value="<%= m.getIdMedico() %>" <%= idMedicoSel == m.getIdMedico() ? "selected" : "" %>>
                                                <%= m.getNombres() %> <%= m.getApellidos() %>
                                            </option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="col-12 col-md-4">
                                <label class="form-label mb-1">Fecha</label>
                                <input type="date" class="form-control" name="fecha" required value="${fecha}">
                            </div>

                            <div class="col-12 col-md-2 d-grid">
                                <button type="submit" class="btn btn-success"><i class="bi bi-search"></i> Ver</button>
                            </div>
                        </form>
                    </div>
                </div>

                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                <% } %>

                <div class="card border-0 shadow-sm">
                    <div class="card-body table-responsive">
                        <table class="table table-hover align-middle table-green">
                            <thead>
                                <tr>
                                    <th>Hora</th>
                                    <th>DNI Paciente</th>
                                    <th>Paciente</th>
                                    <th>Especialidad</th>
                                    <th>Observaciones</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% boolean buscar = Boolean.TRUE.equals(request.getAttribute("buscar")); %>
                                <% List<Cita> citas = (List<Cita>) request.getAttribute("citas"); %>
                                <% if (!buscar) { %>
                                    <tr>
                                        <td colspan="6" class="text-center text-muted py-4">Seleccione medico y fecha para consultar la agenda.</td>
                                    </tr>
                                <% } else if (citas == null || citas.isEmpty()) { %>
                                    <tr>
                                        <td colspan="6" class="text-center text-muted py-4">No hay citas programadas para la fecha seleccionada.</td>
                                    </tr>
                                <% } else { %>
                                    <% for (Cita c : citas) { %>
                                        <tr>
                                            <td><%= c.getHora() == null ? "" : c.getHora() %></td>
                                            <td><%= c.getPacienteDni() == null ? "" : c.getPacienteDni() %></td>
                                            <td><%= c.getPacienteNombreCompleto() == null ? "" : c.getPacienteNombreCompleto() %></td>
                                            <td><%= c.getEspecialidad() == null ? "" : c.getEspecialidad() %></td>
                                            <td><%= c.getObservaciones() == null ? "" : c.getObservaciones() %></td>
                                            <td><span class="badge bg-success"><%= c.getEstado() == null ? "" : c.getEstado() %></span></td>
                                        </tr>
                                    <% } %>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
