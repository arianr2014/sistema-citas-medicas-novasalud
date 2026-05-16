"""
Estrategias concretas de filtro para Citas (patrón Strategy).

Cada clase encapsula una regla de filtrado. El FiltroCompuesto
las combina dinámicamente según los parámetros del request.
"""
from django.db.models import Q

from .base import FiltroEstrategia


class FiltroPorFechaDesde(FiltroEstrategia):
    """Filtra citas cuya fecha sea >= fecha_desde."""

    def __init__(self, fecha):
        self.fecha = fecha

    def aplicar(self, queryset):
        if self.fecha:
            return queryset.filter(fecha_hora__date__gte=self.fecha)
        return queryset


class FiltroPorFechaHasta(FiltroEstrategia):
    """Filtra citas cuya fecha sea <= fecha_hasta."""

    def __init__(self, fecha):
        self.fecha = fecha

    def aplicar(self, queryset):
        if self.fecha:
            return queryset.filter(fecha_hora__date__lte=self.fecha)
        return queryset


class FiltroPorEstado(FiltroEstrategia):
    """Filtra citas por estado (programada, confirmada, etc.)."""

    def __init__(self, estado):
        self.estado = estado

    def aplicar(self, queryset):
        if self.estado:
            return queryset.filter(estado=self.estado)
        return queryset


class FiltroPorMedico(FiltroEstrategia):
    """Filtra citas asignadas a un médico específico."""

    def __init__(self, medico):
        self.medico = medico

    def aplicar(self, queryset):
        if self.medico:
            return queryset.filter(medico=self.medico)
        return queryset


class FiltroPorPaciente(FiltroEstrategia):
    """Busca citas cuyo paciente coincida con el término (nombre o documento)."""

    def __init__(self, termino):
        self.termino = termino

    def aplicar(self, queryset):
        if self.termino:
            return queryset.filter(
                Q(paciente__nombres__icontains=self.termino)
                | Q(paciente__apellidos__icontains=self.termino)
                | Q(paciente__numero_documento__icontains=self.termino)
            )
        return queryset
