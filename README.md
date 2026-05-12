# Sistema de Citas Médicas – NovaSalud

Aplicación web para la gestión de citas médicas de la clínica **NovaSalud**, desarrollada con **Django 6**.

## Funcionalidades

- **Dashboard** con estadísticas en tiempo real (citas del día, pacientes, médicos, próximas citas).
- **Gestión de Citas**: crear, editar, ver detalle y cancelar citas médicas.
- **Gestión de Pacientes**: registro, edición y búsqueda de pacientes con historial de citas.
- **Catálogo de Médicos**: lista de médicos por especialidad con agenda de citas.
- **Filtros** en la lista de citas por fecha, estado y médico.
- **Panel de Administración** de Django para gestión completa de todos los datos.

## Modelos de datos

| Modelo | Descripción |
|---|---|
| `Especialidad` | Especialidades médicas (Cardiología, Pediatría, etc.) |
| `Medico` | Médicos adscritos a la clínica |
| `Paciente` | Pacientes registrados en el sistema |
| `Cita` | Citas médicas con estado (programada, confirmada, atendida, cancelada) |

## Instalación y ejecución

### Requisitos previos
- Python 3.10+

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/arianr2014/sistema-citas-medicas-novasalud.git
cd sistema-citas-medicas-novasalud

# 2. Crear entorno virtual
python -m venv venv
source venv/bin/activate        # Linux/macOS
# venv\Scripts\activate         # Windows

# 3. Instalar dependencias
pip install -r requirements.txt

# 4. Aplicar migraciones
python manage.py migrate

# 5. Cargar datos iniciales (especialidades, médicos y pacientes de ejemplo)
python manage.py loaddata datos_iniciales

# 6. Crear superusuario para el panel de administración
python manage.py createsuperuser

# 7. Iniciar el servidor de desarrollo
python manage.py runserver
```

Abre tu navegador en **http://127.0.0.1:8000/**

El panel de administración está disponible en **http://127.0.0.1:8000/admin/**

## Ejecutar pruebas

```bash
python manage.py test citas
```

## Estructura del proyecto

```
sistema-citas-medicas-novasalud/
├── manage.py
├── requirements.txt
├── novasalud/               # Configuración del proyecto Django
│   ├── settings.py
│   └── urls.py
└── citas/                   # Aplicación principal
    ├── models.py            # Modelos: Especialidad, Medico, Paciente, Cita
    ├── views.py             # Vistas de la aplicación
    ├── forms.py             # Formularios
    ├── urls.py              # Rutas de la aplicación
    ├── admin.py             # Configuración del panel de administración
    ├── tests.py             # Pruebas unitarias e integración
    ├── fixtures/
    │   └── datos_iniciales.json  # Datos de ejemplo
    ├── migrations/
    └── templates/citas/     # Plantillas HTML (Bootstrap 5)
```
