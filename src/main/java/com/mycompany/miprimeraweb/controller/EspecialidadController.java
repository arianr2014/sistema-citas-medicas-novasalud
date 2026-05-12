package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.model.Especialidad;
import com.mycompany.miprimeraweb.service.EspecialidadService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/especialidades")
public class EspecialidadController extends HttpServlet {

    private final EspecialidadService especialidadService = new EspecialidadService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion == null || accion.isBlank() || "listar".equals(accion)) {
            listar(request, response);
            return;
        }

        switch (accion) {
            case "form":
                abrirFormulario(request, response);
                break;
            case "editar":
                abrirFormulario(request, response);
                break;
            case "eliminar":
                eliminar(request, response);
                break;
            default:
                listar(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Especialidad especialidad = new Especialidad();
        especialidad.setIdEspecialidad(parseEntero(request.getParameter("idEspecialidad")));
        especialidad.setNombre(texto(request.getParameter("nombre")));
        especialidad.setDescripcion(texto(request.getParameter("descripcion")));

        try {
            HttpSession session = request.getSession(false);
            String usuario = session == null ? "sistema" : String.valueOf(session.getAttribute("usuarioLogeado"));
            especialidadService.guardar(especialidad, usuario);
            response.sendRedirect(request.getContextPath() + "/especialidades?msg=ok");
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("especialidad", especialidad);
            request.getRequestDispatcher("/WEB-INF/views/especialidad/form.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "No se pudo guardar la especialidad: " + ex.getMessage());
            request.setAttribute("especialidad", especialidad);
            request.getRequestDispatcher("/WEB-INF/views/especialidad/form.jsp").forward(request, response);
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String q = texto(request.getParameter("q"));
        try {
            List<Especialidad> especialidades = especialidadService.listar(q);
            request.setAttribute("especialidades", especialidades);
            request.setAttribute("q", q);
            request.getRequestDispatcher("/WEB-INF/views/especialidad/list.jsp").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException("Error al listar especialidades", ex);
        }
    }

    private void abrirFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idEspecialidad = parseEntero(request.getParameter("id"));

        if (idEspecialidad > 0) {
            try {
                Especialidad especialidad = especialidadService.obtenerPorId(idEspecialidad);
                if (especialidad == null) {
                    response.sendRedirect(request.getContextPath() + "/especialidades?msg=noexiste");
                    return;
                }
                request.setAttribute("especialidad", especialidad);
            } catch (SQLException ex) {
                throw new ServletException("Error al cargar especialidad", ex);
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/especialidad/form.jsp").forward(request, response);
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        int idEspecialidad = parseEntero(request.getParameter("id"));

        if (idEspecialidad <= 0) {
            response.sendRedirect(request.getContextPath() + "/especialidades?msg=invalid");
            return;
        }

        try {
            especialidadService.eliminar(idEspecialidad);
            response.sendRedirect(request.getContextPath() + "/especialidades?msg=deleted");
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/especialidades?msg=invalid");
        } catch (SQLException ex) {
            response.sendRedirect(request.getContextPath() + "/especialidades?msg=errorDelete");
        }
    }

    private int parseEntero(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String texto(String value) {
        return value == null ? "" : value.trim();
    }
}
