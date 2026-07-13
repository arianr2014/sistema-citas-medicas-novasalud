# CHANGELOG - NovaSalud V3.2.1

## Versión 3.2.0 - Gestión operativa clínica avanzada

### Cambios principales

- Nueva base de datos independiente: `bd_citasmedicas_v321`.
- Nuevo contexto web: `/NovaSaludV321`.
- Datos de demostración realistas entre el 10/06/2026 y el 11/08/2026.
- 8 especialidades, 12 médicos, 90 pacientes y cientos de citas/pagos de prueba.
- Citas históricas atendidas y pagadas entre el 10/06/2026 y el 10/07/2026.
- Citas futuras programadas entre el 11/07/2026 y el 11/08/2026 con pagos pendientes, parciales y pagados.
- Agenda del médico con vista mensual y detalle diario.
- Control de cupos por médico según duración promedio de cita y máximo diario.
- Gestión de usuarios ampliada con datos personales del trabajador.
- Médicos ampliados con DNI, correo, CMP, consultorio, duración por cita y máximo diario.
- Pacientes ampliados con fecha de nacimiento, sexo, correo, contacto de emergencia e historia clínica automática.
- Caja permite búsqueda por DNI del paciente, además de fecha y estado de pago.
- Tarifario rápido mejor presentado para caja.
- Menú activo restaurado por módulo.
- Sesiones se invalidan si el usuario es modificado o desactivado por administración.

### Usuarios demo

Todos los usuarios demo usan la contraseña `123456`.

- `administrador`
- `recepcionista1`
- `recepcionista2`
- `cajero1`
- `cajero2`
- `direccion`
- `valeria.rojas`
- `luis.herrera`
- `camila.torres`
- `marco.salas`
- `ana.beltran`
- `diego.munoz`
- `rafael.quispe`
- `sofia.nunez`
- `bruno.castillo`
- `elena.vargas`
- `patricia.reategui`
- `hector.ramirez`

### Base de datos

Ejecutar solo:

```sql
database/v321/00_instalacion_completa_bd_citasmedicas_v321.sql
```
