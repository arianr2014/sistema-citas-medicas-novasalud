<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<c:set var="pageTitle" value="Citas" />
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
                    <h4 class="text-green fw-bold mb-0">
                        <i class="bi bi-calendar-check"></i> Listado de Citas
                    </h4>

                    <a href="${pageContext.request.contextPath}/citas?accion=form"
                       class="btn btn-success">
                        <i class="bi bi-plus-lg"></i> Nuevo
                    </a>
                </div>

                <!--
                    Fase 5:
                    Se escapan los valores que regresan al formulario de filtros.
                    Esto evita que texto malicioso quede reflejado en la vista.
                -->
                <div class="card mb-3 border-0 shadow-sm">
                    <div class="card-body">
                        <form method="get"
                              action="${pageContext.request.contextPath}/citas"
                              class="row g-2 align-items-end">

                            <input type="hidden" name="accion" value="listar">
                            <input type="hidden" name="buscar" value="1">

                            <div class="col-12 col-md-4">
                                <label class="form-label mb-1">DNI del paciente</label>
                                <input type="text"
                                       class="form-control"
                                       name="dniPaciente"
                                       placeholder="Ingrese DNI"
                                       value="${fn:escapeXml(dniPaciente)}">
                            </div>

                            <div class="col-12 col-md-4">
                                <label class="form-label mb-1">Medico</label>

                                <select name="idMedico" class="form-select">
                                    <option value="0">Todos</option>

                                    <c:forEach var="m" items="${medicos}">
                                        <option value="${m.idMedico}" ${idMedico == m.idMedico ? 'selected' : ''}>
                                            <c:out value="${m.nombres}" /> <c:out value="${m.apellidos}" />
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-12 col-md-4">
                                <label class="form-label mb-1">Especialidad</label>

                                <select name="idEspecialidad" class="form-select">
                                    <option value="0">Todas</option>

                                    <c:forEach var="e" items="${especialidades}">
                                        <option value="${e.idEspecialidad}" ${idEspecialidad == e.idEspecialidad ? 'selected' : ''}>
                                            <c:out value="${e.nombre}" />
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-12 d-grid d-md-flex gap-2 justify-content-md-end">
                                <button type="submit" class="btn btn-success">
                                    <i class="bi bi-search"></i> Buscar
                                </button>

                                <a href="${pageContext.request.contextPath}/citas"
                                   class="btn btn-outline-success">
                                    Limpiar
                                </a>
                            </div>
                        </form>
                    </div>
                </div>

                
                    <!--
                        Mensajes funcionales del módulo Citas.
                        Mejora Fase 5:
                        Se muestran mensajes más específicos y profesionales según la operación realizada.
                    -->
                    <c:choose>
                        <c:when test="${param.msg == 'created'}">
                            <div class="alert alert-success">
                                <i class="bi bi-check-circle"></i>
                                <strong>Cita registrada correctamente.</strong>
                                La cita ya fue programada en el sistema.
                            </div>
                        </c:when>

                        <c:when test="${param.msg == 'updated'}">
                            <div class="alert alert-success">
                                <i class="bi bi-check-circle"></i>
                                <strong>Cita actualizada correctamente.</strong>
                                Los cambios fueron guardados en el sistema.
                            </div>
                        </c:when>

                        <c:when test="${param.msg == 'ok'}">
                            <div class="alert alert-success">
                                <i class="bi bi-check-circle"></i>
                                <strong>Operacion realizada correctamente.</strong>
                            </div>
                        </c:when>

                        <c:when test="${param.msg == 'deleted'}">
                            <div class="alert alert-warning">
                                <i class="bi bi-exclamation-triangle"></i>
                                <strong>Cita anulada correctamente.</strong>
                                La cita quedó registrada como cancelada.
                            </div>
                        </c:when>

                        <c:when test="${param.msg == 'attended'}">
                            <div class="alert alert-success">
                                <i class="bi bi-check2-square"></i>
                                <strong>Cita marcada como ATENDIDA correctamente.</strong>
                                El estado de atención fue actualizado.
                            </div>
                        </c:when>

                        <c:when test="${param.msg == 'nochange'}">
                            <div class="alert alert-info">
                                <i class="bi bi-info-circle"></i>
                                <strong>No se pudo cambiar el estado.</strong>
                                Verifique si la cita ya fue atendida o cancelada.
                            </div>
                        </c:when>

                        <c:when test="${param.msg == 'noexiste'}">
                            <div class="alert alert-info">
                                <i class="bi bi-info-circle"></i>
                                <strong>La cita solicitada no existe.</strong>
                                Es posible que ya no se encuentre activa en el sistema.
                            </div>
                        </c:when>

                        <c:when test="${param.msg == 'readonly'}">
                            <div class="alert alert-info">
                                <i class="bi bi-lock"></i>
                                <strong>La cita ya fue atendida.</strong>
                                Por seguridad, no puede editarse.
                            </div>
                        </c:when>

                        <c:when test="${param.msg == 'invalid'}">
                            <div class="alert alert-danger">
                                <i class="bi bi-x-circle"></i>
                                <strong>Solicitud invalida.</strong>
                                Verifique los datos enviados.
                            </div>
                        </c:when>

                        <c:when test="${param.msg == 'metodo_invalido'}">
                            <div class="alert alert-danger">
                                <i class="bi bi-shield-lock"></i>
                                <strong>Operacion no permitida.</strong>
                                Esta acción no puede ejecutarse mediante este método.
                            </div>
                        </c:when>

                        <c:when test="${param.msg == 'errorDelete'}">
                            <div class="alert alert-danger">
                                <i class="bi bi-x-circle"></i>
                                <strong>No se pudo anular la cita.</strong>
                                Intente nuevamente o revise el estado actual de la cita.
                            </div>
                        </c:when>

                        <c:when test="${param.msg == 'errorUpdate'}">
                            <div class="alert alert-danger">
                                <i class="bi bi-x-circle"></i>
                                <strong>No se pudo actualizar el estado de la cita.</strong>
                                Intente nuevamente.
                            </div>
                        </c:when>
                    </c:choose>

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
                                <c:choose>

                                    <c:when test="${buscar != true}">
                                        <tr>
                                            <td colspan="9" class="text-center text-muted py-4">
                                                Seleccione filtros y presione Buscar para ver resultados.
                                            </td>
                                        </tr>
                                    </c:when>

                                    <c:when test="${empty citas}">
                                        <tr>
                                            <td colspan="9" class="text-center text-muted py-4">
                                                No hay registros.
                                            </td>
                                        </tr>
                                    </c:when>

                                    <c:otherwise>
                                        <c:forEach var="cita" items="${citas}">

                                            <c:set var="estadoCita" value="${fn:toUpperCase(cita.estado)}" />

                                            <c:set var="claseEstado" value="bg-success" />

                                            <c:if test="${estadoCita == 'CANCELADA'}">
                                                <c:set var="claseEstado" value="bg-secondary" />
                                            </c:if>

                                            <c:if test="${estadoCita == 'ATENDIDA'}">
                                                <c:set var="claseEstado" value="bg-primary" />
                                            </c:if>

                                            <tr>
                                                <!--
                                                    Fase 5:
                                                    Se usa c:out para escapar datos antes de mostrarlos.
                                                    Esto ayuda a prevenir Cross-Site Scripting, XSS.
                                                -->
                                                <td><c:out value="${cita.idCita}" /></td>
                                                <td><c:out value="${cita.pacienteDni}" /></td>
                                                <td><c:out value="${cita.pacienteNombreCompleto}" /></td>
                                                <td><c:out value="${cita.medicoNombreCompleto}" /></td>
                                                <td><c:out value="${cita.especialidad}" /></td>
                                                <td><c:out value="${cita.fecha}" /></td>
                                                <td><c:out value="${cita.hora}" /></td>

                                                <td>
                                                    <span class="badge ${claseEstado}">
                                                        <c:out value="${cita.estado}" />
                                                    </span>
                                                </td>

                                                <td class="text-center">

                                                    <c:if test="${estadoCita != 'ATENDIDA'}">
                                                        <!--
                                                            Fase 3:
                                                            Marcar como ATENDIDA se mantiene mediante POST.
                                                            No se ejecuta por enlace GET.
                                                        -->
                                                        <form action="${pageContext.request.contextPath}/citas"
                                                              method="post"
                                                              class="d-inline js-form-atender">

                                                            <input type="hidden" name="accion" value="atender">
                                                            <input type="hidden" name="id" value="${cita.idCita}">

                                                            <button type="submit"
                                                                    class="btn btn-outline-success btn-sm"
                                                                    title="Atender cita">
                                                                <i class="bi bi-check-lg"></i>
                                                            </button>
                                                        </form>

                                                        <a href="${pageContext.request.contextPath}/citas?accion=editar&id=${cita.idCita}"
                                                           class="btn btn-outline-success btn-sm"
                                                           title="Editar cita">
                                                            <i class="bi bi-pencil"></i>
                                                        </a>
                                                    </c:if>

                                                    <!--
                                                        Fase 3:
                                                        Anular cita se mantiene mediante POST.
                                                        No se ejecuta por enlace GET.
                                                    -->
                                                    <form action="${pageContext.request.contextPath}/citas"
                                                          method="post"
                                                          class="d-inline js-form-anular">

                                                        <input type="hidden" name="accion" value="eliminar">
                                                        <input type="hidden" name="id" value="${cita.idCita}">

                                                        <button type="submit"
                                                                class="btn btn-outline-danger btn-sm"
                                                                title="Anular cita">
                                                            <i class="bi bi-x-lg"></i>
                                                        </button>
                                                    </form>

                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>

                        <nav id="paginacion"
                             class="mt-3 d-flex flex-column align-items-center gap-2"
                             style="display:none">
                            <span id="pag-info" class="text-muted small text-center"></span>
                            <ul class="pagination pagination-sm mb-0 justify-content-center"
                                id="pag-controles"></ul>
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

            /*
                Paginado simple del lado del cliente.
                Muestra 10 registros por página.
            */
            (function () {
                const FILAS_POR_PAGINA = 10;
                const tbody = document.querySelector(".table tbody");

                if (!tbody) {
                    return;
                }

                const filas = Array.from(tbody.querySelectorAll("tr")).filter(function (tr) {
                    return tr.querySelectorAll("td").length > 1;
                });

                if (filas.length === 0) {
                    return;
                }

                const totalPaginas = Math.ceil(filas.length / FILAS_POR_PAGINA);
                let paginaActual = 1;

                const paginacion = document.getElementById("paginacion");
                const info = document.getElementById("pag-info");
                const controles = document.getElementById("pag-controles");

                paginacion.style.display = "flex";

                function mostrarPagina(pagina) {
                    paginaActual = pagina;

                    const inicio = (pagina - 1) * FILAS_POR_PAGINA;
                    const fin = inicio + FILAS_POR_PAGINA;

                    filas.forEach(function (tr, i) {
                        tr.style.display = (i >= inicio && i < fin) ? "" : "none";
                    });

                    info.textContent = "Mostrando "
                            + (inicio + 1)
                            + " - "
                            + Math.min(fin, filas.length)
                            + " de "
                            + filas.length
                            + " registros";

                    renderControles();
                }

                function renderControles() {
                    controles.innerHTML = "";

                    var prev = document.createElement("li");
                    prev.className = "page-item" + (paginaActual === 1 ? " disabled" : "");
                    prev.innerHTML = '<a class="page-link" href="#">&laquo;</a>';

                    prev.addEventListener("click", function (e) {
                        e.preventDefault();

                        if (paginaActual > 1) {
                            mostrarPagina(paginaActual - 1);
                        }
                    });

                    controles.appendChild(prev);

                    for (var p = 1; p <= totalPaginas; p++) {
                        (function (pg) {
                            var li = document.createElement("li");
                            li.className = "page-item" + (pg === paginaActual ? " active" : "");
                            li.innerHTML = '<a class="page-link" href="#">' + pg + '</a>';

                            li.addEventListener("click", function (e) {
                                e.preventDefault();
                                mostrarPagina(pg);
                            });

                            controles.appendChild(li);
                        })(p);
                    }

                    var next = document.createElement("li");
                    next.className = "page-item" + (paginaActual === totalPaginas ? " disabled" : "");
                    next.innerHTML = '<a class="page-link" href="#">&raquo;</a>';

                    next.addEventListener("click", function (e) {
                        e.preventDefault();

                        if (paginaActual < totalPaginas) {
                            mostrarPagina(paginaActual + 1);
                        }
                    });

                    controles.appendChild(next);
                }

                mostrarPagina(1);
            })();

            /*
                SweetAlert2 confirmaciones.
                Fase 3:
                Las confirmaciones no redirigen por URL.
                Confirman y envían formularios POST.
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