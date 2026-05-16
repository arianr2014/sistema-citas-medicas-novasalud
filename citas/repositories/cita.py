"""
Repositorio para el modelo Cita.

Centraliza todo acceso a datos de Cita, aislando las vistas y el facade
de los detalles del ORM de Django (patrón Repositorio).
"""
from django.utils import timezone

from ..models import Cita


class CitaRepository:
    """Store / repositorio para Cita."""

    # ── Consultas ────────────────────────────────────────────

    def get_by_pk(self, pk):
        return Cita.objects.select_related(
            'paciente', 'medico', 'medico__especialidad'
        ).get(pk=pk)

    def list_all(self):
        return Cita.objects.select_related(
            'paciente', 'medico', 'medico__especialidad'
        ).all()

    def get_proximas(self, limit=10):
        return (
            Cita.objects.filter(
                fecha_hora__gte=timezone.now(),
                estado__in=['programada', 'confirmada'],
            )
            .select_related('paciente', 'medico', 'medico__especialidad')
            .order_by('fecha_hora')[:limit]
        )

    def contar_hoy(self):
        hoy = timezone.localdate()  # fecha en TIME_ZONE configurado (America/Bogota)
        return Cita.objects.filter(fecha_hora__date=hoy).count()

    def contar_programadas(self):
        return Cita.objects.filter(estado='programada').count()

    # ── Comandos ─────────────────────────────────────────────

    def guardar(self, cita):
        cita.save()
        return cita

    def cancelar(self, cita):
        cita.estado = 'cancelada'
        cita.save()
        return cita
