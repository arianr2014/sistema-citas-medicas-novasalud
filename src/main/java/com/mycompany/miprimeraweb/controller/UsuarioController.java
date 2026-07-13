package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.MedicoDAO;
import com.mycompany.miprimeraweb.dao.MedicoDAOImpl;
import com.mycompany.miprimeraweb.model.Medico;
import com.mycompany.miprimeraweb.model.Usuario;
import com.mycompany.miprimeraweb.service.UsuarioService;
import com.mycompany.miprimeraweb.util.AppLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** Controlador del módulo Usuarios. Solo ADMIN según AuthFilter. */
@WebServlet("/usuarios")
public class UsuarioController extends HttpServlet {
    private final UsuarioService service = new UsuarioService();
    private final MedicoDAO medicoDAO = new MedicoDAOImpl();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = texto(request.getParameter("accion"));
        try {
            if ("form".equals(accion) || "editar".equals(accion)) { abrirFormulario(request, response); return; }
            listar(request, response);
        } catch (SQLException ex) {
            AppLogger.error(getClass(), "Error en módulo usuarios", ex);
            throw new ServletException("No se pudo procesar el módulo usuarios.");
        }
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = texto(request.getParameter("accion"));
        try {
            if ("desactivar".equals(accion)) { service.cambiarEstado(parseInt(request.getParameter("id")), "INACTIVO", usuarioSesion(request)); response.sendRedirect(request.getContextPath()+"/usuarios?msg=deactivated"); return; }
            if ("activar".equals(accion)) { service.cambiarEstado(parseInt(request.getParameter("id")), "ACTIVO", usuarioSesion(request)); response.sendRedirect(request.getContextPath()+"/usuarios?msg=activated"); return; }
            if ("reset".equals(accion)) { service.resetearPassword(parseInt(request.getParameter("id")), texto(request.getParameter("passwordNuevo")), usuarioSesion(request)); response.sendRedirect(request.getContextPath()+"/usuarios?msg=reset"); return; }
            guardar(request, response);
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath()+"/usuarios?msg=invalid");
        } catch (SQLException ex) {
            AppLogger.error(getClass(), "Error guardando usuario", ex);
            response.sendRedirect(request.getContextPath()+"/usuarios?msg=error");
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        String filtro = texto(request.getParameter("q"));
        request.setAttribute("usuarios", service.listar(filtro));
        request.setAttribute("q", filtro);
        request.getRequestDispatcher("/WEB-INF/views/usuario/list.jsp").forward(request, response);
    }

    private void abrirFormulario(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = parseInt(request.getParameter("id"));
        if (id > 0) request.setAttribute("usuario", service.obtenerPorId(id));
        List<Medico> medicos = medicoDAO.listar("");
        request.setAttribute("medicos", medicos);
        request.getRequestDispatcher("/WEB-INF/views/usuario/form.jsp").forward(request, response);
    }

    private void guardar(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        Usuario u = new Usuario();
        u.setIdUsuario(parseInt(request.getParameter("idUsuario")));
        u.setUsername(texto(request.getParameter("username")));
        u.setDni(texto(request.getParameter("dni")));
        u.setNombres(texto(request.getParameter("nombres")));
        u.setApellidos(texto(request.getParameter("apellidos")));
        u.setTelefono(texto(request.getParameter("telefono")));
        u.setCorreo(texto(request.getParameter("correo")));
        u.setCargo(texto(request.getParameter("cargo")));
        u.setRol(texto(request.getParameter("rol")));
        u.setEstadoRegistro(texto(request.getParameter("estadoRegistro")));
        int idMedico = parseInt(request.getParameter("idMedico"));
        u.setIdMedico(idMedico <= 0 ? null : idMedico);
        service.guardar(u, texto(request.getParameter("passwordInicial")), usuarioSesion(request));
        response.sendRedirect(request.getContextPath()+"/usuarios?msg=saved");
    }

    private String usuarioSesion(HttpServletRequest request) { HttpSession s = request.getSession(false); return s == null ? "sistema" : String.valueOf(s.getAttribute("usuarioLogeado")); }
    private String texto(String v) { return v == null ? "" : v.trim(); }
    private int parseInt(String v) { try { return Integer.parseInt(v); } catch(Exception e) { return 0; } }
}
