package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.MedicoDAO;
import com.mycompany.miprimeraweb.model.Cita;
import com.mycompany.miprimeraweb.model.Medico;
import com.mycompany.miprimeraweb.service.CitaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/agenda-medico")
public class AgendaMedicoController extends HttpServlet {

    private final CitaService citaService = new CitaService();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fecha = texto(request.getParameter("fecha"));
        int idMedico = parseEntero(request.getParameter("idMedico"));
        boolean buscar = "1".equals(request.getParameter("buscar"));

        if (fecha.isBlank()) {
            fecha = LocalDate.now().toString();
        }

        try {
            List<Medico> medicos = medicoDAO.listar("");
            List<Cita> citas = new ArrayList<>();

            if (buscar) {
                citas = citaService.listarAgendaProgramada(idMedico, fecha);
            }

            request.setAttribute("medicos", medicos);
            request.setAttribute("citas", citas);
            request.setAttribute("idMedico", idMedico);
            request.setAttribute("fecha", fecha);
            request.setAttribute("buscar", buscar);
            request.getRequestDispatcher("/WEB-INF/views/agenda/list.jsp").forward(request, response);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("error", ex.getMessage());
            recargarFormulario(request, idMedico, fecha, buscar);
            request.getRequestDispatcher("/WEB-INF/views/agenda/list.jsp").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException("Error al consultar agenda medica", ex);
        }
    }

    private void recargarFormulario(HttpServletRequest request, int idMedico, String fecha, boolean buscar)
            throws ServletException {
        try {
            request.setAttribute("medicos", medicoDAO.listar(""));
            request.setAttribute("citas", new ArrayList<Cita>());
            request.setAttribute("idMedico", idMedico);
            request.setAttribute("fecha", fecha);
            request.setAttribute("buscar", buscar);
        } catch (SQLException ex) {
            throw new ServletException("No se pudo preparar la pantalla de agenda", ex);
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
