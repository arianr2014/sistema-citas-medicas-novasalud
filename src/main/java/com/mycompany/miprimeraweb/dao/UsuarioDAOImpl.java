package com.mycompany.miprimeraweb.dao;

import com.mycompany.miprimeraweb.model.Usuario;
import com.mycompany.miprimeraweb.util.ConexionDB;
import com.mycompany.miprimeraweb.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO encargado de validar usuarios del sistema.
 * Fase 2: la validacion ya no compara password directamente en SQL.
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario validarCredenciales(String username, String password) throws SQLException {
        String sql = "SELECT id_usuario, username, password, rol FROM usuario WHERE username = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String passwordAlmacenado = rs.getString("password");

                    if (!PasswordUtil.verificarPassword(password, passwordAlmacenado)) {
                        return null;
                    }

                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setPassword(null); // No guardar contrasena en sesion ni en memoria del objeto.
                    usuario.setRol(rs.getString("rol"));
                    return usuario;
                }
            }
        }

        return null;
    }
}
