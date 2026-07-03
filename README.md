# Sistema de Citas Médicas NovaSalud - V2.1

## 1. Descripción general

El Sistema de Citas Médicas NovaSalud es una aplicación web desarrollada en Java para la gestión de pacientes, médicos, especialidades, horarios y citas médicas.

El sistema permite administrar el flujo principal de atención médica, desde el registro de pacientes y médicos hasta la programación, atención y anulación de citas. Además, cuenta con un dashboard inicial que muestra un resumen operativo de las citas del día y la actividad semanal.

La versión V2.1 incorpora mejoras de integridad de datos, seguridad de autenticación y protección de operaciones críticas.

---

## 2. Tecnologías utilizadas

- Java
- Jakarta EE 10
- JSP
- Servlets
- JDBC
- MySQL
- MySQL Workbench
- Apache Tomcat
- Maven
- HTML
- CSS
- Bootstrap / estilos personalizados
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

---

## 9. Instalación y ejecución

### 9.1. Requisitos previos

- JDK instalado
- Apache NetBeans
- Apache Tomcat configurado en NetBeans
- MySQL Server
- MySQL Workbench
- Maven

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

### 12.3. Vistas actualizadas

Se actualizaron los JSP de listado para reemplazar enlaces críticos por formularios POST:

- `paciente/list.jsp`
- `especialidad/list.jsp`
- `medico/list.jsp`
- `horario/list.jsp`
- `cita/list.jsp`

---

## 13. Pruebas realizadas

Se realizaron las siguientes pruebas:

- Inicio de sesión con usuario administrador.
- Inicio de sesión con usuario recepcionista.
- Inicio de sesión con usuario doctor.
- Prueba con contraseña incorrecta.
- Acceso a todos los módulos principales.
- Registro de paciente de prueba.
- Eliminación lógica de paciente.
- Verificación en MySQL de paciente con estado `INACTIVO`.
- Registro de especialidad de prueba.
- Eliminación lógica de especialidad.
- Verificación en MySQL de especialidad con estado `INACTIVO`.
- Registro y eliminación lógica de médico.
- Registro y eliminación lógica de horario.
- Registro de cita médica.
- Marcado de cita como `ATENDIDA`.
- Anulación de cita.
- Intento manual de eliminación por URL GET.
- Verificación de mensaje `Operacion no permitida por este metodo`.
- Ejecución de Clean and Build en Apache NetBeans.
- Ejecución del sistema en Apache Tomcat.

Resultado: el sistema funciona correctamente luego de las mejoras V2.1.

---

## 14. Observaciones de seguridad

En la versión V2.1, las contraseñas del sistema fueron migradas a hashes BCrypt. Por ello, la base de datos ya no almacena contraseñas visibles en texto plano.

Además, las operaciones críticas del sistema se ejecutan mediante POST y no mediante GET.

Como mejoras futuras se recomienda implementar:

- Protección CSRF.
- Validación estricta por roles.
- Mejor control de sesión.
- Sanitización de salidas en JSP para prevenir XSS.
- Manejo centralizado de errores.
- Variables de entorno para credenciales sensibles.
- Recuperación o restablecimiento seguro de contraseñas.

---

## 15. Estado actual

La versión V2.1 se encuentra funcional, con mejoras aplicadas en integridad de datos, autenticación y protección de operaciones críticas.

Estado actual:

- Aplicación web operativa.
- Base de datos conectada correctamente.
- Eliminación lógica implementada.
- Hash de contraseñas implementado con BCrypt.
- Acciones críticas protegidas mediante POST.
- Scripts SQL actualizados.
- Migraciones creadas.
- Documentación técnica principal actualizada.

El sistema está listo para continuar con mejoras de control de roles, seguridad en vistas JSP, mensajes funcionales y preparación de presentación final.