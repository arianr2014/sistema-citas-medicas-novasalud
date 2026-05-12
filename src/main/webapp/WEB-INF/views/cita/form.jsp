<%@page import="com.mycompany.miprimeraweb.model.Especialidad"%>
<%@page import="com.mycompany.miprimeraweb.model.Medico"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.mycompany.miprimeraweb.model.Paciente"%>
<%@page import="com.mycompany.miprimeraweb.model.Cita"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Cita cita = (Cita) request.getAttribute("cita");
    if (cita == null) {
        cita = new Cita();
        cita.setEstado("PROGRAMADA");
    }
    boolean editando = cita.getIdCita() > 0;

    Paciente paciente = (Paciente) request.getAttribute("pacienteEncontrado");
    String dniBusqueda = (String) request.getAttribute("dniBusqueda");
    if (dniBusqueda == null) {
        dniBusqueda = paciente == null ? "" : paciente.getDni();
    }

    Integer idEspecialidadSelObj = (Integer) request.getAttribute("idEspecialidadSel");
    int idEspecialidadSel = idEspecialidadSelObj == null ? 0 : idEspecialidadSelObj;

    Integer idMedicoSelObj = (Integer) request.getAttribute("idMedicoSel");
    int idMedicoSel = idMedicoSelObj == null ? cita.getIdMedico() : idMedicoSelObj;

    String fechaSel = (String) request.getAttribute("fechaSel");
    if (fechaSel == null || fechaSel.isBlank()) {
        fechaSel = cita.getFecha() == null ? "" : cita.getFecha();
    }

    String horaSel = (String) request.getAttribute("horaSel");
    if (horaSel == null || horaSel.isBlank()) {
        horaSel = cita.getHora() == null ? "" : cita.getHora();
    }

    List<Especialidad> especialidades = (List<Especialidad>) request.getAttribute("especialidades");
    List<Medico> medicos = (List<Medico>) request.getAttribute("medicos");
    List<String> horariosDisponibles = (List<String>) request.getAttribute("horariosDisponibles");
    if (horariosDisponibles == null) {
        horariosDisponibles = new ArrayList<>();
    }

    boolean horaActualIncluida = horaSel != null && !horaSel.isBlank() && horariosDisponibles.contains(horaSel);

    boolean filtrosCompletos = idMedicoSel > 0 && fechaSel != null && !fechaSel.isBlank();
    boolean sinHorarios = filtrosCompletos && horariosDisponibles.isEmpty();
    request.setAttribute("pageTitle", (editando ? "Editar" : "Registrar") + " Cita");
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
            <span class="text-white small">Modulo: Citas</span>
        </div>
    </nav>

    <%@ include file="/WEB-INF/views/layout/menu-mobile.jspf" %>

    <main class="container-fluid py-4 px-4">
        <div class="row g-4">
            <div class="col-12 col-lg-3 order-1 order-lg-1">
                <%@ include file="/WEB-INF/views/layout/menu-right.jspf" %>
            </div>
            <div class="col-12 col-lg-9 order-2 order-lg-2">
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">
                            <i class="bi bi-calendar-plus"></i>
                            <%= editando ? "Editar Cita" : "Registrar Cita" %>
                        </h5>
                        <a href="${pageContext.request.contextPath}/citas" class="btn btn-light btn-sm">
                            <i class="bi bi-arrow-left"></i> Volver
                        </a>
                    </div>
                    <div class="card-body">
                        <% if (request.getAttribute("error") != null) { %>
                            <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                        <% } %>

                        <form method="get" action="${pageContext.request.contextPath}/citas" class="row g-2 mb-3">
                            <input type="hidden" name="accion" value="form">
                            <% if (editando) { %>
                                <input type="hidden" name="id" value="<%= cita.getIdCita() %>">
                            <% } %>
                            <input type="hidden" name="idEspecialidadSel" value="<%= idEspecialidadSel %>">

                            <div class="col-md-8">
                                <label class="form-label">Buscar paciente por DNI</label>
                                <input type="text" class="form-control" name="dniBusqueda" value="<%= dniBusqueda %>" placeholder="Ingrese DNI">
                            </div>
                            <div class="col-md-4 d-grid align-content-end">
                                <button type="submit" class="btn btn-outline-success mt-md-4"><i class="bi bi-search"></i> Buscar paciente</button>
                            </div>
                        </form>

                        <div class="border rounded p-3 mb-3 bg-white">
                            <h6 class="text-green mb-2">Datos del paciente</h6>
                            <% if (paciente != null) { %>
                                <input type="hidden" form="formCita" name="idPaciente" value="<%= paciente.getIdPaciente() %>">
                                <div><strong>DNI:</strong> <%= paciente.getDni() %></div>
                                <div><strong>Nombre:</strong> <%= paciente.getNombres() %> <%= paciente.getApellidos() %></div>
                                <div><strong>Telefono:</strong> <%= paciente.getTelefono() == null ? "" : paciente.getTelefono() %></div>
                                <div><strong>Direccion:</strong> <%= paciente.getDireccion() == null ? "" : paciente.getDireccion() %></div>
                            <% } else { %>
                                <div class="text-muted">No hay paciente seleccionado. Busque por DNI.</div>
                            <% } %>
                        </div>

                        <form id="formEspecialidad" method="get" action="${pageContext.request.contextPath}/citas" class="row g-2 mb-3">
                            <input type="hidden" name="accion" value="form">
                            <% if (editando) { %>
                                <input type="hidden" name="id" value="<%= cita.getIdCita() %>">
                            <% } %>
                            <input type="hidden" name="dniBusqueda" value="<%= dniBusqueda %>">
                            <input type="hidden" name="idMedicoSel" id="idMedicoSelHelper" value="<%= idMedicoSel %>">
                            <input type="hidden" name="fechaSel" id="fechaSelHelper" value="<%= fechaSel %>">
                            <input type="hidden" name="horaSel" id="horaSelHelper" value="<%= horaSel %>">

                            <div class="col-md-12">
                                <label class="form-label">Especialidad</label>
                                <select class="form-select" name="idEspecialidadSel" required onchange="this.form.submit()">
                                    <option value="">Seleccione especialidad</option>
                                    <% if (especialidades != null) { %>
                                        <% for (Especialidad e : especialidades) { %>
                                            <option value="<%= e.getIdEspecialidad() %>" <%= idEspecialidadSel == e.getIdEspecialidad() ? "selected" : "" %>>
                                                <%= e.getNombre() %>
                                            </option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>
                        </form>

                        <form id="formCita" method="post" action="${pageContext.request.contextPath}/citas" class="row g-3">
                            <input type="hidden" name="idCita" value="<%= cita.getIdCita() %>">
                            <input type="hidden" name="dniBusqueda" value="<%= dniBusqueda %>">
                            <input type="hidden" name="idEspecialidadSel" value="<%= idEspecialidadSel %>">
                            <% if (paciente != null) { %>
                                <input type="hidden" name="idPaciente" value="<%= paciente.getIdPaciente() %>">
                            <% } else { %>
                                <input type="hidden" name="idPaciente" value="0">
                            <% } %>

                            <div class="col-md-6">
                                <label class="form-label">Medico *</label>
                                <select class="form-select" id="idMedico" name="idMedico" required onchange="recargarHorarios()">
                                    <option value="">Seleccione medico</option>
                                    <% if (medicos != null) { %>
                                        <% for (Medico m : medicos) { %>
                                            <option value="<%= m.getIdMedico() %>" <%= idMedicoSel == m.getIdMedico() ? "selected" : "" %>>
                                                <%= m.getNombreCompleto() %>
                                            </option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="col-md-3">
                                <label class="form-label">Fecha *</label>
                                <input type="date" class="form-control" id="fecha" name="fecha" required value="<%= fechaSel %>" onchange="recargarHorarios()">
                            </div>

                            <div class="col-md-3">
                                <label class="form-label">Hora *</label>
                                <select class="form-select" id="hora" name="hora" required>
                                    <option value="">Seleccione hora</option>
                                    <% if (!horaActualIncluida && horaSel != null && !horaSel.isBlank()) { %>
                                        <option value="<%= horaSel %>" selected><%= horaSel %> (actual)</option>
                                    <% } %>
                                    <% for (String h : horariosDisponibles) { %>
                                        <option value="<%= h %>" <%= h.equals(horaSel) ? "selected" : "" %>><%= h %></option>
                                    <% } %>
                                </select>
                                <% if (!filtrosCompletos) { %>
                                    <div class="form-text">Seleccione medico y fecha para ver horarios disponibles.</div>
                                <% } else if (sinHorarios) { %>
                                    <div class="text-danger small mt-1">No hay horarios disponibles para ese medico en esa fecha.</div>
                                <% } else { %>
                                    <div class="text-success small mt-1">Se muestran solo horas validas y disponibles.</div>
                                <% } %>
                            </div>

                            <div class="col-md-4">
                                <label class="form-label">Estado *</label>
                                <select class="form-select" name="estado" required>
                                    <option value="PROGRAMADA" <%= "PROGRAMADA".equalsIgnoreCase(cita.getEstado()) ? "selected" : "" %>>PROGRAMADA</option>
                                    <option value="ATENDIDA" <%= "ATENDIDA".equalsIgnoreCase(cita.getEstado()) ? "selected" : "" %>>ATENDIDA</option>
                                    <option value="CANCELADA" <%= "CANCELADA".equalsIgnoreCase(cita.getEstado()) ? "selected" : "" %>>CANCELADA</option>
                                </select>
                            </div>

                            <div class="col-md-8">
                                <label class="form-label">Observaciones</label>
                                <input type="text" class="form-control" name="observaciones" maxlength="255" value="<%= cita.getObservaciones() == null ? "" : cita.getObservaciones() %>">
                            </div>

                            <div class="col-12 d-flex justify-content-end gap-2">
                                <a href="${pageContext.request.contextPath}/citas" class="btn btn-outline-secondary">Cancelar</a>
                                <button type="submit" class="btn btn-success" <%= sinHorarios ? "disabled" : "" %>><i class="bi bi-save"></i> Guardar</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <script>
        function recargarHorarios() {
            const medico = document.getElementById("idMedico").value;
            const fecha = document.getElementById("fecha").value;
            const hora = document.getElementById("hora").value;

            document.getElementById("idMedicoSelHelper").value = medico;
            document.getElementById("fechaSelHelper").value = fecha;
            document.getElementById("horaSelHelper").value = hora;

            document.getElementById("formEspecialidad").submit();
        }
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
