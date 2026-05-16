"""
Infraestructura del patrón Observer para eventos de Cita.

EventoCita        – datos del evento (qué pasó, con qué cita, en qué request).
ObservadorCita    – interfaz abstracta que deben implementar los observadores.
BusEventosCita    – sujeto/publisher; gestiona la lista de suscriptores y
                    los notifica cuando ocurre un evento.
"""
from abc import ABC, abstractmethod


class EventoCita:
    """Representa un evento ocurrido sobre una Cita."""

    CREADA = 'creada'
    ACTUALIZADA = 'actualizada'
    CANCELADA = 'cancelada'

    def __init__(self, tipo, cita, request=None, **extra):
        self.tipo = tipo
        self.cita = cita
        self.request = request  # puede ser None si el evento no viene de una vista
        self.extra = extra


class ObservadorCita(ABC):
    """Interfaz abstracta que deben implementar todos los observadores."""

    @abstractmethod
    def actualizar(self, evento: EventoCita):
        """Reacciona ante un evento de Cita."""
        ...


class BusEventosCita:
    """
    Bus de eventos / sujeto para el patrón Observer.

    Las vistas (o el Facade) llaman a `notificar()` y el bus
    despacha el evento a todos los observadores suscritos.
    """

    def __init__(self):
        self._observadores: list[ObservadorCita] = []

    def suscribir(self, observador: ObservadorCita):
        """Registra un nuevo observador."""
        self._observadores.append(observador)

    def desuscribir(self, observador: ObservadorCita):
        """Elimina un observador previamente registrado."""
        self._observadores.remove(observador)

    def notificar(self, evento: EventoCita):
        """Envía el evento a todos los observadores registrados."""
        for obs in self._observadores:
            obs.actualizar(evento)
