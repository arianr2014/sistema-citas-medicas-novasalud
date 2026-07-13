package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.model.AtencionMedica;
import com.mycompany.miprimeraweb.model.Cita;
import com.mycompany.miprimeraweb.service.AtencionMedicaService;
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

/**
 * Permite al médico registrar y editar la atención clínica de sus propias citas.
 * El doctor solo puede acceder a citas vinculadas a su id_medico de sesión.
 */
@WebServlet("/atencion-medica")
public class AtencionMedicaController extends HttpServlet {

    private final CitaService citaService = new CitaService();
    private final AtencionMedicaService atencionService = new AtencionMedicaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarFormulario(request, response, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        int idMedicoSesion = obtenerIdMedicoSesion(session);
        int idCita = parseEntero(request.getParameter("idCita"));
        String accion = texto(request.getParameter("accion"));

        try {
            validarDoctor(session);
            AtencionMedica atencion = leerAtencion(request);
            if ("guardar_atender".equals(accion)) {
                atencionService.guardarYMarcarAtendida(atencion, idMedicoSesion);
                response.sendRedirect(request.getContextPath() + "/agenda-medico?buscar=1&fecha=" + texto(request.getParameter("fecha")) + "&msg=attended");
            } else {
                atencionService.guardar(atencion, idMedicoSesion);
                response.sendRedirect(request.getContextPath() + "/atencion-medica?idCita=" + idCita + "&msg=saved");
            }
        } catch (IllegalStateException ex) {
            cargarFormulario(request, response, ex.getMessage(), leerAtencion(request));
        } catch (IllegalArgumentException ex) {
            cargarFormulario(request, response, ex.getMessage(), leerAtencion(request));
        } catch (SQLException ex) {
            AppLogger.error(getClass(), "Error al guardar atención médica", ex);
            cargarFormulario(request, response, "No se pudo guardar la atención médica. Intente nuevamente.", leerAtencion(request));
        }
    }

    private void cargarFormulario(HttpServletRequest request, HttpServletResponse response, String error, AtencionMedica atencionTemporal)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        int idCita = parseEntero(request.getParameter("idCita"));
        int idMedicoSesion = obtenerIdMedicoSesion(session);
        try {
            validarDoctor(session);
            Cita cita = citaService.obtenerPorId(idCita);
            if (cita == null || cita.getIdMedico() != idMedicoSesion) {
                response.sendRedirect(request.getContextPath() + "/acceso-denegado?from=atencion-medica");
                return;
            }

            AtencionMedica atencion = atencionTemporal != null ? atencionTemporal : atencionService.obtenerPorCita(idCita);
            if (atencion == null) {
                atencion = new AtencionMedica();
                atencion.setIdCita(cita.getIdCita());
                atencion.setIdMedico(cita.getIdMedico());
                atencion.setIdPaciente(cita.getIdPaciente());
                atencion.setCodigoHistoria(cita.getHistoriaClinicaCodigo());
            }

            request.setAttribute("cita", cita);
            request.setAttribute("atencion", atencion);
            if (error != null) request.setAttribute("error", error);
            request.getRequestDispatcher("/WEB-INF/views/atencion/form.jsp").forward(request, response);
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/acceso-denegado?from=atencion-medica");
        } catch (SQLException ex) {
            AppLogger.error(getClass(), "Error al cargar atención médica", ex);
            throw new ServletException("Error al cargar atención médica");
        }
    }

    private AtencionMedica leerAtencion(HttpServletRequest request) {
        AtencionMedica atencion = new AtencionMedica();
        atencion.setIdCita(parseEntero(request.getParameter("idCita")));
        atencion.setMotivoConsulta(texto(request.getParameter("motivoConsulta")));
        atencion.setSintomas(texto(request.getParameter("sintomas")));
        atencion.setDiagnostico(texto(request.getParameter("diagnostico")));
        atencion.setTratamiento(texto(request.getParameter("tratamiento")));
        atencion.setRecetaMedica(texto(request.getParameter("recetaMedica")));
        atencion.setIndicaciones(texto(request.getParameter("indicaciones")));
        atencion.setCodigoHistoria(texto(request.getParameter("codigoHistoria")));
        return atencion;
    }

    private void validarDoctor(HttpSession session) {
        if (session == null || !"DOCTOR".equalsIgnoreCase(String.valueOf(session.getAttribute("rolUsuario")))) {
            throw new IllegalArgumentException("Solo el médico responsable puede registrar la atención clínica.");
        }
        if (obtenerIdMedicoSesion(session) <= 0) {
            throw new IllegalArgumentException("Su usuario no está vinculado a un médico real.");
        }
    }

    private int obtenerIdMedicoSesion(HttpSession session) {
        if (session == null || session.getAttribute("idMedico") == null) return 0;
        try { return Integer.parseInt(String.valueOf(session.getAttribute("idMedico"))); } catch (NumberFormatException ex) { return 0; }
    }

    private int parseEntero(String value) { try { return Integer.parseInt(value); } catch (Exception ex) { return 0; } }
    private String texto(String value) { return value == null ? "" : value.trim(); }
}
