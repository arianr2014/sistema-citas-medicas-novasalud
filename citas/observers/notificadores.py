"""
Observadores concretos de eventos de Cita (patrón Observer).

NotificadorMensajesDjango  – muestra mensajes en la UI usando el
                             framework de mensajes de Django.
RegistradorLogCita         – escribe en el log de la aplicación.
"""
import logging

from django.contrib import messages as django_messages

from .base import EventoCita, ObservadorCita

logger = logging.getLogger(__name__)


class NotificadorMensajesDjango(ObservadorCita):
    """
    Muestra un mensaje en la interfaz de usuario cada vez que
    ocurre un evento sobre una Cita.

    Requiere que el EventoCita incluya el `request` HTTP activo.
    """

    def actualizar(self, evento: EventoCita):
        request = evento.request
        if request is None:
            return

        cita = evento.cita
        if evento.tipo == EventoCita.CREADA:
            django_messages.success(request, f'Cita #{cita.pk} registrada exitosamente.')
        elif evento.tipo == EventoCita.ACTUALIZADA:
            django_messages.success(request, f'Cita #{cita.pk} actualizada.')
        elif evento.tipo == EventoCita.CANCELADA:
            django_messages.warning(request, f'Cita #{cita.pk} cancelada.')


class RegistradorLogCita(ObservadorCita):
    """
    Registra cada evento de Cita en el log de la aplicación.
    Útil para auditoría y depuración.
    """

    def actualizar(self, evento: EventoCita):
        cita = evento.cita
        logger.info(
            'Evento de cita | tipo=%s | pk=%s | paciente=%s | medico=%s',
            evento.tipo,
            cita.pk,
            cita.paciente.nombre_completo,
            str(cita.medico),
        )
