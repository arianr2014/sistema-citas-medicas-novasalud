package com.mycompany.miprimeraweb.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestiona la conexión JDBC hacia MySQL.
 *
 * Prioridad de configuración:
 * 1. Variables de entorno específicas del sistema: NOVASALUD_DB_*
 * 2. Variables de entorno genéricas: DB_*
 * 3. Propiedades del sistema Java: db.*
 * 4. Valores locales por defecto para desarrollo académico/local.
 */
public final class ConexionDB {

    /**
     * URL local por defecto para NovaSalud V3.
     * En producción o pruebas formales se recomienda usar variables de entorno.
     */
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/bd_citasmedicas_v321"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";

    /**
     * Usuario de aplicación con permisos limitados sobre bd_citasmedicas_v321.
     */
    private static final String DEFAULT_USER = "usuario_citas";

    /**
     * Contraseña local de desarrollo.
     * En un entorno real no debe quedar fija en el código fuente.
     */
    private static final String DEFAULT_PASSWORD = "ISO/IEC27001";

    /*
     * Carga explícita del driver MySQL.
     * Esto ayuda a detectar rápidamente si falta la dependencia mysql-connector-j.
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError(
                    "No se encontro el driver MySQL (com.mysql.cj.jdbc.Driver)"
            );
        }
    }

    /**
     * Constructor privado porque esta clase es utilitaria.
     */
    private ConexionDB() {
    }

    /**
     * Retorna una conexión activa usando la configuración disponible.
     */
    public static Connection getConnection() throws SQLException {
        String url = readConfig(
                "NOVASALUD_DB_URL",
                "DB_URL",
                "db.url",
                DEFAULT_URL
        );

        String user = readConfig(
                "NOVASALUD_DB_USER",
                "DB_USER",
                "db.user",
                DEFAULT_USER
        );

        String password = readConfig(
                "NOVASALUD_DB_PASSWORD",
                "DB_PASSWORD",
                "db.password",
                DEFAULT_PASSWORD
        );

        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Alias de compatibilidad por si algún DAO usa este nombre.
     */
    public static Connection getConexion() throws SQLException {
        return getConnection();
    }

    /**
     * Alias de compatibilidad por si algún DAO usa este nombre.
     */
    public static Connection obtenerConexion() throws SQLException {
        return getConnection();
    }

    /**
     * Lee configuración en orden de prioridad:
     * variable específica del sistema, variable genérica, propiedad Java y valor por defecto.
     */
    private static String readConfig(
            String specificEnvKey,
            String genericEnvKey,
            String propertyKey,
            String defaultValue
    ) {
        String specificEnvValue = System.getenv(specificEnvKey);
        if (specificEnvValue != null && !specificEnvValue.isBlank()) {
            return specificEnvValue.trim();
        }

        String genericEnvValue = System.getenv(genericEnvKey);
        if (genericEnvValue != null && !genericEnvValue.isBlank()) {
            return genericEnvValue.trim();
        }

        String propertyValue = System.getProperty(propertyKey);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }

        return defaultValue;
    }
}