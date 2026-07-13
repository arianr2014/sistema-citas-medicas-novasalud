# Arquitectura NovaSalud V3.2.1

## Patrón principal

El sistema mantiene arquitectura MVC por capas:

```text
Controller -> Service -> DAO -> MySQL
JSP/JSTL -> Controller
```

## Capas

- **Controller:** recibe peticiones HTTP y coordina navegación.
- **Service:** aplica reglas de negocio.
- **DAO:** realiza consultas y operaciones JDBC.
- **Model:** representa entidades del dominio.
- **JSP/JSTL:** presenta la interfaz con datos escapados.
- **Filters:** controlan autenticación, autorización, CSRF y codificación UTF-8.

## Reglas de seguridad relevantes

- Login con usuario y contraseña solamente.
- El rol se obtiene desde la base de datos.
- Un usuario DOCTOR debe estar asociado a un médico real activo.
- El médico solo visualiza su propia agenda.
- Las operaciones críticas usan POST.
- Se aplica CSRF básico en formularios.
- Sesión invalidada si el usuario cambia de estado o versión.

## Base de datos

La versión usa:

```text
bd_citasmedicas_v321
```

Incluye entidades clínicas, usuarios, pagos, historia clínica básica, atención médica, auditoría y configuración.
