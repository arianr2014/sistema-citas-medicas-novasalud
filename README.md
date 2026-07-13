# NovaSalud V3.2.1

Sistema web para gestión clínica de consultas externas: pacientes, médicos, especialidades, horarios, citas, pagos, caja, reportes, estadísticas, usuarios y agenda médica.

## Tecnologías

- Java
- Jakarta Servlet/JSP
- JDBC
- MySQL
- Maven WAR
- Bootstrap Icons / Bootstrap
- Apache Tomcat
- Apache NetBeans

## Base de datos

La versión 3.2.0 usa una base independiente:

```text
bd_citasmedicas_v321
```

Ejecutar en MySQL Workbench únicamente:

```text
database/v321/00_instalacion_completa_bd_citasmedicas_v321.sql
```

## URL esperada

```text
http://localhost:8080/NovaSaludV321/
```

## Usuarios demo

Todos usan la contraseña:

```text
123456
```

Usuarios administrativos:

```text
administrador
recepcionista1
recepcionista2
cajero1
cajero2
direccion
```

Usuarios médicos reales:

```text
valeria.rojas
luis.herrera
camila.torres
marco.salas
ana.beltran
diego.munoz
rafael.quispe
sofia.nunez
bruno.castillo
elena.vargas
patricia.reategui
hector.ramirez
```

## Flujo recomendado de prueba

1. Ejecutar el script completo de base de datos V3.2.1.
2. Abrir el proyecto en NetBeans.
3. Hacer Clean and Build.
4. Ejecutar el proyecto.
5. Ingresar como `administrador` para revisar módulos.
6. Ingresar como `recepcionista1` para crear citas.
7. Ingresar como `cajero1` para registrar pagos parciales o totales.
8. Ingresar como un médico real, por ejemplo `camila.torres`, para validar agenda propia.
9. Revisar reportes, estadísticas y dirección.

## Reglas funcionales clave

- Una cita nueva nace como PROGRAMADA.
- Recepción puede reprogramar, cancelar, marcar en espera o no asistió.
- Caja registra pagos pendientes, parciales o completos.
- El médico solo ve su propia agenda.
- El médico solo puede atender una cita pagada.
- Cada usuario DOCTOR debe estar asociado a un médico real activo.
- La historia clínica base se genera automáticamente al crear pacientes.

## Nota V3.2.1

La versión V3.2.1 incorpora el registro clínico por atención médica. El médico responsable debe completar síntomas, diagnóstico, tratamiento, receta e indicaciones antes de marcar la cita como atendida. La historia clínica se identifica con el DNI del paciente.
