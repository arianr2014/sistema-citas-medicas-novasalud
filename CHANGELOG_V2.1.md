# CHANGELOG V2.1 - Sistema de Citas Médicas NovaSalud

## Versión

V2.1 - Mejora de integridad de datos, seguridad de autenticación y protección de operaciones críticas.

---

## Fase 1 - Integridad de datos y eliminación lógica

### Objetivo

Mejorar la integridad de la base de datos y evitar la eliminación física de registros importantes del sistema.

La mejora permite conservar el historial de pacientes, médicos, especialidades y horarios, marcando los registros como inactivos en lugar de borrarlos definitivamente.

### Cambios realizados

#### 1. Backup previo

Antes de aplicar cambios, se generó un respaldo de la base de datos `bd_citasmedicas` desde MySQL Workbench, incluyendo estructura, datos y procedimientos almacenados.

#### 2. Eliminación lógica

Se agregó la columna `estado_registro` en las siguientes tablas:

- `paciente`
- `medico`
- `especialidad`
- `horario`

La tabla `cita` ya contaba con una lógica similar.

#### 3. Corrección de procedimientos almacenados

Se corrigieron y/o agregaron procedimientos almacenados para manejar eliminación lógica:

- `sp_eliminar_paciente`
- `sp_eliminar_medico`
- `sp_eliminar_especialidad`
- `sp_eliminar_horario`

Estos procedimientos ya no eliminan registros físicamente. Ahora actualizan el campo `estado_registro` con el valor `INACTIVO`.

#### 4. Actualización de DAO

Se actualizaron las clases DAO para listar únicamente registros activos y ejecutar eliminación lógica:

- `PacienteDAO.java`
- `MedicoDAO.java`
- `EspecialidadDAO.java`
- `HorarioDAO.java`

#### 5. Actualización de scripts SQL

Se actualizaron los archivos SQL del proyecto:

- `database/01 - bd - tablas.sql`
- `database/03 - stores.sql`

Esto permite que una instalación nueva del proyecto ya incluya la estructura corregida.

#### 6. Script de migración

Se creó el archivo:

```text
database/migrations/migracion_v2_1_eliminacion_logica.sql
```

Este script permite actualizar una base de datos existente V2.0 hacia la versión V2.1 sin perder datos.

### Pruebas realizadas

- Inicio de sesión con usuario administrador.
- Acceso a módulos principales.
- Registro y eliminación de paciente de prueba.
- Registro y eliminación de especialidad de prueba.
- Validación en MySQL de que los registros eliminados permanecen en la base de datos con `estado_registro = 'INACTIVO'`.
- Verificación de que los registros inactivos ya no aparecen en los listados del sistema.
- Compilación del proyecto mediante Clean and Build en Apache NetBeans.

### Resultado

La mejora fue aplicada correctamente. El sistema conserva la información histórica y evita eliminaciones físicas innecesarias, aumentando la seguridad, trazabilidad e integridad de los datos.

---

## Fase 2 - Seguridad de autenticación

### Objetivo

Fortalecer el proceso de autenticación del sistema, evitando que las contraseñas de los usuarios se almacenen en texto plano dentro de la base de datos.

### Cambios realizados

1. Se agregó la dependencia `jbcrypt` en el archivo `pom.xml`.
2. Se creó la clase `PasswordUtil.java` en el paquete `util`.
3. Se modificó `UsuarioDAO.java` para validar contraseñas mediante BCrypt.
4. Se generaron hashes BCrypt para los usuarios principales del sistema:
   - administrador
   - recepcionista
   - doctor
5. Se actualizó la tabla `usuario` en MySQL, reemplazando contraseñas visibles por hashes.
6. Se actualizó el archivo `database/02 - data - usuario.sql` para que una instalación nueva inserte usuarios con contraseñas hasheadas.
7. Se creó la migración:

```text
database/migrations/migracion_v2_1_hash_passwords.sql
```

8. Se retiró la clase temporal `PasswordHashGenerator.java` del código fuente principal.

### Pruebas realizadas

- Inicio de sesión con usuario administrador.
- Inicio de sesión con usuario recepcionista.
- Inicio de sesión con usuario doctor.
- Prueba con contraseña incorrecta.
- Verificación en MySQL de que las contraseñas ya no aparecen como `123456`.
- Validación de que los hashes almacenados inician con `$2a$12$`.
- Ejecución de Clean and Build en Apache NetBeans.

### Resultado

La autenticación fue mejorada correctamente. El sistema mantiene el ingreso normal con las credenciales de prueba, pero la base de datos ya no almacena contraseñas en texto plano.

---

## Fase 3 - Protección de operaciones críticas mediante POST

### Objetivo

Mejorar la seguridad del sistema evitando que operaciones críticas se ejecuten mediante URLs manipulables por método GET.

### Problema identificado

En versiones anteriores, algunas acciones que modificaban datos se ejecutaban mediante enlaces GET, por ejemplo:

```text
/pacientes?accion=eliminar&id=1
/medicos?accion=eliminar&id=1
/citas?accion=atender&id=1
/citas?accion=eliminar&id=1
```

Esto representaba una mala práctica, porque el método GET debe usarse para consultar información, no para modificar registros.

### Cambios realizados

1. Se refactorizó `PacienteController.java`.
2. Se refactorizó `EspecialidadController.java`.
3. Se refactorizó `MedicoController.java`.
4. Se refactorizó `HorarioController.java`.
5. Se refactorizó `CitaController.java`.
6. Se actualizaron los JSP de listado para reemplazar enlaces críticos por formularios POST.
7. Se bloqueó la ejecución de eliminaciones por URL GET.
8. Se bloquearon las acciones de atender y anular cita por URL GET.
9. Se mantuvieron confirmaciones visuales antes de ejecutar acciones críticas.
10. Se agregaron mensajes de control para intentos inválidos, como `metodo_invalido`.

### Módulos protegidos

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

La Fase 3 fue completada correctamente. Las operaciones críticas ya no se ejecutan por URL GET, sino mediante formularios POST. Esto mejora la seguridad del sistema y reduce el riesgo de modificaciones accidentales o manipuladas desde el navegador.

---

## Estado general de la versión V2.1

La versión V2.1 queda consolidada con las siguientes mejoras:

- Eliminación lógica.
- Hash de contraseñas con BCrypt.
- Protección de operaciones críticas mediante POST.
- Scripts SQL actualizados.
- Migraciones creadas.
- Documentación técnica actualizada.
- Pruebas funcionales satisfactorias.

---

## Fase 4 - Control de roles, acceso denegado personalizado e identidad de sesión

### Objetivo

Reforzar la seguridad del sistema mediante control de acceso por roles, evitando que usuarios no autorizados ingresen a módulos restringidos mediante manipulación directa de URL.

### Problema identificado

Aunque el sistema ya contaba con roles como `ADMIN`, `RECEPCIONISTA` y `DOCTOR`, era necesario reforzar visual y técnicamente el control de acceso.

También se identificó que el usuario no tenía una referencia clara de qué sesión estaba activa, qué rol estaba usando y en qué módulo se encontraba.

### Cambios realizados

1. Se reforzó `AuthFilter.java` para validar rutas protegidas según el rol del usuario.
2. Se actualizó `AuthController.java` para mejorar el manejo de sesión y redirección por rol.
3. Se creó `AccesoDenegadoController.java`.
4. Se creó la vista personalizada `acceso-denegado.jsp`.
5. Se actualizó el menú lateral para mostrar solo las opciones permitidas por rol.
6. Se implementó indicador visual del módulo activo en el menú.
7. Se creó el fragmento reutilizable `usuario-sesion.jspf`.
8. Se agregó visualización del usuario autenticado y rol activo en la barra superior.
9. Se validó el acceso manual por URL para cada rol.

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

### Pruebas realizadas

- Inicio de sesión como administrador.
- Inicio de sesión como recepcionista.
- Inicio de sesión como doctor.
- Validación de menú visible según rol.
- Validación de acceso permitido según rol.
- Intento de ingreso manual a rutas no autorizadas.
- Visualización de página personalizada de acceso denegado.
- Verificación del indicador visual del menú activo.
- Verificación de usuario y rol visibles en la barra superior.
- Ejecución de Clean and Build en Apache NetBeans.

### Resultado

La Fase 4 fue completada correctamente. El sistema ahora aplica control de acceso por roles, bloquea rutas no autorizadas, muestra una página personalizada de acceso denegado y permite identificar claramente el usuario y rol activo dentro del sistema.

Esta mejora responde a buenas prácticas de seguridad web relacionadas con control de acceso y prevención de fallas tipo Broken Access Control.

---

## Fase 5 - Seguridad en vistas JSP y prevención XSS

### Objetivo

Reforzar la seguridad de las vistas JSP del sistema para reducir el riesgo de Cross-Site Scripting, XSS, evitando mostrar datos dinámicos directamente en pantalla sin escape.

### Problema identificado

Varias vistas JSP mostraban datos usando expresiones directas como:

```jsp
<%= objeto.getCampo() %>
```

También algunos formularios devolvían valores dentro de atributos `value` sin escape explícito.

Esto podía representar un riesgo si un usuario ingresaba texto malicioso como:

```html
<script>alert("XSS")</script>
```

### Cambios realizados

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

### Vistas protegidas

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

Se probó texto malicioso controlado en buscadores, formularios y campos editables:

```html
<script>alert("XSS")</script>
```

### Resultado de las pruebas

- No se ejecutó JavaScript.
- No apareció alerta en el navegador.
- La página no se rompió.
- El texto se mostró como texto normal o no devolvió registros.
- Crear, editar, buscar, atender, anular y eliminar siguieron funcionando correctamente.
- El menú activo, usuario en sesión y rol activo siguieron mostrándose correctamente.

### Resultado

La Fase 5 fue completada correctamente. El sistema ahora muestra datos dinámicos de forma más segura en las vistas JSP y reduce el riesgo de XSS reflejado o almacenado.

Esta mejora fortalece la capa de presentación del sistema dentro de la arquitectura MVC y aplica buenas prácticas de seguridad web asociadas a OWASP.