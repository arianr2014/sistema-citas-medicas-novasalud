package com.mycompany.miprimeraweb.filter;

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

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization required for now.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String requestPath = httpRequest.getRequestURI().substring(contextPath.length());

        if (esRutaPublica(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        boolean autenticado = session != null && session.getAttribute("usuarioLogeado") != null;

        if (autenticado) {
            String rol = String.valueOf(session.getAttribute("rolUsuario"));
            if (!esRutaPermitidaPorRol(requestPath, rol)) {
                httpResponse.sendRedirect(contextPath + obtenerRutaInicioPorRol(rol));
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        httpResponse.sendRedirect(contextPath + "/login");
    }

    @Override
    public void destroy() {
        // No resources to cleanup.
    }

    private boolean esRutaPublica(String requestPath) {
        return requestPath.equals("/")
                || requestPath.startsWith("/login")
                || requestPath.startsWith("/logout")
                || requestPath.startsWith("/css/")
                || requestPath.startsWith("/js/")
                || requestPath.startsWith("/resources/")
                || requestPath.endsWith(".css")
                || requestPath.endsWith(".js")
                || requestPath.endsWith(".png")
                || requestPath.endsWith(".jpg")
                || requestPath.endsWith(".jpeg")
                || requestPath.endsWith(".gif")
                || requestPath.endsWith(".svg")
                || requestPath.endsWith(".ico");
    }

    private boolean esRutaPermitidaPorRol(String requestPath, String rol) {
        String rolNormalizado = rol == null ? "" : rol.trim().toUpperCase();

        if ("ADMIN".equals(rolNormalizado)) {
            return true;
        }

        if ("RECEPCIONISTA".equals(rolNormalizado)) {
            return requestPath.startsWith("/pacientes")
                    || requestPath.startsWith("/citas")
                    || requestPath.startsWith("/agenda-medico");
        }

        if ("DOCTOR".equals(rolNormalizado)) {
            return requestPath.startsWith("/agenda-medico");
        }

        return false;
    }

    private String obtenerRutaInicioPorRol(String rol) {
        String rolNormalizado = rol == null ? "" : rol.trim().toUpperCase();
        switch (rolNormalizado) {
            case "ADMIN":
                return "/inicio";
            case "RECEPCIONISTA":
                return "/citas";
            case "DOCTOR":
                return "/agenda-medico";
            default:
                return "/login";
        }
    }
}
