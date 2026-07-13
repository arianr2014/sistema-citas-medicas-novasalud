package com.mycompany.miprimeraweb.dao;

import com.mycompany.miprimeraweb.model.Cita;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO del módulo Citas.
 *
 * V3.2:
 * - Registra trazabilidad de recepcionista, especialidad, tarifa y monto aplicado.
 * - Permite agenda flexible por fecha, médico y especialidad.
 * - Mantiene estado CANCELADA / NO_ASISTIO visible para reportes.
 */
public interface CitaDAO {

    Map<String, Integer> contarCitasHoyPorEstado(String fechaHoy) throws SQLException;

    int contarMedicosEnTurnoHoy(String fechaHoy) throws SQLException;

    Map<String, Integer> topEspecialidadesPorRango(String fechaInicio, String fechaFin) throws SQLException;

    Map<String, Integer> resumenSemanalPorDia(String fechaInicio, String fechaFin) throws SQLException;

    List<Cita> listarFiltrado(String dniPaciente, int idMedico, int idEspecialidad) throws SQLException;

    List<Cita> listarAgendaProgramada(int idMedico, String fecha) throws SQLException;

    List<Cita> listarAgendaProgramadaFlexible(int idMedico, int idEspecialidad, String fecha) throws SQLException;

    Map<String, Integer> resumenMensualPorMedico(int idMedico, String inicioMes, String finMes) throws SQLException;

    int contarCitasActivasMedicoFecha(int idMedico, String fecha, int idCitaExcluir) throws SQLException;

    Cita obtenerPorId(int idCita) throws SQLException;

    void guardar(Cita cita, String usuarioRegistro) throws SQLException;

    void eliminar(int idCita) throws SQLException;

    boolean actualizarEstado(int idCita, String estado) throws SQLException;

    boolean actualizarEstadoPorMedico(int idCita, int idMedico, String estado) throws SQLException;
}
