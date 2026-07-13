# Changelog NovaSalud V3.2.1

## Correcciones funcionales y de seguridad

- Se elimina la lógica de usuario médico genérico para acceso clínico.
- Se agregan usuarios médicos reales vinculados a registros de médico: `valeria.rojas`, `luis.herrera` y `camila.torres`.
- El login sigue solicitando solo usuario y contraseña, pero ahora valida internamente que un DOCTOR tenga médico activo asociado.
- La sesión del DOCTOR guarda `idMedico`, `nombreMedico`, `idEspecialidad` y `nombreEspecialidad`.
- La agenda del DOCTOR ignora cualquier `idMedico` enviado por URL y usa exclusivamente el médico de sesión.
- El módulo Usuarios exige médico asociado cuando el rol seleccionado es DOCTOR.
- Se evita tener dos usuarios DOCTOR activos para el mismo médico.
- Se refuerza UTF-8 en JSP, JSPF, web.xml y menú para corregir textos como Menú, Médicos, Dirección, Estadísticas y Cerrar sesión.
- Se crea nueva base independiente `bd_citasmedicas_v321`.
- Se actualiza el contexto web a `/NovaSaludV321`.

## Instalación

Ejecutar únicamente:

```text
database/v321/00_instalacion_completa_bd_citasmedicas_v321.sql
```
