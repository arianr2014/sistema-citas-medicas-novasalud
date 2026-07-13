package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.service.PagoService;
import com.mycompany.miprimeraweb.util.AppLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/** Reportes de ingresos por especialidad y método de pago. */
@WebServlet("/reportes-financieros")
public class ReporteFinancieroController extends HttpServlet {
    private final PagoService service = new PagoService();
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String inicio = texto(request.getParameter("fechaInicio"));
        String fin = texto(request.getParameter("fechaFin"));
        if (inicio.isBlank()) inicio = LocalDate.now().withDayOfMonth(1).toString();
        if (fin.isBlank()) fin = LocalDate.now().toString();
        try {
            request.setAttribute("fechaInicio", inicio);
            request.setAttribute("fechaFin", fin);
            request.setAttribute("totalIngresos", service.totalIngresos(inicio, fin));
            request.setAttribute("porEspecialidad", service.ingresosPorEspecialidad(inicio, fin));
            request.setAttribute("porMetodo", service.ingresosPorMetodoPago(inicio, fin));
            request.getRequestDispatcher("/WEB-INF/views/reporte/financiero.jsp").forward(request, response);
        } catch (SQLException ex) {
            AppLogger.error(getClass(), "Error cargando reportes financieros", ex);
            throw new ServletException("No se pudieron cargar los reportes financieros.");
        }
    }
    private String texto(String v) { return v == null ? "" : v.trim(); }
}
