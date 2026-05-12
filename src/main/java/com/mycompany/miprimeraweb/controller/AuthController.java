package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.UsuarioDAO;
import com.mycompany.miprimeraweb.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet({"/login", "/logout"})
public class AuthController extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        if ("/logout".equals(servletPath)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login?logout=1");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioLogeado") != null) {
            String rol = (String) session.getAttribute("rolUsuario");
            response.sendRedirect(request.getContextPath() + obtenerRutaInicioPorRol(rol));
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
                HttpSession session = request.getSession(true);
                session.setAttribute("usuarioLogeado", usuario.getUsername());
                session.setAttribute("rolUsuario", usuario.getRol());
                session.setAttribute("idUsuario", usuario.getIdUsuario());
                response.sendRedirect(request.getContextPath() + obtenerRutaInicioPorRol(usuario.getRol()));
                return;
            }
        } catch (SQLException ex) {
            request.setAttribute("error", "Error de base de datos: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        request.setAttribute("error", "Credenciales invalidas.");
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    private String texto(String value) {
        return value == null ? "" : value.trim();
    }

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
}
