package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.EspecialidadDAO;
import com.mycompany.miprimeraweb.dao.MedicoDAO;
import com.mycompany.miprimeraweb.model.Cita;
import com.mycompany.miprimeraweb.model.Especialidad;
import com.mycompany.miprimeraweb.model.Medico;
import com.mycompany.miprimeraweb.service.CitaService;
import com.mycompany.miprimeraweb.util.AppLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Agenda médica.
 *
 * V3.2:
 * - Recepción/Admin pueden buscar por fecha, médico o especialidad.
 * - Doctor ve solo su agenda propia y registra la atención final.
 */
@WebServlet("/agenda-medico")
public class AgendaMedicoController extends HttpServlet {

    private final CitaService citaService = new CitaService();
    private final MedicoDAO medicoDAO = new MedicoDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarAgenda(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
         * Desde V3.2.1 el médico ya no marca una cita como ATENDIDA
         * directamente desde la tabla. Primero debe registrar síntomas,
         * diagnóstico, tratamiento y receta en /atencion-medica.
         */
        int idCita = parseEntero(request.getParameter("id"));
        if (idCita > 0) {
            response.sendRedirect(request.getContextPath() + "/atencion-medica?idCita=" + idCita);
        } else {
            response.sendRedirect(request.getContextPath() + "/agenda-medico?msg=invalid");
        }
    }

    private void cargarAgenda(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {

        String fecha = texto(request.getParameter("fecha"));
        int idMedico = parseEntero(request.getParameter("idMedico"));
        int idEspecialidad = parseEntero(request.getParameter("idEspecialidad"));
        boolean buscar = "1".equals(request.getParameter("buscar"));
        if (fecha.isBlank()) fecha = LocalDate.now().toString();

        try {
            HttpSession session = request.getSession(false);
            String rol = session == null ? "" : String.valueOf(session.getAttribute("rolUsuario"));
            boolean esDoctor = "DOCTOR".equalsIgnoreCase(rol);

            if (esDoctor) {
                idMedico = obtenerIdMedicoSesion(session);
                idEspecialidad = 0;
                buscar = true;
            }

            List<Medico> medicos = esDoctor ? new ArrayList<>() : medicoDAO.listar("");
            List<Especialidad> especialidades = esDoctor ? new ArrayList<>() : especialidadDAO.listar();
            List<Cita> citas;
            if (esDoctor && idMedico <= 0) {
                citas = new ArrayList<>();
                request.setAttribute("error", "Su usuario no está asociado a un médico. Contacte al administrador.");
            } else {
                citas = buscar ? citaService.listarAgendaProgramadaFlexible(idMedico, idEspecialidad, fecha) : new ArrayList<>();
            }

            if (esDoctor && idMedico > 0) {
                YearMonth ym = YearMonth.from(LocalDate.parse(fecha));
                List<String> diasMesAgenda = new ArrayList<>();
                for (int dia = 1; dia <= ym.lengthOfMonth(); dia++) {
                    diasMesAgenda.add(ym.atDay(dia).toString());
                }
                request.setAttribute("resumenMensual", citaService.resumenMensualPorMedico(idMedico, ym.atDay(1).toString(), ym.atEndOfMonth().toString()));
                request.setAttribute("diasMesAgenda", diasMesAgenda);
                request.setAttribute("mesAgenda", ym.toString());
            }

            request.setAttribute("medicos", medicos);
            request.setAttribute("especialidades", especialidades);
            request.setAttribute("citas", citas);
            request.setAttribute("idMedico", idMedico);
            request.setAttribute("idEspecialidad", idEspecialidad);
            request.setAttribute("fecha", fecha);
            request.setAttribute("buscar", buscar);
            request.setAttribute("modoDoctor", esDoctor);
            if (error != null) request.setAttribute("error", error);
            request.getRequestDispatcher("/WEB-INF/views/agenda/list.jsp").forward(request, response);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("medicos", new ArrayList<Medico>());
            request.setAttribute("especialidades", new ArrayList<Especialidad>());
            request.setAttribute("citas", new ArrayList<Cita>());
            request.setAttribute("idMedico", idMedico);
            request.setAttribute("idEspecialidad", idEspecialidad);
            request.setAttribute("fecha", fecha);
            request.setAttribute("buscar", buscar);
            request.getRequestDispatcher("/WEB-INF/views/agenda/list.jsp").forward(request, response);
        } catch (SQLException ex) {
            AppLogger.error(getClass(), "Error al consultar agenda médica", ex);
            throw new ServletException("Error al consultar agenda médica");
        }
    }

    private int obtenerIdMedicoSesion(HttpSession session) {
        if (session == null || session.getAttribute("idMedico") == null) return 0;
        try { return Integer.parseInt(String.valueOf(session.getAttribute("idMedico"))); } catch (NumberFormatException ex) { return 0; }
    }

    private int parseEntero(String value) { try { return Integer.parseInt(value); } catch (NumberFormatException ex) { return 0; } }
    private String texto(String value) { return value == null ? "" : value.trim(); }
}
