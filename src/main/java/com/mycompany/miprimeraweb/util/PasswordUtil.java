package com.mycompany.miprimeraweb.util;
/**
 * author@: FRNACK
 */
import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para generar y verificar contraseñas con BCrypt.
 * Mejora Fase 2: evita comparar contraseñas directamente en texto plano.
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    /**
     * Genera un hash seguro BCrypt a partir de una contraseña en texto plano.
     *
     * @param passwordPlano contraseña escrita por el usuario.
     * @return hash BCrypt listo para guardar en base de datos.
     */
    public static String generarHash(String passwordPlano) {
        return BCrypt.hashpw(passwordPlano, BCrypt.gensalt(12));
    }

    /**
     * Verifica si la contraseña ingresada coincide con el hash almacenado.
     *
     * Nota: se mantiene compatibilidad temporal con contraseñas antiguas en texto plano
     * para no romper el login durante la migración.
     *
     * @param passwordPlano contraseña ingresada en el formulario.
     * @param passwordAlmacenado hash BCrypt o contraseña antigua en texto plano.
     * @return true si coincide; false si no coincide.
     */
    public static boolean verificarPassword(String passwordPlano, String passwordAlmacenado) {
        if (passwordPlano == null || passwordAlmacenado == null || passwordAlmacenado.isBlank()) {
            return false;
        }

        String valorAlmacenado = passwordAlmacenado.trim();

        try {
            if (valorAlmacenado.startsWith("$2a$")) {
                return BCrypt.checkpw(passwordPlano, valorAlmacenado);
            }

            // Compatibilidad temporal con la versión anterior del sistema.
            return passwordPlano.equals(valorAlmacenado);

        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}