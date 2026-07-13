package com.mycompany.miprimeraweb.dao;

import com.mycompany.miprimeraweb.model.Medico;
import com.mycompany.miprimeraweb.util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** DAO de médicos con parámetros de agenda y capacidad diaria. */
public class MedicoDAO {

    public List<Medico> listar(String filtro) throws SQLException {
        List<Medico> medicos = new ArrayList<>();
        String texto = filtro == null ? "" : filtro.trim();
        StringBuilder sql = new StringBuilder(baseSelect() + "WHERE m.estado_registro='ACTIVO' AND e.estado_registro='ACTIVO' ");
        if (!texto.isEmpty()) sql.append("AND (m.nombres LIKE ? OR m.apellidos LIKE ? OR e.nombre LIKE ? OR m.cmp LIKE ?) ");
        sql.append("ORDER BY e.nombre ASC, m.apellidos ASC, m.nombres ASC");
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            if (!texto.isEmpty()) { String like = "%" + texto + "%"; ps.setString(1, like); ps.setString(2, like); ps.setString(3, like); ps.setString(4, like); }
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) medicos.add(mapper(rs)); }
        }
        return medicos;
    }

    public Medico obtenerPorId(int idMedico) throws SQLException {
        String sql = baseSelect() + "WHERE m.id_medico=? AND m.estado_registro='ACTIVO'";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapper(rs); }
        }
        return null;
    }

    public List<Medico> listarPorEspecialidad(int idEspecialidad) throws SQLException {
        List<Medico> medicos = new ArrayList<>();
        String sql = baseSelect() + "WHERE m.id_especialidad=? AND m.estado_registro='ACTIVO' ORDER BY m.apellidos, m.nombres";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEspecialidad);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) medicos.add(mapper(rs)); }
        }
        return medicos;
    }

    public void guardar(Medico medico, String usuarioRegistro) throws SQLException {
        if (medico.getIdMedico() > 0) actualizar(medico); else registrar(medico, usuarioRegistro);
    }

    private void registrar(Medico m, String usuarioRegistro) throws SQLException {
        String sql = "INSERT INTO medico(nombres,apellidos,id_especialidad,dni,telefono,correo,cmp,duracion_cita_minutos,tolerancia_minutos,max_citas_dia,consultorio,usuario_registro,fecha_registro,estado_registro) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,NOW(),'ACTIVO')";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) { setValues(ps, m); ps.setString(12, usuarioRegistro == null || usuarioRegistro.isBlank() ? "sistema" : usuarioRegistro); ps.executeUpdate(); }
    }

    private void actualizar(Medico m) throws SQLException {
        String sql = "UPDATE medico SET nombres=?,apellidos=?,id_especialidad=?,dni=?,telefono=?,correo=?,cmp=?,duracion_cita_minutos=?,tolerancia_minutos=?,max_citas_dia=?,consultorio=?,fecha_actualizacion=NOW() WHERE id_medico=? AND estado_registro='ACTIVO'";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) { setValues(ps, m); ps.setInt(12, m.getIdMedico()); ps.executeUpdate(); }
    }

    public void eliminar(int idMedico) throws SQLException {
        String sql = "UPDATE medico SET estado_registro='INACTIVO', fecha_actualizacion=NOW() WHERE id_medico=?";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) { ps.setInt(1, idMedico); ps.executeUpdate(); }
    }

    public int contarCitasActivasDia(int idMedico, String fecha) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cita WHERE id_medico=? AND fecha=? AND estado_registro='ACTIVO' AND UPPER(estado) NOT IN ('CANCELADA','ANULADA','NO_ASISTIO')";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) { ps.setInt(1, idMedico); ps.setString(2, fecha); try(ResultSet rs=ps.executeQuery()){ return rs.next()?rs.getInt(1):0; } }
    }

    private String baseSelect() {
        return "SELECT m.id_medico,m.nombres,m.apellidos,m.id_especialidad,e.nombre AS especialidad,m.dni,m.telefono,m.correo,m.cmp,m.duracion_cita_minutos,m.tolerancia_minutos,m.max_citas_dia,m.consultorio FROM medico m INNER JOIN especialidad e ON m.id_especialidad=e.id_especialidad ";
    }

    private void setValues(PreparedStatement ps, Medico m) throws SQLException {
        ps.setString(1, m.getNombres()); ps.setString(2, m.getApellidos()); ps.setInt(3, m.getIdEspecialidad()); ps.setString(4, m.getDni()); ps.setString(5, m.getTelefono()); ps.setString(6, m.getCorreo()); ps.setString(7, m.getCmp()); ps.setInt(8, m.getDuracionCitaMinutos() <= 0 ? 30 : m.getDuracionCitaMinutos()); ps.setInt(9, m.getToleranciaMinutos() < 0 ? 10 : m.getToleranciaMinutos()); ps.setInt(10, m.getMaxCitasDia() <= 0 ? 12 : m.getMaxCitasDia()); ps.setString(11, m.getConsultorio());
    }

    private Medico mapper(ResultSet rs) throws SQLException {
        Medico m = new Medico();
        m.setIdMedico(rs.getInt("id_medico")); m.setNombres(rs.getString("nombres")); m.setApellidos(rs.getString("apellidos")); m.setIdEspecialidad(rs.getInt("id_especialidad")); m.setNombreEspecialidad(rs.getString("especialidad")); m.setDni(rs.getString("dni")); m.setTelefono(rs.getString("telefono")); m.setCorreo(rs.getString("correo")); m.setCmp(rs.getString("cmp")); m.setDuracionCitaMinutos(rs.getInt("duracion_cita_minutos")); m.setToleranciaMinutos(rs.getInt("tolerancia_minutos")); m.setMaxCitasDia(rs.getInt("max_citas_dia")); m.setConsultorio(rs.getString("consultorio"));
        return m;
    }
}
