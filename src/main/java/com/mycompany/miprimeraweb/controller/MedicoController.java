package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.EspecialidadDAO;
import com.mycompany.miprimeraweb.dao.EspecialidadDAOImpl;
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

/**
 * Controlador del módulo Médicos.
 *
 * Fase 3:
 * - GET se usa solo para consultar, listar o abrir formularios.
 * - POST se usa para operaciones que modifican datos: guardar, actualizar y eliminar.
 */
@WebServlet("/medicos")
public class MedicoController extends HttpServlet {

    private final MedicoService medicoService = new MedicoService();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAOImpl();

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
             * /medicos?accion=eliminar&id=...
             * el sistema no elimina y redirige al listado.
             */
            case "eliminar":
                response.sendRedirect(request.getContextPath() + "/medicos?msg=metodo_invalido");
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
         * La eliminación de médicos ahora se procesa únicamente por POST.
         */
        if ("eliminar".equals(accion)) {
            eliminar(request, response);
            return;
        }

        /*
         * Si no llega accion=eliminar, se interpreta como registro o actualización
         * desde el formulario de médico.
         */
        guardarMedico(request, response);
    }

    /**
     * Lista médicos activos, con o sin filtro de búsqueda.
     */
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

    /**
     * Abre el formulario para crear un nuevo médico o editar uno existente.
     */
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

    /**
     * Guarda o actualiza un médico.
     */
    private void guardarMedico(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idTexto = request.getParameter("idMedico");
        String nombres = texto(request.getParameter("nombres"));
        String apellidos = texto(request.getParameter("apellidos"));
        String dni = texto(request.getParameter("dni"));
        String telefono = texto(request.getParameter("telefono"));
        String correo = texto(request.getParameter("correo"));
        String cmp = texto(request.getParameter("cmp"));
        String consultorio = texto(request.getParameter("consultorio"));
        int duracionCitaMinutos = parseEntero(request.getParameter("duracionCitaMinutos"));
        int toleranciaMinutos = parseEntero(request.getParameter("toleranciaMinutos"));
        int maxCitasDia = parseEntero(request.getParameter("maxCitasDia"));
        int idEspecialidad = parseEntero(request.getParameter("idEspecialidad"));

        Medico medico = new Medico();
        medico.setIdMedico(parseEntero(idTexto));
        medico.setNombres(nombres);
        medico.setApellidos(apellidos);
        medico.setDni(dni);
        medico.setTelefono(telefono);
        medico.setCorreo(correo);
        medico.setCmp(cmp);
        medico.setConsultorio(consultorio);
        medico.setDuracionCitaMinutos(duracionCitaMinutos);
        medico.setToleranciaMinutos(toleranciaMinutos);
        medico.setMaxCitasDia(maxCitasDia);
        medico.setIdEspecialidad(idEspecialidad);

        try {
            HttpSession session = request.getSession(false);
            String usuario = obtenerUsuarioSesion(session);

            medicoService.guardar(medico, usuario);
            response.sendRedirect(request.getContextPath() + "/medicos?msg=ok");

        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("medico", medico);
            cargarEspecialidades(request);
            request.getRequestDispatcher("/WEB-INF/views/medico/form.jsp").forward(request, response);

        } catch (SQLException ex) {
            request.setAttribute("error", "No se pudo guardar el médico. Verifique los datos e intente nuevamente.");
            request.setAttribute("medico", medico);
            cargarEspecialidades(request);
            request.getRequestDispatcher("/WEB-INF/views/medico/form.jsp").forward(request, response);
        }
    }

    /**
     * Elimina lógicamente un médico.
     * Esta acción debe recibirse únicamente mediante POST.
     */
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
            response.sendRedirect(request.getContextPath() + "/medicos?msg=errorDelete");
        }
    }

    /**
     * Carga especialidades activas para el formulario de médicos.
     */
    private void cargarEspecialidades(HttpServletRequest request) throws ServletException {
        try {
            List<Especialidad> especialidades = especialidadDAO.listar();
            request.setAttribute("especialidades", especialidades);

        } catch (SQLException ex) {
            throw new ServletException("No se pudo cargar la lista de especialidades", ex);
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