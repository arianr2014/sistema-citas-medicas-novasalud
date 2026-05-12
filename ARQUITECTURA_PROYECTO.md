# Revision de arquitectura - MiPrimeraWeb

Fecha: 2026-04-14

## 1) Tipo de proyecto
- Proyecto Maven empaquetado como WAR.
- Stack principal: Jakarta EE 10 (API con alcance provided).
- Version de Java configurada para compilacion: 11.
- Tipo de arquitectura identificado: arquitectura por capas con enfoque MVC en la capa web.
- Capas visibles en el proyecto: controller, service, dao, model, filter y resources.

## 1.1) Patrones de diseño identificados
- MVC: los servlets en controller actuan como capa de control, los JSP como vistas y los modelos en model representan el dominio.
- DAO: la capa dao encapsula el acceso a datos y las operaciones SQL o con procedimientos almacenados.
- Service Layer: la capa service concentra reglas de negocio, validaciones y coordinacion entre controladores y DAO.
- Front Controller parcial: los servlets centralizan la recepcion de solicitudes por modulo y delegan el flujo hacia las vistas.
- Utility Class: ConexionDB concentra la creacion de conexiones JDBC y la lectura de configuracion.
- Singleton-like para acceso a conexion: ConexionDB no se instancia y expone un punto unico de acceso estatico a getConnection().

## 2) Estructura identificada
- src/main/java: capa backend Java.
- src/main/resources/META-INF/persistence.xml: base para JPA (sin configurar aun).
- src/main/webapp: recursos web y configuracion WEB-INF.
- src/main/prototipos: vistas HTML de prototipo (citas, medicos, pacientes).
- target: artefactos generados de compilacion/despliegue.

## 3) Componentes backend actuales
- Configuracion JAX-RS global:
  - Clase: com.mycompany.miprimeraweb.JakartaRestConfiguration
  - Base path REST: /resources
- Recurso REST de prueba:
  - Clase: com.mycompany.miprimeraweb.resources.JakartaEE10Resource
  - Endpoint: GET /resources/jakartaee10
  - Respuesta actual: "ping Jakarta EE"

## 4) Configuracion web
- web.xml en version Jakarta Web 6.0.
- Solo contiene session-timeout (30 min).
- No hay servlets/controladores declarados explicitamente en web.xml.

## 5) Persistencia (estado actual)
- Existe persistence.xml con persistence-unit "my_persistence_unit".
- No tiene proveedor, datasource, entidades ni propiedades definidas.
- Conclusión: la capa de datos no esta implementada todavia.

## 6) Hallazgos clave
- El proyecto esta en estado base/plantilla de NetBeans para Jakarta EE.
- Ya hay una prueba REST minima funcional (ping).
- No hay entidades JPA, repositorios, servicios ni API de negocio para citas/medicos/pacientes.
- Los HTML de prototipo todavia no estan conectados al backend.

## 7) Prioridad sugerida para completar codigo
1. Definir modelo de dominio (Paciente, Medico, Cita) como entidades JPA.
2. Completar persistence.xml con datasource y propiedades de entorno.
3. Crear capa de acceso a datos (DAO/Repository) por entidad.
4. Crear capa de servicios (reglas de negocio).
5. Exponer endpoints REST CRUD para cada modulo.
6. Conectar formularios/prototipos HTML al backend (fetch/AJAX o migracion a JSP/JSF si aplica).
7. Agregar validaciones y manejo de errores con respuestas HTTP consistentes.

## 8) Riesgos actuales
- Sin configuracion de persistencia no hay almacenamiento real.
- Si no se separan capas (resource/service/repository), el crecimiento del proyecto se vuelve dificil.
- Diferencias entre prototipos y modelo real pueden generar retrabajo si no se alinea pronto.

## 9) Siguiente entregable recomendado
- Documento tecnico corto con contratos de API (rutas, payloads y codigos HTTP) para:
  - /pacientes
  - /medicos
  - /citas

## 10) Base de datos MySQL (configuracion entregada)

### 10.1 Creacion de BD y usuario
- Motor: MySQL
- Base de datos: bd_citasmedicas
- Usuario: usuario_citas
- Password: ISO/IEC27001
- Host: localhost
- Permisos: ALL PRIVILEGES sobre bd_citasmedicas

Script de referencia:

```sql
CREATE DATABASE bd_citasmedicas;
USE bd_citasmedicas;

CREATE USER 'usuario_citas'@'localhost' IDENTIFIED BY 'ISO/IEC27001';
GRANT ALL PRIVILEGES ON bd_citasmedicas.* TO 'usuario_citas'@'localhost';
FLUSH PRIVILEGES;
```

### 10.2 Tablas del modelo actual
- paciente
- medico
- especialidad
- horario
- cita
- usuario

### 10.3 Relaciones principales
- medico.id_especialidad -> especialidad.id_especialidad
- horario.id_medico -> medico.id_medico
- cita.id_paciente -> paciente.id_paciente
- cita.id_medico -> medico.id_medico

### 10.4 Reglas relevantes del esquema
- paciente.dni es unico.
- usuario.username es unico.
- cita.estado_registro tiene valor por defecto ACTIVO.
- Todas las PK son autoincrementales en tipo int.

### 10.5 Implicaciones para el backend (Jakarta EE)
- persistence.xml debe apuntar a bd_citasmedicas con el usuario usuario_citas.
- Se deben mapear entidades JPA para: Paciente, Medico, Especialidad, Horario, Cita y Usuario.
- Se recomienda usar LocalDate para campos DATE, LocalTime para TIME y LocalDateTime para DATETIME.
- Relaciones JPA minimas esperadas:
  - Medico ManyToOne Especialidad
  - Horario ManyToOne Medico
  - Cita ManyToOne Paciente
  - Cita ManyToOne Medico

### 10.6 Nota de seguridad
- Evitar dejar credenciales reales en texto plano dentro del repositorio.
- Para desarrollo local, mover usuario/password a variables de entorno o configuracion externa.

## 11) Procedimientos almacenados (stored procedures)

### 11.1 Procedimientos de registro
- sp_registrar_paciente
- sp_registrar_medico
- sp_registrar_cita

### 11.2 Procedimientos de actualizacion
- sp_actualizar_paciente
- sp_actualizar_medico
- sp_actualizar_cita

### 11.3 Procedimientos de eliminacion logica
- sp_eliminar_paciente
- sp_eliminar_medico
- sp_eliminar_cita

### 11.4 Procedimientos de consulta
- sp_listar_medicos
- sp_listar_citas

### 11.5 Contrato funcional resumido
- Paciente:
  - Registrar: recibe dni, nombres, apellidos, telefono, direccion, usuario.
  - Actualizar: recibe id_paciente y datos principales.
  - Eliminar: cambia estado a INACTIVO por id.
- Medico:
  - Registrar: recibe nombres, apellidos, especialidad, telefono, usuario.
  - Actualizar: recibe id_medico y datos principales.
  - Eliminar: cambia estado a INACTIVO por id.
- Cita:
  - Registrar: recibe paciente, medico, fecha, hora, estado, observaciones, usuario.
  - Actualizar: modifica campos de la cita activa.
  - Eliminar: eliminacion logica con estado_registro = INACTIVO.
  - Listar: retorna datos de paciente, medico, especialidad y detalle de cita.

### 11.6 Nota tecnica de consistencia (importante)
- En el esquema de tablas documentado, medico y paciente no incluyen la columna estado.
- Sin embargo, sp_eliminar_medico y sp_eliminar_paciente hacen UPDATE sobre estado.
- Esto puede fallar en ejecucion con error de columna inexistente.
- Recomendacion:
  - Opcion A: agregar columna estado en medico y paciente (por ejemplo, VARCHAR(10) con default ACTIVO).
  - Opcion B: cambiar esos dos SP para usar eliminacion fisica (DELETE) o una columna existente.

### 11.7 Implicaciones para capa Java (DAO/Repository)
- Si el proyecto usara SP como contrato principal, la capa de datos debe invocar CallableStatement.
- Alternativamente, con JPA se puede usar StoredProcedureQuery para mantener integracion con EntityManager.
- Definir una estrategia unica por modulo (solo JPA CRUD o SP + JPA) para evitar duplicidad de logica.


