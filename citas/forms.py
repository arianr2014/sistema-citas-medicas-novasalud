from django import forms
from django.utils import timezone

from .models import Cita, Medico, Paciente


class PacienteForm(forms.ModelForm):
    class Meta:
        model = Paciente
        fields = [
            'tipo_documento',
            'numero_documento',
            'nombres',
            'apellidos',
            'fecha_nacimiento',
            'telefono',
            'email',
            'direccion',
            'eps',
        ]
        widgets = {
            'fecha_nacimiento': forms.DateInput(attrs={'type': 'date'}),
        }


class CitaForm(forms.ModelForm):
    class Meta:
        model = Cita
        fields = ['paciente', 'medico', 'fecha_hora', 'motivo', 'notas', 'estado']
        widgets = {
            'fecha_hora': forms.DateTimeInput(
                attrs={'type': 'datetime-local'}, format='%Y-%m-%dT%H:%M'
            ),
            'motivo': forms.Textarea(attrs={'rows': 3}),
            'notas': forms.Textarea(attrs={'rows': 3}),
        }

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.fields['medico'].queryset = Medico.objects.filter(activo=True).select_related(
            'especialidad'
        )
        # Format the initial datetime value for the widget
        if self.instance.pk and self.instance.fecha_hora:
            self.initial['fecha_hora'] = self.instance.fecha_hora.strftime('%Y-%m-%dT%H:%M')

    def clean_fecha_hora(self):
        fecha_hora = self.cleaned_data.get('fecha_hora')
        if fecha_hora and not self.instance.pk:
            if fecha_hora < timezone.now():
                raise forms.ValidationError(
                    'La fecha y hora de la cita debe ser en el futuro.'
                )
        return fecha_hora


class CitaFiltroForm(forms.Form):
    fecha_desde = forms.DateField(
        required=False,
        widget=forms.DateInput(attrs={'type': 'date'}),
        label='Desde',
    )
    fecha_hasta = forms.DateField(
        required=False,
        widget=forms.DateInput(attrs={'type': 'date'}),
        label='Hasta',
    )
    estado = forms.ChoiceField(
        required=False,
        choices=[('', 'Todos')] + Cita.ESTADO_CHOICES,
        label='Estado',
    )
    medico = forms.ModelChoiceField(
        required=False,
        queryset=Medico.objects.filter(activo=True),
        empty_label='Todos',
        label='Médico',
    )
