from django.db import models
from django.utils import timezone


class Especialidad(models.Model):
    nombre = models.CharField(max_length=100, unique=True, verbose_name='Nombre')
    descripcion = models.TextField(blank=True, verbose_name='Descripción')

    class Meta:
        verbose_name = 'Especialidad'
        verbose_name_plural = 'Especialidades'
        ordering = ['nombre']

    def __str__(self):
        return self.nombre


class Medico(models.Model):
    nombres = models.CharField(max_length=100, verbose_name='Nombres')
    apellidos = models.CharField(max_length=100, verbose_name='Apellidos')
    especialidad = models.ForeignKey(
        Especialidad,
        on_delete=models.PROTECT,
        related_name='medicos',
        verbose_name='Especialidad',
    )
    numero_registro = models.CharField(
        max_length=30, unique=True, verbose_name='Número de registro médico'
    )
    telefono = models.CharField(max_length=20, blank=True, verbose_name='Teléfono')
    email = models.EmailField(blank=True, verbose_name='Correo electrónico')
    activo = models.BooleanField(default=True, verbose_name='Activo')

    class Meta:
        verbose_name = 'Médico'
        verbose_name_plural = 'Médicos'
        ordering = ['apellidos', 'nombres']

    def __str__(self):
        return f'Dr(a). {self.apellidos}, {self.nombres}'

    @property
    def nombre_completo(self):
        return f'{self.nombres} {self.apellidos}'


class Paciente(models.Model):
    TIPO_DOC_CHOICES = [
        ('CC', 'Cédula de Ciudadanía'),
        ('TI', 'Tarjeta de Identidad'),
        ('CE', 'Cédula de Extranjería'),
        ('PA', 'Pasaporte'),
        ('RC', 'Registro Civil'),
    ]

    tipo_documento = models.CharField(
        max_length=2,
        choices=TIPO_DOC_CHOICES,
        default='CC',
        verbose_name='Tipo de documento',
    )
    numero_documento = models.CharField(
        max_length=20, unique=True, verbose_name='Número de documento'
    )
    nombres = models.CharField(max_length=100, verbose_name='Nombres')
    apellidos = models.CharField(max_length=100, verbose_name='Apellidos')
    fecha_nacimiento = models.DateField(verbose_name='Fecha de nacimiento')
    telefono = models.CharField(max_length=20, verbose_name='Teléfono')
    email = models.EmailField(blank=True, verbose_name='Correo electrónico')
    direccion = models.CharField(max_length=200, blank=True, verbose_name='Dirección')
    eps = models.CharField(max_length=100, blank=True, verbose_name='EPS / Aseguradora')
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = 'Paciente'
        verbose_name_plural = 'Pacientes'
        ordering = ['apellidos', 'nombres']

    def __str__(self):
        return f'{self.apellidos}, {self.nombres} ({self.numero_documento})'

    @property
    def nombre_completo(self):
        return f'{self.nombres} {self.apellidos}'


class Cita(models.Model):
    ESTADO_CHOICES = [
        ('programada', 'Programada'),
        ('confirmada', 'Confirmada'),
        ('atendida', 'Atendida'),
        ('cancelada', 'Cancelada'),
        ('no_asistio', 'No asistió'),
    ]

    paciente = models.ForeignKey(
        Paciente,
        on_delete=models.PROTECT,
        related_name='citas',
        verbose_name='Paciente',
    )
    medico = models.ForeignKey(
        Medico,
        on_delete=models.PROTECT,
        related_name='citas',
        verbose_name='Médico',
    )
    fecha_hora = models.DateTimeField(verbose_name='Fecha y hora')
    estado = models.CharField(
        max_length=20,
        choices=ESTADO_CHOICES,
        default='programada',
        verbose_name='Estado',
    )
    motivo = models.TextField(verbose_name='Motivo de consulta')
    notas = models.TextField(blank=True, verbose_name='Notas / Diagnóstico')
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        verbose_name = 'Cita'
        verbose_name_plural = 'Citas'
        ordering = ['-fecha_hora']

    def __str__(self):
        return (
            f'Cita {self.pk} – {self.paciente.nombre_completo} '
            f'con {self.medico} el {self.fecha_hora.strftime("%d/%m/%Y %H:%M")}'
        )

    @property
    def es_futura(self):
        return self.fecha_hora > timezone.now()
