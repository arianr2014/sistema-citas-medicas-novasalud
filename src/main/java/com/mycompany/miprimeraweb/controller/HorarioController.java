package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.MedicoDAO;
import com.mycompany.miprimeraweb.dao.MedicoDAOImpl;
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

/**
 * Controlador del módulo Horarios.
 *
 * Fase 3:
 * - GET se usa solo para consultar, listar o abrir formularios.
 * - POST se usa para operaciones que modifican datos: guardar, actualizar y eliminar.
 */
@WebServlet("/horarios")
public class HorarioController extends HttpServlet {

    private final HorarioService horarioService = new HorarioService();
    private final MedicoDAO medicoDAO = new MedicoDAOImpl();

    /**
     * Atiende solicitudes de navegación y consulta.
     * No debe modificar datos en la base de datos.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = texto(request.getParameter("accion"));

        if (accion.isBlank() || "listar".equals(accion)) {
            listar(request, response);
            return;
        }

        switch (accion) {
            case "form":
            case "editar":
                abrirFormulario(request, response);
                break;

            /*
             * Seguridad Fase 3:
             * La eliminación ya no se permite por GET.
             * Si alguien intenta usar:
             * /horarios?accion=eliminar&id=...
             * el sistema no elimina y redirige al listado.
             */
            case "eliminar":
                response.sendRedirect(request.getContextPath() + "/horarios?msg=metodo_invalido");
                break;

            default:
                listar(request, response);
                break;
        }
    }

    /**
     * Atiende operaciones que modifican información.
     * Aquí se procesan guardar/actualizar y eliminar.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String accion = texto(request.getParameter("accion"));

        /*
         * Fase 3:
         * La eliminación de horarios ahora se procesa únicamente por POST.
         */
        if ("eliminar".equals(accion)) {
            eliminar(request, response);
            return;
        }

        /*
         * Si no llega accion=eliminar, se interpreta como registro o actualización
         * desde el formulario de horario.
         */
        guardarHorario(request, response);
    }

    /**
     * Lista horarios activos, con o sin filtro de búsqueda.
     */
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

    /**
     * Abre el formulario para crear un nuevo horario o editar uno existente.
     */
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

    /**
     * Guarda o actualiza un horario.
     */
    private void guardarHorario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Horario horario = new Horario();
        horario.setIdHorario(parseEntero(request.getParameter("idHorario")));
        horario.setIdMedico(parseEntero(request.getParameter("idMedico")));
        horario.setDia(texto(request.getParameter("dia")));
        horario.setHoraInicio(texto(request.getParameter("horaInicio")));
        horario.setHoraFin(texto(request.getParameter("horaFin")));

        try {
            HttpSession session = request.getSession(false);
            String usuario = obtenerUsuarioSesion(session);

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

    /**
     * Elimina lógicamente un horario.
     * Esta acción debe recibirse únicamente mediante POST.
     */
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
            response.sendRedirect(request.getContextPath() + "/horarios?msg=errorDelete");
        }
    }

    /**
     * Carga médicos activos para el formulario de horarios.
     */
    private void cargarMedicos(HttpServletRequest request) throws ServletException {
        try {
            List<Medico> medicos = medicoDAO.listar("");
            request.setAttribute("medicos", medicos);

        } catch (SQLException ex) {
            throw new ServletException("No se pudo cargar la lista de medicos", ex);
        }
    }

    /**
     * Obtiene el usuario autenticado para registrar auditoría básica.
     */
    private String obtenerUsuarioSesion(HttpSession session) {
        if (session == null || session.getAttribute("usuarioLogeado") == null) {
            return "sistema";
        }

        return String.valueOf(session.getAttribute("usuarioLogeado"));
    }

    /**
     * Convierte texto a entero de forma segura.
     */
    private int parseEntero(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * Normaliza texto evitando valores null.
     */
    private String texto(String value) {
        return value == null ? "" : value.trim();
    }
}