package com.golReserve.gol.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golReserve.gol.dto.LoginResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Lista de rutas públicas que no requieren autenticación
    private static final List<String> PUBLIC_ROUTES = Arrays.asList(
            "/api/usuarios/login",
            "/api/usuarios/registrar",
            "/error"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Comprobar si la ruta es pública
        String path = request.getServletPath();
        if (isPublicRoute(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Obtener el header Authorization
        final String requestTokenHeader = request.getHeader("Authorization");

        // Si hay token, intentar procesarlo
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // Extraer el token sin el prefijo "Bearer "
            String jwtToken = requestTokenHeader.substring(7);

            try {
                // Validar el token
                if (jwtTokenUtil.validateToken(jwtToken)) {
                    // Extraer información del token
                    Long userId = jwtTokenUtil.getUserIdFromToken(jwtToken);
                    String email = jwtTokenUtil.getEmailFromToken(jwtToken);
                    String role = jwtTokenUtil.getRoleFromToken(jwtToken);

                    System.out.println("=== JWT VALIDADO CORRECTAMENTE ===");
                    System.out.println("User ID: " + userId);
                    System.out.println("Email: " + email);
                    System.out.println("Role: " + role);
                    System.out.println("Path: " + path);

                    // Crear autenticación con el rol del usuario
                    // Spring Security necesita el prefijo "ROLE_" para hasAnyAuthority
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    System.out.println("Autenticación establecida correctamente");
                    System.out.println("Authorities: " + authentication.getAuthorities());
                    System.out.println("Request URL: " + request.getRequestURL());
                    System.out.println("Request URI: " + request.getRequestURI());
                    System.out.println("Request Method: " + request.getMethod());
                } else {
                    System.out.println("Token NO válido (expirado o inválido)");
                }
            } catch (Exception e) {
                // Si hay error procesando el token, simplemente continuar sin autenticación
                // Spring Security se encargará de rechazar si la ruta requiere autenticación
                System.err.println("Error procesando JWT: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Continuar con la cadena de filtros (dejar que Spring Security decida)
        chain.doFilter(request, response);
    }

    private boolean isPublicRoute(String path) {
        return PUBLIC_ROUTES.stream().anyMatch(path::startsWith);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        ObjectMapper mapper = new ObjectMapper();
        LoginResponse errorResponse = new LoginResponse(null, null, null, null, message);

        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
