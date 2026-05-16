"""
Repositorio para el modelo Paciente.
"""
from django.db.models import Q

from ..models import Paciente


class PacienteRepository:
    """Store / repositorio para Paciente."""

    # ── Consultas ────────────────────────────────────────────

    def get_by_pk(self, pk):
        return Paciente.objects.get(pk=pk)

    def list_all(self):
        return Paciente.objects.all()

    def search(self, termino):
        return Paciente.objects.filter(
            Q(nombres__icontains=termino)
            | Q(apellidos__icontains=termino)
            | Q(numero_documento__icontains=termino)
        )

    def contar(self):
        return Paciente.objects.count()

    # ── Comandos ─────────────────────────────────────────────

    def guardar(self, paciente):
        paciente.save()
        return paciente
