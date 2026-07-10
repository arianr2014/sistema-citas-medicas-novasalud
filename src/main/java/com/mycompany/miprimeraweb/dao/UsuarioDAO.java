package com.mycompany.miprimeraweb.dao;

import com.mycompany.miprimeraweb.model.Usuario;
import java.sql.SQLException;

/**
 * DAO encargado de validar usuarios del sistema.
 * Fase 2: la validación ya no compara password directamente en SQL.
 */
public interface UsuarioDAO {

    Usuario validarCredenciales(String username, String password) throws SQLException;
}