from datetime import date

from django.test import TestCase, RequestFactory
from django.urls import reverse
from django.utils import timezone

from .models import Cita, Especialidad, Medico, Paciente
from .repositories import CitaRepository, PacienteRepository, MedicoRepository
from .strategies import (
    FiltroCompuesto,
    FiltroPorEstado,
    FiltroPorFechaDesde,
    FiltroPorFechaHasta,
    FiltroPorMedico,
    FiltroPorPaciente,
)
from .observers.base import BusEventosCita, EventoCita
from .observers.notificadores import NotificadorMensajesDjango, RegistradorLogCita
from .facade import CitasFacade


# ──────────────────────────────────────────────────────────────
# Model tests
# ──────────────────────────────────────────────────────────────

class EspecialidadModelTest(TestCase):
    def test_str(self):
        esp = Especialidad.objects.create(nombre='Cardiología')
        self.assertEqual(str(esp), 'Cardiología')


class MedicoModelTest(TestCase):
    def setUp(self):
        self.esp = Especialidad.objects.create(nombre='Dermatología')
        self.medico = Medico.objects.create(
            nombres='Luis',
            apellidos='Torres',
            especialidad=self.esp,
            numero_registro='RM-999',
        )

    def test_str(self):
        self.assertEqual(str(self.medico), 'Dr(a). Torres, Luis')

    def test_nombre_completo(self):
        self.assertEqual(self.medico.nombre_completo, 'Luis Torres')


class PacienteModelTest(TestCase):
    def setUp(self):
        self.paciente = Paciente.objects.create(
            tipo_documento='CC',
            numero_documento='123456789',
            nombres='Ana',
            apellidos='García',
            fecha_nacimiento=date(1990, 5, 20),
            telefono='3001234567',
        )

    def test_str(self):
        self.assertIn('123456789', str(self.paciente))

    def test_nombre_completo(self):
        self.assertEqual(self.paciente.nombre_completo, 'Ana García')


class CitaModelTest(TestCase):
    def setUp(self):
        esp = Especialidad.objects.create(nombre='Pediatría')
        medico = Medico.objects.create(
            nombres='María',
            apellidos='López',
            especialidad=esp,
            numero_registro='RM-100',
        )
        paciente = Paciente.objects.create(
            tipo_documento='CC',
            numero_documento='987654321',
            nombres='Carlos',
            apellidos='Suárez',
            fecha_nacimiento=date(1985, 1, 1),
            telefono='3009999999',
        )
        self.cita = Cita.objects.create(
            paciente=paciente,
            medico=medico,
            fecha_hora=timezone.now() + timezone.timedelta(days=1),
            motivo='Control rutinario',
        )

    def test_str_contains_pk(self):
        self.assertIn(str(self.cita.pk), str(self.cita))

    def test_es_futura(self):
        self.assertTrue(self.cita.es_futura)

    def test_default_estado(self):
        self.assertEqual(self.cita.estado, 'programada')


# ──────────────────────────────────────────────────────────────
# View tests
# ──────────────────────────────────────────────────────────────

class ViewsTest(TestCase):
    def setUp(self):
        esp = Especialidad.objects.create(nombre='Medicina General')
        self.medico = Medico.objects.create(
            nombres='Roberto',
            apellidos='Díaz',
            especialidad=esp,
            numero_registro='RM-200',
        )
        self.paciente = Paciente.objects.create(
            tipo_documento='CC',
            numero_documento='111222333',
            nombres='Sofía',
            apellidos='Reyes',
            fecha_nacimiento=date(2000, 6, 15),
            telefono='3100001111',
        )
        self.cita = Cita.objects.create(
            paciente=self.paciente,
            medico=self.medico,
            fecha_hora=timezone.now() + timezone.timedelta(days=2),
            motivo='Revisión general',
        )

    # Dashboard
    def test_index_ok(self):
        response = self.client.get(reverse('index'))
        self.assertEqual(response.status_code, 200)

    # Citas list
    def test_cita_list_ok(self):
        response = self.client.get(reverse('cita_list'))
        self.assertEqual(response.status_code, 200)
        self.assertContains(response, 'Sofía')  # patient name appears in list

    # Cita detail
    def test_cita_detalle_ok(self):
        response = self.client.get(reverse('cita_detalle', args=[self.cita.pk]))
        self.assertEqual(response.status_code, 200)

    # Cita create
    def test_cita_nueva_get(self):
        response = self.client.get(reverse('cita_nueva'))
        self.assertEqual(response.status_code, 200)

    def test_cita_nueva_post_valid(self):
        fecha = (timezone.now() + timezone.timedelta(days=3)).strftime('%Y-%m-%dT%H:%M')
        response = self.client.post(reverse('cita_nueva'), {
            'paciente': self.paciente.pk,
            'medico': self.medico.pk,
            'fecha_hora': fecha,
            'motivo': 'Chequeo general',
            'estado': 'programada',
            'notas': '',
        })
        self.assertRedirects(response, reverse('cita_list'))
        self.assertEqual(Cita.objects.filter(motivo='Chequeo general').count(), 1)

    def test_cita_nueva_post_past_date(self):
        fecha = (timezone.now() - timezone.timedelta(days=1)).strftime('%Y-%m-%dT%H:%M')
        response = self.client.post(reverse('cita_nueva'), {
            'paciente': self.paciente.pk,
            'medico': self.medico.pk,
            'fecha_hora': fecha,
            'motivo': 'Pasada',
            'estado': 'programada',
            'notas': '',
        })
        # Should stay on same page (form invalid)
        self.assertEqual(response.status_code, 200)

    # Cita cancel
    def test_cita_cancelar_post(self):
        response = self.client.post(reverse('cita_cancelar', args=[self.cita.pk]))
        self.assertRedirects(response, reverse('cita_list'))
        self.cita.refresh_from_db()
        self.assertEqual(self.cita.estado, 'cancelada')

    # Pacientes
    def test_paciente_list_ok(self):
        response = self.client.get(reverse('paciente_list'))
        self.assertEqual(response.status_code, 200)
        self.assertContains(response, 'Sofía')

    def test_paciente_detalle_ok(self):
        response = self.client.get(reverse('paciente_detalle', args=[self.paciente.pk]))
        self.assertEqual(response.status_code, 200)

    def test_paciente_nuevo_post_valid(self):
        response = self.client.post(reverse('paciente_nuevo'), {
            'tipo_documento': 'CC',
            'numero_documento': '444555666',
            'nombres': 'Mario',
            'apellidos': 'Vargas',
            'fecha_nacimiento': '1995-08-10',
            'telefono': '3009998877',
            'email': '',
            'direccion': '',
            'eps': '',
        })
        self.assertRedirects(response, reverse('paciente_list'))
        self.assertTrue(Paciente.objects.filter(numero_documento='444555666').exists())

    # Médicos
    def test_medico_list_ok(self):
        response = self.client.get(reverse('medico_list'))
        self.assertEqual(response.status_code, 200)

    def test_medico_detalle_ok(self):
        response = self.client.get(reverse('medico_detalle', args=[self.medico.pk]))
        self.assertEqual(response.status_code, 200)

    # Search / filter
    def test_cita_list_search(self):
        response = self.client.get(reverse('cita_list') + '?q=Sofía')
        self.assertEqual(response.status_code, 200)
        self.assertContains(response, 'Sofía')  # patient name in search results

    def test_paciente_list_search(self):
        response = self.client.get(reverse('paciente_list') + '?q=111222333')
        self.assertEqual(response.status_code, 200)
        self.assertContains(response, 'Sofía')


# ──────────────────────────────────────────────────────────────
# Repository tests
# ──────────────────────────────────────────────────────────────

class CitaRepositoryTest(TestCase):
    def setUp(self):
        esp = Especialidad.objects.create(nombre='Medicina General')
        self.medico = Medico.objects.create(
            nombres='Pedro', apellidos='Soto', especialidad=esp, numero_registro='R-10'
        )
        self.paciente = Paciente.objects.create(
            tipo_documento='CC', numero_documento='555666777',
            nombres='Laura', apellidos='Gómez',
            fecha_nacimiento=date(1990, 1, 1), telefono='3000000001',
        )
        self.repo = CitaRepository()

    def test_list_all_returns_queryset(self):
        Cita.objects.create(
            paciente=self.paciente, medico=self.medico,
            fecha_hora=timezone.now() + timezone.timedelta(days=1),
            motivo='Test',
        )
        self.assertEqual(self.repo.list_all().count(), 1)

    def test_contar_hoy(self):
        # Usar localtime para que el datetime caiga en el día de hoy según TIME_ZONE
        Cita.objects.create(
            paciente=self.paciente, medico=self.medico,
            fecha_hora=timezone.localtime(timezone.now()),
            motivo='Hoy',
        )
        self.assertEqual(self.repo.contar_hoy(), 1)

    def test_contar_programadas(self):
        Cita.objects.create(
            paciente=self.paciente, medico=self.medico,
            fecha_hora=timezone.now() + timezone.timedelta(days=1),
            motivo='Prog', estado='programada',
        )
        self.assertGreaterEqual(self.repo.contar_programadas(), 1)

    def test_cancelar_cambia_estado(self):
        cita = Cita.objects.create(
            paciente=self.paciente, medico=self.medico,
            fecha_hora=timezone.now() + timezone.timedelta(days=1),
            motivo='Test',
        )
        self.repo.cancelar(cita)
        cita.refresh_from_db()
        self.assertEqual(cita.estado, 'cancelada')


class PacienteRepositoryTest(TestCase):
    def setUp(self):
        self.repo = PacienteRepository()
        self.paciente = Paciente.objects.create(
            tipo_documento='CC', numero_documento='888999000',
            nombres='Juan', apellidos='Pérez',
            fecha_nacimiento=date(1985, 5, 10), telefono='3111111111',
        )

    def test_search_por_nombre(self):
        qs = self.repo.search('Juan')
        self.assertIn(self.paciente, qs)

    def test_search_por_documento(self):
        qs = self.repo.search('888999000')
        self.assertIn(self.paciente, qs)

    def test_contar(self):
        self.assertEqual(self.repo.contar(), 1)


class MedicoRepositoryTest(TestCase):
    def setUp(self):
        self.esp = Especialidad.objects.create(nombre='Ortopedia')
        self.repo = MedicoRepository()
        self.medico = Medico.objects.create(
            nombres='Rosa', apellidos='Ríos', especialidad=self.esp,
            numero_registro='R-20', activo=True,
        )

    def test_get_activos(self):
        self.assertIn(self.medico, self.repo.get_activos())

    def test_contar_activos(self):
        self.assertEqual(self.repo.contar_activos(), 1)

    def test_search_por_especialidad(self):
        qs = self.repo.search('Ortopedia')
        self.assertIn(self.medico, qs)


# ──────────────────────────────────────────────────────────────
# Strategy tests
# ──────────────────────────────────────────────────────────────

class EstrategiaFiltroTest(TestCase):
    def setUp(self):
        esp = Especialidad.objects.create(nombre='Cardiología')
        self.medico1 = Medico.objects.create(
            nombres='A', apellidos='B', especialidad=esp, numero_registro='R-30'
        )
        self.medico2 = Medico.objects.create(
            nombres='C', apellidos='D', especialidad=esp, numero_registro='R-31'
        )
        self.paciente = Paciente.objects.create(
            tipo_documento='CC', numero_documento='100200300',
            nombres='Elena', apellidos='Mora',
            fecha_nacimiento=date(1995, 3, 20), telefono='3122222222',
        )
        self.cita_programada = Cita.objects.create(
            paciente=self.paciente, medico=self.medico1,
            fecha_hora=timezone.now() + timezone.timedelta(days=1),
            motivo='Programada', estado='programada',
        )
        self.cita_confirmada = Cita.objects.create(
            paciente=self.paciente, medico=self.medico2,
            fecha_hora=timezone.now() + timezone.timedelta(days=2),
            motivo='Confirmada', estado='confirmada',
        )

    def _qs(self):
        return Cita.objects.select_related('paciente', 'medico').all()

    def test_filtro_por_estado(self):
        resultado = FiltroPorEstado('programada').aplicar(self._qs())
        self.assertIn(self.cita_programada, resultado)
        self.assertNotIn(self.cita_confirmada, resultado)

    def test_filtro_por_medico(self):
        resultado = FiltroPorMedico(self.medico1).aplicar(self._qs())
        self.assertIn(self.cita_programada, resultado)
        self.assertNotIn(self.cita_confirmada, resultado)

    def test_filtro_por_paciente_nombre(self):
        resultado = FiltroPorPaciente('Elena').aplicar(self._qs())
        self.assertEqual(resultado.count(), 2)

    def test_filtro_estado_vacio_no_filtra(self):
        resultado = FiltroPorEstado('').aplicar(self._qs())
        self.assertEqual(resultado.count(), 2)

    def test_filtro_compuesto(self):
        resultado = (
            FiltroCompuesto()
            .agregar(FiltroPorEstado('programada'))
            .agregar(FiltroPorMedico(self.medico1))
            .aplicar(self._qs())
        )
        self.assertIn(self.cita_programada, resultado)
        self.assertNotIn(self.cita_confirmada, resultado)


# ──────────────────────────────────────────────────────────────
# Observer tests
# ──────────────────────────────────────────────────────────────

class ObserverTest(TestCase):
    def setUp(self):
        esp = Especialidad.objects.create(nombre='Neurología')
        medico = Medico.objects.create(
            nombres='X', apellidos='Y', especialidad=esp, numero_registro='R-40'
        )
        paciente = Paciente.objects.create(
            tipo_documento='CC', numero_documento='400500600',
            nombres='Zoe', apellidos='Vega',
            fecha_nacimiento=date(2000, 8, 8), telefono='3133333333',
        )
        self.cita = Cita.objects.create(
            paciente=paciente, medico=medico,
            fecha_hora=timezone.now() + timezone.timedelta(days=1),
            motivo='Test observer',
        )
        self.factory = RequestFactory()

    def _make_request(self):
        from django.contrib.messages.storage.fallback import FallbackStorage
        from django.contrib.sessions.backends.db import SessionStore
        request = self.factory.get('/')
        request.session = SessionStore()
        request.session.create()
        messages_storage = FallbackStorage(request)
        request._messages = messages_storage
        return request

    def test_notificador_creada(self):
        request = self._make_request()
        obs = NotificadorMensajesDjango()
        obs.actualizar(EventoCita(EventoCita.CREADA, self.cita, request=request))
        msgs = list(request._messages)
        self.assertEqual(len(msgs), 1)
        self.assertIn('registrada', msgs[0].message)

    def test_notificador_cancelada(self):
        request = self._make_request()
        obs = NotificadorMensajesDjango()
        obs.actualizar(EventoCita(EventoCita.CANCELADA, self.cita, request=request))
        msgs = list(request._messages)
        self.assertEqual(len(msgs), 1)
        self.assertIn('cancelada', msgs[0].message)

    def test_notificador_sin_request_no_falla(self):
        obs = NotificadorMensajesDjango()
        # No debe lanzar excepción cuando request es None
        obs.actualizar(EventoCita(EventoCita.CREADA, self.cita, request=None))

    def test_bus_notifica_todos_los_observadores(self):
        resultados = []

        class ObsTest:
            def actualizar(self, evento):
                resultados.append(evento.tipo)

        bus = BusEventosCita()
        bus.suscribir(ObsTest())
        bus.suscribir(ObsTest())
        bus.notificar(EventoCita(EventoCita.CREADA, self.cita))
        self.assertEqual(resultados, [EventoCita.CREADA, EventoCita.CREADA])

    def test_bus_desuscribir(self):
        resultados = []

        class ObsTest:
            def actualizar(self, evento):
                resultados.append(evento.tipo)

        obs = ObsTest()
        bus = BusEventosCita()
        bus.suscribir(obs)
        bus.desuscribir(obs)
        bus.notificar(EventoCita(EventoCita.CREADA, self.cita))
        self.assertEqual(resultados, [])


# ──────────────────────────────────────────────────────────────
# Facade tests
# ──────────────────────────────────────────────────────────────

class FacadeTest(TestCase):
    def setUp(self):
        esp = Especialidad.objects.create(nombre='Psiquiatría')
        self.medico = Medico.objects.create(
            nombres='Félix', apellidos='Luna', especialidad=esp, numero_registro='R-50'
        )
        self.paciente = Paciente.objects.create(
            tipo_documento='CC', numero_documento='700800900',
            nombres='Irene', apellidos='Cano',
            fecha_nacimiento=date(1988, 12, 1), telefono='3144444444',
        )
        self.cita = Cita.objects.create(
            paciente=self.paciente, medico=self.medico,
            fecha_hora=timezone.now() + timezone.timedelta(days=3),
            motivo='Consulta inicial',
        )
        self.facade = CitasFacade()

    def test_get_estadisticas_dashboard(self):
        stats = self.facade.get_estadisticas_dashboard()
        self.assertIn('citas_hoy', stats)
        self.assertIn('total_pacientes', stats)
        self.assertGreaterEqual(stats['total_pacientes'], 1)

    def test_listar_citas_sin_filtros(self):
        qs = self.facade.listar_citas()
        self.assertIn(self.cita, qs)

    def test_listar_citas_busqueda(self):
        qs = self.facade.listar_citas(busqueda='Irene')
        self.assertIn(self.cita, qs)

    def test_cancelar_cita(self):
        self.facade.cancelar_cita(self.cita.pk)
        self.cita.refresh_from_db()
        self.assertEqual(self.cita.estado, 'cancelada')

    def test_listar_pacientes_busqueda(self):
        qs = self.facade.listar_pacientes(busqueda='Irene')
        self.assertIn(self.paciente, qs)

    def test_listar_medicos_busqueda(self):
        qs = self.facade.listar_medicos(busqueda='Psiquiatría')
        self.assertIn(self.medico, qs)

    def test_get_citas_de_paciente(self):
        citas = self.facade.get_citas_de_paciente(self.paciente)
        self.assertIn(self.cita, citas)

    def test_get_citas_de_medico(self):
        citas = self.facade.get_citas_de_medico(self.medico)
        self.assertIn(self.cita, citas)
