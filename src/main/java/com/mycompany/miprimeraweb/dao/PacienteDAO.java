package com.mycompany.miprimeraweb.dao;

import com.mycompany.miprimeraweb.model.Paciente;
import com.mycompany.miprimeraweb.util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** DAO de pacientes con datos administrativos e historia clínica base. */
public class PacienteDAO {

    public List<Paciente> listar(String filtro) throws SQLException {
        List<Paciente> pacientes = new ArrayList<>();
        String texto = filtro == null ? "" : filtro.trim();
        StringBuilder sql = new StringBuilder(
                "SELECT id_paciente,dni,nombres,apellidos,telefono,correo,direccion,fecha_nacimiento,sexo,contacto_emergencia,telefono_emergencia,historia_clinica_codigo "
                + "FROM paciente WHERE estado_registro='ACTIVO' ");
        if (!texto.isEmpty()) sql.append("AND (dni LIKE ? OR nombres LIKE ? OR apellidos LIKE ? OR historia_clinica_codigo LIKE ?) ");
        sql.append("ORDER BY id_paciente DESC");
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            if (!texto.isEmpty()) {
                String like = "%" + texto + "%";
                ps.setString(1, like); ps.setString(2, like); ps.setString(3, like); ps.setString(4, like);
            }
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) pacientes.add(mapper(rs)); }
        }
        return pacientes;
    }

    public Paciente obtenerPorId(int idPaciente) throws SQLException {
        String sql = "SELECT id_paciente,dni,nombres,apellidos,telefono,correo,direccion,fecha_nacimiento,sexo,contacto_emergencia,telefono_emergencia,historia_clinica_codigo FROM paciente WHERE id_paciente=? AND estado_registro='ACTIVO'";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapper(rs); }
        }
        return null;
    }

    public Paciente obtenerPorDni(String dni) throws SQLException {
        String sql = "SELECT id_paciente,dni,nombres,apellidos,telefono,correo,direccion,fecha_nacimiento,sexo,contacto_emergencia,telefono_emergencia,historia_clinica_codigo FROM paciente WHERE dni=? AND estado_registro='ACTIVO'";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapper(rs); }
        }
        return null;
    }

    public void guardar(Paciente paciente, String usuarioRegistro) throws SQLException {
        if (paciente.getIdPaciente() > 0) actualizar(paciente); else registrar(paciente, usuarioRegistro);
    }

    private void registrar(Paciente p, String usuarioRegistro) throws SQLException {
        String sql = "INSERT INTO paciente(dni,nombres,apellidos,telefono,correo,direccion,fecha_nacimiento,sexo,contacto_emergencia,telefono_emergencia,historia_clinica_codigo,usuario_registro,fecha_registro,estado_registro) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,NOW(),'ACTIVO')";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setValues(ps, p);
            ps.setString(11, p.getDni());
            ps.setString(12, usuarioRegistro == null || usuarioRegistro.isBlank() ? "sistema" : usuarioRegistro);
            ps.executeUpdate();
            int idGenerado = 0;
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) idGenerado = rs.getInt(1); }
            if (idGenerado > 0) crearHistoriaClinicaBase(cn, idGenerado, p.getDni(), usuarioRegistro);
        }
    }

    private void actualizar(Paciente p) throws SQLException {
        String sql = "UPDATE paciente SET dni=?, nombres=?, apellidos=?, telefono=?, correo=?, direccion=?, fecha_nacimiento=?, sexo=?, contacto_emergencia=?, telefono_emergencia=?, historia_clinica_codigo=?, fecha_actualizacion=NOW() WHERE id_paciente=? AND estado_registro='ACTIVO'";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            setValues(ps, p);
            ps.setString(11, p.getDni());
            ps.setInt(12, p.getIdPaciente());
            ps.executeUpdate();
            sincronizarCodigoHistoria(cn, p.getIdPaciente(), p.getDni());
        }
    }

    public void eliminar(int idPaciente) throws SQLException {
        String sql = "UPDATE paciente SET estado_registro='INACTIVO', fecha_actualizacion=NOW() WHERE id_paciente=?";
        try (Connection cn = ConexionDB.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente); ps.executeUpdate();
        }
    }

    /**
     * En NovaSalud V3.2.1 el código de historia clínica usa el DNI del paciente.
     * Esto evita duplicidad porque el DNI ya es único en el padrón de pacientes.
     */
    private void crearHistoriaClinicaBase(Connection cn, int idPaciente, String codigoHistoria, String usuario) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("INSERT INTO historia_clinica(id_paciente,codigo_historia,antecedentes_generales,alergias,usuario_registro,fecha_registro,estado_registro) VALUES (?,?,?,?,?,NOW(),'ACTIVO')")) {
            ps.setInt(1, idPaciente);
            ps.setString(2, codigoHistoria);
            ps.setString(3, "Pendiente de evaluación médica.");
            ps.setString(4, "No registradas.");
            ps.setString(5, usuario == null || usuario.isBlank() ? "sistema" : usuario);
            ps.executeUpdate();
        }
    }

    /** Mantiene coherencia si se corrige el DNI administrativo del paciente. */
    private void sincronizarCodigoHistoria(Connection cn, int idPaciente, String codigoHistoria) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("UPDATE historia_clinica SET codigo_historia=? WHERE id_paciente=?")) {
            ps.setString(1, codigoHistoria);
            ps.setInt(2, idPaciente);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = cn.prepareStatement("UPDATE atencion_medica SET codigo_historia=? WHERE id_paciente=?")) {
            ps.setString(1, codigoHistoria);
            ps.setInt(2, idPaciente);
            ps.executeUpdate();
        }
    }

    private void setValues(PreparedStatement ps, Paciente p) throws SQLException {
        ps.setString(1, p.getDni()); ps.setString(2, p.getNombres()); ps.setString(3, p.getApellidos()); ps.setString(4, p.getTelefono()); ps.setString(5, p.getCorreo()); ps.setString(6, p.getDireccion());
        if (p.getFechaNacimiento() == null || p.getFechaNacimiento().isBlank()) ps.setNull(7, java.sql.Types.DATE); else ps.setString(7, p.getFechaNacimiento());
        ps.setString(8, p.getSexo()); ps.setString(9, p.getContactoEmergencia()); ps.setString(10, p.getTelefonoEmergencia());
    }

    private Paciente mapper(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setIdPaciente(rs.getInt("id_paciente")); p.setDni(rs.getString("dni")); p.setNombres(rs.getString("nombres")); p.setApellidos(rs.getString("apellidos")); p.setTelefono(rs.getString("telefono")); p.setCorreo(rs.getString("correo")); p.setDireccion(rs.getString("direccion")); p.setFechaNacimiento(rs.getString("fecha_nacimiento")); p.setSexo(rs.getString("sexo")); p.setContactoEmergencia(rs.getString("contacto_emergencia")); p.setTelefonoEmergencia(rs.getString("telefono_emergencia")); p.setHistoriaClinicaCodigo(rs.getString("historia_clinica_codigo"));
        return p;
    }
}
