package com.mycompany.miprimeraweb.controller;

import com.mycompany.miprimeraweb.dao.TarifaConsultaDAO;
import com.mycompany.miprimeraweb.service.PagoService;
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
import java.time.LocalDate;

/** Controlador de Pagos / Caja. */
@WebServlet("/pagos")
public class PagoController extends HttpServlet {
    private final PagoService service = new PagoService();
    private final TarifaConsultaDAO tarifaDAO = new TarifaConsultaDAO();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String accion = texto(request.getParameter("accion"));
            if ("cobrar".equals(accion)) {
                request.setAttribute("pago", service.obtenerPorId(parseInt(request.getParameter("id"))));
                request.setAttribute("tarifasActivas", tarifaDAO.listarActivas());
                request.getRequestDispatcher("/WEB-INF/views/pago/form.jsp").forward(request, response);
                return;
            }
            if ("comprobante".equals(accion)) {
                request.setAttribute("pago", service.obtenerPorId(parseInt(request.getParameter("id"))));
                request.getRequestDispatcher("/WEB-INF/views/pago/comprobante.jsp").forward(request, response);
                return;
            }
            listar(request, response);
        } catch (SQLException ex) {
            AppLogger.error(getClass(), "Error cargando pagos", ex);
            throw new ServletException("No se pudo cargar el módulo de pagos.");
        }
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            String accion = texto(request.getParameter("accion"));
            if ("anular".equals(accion)) {
                service.anularPago(parseInt(request.getParameter("id")), texto(request.getParameter("motivo")));
                response.sendRedirect(request.getContextPath()+"/pagos?msg=annulled");
                return;
            }
            String resultado = service.registrarPago(
                    parseInt(request.getParameter("idPago")),
                    parseMonto(request.getParameter("monto")),
                    texto(request.getParameter("metodoPago")),
                    texto(request.getParameter("numeroOperacion")),
                    texto(request.getParameter("observacion")),
                    idUsuario(request)
            );
            response.sendRedirect(request.getContextPath()+"/pagos?msg=" + resultado);
        } catch (IllegalArgumentException ex) {
            AppLogger.warning(getClass(), "Pago rechazado por validación funcional", ex);
            response.sendRedirect(request.getContextPath()+"/pagos?msg=invalid");
        } catch (Exception ex) {
            AppLogger.warning(getClass(), "Error registrando pago", ex);
            response.sendRedirect(request.getContextPath()+"/pagos?msg=error");
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        String fechaInicio = texto(request.getParameter("fechaInicio"));
        String fechaFin = texto(request.getParameter("fechaFin"));
        String estadoPago = texto(request.getParameter("estadoPago"));
        String dniPaciente = texto(request.getParameter("dniPaciente"));
        if (fechaInicio.isBlank()) fechaInicio = LocalDate.now().toString();
        if (fechaFin.isBlank()) fechaFin = LocalDate.now().toString();
        request.setAttribute("pagos", service.listar(fechaInicio, fechaFin, estadoPago, dniPaciente));
        request.setAttribute("tarifasActivas", tarifaDAO.listarActivas());
        request.setAttribute("fechaInicio", fechaInicio);
        request.setAttribute("fechaFin", fechaFin);
        request.setAttribute("estadoPago", estadoPago);
        request.setAttribute("dniPaciente", dniPaciente);
        request.getRequestDispatcher("/WEB-INF/views/pago/list.jsp").forward(request, response);
    }


    /** Convierte el monto enviado por caja y genera una validación funcional si es inválido. */
    private BigDecimal parseMonto(String value) {
        try {
            return new BigDecimal(texto(value));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Monto inválido.");
        }
    }

    private int idUsuario(HttpServletRequest request) { HttpSession s=request.getSession(false); if (s==null) return 0; Object v=s.getAttribute("idUsuario"); try { return Integer.parseInt(String.valueOf(v)); } catch(Exception e){ return 0; } }
    private String texto(String v) { return v == null ? "" : v.trim(); }
    private int parseInt(String v) { try { return Integer.parseInt(v); } catch(Exception e) { return 0; } }
}
