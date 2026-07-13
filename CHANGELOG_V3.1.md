# Changelog NovaSalud V3.2.1

## Cambios principales

- Base nueva `bd_citasmedicas_v321`.
- Contexto web `/NovaSaludV321`.
- Corrección global de codificación UTF-8 para tildes y caracteres especiales.
- Recepcionista ya no cobra pagos. Caja realiza el cobro.
- Citas nuevas nacen como PROGRAMADA.
- Recepción puede reprogramar, cancelar, marcar en espera o NO ASISTIÓ.
- Doctor es quien marca la cita como ATENDIDA desde su propia agenda.
- Agenda flexible por fecha, médico y especialidad.
- Doctor solo visualiza su propia agenda.
- Pagos parciales con estados PENDIENTE, PARCIAL, PAGADO y ANULADO.
- Número interno de pago `PAG-000001`.
- Número de operación externo obligatorio para Yape, Plin, tarjeta y transferencia.
- Comprobante interno imprimible/exportable como PDF desde navegador.
- La cita registra recepcionista, tarifa, especialidad y monto aplicado.
- Protección para no desactivar el último ADMIN activo.
