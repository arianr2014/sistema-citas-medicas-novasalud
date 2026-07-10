package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.EspecialidadDAO;
import com.mycompany.miprimeraweb.dao.EspecialidadDAOImpl;
import com.mycompany.miprimeraweb.dao.HorarioDAO;
import com.mycompany.miprimeraweb.dao.HorarioDAOImpl;
import com.mycompany.miprimeraweb.dao.MedicoDAO;
import com.mycompany.miprimeraweb.dao.MedicoDAOImpl;
import com.mycompany.miprimeraweb.dao.PacienteDAO;
import com.mycompany.miprimeraweb.dao.PacienteDAOImpl;
import com.mycompany.miprimeraweb.model.Cita;
import com.mycompany.miprimeraweb.model.Especialidad;
import com.mycompany.miprimeraweb.model.Medico;
import com.mycompany.miprimeraweb.model.Paciente;
import com.mycompany.miprimeraweb.service.CitaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del módulo Citas.
 *
 * Fase 3:
 * - GET se usa solo para consultar, buscar, listar o abrir formularios.
 * - POST se usa para operaciones críticas que modifican datos:
 *   guardar, actualizar, atender y anular cita.
 */
@WebServlet("/citas")
public class CitaController extends HttpServlet {

    private final CitaService citaService = new CitaService();
    private final PacienteDAO pacienteDAO = new PacienteDAOImpl();
    private final MedicoDAO medicoDAO = new MedicoDAOImpl();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAOImpl();
    private final HorarioDAO horarioDAO = new HorarioDAOImpl();

    /**
     * Atiende navegación y consultas.
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
             * Atender y anular citas ya no se permiten por GET.
             * Si alguien intenta manipular la URL, no se ejecuta ningún cambio.
             */
            case "atender":
            case "eliminar":
                response.sendRedirect(request.getContextPath() + "/citas?msg=metodo_invalido");
                break;

            default:
                listar(request, response);
                break;
        }
    }

    /**
     * Atiende operaciones que modifican datos.
     * Aquí se procesan guardar/actualizar, atender y anular.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String accion = texto(request.getParameter("accion"));

        /*
         * Fase 3:
         * Marcar cita como atendida ahora se procesa únicamente por POST.
         */
        if ("atender".equals(accion)) {
            atender(request, response);
            return;
        }

        /*
         * Fase 3:
         * Anular cita ahora se procesa únicamente por POST.
         */
        if ("eliminar".equals(accion)) {
            eliminar(request, response);
            return;
        }

        /*
         * Si no llega una acción crítica específica, se interpreta como
         * registro o actualización desde el formulario de cita.
         */
        guardarCita(request, response);
    }

    /**
     * Lista citas según filtros seleccionados por el usuario.
     */
    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String dniPaciente = texto(request.getParameter("dniPaciente"));
        int idMedico = parseEntero(request.getParameter("idMedico"));
        int idEspecialidad = parseEntero(request.getParameter("idEspecialidad"));
        boolean buscar = "1".equals(request.getParameter("buscar"));

        try {
            List<Medico> medicos = medicoDAO.listar("");
            List<Especialidad> especialidades = especialidadDAO.listar();

            List<Cita> citas = buscar
                    ? citaService.listarFiltrado(dniPaciente, idMedico, idEspecialidad)
                    : new ArrayList<>();

            request.setAttribute("medicos", medicos);
            request.setAttribute("especialidades", especialidades);
            request.setAttribute("citas", citas);
            request.setAttribute("dniPaciente", dniPaciente);
            request.setAttribute("idMedico", idMedico);
            request.setAttribute("idEspecialidad", idEspecialidad);
            request.setAttribute("buscar", buscar);

            request.getRequestDispatcher("/WEB-INF/views/cita/list.jsp").forward(request, response);

        } catch (SQLException ex) {
            throw new ServletException("Error al listar citas", ex);
        }
    }

    /**
     * Abre el formulario para registrar o editar una cita.
     */
    private void abrirFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idCita = parseEntero(request.getParameter("id"));
        int idEspecialidadSel = parseEntero(request.getParameter("idEspecialidadSel"));
        int idMedicoSel = parseEntero(request.getParameter("idMedicoSel"));
        String dniBusqueda = texto(request.getParameter("dniBusqueda"));
        String fechaSel = texto(request.getParameter("fechaSel"));
        String horaSel = texto(request.getParameter("horaSel"));

        Cita cita = null;

        if (idCita > 0) {
            try {
                cita = citaService.obtenerPorId(idCita);

                if (cita == null) {
                    response.sendRedirect(request.getContextPath() + "/citas?msg=noexiste");
                    return;
                }

                if ("ATENDIDA".equalsIgnoreCase(texto(cita.getEstado()))) {
                    response.sendRedirect(request.getContextPath() + "/citas?msg=readonly");
                    return;
                }

                request.setAttribute("cita", cita);

            } catch (SQLException ex) {
                throw new ServletException("Error al cargar cita", ex);
            }
        }

        int idPaciente = cita == null ? 0 : cita.getIdPaciente();
        int idMedico = idMedicoSel > 0 ? idMedicoSel : (cita == null ? 0 : cita.getIdMedico());

        if (fechaSel.isBlank() && cita != null && cita.getFecha() != null) {
            fechaSel = cita.getFecha();
        }

        if (horaSel.isBlank() && cita != null && cita.getHora() != null) {
            horaSel = cita.getHora();
        }

        if (idEspecialidadSel <= 0 && idMedico > 0) {
            try {
                Medico medico = medicoDAO.obtenerPorId(idMedico);
                if (medico != null) {
                    idEspecialidadSel = medico.getIdEspecialidad();
                }
            } catch (SQLException ex) {
                throw new ServletException("No se pudo cargar especialidad del medico", ex);
            }
        }

        prepararFormulario(
                request,
                idPaciente,
                dniBusqueda,
                idEspecialidadSel,
                idMedico,
                fechaSel,
                horaSel,
                idCita
        );

        request.getRequestDispatcher("/WEB-INF/views/cita/form.jsp").forward(request, response);
    }

        /**
     * Guarda o actualiza una cita.
     *
     * Mejora Fase 5:
     * Se diferencia el mensaje de respuesta según la operación realizada:
     * - created: cita nueva registrada.
     * - updated: cita existente actualizada.
     */
    private void guardarCita(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Cita cita = new Cita();
        cita.setIdCita(parseEntero(request.getParameter("idCita")));
        cita.setIdPaciente(parseEntero(request.getParameter("idPaciente")));
        cita.setIdMedico(parseEntero(request.getParameter("idMedico")));
        cita.setFecha(texto(request.getParameter("fecha")));
        cita.setHora(texto(request.getParameter("hora")));
        cita.setEstado(texto(request.getParameter("estado")));
        cita.setObservaciones(texto(request.getParameter("observaciones")));

        int idEspecialidadSel = parseEntero(request.getParameter("idEspecialidadSel"));
        String dniBusqueda = texto(request.getParameter("dniBusqueda"));

        /*
         * Si la cita ya tiene ID, significa que estamos editando.
         * Si el ID es 0, significa que estamos registrando una nueva cita.
         */
        boolean esEdicion = cita.getIdCita() > 0;

        try {
            HttpSession session = request.getSession(false);
            String usuario = obtenerUsuarioSesion(session);

            citaService.guardar(cita, usuario);

            if (esEdicion) {
                response.sendRedirect(request.getContextPath() + "/citas?msg=updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/citas?msg=created");
            }

        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("cita", cita);

            prepararFormulario(
                    request,
                    cita.getIdPaciente(),
                    dniBusqueda,
                    idEspecialidadSel,
                    cita.getIdMedico(),
                    cita.getFecha(),
                    cita.getHora(),
                    cita.getIdCita()
            );

            request.getRequestDispatcher("/WEB-INF/views/cita/form.jsp").forward(request, response);

        } catch (SQLException ex) {
            request.setAttribute("error", "No se pudo guardar la cita: " + ex.getMessage());
            request.setAttribute("cita", cita);

            prepararFormulario(
                    request,
                    cita.getIdPaciente(),
                    dniBusqueda,
                    idEspecialidadSel,
                    cita.getIdMedico(),
                    cita.getFecha(),
                    cita.getHora(),
                    cita.getIdCita()
            );

            request.getRequestDispatcher("/WEB-INF/views/cita/form.jsp").forward(request, response);
        }
    }

    /**
     * Anula lógicamente una cita.
     * Esta acción debe recibirse únicamente mediante POST.
     */
    private void eliminar(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        int idCita = parseEntero(request.getParameter("id"));

        if (idCita <= 0) {
            response.sendRedirect(request.getContextPath() + "/citas?msg=invalid");
            return;
        }

        try {
            citaService.eliminar(idCita);
            response.sendRedirect(request.getContextPath() + "/citas?msg=deleted");

        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/citas?msg=invalid");

        } catch (SQLException ex) {
            response.sendRedirect(request.getContextPath() + "/citas?msg=errorDelete");
        }
    }

    /**
     * Marca una cita como ATENDIDA.
     * Esta acción debe recibirse únicamente mediante POST.
     */
    private void atender(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        int idCita = parseEntero(request.getParameter("id"));

        if (idCita <= 0) {
            response.sendRedirect(request.getContextPath() + "/citas?msg=invalid");
            return;
        }

        try {
            boolean actualizada = citaService.marcarComoAtendida(idCita);

            if (actualizada) {
                response.sendRedirect(request.getContextPath() + "/citas?msg=attended");
            } else {
                response.sendRedirect(request.getContextPath() + "/citas?msg=nochange");
            }

        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/citas?msg=invalid");

        } catch (SQLException ex) {
            response.sendRedirect(request.getContextPath() + "/citas?msg=errorUpdate");
        }
    }

    /**
     * Prepara catálogos y datos necesarios para el formulario de citas.
     */
    private void prepararFormulario(
            HttpServletRequest request,
            int idPaciente,
            String dniBusqueda,
            int idEspecialidadSel,
            int idMedicoSel,
            String fechaSel,
            String horaSel,
            int idCitaActual
    ) throws ServletException {

        try {
            Paciente pacienteEncontrado = null;

            if (!dniBusqueda.isBlank()) {
                pacienteEncontrado = pacienteDAO.obtenerPorDni(dniBusqueda);
            }

            if (pacienteEncontrado == null && idPaciente > 0) {
                pacienteEncontrado = pacienteDAO.obtenerPorId(idPaciente);
            }

            List<Especialidad> especialidades = especialidadDAO.listar();

            List<Medico> medicos = idEspecialidadSel > 0
                    ? medicoDAO.listarPorEspecialidad(idEspecialidadSel)
                    : new ArrayList<>();

            List<String> horariosDisponibles = (idMedicoSel > 0 && !fechaSel.isBlank())
                    ? horarioDAO.listarHorasDisponibles(idMedicoSel, fechaSel, idCitaActual)
                    : new ArrayList<>();

            request.setAttribute("pacienteEncontrado", pacienteEncontrado);
            request.setAttribute("dniBusqueda", dniBusqueda);
            request.setAttribute("especialidades", especialidades);
            request.setAttribute("medicos", medicos);
            request.setAttribute("horariosDisponibles", horariosDisponibles);
            request.setAttribute("idEspecialidadSel", idEspecialidadSel);
            request.setAttribute("idMedicoSel", idMedicoSel);
            request.setAttribute("fechaSel", fechaSel);
            request.setAttribute("horaSel", horaSel);

        } catch (SQLException ex) {
            throw new ServletException("No se pudo preparar el formulario de citas", ex);
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