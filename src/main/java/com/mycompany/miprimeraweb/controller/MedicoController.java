package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.EspecialidadDAO;
import com.mycompany.miprimeraweb.model.Especialidad;
import com.mycompany.miprimeraweb.model.Medico;
import com.mycompany.miprimeraweb.service.MedicoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/medicos")
public class MedicoController extends HttpServlet {

    private final MedicoService medicoService = new MedicoService();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

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

        String idTexto = request.getParameter("idMedico");
        String nombres = texto(request.getParameter("nombres"));
        String apellidos = texto(request.getParameter("apellidos"));
        String telefono = texto(request.getParameter("telefono"));
        int idEspecialidad = parseEntero(request.getParameter("idEspecialidad"));

        Medico medico = new Medico();
        medico.setIdMedico(parseEntero(idTexto));
        medico.setNombres(nombres);
        medico.setApellidos(apellidos);
        medico.setTelefono(telefono);
        medico.setIdEspecialidad(idEspecialidad);

        try {
            HttpSession session = request.getSession(false);
            String usuario = session == null ? "sistema" : String.valueOf(session.getAttribute("usuarioLogeado"));
            medicoService.guardar(medico, usuario);
            response.sendRedirect(request.getContextPath() + "/medicos?msg=ok");
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("medico", medico);
            cargarEspecialidades(request);
            request.getRequestDispatcher("/WEB-INF/views/medico/form.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "No se pudo guardar el medico: " + ex.getMessage());
            request.setAttribute("medico", medico);
            cargarEspecialidades(request);
            request.getRequestDispatcher("/WEB-INF/views/medico/form.jsp").forward(request, response);
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String q = texto(request.getParameter("q"));
        try {
            List<Medico> medicos = medicoService.listar(q);
            request.setAttribute("medicos", medicos);
            request.setAttribute("q", q);
            request.getRequestDispatcher("/WEB-INF/views/medico/list.jsp").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException("Error al listar medicos", ex);
        }
    }

    private void abrirFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idMedico = parseEntero(request.getParameter("id"));

        if (idMedico > 0) {
            try {
                Medico medico = medicoService.obtenerPorId(idMedico);
                if (medico == null) {
                    response.sendRedirect(request.getContextPath() + "/medicos?msg=noexiste");
                    return;
                }
                request.setAttribute("medico", medico);
            } catch (SQLException ex) {
                throw new ServletException("Error al cargar medico", ex);
            }
        }

        cargarEspecialidades(request);
        request.getRequestDispatcher("/WEB-INF/views/medico/form.jsp").forward(request, response);
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        int idMedico = parseEntero(request.getParameter("id"));

        if (idMedico <= 0) {
            response.sendRedirect(request.getContextPath() + "/medicos?msg=invalid");
            return;
        }

        try {
            medicoService.eliminar(idMedico);
            response.sendRedirect(request.getContextPath() + "/medicos?msg=deleted");
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/medicos?msg=invalid");
        } catch (SQLException ex) {
            throw new ServletException("Error al eliminar medico", ex);
        }
    }

    private void cargarEspecialidades(HttpServletRequest request) throws ServletException {
        try {
            List<Especialidad> especialidades = especialidadDAO.listar();
            request.setAttribute("especialidades", especialidades);
        } catch (SQLException ex) {
            throw new ServletException("No se pudo cargar la lista de especialidades", ex);
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
