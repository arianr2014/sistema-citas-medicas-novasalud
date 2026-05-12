package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.MedicoDAO;
import com.mycompany.miprimeraweb.model.Horario;
import com.mycompany.miprimeraweb.model.Medico;
import com.mycompany.miprimeraweb.service.HorarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/horarios")
public class HorarioController extends HttpServlet {

    private final HorarioService horarioService = new HorarioService();
    private final MedicoDAO medicoDAO = new MedicoDAO();

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

        Horario horario = new Horario();
        horario.setIdHorario(parseEntero(request.getParameter("idHorario")));
        horario.setIdMedico(parseEntero(request.getParameter("idMedico")));
        horario.setDia(texto(request.getParameter("dia")));
        horario.setHoraInicio(texto(request.getParameter("horaInicio")));
        horario.setHoraFin(texto(request.getParameter("horaFin")));

        try {
            HttpSession session = request.getSession(false);
            String usuario = session == null ? "sistema" : String.valueOf(session.getAttribute("usuarioLogeado"));
            horarioService.guardar(horario, usuario);
            response.sendRedirect(request.getContextPath() + "/horarios?msg=ok");
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("horario", horario);
            cargarMedicos(request);
            request.getRequestDispatcher("/WEB-INF/views/horario/form.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "No se pudo guardar el horario: " + ex.getMessage());
            request.setAttribute("horario", horario);
            cargarMedicos(request);
            request.getRequestDispatcher("/WEB-INF/views/horario/form.jsp").forward(request, response);
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String q = texto(request.getParameter("q"));
        try {
            List<Horario> horarios = horarioService.listar(q);
            request.setAttribute("horarios", horarios);
            request.setAttribute("q", q);
            request.getRequestDispatcher("/WEB-INF/views/horario/list.jsp").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException("Error al listar horarios", ex);
        }
    }

    private void abrirFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int idHorario = parseEntero(request.getParameter("id"));

        if (idHorario > 0) {
            try {
                Horario horario = horarioService.obtenerPorId(idHorario);
                if (horario == null) {
                    response.sendRedirect(request.getContextPath() + "/horarios?msg=noexiste");
                    return;
                }
                request.setAttribute("horario", horario);
            } catch (SQLException ex) {
                throw new ServletException("Error al cargar horario", ex);
            }
        }

        cargarMedicos(request);
        request.getRequestDispatcher("/WEB-INF/views/horario/form.jsp").forward(request, response);
    }

    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        int idHorario = parseEntero(request.getParameter("id"));

        if (idHorario <= 0) {
            response.sendRedirect(request.getContextPath() + "/horarios?msg=invalid");
            return;
        }

        try {
            horarioService.eliminar(idHorario);
            response.sendRedirect(request.getContextPath() + "/horarios?msg=deleted");
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/horarios?msg=invalid");
        } catch (SQLException ex) {
            throw new ServletException("Error al eliminar horario", ex);
        }
    }

    private void cargarMedicos(HttpServletRequest request) throws ServletException {
        try {
            List<Medico> medicos = medicoDAO.listar("");
            request.setAttribute("medicos", medicos);
        } catch (SQLException ex) {
            throw new ServletException("No se pudo cargar la lista de medicos", ex);
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
