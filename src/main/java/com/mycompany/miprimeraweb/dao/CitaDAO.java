package com.mycompany.miprimeraweb.dao;

import com.mycompany.miprimeraweb.model.Cita;
import com.mycompany.miprimeraweb.util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
public class CitaDAO {

    private static final DateTimeFormatter HORA_MINUTOS = DateTimeFormatter.ofPattern("HH:mm");

    public Map<String, Integer> contarCitasHoyPorEstado(String fechaHoy) throws SQLException {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT UPPER(estado) AS estado, COUNT(*) AS total FROM cita WHERE estado_registro='ACTIVO' AND fecha=? GROUP BY UPPER(estado) ORDER BY total DESC";
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fechaHoy);
            try (ResultSet rs = statement.executeQuery()) { while (rs.next()) resultado.put(rs.getString("estado"), rs.getInt("total")); }
        }
        return resultado;
    }

    public int contarMedicosEnTurnoHoy(String fechaHoy) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT id_medico) AS total FROM cita WHERE estado_registro='ACTIVO' AND fecha=? AND UPPER(estado) NOT IN ('CANCELADA','ANULADA','NO_ASISTIO')";
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fechaHoy);
            try (ResultSet rs = statement.executeQuery()) { if (rs.next()) return rs.getInt("total"); }
        }
        return 0;
    }

    public Map<String, Integer> topEspecialidadesPorRango(String fechaInicio, String fechaFin) throws SQLException {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT e.nombre AS especialidad, COUNT(*) AS total FROM cita c "
                + "INNER JOIN medico m ON c.id_medico=m.id_medico INNER JOIN especialidad e ON m.id_especialidad=e.id_especialidad "
                + "WHERE c.estado_registro='ACTIVO' AND c.fecha BETWEEN ? AND ? GROUP BY e.id_especialidad, e.nombre ORDER BY total DESC, e.nombre ASC";
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fechaInicio); statement.setString(2, fechaFin);
            try (ResultSet rs = statement.executeQuery()) { while (rs.next()) resultado.put(rs.getString("especialidad"), rs.getInt("total")); }
        }
        return resultado;
    }

    public Map<String, Integer> resumenSemanalPorDia(String fechaInicio, String fechaFin) throws SQLException {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT fecha, COUNT(*) AS total FROM cita WHERE estado_registro='ACTIVO' AND fecha BETWEEN ? AND ? GROUP BY fecha ORDER BY fecha ASC";
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fechaInicio); statement.setString(2, fechaFin);
            try (ResultSet rs = statement.executeQuery()) { while (rs.next()) resultado.put(rs.getString("fecha"), rs.getInt("total")); }
        }
        return resultado;
    }

    public List<Cita> listarFiltrado(String dniPaciente, int idMedico, int idEspecialidad) throws SQLException {
        List<Cita> citas = new ArrayList<>();
        String dni = dniPaciente == null ? "" : dniPaciente.trim();
        StringBuilder sql = new StringBuilder(baseListado() + "WHERE c.estado_registro='ACTIVO' ");
        if (!dni.isEmpty()) sql.append("AND p.dni = ? ");
        if (idMedico > 0) sql.append("AND c.id_medico = ? ");
        if (idEspecialidad > 0) sql.append("AND m.id_especialidad = ? ");
        sql.append("ORDER BY c.fecha DESC, c.hora DESC");
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int index = 1;
            if (!dni.isEmpty()) statement.setString(index++, dni);
            if (idMedico > 0) statement.setInt(index++, idMedico);
            if (idEspecialidad > 0) statement.setInt(index, idEspecialidad);
            try (ResultSet rs = statement.executeQuery()) { while (rs.next()) citas.add(mapperListado(rs)); }
        }
        return citas;
    }

    public List<Cita> listarAgendaProgramada(int idMedico, String fecha) throws SQLException {
        return listarAgendaProgramadaFlexible(idMedico, 0, fecha);
    }

    public List<Cita> listarAgendaProgramadaFlexible(int idMedico, int idEspecialidad, String fecha) throws SQLException {
        List<Cita> citas = new ArrayList<>();
        StringBuilder sql = new StringBuilder(baseListado()
                + "WHERE c.estado_registro='ACTIVO' AND c.fecha=? "
                + "AND UPPER(COALESCE(c.estado,'')) IN ('PROGRAMADA','CONFIRMADA','REPROGRAMADA','EN_ESPERA','EN_CONSULTA','ATENDIDA','NO_ASISTIO') ");
        if (idMedico > 0) sql.append("AND c.id_medico=? ");
        if (idEspecialidad > 0) sql.append("AND m.id_especialidad=? ");
        sql.append("ORDER BY m.apellidos ASC, m.nombres ASC, c.hora ASC");
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int i = 1;
            statement.setString(i++, fecha);
            if (idMedico > 0) statement.setInt(i++, idMedico);
            if (idEspecialidad > 0) statement.setInt(i, idEspecialidad);
            try (ResultSet rs = statement.executeQuery()) { while (rs.next()) citas.add(mapperListado(rs)); }
        }
        return citas;
    }


    /** Resumen mensual de citas por día para vista calendario. */
    public Map<String, Integer> resumenMensualPorMedico(int idMedico, String inicioMes, String finMes) throws SQLException {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT fecha, COUNT(*) AS total FROM cita WHERE estado_registro='ACTIVO' AND id_medico=? AND fecha BETWEEN ? AND ? GROUP BY fecha ORDER BY fecha";
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idMedico); statement.setString(2, inicioMes); statement.setString(3, finMes);
            try (ResultSet rs = statement.executeQuery()) { while (rs.next()) resultado.put(rs.getString("fecha"), rs.getInt("total")); }
        }
        return resultado;
    }

    /** Cuenta citas activas del médico en una fecha para respetar la capacidad diaria configurada. */
    public int contarCitasActivasMedicoFecha(int idMedico, String fecha, int idCitaExcluir) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM cita WHERE id_medico=? AND fecha=? AND estado_registro='ACTIVO' AND UPPER(estado) NOT IN ('CANCELADA','ANULADA','NO_ASISTIO') ");
        if (idCitaExcluir > 0) sql.append("AND id_cita<>? ");
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setInt(1, idMedico); statement.setString(2, fecha);
            if (idCitaExcluir > 0) statement.setInt(3, idCitaExcluir);
            try (ResultSet rs = statement.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    public Cita obtenerPorId(int idCita) throws SQLException {
        String sql = baseListado() + "WHERE c.id_cita=? AND c.estado_registro='ACTIVO'";
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idCita);
            try (ResultSet rs = statement.executeQuery()) { if (rs.next()) return mapperListado(rs); }
        }
        return null;
    }

    public void guardar(Cita cita, String usuarioRegistro) throws SQLException {
        if (cita.getIdCita() > 0) actualizar(cita); else registrar(cita, usuarioRegistro);
    }

    private void registrar(Cita cita, String usuarioRegistro) throws SQLException {
        String sql = "INSERT INTO cita(id_paciente,id_medico,id_especialidad,id_tarifa_aplicada,id_usuario_recepcionista,monto_consulta,fecha,hora,duracion_minutos,estado,observaciones,usuario_registro,fecha_registro,estado_registro) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,NOW(),'ACTIVO')";
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, cita.getIdPaciente());
            statement.setInt(2, cita.getIdMedico());
            setNullableInt(statement, 3, cita.getIdEspecialidad());
            setNullableInt(statement, 4, cita.getIdTarifaAplicada());
            setNullableInt(statement, 5, cita.getIdUsuarioRecepcionista());
            statement.setBigDecimal(6, cita.getMontoConsultaDecimal());
            statement.setString(7, cita.getFecha());
            statement.setString(8, cita.getHora());
            statement.setInt(9, cita.getDuracionMinutos() == null || cita.getDuracionMinutos() <= 0 ? 30 : cita.getDuracionMinutos());
            statement.setString(10, cita.getEstado());
            statement.setString(11, cita.getObservaciones());
            statement.setString(12, usuarioRegistro);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) { if (keys.next()) cita.setIdCita(keys.getInt(1)); }
        }
    }

    private void actualizar(Cita cita) throws SQLException {
        String sql = "UPDATE cita SET id_paciente=?, id_medico=?, id_especialidad=?, id_tarifa_aplicada=?, monto_consulta=?, fecha=?, hora=?, duracion_minutos=?, estado=?, observaciones=?, fecha_actualizacion=NOW() WHERE id_cita=? AND estado_registro='ACTIVO'";
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, cita.getIdPaciente());
            statement.setInt(2, cita.getIdMedico());
            setNullableInt(statement, 3, cita.getIdEspecialidad());
            setNullableInt(statement, 4, cita.getIdTarifaAplicada());
            statement.setBigDecimal(5, cita.getMontoConsultaDecimal());
            statement.setString(6, cita.getFecha());
            statement.setString(7, cita.getHora());
            statement.setInt(8, cita.getDuracionMinutos() == null || cita.getDuracionMinutos() <= 0 ? 30 : cita.getDuracionMinutos());
            statement.setString(9, cita.getEstado());
            statement.setString(10, cita.getObservaciones());
            statement.setInt(11, cita.getIdCita());
            statement.executeUpdate();
        }
    }

    public void eliminar(int idCita) throws SQLException {
        String sql = "UPDATE cita SET estado='CANCELADA', fecha_actualizacion=NOW() WHERE id_cita=? AND estado_registro='ACTIVO' AND UPPER(estado) NOT IN ('ATENDIDA','ANULADA')";
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idCita); statement.executeUpdate();
        }
    }

    public boolean actualizarEstado(int idCita, String estado) throws SQLException {
        String sql = "UPDATE cita SET estado=?, fecha_actualizacion=NOW() WHERE id_cita=? AND estado_registro='ACTIVO' AND UPPER(COALESCE(estado,'')) NOT IN ('ATENDIDA','ANULADA','CANCELADA')";
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, estado == null ? "" : estado.trim().toUpperCase());
            statement.setInt(2, idCita);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean actualizarEstadoPorMedico(int idCita, int idMedico, String estado) throws SQLException {
        String sql = "UPDATE cita SET estado=?, fecha_actualizacion=NOW() WHERE id_cita=? AND id_medico=? AND estado_registro='ACTIVO' AND UPPER(COALESCE(estado,'')) NOT IN ('ATENDIDA','ANULADA','CANCELADA','NO_ASISTIO')";
        try (Connection connection = ConexionDB.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, estado == null ? "" : estado.trim().toUpperCase());
            statement.setInt(2, idCita);
            statement.setInt(3, idMedico);
            return statement.executeUpdate() > 0;
        }
    }

    private String baseListado() {
        return "SELECT c.id_cita,c.id_paciente,c.id_medico,c.id_especialidad,c.id_tarifa_aplicada,c.id_usuario_recepcionista,c.monto_consulta,c.fecha,c.hora,c.hora_fin,c.duracion_minutos,c.estado,c.observaciones, "
                + "p.dni,p.historia_clinica_codigo,p.nombres AS paciente_nombres,p.apellidos AS paciente_apellidos, "
                + "m.nombres AS medico_nombres,m.apellidos AS medico_apellidos,e.nombre AS especialidad, "
                + "COALESCE(pg.estado_pago,'PENDIENTE') AS estado_pago, COALESCE(pg.monto_total,COALESCE(c.monto_consulta,0)) AS monto_consulta_pago, pg.id_pago, am.id_atencion "
                + "FROM cita c INNER JOIN paciente p ON c.id_paciente=p.id_paciente "
                + "INNER JOIN medico m ON c.id_medico=m.id_medico INNER JOIN especialidad e ON m.id_especialidad=e.id_especialidad "
                + "LEFT JOIN pago pg ON c.id_cita=pg.id_cita AND pg.estado_registro='ACTIVO' LEFT JOIN atencion_medica am ON c.id_cita=am.id_cita AND am.estado_registro='ACTIVO' ";
    }

    private Cita mapperListado(ResultSet rs) throws SQLException {
        Cita cita = new Cita();
        cita.setIdCita(rs.getInt("id_cita"));
        cita.setIdPaciente(rs.getInt("id_paciente"));
        cita.setIdMedico(rs.getInt("id_medico"));
        int idEspecialidad = rs.getInt("id_especialidad"); cita.setIdEspecialidad(rs.wasNull() ? null : idEspecialidad);
        int idTarifa = rs.getInt("id_tarifa_aplicada"); cita.setIdTarifaAplicada(rs.wasNull() ? null : idTarifa);
        int idRecepcionista = rs.getInt("id_usuario_recepcionista"); cita.setIdUsuarioRecepcionista(rs.wasNull() ? null : idRecepcionista);
        java.math.BigDecimal montoCita = rs.getBigDecimal("monto_consulta");
        if (montoCita == null) {
            montoCita = rs.getBigDecimal("monto_consulta_pago");
        }
        cita.setMontoConsultaDecimal(montoCita);
        cita.setFecha(rs.getString("fecha"));
        cita.setHora(formatHora(rs.getTime("hora")));
        cita.setHoraFin(formatHora(rs.getTime("hora_fin")));
        cita.setDuracionMinutos(rs.getInt("duracion_minutos"));
        cita.setEstado(rs.getString("estado"));
        cita.setObservaciones(rs.getString("observaciones"));
        cita.setPacienteDni(rs.getString("dni"));
        cita.setHistoriaClinicaCodigo(rs.getString("historia_clinica_codigo"));
        cita.setPacienteNombreCompleto((rs.getString("paciente_nombres") + " " + rs.getString("paciente_apellidos")).trim());
        cita.setMedicoNombreCompleto((rs.getString("medico_nombres") + " " + rs.getString("medico_apellidos")).trim());
        cita.setEspecialidad(rs.getString("especialidad"));
        cita.setEstadoPago(rs.getString("estado_pago"));
        cita.setMontoConsulta(String.valueOf(rs.getBigDecimal("monto_consulta_pago")));
        int idPago = rs.getInt("id_pago"); cita.setIdPago(rs.wasNull() ? null : idPago);
        int idAtencion = rs.getInt("id_atencion"); cita.setIdAtencion(rs.wasNull() ? null : idAtencion);
        return cita;
    }

    private void setNullableInt(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null || value <= 0) statement.setNull(index, java.sql.Types.INTEGER); else statement.setInt(index, value);
    }

    private String formatHora(Time time) { return time == null ? "" : time.toLocalTime().format(HORA_MINUTOS); }
}
