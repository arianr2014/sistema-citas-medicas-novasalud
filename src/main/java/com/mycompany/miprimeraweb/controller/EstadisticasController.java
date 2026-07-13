package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.CitaDAO;
import com.mycompany.miprimeraweb.dao.CitaDAOImpl;
import com.mycompany.miprimeraweb.util.AppLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/** Estadísticas clínicas y operativas. */
@WebServlet("/estadisticas")
public class EstadisticasController extends HttpServlet {
    private final CitaDAO citaDAO = new CitaDAOImpl();
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String inicio = texto(request.getParameter("fechaInicio"));
        String fin = texto(request.getParameter("fechaFin"));
        if (inicio.isBlank()) inicio = LocalDate.now().minusDays(6).toString();
        if (fin.isBlank()) fin = LocalDate.now().toString();
        try {
            request.setAttribute("fechaInicio", inicio);
            request.setAttribute("fechaFin", fin);
            request.setAttribute("topEspecialidades", citaDAO.topEspecialidadesPorRango(inicio, fin));
            request.setAttribute("resumenSemanal", citaDAO.resumenSemanalPorDia(inicio, fin));
            request.getRequestDispatcher("/WEB-INF/views/estadistica/index.jsp").forward(request, response);
        } catch (SQLException ex) {
            AppLogger.error(getClass(), "Error cargando estadísticas", ex);
            throw new ServletException("No se pudieron cargar las estadísticas.");
        }
    }
    private String texto(String v) { return v == null ? "" : v.trim(); }
}
