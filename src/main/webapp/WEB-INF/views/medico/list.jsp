<%@page import="com.mycompany.miprimeraweb.model.Medico"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% request.setAttribute("pageTitle", "Medicos"); %>
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
            <span class="text-white small">Modulo: Medicos</span>
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
                    <h4 class="text-green fw-bold mb-0"><i class="bi bi-person-badge"></i> Listado de Medicos</h4>
                    <a href="${pageContext.request.contextPath}/medicos?accion=form" class="btn btn-success">
                        <i class="bi bi-plus-lg"></i> Nuevo
                    </a>
                </div>

                <div class="card mb-3 border-0 shadow-sm">
                    <div class="card-body">
                        <form method="get" action="${pageContext.request.contextPath}/medicos" class="row g-2">
                            <input type="hidden" name="accion" value="listar">
                            <div class="col-12 col-md-8">
                                <input type="text" class="form-control" name="q" placeholder="Buscar por medico o especialidad" value="${q}">
                            </div>
                            <div class="col-12 col-md-4 d-grid d-md-flex gap-2 justify-content-md-end">
                                <button type="submit" class="btn btn-success"><i class="bi bi-search"></i> Buscar</button>
                                <a href="${pageContext.request.contextPath}/medicos" class="btn btn-outline-success">Limpiar</a>
                            </div>
                        </form>
                    </div>
                </div>

                <% String msg = request.getParameter("msg"); %>
                <% if ("ok".equals(msg)) { %>
                    <div class="alert alert-success">Operacion realizada correctamente.</div>
                <% } else if ("deleted".equals(msg)) { %>
                    <div class="alert alert-warning">Medico eliminado correctamente.</div>
                <% } else if ("noexiste".equals(msg)) { %>
                    <div class="alert alert-info">El medico solicitado no existe.</div>
                <% } %>

                <div class="card border-0 shadow-sm">
                    <div class="card-body table-responsive">
                        <table class="table table-hover align-middle table-green">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Medico</th>
                                    <th>Especialidad</th>
                                    <th>Telefono</th>
                                    <th class="text-center">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% List<Medico> medicos = (List<Medico>) request.getAttribute("medicos"); %>
                                <% if (medicos == null || medicos.isEmpty()) { %>
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-4">No hay registros.</td>
                                    </tr>
                                <% } else { %>
                                    <% for (Medico m : medicos) { %>
                                        <tr>
                                            <td><%= m.getIdMedico() %></td>
                                            <td><%= m.getNombreCompleto() %></td>
                                            <td><%= m.getNombreEspecialidad() == null ? "" : m.getNombreEspecialidad() %></td>
                                            <td><%= m.getTelefono() == null ? "" : m.getTelefono() %></td>
                                            <td class="text-center">
                                                <a href="${pageContext.request.contextPath}/medicos?accion=editar&id=<%= m.getIdMedico() %>" class="btn btn-outline-success btn-sm">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/medicos?accion=eliminar&id=<%= m.getIdMedico() %>" class="btn btn-outline-danger btn-sm" onclick="return confirm('¿Eliminar medico?');">
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
