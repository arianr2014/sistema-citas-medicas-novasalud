package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.UsuarioDAO;
import com.mycompany.miprimeraweb.dao.UsuarioDAOImpl;
import com.mycompany.miprimeraweb.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Controlador de autenticación.
 *
 * Responsabilidades:
 * - Mostrar login.
 * - Validar credenciales.
 * - Crear sesión.
 * - Guardar usuario y rol en sesión.
 * - Cerrar sesión.
 *
 * Fase 4:
 * Se refuerza el manejo de sesión y redirección por rol.
 */
@WebServlet({"/login", "/logout"})
public class AuthController extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    /**
     * Atiende la navegación hacia login o logout.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();

        /*
         * Cierre de sesión:
         * Invalida la sesión actual y vuelve al login.
         */
        if ("/logout".equals(servletPath)) {
            cerrarSesion(request, response);
            return;
        }

        /*
         * Si el usuario ya está autenticado, no se vuelve a mostrar login.
         * Se redirige a la pantalla inicial según su rol.
         */
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuarioLogeado") != null) {
            String rol = (String) session.getAttribute("rolUsuario");
            String rutaInicio = obtenerRutaInicioPorRol(rol);

            if ("/login".equals(rutaInicio)) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login?error=rol");
                return;
            }

            response.sendRedirect(request.getContextPath() + rutaInicio);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    /**
     * Atiende el envío del formulario de login.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = texto(request.getParameter("username"));
        String password = texto(request.getParameter("password"));

        if (username.isBlank() || password.isBlank()) {
            request.setAttribute("error", "Usuario y contrasena son obligatorios.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        try {
            Usuario usuario = usuarioDAO.validarCredenciales(username, password);

            if (usuario != null) {
                iniciarSesionSegura(request, response, usuario);
                return;
            }

        } catch (SQLException ex) {
            /*
             * Buena práctica:
             * No mostramos detalles técnicos de base de datos al usuario final.
             */
            request.setAttribute("error", "No se pudo validar el acceso. Intente nuevamente.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        request.setAttribute("error", "Credenciales invalidas.");
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    /**
     * Crea una nueva sesión segura para el usuario autenticado.
     */
    private void iniciarSesionSegura(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws IOException {

        /*
         * Seguridad:
         * Se invalida cualquier sesión anterior antes de crear una nueva.
         * Esto ayuda a reducir riesgos de fijación de sesión.
         */
        HttpSession sesionAnterior = request.getSession(false);
        if (sesionAnterior != null) {
            sesionAnterior.invalidate();
        }

        HttpSession session = request.getSession(true);

        /*
         * Tiempo máximo de inactividad: 30 minutos.
         */
        session.setMaxInactiveInterval(30 * 60);

        session.setAttribute("usuarioLogeado", usuario.getUsername());
        session.setAttribute("rolUsuario", usuario.getRol());
        session.setAttribute("idUsuario", usuario.getIdUsuario());

        response.sendRedirect(request.getContextPath() + obtenerRutaInicioPorRol(usuario.getRol()));
    }

    /**
     * Cierra la sesión actual.
     */
    private void cerrarSesion(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/login?logout=1");
    }

    /**
     * Devuelve la ruta inicial según el rol del usuario.
     */
    private String obtenerRutaInicioPorRol(String rol) {
        String rolNormalizado = rol == null ? "" : rol.trim().toUpperCase();

        switch (rolNormalizado) {
            case "ADMIN":
                return "/inicio";

            case "RECEPCIONISTA":
                return "/citas";

            case "DOCTOR":
                return "/agenda-medico";

            default:
                return "/login";
        }
    }

    /**
     * Normaliza texto evitando valores null.
     */
    private String texto(String value) {
        return value == null ? "" : value.trim();
    }
}