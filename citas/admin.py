from django.contrib import admin

from .models import Cita, Especialidad, Medico, Paciente


@admin.register(Especialidad)
class EspecialidadAdmin(admin.ModelAdmin):
    list_display = ['nombre', 'descripcion']
    search_fields = ['nombre']


@admin.register(Medico)
class MedicoAdmin(admin.ModelAdmin):
    list_display = ['apellidos', 'nombres', 'especialidad', 'numero_registro', 'activo']
    list_filter = ['especialidad', 'activo']
    search_fields = ['apellidos', 'nombres', 'numero_registro']


@admin.register(Paciente)
class PacienteAdmin(admin.ModelAdmin):
    list_display = [
        'apellidos', 'nombres', 'tipo_documento', 'numero_documento', 'telefono', 'eps'
    ]
    list_filter = ['tipo_documento', 'eps']
    search_fields = ['apellidos', 'nombres', 'numero_documento']


@admin.register(Cita)
class CitaAdmin(admin.ModelAdmin):
    list_display = ['pk', 'paciente', 'medico', 'fecha_hora', 'estado']
    list_filter = ['estado', 'medico__especialidad', 'fecha_hora']
    search_fields = [
        'paciente__nombres', 'paciente__apellidos', 'paciente__numero_documento',
        'medico__nombres', 'medico__apellidos',
    ]
    date_hierarchy = 'fecha_hora'
