# Sistema de Citas Médicas NovaSalud - V2.1

## 1. Descripción general

El Sistema de Citas Médicas NovaSalud es una aplicación web desarrollada en Java para la gestión de pacientes, médicos, especialidades, horarios y citas médicas.

El sistema permite administrar el flujo principal de atención médica, desde el registro de pacientes y médicos hasta la programación, atención y anulación de citas. Además, cuenta con un dashboard inicial que muestra un resumen operativo de las citas del día y la actividad semanal.

La versión V2.1 incorpora mejoras de integridad de datos, seguridad de autenticación, protección de operaciones críticas, control de acceso por roles y seguridad en vistas JSP.

---

## 2. Tecnologías utilizadas

- Java
- Jakarta EE 10
- JSP
- JSTL
- Servlets
- JDBC
- MySQL
- MySQL Workbench
- Apache Tomcat
- Maven
- HTML
- CSS
- Bootstrap
- Apache NetBeans

---

## 3. Arquitectura del proyecto

El proyecto está organizado bajo una arquitectura por capas:

- **Controller:** recibe las solicitudes HTTP y coordina la navegación del sistema.
- **Service:** contiene reglas de negocio y validaciones principales.
- **DAO:** gestiona el acceso a datos mediante JDBC y procedimientos almacenados.
- **Model:** representa las entidades del sistema.
- **Filter:** controla sesión y acceso por roles.
- **Util:** contiene clases auxiliares, como conexión a base de datos y validación de contraseñas.

Estructura general:

```text
Vista JSP
   ↓
Controller / Servlet
   ↓
Service
   ↓
DAO
   ↓
JDBC
   ↓
MySQL
```

---

## 4. Módulos principales

El sistema incluye los siguientes módulos:

- Inicio / Dashboard
- Gestión de pacientes
- Gestión de médicos
- Gestión de especialidades
- Gestión de horarios
- Gestión de citas
- Agenda médica
- Login y cierre de sesión
- Control de acceso por roles
- Página personalizada de acceso denegado

---

## 5. Base de datos

Nombre de la base de datos:

```text
bd_citasmedicas
```

Tablas principales:

- usuario
- paciente
- medico
- especialidad
- horario
- cita

La carpeta `database` contiene los scripts necesarios para crear, configurar y migrar la base de datos del proyecto:

```text
database
├── 01 - bd - tablas.sql
├── 02 - data - usuario.sql
├── 03 - stores.sql
├── 04 - crear usuario acceso.sql
└── migrations
    ├── migracion_v2_1_eliminacion_logica.sql
    └── migracion_v2_1_hash_passwords.sql
```

---

## 6. Scripts SQL

### 6.1. `01 - bd - tablas.sql`

Crea la base de datos y las tablas principales del sistema.

### 6.2. `02 - data - usuario.sql`

Inserta usuarios iniciales para pruebas del sistema. En la versión V2.1, las contraseñas se insertan como hashes BCrypt.

### 6.3. `03 - stores.sql`

Crea procedimientos almacenados utilizados por el sistema.

### 6.4. `04 - crear usuario acceso.sql`

Crea el usuario de base de datos utilizado por la aplicación Java.

### 6.5. `migrations/migracion_v2_1_eliminacion_logica.sql`

Script de migración que permite actualizar una base existente de V2.0 a V2.1 sin perder datos, incorporando eliminación lógica.

### 6.6. `migrations/migracion_v2_1_hash_passwords.sql`

Script de migración que permite reemplazar contraseñas en texto plano por hashes BCrypt.

---

## 7. Credenciales de prueba del sistema

Usuarios del sistema web:

```text
Usuario: administrador
Contraseña: 123456
Rol: ADMIN
```

```text
Usuario: recepcionista
Contraseña: 123456
Rol: RECEPCIONISTA
```

```text
Usuario: doctor
Contraseña: 123456
Rol: DOCTOR
```

Estas credenciales son únicamente para pruebas académicas. En la base de datos, las contraseñas no se almacenan como texto plano, sino como hashes BCrypt.

---

## 8. Credenciales de conexión a base de datos

Usuario de base de datos usado por la aplicación:

```text
Usuario BD: usuario_citas
Contraseña BD: ISO/IEC27001
Base de datos: bd_citasmedicas
Host: localhost
Puerto: 3306
```

URL JDBC usada por el sistema:

```text
jdbc:mysql://localhost:3306/bd_citasmedicas?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Nota: estas credenciales corresponden al entorno local académico. Para una versión productiva se recomienda mover credenciales sensibles a variables de entorno o archivos de configuración no versionados.

---

## 9. Instalación y ejecución

### 9.1. Requisitos previos

- JDK instalado
- Apache NetBeans
- Apache Tomcat configurado en NetBeans
- MySQL Server
- MySQL Workbench
- Maven o soporte Maven desde NetBeans

### 9.2. Pasos de instalación

1. Crear la base de datos ejecutando:

```text
database/01 - bd - tablas.sql
```

2. Insertar usuarios iniciales:

```text
database/02 - data - usuario.sql
```

3. Crear procedimientos almacenados:

```text
database/03 - stores.sql
```

4. Crear el usuario de acceso para la aplicación:

```text
database/04 - crear usuario acceso.sql
```

5. Abrir el proyecto en Apache NetBeans.

6. Ejecutar:

```text
Clean and Build
```

7. Ejecutar el proyecto con:

```text
Run
```

8. Ingresar al sistema desde el navegador:

```text
http://localhost:8080/MiPrimeraWeb/
```

---

## 10. Mejoras aplicadas en V2.1

### 10.1. Eliminación lógica

En versiones anteriores, algunos módulos podían eliminar registros físicamente de la base de datos. En la versión V2.1 se implementó eliminación lógica mediante el campo:

```text
estado_registro
```

Este campo permite marcar registros como:

```text
ACTIVO
INACTIVO
```

Tablas actualizadas:

- paciente
- medico
- especialidad
- horario

La tabla `cita` ya contaba con una lógica similar.

Beneficios:

- Conserva historial.
- Evita pérdida definitiva de información.
- Reduce conflictos con claves foráneas.
- Mejora la trazabilidad.
- Facilita auditoría futura.

---

### 10.2. Seguridad de autenticación con BCrypt

En la Fase 2 de la versión V2.1 se mejoró la seguridad del inicio de sesión mediante el uso de BCrypt para el almacenamiento de contraseñas.

En versiones anteriores, las contraseñas se encontraban almacenadas en texto plano. Ahora, la base de datos almacena hashes BCrypt, evitando que la contraseña real sea visible en la tabla `usuario`.

La contraseña de prueba sigue siendo:

```text
123456
```

Pero en la base de datos se almacena un valor similar a:

```text
$2a$12$...
```

Archivos modificados:

- `pom.xml`
- `UsuarioDAO.java`
- `PasswordUtil.java`
- `database/02 - data - usuario.sql`

Migración creada:

```text
database/migrations/migracion_v2_1_hash_passwords.sql
```

---

### 10.3. Protección de operaciones críticas mediante POST

En la Fase 3 de la versión V2.1 se reforzó la seguridad de las acciones críticas del sistema.

Antes, algunas operaciones como eliminar pacientes, médicos, especialidades, horarios o cambiar el estado de una cita podían ejecutarse mediante enlaces GET. Ahora, estas acciones se realizan mediante formularios POST.

Separación aplicada:

```text
GET  = consultar, listar, buscar o abrir formularios
POST = guardar, actualizar, eliminar, atender o anular
```

Módulos actualizados:

- Pacientes
- Especialidades
- Médicos
- Horarios
- Citas

Acciones protegidas:

- Eliminar paciente
- Eliminar especialidad
- Eliminar médico
- Eliminar horario
- Atender cita
- Anular cita

También se incorporó una validación para impedir que estas operaciones se ejecuten manipulando directamente la URL del navegador.

---

### 10.4. Control de roles, acceso denegado e identidad de sesión

En la Fase 4 de la versión V2.1 se reforzó el control de acceso por roles del sistema.

El sistema maneja tres roles principales:

```text
ADMIN
RECEPCIONISTA
DOCTOR
```

Matriz de acceso aplicada:

| Módulo | ADMIN | RECEPCIONISTA | DOCTOR |
|---|---:|---:|---:|
| Inicio / Dashboard | Sí | No | No |
| Pacientes | Sí | Sí | No |
| Médicos | Sí | No | No |
| Especialidades | Sí | No | No |
| Horarios | Sí | No | No |
| Citas | Sí | Sí | No |
| Agenda Médica | Sí | Sí | Sí |

También se implementó una página personalizada de acceso denegado. Si un usuario intenta ingresar manualmente a una ruta no autorizada, el sistema no permite el acceso y muestra un mensaje claro con el rol actual y la ruta solicitada.

Además, se incorporó una identificación visual de sesión en la barra superior del sistema, mostrando:

```text
Módulo actual
Usuario autenticado
Rol activo
```

---

### 10.5. Seguridad en vistas JSP y prevención XSS

En la Fase 5 se reforzó la seguridad de las vistas JSP usando JSTL.

Se reemplazaron salidas directas con expresiones JSP como:

```jsp
<%= objeto.getCampo() %>
```

por etiquetas más seguras como:

```jsp
<c:out value="${dato}" />
```

También se protegieron valores mostrados dentro de formularios usando:

```jsp
${fn:escapeXml(valor)}
```

Esto ayuda a evitar que entradas maliciosas, como fragmentos HTML o JavaScript, se ejecuten en el navegador.

Ejemplo de texto malicioso usado en pruebas controladas:

```html
<script>alert("XSS")</script>
```

Esta mejora se aplicó en listados y formularios de los siguientes módulos:

- Pacientes
- Médicos
- Especialidades
- Horarios
- Citas
- Agenda Médica

Además, el módulo Citas ahora muestra mensajes personalizados para operaciones como registrar, actualizar, atender y anular citas.

---

### 10.6. Limpieza final de salidas JSP directas

En la Fase 5.1 se eliminaron las salidas JSP directas restantes en vistas generales y fragmentos de layout.

Archivos actualizados:

- `auth/login.jsp`
- `error/acceso-denegado.jsp`
- `home/inicio.jsp`
- `layout/head.jspf`
- `layout/menu-mobile.jspf`
- `layout/menu-right.jspf`
- `layout/usuario-sesion.jspf`

Se validó con el siguiente comando:

```cmd
findstr /S /N /C:"<%=" src\main\webapp\WEB-INF\views\*.jsp src\main\webapp\WEB-INF\views\*.jspf
```

Resultado esperado:

```text
Sin resultados.
```

Con esta mejora, la capa de presentación del sistema queda más protegida frente a riesgos de Cross-Site Scripting, XSS, manteniendo el funcionamiento de búsqueda, registro, edición, menús por rol y operaciones críticas mediante POST.

---

## 11. Procedimientos almacenados corregidos

Se corrigieron y/o agregaron los siguientes procedimientos:

- `sp_eliminar_paciente`
- `sp_eliminar_medico`
- `sp_eliminar_especialidad`
- `sp_eliminar_horario`

Estos procedimientos ya no borran físicamente la información. Ahora actualizan el campo `estado_registro` a `INACTIVO`.

---

## 12. Clases y archivos actualizados

### 12.1. DAO actualizados

Se actualizaron los DAO para trabajar con eliminación lógica:

- `PacienteDAO.java`
- `MedicoDAO.java`
- `EspecialidadDAO.java`
- `HorarioDAO.java`

Los listados ahora muestran únicamente registros activos.

### 12.2. Controladores actualizados

Se refactorizaron los controladores para separar operaciones GET y POST:

- `PacienteController.java`
- `EspecialidadController.java`
- `MedicoController.java`
- `HorarioController.java`
- `CitaController.java`

También se reforzaron:

- `AuthController.java`
- `AccesoDenegadoController.java`

### 12.3. Vistas actualizadas

Se actualizaron listados, formularios, vistas generales y fragmentos de layout:

- `paciente/list.jsp`
- `paciente/form.jsp`
- `medico/list.jsp`
- `medico/form.jsp`
- `especialidad/list.jsp`
- `especialidad/form.jsp`
- `horario/list.jsp`
- `horario/form.jsp`
- `cita/list.jsp`
- `cita/form.jsp`
- `agenda/list.jsp`
- `auth/login.jsp`
- `error/acceso-denegado.jsp`
- `home/inicio.jsp`
- `layout/head.jspf`
- `layout/menu-mobile.jspf`
- `layout/menu-right.jspf`
- `layout/usuario-sesion.jspf`

---

## 13. Pruebas realizadas

Se realizaron las siguientes pruebas:

- Inicio de sesión con usuario administrador.
- Inicio de sesión con usuario recepcionista.
- Inicio de sesión con usuario doctor.
- Prueba con contraseña incorrecta.
- Acceso a módulos permitidos según rol.
- Bloqueo de rutas no autorizadas.
- Visualización de página de acceso denegado.
- Registro de paciente de prueba.
- Eliminación lógica de paciente.
- Verificación en MySQL de paciente con estado `INACTIVO`.
- Registro de especialidad de prueba.
- Eliminación lógica de especialidad.
- Verificación en MySQL de especialidad con estado `INACTIVO`.
- Registro y eliminación lógica de médico.
- Registro y eliminación lógica de horario.
- Registro de cita médica.
- Edición de cita médica.
- Marcado de cita como `ATENDIDA`.
- Anulación de cita.
- Intento manual de eliminación por URL GET.
- Verificación de mensaje `Operacion no permitida`.
- Prueba controlada con texto tipo script para validar prevención XSS.
- Verificación de ausencia de salidas JSP directas.
- Ejecución de Clean and Build en Apache NetBeans.
- Ejecución del sistema en Apache Tomcat.

Resultado:

```text
El sistema funciona correctamente luego de las mejoras aplicadas en V2.1.
```

---

## 14. Observaciones de seguridad

En la versión V2.1, las contraseñas del sistema fueron migradas a hashes BCrypt. Por ello, la base de datos ya no almacena contraseñas visibles en texto plano.

Además, las operaciones críticas del sistema se ejecutan mediante POST y no mediante GET.

El sistema aplica control de acceso por roles mediante `AuthFilter.java`, bloqueando rutas no autorizadas aunque el usuario intente acceder manualmente por URL.

También se reforzó la capa de presentación mediante JSTL, `c:out`, `fn:escapeXml` y eliminación de salidas JSP directas.

Como mejoras futuras se recomienda implementar:

- Protección CSRF.
- Variables de entorno para credenciales sensibles.
- Relación directa entre usuario DOCTOR y registro de médico.
- Manejo centralizado de errores.
- Auditoría de acciones críticas.
- Recuperación o restablecimiento seguro de contraseñas.
- Módulo visual de gestión de usuarios para ADMIN.

---

## 15. Estado actual

La versión V2.1 se encuentra funcional, con mejoras aplicadas en integridad de datos, autenticación, control de acceso, protección de operaciones críticas y seguridad en vistas JSP.

Estado actual:

- Aplicación web operativa.
- Base de datos conectada correctamente.
- Eliminación lógica implementada.
- Hash de contraseñas implementado con BCrypt.
- Acciones críticas protegidas mediante POST.
- Control de acceso por roles implementado.
- Menús por rol implementados.
- Página de acceso denegado implementada.
- Vistas JSP protegidas con JSTL.
- Salidas JSP directas eliminadas.
- Scripts SQL actualizados.
- Migraciones creadas.
- Documentación técnica principal actualizada.

El sistema está listo para continuar con la Fase 6: creación del módulo visual de gestión de usuarios para el rol ADMIN.

---

## 16. Wireframes Mobile y evidencia responsive

Como respaldo documental para PF3, se define la versión Mobile de pantallas clave usando una grilla de 1 columna para celulares y distribución por columnas para tablet y desktop.

### 16.1. Wireframe Mobile - Login

```text
+----------------------------------+
| NOVASALUD                        |
| Bienvenido                       |
|                                  |
| Usuario                          |
| [____________________________]   |
|                                  |
| Contrasena                       |
| [____________________________]   |
|                                  |
| [        INGRESAR             ]  |
+----------------------------------+
```

### 16.2. Wireframe Mobile - Listado de Pacientes

```text
+----------------------------------+
| Menu | Sistema de Citas          |
| Modulo: Pacientes                |
| Usuario / Rol                    |
+----------------------------------+
| Buscar por DNI/Nombres           |
| [____________________________]   |
| [Buscar] [Limpiar]               |
+----------------------------------+
| ID | Paciente | Telefono         |
| ... registros ...                |
+----------------------------------+
```

### 16.3. Wireframe Mobile - Formulario de Citas

```text
+----------------------------------+
| Paciente (DNI)                   |
| [____________________________]   |
|                                  |
| Especialidad                     |
| [____________________________]   |
| Medico                           |
| [____________________________]   |
| Fecha                            |
| [____________________________]   |
| Hora                             |
| [____________________________]   |
|                                  |
| [       GUARDAR CITA         ]   |
+----------------------------------+
```

### 16.4. Evidencia de clases responsive aplicadas

- Se usa `meta viewport` en `layout/head.jspf`.
- Se aplican clases Bootstrap `col-sm-*` y `col-md-*` en las vistas de módulos.
- Se incorporó soporte móvil en vistas generales, incluyendo `auth/login.jsp` y `error/acceso-denegado.jsp`.
- La navegación lateral usa patrón móvil con `offcanvas`.

### 16.5. Criterio de grilla por dispositivo

- Mobile (<576px): `col-12`.
- Small (>=576px): `col-sm-*` para transición vertical estable.
- Medium (>=768px): `col-md-*` para formularios en dos o más columnas.
- Large (>=992px): `col-lg-*` para dashboard completo con menú lateral.



## 17 Estructura de carpetas del proyecto (actualizada)

La siguiente estructura resume los directorios y archivos principales de la solución para documentación del Capítulo de Arquitectura:

```text
proyecto/
├── ARQUITECTURA_PROYECTO.md
├── README.md
├── CHANGELOG_V2.1.md
├── pom.xml
├── database/
│   ├── 01 -  bd - tablas.sql
│   ├── 02 -  data - usuario.sql
│   ├── 03 -  stores.sql
│   ├── 04 - crear usuario acceso.sql
│   └── migrations/
│       ├── migracion_v2_1_eliminacion_logica.sql
│       └── migracion_v2_1_hash_passwords.sql
├── src/
│   ├── main/
│   │   ├── java/com/mycompany/miprimeraweb/
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── HomeController.java
│   │   │   │   ├── PacienteController.java
│   │   │   │   ├── MedicoController.java
│   │   │   │   ├── EspecialidadController.java
│   │   │   │   ├── HorarioController.java
│   │   │   │   ├── CitaController.java
│   │   │   │   ├── AgendaMedicoController.java
│   │   │   │   └── AccesoDenegadoController.java
│   │   │   ├── service/
│   │   │   │   ├── PacienteService.java
│   │   │   │   ├── MedicoService.java
│   │   │   │   ├── EspecialidadService.java
│   │   │   │   ├── HorarioService.java
│   │   │   │   └── CitaService.java
│   │   │   ├── dao/
│   │   │   │   ├── PacienteDAO.java / PacienteDAOImpl.java
│   │   │   │   ├── MedicoDAO.java / MedicoDAOImpl.java
│   │   │   │   ├── EspecialidadDAO.java / EspecialidadDAOImpl.java
│   │   │   │   ├── HorarioDAO.java / HorarioDAOImpl.java
│   │   │   │   ├── CitaDAO.java / CitaDAOImpl.java
│   │   │   │   └── UsuarioDAO.java / UsuarioDAOImpl.java
│   │   │   ├── model/
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── Paciente.java
│   │   │   │   ├── Medico.java
│   │   │   │   ├── Especialidad.java
│   │   │   │   ├── Horario.java
│   │   │   │   └── Cita.java
│   │   │   ├── filter/
│   │   │   │   └── AuthFilter.java
│   │   │   ├── util/
│   │   │   │   ├── ConexionDB.java
│   │   │   │   └── PasswordUtil.java
│   │   │   └── resources/
│   │   ├── resources/META-INF/persistence.xml
│   │   └── webapp/
│   │       ├── css/app.css
│   │       └── WEB-INF/
│   │           ├── web.xml
│   │           ├── beans.xml
│   │           └── views/
│   │               ├── auth/
│   │               ├── home/
│   │               ├── paciente/
│   │               ├── medico/
│   │               ├── especialidad/
│   │               ├── horario/
│   │               ├── cita/
│   │               ├── agenda/
│   │               ├── error/
│   │               └── layout/
│   └── test/java/com/mycompany/miprimeraweb/
└── target/
```

---

## 18. Diagramas de Arquitectura y UML

### 18.1. Diagrama de Clases UML

![Diagrama de Clases UML](img/Diagrama%20de%20Clases%20UML.png)

### 18.2. Arquitectura del Software

![Arquitectura del software](img/Arquitectura%20del%20software.png)
