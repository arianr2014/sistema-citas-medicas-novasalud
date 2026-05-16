"""
Estrategia base y compositor para filtros (patrón Strategy).

Cada filtro de búsqueda es una estrategia independiente que sabe cómo
aplicarse sobre un queryset. El FiltroCompuesto encadena N estrategias.
"""
from abc import ABC, abstractmethod


class FiltroEstrategia(ABC):
    """Interfaz de estrategia de filtro."""

    @abstractmethod
    def aplicar(self, queryset):
        """Recibe y devuelve un queryset con el filtro aplicado."""
        ...


class FiltroCompuesto:
    """
    Aplica una cadena de estrategias de filtro en secuencia.
    Usa una API fluida para agregar estrategias.
    """

    def __init__(self):
        self._estrategias: list[FiltroEstrategia] = []

    def agregar(self, estrategia: FiltroEstrategia):
        """Agrega una estrategia; ignora las que no tienen valor."""
        self._estrategias.append(estrategia)
        return self  # API fluida

    def aplicar(self, queryset):
        for estrategia in self._estrategias:
            queryset = estrategia.aplicar(queryset)
        return queryset
