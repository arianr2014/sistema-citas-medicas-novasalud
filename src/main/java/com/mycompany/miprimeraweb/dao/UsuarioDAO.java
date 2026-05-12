package com.mycompany.miprimeraweb.dao;

import com.mycompany.miprimeraweb.model.Usuario;
import com.mycompany.miprimeraweb.util.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario validarCredenciales(String username, String password) throws SQLException {
        String sql = "SELECT id_usuario, username, password, rol FROM usuario WHERE username = ? AND password = ?";

        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setRol(rs.getString("rol"));
                    return usuario;
                }
            }
        }

        return null;
    }
}
