package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.EspecialidadDAO;
import com.mycompany.miprimeraweb.dao.EspecialidadDAOImpl;
import com.mycompany.miprimeraweb.model.TarifaConsulta;
import com.mycompany.miprimeraweb.service.TarifaConsultaService;
import com.mycompany.miprimeraweb.util.AppLogger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

/** Controlador del módulo Tarifas. */
@WebServlet("/tarifas")
public class TarifaConsultaController extends HttpServlet {
    private final TarifaConsultaService service = new TarifaConsultaService();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAOImpl();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String accion = texto(request.getParameter("accion"));
            if ("form".equals(accion) || "editar".equals(accion)) {
                int id = parseInt(request.getParameter("id"));
                if (id > 0) request.setAttribute("tarifa", service.obtenerPorId(id));
                request.setAttribute("especialidades", especialidadDAO.listar());
                request.getRequestDispatcher("/WEB-INF/views/tarifa/form.jsp").forward(request, response);
                return;
            }
            request.setAttribute("tarifas", service.listar());
            request.getRequestDispatcher("/WEB-INF/views/tarifa/list.jsp").forward(request, response);
        } catch (SQLException ex) {
            AppLogger.error(getClass(), "Error en tarifas", ex);
            throw new ServletException("No se pudo cargar tarifas.");
        }
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            String accion = texto(request.getParameter("accion"));
            if ("desactivar".equals(accion)) { service.desactivar(parseInt(request.getParameter("id")), usuarioSesion(request)); response.sendRedirect(request.getContextPath()+"/tarifas?msg=deleted"); return; }
            TarifaConsulta t = new TarifaConsulta();
            t.setIdTarifa(parseInt(request.getParameter("idTarifa")));
            t.setIdEspecialidad(parseInt(request.getParameter("idEspecialidad")));
            t.setNombreTarifa(texto(request.getParameter("nombreTarifa")));
            t.setMonto(new BigDecimal(texto(request.getParameter("monto"))));
            t.setMoneda(texto(request.getParameter("moneda")));
            t.setVigenciaDesde(texto(request.getParameter("vigenciaDesde")));
            t.setVigenciaHasta(texto(request.getParameter("vigenciaHasta")));
            t.setEstadoRegistro(texto(request.getParameter("estadoRegistro")));
            service.guardar(t, usuarioSesion(request));
            response.sendRedirect(request.getContextPath()+"/tarifas?msg=saved");
        } catch (Exception ex) {
            AppLogger.warning(getClass(), "Error guardando tarifa", ex);
            response.sendRedirect(request.getContextPath()+"/tarifas?msg=invalid");
        }
    }
    private String usuarioSesion(HttpServletRequest request) { HttpSession s=request.getSession(false); return s==null?"sistema":String.valueOf(s.getAttribute("usuarioLogeado")); }
    private String texto(String v) { return v == null ? "" : v.trim(); }
    private int parseInt(String v) { try { return Integer.parseInt(v); } catch(Exception e) { return 0; } }
}
