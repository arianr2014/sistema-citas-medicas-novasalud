package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.CitaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/inicio")
public class HomeController extends HttpServlet {

    private final CitaDAO citaDAO = new CitaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.minusDays(6);

        try {
            Map<String, Integer> citasPorEstadoDb = citaDAO.contarCitasHoyPorEstado(hoy.toString());
            Map<String, Integer> citasPorEstadoHoy = new LinkedHashMap<>();
            citasPorEstadoHoy.put("PROGRAMADA", 0);
            citasPorEstadoHoy.put("ATENDIDA", 0);
            citasPorEstadoHoy.put("CANCELADA", 0);
            citasPorEstadoHoy.put("NO ASISTIO", 0);

            for (Map.Entry<String, Integer> entry : citasPorEstadoDb.entrySet()) {
                citasPorEstadoHoy.put(entry.getKey(), entry.getValue());
            }

            int medicosTurnoHoy = citaDAO.contarMedicosEnTurnoHoy(hoy.toString());

            Map<String, Integer> especialidadesDemanda = citaDAO.topEspecialidadesPorRango(inicioSemana.toString(), hoy.toString());
            List<Map.Entry<String, Integer>> topEspecialidades = new ArrayList<>(especialidadesDemanda.entrySet());
            if (topEspecialidades.size() > 3) {
                topEspecialidades = topEspecialidades.subList(0, 3);
            }

            Map<String, Integer> resumenSemanalDb = citaDAO.resumenSemanalPorDia(inicioSemana.toString(), hoy.toString());
            Map<String, Integer> resumenSemanal = new LinkedHashMap<>();
            for (int i = 0; i < 7; i++) {
                LocalDate fecha = inicioSemana.plusDays(i);
                String clave = fecha.toString();
                resumenSemanal.put(clave, resumenSemanalDb.getOrDefault(clave, 0));
            }

            request.setAttribute("citasPorEstadoHoy", citasPorEstadoHoy);
            request.setAttribute("medicosTurnoHoy", medicosTurnoHoy);
            request.setAttribute("topEspecialidades", topEspecialidades);
            request.setAttribute("resumenSemanal", resumenSemanal);
            request.setAttribute("fechaHoy", hoy.toString());
        } catch (SQLException ex) {
            throw new ServletException("No se pudo cargar el dashboard de inicio", ex);
        }

        request.getRequestDispatcher("/WEB-INF/views/home/inicio.jsp").forward(request, response);
    }
}
