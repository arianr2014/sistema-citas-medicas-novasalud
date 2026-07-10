package com.mycompany.miprimeraweb.service;

import com.mycompany.miprimeraweb.dao.CitaDAO;
import com.mycompany.miprimeraweb.dao.HorarioDAO;
import com.mycompany.miprimeraweb.dao.CitaDAOImpl;
import com.mycompany.miprimeraweb.dao.HorarioDAOImpl;
import com.mycompany.miprimeraweb.model.Cita;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CitaService {

    private final CitaDAO citaDAO = new CitaDAOImpl();
    private final HorarioDAO horarioDAO = new HorarioDAOImpl();

    public List<Cita> listarFiltrado(String dniPaciente, int idMedico, int idEspecialidad) throws SQLException {
        return citaDAO.listarFiltrado(dniPaciente, idMedico, idEspecialidad);
    }

    public List<Cita> listarAgendaProgramada(int idMedico, String fecha) throws SQLException {
        if (idMedico <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un medico.");
        }
        if (isBlank(fecha)) {
            throw new IllegalArgumentException("Debe seleccionar una fecha.");
        }

        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("La fecha seleccionada no tiene un formato valido.");
        }

        return citaDAO.listarAgendaProgramada(idMedico, fecha.trim());
    }

    public Cita obtenerPorId(int idCita) throws SQLException {
        if (idCita <= 0) {
            return null;
        }
        return citaDAO.obtenerPorId(idCita);
    }

    public void guardar(Cita cita, String usuarioRegistro) throws SQLException {
        validarCita(cita);
        validarDisponibilidadHoraria(cita);
        cita.setEstado(cita.getEstado().trim().toUpperCase());
        citaDAO.guardar(cita, usuarioRegistro == null || usuarioRegistro.isBlank() ? "sistema" : usuarioRegistro);
    }

    public void eliminar(int idCita) throws SQLException {
        if (idCita <= 0) {
            throw new IllegalArgumentException("El id de la cita no es valido.");
        }
        citaDAO.eliminar(idCita);
    }

    public boolean marcarComoAtendida(int idCita) throws SQLException {
        if (idCita <= 0) {
            throw new IllegalArgumentException("El id de la cita no es valido.");
        }

        Cita cita = citaDAO.obtenerPorId(idCita);
        if (cita == null) {
            throw new IllegalArgumentException("La cita no existe.");
        }

        String estadoActual = cita.getEstado() == null ? "" : cita.getEstado().trim().toUpperCase();
        if ("ATENDIDA".equals(estadoActual) || "CANCELADA".equals(estadoActual)) {
            return false;
        }

        return citaDAO.actualizarEstado(idCita, "ATENDIDA");
    }

    private void validarCita(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("No se recibio informacion de la cita.");
        }

        if (cita.getIdPaciente() <= 0 || cita.getIdMedico() <= 0) {
            throw new IllegalArgumentException("Paciente y medico son obligatorios.");
        }

        if (isBlank(cita.getFecha()) || isBlank(cita.getHora()) || isBlank(cita.getEstado())) {
            throw new IllegalArgumentException("Fecha, hora y estado son obligatorios.");
        }

        validarFecha(cita.getFecha());
    }

    private void validarFecha(String fecha) {
        try {
            LocalDate fechaCita = LocalDate.parse(fecha);
            if (fechaCita.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("No se permite registrar citas en fechas pasadas.");
            }
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("El formato de la fecha es invalido.");
        }
    }

    private void validarDisponibilidadHoraria(Cita cita) throws SQLException {
        List<String> horasDisponibles = horarioDAO.listarHorasDisponibles(cita.getIdMedico(), cita.getFecha(), cita.getIdCita());

        if (horasDisponibles.isEmpty()) {
            throw new IllegalArgumentException("El medico no tiene horarios disponibles para la fecha seleccionada.");
        }

        String hora = cita.getHora() == null ? "" : cita.getHora().trim();
        if (!horasDisponibles.contains(hora)) {
            throw new IllegalArgumentException("La hora seleccionada ya no esta disponible. Recargue y elija otro horario.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
