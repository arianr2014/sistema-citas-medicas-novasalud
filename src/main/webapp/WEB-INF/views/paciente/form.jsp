<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>

<c:set var="editandoPaciente" value="${not empty paciente and paciente.idPaciente > 0}" />
<c:set var="pageTitle"
       value="${editandoPaciente ? 'Editar Paciente' : 'Registrar Paciente'}"
       scope="request" />

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
                <span class="text-white small">Modulo: Pacientes</span>
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

                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">
                            <i class="bi bi-person-plus"></i>

                            <c:choose>
                                <c:when test="${editandoPaciente}">
                                    Editar Paciente
                                </c:when>
                                <c:otherwise>
                                    Registrar Paciente
                                </c:otherwise>
                            </c:choose>
                        </h5>

                        <a href="${pageContext.request.contextPath}/pacientes"
                           class="btn btn-light btn-sm">
                            <i class="bi bi-arrow-left"></i> Volver
                        </a>
                    </div>

                    <div class="card-body">

                        <!--
                            Fase 5:
                            El mensaje de error se muestra con c:out para evitar salida directa.
                        -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">
                                <c:out value="${error}" />
                            </div>
                        </c:if>

                        <form method="post"
                              action="${pageContext.request.contextPath}/pacientes"
                              class="row g-3">

                            <input type="hidden"
                                   name="idPaciente"
                                   value="${empty paciente.idPaciente ? 0 : paciente.idPaciente}">

                            <!--
                                Fase 5:
                                Los valores que regresan al formulario se escapan con fn:escapeXml.
                                Esto evita reflejar código HTML o JavaScript en atributos value.
                            -->
                            <div class="col-md-4">
                                <label class="form-label">DNI *</label>
                                <input type="text"
                                       class="form-control"
                                       name="dni"
                                       maxlength="15"
                                       required
                                       value="${fn:escapeXml(paciente.dni)}">
                            </div>

                            <div class="col-md-4">
                                <label class="form-label">Nombres *</label>
                                <input type="text"
                                       class="form-control"
                                       name="nombres"
                                       maxlength="100"
                                       required
                                       value="${fn:escapeXml(paciente.nombres)}">
                            </div>

                            <div class="col-md-4">
                                <label class="form-label">Apellidos *</label>
                                <input type="text"
                                       class="form-control"
                                       name="apellidos"
                                       maxlength="100"
                                       required
                                       value="${fn:escapeXml(paciente.apellidos)}">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Telefono</label>
                                <input type="text"
                                       class="form-control"
                                       name="telefono"
                                       maxlength="20"
                                       value="${fn:escapeXml(paciente.telefono)}">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Direccion</label>
                                <input type="text"
                                       class="form-control"
                                       name="direccion"
                                       maxlength="255"
                                       value="${fn:escapeXml(paciente.direccion)}">
                            </div>

                            <div class="col-12 d-flex justify-content-end gap-2">
                                <a href="${pageContext.request.contextPath}/pacientes"
                                   class="btn btn-outline-secondary">
                                    Cancelar
                                </a>

                                <button type="submit" class="btn btn-success">
                                    <i class="bi bi-save"></i> Guardar
                                </button>
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