from django.urls import path

from . import views

urlpatterns = [
    path('', views.index, name='index'),
    # Citas
    path('citas/', views.cita_list, name='cita_list'),
    path('citas/nueva/', views.cita_nueva, name='cita_nueva'),
    path('citas/<int:pk>/', views.cita_detalle, name='cita_detalle'),
    path('citas/<int:pk>/editar/', views.cita_editar, name='cita_editar'),
    path('citas/<int:pk>/cancelar/', views.cita_cancelar, name='cita_cancelar'),
    # Pacientes
    path('pacientes/', views.paciente_list, name='paciente_list'),
    path('pacientes/nuevo/', views.paciente_nuevo, name='paciente_nuevo'),
    path('pacientes/<int:pk>/', views.paciente_detalle, name='paciente_detalle'),
    path('pacientes/<int:pk>/editar/', views.paciente_editar, name='paciente_editar'),
    # Médicos
    path('medicos/', views.medico_list, name='medico_list'),
    path('medicos/<int:pk>/', views.medico_detalle, name='medico_detalle'),
]
