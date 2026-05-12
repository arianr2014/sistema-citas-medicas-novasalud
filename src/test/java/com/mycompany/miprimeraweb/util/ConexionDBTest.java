package com.mycompany.miprimeraweb.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Test;

class ConexionDBTest {

    @Test
    void shouldOpenDatabaseConnection() throws Exception {
        try (Connection connection = ConexionDB.getConnection()) {
            assertNotNull(connection, "La conexion no debe ser nula");
            assertTrue(connection.isValid(2), "La conexion a la base de datos no es valida");
        }
    }

    @Test
    void shouldReadUsuarioTable() throws Exception {
        String sql = "SELECT COUNT(*) FROM usuario";

        try (Connection connection = ConexionDB.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            assertTrue(rs.next(), "La consulta de usuario no retorno resultados");
            assertTrue(rs.getInt(1) >= 0, "El conteo de usuario debe ser mayor o igual a 0");
        }
    }
}
