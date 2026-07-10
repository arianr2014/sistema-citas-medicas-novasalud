package com.mycompany.miprimeraweb.dao;

import com.mycompany.miprimeraweb.model.Medico;
import java.sql.SQLException;
import java.util.List;

public interface MedicoDAO {

    List<Medico> listar(String filtro) throws SQLException;

    Medico obtenerPorId(int idMedico) throws SQLException;

    List<Medico> listarPorEspecialidad(int idEspecialidad) throws SQLException;

    void guardar(Medico medico, String usuarioRegistro) throws SQLException;

    void eliminar(int idMedico) throws SQLException;
}
