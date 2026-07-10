package com.mycompany.miprimeraweb.dao;

import com.mycompany.miprimeraweb.model.Cita;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CitaDAO {

    Map<String, Integer> contarCitasHoyPorEstado(String fechaHoy) throws SQLException;

    int contarMedicosEnTurnoHoy(String fechaHoy) throws SQLException;

    Map<String, Integer> topEspecialidadesPorRango(String fechaInicio, String fechaFin) throws SQLException;

    Map<String, Integer> resumenSemanalPorDia(String fechaInicio, String fechaFin) throws SQLException;

    List<Cita> listarFiltrado(String dniPaciente, int idMedico, int idEspecialidad) throws SQLException;

    List<Cita> listarAgendaProgramada(int idMedico, String fecha) throws SQLException;

    Cita obtenerPorId(int idCita) throws SQLException;

    void guardar(Cita cita, String usuarioRegistro) throws SQLException;

    void eliminar(int idCita) throws SQLException;

    boolean actualizarEstado(int idCita, String estado) throws SQLException;
}
