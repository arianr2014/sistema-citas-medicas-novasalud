# Arquitectura del Proyecto - Sistema de Citas Médicas NovaSalud V2.1

## 1. Descripción general

El Sistema de Citas Médicas NovaSalud es una aplicación web desarrollada en Java para gestionar pacientes, médicos, especialidades, horarios y citas médicas.

El sistema trabaja con una arquitectura por capas basada en el patrón MVC, complementada con DAO, Service Layer, filtros de seguridad, utilidades de autenticación y conexión JDBC hacia una base de datos MySQL.

La versión V2.1 incorpora mejoras de integridad de datos, autenticación segura y protección de operaciones críticas.

---

## 2. Objetivo de la arquitectura

El objetivo de la arquitectura es separar responsabilidades dentro del proyecto para facilitar:

- Mantenimiento del código.
- Reutilización de lógica.
- Separación entre presentación, negocio y persistencia.
- Mayor orden en el desarrollo.
- Escalabilidad futura.
- Mejor control de seguridad y sesión.
- Mayor facilidad para probar y corregir errores.

---

## 3. Patrón arquitectónico aplicado

El proyecto aplica una arquitectura MVC con capas complementarias:

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

Cada capa cumple una función específica dentro del sistema.

---

## 4. Vista general de capas

### 4.1. Capa de presentación

La capa de presentación está compuesta por archivos JSP, HTML, CSS y JavaScript.

Su responsabilidad es mostrar la información al usuario y enviar solicitudes al backend mediante formularios, botones y enlaces del sistema.

Ubicación principal:

```text
src/main/webapp
src/main/webapp/WEB-INF/views
```

Principales vistas del sistema:

- Login
- Dashboard
- Listado de pacientes
- Formulario de pacientes
- Listado de médicos
- Formulario de médicos
- Listado de especialidades
- Formulario de especialidades
- Listado de horarios
- Formulario de horarios
- Listado de citas
- Agenda médica

Las vistas internas se ubican bajo `WEB-INF/views`, lo cual evita que puedan ser accedidas directamente desde el navegador sin pasar por los controladores.

---

### 4.2. Capa Controller

La capa Controller está formada por Servlets. Su función es recibir solicitudes HTTP, coordinar la lógica correspondiente y redirigir hacia la vista adecuada.

Ubicación:

```text
src/main/java/com/mycompany/miprimeraweb/controller
```

Responsabilidades principales:

- Recibir parámetros de formularios.
- Validar acciones solicitadas.
- Invocar servicios.
- Guardar mensajes de éxito o error.
- Redirigir o reenviar a vistas JSP.
- Controlar el flujo de navegación del sistema.
- Separar operaciones GET y POST.

Ejemplos de controladores:

- `LoginController`
- `PacienteController`
- `MedicoController`
- `EspecialidadController`
- `HorarioController`
- `CitaController`
- `AgendaMedicaController`
- `DashboardController`

---

### 4.3. Separación de responsabilidades HTTP

En la versión V2.1 se reforzó la arquitectura de los controladores separando las operaciones según el método HTTP utilizado.

```text
GET  = listar, buscar, abrir formularios y editar visualmente.
POST = guardar, actualizar, eliminar y cambiar estados.
```

Esta separación evita que acciones críticas se ejecuten desde URLs manipulables y mejora la seguridad del sistema.

Acciones protegidas:

- Eliminación lógica de pacientes.
- Eliminación lógica de médicos.
- Eliminación lógica de especialidades.
- Eliminación lógica de horarios.
- Atención de citas.
- Anulación de citas.

Controladores actualizados:

- `PacienteController.java`
- `EspecialidadController.java`
- `MedicoController.java`
- `HorarioController.java`
- `CitaController.java`

Vistas actualizadas:

- `paciente/list.jsp`
- `especialidad/list.jsp`
- `medico/list.jsp`
- `horario/list.jsp`
- `cita/list.jsp`

---

### 4.4. Capa Service

La capa Service contiene reglas de negocio y validaciones antes de acceder a la base de datos.

Ubicación:

```text
src/main/java/com/mycompany/miprimeraweb/service
```

Responsabilidades principales:

- Validar datos antes de registrar.
- Evitar reglas inválidas del negocio.
- Coordinar operaciones entre Controller y DAO.
- Centralizar lógica que no debe estar en la vista ni directamente en el DAO.

Ejemplos de reglas aplicadas:

- Validación de datos obligatorios.
- Validación de fechas de citas.
- Validación de cruce de horarios.
- Validación de existencia de registros.
- Control de estados de citas.

---

### 4.5. Capa DAO

La capa DAO, Data Access Object, se encarga del acceso a datos. Esta capa comunica la aplicación Java con la base de datos MySQL usando JDBC.

Ubicación:

```text
src/main/java/com/mycompany/miprimeraweb/dao
```

Responsabilidades principales:

- Ejecutar consultas SQL.
- Ejecutar procedimientos almacenados.
- Registrar datos.
- Actualizar datos.
- Consultar listados.
- Aplicar eliminación lógica.
- Transformar resultados de MySQL en objetos Java.

DAO principales:

- `UsuarioDAO`
- `PacienteDAO`
- `MedicoDAO`
- `EspecialidadDAO`
- `HorarioDAO`
- `CitaDAO`
- `DashboardDAO`

En la versión V2.1 se actualizaron los DAO de pacientes, médicos, especialidades y horarios para trabajar con eliminación lógica mediante el campo `estado_registro`.

---

### 4.6. Capa Model

La capa Model contiene las clases que representan las entidades principales del sistema.

Ubicación:

```text
src/main/java/com/mycompany/miprimeraweb/model
```

Modelos principales:

- `Usuario`
- `Paciente`
- `Medico`
- `Especialidad`
- `Horario`
- `Cita`

Estas clases permiten transportar datos entre Controller, Service, DAO y vistas.

---

### 4.7. Capa Filter

La capa Filter controla la seguridad básica de acceso al sistema.

Ubicación:

```text
src/main/java/com/mycompany/miprimeraweb/filter
```

Responsabilidades principales:

- Verificar si el usuario inició sesión.
- Evitar acceso directo a rutas protegidas.
- Validar acceso según rol.
- Redirigir al login cuando no existe sesión activa.

Roles identificados en el sistema:

- ADMIN
- RECEPCIONISTA
- DOCTOR

---

### 4.8. Capa Util

La capa Util contiene clases auxiliares reutilizables.

Ubicación:

```text
src/main/java/com/mycompany/miprimeraweb/util
```

Clases principales:

```text
ConexionDB.java
PasswordUtil.java
```

`ConexionDB.java` centraliza la conexión JDBC hacia MySQL.

Datos principales de conexión:

```text
Base de datos: bd_citasmedicas
Host: localhost
Puerto: 3306
Usuario BD: usuario_citas
```

`PasswordUtil.java` centraliza la generación y validación de contraseñas mediante BCrypt. Su objetivo es evitar la comparación directa de contraseñas en texto plano y permitir que el sistema valide el acceso contra hashes almacenados en la base de datos.

---

## 5. Base de datos

El sistema utiliza MySQL como motor de base de datos.

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

La carpeta `database` contiene los scripts necesarios para crear y mantener la base de datos del proyecto.

Estructura:

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

## 6. Procedimientos almacenados

El sistema utiliza procedimientos almacenados para operaciones principales sobre pacientes, médicos y citas.

Procedimientos relevantes:

- `sp_registrar_paciente`
- `sp_actualizar_paciente`
- `sp_eliminar_paciente`
- `sp_registrar_medico`
- `sp_actualizar_medico`
- `sp_eliminar_medico`
- `sp_registrar_cita`
- `sp_actualizar_cita`
- `sp_eliminar_cita`
- `sp_eliminar_especialidad`
- `sp_eliminar_horario`
- `sp_listar_medicos`
- `sp_listar_citas`

En la versión V2.1 se corrigieron procedimientos de eliminación para evitar borrado físico y trabajar con eliminación lógica.

---

## 7. Eliminación lógica en V2.1

Antes de la versión V2.1, algunos módulos podían eliminar registros físicamente de la base de datos.

En la versión V2.1 se implementó eliminación lógica mediante el campo:

```text
estado_registro
```

Valores utilizados:

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

- Conserva historial de información.
- Evita pérdida definitiva de registros.
- Reduce problemas con claves foráneas.
- Mejora la trazabilidad del sistema.
- Facilita auditoría futura.
- Permite recuperar información si fuera necesario.

---

## 8. Flujo general del sistema

El flujo general de una operación dentro del sistema es el siguiente:

```text
Usuario interactúa con una vista JSP
        ↓
Servlet Controller recibe la solicitud
        ↓
Service valida reglas de negocio
        ↓
DAO ejecuta consulta o procedimiento almacenado
        ↓
MySQL responde con datos
        ↓
Controller envía resultado a la vista JSP
```

Ejemplo aplicado al registro de paciente:

```text
Formulario paciente.jsp
        ↓
PacienteController
        ↓
PacienteService
        ↓
PacienteDAO
        ↓
sp_registrar_paciente
        ↓
Tabla paciente
```

---

## 9. Seguridad actual

El sistema cuenta con seguridad básica mediante:

- Login.
- Manejo de sesión.
- Filtros de acceso.
- Roles de usuario.
- Validación de rutas protegidas.
- Usuario de base de datos limitado a `localhost`.
- Permisos de base de datos restringidos a `bd_citasmedicas`.
- Almacenamiento de contraseñas mediante hashes BCrypt.
- Operaciones críticas ejecutadas mediante POST y no mediante GET.

Roles disponibles:

```text
ADMIN
RECEPCIONISTA
DOCTOR
```

Credenciales de prueba del sistema:

```text
administrador / 123456 / ADMIN
recepcionista / 123456 / RECEPCIONISTA
doctor / 123456 / DOCTOR
```

Estas contraseñas son credenciales de prueba. En la base de datos no se almacenan como texto plano, sino como hashes BCrypt.

## Control de acceso por roles en V2.1

En la Fase 4 de la versión V2.1 se reforzó el control de acceso por roles mediante `AuthFilter.java`.

El sistema aplica un esquema RBAC, Role-Based Access Control, donde cada usuario autenticado tiene un rol y dicho rol determina a qué módulos puede acceder.

Roles definidos:

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

El filtro no solo protege el menú visible, sino que también bloquea el acceso manual por URL. Esto evita que un usuario ingrese directamente a rutas no autorizadas escribiéndolas en el navegador.

Ejemplo:

```text
/medicos
/especialidades
/horarios
```

Si un usuario sin permiso intenta acceder a una ruta restringida, el sistema lo redirige a una página personalizada de acceso denegado.

Archivos relacionados:

- `AuthFilter.java`
- `AuthController.java`
- `AccesoDenegadoController.java`
- `error/acceso-denegado.jsp`
- `layout/menu-right.jspf`
- `layout/menu-mobile.jspf`
- `layout/usuario-sesion.jspf`

También se agregó un indicador visual del módulo activo en el menú y una identificación de sesión en la barra superior, mostrando el usuario autenticado y el rol activo.
---

## 10. Mejoras pendientes de seguridad

Como parte de futuras versiones, se recomienda implementar:

- Protección CSRF.
- Sanitización de salidas en JSP para prevenir XSS.
- Mayor control de permisos por rol.
- Relación directa entre usuario DOCTOR y médico.
- Manejo centralizado de errores.
- Retiro de credenciales reales del código fuente.
- Variables de entorno para credenciales sensibles.
- Restablecimiento seguro de contraseñas.

---

## 11. Ventajas de la arquitectura actual

La arquitectura actual permite:

- Mejor organización del código.
- Separación clara de responsabilidades.
- Fácil mantenimiento.
- Mejor comprensión para exposición académica.
- Posibilidad de escalar funcionalidades.
- Mayor facilidad para aplicar seguridad progresiva.
- Mejor trazabilidad entre vista, lógica y base de datos.

---

## 12. Estado actual de la versión V2.1

La versión V2.1 se encuentra funcional y validada.

Pruebas realizadas:

- Inicio de sesión.
- Acceso a módulos principales.
- Registro de pacientes.
- Eliminación lógica de pacientes.
- Registro de especialidades.
- Eliminación lógica de especialidades.
- Registro y eliminación lógica de médicos.
- Registro y eliminación lógica de horarios.
- Registro de citas.
- Marcado de citas como ATENDIDA.
- Anulación de citas.
- Validación en MySQL de registros con estado `INACTIVO`.
- Compilación mediante Clean and Build.
- Ejecución del proyecto en Apache Tomcat.

Resultado:

```text
Sistema funcional con mejoras de integridad de datos, autenticación y protección de operaciones críticas.
```

---

## 13. Conclusión técnica

El Sistema de Citas Médicas NovaSalud V2.1 mantiene una arquitectura MVC por capas, con DAO, Service Layer, filtros de seguridad, utilidades de autenticación y conexión JDBC a MySQL.

Las mejoras aplicadas fortalecen la integridad de datos, la autenticación y la seguridad de operaciones críticas. El sistema queda preparado para continuar con mejoras de control de roles, protección CSRF, sanitización de vistas JSP, auditoría y refinamiento funcional.