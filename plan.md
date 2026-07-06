# Plan de Trabajo - Sistema de Citas Médicas NovaSalud V2.1

## 1. Objetivo general

Mejorar y consolidar el Sistema de Citas Médicas NovaSalud, asegurando su funcionamiento, integridad de datos, seguridad, documentación técnica y preparación para futuras mejoras funcionales.

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

Reforzar el control de acceso según el rol del usuario autenticado, evitando que usuarios no autorizados ingresen a módulos restringidos mediante manipulación directa de URL.

### Actividades realizadas

1. Se revisó `AuthFilter.java`.
2. Se revisó `AuthController.java`.
3. Se validó cómo se guarda el usuario y rol en sesión.
4. Se definió una matriz de permisos por rol.
5. Se reforzó el filtro de autorización por rutas.
6. Se creó una página personalizada de acceso denegado.
7. Se creó `AccesoDenegadoController.java`.
8. Se creó la vista `error/acceso-denegado.jsp`.
9. Se actualizó el menú lateral por rol.
10. Se implementó indicador visual del módulo activo.
11. Se creó el fragmento `usuario-sesion.jspf`.
12. Se mostró el usuario autenticado y rol activo en la barra superior.
13. Se probaron accesos permitidos y bloqueados con los roles ADMIN, RECEPCIONISTA y DOCTOR.

### Matriz de acceso aplicada

| Módulo | ADMIN | RECEPCIONISTA | DOCTOR |
|---|---:|---:|---:|
| Inicio / Dashboard | Sí | No | No |
| Pacientes | Sí | Sí | No |
| Médicos | Sí | No | No |
| Especialidades | Sí | No | No |
| Horarios | Sí | No | No |
| Citas | Sí | Sí | No |
| Agenda Médica | Sí | Sí | Sí |
| Cerrar sesión | Sí | Sí | Sí |

### Resultado

La Fase 4 fue completada correctamente. El sistema ahora protege rutas según el rol del usuario, muestra una página personalizada de acceso denegado y permite identificar claramente el usuario y rol activo en la barra superior.

### Estado

Completado.

---

## 7. Fase 5 - Seguridad en vistas JSP y prevención XSS

### Objetivo

Reducir riesgos de Cross-Site Scripting, XSS, y mejorar la salida segura de datos dinámicos en las vistas JSP.

### Actividades realizadas

1. Se agregó soporte JSTL en `pom.xml`.
2. Se incorporaron las taglibs `c` y `fn` en las vistas refactorizadas.
3. Se reemplazaron salidas directas por `<c:out>`.
4. Se usó `fn:escapeXml()` en campos `input value`.
5. Se protegieron mensajes de error con `<c:out>`.
6. Se refactorizaron los listados principales.
7. Se refactorizaron los formularios principales.
8. Se mantuvieron las operaciones críticas mediante POST.
9. Se validó que los menús, roles y sesiones siguieran funcionando.
10. Se mejoraron los mensajes funcionales del módulo Citas.

### Vistas trabajadas

- `paciente/list.jsp`
- `medico/list.jsp`
- `especialidad/list.jsp`
- `horario/list.jsp`
- `cita/list.jsp`
- `agenda/list.jsp`
- `paciente/form.jsp`
- `medico/form.jsp`
- `especialidad/form.jsp`
- `horario/form.jsp`
- `cita/form.jsp`

### Pruebas realizadas

Se probaron entradas controladas con texto tipo:

```html
<script>alert("XSS")</script>
```

Resultado de las pruebas:

- No se ejecutó JavaScript.
- No apareció alerta en el navegador.
- La página no se rompió.
- Crear, editar, buscar, atender, anular y eliminar siguieron funcionando correctamente.

### Estado

Completado.

---

## 8. Fase 5.1 - Limpieza final de salidas JSP directas

### Objetivo

Eliminar las salidas JSP directas restantes en vistas y fragmentos de layout para dejar el sistema con una capa de presentación más segura, limpia y reutilizable.

### Actividades realizadas

1. Se revisaron todas las vistas JSP y fragmentos JSPF buscando expresiones directas.
2. Se reemplazaron salidas JSP de expresión por JSTL y EL.
3. Se corrigió el login para mostrar mensajes usando `<c:out>`.
4. Se corrigió la página de acceso denegado.
5. Se corrigió el dashboard.
6. Se corrigió el encabezado general.
7. Se corrigió el fragmento de usuario y rol en sesión.
8. Se corrigió el menú lateral.
9. Se corrigió el menú móvil.
10. Se validó el acceso funcional por roles después de los cambios.

### Archivos trabajados

- `auth/login.jsp`
- `error/acceso-denegado.jsp`
- `home/inicio.jsp`
- `layout/head.jspf`
- `layout/menu-mobile.jspf`
- `layout/menu-right.jspf`
- `layout/usuario-sesion.jspf`

### Auditoría realizada

Se ejecutó el comando:

```cmd
findstr /S /N /C:"<%=" src\main\webapp\WEB-INF\views\*.jsp src\main\webapp\WEB-INF\views\*.jspf
```

Resultado:

```text
Sin resultados.
```

### Pruebas realizadas

- Inicio de sesión como ADMIN.
- Inicio de sesión como RECEPCIONISTA.
- Inicio de sesión como DOCTOR.
- Navegación por módulos permitidos según rol.
- Validación de menú visible según rol.
- Validación de indicador de módulo activo.
- Validación de acceso denegado para rutas no autorizadas.
- Ejecución del sistema en Apache Tomcat.

### Estado

Completado.

---

## 9. Fase 6 - Gestión de usuarios y administración de accesos

### Objetivo

Crear un módulo visual de gestión de usuarios para que el rol ADMIN pueda administrar cuentas del sistema de forma segura y ordenada.

### Actividades propuestas

1. Revisar la estructura actual de la tabla `usuario`.
2. Evaluar si la tabla necesita campos adicionales como `estado_registro`, `fecha_creacion` o `fecha_actualizacion`.
3. Ampliar `UsuarioDAO.java` para listar, registrar, actualizar y desactivar usuarios.
4. Crear `UsuarioService.java` para reglas de negocio.
5. Crear `UsuarioController.java`.
6. Crear la vista `usuario/list.jsp`.
7. Crear la vista `usuario/form.jsp`.
8. Agregar opción “Usuarios” al menú solo para ADMIN.
9. Proteger rutas de usuarios en `AuthFilter.java`.
10. Implementar creación de usuario con contraseña hasheada mediante BCrypt.
11. Implementar cambio de rol.
12. Implementar activación o desactivación lógica.
13. Evaluar opción de reseteo de contraseña.
14. Probar acceso y operaciones solo con rol ADMIN.

### Estado

Pendiente.

---

## 10. Fase 7 - Refinamiento funcional y preparación de entrega

### Objetivo

Preparar el sistema para una presentación académica clara, ordenada y defendible.

### Actividades propuestas

1. Revisar diseño visual de pantallas principales.
2. Preparar capturas de evidencia.
3. Crear guion de exposición.
4. Preparar respuestas a posibles preguntas del docente.
5. Crear versión comprimida final del proyecto.
6. Validar instalación desde cero usando scripts SQL.
7. Revisar documentación final.
8. Preparar evidencia técnica de commits y pruebas.

### Estado

Pendiente.

---

## 11. Riesgos identificados

| Riesgo | Impacto | Estado / Medida |
|---|---|---|
| Eliminación física de registros | Alto | Mitigado con eliminación lógica |
| Contraseñas en texto plano | Alto | Mitigado con BCrypt |
| Acciones críticas por GET | Alto | Mitigado con formularios POST |
| Acceso por roles incompleto | Alto | Mitigado con `AuthFilter.java` y matriz RBAC |
| Posible XSS en JSP | Medio | Mitigado con JSTL, `c:out` y `fn:escapeXml` |
| Salidas JSP directas restantes | Medio | Mitigado en Fase 5.1 |
| Falta de módulo visual de usuarios | Medio | Pendiente para Fase 6 |
| Credenciales de BD en código | Medio | Pendiente de variables de entorno |
| Falta de protección CSRF | Medio | Pendiente |
| Scripts desactualizados | Bajo | Mitigado con carpeta `database` y migraciones |

---

## 12. Prioridad de trabajo

1. Fase 1 completada: integridad de datos y eliminación lógica.
2. Fase 2 completada: hash de contraseñas con BCrypt.
3. Fase 3 completada: protección de operaciones críticas mediante POST.
4. Fase 4 completada: control de roles, acceso denegado e identidad de sesión.
5. Fase 5 completada: seguridad en vistas JSP y prevención XSS.
6. Fase 5.1 completada: limpieza final de salidas JSP directas.
7. Próxima fase: gestión de usuarios y administración de accesos.
8. Luego: refinamiento funcional, documentación final y preparación de entrega.

---

## 13. Estado actual del proyecto

La versión V2.1 se encuentra funcional y validada.

Estado:

- Aplicación web operativa.
- Base de datos conectada correctamente.
- Eliminación lógica implementada.
- Contraseñas almacenadas con hash BCrypt.
- Operaciones críticas protegidas mediante POST.
- Control de acceso por roles implementado.
- Página de acceso denegado implementada.
- Menús por rol implementados.
- Indicador de módulo activo implementado.
- Usuario y rol visibles en sesión.
- DAO actualizados.
- Controladores refactorizados.
- JSP de listado y formularios principales protegidos.
- Salidas JSP directas eliminadas de vistas revisadas.
- Scripts SQL actualizados.
- Migraciones V2.1 creadas.
- Documentación técnica principal actualizada.

El proyecto queda listo para iniciar la Fase 6, enfocada en la creación del módulo de gestión de usuarios para el rol ADMIN.