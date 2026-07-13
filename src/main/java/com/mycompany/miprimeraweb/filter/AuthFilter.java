package com.mycompany.miprimeraweb.filter;

import com.mycompany.miprimeraweb.dao.UsuarioDAO;
import com.mycompany.miprimeraweb.dao.UsuarioDAOImpl;
import com.mycompany.miprimeraweb.util.CsrfUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;

/**
 * Filtro de autenticación y autorización.
 *
 * V3.2:
 * - Aplica RBAC para ADMIN, RECEPCIONISTA, CAJERO, DOCTOR y DIRECCION.
 * - Genera token CSRF por sesión para formularios POST.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @Override public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String contextPath = httpRequest.getContextPath();
        String requestPath = obtenerRutaSolicitada(httpRequest, contextPath);

        if (esRutaPublica(requestPath)) { chain.doFilter(request, response); return; }

        HttpSession session = httpRequest.getSession(false);
        boolean autenticado = session != null && session.getAttribute("usuarioLogeado") != null;
        if (!autenticado) { httpResponse.sendRedirect(contextPath + "/login"); return; }

        if (!sesionSigueVigente(session)) {
            session.invalidate();
            httpResponse.sendRedirect(contextPath + "/login?error=sesion_invalida");
            return;
        }

        CsrfUtil.obtenerToken(session);

        String rol = String.valueOf(session.getAttribute("rolUsuario"));
        if (!esRutaPermitidaPorRol(requestPath, rol)) {
            String rutaCodificada = URLEncoder.encode(requestPath, StandardCharsets.UTF_8);
            httpResponse.sendRedirect(contextPath + "/acceso-denegado?from=" + rutaCodificada);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override public void destroy() { }

    private boolean sesionSigueVigente(HttpSession session) throws ServletException {
        try {
            int idUsuario = Integer.parseInt(String.valueOf(session.getAttribute("idUsuario")));
            int sessionVersion = Integer.parseInt(String.valueOf(session.getAttribute("sessionVersion")));
            return usuarioDAO.sesionSigueVigente(idUsuario, sessionVersion);
        } catch (SQLException ex) {
            throw new ServletException("No se pudo validar la vigencia de la sesión.", ex);
        } catch (Exception ex) {
            return false;
        }
    }

    private String obtenerRutaSolicitada(HttpServletRequest request, String contextPath) {
        String uri = request.getRequestURI();
        return uri.substring(contextPath.length());
    }

    private boolean esRutaPublica(String requestPath) {
        return requestPath.equals("/")
                || requestPath.startsWith("/login")
                || requestPath.startsWith("/logout")
                || requestPath.startsWith("/css/")
                || requestPath.startsWith("/js/")
                || requestPath.startsWith("/resources/")
                || requestPath.endsWith(".css") || requestPath.endsWith(".js")
                || requestPath.endsWith(".png") || requestPath.endsWith(".jpg")
                || requestPath.endsWith(".jpeg") || requestPath.endsWith(".gif")
                || requestPath.endsWith(".svg") || requestPath.endsWith(".ico");
    }

    private boolean esRutaPermitidaPorRol(String requestPath, String rol) {
        String r = normalizarRol(rol);
        if (requestPath.startsWith("/acceso-denegado")) return true;
        if ("ADMIN".equals(r)) return true;

        if ("RECEPCIONISTA".equals(r)) {
            return requestPath.startsWith("/pacientes")
                    || requestPath.startsWith("/citas")
                    || requestPath.startsWith("/agenda-medico");
        }

        if ("CAJERO".equals(r)) {
            return requestPath.startsWith("/pagos")
                    || requestPath.startsWith("/reportes-financieros");
        }

        if ("DOCTOR".equals(r)) {
            return requestPath.startsWith("/agenda-medico")
                    || requestPath.startsWith("/atencion-medica");
        }

        if ("DIRECCION".equals(r)) {
            return requestPath.startsWith("/direccion")
                    || requestPath.startsWith("/inicio")
                    || requestPath.startsWith("/reportes-financieros")
                    || requestPath.startsWith("/estadisticas");
        }
        return false;
    }

    private String normalizarRol(String rol) { return rol == null ? "" : rol.trim().toUpperCase(); }
}
