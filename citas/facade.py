"""
Fachada principal de la aplicación (patrón Facade).

CitasFacade ofrece una interfaz simplificada a las vistas; internamente
coordina los repositorios, las estrategias de filtro y el bus de eventos.

Las vistas nunca acceden directamente a los modelos, repositorios,
estrategias u observadores: todo pasa por esta fachada.
"""
from django.shortcuts import get_object_or_404

from .repositories import CitaRepository, PacienteRepository, MedicoRepository
from .strategies import (
    FiltroCompuesto,
    FiltroPorFechaDesde,
    FiltroPorFechaHasta,
    FiltroPorEstado,
    FiltroPorMedico,
    FiltroPorPaciente,
)
from .observers import (
    BusEventosCita,
    EventoCita,
    NotificadorMensajesDjango,
    RegistradorLogCita,
)
from django.contrib import messages as django_messages


class CitasFacade:
    """
    Punto de entrada único para todas las operaciones del sistema.

    Composición interna:
      - Repositorios  → acceso a datos (patrón Repositorio)
      - FiltroCompuesto + estrategias → filtrado de citas (patrón Strategy)
      - BusEventosCita + observadores  → notificaciones (patrón Observer)
    """

    def __init__(self):
        # Repositorios
        self._cita_repo = CitaRepository()
        self._paciente_repo = PacienteRepository()
        self._medico_repo = MedicoRepository()

        # Bus de eventos con los observadores por defecto
        self._bus = BusEventosCita()
        self._bus.suscribir(NotificadorMensajesDjango())
        self._bus.suscribir(RegistradorLogCita())

    # ── Dashboard ────────────────────────────────────────────────────────

    def get_estadisticas_dashboard(self):
        """Retorna el contexto completo para el panel de control."""
        return {
            'citas_hoy': self._cita_repo.contar_hoy(),
            'citas_programadas': self._cita_repo.contar_programadas(),
            'total_pacientes': self._paciente_repo.contar(),
            'total_medicos': self._medico_repo.contar_activos(),
            'proximas_citas': self._cita_repo.get_proximas(),
        }

    # ── Citas ────────────────────────────────────────────────────────────

    def listar_citas(self, filtro_form=None, busqueda=''):
        """
        Devuelve el queryset de Citas aplicando las estrategias de filtro
        correspondientes a los valores del formulario y la búsqueda libre.
        """
        qs = self._cita_repo.list_all()

        compositor = FiltroCompuesto()

        if filtro_form and filtro_form.is_valid():
            d = filtro_form.cleaned_data
            compositor.agregar(FiltroPorFechaDesde(d.get('fecha_desde')))
            compositor.agregar(FiltroPorFechaHasta(d.get('fecha_hasta')))
            compositor.agregar(FiltroPorEstado(d.get('estado')))
            compositor.agregar(FiltroPorMedico(d.get('medico')))

        compositor.agregar(FiltroPorPaciente(busqueda))

        return compositor.aplicar(qs)

    def get_cita(self, pk):
        """Obtiene una Cita por PK o lanza 404."""
        return get_object_or_404(self._cita_repo.list_all(), pk=pk)

    def crear_cita(self, form, request=None):
        """Guarda una nueva Cita y dispara el evento CREADA."""
        cita = form.save()
        self._bus.notificar(EventoCita(EventoCita.CREADA, cita, request=request))
        return cita

    def actualizar_cita(self, form, request=None):
        """Guarda los cambios en una Cita y dispara el evento ACTUALIZADA."""
        cita = form.save()
        self._bus.notificar(EventoCita(EventoCita.ACTUALIZADA, cita, request=request))
        return cita

    def cancelar_cita(self, pk, request=None):
        """Cambia el estado de una Cita a 'cancelada' y dispara el evento CANCELADA."""
        cita = get_object_or_404(self._cita_repo.list_all(), pk=pk)
        self._cita_repo.cancelar(cita)
        self._bus.notificar(EventoCita(EventoCita.CANCELADA, cita, request=request))
        return cita

    # ── Pacientes ────────────────────────────────────────────────────────

    def listar_pacientes(self, busqueda=''):
        """Devuelve todos los pacientes o filtra por nombre / documento."""
        if busqueda:
            return self._paciente_repo.search(busqueda)
        return self._paciente_repo.list_all()

    def get_paciente(self, pk):
        """Obtiene un Paciente por PK o lanza 404."""
        return get_object_or_404(self._paciente_repo.list_all(), pk=pk)

    def crear_paciente(self, form, request=None):
        """Guarda un nuevo Paciente y muestra un mensaje de éxito."""
        paciente = form.save()
        if request:
            django_messages.success(
                request, f'Paciente {paciente.nombre_completo} registrado.'
            )
        return paciente

    def actualizar_paciente(self, form, request=None):
        """Actualiza los datos de un Paciente y muestra un mensaje de éxito."""
        paciente = form.save()
        if request:
            django_messages.success(request, 'Datos del paciente actualizados.')
        return paciente

    def get_citas_de_paciente(self, paciente):
        """Devuelve el historial de citas de un paciente, ordenado descendente."""
        return (
            paciente.citas
            .select_related('medico', 'medico__especialidad')
            .order_by('-fecha_hora')
        )

    # ── Médicos ──────────────────────────────────────────────────────────

    def listar_medicos(self, busqueda=''):
        """Devuelve todos los médicos o filtra por nombre / especialidad."""
        if busqueda:
            return self._medico_repo.search(busqueda)
        return self._medico_repo.list_all()

    def get_medico(self, pk):
        """Obtiene un Médico por PK o lanza 404."""
        return get_object_or_404(self._medico_repo.list_all(), pk=pk)

    def get_citas_de_medico(self, medico, limit=20):
        """Devuelve las últimas `limit` citas de un médico."""
        return (
            medico.citas
            .select_related('paciente')
            .order_by('-fecha_hora')[:limit]
        )
