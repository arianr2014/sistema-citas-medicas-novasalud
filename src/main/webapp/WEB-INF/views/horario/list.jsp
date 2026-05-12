<%@page import="com.mycompany.miprimeraweb.model.Horario"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% request.setAttribute("pageTitle", "Horarios"); %>
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
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4 class="text-green fw-bold mb-0"><i class="bi bi-clock-history"></i> Listado de Horarios</h4>
                    <a href="${pageContext.request.contextPath}/horarios?accion=form" class="btn btn-success">
                        <i class="bi bi-plus-lg"></i> Nuevo
                    </a>
                </div>

                <div class="card mb-3 border-0 shadow-sm">
                    <div class="card-body">
                        <form method="get" action="${pageContext.request.contextPath}/horarios" class="row g-2">
                            <input type="hidden" name="accion" value="listar">
                            <div class="col-12 col-md-8">
                                <input type="text" class="form-control" name="q" placeholder="Buscar por medico, especialidad o dia" value="${q}">
                            </div>
                            <div class="col-12 col-md-4 d-grid d-md-flex gap-2 justify-content-md-end">
                                <button type="submit" class="btn btn-success"><i class="bi bi-search"></i> Buscar</button>
                                <a href="${pageContext.request.contextPath}/horarios" class="btn btn-outline-success">Limpiar</a>
                            </div>
                        </form>
                    </div>
                </div>

                <% String msg = request.getParameter("msg"); %>
                <% if ("ok".equals(msg)) { %>
                    <div class="alert alert-success">Operacion realizada correctamente.</div>
                <% } else if ("deleted".equals(msg)) { %>
                    <div class="alert alert-warning">Horario eliminado correctamente.</div>
                <% } else if ("noexiste".equals(msg)) { %>
                    <div class="alert alert-info">El horario solicitado no existe.</div>
                <% } %>

                <div class="card border-0 shadow-sm">
                    <div class="card-body table-responsive">
                        <table class="table table-hover align-middle table-green">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Medico</th>
                                    <th>Especialidad</th>
                                    <th>Dia</th>
                                    <th>Inicio</th>
                                    <th>Fin</th>
                                    <th class="text-center">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% List<Horario> horarios = (List<Horario>) request.getAttribute("horarios"); %>
                                <% if (horarios == null || horarios.isEmpty()) { %>
                                    <tr>
                                        <td colspan="7" class="text-center text-muted py-4">No hay registros.</td>
                                    </tr>
                                <% } else { %>
                                    <% for (Horario h : horarios) { %>
                                        <tr>
                                            <td><%= h.getIdHorario() %></td>
                                            <td><%= h.getMedicoNombreCompleto() == null ? "" : h.getMedicoNombreCompleto() %></td>
                                            <td><%= h.getEspecialidad() == null ? "" : h.getEspecialidad() %></td>
                                            <td class="text-capitalize"><%= h.getDia() == null ? "" : h.getDia() %></td>
                                            <td><%= h.getHoraInicio() == null ? "" : h.getHoraInicio() %></td>
                                            <td><%= h.getHoraFin() == null ? "" : h.getHoraFin() %></td>
                                            <td class="text-center">
                                                <a href="${pageContext.request.contextPath}/horarios?accion=editar&id=<%= h.getIdHorario() %>" class="btn btn-outline-success btn-sm">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/horarios?accion=eliminar&id=<%= h.getIdHorario() %>" class="btn btn-outline-danger btn-sm" onclick="return confirm('¿Eliminar horario?');">
                                                    <i class="bi bi-x-lg"></i>
                                                </a>
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
    <script>
        document.addEventListener("DOMContentLoaded", function () {
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
                const paginacion = document.getElementById("paginacion");
                const info       = document.getElementById("pag-info");
                const controles  = document.getElementById("pag-controles");
                paginacion.style.display = "flex";

                function mostrarPagina(pagina) {
                    paginaActual = pagina;
                    const inicio = (pagina - 1) * FILAS_POR_PAGINA;
                    const fin = inicio + FILAS_POR_PAGINA;

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
        });
    </script>
</body>
</html>
