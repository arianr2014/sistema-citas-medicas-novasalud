package com.mycompany.miprimeraweb.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionDB {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/bd_citasmedicas?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "usuario_citas";
    private static final String DEFAULT_PASSWORD = "ISO/IEC27001";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError("No se encontro el driver MySQL (com.mysql.cj.jdbc.Driver)");
        }
    }

    private ConexionDB() {
    }

    public static Connection getConnection() throws SQLException {
        String url = readConfig("DB_URL", "db.url", DEFAULT_URL);
        String user = readConfig("DB_USER", "db.user", DEFAULT_USER);
        String password = readConfig("DB_PASSWORD", "db.password", DEFAULT_PASSWORD);

        return DriverManager.getConnection(url, user, password);
    }

    private static String readConfig(String envKey, String propertyKey, String defaultValue) {
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }

        String propertyValue = System.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }

        return defaultValue;
    }
}
