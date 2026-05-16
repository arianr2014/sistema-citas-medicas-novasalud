"""
Repositorio para el modelo Medico.
"""
from django.db.models import Q

from ..models import Medico


class MedicoRepository:
    """Store / repositorio para Medico."""

    # ── Consultas ────────────────────────────────────────────

    def get_by_pk(self, pk):
        return Medico.objects.select_related('especialidad').get(pk=pk)

    def list_all(self):
        return Medico.objects.select_related('especialidad').all()

    def get_activos(self):
        return Medico.objects.filter(activo=True).select_related('especialidad')

    def search(self, termino):
        return Medico.objects.select_related('especialidad').filter(
            Q(nombres__icontains=termino)
            | Q(apellidos__icontains=termino)
            | Q(especialidad__nombre__icontains=termino)
        )

    def contar_activos(self):
        return Medico.objects.filter(activo=True).count()
