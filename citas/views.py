from django.shortcuts import redirect, render

from .facade import CitasFacade
from .forms import CitaFiltroForm, CitaForm, PacienteForm

# Una única instancia de la fachada para toda la aplicación
_facade = CitasFacade()


# ──────────────────────────────────────────────────────────────
# Dashboard
# ──────────────────────────────────────────────────────────────

def index(request):
    context = _facade.get_estadisticas_dashboard()
    return render(request, 'citas/index.html', context)


# ──────────────────────────────────────────────────────────────
# Citas
# ──────────────────────────────────────────────────────────────

def cita_list(request):
    form = CitaFiltroForm(request.GET or None)
    busqueda = request.GET.get('q', '').strip()
    citas = _facade.listar_citas(filtro_form=form, busqueda=busqueda)
    return render(request, 'citas/cita_list.html', {'citas': citas, 'form': form, 'q': busqueda})


def cita_nueva(request):
    form = CitaForm(request.POST or None)
    if request.method == 'POST' and form.is_valid():
        _facade.crear_cita(form, request=request)
        return redirect('cita_list')
    return render(request, 'citas/cita_form.html', {'form': form, 'titulo': 'Nueva Cita'})


def cita_editar(request, pk):
    cita = _facade.get_cita(pk)
    form = CitaForm(request.POST or None, instance=cita)
    if request.method == 'POST' and form.is_valid():
        _facade.actualizar_cita(form, request=request)
        return redirect('cita_list')
    return render(request, 'citas/cita_form.html', {'form': form, 'titulo': 'Editar Cita', 'cita': cita})


def cita_detalle(request, pk):
    cita = _facade.get_cita(pk)
    return render(request, 'citas/cita_detalle.html', {'cita': cita})


def cita_cancelar(request, pk):
    cita = _facade.get_cita(pk)
    if request.method == 'POST':
        _facade.cancelar_cita(pk, request=request)
        return redirect('cita_list')
    return render(request, 'citas/cita_cancelar.html', {'cita': cita})


# ──────────────────────────────────────────────────────────────
# Pacientes
# ──────────────────────────────────────────────────────────────

def paciente_list(request):
    q = request.GET.get('q', '').strip()
    pacientes = _facade.listar_pacientes(busqueda=q)
    return render(request, 'citas/paciente_list.html', {'pacientes': pacientes, 'q': q})


def paciente_nuevo(request):
    form = PacienteForm(request.POST or None)
    if request.method == 'POST' and form.is_valid():
        _facade.crear_paciente(form, request=request)
        return redirect('paciente_list')
    return render(request, 'citas/paciente_form.html', {'form': form, 'titulo': 'Nuevo Paciente'})


def paciente_editar(request, pk):
    paciente = _facade.get_paciente(pk)
    form = PacienteForm(request.POST or None, instance=paciente)
    if request.method == 'POST' and form.is_valid():
        _facade.actualizar_paciente(form, request=request)
        return redirect('paciente_list')
    return render(
        request,
        'citas/paciente_form.html',
        {'form': form, 'titulo': 'Editar Paciente', 'paciente': paciente},
    )


def paciente_detalle(request, pk):
    paciente = _facade.get_paciente(pk)
    citas = _facade.get_citas_de_paciente(paciente)
    return render(request, 'citas/paciente_detalle.html', {'paciente': paciente, 'citas': citas})


# ──────────────────────────────────────────────────────────────
# Médicos
# ──────────────────────────────────────────────────────────────

def medico_list(request):
    q = request.GET.get('q', '').strip()
    medicos = _facade.listar_medicos(busqueda=q)
    return render(request, 'citas/medico_list.html', {'medicos': medicos, 'q': q})


def medico_detalle(request, pk):
    medico = _facade.get_medico(pk)
    citas = _facade.get_citas_de_medico(medico)
    return render(request, 'citas/medico_detalle.html', {'medico': medico, 'citas': citas})
