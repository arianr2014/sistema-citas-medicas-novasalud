from django.contrib import messages
from django.db.models import Count, Q
from django.shortcuts import get_object_or_404, redirect, render
from django.utils import timezone

from .forms import CitaFiltroForm, CitaForm, PacienteForm
from .models import Cita, Especialidad, Medico, Paciente


# ──────────────────────────────────────────────────────────────
# Dashboard
# ──────────────────────────────────────────────────────────────

def index(request):
    hoy = timezone.now().date()
    citas_hoy = Cita.objects.filter(fecha_hora__date=hoy).count()
    citas_programadas = Cita.objects.filter(estado='programada').count()
    total_pacientes = Paciente.objects.count()
    total_medicos = Medico.objects.filter(activo=True).count()
    proximas_citas = (
        Cita.objects.filter(fecha_hora__gte=timezone.now(), estado__in=['programada', 'confirmada'])
        .select_related('paciente', 'medico', 'medico__especialidad')
        .order_by('fecha_hora')[:10]
    )
    context = {
        'citas_hoy': citas_hoy,
        'citas_programadas': citas_programadas,
        'total_pacientes': total_pacientes,
        'total_medicos': total_medicos,
        'proximas_citas': proximas_citas,
    }
    return render(request, 'citas/index.html', context)


# ──────────────────────────────────────────────────────────────
# Citas
# ──────────────────────────────────────────────────────────────

def cita_list(request):
    form = CitaFiltroForm(request.GET or None)
    qs = Cita.objects.select_related('paciente', 'medico', 'medico__especialidad').all()

    if form.is_valid():
        if form.cleaned_data.get('fecha_desde'):
            qs = qs.filter(fecha_hora__date__gte=form.cleaned_data['fecha_desde'])
        if form.cleaned_data.get('fecha_hasta'):
            qs = qs.filter(fecha_hora__date__lte=form.cleaned_data['fecha_hasta'])
        if form.cleaned_data.get('estado'):
            qs = qs.filter(estado=form.cleaned_data['estado'])
        if form.cleaned_data.get('medico'):
            qs = qs.filter(medico=form.cleaned_data['medico'])

    busqueda = request.GET.get('q', '').strip()
    if busqueda:
        qs = qs.filter(
            Q(paciente__nombres__icontains=busqueda)
            | Q(paciente__apellidos__icontains=busqueda)
            | Q(paciente__numero_documento__icontains=busqueda)
        )

    return render(request, 'citas/cita_list.html', {'citas': qs, 'form': form, 'q': busqueda})


def cita_nueva(request):
    form = CitaForm(request.POST or None)
    if request.method == 'POST' and form.is_valid():
        cita = form.save()
        messages.success(request, f'Cita #{cita.pk} registrada exitosamente.')
        return redirect('cita_list')
    return render(request, 'citas/cita_form.html', {'form': form, 'titulo': 'Nueva Cita'})


def cita_editar(request, pk):
    cita = get_object_or_404(Cita, pk=pk)
    form = CitaForm(request.POST or None, instance=cita)
    if request.method == 'POST' and form.is_valid():
        form.save()
        messages.success(request, f'Cita #{cita.pk} actualizada.')
        return redirect('cita_list')
    return render(request, 'citas/cita_form.html', {'form': form, 'titulo': 'Editar Cita', 'cita': cita})


def cita_detalle(request, pk):
    cita = get_object_or_404(
        Cita.objects.select_related('paciente', 'medico', 'medico__especialidad'), pk=pk
    )
    return render(request, 'citas/cita_detalle.html', {'cita': cita})


def cita_cancelar(request, pk):
    cita = get_object_or_404(Cita, pk=pk)
    if request.method == 'POST':
        cita.estado = 'cancelada'
        cita.save()
        messages.warning(request, f'Cita #{cita.pk} cancelada.')
        return redirect('cita_list')
    return render(request, 'citas/cita_cancelar.html', {'cita': cita})


# ──────────────────────────────────────────────────────────────
# Pacientes
# ──────────────────────────────────────────────────────────────

def paciente_list(request):
    q = request.GET.get('q', '').strip()
    qs = Paciente.objects.all()
    if q:
        qs = qs.filter(
            Q(nombres__icontains=q)
            | Q(apellidos__icontains=q)
            | Q(numero_documento__icontains=q)
        )
    return render(request, 'citas/paciente_list.html', {'pacientes': qs, 'q': q})


def paciente_nuevo(request):
    form = PacienteForm(request.POST or None)
    if request.method == 'POST' and form.is_valid():
        paciente = form.save()
        messages.success(request, f'Paciente {paciente.nombre_completo} registrado.')
        return redirect('paciente_list')
    return render(request, 'citas/paciente_form.html', {'form': form, 'titulo': 'Nuevo Paciente'})


def paciente_editar(request, pk):
    paciente = get_object_or_404(Paciente, pk=pk)
    form = PacienteForm(request.POST or None, instance=paciente)
    if request.method == 'POST' and form.is_valid():
        form.save()
        messages.success(request, 'Datos del paciente actualizados.')
        return redirect('paciente_list')
    return render(
        request,
        'citas/paciente_form.html',
        {'form': form, 'titulo': 'Editar Paciente', 'paciente': paciente},
    )


def paciente_detalle(request, pk):
    paciente = get_object_or_404(Paciente, pk=pk)
    citas = paciente.citas.select_related('medico', 'medico__especialidad').order_by('-fecha_hora')
    return render(request, 'citas/paciente_detalle.html', {'paciente': paciente, 'citas': citas})


# ──────────────────────────────────────────────────────────────
# Médicos
# ──────────────────────────────────────────────────────────────

def medico_list(request):
    q = request.GET.get('q', '').strip()
    qs = Medico.objects.select_related('especialidad').all()
    if q:
        qs = qs.filter(
            Q(nombres__icontains=q)
            | Q(apellidos__icontains=q)
            | Q(especialidad__nombre__icontains=q)
        )
    return render(request, 'citas/medico_list.html', {'medicos': qs, 'q': q})


def medico_detalle(request, pk):
    medico = get_object_or_404(Medico.objects.select_related('especialidad'), pk=pk)
    citas = medico.citas.select_related('paciente').order_by('-fecha_hora')[:20]
    return render(request, 'citas/medico_detalle.html', {'medico': medico, 'citas': citas})
