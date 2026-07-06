<%@page import="com.mycompany.miprimeraweb.model.Cita"%>
<%@page import="com.mycompany.miprimeraweb.model.Especialidad"%>
<%@page import="com.mycompany.miprimeraweb.model.Medico"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% request.setAttribute("pageTitle", "Citas"); %>
<!DOCTYPE html>
<html lang="es">
<%@ include file="/WEB-INF/views/layout/head.jspf" %>
<body class="bg-light">
    
    <nav class="navbar navbar-expand-lg navbar-dark bg-success shadow-sm">
        <div class="container-fluid px-4">

            <button class="btn btn-outline-light d-lg-none me-2 mobile-menu-toggle"
                    type="button"
                    data-bs-toggle="offcanvas"
                    data-bs-target="#sidebarMenu"
                    aria-controls="sidebarMenu"
                    aria-label="Abrir menu">
                <i class="bi bi-list fs-4"></i>
            </button>

            <span class="navbar-brand fw-semibold">Sistema de Citas Medicas</span>

            <!--
                Fase 4:
                Se muestra el módulo actual, el usuario autenticado y el rol activo.
            -->
            <div class="d-flex flex-column flex-md-row align-items-md-center gap-2 ms-auto">
                <span class="text-white small">Modulo: Citas</span>
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
                    <h4 class="text-green fw-bold mb-0"><i class="bi bi-calendar-check"></i> Listado de Citas</h4>
                    <a href="${pageContext.request.contextPath}/citas?accion=form" class="btn btn-success">
                        <i class="bi bi-plus-lg"></i> Nuevo
                    </a>
                </div>

                <div class="card mb-3 border-0 shadow-sm">
                    <div class="card-body">
                        <form method="get" action="${pageContext.request.contextPath}/citas" class="row g-2 align-items-end">
                            <input type="hidden" name="accion" value="listar">
                            <input type="hidden" name="buscar" value="1">
                            <div class="col-12 col-md-4">
                                <label class="form-label mb-1">DNI del paciente</label>
                                <input type="text" class="form-control" name="dniPaciente" placeholder="Ingrese DNI" value="${dniPaciente}">
                            </div>
                            <div class="col-12 col-md-4">
                                <label class="form-label mb-1">Medico</label>
                                <select name="idMedico" class="form-select">
                                    <option value="0">Todos</option>
                                    <% List<Medico> medicosFiltro = (List<Medico>) request.getAttribute("medicos"); %>
                                    <% int idMedicoSel = request.getAttribute("idMedico") == null ? 0 : (Integer) request.getAttribute("idMedico"); %>
                                    <% if (medicosFiltro != null) {
                                           for (Medico m : medicosFiltro) {
                                    %>
                                        <option value="<%= m.getIdMedico() %>" <%= idMedicoSel == m.getIdMedico() ? "selected" : "" %>>
                                            <%= m.getNombres() %> <%= m.getApellidos() %>
                                        </option>
                                    <%   }
                                       } %>
                                </select>
                            </div>
                            <div class="col-12 col-md-4">
                                <label class="form-label mb-1">Especialidad</label>
                                <select name="idEspecialidad" class="form-select">
                                    <option value="0">Todas</option>
                                    <% List<Especialidad> especialidadesFiltro = (List<Especialidad>) request.getAttribute("especialidades"); %>
                                    <% int idEspecialidadSel = request.getAttribute("idEspecialidad") == null ? 0 : (Integer) request.getAttribute("idEspecialidad"); %>
                                    <% if (especialidadesFiltro != null) {
                                           for (Especialidad e : especialidadesFiltro) {
                                    %>
                                        <option value="<%= e.getIdEspecialidad() %>" <%= idEspecialidadSel == e.getIdEspecialidad() ? "selected" : "" %>>
                                            <%= e.getNombre() %>
                                        </option>
                                    <%   }
                                       } %>
                                </select>
                            </div>
                            <div class="col-12 d-grid d-md-flex gap-2 justify-content-md-end">
                                <button type="submit" class="btn btn-success"><i class="bi bi-search"></i> Buscar</button>
                                <a href="${pageContext.request.contextPath}/citas" class="btn btn-outline-success">Limpiar</a>
                            </div>
                        </form>
                    </div>
                </div>

                <% String msg = request.getParameter("msg"); %>
                <% if ("ok".equals(msg)) { %>
                    <div class="alert alert-success">Operacion realizada correctamente.</div>
                <% } else if ("deleted".equals(msg)) { %>
                    <div class="alert alert-warning">Cita anulada correctamente.</div>
                <% } else if ("attended".equals(msg)) { %>
                    <div class="alert alert-success">La cita fue marcada como ATENDIDA.</div>
                <% } else if ("nochange".equals(msg)) { %>
                    <div class="alert alert-info">No se pudo cambiar el estado. Verifique si la cita ya fue atendida o cancelada.</div>
                <% } else if ("noexiste".equals(msg)) { %>
                    <div class="alert alert-info">La cita solicitada no existe.</div>
                <% } else if ("readonly".equals(msg)) { %>
                    <div class="alert alert-info">La cita ya fue atendida y no puede editarse.</div>
                <% } else if ("invalid".equals(msg)) { %>
                    <div class="alert alert-danger">Solicitud invalida.</div>
                <% } else if ("metodo_invalido".equals(msg)) { %>
                    <div class="alert alert-danger">Operacion no permitida por este metodo.</div>
                <% } else if ("errorDelete".equals(msg)) { %>
                    <div class="alert alert-danger">No se pudo anular la cita.</div>
                <% } else if ("errorUpdate".equals(msg)) { %>
                    <div class="alert alert-danger">No se pudo actualizar el estado de la cita.</div>
                <% } %>

                <div class="card border-0 shadow-sm">
                    <div class="card-body table-responsive">
                        <table class="table table-hover align-middle table-green">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>DNI</th>
                                    <th>Paciente</th>
                                    <th>Medico</th>
                                    <th>Especialidad</th>
                                    <th>Fecha</th>
                                    <th>Hora</th>
                                    <th>Estado</th>
                                    <th class="text-center">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% List<Cita> citas = (List<Cita>) request.getAttribute("citas"); %>
                                <% boolean buscar = Boolean.TRUE.equals(request.getAttribute("buscar")); %>
                                <% if (!buscar) { %>
                                    <tr>
                                        <td colspan="9" class="text-center text-muted py-4">Seleccione filtros y presione Buscar para ver resultados.</td>
                                    </tr>
                                <% } else if (citas == null || citas.isEmpty()) { %>
                                    <tr>
                                        <td colspan="9" class="text-center text-muted py-4">No hay registros.</td>
                                    </tr>
                                <% } else { %>
                                    <% for (Cita c : citas) { %>
                                        <% boolean esAtendida = "ATENDIDA".equalsIgnoreCase(c.getEstado()); %>
                                        <tr>
                                            <td><%= c.getIdCita() %></td>
                                            <td><%= c.getPacienteDni() == null ? "" : c.getPacienteDni() %></td>
                                            <td><%= c.getPacienteNombreCompleto() == null ? "" : c.getPacienteNombreCompleto() %></td>
                                            <td><%= c.getMedicoNombreCompleto() == null ? "" : c.getMedicoNombreCompleto() %></td>
                                            <td><%= c.getEspecialidad() == null ? "" : c.getEspecialidad() %></td>
                                            <td><%= c.getFecha() == null ? "" : c.getFecha() %></td>
                                            <td><%= c.getHora() == null ? "" : c.getHora() %></td>
                                            <td>
                                                <span class="badge <%= "CANCELADA".equalsIgnoreCase(c.getEstado()) ? "bg-secondary" : ("ATENDIDA".equalsIgnoreCase(c.getEstado()) ? "bg-primary" : "bg-success") %>">
                                                    <%= c.getEstado() == null ? "" : c.getEstado() %>
                                                </span>
                                            </td>
                                            
                                            <td class="text-center">
                                            <% if (!esAtendida) { %>
                                                <!--
                                                    Fase 3:
                                                    Marcar como ATENDIDA ya no se ejecuta por enlace GET.
                                                    Ahora se envía mediante formulario POST.
                                                -->
                                                <form action="${pageContext.request.contextPath}/citas"
                                                      method="post"
                                                      class="d-inline js-form-atender">
                                                    <input type="hidden" name="accion" value="atender">
                                                    <input type="hidden" name="id" value="<%= c.getIdCita() %>">

                                                    <button type="submit"
                                                            class="btn btn-outline-success btn-sm"
                                                            title="Atender cita">
                                                        <i class="bi bi-check-lg"></i>
                                                    </button>
                                                </form>

                                                <a href="${pageContext.request.contextPath}/citas?accion=editar&id=<%= c.getIdCita() %>"
                                                   class="btn btn-outline-success btn-sm"
                                                   title="Editar cita">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                            <% } %>

                                            <!--
                                                Fase 3:
                                                Anular cita ya no se ejecuta por enlace GET.
                                                Ahora se envía mediante formulario POST.
                                            -->
                                            <form action="${pageContext.request.contextPath}/citas"
                                                  method="post"
                                                  class="d-inline js-form-anular">
                                                <input type="hidden" name="accion" value="eliminar">
                                                <input type="hidden" name="id" value="<%= c.getIdCita() %>">

                                                <button type="submit"
                                                        class="btn btn-outline-danger btn-sm"
                                                        title="Anular cita">
                                                    <i class="bi bi-x-lg"></i>
                                                </button>
                                            </form>
                                        </td>
                                        </tr>
                                    <% } %>
                                <% } %>
                            </tbody>
                        </table>

                        <nav id="paginacion" class="mt-3 d-flex flex-column align-items-center gap-2" style="display:none">
                            <span id="pag-info" class="text-muted small text-center"></span>
                            <ul class="pagination pagination-sm mb-0 justify-content-center" id="pag-controles"></ul>
                        </nav>
                    </div>
                </div>

            </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>
        document.addEventListener("DOMContentLoaded", function () {

            /* ── Paginado de 10 filas ── */
            (function () {
                const FILAS_POR_PAGINA = 10;
                const tbody = document.querySelector(".table tbody");
                if (!tbody) return;

                const filas = Array.from(tbody.querySelectorAll("tr")).filter(function (tr) {
                    return tr.querySelectorAll("td").length > 1;
                });

                if (filas.length === 0) return;

                const totalPaginas = Math.ceil(filas.length / FILAS_POR_PAGINA);
                let paginaActual = 1;

                const paginacion  = document.getElementById("paginacion");
                const info        = document.getElementById("pag-info");
                const controles   = document.getElementById("pag-controles");
                paginacion.style.display = "flex";

                function mostrarPagina(pagina) {
                    paginaActual = pagina;
                    const inicio = (pagina - 1) * FILAS_POR_PAGINA;
                    const fin    = inicio + FILAS_POR_PAGINA;

                    filas.forEach(function (tr, i) {
                        tr.style.display = (i >= inicio && i < fin) ? "" : "none";
                    });

                    info.textContent = "Mostrando " + (inicio + 1) + " - " + Math.min(fin, filas.length) + " de " + filas.length + " registros";
                    renderControles();
                }

                function renderControles() {
                    controles.innerHTML = "";

                    var prev = document.createElement("li");
                    prev.className = "page-item" + (paginaActual === 1 ? " disabled" : "");
                    prev.innerHTML = '<a class="page-link" href="#">&laquo;</a>';
                    prev.addEventListener("click", function (e) { e.preventDefault(); if (paginaActual > 1) mostrarPagina(paginaActual - 1); });
                    controles.appendChild(prev);

                    for (var p = 1; p <= totalPaginas; p++) {
                        (function (pg) {
                            var li = document.createElement("li");
                            li.className = "page-item" + (pg === paginaActual ? " active" : "");
                            li.innerHTML = '<a class="page-link" href="#">' + pg + '</a>';
                            li.addEventListener("click", function (e) { e.preventDefault(); mostrarPagina(pg); });
                            controles.appendChild(li);
                        })(p);
                    }

                    var next = document.createElement("li");
                    next.className = "page-item" + (paginaActual === totalPaginas ? " disabled" : "");
                    next.innerHTML = '<a class="page-link" href="#">&raquo;</a>';
                    next.addEventListener("click", function (e) { e.preventDefault(); if (paginaActual < totalPaginas) mostrarPagina(paginaActual + 1); });
                    controles.appendChild(next);
                }

                mostrarPagina(1);
            })();

            /* ── SweetAlert2 confirmaciones ──
                Fase 3:
                Las confirmaciones ya no redirigen por URL.
                Ahora confirman y envían formularios POST.
             */
             document.querySelectorAll(".js-form-atender").forEach(function (form) {
                 form.addEventListener("submit", function (event) {
                     event.preventDefault();

                     Swal.fire({
                         title: "Marcar como ATENDIDA",
                         text: "La cita cambiara a estado ATENDIDA.",
                         icon: "question",
                         showCancelButton: true,
                         confirmButtonText: "Si, atender",
                         cancelButtonText: "Cancelar",
                         confirmButtonColor: "#198754"
                     }).then(function (result) {
                         if (result.isConfirmed) {
                             form.submit();
                         }
                     });
                 });
             });

             document.querySelectorAll(".js-form-anular").forEach(function (form) {
                 form.addEventListener("submit", function (event) {
                     event.preventDefault();

                     Swal.fire({
                         title: "Anular cita",
                         text: "Esta accion anulara la cita seleccionada.",
                         icon: "warning",
                         showCancelButton: true,
                         confirmButtonText: "Si, anular",
                         cancelButtonText: "Cancelar",
                         confirmButtonColor: "#dc3545"
                     }).then(function (result) {
                         if (result.isConfirmed) {
                             form.submit();
                         }
                     });
                 });
             });
        });
    </script>
</body>
</html>
