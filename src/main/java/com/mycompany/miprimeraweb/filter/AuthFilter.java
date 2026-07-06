package com.mycompany.miprimeraweb.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Filtro de autenticación y autorización del sistema.
 *
 * Fase 4:
 * - Verifica si el usuario inició sesión.
 * - Valida el rol del usuario.
 * - Bloquea el acceso manual por URL a módulos no permitidos.
 * - Redirige a una página personalizada de acceso denegado.
 *
 * Tema aplicado:
 * Control de acceso basado en roles, conocido como RBAC.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No se requiere inicialización adicional.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String requestPath = obtenerRutaSolicitada(httpRequest, contextPath);

        /*
         * Las rutas públicas no requieren sesión.
         * Ejemplos: login, logout, CSS, JS, imágenes.
         */
        if (esRutaPublica(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        /*
         * Validación de sesión.
         * Si no existe usuario logueado, se redirige al login.
         */
        HttpSession session = httpRequest.getSession(false);
        boolean autenticado = session != null && session.getAttribute("usuarioLogeado") != null;

        if (!autenticado) {
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        /*
         * Validación de rol.
         * Aunque el menú oculte opciones, igual se valida la URL manual.
         */
        String rol = String.valueOf(session.getAttribute("rolUsuario"));

        if (!esRutaPermitidaPorRol(requestPath, rol)) {
            String rutaCodificada = URLEncoder.encode(requestPath, StandardCharsets.UTF_8);
            httpResponse.sendRedirect(contextPath + "/acceso-denegado?from=" + rutaCodificada);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No hay recursos que liberar.
    }

    /**
     * Obtiene la ruta solicitada sin el contextPath.
     */
    private String obtenerRutaSolicitada(HttpServletRequest request, String contextPath) {
        String uri = request.getRequestURI();
        return uri.substring(contextPath.length());
    }

    /**
     * Define rutas públicas del sistema.
     */
    private boolean esRutaPublica(String requestPath) {
        return requestPath.equals("/")
                || requestPath.startsWith("/login")
                || requestPath.startsWith("/logout")
                || requestPath.startsWith("/css/")
                || requestPath.startsWith("/js/")
                || requestPath.startsWith("/resources/")
                || requestPath.endsWith(".css")
                || requestPath.endsWith(".js")
                || requestPath.endsWith(".png")
                || requestPath.endsWith(".jpg")
                || requestPath.endsWith(".jpeg")
                || requestPath.endsWith(".gif")
                || requestPath.endsWith(".svg")
                || requestPath.endsWith(".ico");
    }

    /**
     * Valida si la ruta solicitada está permitida para el rol del usuario.
     */
    private boolean esRutaPermitidaPorRol(String requestPath, String rol) {
        String rolNormalizado = normalizarRol(rol);

        /*
         * Ruta interna de error.
         * Requiere sesión, pero puede verla cualquier rol autenticado.
         */
        if (requestPath.startsWith("/acceso-denegado")) {
            return true;
        }

        /*
         * ADMIN:
         * Acceso completo al sistema.
         */
        if ("ADMIN".equals(rolNormalizado)) {
            return true;
        }

        /*
         * RECEPCIONISTA:
         * Gestiona pacientes, citas y consulta agenda médica.
         */
        if ("RECEPCIONISTA".equals(rolNormalizado)) {
            return requestPath.startsWith("/pacientes")
                    || requestPath.startsWith("/citas")
                    || requestPath.startsWith("/agenda-medico");
        }

        /*
         * DOCTOR:
         * Solo accede a la agenda médica.
         */
        if ("DOCTOR".equals(rolNormalizado)) {
            return requestPath.startsWith("/agenda-medico");
        }

        return false;
    }

    /**
     * Normaliza el rol para evitar problemas por espacios o minúsculas.
     */
    private String normalizarRol(String rol) {
        return rol == null ? "" : rol.trim().toUpperCase();
    }
}