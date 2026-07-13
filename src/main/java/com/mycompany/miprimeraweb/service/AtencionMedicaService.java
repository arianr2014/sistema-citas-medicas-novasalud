package com.mycompany.miprimeraweb.service;

import com.mycompany.miprimeraweb.dao.AtencionMedicaDAO;
import com.mycompany.miprimeraweb.model.AtencionMedica;
import com.mycompany.miprimeraweb.model.Cita;
import java.sql.SQLException;

/** Reglas de negocio para la atención médica. */
public class AtencionMedicaService {

    private final AtencionMedicaDAO atencionDAO = new AtencionMedicaDAO();
    private final CitaService citaService = new CitaService();

    public AtencionMedica obtenerPorCita(int idCita) throws SQLException {
        if (idCita <= 0) return null;
        return atencionDAO.obtenerPorCita(idCita);
    }

    public void guardar(AtencionMedica atencion, int idMedicoSesion) throws SQLException {
        validar(atencion, idMedicoSesion);
        atencionDAO.guardarOActualizar(atencion);
    }

    public void guardarYMarcarAtendida(AtencionMedica atencion, int idMedicoSesion) throws SQLException {
        guardar(atencion, idMedicoSesion);
        citaService.marcarComoAtendidaPorDoctor(atencion.getIdCita(), idMedicoSesion);
    }

    private void validar(AtencionMedica atencion, int idMedicoSesion) throws SQLException {
        if (atencion == null) throw new IllegalArgumentException("No se recibió información de la atención médica.");
        if (idMedicoSesion <= 0) throw new IllegalArgumentException("Su usuario no está vinculado a un médico real.");
        if (atencion.getIdCita() <= 0) throw new IllegalArgumentException("La cita es obligatoria.");

        Cita cita = citaService.obtenerPorId(atencion.getIdCita());
        if (cita == null || cita.getIdMedico() != idMedicoSesion) {
            throw new IllegalArgumentException("No tiene permiso para registrar la atención de esta cita.");
        }
        if (!"PAGADO".equalsIgnoreCase(cita.getEstadoPago())) {
            throw new IllegalStateException("La atención clínica solo puede registrarse cuando la cita está pagada.");
        }
        if (isBlank(atencion.getMotivoConsulta()) || isBlank(atencion.getSintomas()) || isBlank(atencion.getDiagnostico()) || isBlank(atencion.getTratamiento())) {
            throw new IllegalArgumentException("Motivo, síntomas, diagnóstico y tratamiento son obligatorios antes de cerrar la atención.");
        }
        atencion.setIdMedico(cita.getIdMedico());
        atencion.setIdPaciente(cita.getIdPaciente());
        atencion.setCodigoHistoria(cita.getHistoriaClinicaCodigo());
    }

    private boolean isBlank(String value) { return value == null || value.isBlank(); }
}
