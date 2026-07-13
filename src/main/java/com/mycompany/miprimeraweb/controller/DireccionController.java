package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.CitaDAO;
import com.mycompany.miprimeraweb.dao.CitaDAOImpl;
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

/** Dashboard ejecutivo para Dirección. */
@WebServlet("/direccion")
public class DireccionController extends HttpServlet {
    private final CitaDAO citaDAO = new CitaDAOImpl();
    private final PagoService pagoService = new PagoService();
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LocalDate hoy = LocalDate.now();
        String inicioMes = hoy.withDayOfMonth(1).toString();
        try {
            request.setAttribute("fechaHoy", hoy.toString());
            request.setAttribute("citasHoy", citaDAO.contarCitasHoyPorEstado(hoy.toString()));
            request.setAttribute("ingresosMes", pagoService.totalIngresos(inicioMes, hoy.toString()));
            request.setAttribute("topEspecialidades", citaDAO.topEspecialidadesPorRango(inicioMes, hoy.toString()));
            request.setAttribute("ingresosEspecialidad", pagoService.ingresosPorEspecialidad(inicioMes, hoy.toString()));
            request.getRequestDispatcher("/WEB-INF/views/direccion/index.jsp").forward(request, response);
        } catch (SQLException ex) {
            AppLogger.error(getClass(), "Error cargando dirección", ex);
            throw new ServletException("No se pudo cargar el dashboard de dirección.");
        }
    }
}
