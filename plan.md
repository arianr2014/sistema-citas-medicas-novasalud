# Plan de Trabajo - Sistema de Citas Médicas NovaSalud V2.1

## 1. Objetivo general

Mejorar y consolidar el Sistema de Citas Médicas NovaSalud, asegurando su funcionamiento, integridad de datos, documentación técnica y preparación para futuras mejoras de seguridad.

El sistema permite gestionar pacientes, médicos, especialidades, horarios y citas médicas mediante una aplicación web desarrollada en Java, JSP, Servlets, JDBC y MySQL.

---

## 2. Estado inicial del proyecto

El proyecto base ya contaba con los siguientes módulos funcionales:

- Login de usuario.
- Dashboard principal.
- Gestión de pacientes.
- Gestión de médicos.
- Gestión de especialidades.
- Gestión de horarios.
- Gestión de citas.
- Agenda médica.
- Cierre de sesión.
- Conexión a base de datos MySQL.
- Arquitectura por capas con Controller, Service, DAO, Model, Filter y Util.

---

## 3. Fase 1 - Integridad de datos y eliminación lógica

### Objetivo

Evitar la eliminación física de registros importantes del sistema y mejorar la trazabilidad de la información.

### Actividades realizadas

1. Se creó una copia de trabajo del proyecto como versión V2.1.
2. Se generó un backup de la base de datos `bd_citasmedicas`.
3. Se agregó el campo `estado_registro` en las tablas:
   - paciente
   - medico
   - especialidad
   - horario
4. Se corrigieron procedimientos almacenados de eliminación:
   - `sp_eliminar_paciente`
   - `sp_eliminar_medico`
   - `sp_eliminar_especialidad`
   - `sp_eliminar_horario`
5. Se actualizaron los DAO:
   - `PacienteDAO.java`
   - `MedicoDAO.java`
   - `EspecialidadDAO.java`
   - `HorarioDAO.java`
6. Se actualizaron los scripts SQL:
   - `database/01 - bd - tablas.sql`
   - `database/03 - stores.sql`
7. Se creó el script de migración:
   - `database/migrations/migracion_v2_1_eliminacion_logica.sql`
8. Se actualizaron archivos documentales:
   - `README.md`
   - `ARQUITECTURA_PROYECTO.md`
   - `CHANGELOG_V2.1.md`

### Pruebas realizadas

- Inicio de sesión con usuario administrador.
- Acceso correcto a todos los módulos principales.
- Registro de paciente de prueba.
- Eliminación lógica de paciente.
- Verificación en MySQL de paciente con `estado_registro = 'INACTIVO'`.
- Registro de especialidad de prueba.
- Eliminación lógica de especialidad.
- Verificación en MySQL de especialidad con `estado_registro = 'INACTIVO'`.
- Verificación de que los registros inactivos no aparezcan en los listados del sistema.
- Ejecución de Clean and Build en Apache NetBeans.
- Ejecución del sistema en Apache Tomcat.

### Resultado

La eliminación lógica fue implementada correctamente. Los registros eliminados desde el sistema ya no desaparecen físicamente de la base de datos, sino que quedan marcados como `INACTIVO`.

### Estado

Completado.

---

## 4. Fase 2 - Seguridad de autenticación

### Objetivo

Fortalecer el inicio de sesión y evitar el almacenamiento de contraseñas en texto plano.

### Actividades realizadas

1. Se agregó la dependencia BCrypt en `pom.xml`.
2. Se creó la clase `PasswordUtil.java`.
3. Se actualizó `UsuarioDAO.java` para validar contraseñas mediante BCrypt.
4. Se generaron hashes BCrypt para los usuarios:
   - administrador
   - recepcionista
   - doctor
5. Se actualizó la tabla `usuario` en MySQL.
6. Se actualizó el script `database/02 - data - usuario.sql`.
7. Se creó la migración:
   - `database/migrations/migracion_v2_1_hash_passwords.sql`
8. Se retiró la clase temporal `PasswordHashGenerator.java` del código fuente principal.
9. Se realizaron pruebas de inicio de sesión con los tres roles.

### Pruebas realizadas

- Inicio de sesión con usuario administrador.
- Inicio de sesión con usuario recepcionista.
- Inicio de sesión con usuario doctor.
- Prueba con contraseña incorrecta.
- Verificación en MySQL de que las contraseñas ya no aparecen como `123456`.
- Validación de hashes BCrypt.
- Ejecución de Clean and Build en Apache NetBeans.

### Resultado

La Fase 2 fue completada correctamente. El sistema ya no almacena contraseñas en texto plano y mantiene el acceso funcional con los roles ADMIN, RECEPCIONISTA y DOCTOR.

### Estado

Completado.

---

## 5. Fase 3 - Protección de operaciones críticas mediante POST

### Objetivo

Evitar que acciones críticas del sistema se ejecuten mediante URLs manipulables por método GET.

### Problema identificado

En versiones anteriores, algunas operaciones que modificaban información podían ejecutarse directamente desde la URL del navegador.

Ejemplos:

```text
/pacientes?accion=eliminar&id=1
/medicos?accion=eliminar&id=1
/horarios?accion=eliminar&id=1
/citas?accion=atender&id=1
/citas?accion=eliminar&id=1
```

Esto representaba una mala práctica, porque el método GET debe utilizarse para consultar información, no para modificar registros.

### Actividades realizadas

1. Se revisaron los módulos con acciones de eliminación o cambio de estado.
2. Se identificaron operaciones críticas ejecutadas mediante GET.
3. Se refactorizaron los controladores para separar responsabilidades HTTP.
4. Se actualizaron los botones de eliminación en los JSP.
5. Se cambiaron enlaces de eliminación por formularios POST.
6. Se protegieron las acciones de atención y anulación de citas.
7. Se agregaron mensajes de control para operaciones no permitidas.
8. Se realizaron pruebas funcionales en todos los módulos intervenidos.

### Separación aplicada

```text
GET  = listar, buscar, abrir formularios y editar visualmente.
POST = guardar, actualizar, eliminar, atender y anular.
```

### Módulos intervenidos

- Pacientes
- Especialidades
- Médicos
- Horarios
- Citas

### Pruebas realizadas

- Creación de registros de prueba.
- Eliminación desde botón del sistema.
- Confirmación de eliminación lógica en MySQL.
- Intento manual de eliminación por URL GET.
- Marcado de cita como ATENDIDA.
- Anulación de cita.
- Verificación de mensajes de operación no permitida.
- Ejecución de Clean and Build en Apache NetBeans.

### Resultado

La Fase 3 fue completada correctamente. El sistema ya no permite eliminar, atender o anular registros mediante manipulación directa de la URL.

### Estado

Completado.

---

## 6. Fase 4 - Control de roles y acceso

### Objetivo

Reforzar el control de acceso según el rol del usuario autenticado.

### Actividades propuestas

1. Revisar `AuthFilter.java`.
2. Confirmar rutas permitidas por rol.
3. Validar que ADMIN tenga acceso completo.
4. Validar que RECEPCIONISTA tenga acceso operativo.
5. Validar que DOCTOR solo tenga acceso a su agenda.
6. Evaluar relación entre usuario DOCTOR y registro de médico.
7. Documentar matriz de permisos.

### Estado

Pendiente.

---

## 7. Fase 5 - Seguridad en vistas JSP

### Objetivo

Reducir riesgos de XSS y mejorar la salida segura de datos en las vistas.

### Actividades propuestas

1. Revisar salidas directas con `<%= ... %>`.
2. Reemplazar salidas sensibles por mecanismos de escape.
3. Revisar mensajes de error y éxito.
4. Evitar mostrar detalles técnicos al usuario final.
5. Probar registros con caracteres especiales.
6. Documentar mejora.

### Estado

Pendiente.

---

## 8. Fase 6 - Refinamiento funcional y presentación

### Objetivo

Preparar el sistema para una presentación académica clara, ordenada y defendible.

### Actividades propuestas

1. Mejorar mensajes funcionales del sistema.
2. Evaluar mostrar edad del paciente en el formulario de citas.
3. Revisar diseño visual de pantallas principales.
4. Preparar capturas de evidencia.
5. Crear guion de exposición.
6. Preparar respuestas a posibles preguntas del docente.
7. Crear versión comprimida final del proyecto.
8. Validar instalación desde cero usando scripts SQL.

### Estado

Pendiente.

---

## 9. Riesgos identificados

| Riesgo | Impacto | Estado / Medida |
|---|---|---|
| Eliminación física de registros | Alto | Mitigado con eliminación lógica |
| Contraseñas en texto plano | Alto | Mitigado con BCrypt |
| Acciones críticas por GET | Alto | Mitigado con formularios POST |
| Acceso por roles incompleto | Medio | Pendiente de reforzar |
| Posible XSS en JSP | Medio | Pendiente de revisar |
| Credenciales de BD en código | Medio | Pendiente de variables de entorno |
| Scripts desactualizados | Bajo | Mitigado con carpeta `database` y migraciones |

---

## 10. Prioridad de trabajo

1. Fase 1 completada: integridad de datos y eliminación lógica.
2. Fase 2 completada: hash de contraseñas con BCrypt.
3. Fase 3 completada: protección de operaciones críticas mediante POST.
4. Próxima fase: reforzar acceso por roles.
5. Revisar salidas JSP.
6. Mejorar mensajes funcionales.
7. Preparar documentación y presentación final.

---

## 11. Estado actual del proyecto

La versión V2.1 se encuentra funcional y validada.

Estado:

- Aplicación web operativa.
- Base de datos conectada correctamente.
- Eliminación lógica implementada.
- Contraseñas almacenadas con hash BCrypt.
- Operaciones críticas protegidas mediante POST.
- DAO actualizados.
- Controladores refactorizados.
- JSP de listado actualizados.
- Scripts SQL actualizados.
- Migraciones V2.1 creadas.
- Documentación técnica principal actualizada.

El proyecto queda listo para iniciar la Fase 4 enfocada en control de roles y acceso.