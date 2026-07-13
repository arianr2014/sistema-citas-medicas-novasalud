# CHANGELOG V3.0 - Sistema de Gestión Clínica NovaSalud

## Objetivo

Escalar NovaSalud desde un sistema de citas médicas hacia una plataforma administrativa para clínicas de consultas externas.

## Cambios principales

### Seguridad y hardening

- Usuario de base de datos con privilegios mínimos.
- Eliminación de compatibilidad con contraseñas en texto plano.
- Validación exclusiva mediante BCrypt.
- Token CSRF para formularios POST internos.
- Logs centralizados con `AppLogger`.
- Bloqueo de agenda ajena para rol DOCTOR.

### Usuarios

- Nuevo módulo de gestión de usuarios.
- Creación, edición, activación, desactivación y reseteo de contraseña.
- Asociación de usuario DOCTOR con registro médico.
- Nuevos roles: CAJERO y DIRECCION.

### Tarifas

- Nuevo módulo de tarifas por especialidad.
- Tarifas con monto, moneda, vigencia y estado.
- Base para generar cargos de citas.

### Pagos / Caja

- Nuevo módulo de pagos.
- Registro de pago por cita.
- Soporte para métodos: EFECTIVO, YAPE, PLIN, TARJETA y TRANSFERENCIA.
- Estado de pago: PENDIENTE, PAGADO y ANULADO.

### Reportes financieros

- Ingresos por rango de fechas.
- Ingresos por especialidad.
- Ingresos por método de pago.

### Estadísticas clínicas

- Especialidades con mayor demanda.
- Citas por día en rango seleccionado.

### Dirección

- Nuevo panel ejecutivo para rol DIRECCION.
- Vista de ingresos, citas del día y demanda por especialidad.

### Experiencia de usuario

- Diseño visual premium orientado a clínica.
- Nuevos íconos por módulo.
- Cards KPI.
- Menú clínico por rol.
- Badges de estado.

## Archivos principales agregados

- `UsuarioController.java`
- `TarifaConsultaController.java`
- `PagoController.java`
- `ReporteFinancieroController.java`
- `EstadisticasController.java`
- `DireccionController.java`
- `CsrfFilter.java`
- `CsrfUtil.java`
- `AppLogger.java`
- `UsuarioService.java`
- `TarifaConsultaService.java`
- `PagoService.java`
- `TarifaConsultaDAO.java`
- `PagoDAO.java`

## Módulos JSP agregados

- `usuario/list.jsp`
- `usuario/form.jsp`
- `tarifa/list.jsp`
- `tarifa/form.jsp`
- `pago/list.jsp`
- `pago/form.jsp`
- `reporte/financiero.jsp`
- `estadistica/index.jsp`
- `direccion/index.jsp`

