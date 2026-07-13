# Plan NovaSalud V3.2.1

## Objetivo

Consolidar una versión operativa para clínica de consultas externas con flujo completo de agenda, pacientes, médicos, pagos, caja, reportes y agenda médica segura por usuario.

## Alcance implementado

1. Base independiente `bd_citasmedicas_v321`.
2. Usuarios con datos personales, rol y asociación médica cuando corresponde.
3. Médicos con datos profesionales, duración de cita y máximo diario.
4. Pacientes con información ampliada e historia clínica automática.
5. Agenda médica con vista mensual y detalle diario.
6. Búsqueda de caja por DNI, fecha y estado de pago.
7. Datos de prueba realistas entre junio y agosto de 2026.
8. Control de cupos por día para evitar sobreprogramación.
9. Sesiones invalidadas cuando el usuario es modificado/desactivado.

## Próximas mejoras sugeridas

- Formulario clínico completo de atención médica.
- Exportación PDF nativa de comprobante.
- Cierre formal de caja diaria.
- Integración futura con facturación electrónica si el caso lo requiere.
