<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<c:set var="pageTitle" value="Dirección - NovaSalud V3.2.1" scope="request" />
<!DOCTYPE html>
<html lang="es">
<%@ include file="/WEB-INF/views/layout/head.jspf" %>
<body class="bg-light">
<nav class="navbar navbar-expand-lg navbar-dark bg-success shadow-sm">
    <div class="container-fluid px-4">
        <button class="btn btn-outline-light d-lg-none me-2 mobile-menu-toggle" type="button" data-bs-toggle="offcanvas" data-bs-target="#sidebarMenu" aria-controls="sidebarMenu" aria-label="Abrir menu"><i class="bi bi-list fs-4"></i></button>
        <span class="navbar-brand fw-semibold"><i class="bi bi-hospital me-2"></i>NovaSalud V3.2.1</span>
        <div class="d-flex flex-column flex-md-row align-items-md-center gap-2 ms-auto"><span class="text-white small">Módulo: Dirección</span><%@ include file="/WEB-INF/views/layout/usuario-sesion.jspf" %></div>
    </div>
</nav>
<%@ include file="/WEB-INF/views/layout/menu-mobile.jspf" %>
<main class="container-fluid py-4 px-4">
  <div class="row g-4">
    <div class="col-12 col-lg-3 order-1 order-lg-1"><%@ include file="/WEB-INF/views/layout/menu-right.jspf" %></div>
    <div class="col-12 col-lg-9 order-2 order-lg-2">

<h4 class="fw-bold text-success mb-3"><span class="page-title-icon"><i class="bi bi-building-check"></i></span> Panel de Dirección</h4><p class="text-muted">Vista ejecutiva para seguimiento de ingresos, demanda y operación clínica.</p>
<div class="row g-3 mb-3"><div class="col-md-4"><div class="kpi-card p-3"><div class="d-flex gap-3 align-items-center"><div class="kpi-icon"><i class="bi bi-cash-coin"></i></div><div><div class="text-muted small">Ingresos del mes</div><div class="fs-3 fw-bold text-success">S/ <c:out value="${ingresosMes}"/></div></div></div></div></div><div class="col-md-4"><div class="kpi-card p-3"><div class="d-flex gap-3 align-items-center"><div class="kpi-icon"><i class="bi bi-calendar2-check"></i></div><div><div class="text-muted small">Fecha operativa</div><div class="fs-4 fw-bold"><c:out value="${fechaHoy}"/></div></div></div></div></div><div class="col-md-4"><div class="kpi-card p-3"><div class="d-flex gap-3 align-items-center"><div class="kpi-icon"><i class="bi bi-bar-chart"></i></div><div><div class="text-muted small">Top especialidades</div><div class="fs-4 fw-bold"><c:out value="${fn:length(topEspecialidades)}"/></div></div></div></div></div></div>
<div class="row g-3"><div class="col-lg-6"><div class="card premium-card border-0"><div class="card-header bg-success text-white">Citas de hoy por estado</div><table class="table mb-0"><tbody><c:forEach var="c" items="${citasHoy}"><tr><td><c:out value="${c.key}"/></td><td class="text-end"><span class="badge bg-primary"><c:out value="${c.value}"/></span></td></tr></c:forEach></tbody></table></div></div><div class="col-lg-6"><div class="card premium-card border-0"><div class="card-header bg-success text-white">Ingresos por especialidad</div><table class="table mb-0"><tbody><c:forEach var="i" items="${ingresosEspecialidad}"><tr><td><c:out value="${i.etiqueta}"/></td><td class="text-end">S/ <c:out value="${i.total}"/></td></tr></c:forEach></tbody></table></div></div></div>

    </div>
  </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>