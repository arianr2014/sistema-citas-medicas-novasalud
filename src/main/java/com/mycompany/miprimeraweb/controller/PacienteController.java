package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.model.Paciente;
import com.mycompany.miprimeraweb.service.PacienteService;
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
 * Controlador del módulo Pacientes.
 *
 * Fase 3:
 * - GET se usa solo para consultar o mostrar pantallas.
 * - POST se usa para operaciones que modifican datos: guardar, actualizar y eliminar.
 */
@WebServlet("/pacientes")
public class PacienteController extends HttpServlet {

    private final PacienteService pacienteService = new PacienteService();

    /**
     * Atiende solicitudes de consulta/navegación.
     * No debe modificar datos.
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
             * Si alguien intenta usar /pacientes?accion=eliminar&id=...
             * lo redirigimos al listado sin ejecutar eliminación.
             */
            case "eliminar":
                response.sendRedirect(request.getContextPath() + "/pacientes?msg=metodo_invalido");
                break;

            default:
                listar(request, response);
                break;
        }
    }

    /**
     * Atiende solicitudes que modifican datos.
     * Aquí se procesan guardar/actualizar y eliminar.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String accion = texto(request.getParameter("accion"));

        /*
         * Fase 3:
         * Eliminar ahora se procesa por POST.
         */
        if ("eliminar".equals(accion)) {
            eliminar(request, response);
            return;
        }

        /*
         * Si no llega accion=eliminar, asumimos que el POST corresponde
         * al formulario de registro o actualización de paciente.
         */
        guardarPaciente(request, response);
    }

    /**
     * Lista pacientes activos, con o sin filtro de búsqueda.
     */
    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = texto(request.getParameter("q"));

        try {
            List<Paciente> pacientes = pacienteService.listar(q);
            request.setAttribute("pacientes", pacientes);
            request.setAttribute("q", q);
            request.getRequestDispatcher("/WEB-INF/views/paciente/list.jsp").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException("Error al listar pacientes", ex);
        }
    }

    /**
     * Abre el formulario para crear o editar paciente.
     */
    private void abrirFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idPaciente = parseEntero(request.getParameter("id"));

        if (idPaciente > 0) {
            try {
                Paciente paciente = pacienteService.obtenerPorId(idPaciente);

                if (paciente == null) {
                    response.sendRedirect(request.getContextPath() + "/pacientes?msg=noexiste");
                    return;
                }

                request.setAttribute("paciente", paciente);

            } catch (SQLException ex) {
                throw new ServletException("Error al cargar paciente", ex);
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/paciente/form.jsp").forward(request, response);
    }

    /**
     * Guarda o actualiza un paciente.
     */
    private void guardarPaciente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idTexto = request.getParameter("idPaciente");
        String dni = texto(request.getParameter("dni"));
        String nombres = texto(request.getParameter("nombres"));
        String apellidos = texto(request.getParameter("apellidos"));
        String telefono = texto(request.getParameter("telefono"));
        String direccion = texto(request.getParameter("direccion"));

        Paciente paciente = new Paciente();
        paciente.setIdPaciente(parseEntero(idTexto));
        paciente.setDni(dni);
        paciente.setNombres(nombres);
        paciente.setApellidos(apellidos);
        paciente.setTelefono(telefono);
        paciente.setDireccion(direccion);

        try {
            HttpSession session = request.getSession(false);
            String usuario = session == null ? "sistema" : String.valueOf(session.getAttribute("usuarioLogeado"));

            pacienteService.guardar(paciente, usuario);
            response.sendRedirect(request.getContextPath() + "/pacientes?msg=ok");

        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("paciente", paciente);
            request.getRequestDispatcher("/WEB-INF/views/paciente/form.jsp").forward(request, response);

        } catch (SQLException ex) {
            request.setAttribute("error", "No se pudo guardar el paciente: " + ex.getMessage());
            request.setAttribute("paciente", paciente);
            request.getRequestDispatcher("/WEB-INF/views/paciente/form.jsp").forward(request, response);
        }
    }

    /**
     * Elimina lógicamente un paciente.
     * Esta acción debe llegar únicamente por POST.
     */
    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        int idPaciente = parseEntero(request.getParameter("id"));

        if (idPaciente <= 0) {
            response.sendRedirect(request.getContextPath() + "/pacientes?msg=invalid");
            return;
        }

        try {
            pacienteService.eliminar(idPaciente);
            response.sendRedirect(request.getContextPath() + "/pacientes?msg=deleted");

        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/pacientes?msg=invalid");

        } catch (SQLException ex) {
            throw new ServletException("Error al eliminar paciente", ex);
        }
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