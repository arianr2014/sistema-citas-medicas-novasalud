# CHANGELOG NovaSalud V3.2.1

## Correcciones y mejoras

- Se corrigió el encabezado del menú lateral y la visibilidad del título "Menú clínico".
- Se reforzó el comportamiento responsive para evitar duplicidad entre menú lateral y menú hamburguesa.
- Se agregó módulo de atención médica para el rol DOCTOR.
- El médico ahora debe registrar motivo de consulta, síntomas, diagnóstico, tratamiento, receta e indicaciones antes de marcar una cita como atendida.
- La atención médica puede editarse nuevamente por el médico responsable de la cita.
- La agenda del médico ahora dirige a una pantalla clínica antes de cerrar la atención.
- La historia clínica del paciente usa el DNI como código único.
- El módulo Pacientes muestra el código de historia clínica en el listado y formulario.
- Se mejoró el formulario de usuarios para mostrar correctamente los datos del personal, acceso, rol y médico asociado.
- Se actualizó la base de datos independiente a `bd_citasmedicas_v321`.

## Base de datos

Ejecutar únicamente:

```sql
database/v321/00_instalacion_completa_bd_citasmedicas_v321.sql
```
