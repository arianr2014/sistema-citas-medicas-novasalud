package com.mycompany.miprimeraweb.dao;

import com.mycompany.miprimeraweb.model.AtencionMedica;
import com.mycompany.miprimeraweb.util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** DAO de atención médica e historia clínica operativa. */
public class AtencionMedicaDAO {

    public AtencionMedica obtenerPorCita(int idCita) throws SQLException {
        String sql = "SELECT id_atencion,id_cita,id_medico,id_paciente,codigo_historia,motivo_consulta,sintomas,diagnostico,tratamiento,receta_medica,indicaciones,fecha_atencion "
                + "FROM atencion_medica WHERE id_cita=? AND estado_registro='ACTIVO'";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapper(rs);
            }
        }
        return null;
    }

    public void guardarOActualizar(AtencionMedica atencion) throws SQLException {
        AtencionMedica existente = obtenerPorCita(atencion.getIdCita());
        if (existente == null) {
            insertar(atencion);
        } else {
            atencion.setIdAtencion(existente.getIdAtencion());
            actualizar(atencion);
        }
    }

    private void insertar(AtencionMedica a) throws SQLException {
        String sql = "INSERT INTO atencion_medica(id_cita,id_medico,id_paciente,codigo_historia,motivo_consulta,sintomas,diagnostico,tratamiento,receta_medica,indicaciones,fecha_atencion,estado_registro) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,NOW(),'ACTIVO')";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setValues(ps, a);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) a.setIdAtencion(rs.getInt(1)); }
        }
    }

    private void actualizar(AtencionMedica a) throws SQLException {
        String sql = "UPDATE atencion_medica SET motivo_consulta=?, sintomas=?, diagnostico=?, tratamiento=?, receta_medica=?, indicaciones=?, fecha_atencion=NOW() "
                + "WHERE id_atencion=? AND id_cita=? AND id_medico=? AND estado_registro='ACTIVO'";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, a.getMotivoConsulta());
            ps.setString(2, a.getSintomas());
            ps.setString(3, a.getDiagnostico());
            ps.setString(4, a.getTratamiento());
            ps.setString(5, a.getRecetaMedica());
            ps.setString(6, a.getIndicaciones());
            ps.setInt(7, a.getIdAtencion());
            ps.setInt(8, a.getIdCita());
            ps.setInt(9, a.getIdMedico());
            ps.executeUpdate();
        }
    }

    private void setValues(PreparedStatement ps, AtencionMedica a) throws SQLException {
        ps.setInt(1, a.getIdCita());
        ps.setInt(2, a.getIdMedico());
        ps.setInt(3, a.getIdPaciente());
        ps.setString(4, a.getCodigoHistoria());
        ps.setString(5, a.getMotivoConsulta());
        ps.setString(6, a.getSintomas());
        ps.setString(7, a.getDiagnostico());
        ps.setString(8, a.getTratamiento());
        ps.setString(9, a.getRecetaMedica());
        ps.setString(10, a.getIndicaciones());
    }

    private AtencionMedica mapper(ResultSet rs) throws SQLException {
        AtencionMedica a = new AtencionMedica();
        a.setIdAtencion(rs.getInt("id_atencion"));
        a.setIdCita(rs.getInt("id_cita"));
        a.setIdMedico(rs.getInt("id_medico"));
        a.setIdPaciente(rs.getInt("id_paciente"));
        a.setCodigoHistoria(rs.getString("codigo_historia"));
        a.setMotivoConsulta(rs.getString("motivo_consulta"));
        a.setSintomas(rs.getString("sintomas"));
        a.setDiagnostico(rs.getString("diagnostico"));
        a.setTratamiento(rs.getString("tratamiento"));
        a.setRecetaMedica(rs.getString("receta_medica"));
        a.setIndicaciones(rs.getString("indicaciones"));
        a.setFechaAtencion(rs.getString("fecha_atencion"));
        return a;
    }
}
