# Plan de Implementacion MVC Monolitico (sin API REST)

Fecha: 2026-04-14
Proyecto: MiPrimeraWeb

## 1) Enfoque arquitectonico
- Se usara un solo proyecto web Java con patron MVC.
- No se expondra API REST.
- El flujo sera: Vista (JSP/HTML) -> Controlador (Servlet) -> Servicio (opcional, recomendado) -> DAO -> Base de datos.

## 2) Estructura de paquetes recomendada
- com.mycompany.miprimeraweb.model
- com.mycompany.miprimeraweb.dao
- com.mycompany.miprimeraweb.service
- com.mycompany.miprimeraweb.controller
- com.mycompany.miprimeraweb.util

## 3) Capas y responsabilidades
### 3.1 Modelo (entidades)
- Representa tablas: Paciente, Medico, Especialidad, Horario, Cita, Usuario.
- Solo atributos, constructores, getters y setters.

### 3.2 DAO
- Encapsula acceso a datos con JDBC/JPA.
- Ejecuta CRUD y/o stored procedures.
- No contiene logica de vista ni manejo de UI.

### 3.3 Servicio (recomendado)
- Aplica reglas de negocio (validaciones, flujos).
- Orquesta uno o varios DAO.
- Mantiene la logica fuera del Servlet.

### 3.4 Controlador (Servlet)
- Recibe acciones del usuario (listar, nuevo, guardar, editar, eliminar).
- Valida datos basicos de entrada.
- Invoca servicios/DAO y decide la vista destino.

### 3.5 Vista (JSP)
- Muestra formularios, listas y mensajes.
- Evitar logica de negocio en JSP.

## 4) Plan por fases
### Fase 1: Base tecnica
- Confirmar dependencia MySQL en pom.xml (ya agregada).
- Configurar conexion a BD en util.
- Definir manejo estandar de excepciones y mensajes.

### Fase 2: Modulo Paciente (vertical completo)
- Crear entidad Paciente.
- Crear PacienteDAO con metodos: listar, obtenerPorId, registrar, actualizar, eliminar.
- Implementar llamadas a SP cuando aplique.
- Crear PacienteService para validaciones.
- Crear PacienteController (Servlet) con doGet/doPost.
- Crear JSP: pacientes-lista y pacientes-form.

### Fase 3: Modulo Medico
- Repetir patron de Paciente.
- Incluir relacion con Especialidad.

### Fase 4: Modulo Cita
- Crear entidad Cita con relaciones a Paciente y Medico.
- Implementar registrar/actualizar/listar/eliminacion logica.
- Validar agenda, estado y consistencia de datos.

### Fase 5: Integracion final
- Conectar navegacion entre vistas.
- Homogeneizar estilo de mensajes de exito/error.
- Probar flujos completos en servidor local.

## 5) Estrategia de datos (muy importante)
- Elegir una estrategia por operacion:
  - O JPA/JDBC directo.
  - O Stored Procedure.
- Evitar duplicar la misma operacion en dos enfoques a la vez.

## 6) Convencion minima sugerida
- Controladores terminan en Controller.
- DAOs terminan en DAO.
- Servicios terminan en Service.
- JSPs en carpetas por modulo (por ejemplo: /paciente/).

## 7) Riesgos y mitigacion
- Riesgo: mezclar logica en Servlet y JSP.
  - Mitigacion: toda regla de negocio en Service.
- Riesgo: inconsistencias entre SP y tablas.
  - Mitigacion: validar columnas antes de codificar DAO.
- Riesgo: credenciales expuestas.
  - Mitigacion: externalizar configuracion por entorno.

## 8) Sprint inicial recomendado (3 dias)
### Dia 1
- Conexion DB funcional.
- Entidad Paciente y PacienteDAO base.

### Dia 2
- PacienteService y PacienteController.
- JSP de alta y listado.

### Dia 3
- Pruebas funcionales completas de Paciente.
- Ajuste de errores y convenciones.

## 9) Decision sobre plan.md vs Skills
- Para este caso, lo mejor es plan.md dentro del proyecto.
- Motivo: sirve como guia tecnica del equipo y queda versionado con el codigo.
- Skills conviene cuando quieres configurar el comportamiento del asistente de VS Code, no para documentacion funcional del sistema.

## 10) Proximo paso sugerido
- Implementar primero PacienteController + PacienteDAO + vistas JSP y validar punta a punta.

## 11) Analisis de prototipos actuales (carpeta src/main/prototipos)
- Prototipos detectados:
  - pacientes.html y pacientes-form.html
  - medicos.html y medicos-form.html
  - citas.html y citas-form.html
- Estilo visual base:
  - Bootstrap 5 + Bootstrap Icons.
  - Paleta verde corporativa (#198754) definida en css/estilos.css.
  - Layout con cards, tablas y formularios responsivos.
- Hallazgos a corregir al migrar a JSP:
  - Inconsistencia de nombres en enlaces: se usa medico-form.html, pero el archivo es medicos-form.html.
  - En pacientes-form.html hay un error de comillas en onclick del boton Guardar.
  - Parte de los prototipos usa datos simulados en JavaScript; se reemplazara con datos reales desde controlador.

## 12) Ajuste solicitado: Login + Principal + Menu derecho
### 12.1 Paginas funcionales a construir
- Login (entrada al sistema).
- Principal (dashboard) con menu lateral derecho.
- Modulo Pacientes: listado y formulario.
- Modulo Medicos: listado y formulario.
- Modulo Citas: listado y formulario.

### 12.2 Estructura de vistas propuesta (JSP)
- /WEB-INF/views/auth/login.jsp
- /WEB-INF/views/layout/main.jsp
- /WEB-INF/views/layout/menu-right.jsp
- /WEB-INF/views/paciente/list.jsp
- /WEB-INF/views/paciente/form.jsp
- /WEB-INF/views/medico/list.jsp
- /WEB-INF/views/medico/form.jsp
- /WEB-INF/views/cita/list.jsp
- /WEB-INF/views/cita/form.jsp

Nota: mantener vistas bajo /WEB-INF evita acceso directo por URL y obliga a pasar por controlador.

### 12.3 Navegacion (sin REST)
- GET /login -> muestra login.jsp
- POST /login -> valida usuario y crea sesion
- GET /inicio -> muestra principal con menu derecho
- GET /pacientes -> lista pacientes
- GET /pacientes/form -> formulario nuevo/editar
- POST /pacientes/guardar -> registra/actualiza
- POST /pacientes/eliminar -> eliminacion logica
- Mismo patron para /medicos y /citas

### 12.4 Menu lateral derecho (principal)
- Ubicar menu en panel derecho fijo/estable en escritorio.
- Opciones:
  - Inicio
  - Pacientes
  - Medicos
  - Citas
  - Cerrar sesion
- En mobile, colapsar a offcanvas de Bootstrap para mantener usabilidad.

### 12.5 Controladores (Servlet) propuestos
- AuthController: login, logout, validacion de sesion.
- HomeController: dashboard principal.
- PacienteController: listar, form, guardar, eliminar.
- MedicoController: listar, form, guardar, eliminar.
- CitaController: listar, form, guardar, eliminar.

### 12.6 Servicios y DAO por modulo
- AuthService + UsuarioDAO.
- PacienteService + PacienteDAO.
- MedicoService + MedicoDAO.
- CitaService + CitaDAO.
- EspecialidadDAO y HorarioDAO como soporte del modulo Cita.

### 12.7 Plan de implementacion UI en orden recomendado
1. Crear login.jsp + AuthController + control de sesion.
2. Crear main.jsp con layout base y menu-right.jsp reutilizable.
3. Migrar pacientes.html y pacientes-form.html a JSP reales.
4. Migrar medicos.html y medicos-form.html a JSP reales.
5. Migrar citas.html y citas-form.html a JSP reales.
6. Conectar cada pantalla con su controlador y datos de BD.

### 12.8 Criterios de aceptacion
- Solo usuarios autenticados acceden a /inicio y modulos.
- El menu derecho aparece en todas las pantallas internas.
- Se mantiene el estilo verde de prototipos.
- CRUD funcional de Pacientes, Medicos y Citas desde vistas MVC.

## 13) Recomendacion final (plan.md vs Skills)
- Para este escenario, mantener el plan en plan.md es la mejor opcion.
- Skills solo se usaria si deseas automatizar comportamiento del asistente en tareas repetitivas de desarrollo.
