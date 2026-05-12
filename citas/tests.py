from datetime import date

from django.test import TestCase
from django.urls import reverse
from django.utils import timezone

from .models import Cita, Especialidad, Medico, Paciente


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
