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

/**
 * Controlador del módulo Especialidades.
 *
 * Fase 3:
 * - GET se usa solo para consultar, listar o abrir formularios.
 * - POST se usa para operaciones que modifican datos: guardar, actualizar y eliminar.
 */
@WebServlet("/especialidades")
public class EspecialidadController extends HttpServlet {

    private final EspecialidadService especialidadService = new EspecialidadService();

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
             * /especialidades?accion=eliminar&id=...
             * el sistema no elimina y redirige al listado.
             */
            case "eliminar":
                response.sendRedirect(request.getContextPath() + "/especialidades?msg=metodo_invalido");
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
         * La eliminación de especialidades ahora se procesa únicamente por POST.
         */
        if ("eliminar".equals(accion)) {
            eliminar(request, response);
            return;
        }

        /*
         * Si no llega accion=eliminar, se interpreta como registro o actualización
         * desde el formulario de especialidad.
         */
        guardarEspecialidad(request, response);
    }

    /**
     * Lista especialidades activas, con o sin filtro de búsqueda.
     */
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

    /**
     * Abre el formulario para crear una nueva especialidad o editar una existente.
     */
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

    /**
     * Guarda o actualiza una especialidad.
     */
    private void guardarEspecialidad(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Especialidad especialidad = new Especialidad();
        especialidad.setIdEspecialidad(parseEntero(request.getParameter("idEspecialidad")));
        especialidad.setNombre(texto(request.getParameter("nombre")));
        especialidad.setDescripcion(texto(request.getParameter("descripcion")));

        try {
            HttpSession session = request.getSession(false);
            String usuario = obtenerUsuarioSesion(session);

            especialidadService.guardar(especialidad, usuario);
            response.sendRedirect(request.getContextPath() + "/especialidades?msg=ok");

        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("especialidad", especialidad);
            request.getRequestDispatcher("/WEB-INF/views/especialidad/form.jsp").forward(request, response);

        } catch (SQLException ex) {
            request.setAttribute("error", "No se pudo guardar la especialidad. Verifique los datos e intente nuevamente.");
            request.setAttribute("especialidad", especialidad);
            request.getRequestDispatcher("/WEB-INF/views/especialidad/form.jsp").forward(request, response);
        }
    }

    /**
     * Elimina lógicamente una especialidad.
     * Esta acción debe recibirse únicamente mediante POST.
     */
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